package ant.cotton.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Bourse;
import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.stock.BoursesStockDay;
import model.entity.stock.BoursesStockDayDetail;
import model.entity.stock.db.BoursesStockDayDetailSQL;
import model.entity.stock.db.BoursesStockDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCZCZStockDayly implements DayAnt {
  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    GetCZCZStockDayly exp = new GetCZCZStockDayly();
    exp.doAnt();
  }

  @Override
  public void doAnt() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
      cal.add(Calendar.DAY_OF_MONTH, 1);
      int currentDay = Integer.parseInt(sdf2.format(cal.getTime()));

      int lastDay = BoursesStockDaySQL.getLastDay(Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource());
      if (lastDay <= 0)
        lastDay = 20140101;
      boolean dotask = false;
      if (currentDay >= lastDay) {
        Calendar lastDayCalendar = getDate(lastDay);
        Calendar currentDayCalendar = getDate(currentDay);
        currentDayCalendar.add(Calendar.DAY_OF_MONTH, 1);

        int reportDate111 = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
        int reportDate222 = Integer.parseInt(sdf2.format(currentDayCalendar.getTime()));
        getDayPage(lastDayCalendar.get(Calendar.YEAR), Integer.parseInt(sdf2.format(lastDayCalendar.getTime())), true);
        while (!lastDayCalendar.after(currentDayCalendar)) {
          int y = lastDayCalendar.get(Calendar.YEAR);
          int m = lastDayCalendar.get(Calendar.MONTH) + 1;
          int d = lastDayCalendar.get(Calendar.DAY_OF_MONTH);
          int reportDate = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
          getDayPage(y, reportDate, false);
          lastDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
          dotask = true;
        }

      }

      if (!dotask) {
        LogService.msg(" not dotask :");
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void getDayPage(long year, long reportDate, boolean get) throws Exception {
    String url = "http://www.czce.com.cn/portal/exchange/" + year + "/datawhsheet/" + reportDate + ".htm";
    LogService.msg(url);

    //
    File f = AntLogic.getFile(getSource() + "/cotton/datawh/" + year, "datawhsheet-" + reportDate + ".htm");
    String html = null;
    if (!f.exists() || get) {
      html = Util.getHTML(url, "GBK");
      if (None.isNonBlank(html) && html.indexOf("仓单日报表") != -1) {
        if (html.indexOf("仓单日报表") != -1) {

          if (f.exists()) {
            InputStream in = new FileInputStream(f);
            String html2 = FileStreamUtil.getFileContent(in);
            in.close();
            if (!html2.equals(html))
              AntLogic.saveFile(getSource() + "/cotton/datawh/" + year, "datawhsheet-" + reportDate + ".htm", html);
          } else {
            AntLogic.saveFile(getSource() + "/cotton/datawh/" + year, "datawhsheet-" + reportDate + ".htm", html);
          }
        }
      }
      if (f.exists()) {
        praseContent(html, reportDate, f);
      }
    } else {
      InputStream in = new FileInputStream(f);
      String html2 = FileStreamUtil.getFileContent(in);
      in.close();

      praseContent(html2, reportDate, f);
    }
  }

  private Calendar getDate(long day) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse("" + day);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  private void praseContent(String content, long reportDate, File f) throws Exception {
    Date now = new Date();
    Parser parser = Parser.createParser(content, "GBK");
    NodeList nl = parser.parse(null);

    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "class", "table" });
    tableattrs.add(new String[] { "id", "senfe" });
    List<Tag> tables = HTMLParseUtil.getTags(nl, "table", tableattrs);
    boolean donothing = false;
    for (Tag table : tables) {
      List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
      int r = 0;
      String cwhId = null;
      String cwhNam = null;

      int len = trs.size();
      for (Tag tr : trs) {
        if (r == 0) {
          String text = tr.toPlainTextString();
          if (text.indexOf("品种：一号棉CF") != -1) {
            r++;
            continue;
          } else {
            break;
          }

        }
        if (r == 1) {
          r++;
          continue;
        }
        NodeList tdNs = tr.getChildren();
        List<Tag> tds = HTMLParseUtil.getTags(tdNs, "td");
        String text = tr.toPlainTextString();

        if (r == (len - 4)) {
          if (text.indexOf("总计") != -1) {
            double weight1 = 0;
            double weight2 = 0;

            String td3STR = tds.get(5).toPlainTextString().trim();
            String td4STR = tds.get(7).toPlainTextString().trim();
            try {
              weight1 = Double.parseDouble(td3STR);

              weight2 = Double.parseDouble(td4STR);

            } catch (Exception e) {
            }
            saveStockDay(reportDate, weight1, weight2);
            break;
          }
        } else if (text.indexOf("小计") != -1) {
          r++;
          continue;
        } else {
          double weight1 = 0;
          double weight2 = 0;
          String td0STR = tds.get(0).toPlainTextString().trim();
          String td1STR = tds.get(1).toPlainTextString().trim();
          String td2STR = tds.get(2).toPlainTextString().trim();
          String td3STR = tds.get(3).toPlainTextString().trim();
          String td4STR = tds.get(4).toPlainTextString().trim();
          String td5STR = tds.get(5).toPlainTextString().trim();
          String td6STR = tds.get(6).toPlainTextString().trim();
          String td7STR = tds.get(7).toPlainTextString().trim();
          td0STR = StringUtil.replaceString(td0STR, "&nbsp;", "").trim();
          td1STR = StringUtil.replaceString(td1STR, "&nbsp;", "").trim();

          if (None.isBlank(td0STR) && None.isBlank(td1STR)) {
            td0STR = cwhId;
            td1STR = cwhNam;
          } else {
            cwhId = td0STR;
            cwhNam = td1STR;
          }
          try {
            weight1 = Double.parseDouble(td5STR);

            weight2 = Double.parseDouble(td7STR);

          } catch (Exception e) {
          }
          saveStockDayDetail(reportDate, td0STR, td1STR, td2STR, td3STR, td4STR, weight1, weight2);
        }

        donothing = true;
        r++;
      }
    }
    if (!donothing) {
      throw new Exception("donothing");
    }
  }

  private void saveStockDayDetail(long reportDate, String wHId, String wHName, String annual, String grade, String producingArea, double weight1, double weight2) throws SQLException {

    Date now = new Date();
    BoursesStockDayDetail day = BoursesStockDayDetailSQL.getObj(reportDate, Country.getCountry("CHN"), Bourse.getBourse("郑商所"), wHId, wHName, annual, grade, producingArea, Commodity.getCommodity("棉花"), getSource());
    if (day == null) {
      day = new BoursesStockDayDetail();
      day.setBourse(Bourse.getBourse("郑商所"));
      day.setCommodity(Commodity.getCommodity("棉花"));
      day.setCountry(Country.getCountry("CHN"));
      day.setReportDate(reportDate);
      day.setSource(getSource());
      day.setWHId(wHId);
      day.setWHName(wHName);
      day.setAnnual(annual);
      day.setGrade(grade);
      day.setProducingArea(producingArea);
    }
    day.setUpdatedAt(now);
    day.setUpdatedBy(AntManger.UPDATEBY);
    day.setValue(weight1);
    day.setPredictedValue(weight2);
    day.setWeightUnit(WeightUnit.getWeightUnit("仓单张"));
    BoursesStockDayDetailSQL.save(day);
  }

  private void saveStockDay(long reportDate, double weight1, double weight2) throws SQLException {
    Date now = new Date();
    BoursesStockDay day = BoursesStockDaySQL.getObj(reportDate, Country.getCountry("CHN"), Bourse.getBourse("郑商所"), Commodity.getCommodity("棉花"), getSource());
    if (day == null) {
      day = new BoursesStockDay();
      day.setBourse(Bourse.getBourse("郑商所"));
      day.setCommodity(Commodity.getCommodity("棉花"));
      day.setCountry(Country.getCountry("CHN"));
      day.setReportDate(reportDate);
      day.setSource(getSource());
    }
    day.setUpdatedAt(now);
    day.setUpdatedBy(AntManger.UPDATEBY);
    day.setValue(weight1);
    day.setPredictedValue(weight2);
    day.setWeightUnit(WeightUnit.getWeightUnit("仓单张"));
    BoursesStockDaySQL.save(day);
  }

  @Override
  public String getSource() {

    return "CZCE";
  }

}
