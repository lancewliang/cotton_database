package ant.cotton.wasde;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.production.country.YieldDay;
import model.entity.production.country.YieldYear;
import model.entity.production.country.db.YieldDaySQL;
import model.entity.production.country.db.YieldYearSQL;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.db.DBUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetINDWASDEAndCope implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetINDWASDEAndCope mhhf = new GetINDWASDEAndCope();
    try {
      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * http://www.caionline.in/ps.asp
   */
  @Override
  public void doAnt() {
    Date now = new Date();
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(now);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    long today = Long.parseLong(sdf2.format(calendar.getTime()));

    try {
      String html = engine.util.Util.getHTML("http://www.caionline.in/ps.asp", "utf-8");
      long lastDay1 = WorldSupplyDemandMonthlyHistorySQL.getLastDay(Commodity.getCommodity("ÃÞ»¨"), getSource());
      long lastDay2 = YieldYearSQL.getLastDay(Commodity.getCommodity("ÃÞ»¨"), Country.getCountry("IND"), getSource());

      Parser parser = Parser.createParser(html, "iso-8859-1");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "class", "link" });
      List<Tag> trs = HTMLParseUtil.getTags(nl, "a");
      for (int i = 0; i < trs.size(); i++) {
        Tag a = trs.get(i);
        if (a.toPlainTextString().indexOf("more") != -1) {
          String href = a.getAttribute("href");
          String[] ss = StringUtil.split(href, ".", true);
          String[] ss2 = ss[0].split("_");
          if (ss2.length == 3) {
            try {
              String str = DateUtil.getMonthByEN(ss2[0]) + "/" + ss2[1] + "/" + ss2[2];
              long date = DateUtil.getYYYYMMDD(str);
              if (date >= lastDay1 || date >= lastDay2) {

                parsePage(lastDay1, lastDay2, date, href);
              }
            } catch (Exception e1) {
              LogService.trace(e1, href);
            }
          }
        }
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  private void parsePage(long lastDay1, long lastDay2, long today, String href) throws IOException, ParserException, ParseException, SQLException {
    File file = AntLogic.getFile(getSource() + "/WASDE", href + "market_report.htm");

    if (!file.exists()) {
      String html = engine.util.Util.getHTML("http://www.caionline.in/" + href, "utf-8");

      AntLogic.saveFile(getSource() + "/WASDE", href + "market_report.htm", html);

    }

    if (file.exists()) {
      InputStream in = new FileInputStream(file);
      String html3 = FileStreamUtil.getFileContent(in);
      in.close();
      praseContent(html3, lastDay1, lastDay2, file);
    }
  }

  private boolean praseContent(String content, long lastDay1, long lastDay2, File f) throws ParserException, SQLException, ParseException {
    Date now = new Date();
    content = StringUtil.replaceString(content, "&nbsp;", "");
    content = StringUtil.replaceString(content, "\n", "");
    content = HTMLParseUtil.trim2bank(content).trim();
    Parser parser = Parser.createParser(content, "iso-8859-1");
    NodeList nl = parser.parse(null);
    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "border", "0" });
    tableattrs.add(new String[] { "width", "600" });
    tableattrs.add(new String[] { "cellpadding", "0" });
    tableattrs.add(new String[] { "cellspacing", "0" });
    Tag table = HTMLParseUtil.getTag(nl, "table", tableattrs);
    List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
    int r = 0;
    for (int i = 0; i < trs.size(); i++) {
      Tag tr = trs.get(i);
      List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");
      if (tds.size() == 1) {
        Tag td = tds.get(0);
        String text = td.toPlainTextString();
        text = HTMLParseUtil.trim2bank(text).trim();
        if (text.indexOf("INDIAN COTTON CROP ESTIMATE FOR") != -1) {
          i = processCrop(lastDay2, text, trs, i);
        } else if (text.indexOf("INDIAN COTTON BALANCE SHEET") != -1) {
          i = processSheet(lastDay1, text, trs, i);
        }
      }
    }
    return true;
  }

  private int processCrop(long lastDate, String text, List<Tag> trs, int i) throws ParseException, NumberFormatException, SQLException {
    int tstart = text.indexOf("Estimated as on");
    int tsend = text.indexOf("(", tstart);

    String date = null;

    try {
      if (tsend > 0) {
        date = text.substring(tstart + "Estimated as on".length(), tsend).trim();
      } else {
        date = text.substring(tstart + "Estimated as on".length());
      }
      date = StringUtil.replaceString(date, "2014", " 2014");
      date = HTMLParseUtil.trim2bank(date).trim();
      getDate(date);
    } catch (Exception e) {
      System.out.println(date);
      e.printStackTrace();
    }
    long reportDate = getDate(date);

    String currentYear = null;
    String lastYear = null;
    i++;
    i++;
    Tag __trYH = trs.get(i);
    try {

      List<Tag> __tdYHs = HTMLParseUtil.getTags(__trYH.getChildren(), "td");
      if (__tdYHs.size() == 3) {
        currentYear = __tdYHs.get(1).toPlainTextString();
        lastYear = __tdYHs.get(2).toPlainTextString();
      } else if (__tdYHs.size() == 2) {
        currentYear = __tdYHs.get(0).toPlainTextString();
        lastYear = __tdYHs.get(1).toPlainTextString();
      } else {
        currentYear = __tdYHs.get(1).toPlainTextString();
        lastYear = __tdYHs.get(2).toPlainTextString();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    i++;
    int __i = 0;
    while (true) {
      i++;
      Tag __tr = null;
      try {
        __tr = trs.get(i);
      } catch (Exception e) {
        return i;
      }
      List<Tag> __tds = HTMLParseUtil.getTags(__tr.getChildren(), "td");
      if (__tds.size() == 7) {
        Tag __td1 = __tds.get(0);
        String td1Str = __td1.toPlainTextString();
        // System.out.println(td1Str);

        td1Str = HTMLParseUtil.trim2bank(td1Str).trim();
        if ("Grand Total".equals(td1Str.trim())) {
          String td2Str = __tds.get(2).toPlainTextString();
          String td4Str = __tds.get(4).toPlainTextString();
          String td6Str = __tds.get(6).toPlainTextString();
          if (reportDate >= lastDate) {
            saveGinnObj(reportDate, Double.parseDouble(td6Str));
            saveGinnObj(getNextMonthStartDate(reportDate), Double.parseDouble(td6Str));
            saveYieldYear(currentYear, reportDate, Double.parseDouble(td2Str), 0);
            saveYieldYear(lastYear, reportDate, Double.parseDouble(td4Str), 0);
          }
          return i;
        }
      }
      __i++;
      if (__i > 20) {
        return i;
      }
    }
  }

  private long getNextMonthStartDate(long reportDate) throws ParseException {
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    Date date = sdf2.parse("" + reportDate);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DATE, 4);
    return Long.parseLong(sdf2.format(calendar.getTime()));

  }

  private int processSheet(long lastDate, String text, List<Tag> trs, int i) throws SQLException, ParseException {
    i++;
    String text2 = trs.get(i).toPlainTextString();
    int tstart = text2.indexOf("Estimated as on");

    String date = text2.substring(tstart + "Estimated as on".length());
    long reportDate = getDate(date);

    i++;

    i++;
    Tag __trYH = trs.get(i);
    List<Tag> __tdYHs = HTMLParseUtil.getTags(__trYH.getChildren(), "td");
    String currentYear = __tdYHs.get(1).toPlainTextString();
    String lastYear = __tdYHs.get(2).toPlainTextString();
    i++;

    i++;
    double[] __tdOpeningTDs = getValues(trs.get(++i), 2, 4);
    double currentYearOpeningStockData = __tdOpeningTDs[0];
    double lastYearOpeningStockData = __tdOpeningTDs[1];
    //
    double[] __tdCropTDs = getValues(trs.get(++i), 2, 4);
    double currentYearCropData = __tdCropTDs[0];
    double lastYearCropData = __tdCropTDs[1];
    //
    double[] __tdImportsTDs = getValues(trs.get(++i), 2, 4);
    double currentYearImportsData = __tdImportsTDs[0];
    double lastYearImportsData = __tdImportsTDs[1];
    i++;
    i++;
    double[] __tdMillConsumptionTDs = getValues(trs.get(++i), 2, 4);
    double currentYearMillConsumptionData = __tdMillConsumptionTDs[0];
    double lastYearMillConsumptionData = __tdMillConsumptionTDs[1];
    //
    double[] __tdConsumptionbySSIUnitsTDs = getValues(trs.get(++i), 2, 4);
    double currentYearConsumptionbySSIUnitsData = __tdConsumptionbySSIUnitsTDs[0];
    double lastYearConsumptionbySSIUnitsData = __tdConsumptionbySSIUnitsTDs[1];
    //
    double[] __tdNonmillConsumptionTDs = getValues(trs.get(++i), 2, 4);
    double currentYearNonmillConsumptionData = __tdNonmillConsumptionTDs[0];
    double lastYearNonmillConsumptionData = __tdNonmillConsumptionTDs[1];
    //
    double[] __tdExportsTDs = getValues(trs.get(++i), 2, 4);
    double currentYearExportsData = __tdExportsTDs[0];
    double lastYearExportsData = __tdExportsTDs[1];
    i++;
    double[] __tdClosingStock1TDs = getValues(trs.get(++i), 2, 4);
    double currentYearClosingStockData = __tdClosingStock1TDs[0];

    double[] __tdClosingStock2TDs = getValues(trs.get(++i), 2, 4);
    double lastYeaClosingStockData = __tdClosingStock2TDs[1];
    if (reportDate >= lastDate) {
      saveWorldSupplyDemandMonthlyHistory(reportDate, currentYear, currentYearOpeningStockData, currentYearCropData, currentYearImportsData, currentYearMillConsumptionData + currentYearConsumptionbySSIUnitsData + currentYearNonmillConsumptionData, currentYearExportsData, currentYearClosingStockData);
      saveWorldSupplyDemandMonthlyHistory(reportDate, lastYear, lastYearOpeningStockData, lastYearCropData, lastYearImportsData, lastYearMillConsumptionData + lastYearConsumptionbySSIUnitsData + lastYearNonmillConsumptionData, lastYearExportsData, lastYeaClosingStockData);
    }
    return i;
  }

  private double[] getValues(Tag tr, int a1, int a2) {
    List<Tag> __tdOpeningTDs = HTMLParseUtil.getTags(tr.getChildren(), "td");
    String S1 = __tdOpeningTDs.get(a1).toPlainTextString();
    String S2 = __tdOpeningTDs.get(a2).toPlainTextString();

    double d = 0;
    try {
      S1 = StringUtil.replaceString(S1, "*", "");
      d = Double.parseDouble(S1);

    } catch (Exception e) {
    }
    double f = 0;
    try {
      S2 = StringUtil.replaceString(S2, "*", "");
      f = Double.parseDouble(S2);

    } catch (Exception e) {
    }

    return new double[] { d, f };
  }

  private void saveGinnObj(long reportDate, double currentValue) throws SQLException {
    Date now = new Date();
    YieldDay yieldDay = YieldDaySQL.getObj(reportDate, Country.getCountry("IND"), Commodity.getCommodity("ÃÞ»¨"), getSource());

    if (yieldDay == null) {
      yieldDay = new YieldDay();
      yieldDay.setCommodity(Commodity.getCommodity("ÃÞ»¨"));
      yieldDay.setCountry(Country.getCountry("IND"));
      yieldDay.setReportDate(reportDate);
      yieldDay.setSource(getSource());
    }
    yieldDay.setUpdatedAt(now);
    yieldDay.setUpdatedBy(AntManger.UPDATEBY);
    yieldDay.setWeightUnit(WeightUnit.getWeightUnit("Ç§¶Ö"));
    yieldDay.setTotal(currentValue);

    YieldDaySQL.save(yieldDay);
  }

  private void saveYieldYear(String year, long reportDate, double value, int status) throws SQLException {

    Date now = new Date();
    Country country = Country.getCountry("IND");
    Commodity commodity = Commodity.getCommodity("ÃÞ»¨");
    YieldYear obj = YieldYearSQL.getObj(year, reportDate, country, status, commodity, getSource());
    if (obj == null) {
      obj = new YieldYear();
      obj.setCommodity(commodity);
      obj.setCountry(country);
      obj.setReportDate(reportDate);
      obj.setSource(getSource());
      obj.setYear(year);
      obj.setReportStatus(status);
    }
    obj.setValue(value);
    obj.setWeightUnit(WeightUnit.getWeightUnit("Ç§¶Ö"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    YieldYearSQL.save(obj);
  }

  private void saveWorldSupplyDemandMonthlyHistory(long reportDate, String year, double OpeningStock, double Crop, double imports, double Consumption, double exports, double ClosingStock) throws SQLException {
    Date now = new Date();
    WorldSupplyDemandMonthlyHistory record = WorldSupplyDemandMonthlyHistorySQL.getObj(Country.getCountry("IND"), year, reportDate, 0, Commodity.getCommodity("ÃÞ»¨"), getSource());
    if (record == null) {
      record = new WorldSupplyDemandMonthlyHistory();
      record.setReportDate(reportDate);
      record.setCountry(Country.getCountry("IND"));
      record.setYear(year);
      record.setCommodity(Commodity.getCommodity("ÃÞ»¨"));
      record.setSource(getSource());

    }
    record.setBeginStock(OpeningStock);
    record.setUses(Consumption);
    record.setProduction(Crop);
    record.setImports(imports);
    record.setExports(exports);
    record.setEndStock(ClosingStock);
    record.setUpdatedAt(now);
    record.setUpdatedBy(AntManger.UPDATEBY);
    record.setWeightUnit(WeightUnit.getWeightUnit("Ç§¶Ö"));
    if (!WorldSupplyDemandMonthlyHistorySQL.isSameWithLast(record)) {
      WorldSupplyDemandMonthlyHistorySQL.save(record);
    }
  }

  private long getDate(String dateSTR) throws ParseException {
    String[] datestr = dateSTR.trim().split(" ");
    String dd = datestr[0].replaceAll("th", "").replaceAll("st", "");

    String str = DateUtil.getMonthByEN(datestr[1]) + "/" + dd + "/" + datestr[2];
    return DateUtil.getYYYYMMDD(str);
  }

  @Override
  public String getSource() {
    return "CottonAssociationOfINDIA";
  }

}
