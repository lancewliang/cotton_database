package ant.cotton.production;

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

import model.constant.AreaUnit;
import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.consumption.ConsumptionDay;
import model.entity.consumption.db.ConsumptionDaySQL;
import model.entity.production.country.GrowAreaYear;
import model.entity.production.country.YieldYear;
import model.entity.production.country.db.GrowAreaYearSQL;
import model.entity.production.country.db.YieldYearSQL;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;

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
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetUSDAProductionMonth implements DayAnt {

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetUSDAProductionMonth mhhf = new GetUSDAProductionMonth();
    try {

      mhhf.doAnt();
      File f = new File("D:/lwwork/ExamKing/economics3-data/ant/USDA/Cotton and Wool Outlook/2012/CWS-04-12-2012.pdf");
      // mhhf.prasePDF(f, 20120412, 0);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private List<String[]> getFileList(long lastDay) {
    List<String[]> flist = new ArrayList<String[]>();
    try {
      String url = "http://usda.mannlib.cornell.edu/MannUsda/viewDocumentInfo.do?documentID=1281";
      String content = engine.util.Util.getHTML(url, "UTF-8");

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

  private void prasePDF(File f, long reportDate, long lastDay) throws IOException {
    PDFBOX parse = new PDFBOX();
    String content = parse.getPDFText(f);
    prasePre(f, reportDate, lastDay, content);
  }

  @Override
  public void doAnt() {
    try {
      long lastDay = GrowAreaYearSQL.getLastDay(Commodity.getCommodity("棉花"), Country.getCountry("USA"), getSource());
      if (lastDay <= 0)
        lastDay = 20051001;
      List<String[]> files = getFileList(lastDay);
      for (String[] fileurl : files) {
        try {
          File f = AntLogic.getFile(getSource() + "/Cotton and Wool Outlook/" + fileurl[2], fileurl[0]);
          if (!f.exists()) {
            AntLogic.saveFile(getSource() + "/Cotton and Wool Outlook/" + fileurl[2], fileurl[0], fileurl[1]);

            engine.util.Util.getFile(fileurl[1], f);
          }

        } catch (Exception e) {
          LogService.trace(e, null);
        }
      }

      File f = AntLogic.getFile(getSource() + "/Cotton and Wool Outlook", "");
      for (File f1 : f.listFiles()) {
        if (f1.isDirectory()) {
          if (f1.getName().equals("CVS")) {
            continue;
          }
          for (File f2 : f1.listFiles()) {
            if (f2.getName().equals("CVS")) {
              continue;
            }
            if (f2.isFile()) {
              try {
                String filename = f2.getName();
                String date = filename.replaceAll("CWS-", "").replaceAll(".pdf", "");
                long reportDate = getReportDate(date);
                if (reportDate >= lastDay) {
                  prasePDF(f2, reportDate, lastDay);
                }
              } catch (Exception e1) {
                LogService.trace(e1, f.getAbsolutePath());
              }
            }
          }
        }
      }

    } catch (Exception e1) {
      LogService.trace(e1, null);
    }
  }

  private boolean prasePre(File f, long reportDate, long lastDay, String content) {

    BufferedReader br = null;
    boolean todo = false;
    String line = "";
    String lastline = "";
    ParseInfo parseInfo = new ParseInfo();
    try {

      boolean debug = false;
      br = new BufferedReader(new StringReader(content));
      String year1 = "";
      String year2 = "";
      while ((line = br.readLine()) != null) {

        if (parseInfo.stepIndex < 0) {
          if (line.indexOf("U.S. cotton supply and use estimates") != -1) {
            parseInfo.stepIndex = 0;
            if (debug)
              LogService.log(line);
          }
        } else if (parseInfo.stepIndex == 0) {
          line = HTMLParseUtil.trim2bank(line).trim();

          line = HTMLParseUtil.trim2bank(line).trim();

          if (debug)
            LogService.log(line);
          if (line.startsWith("Item")) {
            String[] lss = line.split(" ");
            year1 = lss[1];

            String[] ss = year1.split("/");
            long y2 = Long.parseLong(ss[1]) + 1;
            year2 = (Long.parseLong(ss[0]) + 1) + "/" + (y2 >= 10 ? ("" + y2) : ("0" + y2));
            if (lss[2].indexOf("/") != -1) {
              y2 = Long.parseLong(ss[1]) + 2;
              year2 = (Long.parseLong(ss[0]) + 2) + "/" + (y2 >= 10 ? ("" + y2) : ("0" + y2));
            } else {
              int i = 0;
              for (String s : lss) {
                if (s.equals(lss[lss.length - 1])) {
                  i++;

                }
              }
              if (i == 2) {
                y2 = Long.parseLong(ss[1]) + 2;
                year2 = (Long.parseLong(ss[0]) + 2) + "/" + (y2 >= 10 ? ("" + y2) : ("0" + y2));
              }
            }

            parseInfo.stepIndex = 2;
          }

        } else if (parseInfo.stepIndex == 2) {
          line = HTMLParseUtil.trim2bank(line).trim();
          if (line.startsWith("Harvested")) {
            if (debug)
              LogService.log(line);
            line = StringUtil.replaceString(line, "1/", "").trim();
            line = StringUtil.replaceString(line, "1 /", "").trim();
            line = StringUtil.replaceString(line, " ,", ",");
            line = StringUtil.replaceString(line, ".", "");

            String[] totalstrs = line.split(" ");
            double t1 = Double.parseDouble(totalstrs[1]);
            double t2 = Double.parseDouble(totalstrs[totalstrs.length - 1]);
            saveGrowAreaYear(year1, reportDate, t1);
            saveGrowAreaYear(year2, reportDate, t2);
            parseInfo.stepIndex = 3;
            todo = true;

          }
        } else if (parseInfo.stepIndex == 3) {
          line = HTMLParseUtil.trim2bank(line).trim();
          if (line.startsWith("Production")) {
            if (debug)
              LogService.log(line);
            line = StringUtil.replaceString(line, "1/", "").trim();
            line = StringUtil.replaceString(line, "1 /", "").trim();
            line = StringUtil.replaceString(line, " ,", ",");
            line = StringUtil.replaceString(line, ".", "");

            String[] totalstrs = line.split(" ");
            double t1 = Double.parseDouble(totalstrs[1]);
            double t2 = Double.parseDouble(totalstrs[totalstrs.length - 1]);
            saveYieldYear(year1, reportDate, t1);
            saveYieldYear(year2, reportDate, t2);
            parseInfo.stepIndex = 4;
            todo = true;
            break;
          }
        }
        lastline = line;
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

  private void saveYieldYear(String year, long reportDate, double value) throws SQLException {

    Date now = new Date();
    Country country = Country.getCountry("USA");
    Commodity commodity = Commodity.getCommodity("棉花");
    YieldYear obj = YieldYearSQL.getObj(year, reportDate, country, 0, commodity, getSource());
    if (obj == null) {
      obj = new YieldYear();
      obj.setCommodity(commodity);
      obj.setCountry(country);
      obj.setReportDate(reportDate);
      obj.setSource(getSource());
      obj.setYear(year);
      obj.setReportStatus(0);
    }
    obj.setValue(value);
    obj.setWeightUnit(WeightUnit.getWeightUnit("千包,480 pounds"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    if (!YieldYearSQL.isSameWithLast(obj)) {
      YieldYearSQL.save(obj);
    }
  }

  private void saveGrowAreaYear(String year, long reportDate, double value) throws SQLException {

    Date now = new Date();
    Country country = Country.getCountry("USA");
    Commodity commodity = Commodity.getCommodity("棉花");
    GrowAreaYear obj = GrowAreaYearSQL.getObj(year, reportDate, country, 0, commodity, getSource());
    if (obj == null) {
      obj = new GrowAreaYear();
      obj.setCommodity(commodity);
      obj.setCountry(country);
      obj.setReportDate(reportDate);
      obj.setSource(getSource());
      obj.setYear(year);
      obj.setReportStatus(0);
    }
    obj.setValue(value);
    obj.setAreaUnit(AreaUnit.getAreaUnit("Thousands_of_Acres"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    if (!GrowAreaYearSQL.isSameWithLast(obj)) {
      GrowAreaYearSQL.save(obj);
    }
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
