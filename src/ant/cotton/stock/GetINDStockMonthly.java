package ant.cotton.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.stock.IndustrialStockMonth;
import model.entity.stock.db.IndustrialStockMonthSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetINDStockMonthly implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetINDStockMonthly mhhf = new GetINDStockMonthly();
    try {
      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void doAnt() {
    try {
      String html = Util.getHTML("http://www.txcindia.gov.in/html/domestic%20%20sub.htm", "windows-1252");
      long lastDay = IndustrialStockMonthSQL.getLastDay(Commodity.getCommodity("ÃÞ»¨"), Country.getCountry("IND"), getSource());
      if (lastDay <= 0)
        lastDay = 200810;
      Parser parser = Parser.createParser(html, "windows-1252");
      NodeList nl = parser.parse(null);
      Tag link1tag = HTMLParseUtil.getLinkBySubString(nl, "Stock by Non-SSI Mills");
      Tag link2tag = HTMLParseUtil.getLinkBySubString(nl, "Stock by SSI Mills");

      String link1 = link1tag.getAttribute("href");
      String link2 = link2tag.getAttribute("href");
      String link1html = Util.getHTML("http://www.txcindia.gov.in/html/" + link1, "windows-1252");
      String link2html = Util.getHTML("http://www.txcindia.gov.in/html/" + link2, "windows-1252");

      File f1 = AntLogic.getFile(getSource() + "/Stock", "Stock by Non-SSI Mills.html");
      if (f1.exists()) {
        InputStream in = new FileInputStream(f1);
        String content = FileStreamUtil.getFileContent(in);
        in.close();

        if (!content.equals(link1html)) {
          AntLogic.saveFile(getSource() + "/Stock", "Stock by Non-SSI Mills.html", link1html);
        }
      } else {
        AntLogic.saveFile(getSource() + "/Stock", "Stock by Non-SSI Mills.html", link1html);
      }

      File f2 = AntLogic.getFile(getSource() + "/Stock", "Stock by SSI Mills.html");
      if (f2.exists()) {
        InputStream in = new FileInputStream(f2);
        String content = FileStreamUtil.getFileContent(in);
        in.close();
        if (!content.equals(link2html)) {
          AntLogic.saveFile(getSource() + "/Stock", "Stock by SSI Mills.html", link2html);
        }

      } else {
        AntLogic.saveFile(getSource() + "/Stock", "Stock by SSI Mills.html", link2html);
      }

      Map<Long, Double> nossimills = new HashMap<Long, Double>();
      Map<Long, Double> ssimills = new HashMap<Long, Double>();
      Set<Long> hashSetMonth = new HashSet<Long>();
      List<Long> monthsints = new ArrayList<Long>();

      parseNOSSIMILLS(link1html, nossimills, hashSetMonth);
      parseSSIMILLS(link2html, ssimills, hashSetMonth);
      monthsints.addAll(hashSetMonth);
      Collections.sort(monthsints);
      Date now = new Date();
      for (long reportDate : monthsints) {
        if (reportDate < lastDay) {
          continue;
        }

        Double v1 = nossimills.get(reportDate);
        Double v2 = ssimills.get(reportDate);

        System.out.println(reportDate + "|" + v1 + "|" + v2);
        if (v1 != null && v2 != null) {
          IndustrialStockMonth monthObj = IndustrialStockMonthSQL.getObj(reportDate, Country.getCountry("IND"), Commodity.getCommodity("ÃÞ»¨"), getSource());
          if (monthObj == null) {
            monthObj = new IndustrialStockMonth();
            monthObj.setCommodity(Commodity.getCommodity("ÃÞ»¨"));
            monthObj.setCountry(Country.getCountry("IND"));
            monthObj.setReportDate(reportDate);
            monthObj.setSource(getSource());
          }
          monthObj.setUpdatedAt(now);
          monthObj.setUpdatedBy(AntManger.UPDATEBY);
          monthObj.setValue(v1.doubleValue() + v2.doubleValue());
          monthObj.setWeightUnit(WeightUnit.getWeightUnit("Ç§¶Ö"));
          IndustrialStockMonthSQL.save(monthObj);
        }
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  private void parseNOSSIMILLS(String html, Map<Long, Double> maps, Set<Long> months) throws ParserException {
    Parser parser = Parser.createParser(html, "windows-1252");
    NodeList nl = parser.parse(null);
    NodeList table = HTMLParseUtil.getNodeList(nl, "table", null);
    List<Tag> trs = HTMLParseUtil.getTags(table, "tr");
    long leftYears[] = null;

    long lastleftReportDate = 0;

    int step = -1;
    for (Tag tr : trs) {
      NodeList tdNs = tr.getChildren();
      List<Tag> tds = HTMLParseUtil.getTags(tdNs, "td");
      if (tds.size() < 4)
        continue;
      Tag td1 = tds.get(0);
      Tag td2 = tds.get(1);
      Tag td3 = tds.get(2);
      Tag td4 = tds.get(3);
      String td1STR = getTD(td1);
      boolean isYear = false;
      String td2STR = getTD(td2);
      if (td1STR.indexOf("(P)") > 0 && "".equals(td2STR)) {
        String yearSTR = StringUtil.replaceString(td1STR, "(P)", "").trim();
        String[] yeas = yearSTR.split("-");

        leftYears = new long[] { Long.parseLong(yeas[0]), Long.parseLong(yeas[1].length() == 4 ? yeas[1] : ("20" + yeas[1])) };
        isYear = true;
        step = 0;
        lastleftReportDate = 0;

      } else if ("".equals(td2STR) && "".equals(td1STR)) {
        continue;
      }
      String td3STR = td3.toPlainTextString().trim();
      String td4STR = td4.toPlainTextString().trim();

      if (isYear) {
        continue;
      }
      if (step < 0) {
        continue;
      }
      try {
        if ("--".equals(td1STR)) {

        } else {
          int mm = DateUtil.getMonthByEN(td1STR);
          long reportDate = getMonth(mm, leftYears[0]);
          if (reportDate < lastleftReportDate) {
            reportDate = getMonth(mm, leftYears[1]);
          }
          lastleftReportDate = reportDate;
          months.add(reportDate);
          maps.put(reportDate, Double.parseDouble(td2STR.split(" ")[0]));
          // System.out.println("L|" + reportDate + "|" + mm + "|" + td1STR +
          // "|" + td2STR);
        }

      } catch (Exception e) {
        LogService.trace(e, "");
      }
    }

  }

  private void parseSSIMILLS(String html, Map<Long, Double> maps, Set<Long> months) throws ParserException {
    Parser parser = Parser.createParser(html, "windows-1252");
    NodeList nl = parser.parse(null);
    NodeList table = HTMLParseUtil.getNodeList(nl, "table", null);
    List<Tag> trs = HTMLParseUtil.getTags(table, "tr");
    long leftYears[] = null;
    // long rightYears[] = null;
    long lastleftReportDate = 0;
    // long lastrightReportDate = 0;
    int step = -1;
    for (Tag tr : trs) {
      NodeList tdNs = tr.getChildren();
      List<Tag> tds = HTMLParseUtil.getTags(tdNs, "td");
      if (tds.size() < 4)
        continue;
      Tag td1 = tds.get(0);
      Tag td2 = tds.get(1);
      Tag td3 = tds.get(2);
      Tag td4 = tds.get(3);
      String td1STR = getTD(td1);
      boolean isYear = false;
      String td2STR = getTD(td2);
      if ((td1STR.indexOf("(P)") > 0) && ("-".equals(td2STR) || "".equals(td2STR))) {
        String[] yeas = StringUtil.replaceString(td1STR, "(P)", "").trim().split("-");
        leftYears = new long[] { Long.parseLong(yeas[0]), Long.parseLong("20" + yeas[1]), };
        isYear = true;
        step = 0;
        lastleftReportDate = 0;
      } else if ("2010-2011".equals(td1STR) && "-".equals(td2STR)) {
        String[] yeas = StringUtil.replaceString(td1STR, "(P)", "").trim().split("-");
        leftYears = new long[] { Long.parseLong(yeas[0]), Long.parseLong(yeas[1]), };
        isYear = true;
        step = 0;
        lastleftReportDate = 0;
      } else if ("".equals(td2STR) && "".equals(td1STR)) {
        continue;
      }
      String td3STR = getTD(td3);
      String td4STR = getTD(td4);

      if (isYear) {
        continue;
      }
      if (step < 0) {
        continue;
      }
      try {
        if ("--".equals(td1STR)) {

        } else {
          int mm = DateUtil.getMonthByEN(td1STR);
          long reportDate = getMonth(mm, leftYears[0]);
          if (reportDate < lastleftReportDate) {
            reportDate = getMonth(mm, leftYears[1]);
          }
          lastleftReportDate = reportDate;
          months.add(reportDate);
          maps.put(reportDate, Double.parseDouble(td2STR.split(" ")[0]));
          // System.out.println("L|" + reportDate + "|" + mm + "|" + td1STR +
          // "|" + td2STR);
        }

      } catch (Exception e) {
        LogService.trace(e, "");
      }
    }
  }

  private String getTD(Tag td1) {
    String str = td1.toPlainTextString().trim();
    if (None.isBlank(str))
      return "";
    str = StringUtil.replaceString(str, "&nbsp;", "");

    str = str.replaceAll("mstheme", "").trim();
    return str;
  }

  private long getMonth(int mm, long y) throws ParseException {
    String str = mm + "/" + y;
    SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
    Date date = sdf.parse(str);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
    return Long.parseLong(sdf2.format(date));
  }

  @Override
  public String getSource() {

    return "txcindia";
  }
}
