package ant.cotton.price.spot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.UnitType;
import model.constant.WeightUnit;
import model.entity.price.country.CountryPriceDay;
import model.entity.price.country.db.CountryPriceDaySQL;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

/**
 * 
 * The elaboration of the CEPEA/ESALQ Index for cotton is carried out by daily
 * prices collection through cotton farmers, traders, and textile mills, in the
 * major producing areas in Brazil, to be delivered to Sao Paulo city.
 * 
 * The Index, previously nominated ESALQ/BM&F, started to be calculated in 1996,
 * respecting an agreement with Sao Paulo Mercantile Exchange (BM&FBovespa). The
 * main goal is to provide financial liquidation to the future contracts of this
 * commodity.
 * 
 * The CEPEA/ESALQ Index for cotton has become a reference in the Brazilian
 * cotton market. An expressive number of trades have been settled based on the
 * Index. Cepea also publishes regional prices differentials measured by the
 * Index.
 */
public class BrazilSoptRate implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    BrazilSoptRate parse = new BrazilSoptRate();
    parse.doAnt();

  }

  public void doAnt() {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    String dateSTR = sdf.format(now);
    String url = "http://cepea.esalq.usp.br/english/cotton/";
    File f = AntLogic.getFile(getSource() + "/cotton", "ESALQ-" + dateSTR + ".htm");
    try {
      if (!f.exists()) {
        Util.getFile(url, f);
      }
      if (f.exists()) {
        InputStream in = new FileInputStream(f);
        String content2 = FileStreamUtil.getFileContent(in);
        in.close();
        praseContent(content2, f);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void praseContent(String content, File f) throws Exception {
    Parser parser = Parser.createParser(content, "utf-8");
    NodeList nl = parser.parse(null);
    NodeFilter tablefi[] = new NodeFilter[2];
    tablefi[0] = new TagNameFilter("table");
    tablefi[1] = new HasAttributeFilter("cellspacing", "1");
    tablefi[1] = new HasAttributeFilter("border", "0");
    boolean tableright = false;
    AndFilter tableandfilter = new AndFilter();
    tableandfilter.setPredicates(tablefi);
    NodeList tl = nl.extractAllNodesThatMatch(tableandfilter, true);
    if (tl.size() == 0)
      return;
    Node[] nodes = tl.toNodeArray();
    for (Node n : nodes) {
      Tag table = (Tag) n;
      if (!"85%".equals(table.getAttribute("width"))) {
        continue;
      }
      List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
      if (trs.size() <= 4)
        continue;
      int r = 0;
      for (Tag tr : trs) {
        r++;
        if (r <= 3) {
          continue;
        }
        if (r >= 9) {
          break;
        }
        tableright = true;
        try {
          List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");
          long reportdate = getdate(tds.get(0).toPlainTextString());
          double price = Double.parseDouble(tds.get(1).toPlainTextString());

          saveObj(reportdate, "41-4", price);
        } catch (Exception e) {
          LogService.trace(e, null);
        }
      }

    }
    if (!tableright) {
      throw new Exception("tableright is false");
    }
  }

  private void saveObj(long reportDate, String standard, double price) {
    try {
      if (price <= 0) {
        return;
      }
      CountryPriceDay obj = CountryPriceDaySQL.getObj(reportDate, Country.getCountry("BR"), "COUNTRY", standard, Commodity.getCommodity("棉花"), getSource());
      if (obj == null) {
        obj = new CountryPriceDay();
        obj.setReportDate(reportDate);
        obj.setCommodity(Commodity.getCommodity("棉花"));
        obj.setCountry(Country.getCountry("BR"));
        UnitType unittype = UnitType.getUnitType("重量单位");
        obj.setUnitType(unittype);
        obj.setStandard(standard);
        obj.setState("COUNTRY");
      }

      Date now = new Date();
      obj.setUnit(WeightUnit.getWeightUnit("pound"));
      obj.setPriceUnit(PriceUnit.getPriceUnit("CENTS"));
      obj.setSource(getSource());
      obj.setUpdatedAt(now);
      obj.setUpdatedBy(AntManger.UPDATEBY);
      obj.setValue(price);

      CountryPriceDaySQL.save(obj);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private long getdate(String date) throws ParseException {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yy");
    String s = StringUtil.replaceLastString(date.trim(), "/1", "/20");
    String dateSTR = sdf.format(sdf1.parse(s));

    return Long.parseLong(dateSTR);
  }

  @Override
  public String getSource() {
    return "CEPEA";
  }
}
