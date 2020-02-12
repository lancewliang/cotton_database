package ant.cotton.gov;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.gov.country.GovDay;
import model.entity.gov.country.db.GovDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.Util;

public class GetCNSellHistoryWeb implements DayAnt {

  @Override
  public void doAnt() {
    parseList();
  }

  private void parseList() {
    long maxDate = 0;
    try {
      maxDate = GovDaySQL.getSellLastDay(Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource());

    } catch (Exception e) {
      LogService.trace(e, null);
    }
    if (maxDate <= 0)
      maxDate = 20100101;

    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy");
    int year = Integer.parseInt(sdf1.format(new Date()));
    String url = "http://www.cottonchina.org/news/newsser.php?relnews=%C5%D7%B4%A2%C6%C0%CA%F6&relnews1=%C5%D7%B4%A2";
    List<Tag> returnAss = new ArrayList<Tag>();
    while (None.isEmpty(returnAss)) {
      parseListURL(url, year, "", maxDate, returnAss);
      year--;
      if (year < 2010)
        break;
    }
  }

  private void parseListURL(String url, int year, String pagesize, long maxDate, List<Tag> returnAss) {
    try {

      String content = Util.getContent(url + year + "&" + pagesize, "gb2312");

      Parser parser = Parser.createParser(content, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> attrs = new ArrayList<String[]>();
      attrs.add(new String[] { "class", "a1" });
      attrs.add(new String[] { "target", "_blank" });

      List<Tag> ass = HTMLParseUtil.getTags(nl, "a", attrs);

      if (!None.isEmpty(ass)) {
        List<String> listdate = new ArrayList<String>();
        for (Tag t : ass) {
          String plainText = t.toPlainTextString();
          if (plainText.indexOf("投放日评") != -1) {
            returnAss.add(t);
            String href = t.getAttribute("href");
            Tag pt = (Tag) t.getPreviousSibling();
            String date = pt.toPlainTextString();
            date = "20" + date.replaceAll("/", "");
            if (Long.parseLong(date) > maxDate) {
              listdate.add("http://www.cottonchina.org/news/" + href);

            }
          }
        }

        if (!None.isEmpty(listdate)) {
          for (String n : listdate) {
            parseDayDetailURL(n, year);
          }

        }
        Tag nexttag = HTMLParseUtil.getLink(nl, "下一页");
        if (nexttag != null) {
          String href = nexttag.getAttribute("href");
          long pageIndex = getCurrentPage(content);
          List<Tag> returnAss2 = new ArrayList<Tag>();
          parseListURL(url, year, "offset=" + (pageIndex * 25), maxDate, returnAss2);
        }
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void parseDayDetailURL(String url, int year) {
    try {
      String content = Util.getContent(url, "gb2312");
      Parser parser = Parser.createParser(content, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> attrs = new ArrayList<String[]>();
      attrs.add(new String[] { "class", "main" });

      List<Tag> ass = HTMLParseUtil.getTags(nl, "div", attrs);
      if (!None.isEmpty(ass)) {

        for (Tag t : ass) {
          String s = t.toPlainTextString().trim();
          parseDayDetail(s);
          Date reportDate = getDate(s);

          SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
          long reportDateStr = Long.parseLong(sdf1.format(reportDate));

          AntLogic.saveFile(getSource() + "/SellHistory/" + year + "/", reportDateStr + ".htm", content);
        }

      }

    } catch (Exception e) {
      e.printStackTrace();
      LogService.trace(e, url);
    }
  }

  private void parseDayDetail(String content) {
    try {
      Date now = new Date();
      Date reportDate = getDate(content);

      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
      long reportDateStr = Long.parseLong(sdf1.format(reportDate));

      double weight = getWeight(content);
      double weighttotal = getData3(content);
      GovDay govday = GovDaySQL.getObj(reportDateStr, Country.getCountry("CHN"), Commodity.getCommodity("棉花"), getSource());
      if (govday == null) {

        govday = new GovDay();
        govday.setCommodity(Commodity.getCommodity("棉花"));
        govday.setCountry(Country.getCountry("CHN"));
        govday.setReportDate(reportDateStr);
        govday.setSource(getSource());
      }

      govday.setUpdatedAt(now);
      govday.setUpdatedBy(AntManger.UPDATEBY);
      govday.setWeightUnit(WeightUnit.getWeightUnit("吨"));
      govday.setSellValue(weight);
      govday.setTotalSellValue(weighttotal);
      GovDaySQL.save(govday);

    } catch (Exception e) {
      e.printStackTrace();
      LogService.trace(e, content);
    }
  }

  private Date getDate(String str) throws Exception {
    String regex = "[0-9]{4}年[0-1]{0,1}[0-9]{1}月[0-9]{0,1}[0-9]{1}日";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    if (m.find()) {
      String sttt = m.group();
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");

      return sdf1.parse(sttt);
    } else {
      throw new Exception();
    }

  }

  private double getWeight(String str) throws Exception {
    String regex = "储备棉投放数量为[0-9]+\\.?[0-9]+吨，实际成交[0-9]+\\.?[0-9]+吨";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }
    int x = datastr.indexOf("实际成交");
    if (x != -1) {
      return Double.parseDouble(datastr.substring(x + 4, datastr.length() - 1));
    } else {
      throw new Exception();
    }

  }

  private long getCurrentPage(String str) throws Exception {

    String regex = "第[0-9]+页/共";
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }
    int x = datastr.indexOf("第");
    if (x != -1) {
      return Long.parseLong(datastr.substring(x + 1, datastr.length() - 3));
    } else {
      throw new Exception();
    }

  }

  private double getData3(String str) throws Exception {
    String regex = "累计成交总量[0-9]+\\.?[0-9]+吨";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }
    int x = datastr.indexOf("累计成交总量");
    if (x != -1) {
      return Double.parseDouble(datastr.substring(x + 6, datastr.length() - 1));
    } else {
      throw new Exception();
    }

  }

  @Override
  public String getSource() {

    return "cottonchina";
  }
}
