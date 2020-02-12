package ant.cotton.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
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
import model.entity.custom.country.ImportExportDay;
import model.entity.custom.country.db.ImportExportDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ui.util.PDFBOX;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCustomUSDAExport implements DayAnt {

  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    GetCustomUSDAExport exp = new GetCustomUSDAExport();
     exp.doAnt();
   // exp.doAntHistory(2014);
    //exp.doAntHistory(2013);
    // exp.doAntHistory(2012);
    // exp.doAntHistory(2011);
    // exp.doAntHistory(2010);
  }

  @Override
  public void doAnt() {

    for (int i = 1; i < 6; i++) {
      try {
        String tempcontent = Util.getHTML(getURL(i), "utf-8", 20);
        String h = praseheader(tempcontent).trim();
        long reportDate = getDate(h);
        File f = AntLogic.getFile(getSource() + "/export-sales/" + (reportDate / 10000), h + ".htm");
        if (!f.exists()) {
          AntLogic.saveFile(getSource() + "/export-sales/" + (reportDate / 10000), h + ".htm", tempcontent);
          engine.util.Util.getFile(getURL(i), f);
        }
        InputStream in = new FileInputStream(f);
        String content = FileStreamUtil.getFileContent(in);
        in.close();
        praseContent(content);
      } catch (Exception e) {
        LogService.trace(e, null);
      }

    }

  }

  public void doAntHistory(int year) {
    String url = "http://apps.fas.usda.gov/export-sales/year" + year + ".htm";
    try {
      String listpage = Util.getContent(url, "utf-8");
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
      for (Tag tag : tags) {
        String href = tag.getAttribute("href");
        if (None.isNonBlank(href) && href.endsWith("pdf")) {
          try {
            String pdfHref = "http://apps.fas.usda.gov/export-sales/" + href;
            File f = AntLogic.getFile(getSource() + "/export-sales/" + year, href.split("/")[1]);
            if (!f.exists()) {
              engine.util.Util.getFile(pdfHref, f);
            }
            PDFBOX parse = new PDFBOX();
            String content = parse.getPDFText(f);
            praseContent(content);
          } catch (Exception e) {
            LogService.trace(e, null);
          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  private String getURL(int i) {
    return "http://apps.fas.usda.gov/export-sales/week" + i + "/complete.htm";
  }

  public String praseheader(String content) {
    int i = content.indexOf("U. S. EXPORT SALES AS OF ");
    int i2 = content.indexOf("SUMMARY ", i + 10);
    return content.substring(i + "U. S. EXPORT SALES AS OF ".length(), i2);
  }

  public void praseContent(String content) {
    BufferedReader br = null;
    String line = "";
    ParseInfo parseInfo = new ParseInfo();
    try {

      Date now = new Date();
      br = new BufferedReader(new StringReader(content));
      long reportDate = 0;
      while ((line = br.readLine()) != null) {

        // take Crop and State
        if (parseInfo.stepIndex < 0) {
          if (line.startsWith("ALL UPLAND COTTON")) {
            parseInfo.stepIndex = 0;

          }

        } else if (parseInfo.stepIndex == 0) {
          if (line.startsWith("1000 RUNNING BALES     AS OF")) {
            parseInfo.stepIndex = 1;
            String dateSTR = line.substring("1000 RUNNING BALES     AS OF".length()).trim();
            reportDate = getDate(dateSTR);
          }

        } else if (parseInfo.stepIndex == 1) {
          if (line.startsWith("-------------------------------------")) {
            parseInfo.stepIndex = 2;
          }

        } else if (parseInfo.stepIndex == 2) {
          if (line.startsWith("-------------------------------------")) {
            parseInfo.stepIndex = 3;
          }

        } else if (parseInfo.stepIndex == 3) {
          String line1 = line.trim();
          if (line1.startsWith("-------------------------------------")) {
            parseInfo.stepIndex = 4;
          } else if (line1.length() > 2) {
            String countryStr = line.split(":")[0].trim();
            Country country = Country.getCountry(countryStr);
            String[] values = line.split(":")[1].split(" ");
            List<String> list = new ArrayList<String>();
            for (String s : values) {
              if (None.isNonBlank(s))
                list.add(s);
            }
            double total = 0;
            try {
              total = Double.parseDouble(list.get(2));
            } catch (Exception e) {
              total = -1000;
              System.out.println(line);
            }

            if (country != null && total >= 0) {
              try {

                ImportExportDay impexpDay = ImportExportDaySQL.getObj(reportDate, country, Country.getCountry("USA"), Commodity.getCommodity("棉花"), getSource());
                if (impexpDay != null) {
                  impexpDay.setSource(getSource());
                  impexpDay.setUpdatedAt(now);
                  impexpDay.setUpdatedBy(AntManger.UPDATEBY);
                  impexpDay.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
                  impexpDay.setTotal(total);
                } else {
                  impexpDay = new ImportExportDay();
                  impexpDay.setCommodity(Commodity.getCommodity("棉花"));
                  impexpDay.setFromCountry(Country.getCountry("USA"));
                  impexpDay.setToCountry(country);
                  impexpDay.setReportDate(reportDate);
                  impexpDay.setSource(getSource());
                  impexpDay.setUpdatedAt(now);
                  impexpDay.setUpdatedBy(AntManger.UPDATEBY);
                  impexpDay.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
                  impexpDay.setTotal(total);
                }
                ImportExportDaySQL.save(impexpDay);

                if (isYearStartMonth(reportDate)) {
                  ImportExportDay startDay = ImportExportDaySQL.getObj(getYearStartMonth(reportDate), country, Country.getCountry("USA"), Commodity.getCommodity("棉花"), getSource());
                  if (startDay == null) {
                    startDay = new ImportExportDay();
                    startDay.setCommodity(Commodity.getCommodity("棉花"));
                    startDay.setFromCountry(Country.getCountry("USA"));
                    startDay.setToCountry(country);
                    startDay.setReportDate(getYearStartMonth(reportDate));
                    startDay.setSource(getSource());
                    startDay.setUpdatedAt(now);
                    startDay.setUpdatedBy(AntManger.UPDATEBY);
                    startDay.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
                    startDay.setTotal(0);
                  } else {
                    startDay.setTotal(0);
                  }
                  ImportExportDaySQL.save(startDay);
                }
              } catch (SQLException e) {
                LogService.trace(e, null);
              }

            } else {
              System.out.println(line);
            }

          }

        } else if (parseInfo.stepIndex == 4) {
          if (line.startsWith("-------------------------------------")) {
            parseInfo.stepIndex = 5;
          }
        } else if (parseInfo.stepIndex == 5) {
          if (line.startsWith("TOTAL KNOWN & UNKNOWN :")) {

            Country country = Country.getCountry("全球");
            String[] values = line.split(":")[1].split(" ");
            List<String> list = new ArrayList<String>();
            for (String s : values) {
              if (None.isNonBlank(s))
                list.add(s);
            }
            double total = Double.parseDouble(list.get(2));
            // System.out.println(line);
            if (country != null) {
              try {
                ImportExportDay impexpDay = ImportExportDaySQL.getObj(reportDate, country, Country.getCountry("USA"), Commodity.getCommodity("棉花"), getSource());
                if (impexpDay != null) {
                  impexpDay.setSource(getSource());
                  impexpDay.setUpdatedAt(now);
                  impexpDay.setUpdatedBy(AntManger.UPDATEBY);
                  impexpDay.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
                  impexpDay.setTotal(total);
                } else {
                  impexpDay = new ImportExportDay();
                  impexpDay.setCommodity(Commodity.getCommodity("棉花"));
                  impexpDay.setFromCountry(Country.getCountry("USA"));
                  impexpDay.setToCountry(country);
                  impexpDay.setReportDate(reportDate);
                  impexpDay.setSource(getSource());
                  impexpDay.setUpdatedAt(now);
                  impexpDay.setUpdatedBy(AntManger.UPDATEBY);
                  impexpDay.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
                  impexpDay.setTotal(total);
                }
                ImportExportDaySQL.save(impexpDay);
                if (isYearStartMonth(reportDate)) {
                  ImportExportDay startDay = ImportExportDaySQL.getObj(getYearStartMonth(reportDate), country, Country.getCountry("USA"), Commodity.getCommodity("棉花"), getSource());
                  if (startDay == null) {
                    startDay = new ImportExportDay();
                    startDay.setCommodity(Commodity.getCommodity("棉花"));
                    startDay.setFromCountry(Country.getCountry("USA"));
                    startDay.setToCountry(country);
                    startDay.setReportDate(getYearStartMonth(reportDate));
                    startDay.setSource(getSource());
                    startDay.setUpdatedAt(now);
                    startDay.setUpdatedBy(AntManger.UPDATEBY);
                    startDay.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
                    startDay.setTotal(0);
                  } else {
                    startDay.setTotal(0);
                  }
                  ImportExportDaySQL.save(startDay);
                }
              } catch (SQLException e) {
                LogService.trace(e, null);
              }
            }
            parseInfo.stepIndex = 6;
            break;
          }

        }

      }

    } catch (Exception e) {
      LogService.trace(e, "");
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  class ParseInfo {
    int stepIndex = -100;
    int Crop_and_State_line_size = 0;

  }

  private boolean isYearStartMonth(long reportdate) throws ParseException {
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    Date date = sdf2.parse("" + reportdate);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.get(Calendar.MONTH) == 7;
  }

  private long getYearStartMonth(long reportdate) throws ParseException {
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    Date date = sdf2.parse("" + reportdate);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf3.format(calendar.getTime()));
  }

  private long getDate(String dateSTR) throws ParseException {
    String[] dateSTRs = dateSTR.split(",");
    String[] mmdd = dateSTRs[0].split(" ");
    int mm = DateUtil.getMonthByEN(mmdd[0]);
    String str = mm + "/" + mmdd[1] + "/" + dateSTRs[1];

    return DateUtil.getYYYYMMDD(str);

  }

  @Override
  public String getSource() {

    return "USDA";
  }
}
