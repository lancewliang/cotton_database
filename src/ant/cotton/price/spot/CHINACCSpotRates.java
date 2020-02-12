package ant.cotton.price.spot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import org.htmlparser.util.ParserException;

import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.SetENVUtil;
import engine.util.Util;

public class CHINACCSpotRates implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    CHINACCSpotRates parse = new CHINACCSpotRates();
    parse.doAnt();

  }

  public void doAnt() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

      int currentMonth = Integer.parseInt(sdf2.format(cal.getTime()));

      long lastMonth = CountryPriceDaySQL.getLastMonth(Commodity.getCommodity("棉花"), getSource());
      boolean dotask = false;
      if (currentMonth >= lastMonth) {
        Calendar lastMonthCalendar = getDate(lastMonth);
        Calendar currentMonthCalendar = getDate(currentMonth);

        int reportDateLast = Integer.parseInt(sdf2.format(lastMonthCalendar.getTime()));
        int reportDateCurrent = Integer.parseInt(sdf2.format(currentMonthCalendar.getTime()));
        while (reportDateCurrent >= reportDateLast) {
          int y = lastMonthCalendar.get(Calendar.YEAR);
          int m = lastMonthCalendar.get(Calendar.MONTH) + 1;
          doAURL(y, m);
          lastMonthCalendar.add(Calendar.MONTH, 1);
          reportDateLast = Integer.parseInt(sdf2.format(lastMonthCalendar.getTime()));
          reportDateCurrent = Integer.parseInt(sdf2.format(currentMonthCalendar.getTime()));
          dotask = true;
        }

      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  private void doAURL(int year, int month) {

    try {
      String url = getURL(year, month);
      File f = AntLogic.getFile(getSource() + "/CCindex", "ccindex-" + year + "-" + month + ".htm");

      String content = Util.getHTML(url, "gb2312");
      if (f.exists()) {
        InputStream in = new FileInputStream(f);
        String content2 = FileStreamUtil.getFileContent(in);
        in.close();

        if (!content2.equals(content)) {
          AntLogic.saveFile(getSource() + "/CCindex", "ccindex-" + year + "-" + month + ".htm", content);
        }
      } else {
        AntLogic.saveFile(getSource() + "/CCindex", "ccindex-" + year + "-" + month + ".htm", content);
      }
      praseContent(content, f);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void praseContent(String content, File f) throws ParserException {
    Parser parser = Parser.createParser(content, "GB2312");
    NodeList nl = parser.parse(null);
    NodeFilter tablefi[] = new NodeFilter[2];
    tablefi[0] = new TagNameFilter("table");
    tablefi[1] = new HasAttributeFilter("class", "biaoge");

    AndFilter tableandfilter = new AndFilter();
    tableandfilter.setPredicates(tablefi);
    NodeList tl = nl.extractAllNodesThatMatch(tableandfilter, true);
    if (tl.size() == 0)
      return;
    Node[] nodes = tl.toNodeArray();
    for (Node n : nodes) {
      Tag t = (Tag) n;
      doAURLTable(t);

    }
  }

  private void doAURLTable(Tag table) {
    Node[] trs = getChilds(table, "tr");

    for (int i = 2; i < trs.length - 1; i++) {
      Node[] tds = getChilds((Tag) trs[i], "td");
      String date = getdate(tds[0].toPlainTextString());
      double price1129B = getvalue(tds[1].toPlainTextString());
      double price2129B = getvalue(tds[3].toPlainTextString());
      double price3128B = getvalue(tds[5].toPlainTextString());
      double price4128B = getvalue(tds[7].toPlainTextString());
      double price1228B = getvalue(tds[9].toPlainTextString());
      double price2227B = getvalue(tds[11].toPlainTextString());
      saveObj(Long.parseLong(date), "1129B", price1129B);
      saveObj(Long.parseLong(date), "2129B", price2129B);
      saveObj(Long.parseLong(date), "3128B", price3128B);
      saveObj(Long.parseLong(date), "4128B", price4128B);
      saveObj(Long.parseLong(date), "1228B", price1228B);
      saveObj(Long.parseLong(date), "2227B", price2227B);
    }

  }

  private void saveObj(long reportDate, String standard, double price) {
    try {
      if (price <= 0) {
        return;
      }
      CountryPriceDay obj = CountryPriceDaySQL.getObj(reportDate, Country.getCountry("CHN"), "COUNTRY", standard, Commodity.getCommodity("棉花"), getSource());
      if (obj == null) {
        obj = new CountryPriceDay();
        obj.setReportDate(reportDate);
        obj.setCommodity(Commodity.getCommodity("棉花"));
        obj.setCountry(Country.getCountry("CHN"));
        UnitType unittype = UnitType.getUnitType("重量单位");
        obj.setUnitType(unittype);
        obj.setStandard(standard);
        obj.setState("COUNTRY");
      }

      Date now = new Date();
      obj.setUnit(WeightUnit.getWeightUnit("吨"));
      obj.setPriceUnit(PriceUnit.getPriceUnit("元"));
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

  private String getdate(String date) {

    String[] ds = date.split("-");

    return ds[0] + (ds[1].length() == 1 ? ("0" + ds[1]) : ds[1]) + (ds[2].length() == 1 ? ("0" + ds[2]) : ds[2]);
  }

  private double getvalue(String v) {
    try {
      return Double.parseDouble(v);
    } catch (Exception e) {
    }
    return 0;
  }

  private Calendar getDate(long month) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(month + "01");
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  private Node[] getChilds(Tag table, String str) {
    NodeList nl = table.getChildren();

    NodeFilter tablefi[] = new NodeFilter[1];
    tablefi[0] = new TagNameFilter(str);
    AndFilter filter = new AndFilter();
    filter.setPredicates(tablefi);
    NodeList tl = nl.extractAllNodesThatMatch(filter, true);
    Node[] nodes = tl.toNodeArray();
    return nodes;
  }

  private String getURL(int year, int month) {
    String url = "http://www.china-cotton.org/data/ccindexnew.php?action=ccindexnew&acnew=day&year=" + year + "&month=" + month;
    return url;
  }

  @Override
  public String getSource() {
    return "china-cotton";
  }
}
