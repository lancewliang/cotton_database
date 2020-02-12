package ant.cotton.consumption;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
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
import model.entity.consumption.ConsumptionDay;
import model.entity.consumption.db.ConsumptionDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.StringUtil;
import tcc.utils.file.FileNameUtil;
import tcc.utils.log.LogService;
import ui.util.PDFBOX;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetUSDAConsumptionMonth implements DayAnt {

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetUSDAConsumptionMonth mhhf = new GetUSDAConsumptionMonth();
    try {
      // mhhf.prasePDF(new
      // File("D:/lwwork/ExamKing/economics3-data/ant/USDA/Cotton and Wool Outlook/2010/CWS-09-13-2010.pdf"));
      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private List<String[]> getFileList() {
    List<String[]> flist = new ArrayList<String[]>();
    try {
      String url = "http://usda.mannlib.cornell.edu/MannUsda/viewDocumentInfo.do?documentID=1281";
      String content = engine.util.Util.getHTML(url, "UTF-8");
      long lastDay = ConsumptionDaySQL.getLastDay(Commodity.getCommodity("棉花"), Country.getCountry("USA"), getSource());
      if (lastDay <= 0)
        lastDay = 20081001;

      Parser parser = Parser.createParser(content, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> dirElcs = new ArrayList<String[]>();
      dirElcs.add(new String[] { "class", "dirElement" });
      List<Tag> dirdivs = HTMLParseUtil.getTags(nl, "div", dirElcs);
      for (Tag dir : dirdivs) {
        long year = 0;
        try {
          year = Long.parseLong(dir.getAttribute("id").substring(1, 5));
        } catch (Exception e) {
        }
        if (year >= lastDay / 10000) {
          List<String[]> fileElementcs = new ArrayList<String[]>();
          fileElementcs.add(new String[] { "class", "fileElement" });
          List<Tag> filedivs = HTMLParseUtil.getTags(dir.getChildren(), "div", fileElementcs);
          for (Tag file : filedivs) {
            String filename = file.getAttribute("id");
            if (FileNameUtil.getExtension(filename).equals("pdf")) {
              String date = filename.replaceAll("CWS-", "").replaceAll(".pdf", "");
              long reportDate = getReportDate(date);
              if (reportDate >= lastDay) {
                Tag atag = HTMLParseUtil.getTag(file.getChildren(), "a");
                flist.add(new String[] { filename, atag.getAttribute("href"), "" + (reportDate / 10000) });
              }
            }

          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return flist;
  }

  private void prasePDF(File f) throws IOException {
    PDFBOX parse = new PDFBOX();
    String content = parse.getPDFText(f);
    prasePre(f, 0, content);
  }

  @Override
  public void doAnt() {
    List<String[]> files = getFileList();
    for (String[] fileurl : files) {
      try {
        File f = AntLogic.getFile(getSource() + "/Cotton and Wool Outlook/" + fileurl[2], fileurl[0]);
        if (!f.exists()) {
          AntLogic.saveFile(getSource() + "/Cotton and Wool Outlook/" + fileurl[2], fileurl[0], fileurl[1]);

          engine.util.Util.getFile(fileurl[1], f);
        }
        prasePDF(f);

      } catch (Exception e) {
        LogService.trace(e, null);
      }
    }
  }

  private boolean prasePre(File f, long lastDay, String content) {

    BufferedReader br = null;
    boolean todo = false;
    String line = "";
    ParseInfo parseInfo = new ParseInfo();
    try {
      boolean debug = false;
      br = new BufferedReader(new StringReader(content));
      String line1 = "", line2 = "";
      while ((line = br.readLine()) != null) {
        if (parseInfo.stepIndex < 0) {
          if (line.indexOf("U.S. fiber demand") != -1 || line.indexOf("U.S. cotton system fiber consumption") != -1

          || line.indexOf("U .S . co tto n  s ys tem  fib e r con s um p tio n") != -1) {
            parseInfo.stepIndex = 0;
            if (debug)
              LogService.log(line);
          }
        } else if (parseInfo.stepIndex == 0) {
          line = HTMLParseUtil.trim2bank(line).trim();
          parseInfo.stepIndex = 1;
          line1 = line;
          if (debug)
            LogService.log(line);
        } else if (parseInfo.stepIndex == 1) {
          line = HTMLParseUtil.trim2bank(line).trim();
          if (debug)
            LogService.log(line);
          line2 = line;
          parseInfo.stepIndex = 2;
        } else if (parseInfo.stepIndex == 2) {
          line = HTMLParseUtil.trim2bank(line).trim();
          if (line.startsWith("Total since August 1")) {
            if (debug)
              LogService.log(line);
            line = StringUtil.replaceString(line, "1/", "").trim();
            line = StringUtil.replaceString(line, "1 /", "").trim();
            line = StringUtil.replaceString(line, " ,", ",");

            String[] months = null, years = null;
            int caseSTR = 0;
            if (line2.startsWith("Item")) {
              String[] ss = line2.split(" ");
              for (String s : ss) {
                if (DateUtil.hasMonthByEN(s)) {
                  caseSTR = 1;
                  break;
                }
              }
            }
            List<String> monthlist = new ArrayList();
            if (caseSTR == 0) {
              months = line1.trim().split(" ");
              years = line2.substring("item".length()).trim().split(" ");
              for (int i = 0; i < months.length; i++) {
                monthlist.add(DateUtil.getMonthByEN(months[i]) + "/28/" + years[i]);
              }
            } else if (caseSTR == 1) {
              years = line1.trim().split(" ");
              months = line2.substring("item".length()).trim().split(" ");

              for (int i = 0; i < months.length; i++) {
                String year = null;
                if (i == 0) {
                  year = years[0];
                } else if (months.length - 1 == i) {
                  year = years[years.length - 1];
                } else {
                  if (DateUtil.getMonthByEN(months[i]) < DateUtil.getMonthByEN(months[0])) {
                    year = years[1];
                  } else {
                    year = years[0];
                  }
                }
                monthlist.add(DateUtil.getMonthByEN(months[i]) + "/28/" + year);
              }
            }
            String totalStr = null;

            if (line.startsWith("Total since August 1")) {
              totalStr = line.substring("Total since August 1".length()).trim();
            } else if (line.startsWith("To ta l s in ce Au gu s t 1  1 /")) {
              totalStr = line.substring("To ta l s in ce Au gu s t 1  1 /".length()).trim();
            }

            String[] totals = totalStr.split(" ");
            int i = 0;
            for (String monthstr : monthlist) {

              long reportDate = DateUtil.getYYYYMMDD(monthstr);

              double total = 0;
              try {
                total = Double.parseDouble(totals[i].replaceAll(",", ""));
              } catch (Exception e) {
                System.out.println(monthstr + " total" + total);
                System.out.println(line);
              }
              if (total == 0)
                System.out.println(line);
              saveObj(reportDate, total);
              long nextMonth1day = getYearStartMonth(reportDate);
              if (isYearStartMonth(nextMonth1day)) {
                saveObj(nextMonth1day, 0);
              } else {
                saveObj(nextMonth1day, total);
              }
              i++;

            }
            parseInfo.stepIndex = 3;
            todo = true;
            break;

          }
        }
      }

    } catch (Exception e) {
      LogService.trace(e, content);

    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    if (!todo) {
      LogService.err(f.getAbsolutePath() + "not todo");
    }
    return true;
  }

  @Override
  public String getSource() {

    return "USDA";
  }

  private void saveObj(long reportDate, double total) throws SQLException {
    Date now = new Date();
    ConsumptionDay day = ConsumptionDaySQL.getObj(reportDate, Country.getCountry("USA"), Commodity.getCommodity("棉花"), getSource());
    if (day == null) {

      day = new ConsumptionDay();
      day.setCommodity(Commodity.getCommodity("棉花"));
      day.setCountry(Country.getCountry("USA"));
      day.setReportDate(reportDate);
      day.setSource(getSource());

    }
    day.setUpdatedAt(now);
    day.setUpdatedBy(AntManger.UPDATEBY);
    day.setTotal(total);
    day.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
    ConsumptionDaySQL.save(day);
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
    calendar.add(Calendar.MONTH, 1);
    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf3.format(calendar.getTime()));
  }

  private long getReportDate(String str) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(str);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf2.format(calendar.getTime()));
  }

  class ParseInfo {
    int stepIndex = -100;
    int Crop_and_State_line_size = 0;

  }
}
