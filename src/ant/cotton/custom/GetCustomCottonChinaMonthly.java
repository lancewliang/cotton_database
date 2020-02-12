package ant.cotton.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.custom.country.ImportExportMonth;
import model.entity.custom.country.db.ImportExportMonthSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetCustomCottonChinaMonthly implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCustomCottonChinaMonthly mhhf = new GetCustomCottonChinaMonthly();
    try {
      mhhf.doAnt();

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void doAnt() {
    // TODO Auto-generated method stub
    try {
      String SessionId = CottonchinaUtil.getSessionId();
      doExportAnt(SessionId);
      doImportAnt(SessionId);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void doExportAnt(String SessionId) throws SQLException {
    long lastDay = ImportExportMonthSQL.getTOCountryLastDay(Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource());
    doExportAnt(1, lastDay, SessionId);

  }

  public void doImportAnt(String SessionId) throws SQLException {
    long lastDay = ImportExportMonthSQL.getFROMCountryLastDay(Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource());
    doImportAnt(1, lastDay, SessionId);

  }

  public void doExportAnt(int page, long lastDay, String SessionId) {
    try {
      //

      int offset = (page - 1) * 25;
      String url = "http://www.cottonchina.org/stat/mianhua/mianhua_out.php?offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "target", "_blank" });
      tableattrs.add(new String[] { "class", "a3" });
      List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
      for (Tag tg : tags) {
        String linktitle = tg.toPlainTextString();
        if (linktitle.indexOf("棉花出口月报") != -1) {
          long reportDate = getMonthDay(linktitle);

          if (isUpdate(reportDate, lastDay)) {
            String uuu = "http://www.cottonchina.org/stat/mianhua/" + tg.getAttribute("href");
            antExportMonth(reportDate, uuu, linktitle, SessionId);
          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void doImportAnt(int page, long lastDay, String SessionId) {
    try {
      //

      int offset = (page - 1) * 25;
      String url = "http://www.cottonchina.org/stat/mianhua/mianhua_in.php?offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "target", "_blank" });
      tableattrs.add(new String[] { "class", "a3" });
      List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
      for (Tag tg : tags) {
        String linktitle = tg.toPlainTextString();
        if (linktitle.indexOf("棉花进口月报") != -1) {
          long reportDate = getMonthDay(linktitle);

          if (isUpdate(reportDate, lastDay)) {
            String uuu = "http://www.cottonchina.org/stat/mianhua/" + tg.getAttribute("href");
            antImportMonth(reportDate, uuu, linktitle, SessionId);
          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private long getMonthDay(String linktitle) throws Exception {
    String regex = "[0-9]{4}年[0-1]{0,1}[0-9]{1}月";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(linktitle);
    if (m.find()) {
      String sttt = m.group();
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
      return Long.parseLong(sdf2.format(sdf1.parse(sttt)));
    } else {
      throw new Exception();
    }

  }

  private boolean isUpdate(long reportDate, long lastDay) {
    return reportDate >= lastDay;
  }

  private void antImportMonth(long reportDate, String url, String linktitle, String SessionId) throws Exception {
    File f = AntLogic.getFile("cottonchina/Import", linktitle + ".html");
    if (!f.exists()) {
      String html = CottonchinaUtil.getHTML(url, SessionId);
      AntLogic.saveFile("cottonchina/Import", linktitle + ".html", html);
    }
    if (f.exists()) {
      InputStream in = new FileInputStream(f);
      String html = FileStreamUtil.getFileContent(in);
      in.close();
      praseImportContent(reportDate, html, f);
    }
  }

  private void antExportMonth(long reportDate, String url, String linktitle, String SessionId) throws Exception {
    File f = AntLogic.getFile("cottonchina/Export", linktitle + ".html");
    if (!f.exists()) {
      String html = CottonchinaUtil.getHTML(url, SessionId);
      AntLogic.saveFile("cottonchina/Export", linktitle + ".html", html);
    }
    if (f.exists()) {
      InputStream in = new FileInputStream(f);
      String html = FileStreamUtil.getFileContent(in);
      in.close();
      praseExportContent(reportDate, html, f);
    }
  }

  private void praseExportContent(long reportDate, String html, File f) throws IOException, ParserException, NumberFormatException, SQLException {
    Parser parser = Parser.createParser(html, "GB2312");
    NodeList nl = parser.parse(null);
    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "class", "black" });
    NodeList table = HTMLParseUtil.getNodeList(nl, "table", tableattrs);
    List<Tag> trs = HTMLParseUtil.getTags(table, "tr");
    int row = 0;
    for (Tag tr : trs) {
      row++;
      if (row <= 4) {
        continue;
      }

      NodeList tdNs = tr.getChildren();
      List<Tag> tds = HTMLParseUtil.getTags(tdNs, "td");
      Country fromCountry = null;
      String valueSTR = tds.get(1).toPlainTextString();
      if (row == 5) {
        fromCountry = Country.getCountry("WHOLE");
      } else {
        fromCountry = Country.getCountry(tds.get(0).toPlainTextString());
      }
      double value = 0;
      try {
        value = Double.parseDouble(valueSTR);
      } catch (Exception e) {
        value = -1;
      }
      if (value < 0) {
        LogService.warn(f.getAbsolutePath() + " | value is  0:" + (tds.get(0).toPlainTextString()) + "|" + valueSTR);
        continue;
      }
      if (fromCountry == null) {
        if (value > 1000)
          LogService.warn(f.getAbsolutePath() + " | unknow conutry:" + (tds.get(0).toPlainTextString()) + "|" + valueSTR);
      } else {

        saveMonthInfo(reportDate, value, Country.getCountry("CHN"), fromCountry);
      }
    }
  }

  private void praseImportContent(long reportDate, String html, File f) throws IOException, ParserException, NumberFormatException, SQLException {
    Parser parser = Parser.createParser(html, "GB2312");
    NodeList nl = parser.parse(null);
    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "class", "black" });
    NodeList table = HTMLParseUtil.getNodeList(nl, "table", tableattrs);
    List<Tag> trs = HTMLParseUtil.getTags(table, "tr");
    int row = 0;
    for (Tag tr : trs) {
      row++;
      if (row <= 4) {
        continue;
      }

      NodeList tdNs = tr.getChildren();
      List<Tag> tds = HTMLParseUtil.getTags(tdNs, "td");
      Country fromCountry = null;
      String valueSTR = tds.get(1).toPlainTextString();
      if (row == 5) {
        fromCountry = Country.getCountry("WHOLE");
      } else {
        fromCountry = Country.getCountry(tds.get(0).toPlainTextString());
      }
      double value = 0;
      try {
        value = Double.parseDouble(valueSTR);
      } catch (Exception e) {
        value = -1;
      }
      if (value < 0) {
        LogService.warn(f.getAbsolutePath() + " | value is  0:" + (tds.get(0).toPlainTextString()) + "|" + valueSTR);
        continue;
      }
      if (fromCountry == null) {
        if (value > 1000)
          LogService.warn(f.getAbsolutePath() + " | unknow conutry:" + (tds.get(0).toPlainTextString()) + "|" + valueSTR);
      } else {

        saveMonthInfo(reportDate, value, fromCountry, Country.getCountry("CHN"));
      }
    }
  }

  private void saveMonthInfo(long reportDate, double total, Country fromCountry, Country toCountry) throws SQLException {
    Date now = new Date();

    ImportExportMonth impexpDay = ImportExportMonthSQL.getObj(reportDate, toCountry, fromCountry, Commodity.getCommodity("棉花"), getSource());
    if (impexpDay != null) {
      impexpDay.setSource(getSource());
      impexpDay.setUpdatedAt(now);
      impexpDay.setUpdatedBy(AntManger.UPDATEBY);
      impexpDay.setWeightUnit(WeightUnit.getWeightUnit("吨"));
      impexpDay.setValue(total);
    } else {
      impexpDay = new ImportExportMonth();
      impexpDay.setCommodity(Commodity.getCommodity("棉花"));
      impexpDay.setFromCountry(fromCountry);
      impexpDay.setToCountry(toCountry);
      impexpDay.setReportDate(reportDate);
      impexpDay.setSource(getSource());
      impexpDay.setUpdatedAt(now);
      impexpDay.setUpdatedBy(AntManger.UPDATEBY);
      impexpDay.setWeightUnit(WeightUnit.getWeightUnit("吨"));
      impexpDay.setValue(total);
    }
    ImportExportMonthSQL.save(impexpDay);
  }

  @Override
  public String getSource() {

    return "customs.gov.cn";
  }

}
