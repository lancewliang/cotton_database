package ant.cotton.ginn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import model.entity.production.country.YieldDay;
import model.entity.production.country.db.YieldDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.file.FileNameUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

//美国的扎花进度，为半月  产量 
public class GetUSDAGinnHalfMonth implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();

    GetUSDAGinnHalfMonth m = new GetUSDAGinnHalfMonth();
    m.doAnt();
  }

  @Override
  public void doAnt() {
    List<String[]> files = getFileList();
    for (String[] fileurl : files) {
      try {
        File f = AntLogic.getFile(getSource() + "/Ginn", fileurl[0]);

        if (!f.exists()) {
          AntLogic.saveFile(getSource() + "/Ginn", fileurl[0], fileurl[1]);

          engine.util.Util.getFile(fileurl[1], f);
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

  private List<String[]> getFileList() {
    List<String[]> flist = new ArrayList<String[]>();
    try {
      String url = "http://usda.mannlib.cornell.edu/MannUsda/viewDocumentInfo.do;jsessionid=3BD2BCD4062709A0BC5E497B48EB036E?documentID=1041";
      String content = engine.util.Util.getHTML(url, "UTF-8");
      long lastDay = YieldDaySQL.getLastDay(Commodity.getCommodity("棉花"), Country.getCountry("USA"), getSource());
      if (lastDay <= 0)
        lastDay = 20101001;

      Parser parser = Parser.createParser(content, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> dirElcs = new ArrayList<String[]>();
      dirElcs.add(new String[] { "class", "dirElement" });
      List<Tag> dirdivs = HTMLParseUtil.getTags(nl, "div", dirElcs);
      for (Tag dir : dirdivs) {
        long year = Long.parseLong(dir.getAttribute("id").substring(1, 5));
        if (year >= lastDay / 10000) {
          List<String[]> fileElementcs = new ArrayList<String[]>();
          fileElementcs.add(new String[] { "class", "fileElement" });
          List<Tag> filedivs = HTMLParseUtil.getTags(dir.getChildren(), "div", fileElementcs);
          for (Tag file : filedivs) {
            String filename = file.getAttribute("id");
            if (FileNameUtil.getExtension(filename).equals("txt")) {
              String date = filename.replaceAll("CottGinn-", "").replaceAll(".txt", "");
              long reportDate = getReportDate(date);
              if (reportDate >= lastDay) {
                Tag atag = HTMLParseUtil.getTag(file.getChildren(), "a");
                flist.add(new String[] { filename, atag.getAttribute("href") });
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

  private long getReportDate(String str) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(str);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf2.format(calendar.getTime()));
  }

  private void praseContent(String content) {
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
          if (line.startsWith("Running Bales Ginned by Crop - States and United States")) {
            parseInfo.stepIndex = 0;
            String dateSTR = line.substring("Running Bales Ginned by Crop - States and United States".length() + 2);

            try {
              reportDate = getDate(dateSTR);
            } catch (ParseException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            System.out.println(reportDate);
          }

        } else

        if (parseInfo.stepIndex == 0) {
          if (line.indexOf("Crop and State") != -1 && line.indexOf("Running bales ginned") != -1) {
            parseInfo.stepIndex = 1;
            parseInfo.Crop_and_State_line_size++;
          }

        } else if (parseInfo.stepIndex == 1) {
          if (line.startsWith("-------------------------------------")) {
            parseInfo.stepIndex = 2;
          } else if (line.indexOf(" : ") != -1) {
            parseInfo.Crop_and_State_line_size++;

          } else if (line.indexOf(" :----------- ") != -1) {
            parseInfo.Crop_and_State_line_size++;

          }

        } else if (parseInfo.stepIndex == 2) {
          if (line.startsWith("All cotton")) {
            parseInfo.stepIndex = 3;
          }

        } else if (parseInfo.stepIndex == 3) {
          if (line.startsWith("United States")) {
            String nline = HTMLParseUtil.trim2bank(line);

            parseInfo.stepIndex = 4;
            String[] dd = nline.split(" ");
            String Yield = dd[6].replaceAll(",", "");
            try {
              YieldDay yieldDay = YieldDaySQL.getObj(reportDate, Country.getCountry("USA"), Commodity.getCommodity("棉花"), getSource());

              if (yieldDay == null) {
                yieldDay = new YieldDay();
                yieldDay.setCommodity(Commodity.getCommodity("棉花"));
                yieldDay.setCountry(Country.getCountry("USA"));
                yieldDay.setReportDate(reportDate);
                yieldDay.setSource(getSource());
              }
              yieldDay.setUpdatedAt(now);
              yieldDay.setUpdatedBy(AntManger.UPDATEBY);
              yieldDay.setWeightUnit(WeightUnit.getWeightUnit("包,480 pounds"));
              yieldDay.setTotal(Double.parseDouble(Yield));

              YieldDaySQL.save(yieldDay);
            } catch (SQLException e) {
              LogService.trace(e, null);
            }
            break;

          }

        } else if (parseInfo.stepIndex == 4) {
          if (line.startsWith("American Pima")) {
            parseInfo.stepIndex = 5;
          }

        } else if (parseInfo.stepIndex == 5) {
          if (line.startsWith("United States")) {

            String nline = HTMLParseUtil.trim2bank(line);
            String[] dd = nline.split(" ");
            String Yield = dd[6];
            System.out.println(Yield);
            parseInfo.stepIndex = 6;
          }

        }

      }

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
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

  private long getDate(String dateSTR) throws ParseException {
    String[] datestr = dateSTR.split(",");
    String[] md = datestr[0].trim().split(" ");

    String year = datestr[1];
    if (year.indexOf("-") != -1) {
      year = datestr[1].split("-")[1].trim();
    }

    String str = DateUtil.getMonthByEN(md[0]) + "/" + md[1] + "/" + year;
    return DateUtil.getYYYYMMDD(str);
  }

  class ParseInfo {
    int stepIndex = -100;
    int Crop_and_State_line_size = 0;

  }

  @Override
  public String getSource() {

    return "USDA";
  }
}
