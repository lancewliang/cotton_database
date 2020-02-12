package ant.cotton.price.port;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PortPriceType;
import model.constant.PriceUnit;
import model.constant.WeightUnit;
import model.entity.price.country.PortPriceDay;
import model.entity.price.country.db.PortPriceDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.log.LogService;
import ant.cotton.custom.CottonchinaUtil;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetCNCottonCotlook implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCNCottonCotlook parse = new GetCNCottonCotlook();
    parse.doAnt();

  }

  @Override
  public void doAnt() {

    try {
      String SessionId = CottonchinaUtil.getSessionId();
      int lastDay = PortPriceDaySQL.getLastDay(Commodity.getCommodity("棉花"),getSource());
      if (lastDay <= 0)
        lastDay = 20100101;
      for (int i = 1; i < 2; i++) {
        doAntIndex(lastDay, i, SessionId);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  public void doAntIndex(int lastDay, int pageIndex, String SessionId) {
    try {
      int offset = pageIndex == 0 ? 0 : (pageIndex - 1) * 14;
      String url = "http://www.cottonchina.org/newprice/cotlook_more.php?px=" + pageIndex + "&offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      praseContent(lastDay, listpage, url);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void praseContent(int lastDay, String content, String url) throws ParserException {
    Date now = new Date();
    Parser parser = Parser.createParser(content, "GB2312");
    NodeList nl = parser.parse(null);
    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "border", "0" });
    tableattrs.add(new String[] { "cellpadding", "0" });
    tableattrs.add(new String[] { "cellspacing", "1" });

    List<Tag> tables = HTMLParseUtil.getTags(nl, "table", tableattrs);
    for (Tag table : tables) {
      if (table.toHtml().indexOf("Cotlook指数") != 1) {
        List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
        int i = 1;
        for (Tag tr : trs) {
          i++;
          if (i <= 3)
            continue;
          if ((i - 1) == trs.size())
            continue;

          List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");
          try {
            long reportDate = getDateSTR(tds.get(0).toPlainTextString());
            if (reportDate >= lastDay) {

              String valueSTR = tds.get(1).toPlainTextString();

              double price = 0;
              try {
                valueSTR = valueSTR.replaceAll("N", "");
                price = Double.parseDouble(valueSTR);
              } catch (Exception e) {
                LogService.trace(e, url + "valueSTR" + valueSTR);
              }

              saveObj(reportDate, price);
            }
          } catch (Exception e) {
            LogService.trace(e, null);

          }

        }
      }
    }
  }

  private void saveObj(long reportDate, double price) throws SQLException {
    String standard = "M";
    String term = "";
    Country fromcountry = Country.getCountry("Cotlook");
    PortPriceType portPriceType = PortPriceType.getPortPriceType("市场报价");
    Date now = new Date();
    PortPriceDay obj = PortPriceDaySQL.getObj(reportDate, Country.getCountry("Cotlook"), fromcountry, standard, term, portPriceType, Commodity.getCommodity("棉花"), getSource());
    if (obj == null) {
      obj = new PortPriceDay();
      obj.setReportDate(reportDate);
      obj.setCommodity(Commodity.getCommodity("棉花"));
      obj.setCountry(Country.getCountry("Cotlook"));
      obj.setFromCountry(fromcountry);

      obj.setPortPriceType(portPriceType);
      obj.setStandard(standard);
      obj.setTerm(term);
    }
    // 到港价

    if (price <= 0) {
      return;
    }

    obj.setWeightUnit1(WeightUnit.getWeightUnit("pound"));

    obj.setSource(getSource());
    obj.setPriceUnit1(PriceUnit.getPriceUnit("CENTS"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    obj.setValue1(price);
    PortPriceDaySQL.save(obj);

  }

  private long getDateSTR(String daystr) throws ParseException {

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date date = sdf2.parse(daystr);
    return Long.parseLong(sdf.format(date));
  }

  @Override
  public String getSource() {
    return "Cotlook";
  }

}
