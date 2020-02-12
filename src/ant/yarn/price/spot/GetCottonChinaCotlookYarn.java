package ant.yarn.price.spot;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.UnitType;
import model.constant.WeightUnit;
import model.entity.price.country.CountryPriceDay;
import model.entity.price.country.db.CountryPriceDaySQL;

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

public class GetCottonChinaCotlookYarn implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCottonChinaCotlookYarn parse = new GetCottonChinaCotlookYarn();
    try {
      String SessionId = CottonchinaUtil.getSessionId();
      int lastDay = CountryPriceDaySQL.getLastDay(Commodity.getCommodity("ɴ��"), parse.getSource());
      if (lastDay <= 0)
        lastDay = 20080101;
      for (int i = 1; i < 50; i++) {
        parse.doAntIndex(lastDay, i, SessionId);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  @Override
  public void doAnt() {

    try {
      String SessionId = CottonchinaUtil.getSessionId();
      int lastDay = CountryPriceDaySQL.getLastDay(Commodity.getCommodity("ɴ��"), getSource());
      if (lastDay <= 0)
        lastDay = 20080101;
      for (int i = 1; i < 2; i++) {
        doAntIndex(lastDay, i, SessionId);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  public void doAntIndex(int lastDay, int pageIndex, String SessionId) {
    try {
      int offset = pageIndex == 0 ? 0 : (pageIndex - 1) * 19;
      String url = "http://www.cottonchina.org/newprice/cotcyindex.php?px=" + pageIndex + "&offset=" + offset;
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
      if (table.toHtml().indexOf("�޻�չ����ɴָ��") != 1) {
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

            String valueSTR1 = tds.get(1).toPlainTextString();
            double price1 = 0;

            try {
              valueSTR1 = valueSTR1.replaceAll("N", "");
              price1 = Double.parseDouble(valueSTR1);
            } catch (Exception e) {
              LogService.trace(e, url + "valueSTR1" + valueSTR1);
            }

            saveObj(reportDate, price1);

             }
          } catch (Exception e) {
            LogService.trace(e, null);

          }

        }
      }
    }
  }

  private void saveObj(long reportDate, double price) {
    try {
      String standard = "C32S";
      if (price <= 0) {
        return;
      }
      CountryPriceDay obj = CountryPriceDaySQL.getObj(reportDate, Country.getCountry("Cotlook"), "Cotlook", standard, Commodity.getCommodity("ɴ��"), getSource());
      if (obj == null) {
        obj = new CountryPriceDay();
        obj.setReportDate(reportDate);
        obj.setCommodity(Commodity.getCommodity("ɴ��"));
        obj.setCountry(Country.getCountry("Cotlook"));
        UnitType unittype = UnitType.getUnitType("������λ");
        obj.setUnitType(unittype);
        obj.setStandard(standard);
        obj.setState("Cotlook");
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