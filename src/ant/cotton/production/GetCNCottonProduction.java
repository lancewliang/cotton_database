package ant.cotton.production;

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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.constant.AreaUnit;
import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.ReportStatus;
import model.entity.production.country.GrowAreaYear;
import model.entity.production.country.YieldYear;
import model.entity.production.country.db.GrowAreaYearSQL;
import model.entity.production.country.db.YieldDaySQL;
import model.entity.production.country.db.YieldYearSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ui.util.PDFBOX;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetCNCottonProduction implements DayAnt {
  String gurl = "http://www.cncotton.com/was5/web/search?channelid=200951&searchword=%E7%9B%91%E6%B5%8B%E7%B3%BB%E7%BB%9F%E8%B0%83%E6%9F%A5&perpage=&templet=&orderby=-docreltime&token=&searchscope=doctitle";

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCNCottonProduction mhhf = new GetCNCottonProduction();
    try {
      String ssss = "ffdsfsdf将2013年全国棉花产量调增至699.7万吨asdfsadf";
      // Pattern p =
      // Pattern.compile("[0-9]{4}年[^/t/n/x0B/f/r]*产量[^/t/n/x0B/f/r]*至[0-9]+(.[0-9]{1,2})?万吨");
      // Matcher m = p.matcher(ssss);
      // String datastr = null;
      // if (m.find()) {
      // datastr = m.group();
      // if (None.isNonBlank(datastr)) {
      // System.out.println(datastr);
      // }
      // }

      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void doAnt() {
    try {
      String content = engine.util.Util.getContent(gurl, "UTF-8");
      Parser parser = Parser.createParser(content, "UTF-8");
      NodeList nl = parser.parse(null);

      List<String[]> attrs = new ArrayList<String[]>();
      attrs.add(new String[] { "target", "_blank" });
      attrs.add(new String[] { "class", "list1" });
      Country country = Country.getCountry("CHN");
      Commodity commodity = Commodity.getCommodity("棉花");

      long lastDay = YieldDaySQL.getLastDay(commodity, country, getSource());
      long lastDay2 = GrowAreaYearSQL.getLastDay(commodity, country, getSource());
      if (lastDay2 < lastDay) {
        lastDay = lastDay2;
      }
      if (lastDay <= 0) {
        lastDay = 20100101;
      }

      List<Tag> ass = HTMLParseUtil.getTags(nl, "a", attrs);
      for (Tag a : ass) {
        String title = a.toPlainTextString();
        String href = a.getAttribute("href");
        if (title.indexOf("意向种植面积") != -1 || title.indexOf("产量调查") != -1 || title.indexOf("意向植棉面积") != -1 || title.indexOf("实播种植面积") != -1 || title.indexOf("全国棉花实播面积") != -1 || title.indexOf("长势调查") != -1) {
          String surl = href;
          parseURL(lastDay, surl, title);
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void parseURL(long lastDay, String url, String title) {
    try {
      String content = engine.util.Util.getContent(url, "UTF-8");
      if (None.isBlank(content)) {
        LogService.err("GetCNCottonProduction:" + title + "|" + url);
        return;
      }
      Parser parser = Parser.createParser(content, "UTF-8");
      NodeList nl = parser.parse(null);

      String xurl = url.substring(0, url.lastIndexOf('/') + 1);

      List<String[]> attrs1 = new ArrayList<String[]>();
      attrs1.add(new String[] { "class", "fr" });
      String plainText1 = HTMLParseUtil.getTag(nl, "div", attrs1).toPlainTextString().trim();
      plainText1 = plainText1.trim().split(" ")[0].replaceAll("\n", "");

      List<String[]> attrs = new ArrayList<String[]>();
      attrs.add(new String[] { "class", "TRS_PreAppend" });
      NodeList xnl = HTMLParseUtil.getNodeList(nl, "div", attrs);
      List<Tag> ass = HTMLParseUtil.getTags(xnl, "a");
      for (Tag a : ass) {

        String href = a.getAttribute("href");
        if (None.isNonBlank(href) && href.endsWith("pdf")) {
          // http://www.cncotton.com/sy_59/gnmh_1388/rdxw/201604/W020160411335710813227.pdf
          // http://www.cncotton.com/sy_59/gnmh_1388/rdxw/201606/W020160615588821772025.pdf
          // http://www.cncotton.com/sy_59/gnmh_1388/rdxw/201604/W020160615588821772025.pdf

          href = xurl + href.substring(2);
          File f = AntLogic.getFile(getSource() + "/production/", plainText1 + "-" + title + ".pdf");
          if (!f.exists()) {
            engine.util.Util.getFile(href, f);
          }

          if (f.exists()) {
            long reportDate = getReportDate(f);
            if (reportDate < lastDay) {
              return;
            }

            String filename = f.getName();

            PDFBOX parse = new PDFBOX();
            String contentstring = parse.getPDFText(f);
            if (contentstring.indexOf("???????????") != -1) {
              filename = filename.replaceFirst("pdf", "txt");
              File f1 = new File(f.getParentFile(), filename);
              InputStream in = new FileInputStream(f1);
              contentstring = FileStreamUtil.getFileContent(in, "UTF-8");
              in.close();
            }
            String oldpdfcontent = contentstring;
            contentstring = contentstring.replaceAll("\r\n", "");
            contentstring = contentstring.replaceAll(" ", "");
            if (filename.indexOf("产量调查") != -1) {
              parse1(f, contentstring);
            } else if (filename.indexOf("意向") != -1) {
              parse2(f, contentstring);
            } else if (filename.indexOf("实播") != -1) {
              parse3(f, contentstring);
            } else if (filename.indexOf("长势调查") != -1) {
              parse4(f, contentstring);
            } else {
              LogService.err("contentstring not found:" + filename);

            }
            LogService.msg(filename);
            if ( oldpdfcontent.indexOf("调查表") != -1) {
              BufferedReader br = null;
              String line = "";

              try {

                Date now = new Date();
                br = new BufferedReader(new StringReader(oldpdfcontent));

                while ((line = br.readLine()) != null) {
                  line = HTMLParseUtil.trim2bank(line.trim());
                  if (line.startsWith("全 国")) {
                    String[] ll = line.split(" ");
                    double totalyield = 0;
                    try{
                    if (ll.length >7) {
                      totalyield = Double.parseDouble(ll[6]);
                    } else if (ll.length == 7) {
                      totalyield = Double.parseDouble(ll[5]);
                    } else if (ll.length ==6) {
                      totalyield = Double.parseDouble(ll[5]);
                    }}catch(Exception e){
                      e.printStackTrace();
                    }
                    String year = getYear(f, content);
                    if (totalyield < 0) {
                      LogService.err("parse1   totalyield not found:" + f.getAbsolutePath());
                    } else {
                      saveYieldYear(year, reportDate, totalyield, ReportStatus.EST);
                    }
                    break;
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

          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  // 产量调查
  private void parse1(File f, String content) {
    try {
      long reportDate = getReportDate(f);
      String year = getYear(f, content);
      double totalarea = HTMLParseUtil.getDoubleStringByRegex(content, "面积[0-9]+(.[0-9]{1,2})?万亩", "面积".length(), "万亩".length());
      if (totalarea <= 0) {
        totalarea = HTMLParseUtil.getDoubleStringByRegex(content, "种植面积为[0-9]+(.[0-9]{1,2})?万亩", "种植面积为".length(), "万亩".length());
      }
      double totalyield = HTMLParseUtil.getDoubleStringByRegex(content, "总产量[0-9]+(.[0-9]{1,2})?万吨", "总产量".length(), "万吨".length());
      if (totalyield <= 0) {
        totalyield = HTMLParseUtil.getDoubleStringByRegex(content, "总产量预计为[0-9]+(.[0-9]{1,2})?万吨", "总产量预计为".length(), "万吨".length());
      }
      if (totalarea <= 0) {
        LogService.err("parse1 totalarea  not found:" + f.getAbsolutePath());
      } else {
        saveGrowAreaYear(year, reportDate, totalarea, ReportStatus.EST);
      }

      if (totalyield < 0) {
        LogService.err("parse1   totalyield not found:" + f.getAbsolutePath());
      } else {
        saveYieldYear(year, reportDate, totalyield, ReportStatus.EST);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  // 意向种植面积调查
  private void parse2(File f, String content) {
    try {
      long reportDate = getReportDate(f);
      String year = getYear(f, content);
      double totalarea = HTMLParseUtil.getDoubleStringByRegex(content, "意向种植面积[0-9]+(.[0-9]{1,2})?万亩", "意向种植面积".length(), "万亩".length());
      if (totalarea <= 0) {
        LogService.err("parse2 totalarea not found:" + f.getAbsolutePath());
      } else {
        saveGrowAreaYear(year, reportDate, totalarea, ReportStatus.HOPE);
      }
      Pattern p = Pattern.compile("[0-9]{4}年[^/t/n/x0B/f/r]*产量[^/t/n/x0B/f/r]*至[0-9]+(.[0-9]{1,2})?万吨");
      Matcher m = p.matcher(content);
      String datastr = null;
      if (m.find()) {
        datastr = m.group();
        if (None.isNonBlank(datastr)) {
          String year2 = datastr.substring(0, 4);
          int i = datastr.indexOf('至');
          double totalyield = Double.parseDouble(datastr.substring(i + 1, datastr.length() - 2));

          if (totalyield < 0) {
            LogService.err("parse2   totalyield not found:" + f.getAbsolutePath());
          } else {
            saveYieldYear(year2, reportDate, totalyield, ReportStatus.EST);
          }

        }
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  // 实播面积调查

  private void parse3(File f, String content) {
    try {
      long reportDate = getReportDate(f);
      String year = getYear(f, content);
      double totalarea = HTMLParseUtil.getDoubleStringByRegex(content, "实播面积[0-9]+(.[0-9]{1,2})?万亩", "实播面积".length(), "万亩".length());
      if (totalarea <= 0) {
        LogService.err("parse3 totalarea not found:" + f.getAbsolutePath());
      } else {
        saveGrowAreaYear(year, reportDate, totalarea, ReportStatus.EST);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  // 长势调查
  private void parse4(File f, String content) {
    try {
      long reportDate = getReportDate(f);
      String year = getYear(f, content);
      double totalyield = HTMLParseUtil.getDoubleStringByRegex(content, "棉花总产量预计[0-9]+(.[0-9]{1,2})?万吨", "棉花总产量预计".length(), "万吨".length());
      if (totalyield <= 0) {
        totalyield = HTMLParseUtil.getDoubleStringByRegex(content, "棉花总产量预计为[0-9]+(.[0-9]{1,2})?万吨", "棉花总产量预计为".length(), "万吨".length());
      }
      if (totalyield <= 0) {
        totalyield = HTMLParseUtil.getDoubleStringByRegex(content, "总产量[0-9]+(.[0-9]{1,2})?万吨", "总产量".length(), "万吨".length());
      }

      if (totalyield <= 0) {
        LogService.err("parse4 totalyield not found:" + f.getAbsolutePath());
      } else {
        saveYieldYear(year, reportDate, totalyield, ReportStatus.PROJ);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void saveYieldYear(String year, long reportDate, double value, int status) throws SQLException {

    Date now = new Date();
    Country country = Country.getCountry("CHN");
    Commodity commodity = Commodity.getCommodity("棉花");
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
    obj.setWeightUnit(WeightUnit.getWeightUnit("万吨"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    YieldYearSQL.save(obj);
  }

  private void saveGrowAreaYear(String year, long reportDate, double value, int status) throws SQLException {

    Date now = new Date();
    Country country = Country.getCountry("CHN");
    Commodity commodity = Commodity.getCommodity("棉花");
    GrowAreaYear obj = GrowAreaYearSQL.getObj(year, reportDate, country, status, commodity, getSource());
    if (obj == null) {
      obj = new GrowAreaYear();
      obj.setCommodity(commodity);
      obj.setCountry(country);
      obj.setReportDate(reportDate);
      obj.setSource(getSource());
      obj.setYear(year);
      obj.setReportStatus(status);
    }
    obj.setValue(value);
    obj.setAreaUnit(AreaUnit.getAreaUnit("万亩"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    GrowAreaYearSQL.save(obj);
  }

  private long getReportDate(File nl) throws NumberFormatException, ParseException {
    String[] as = nl.getName().split("-");
    String date = as[0] + "-" + as[1] + "-" + as[2];
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    return Long.parseLong(sdf1.format(sdf2.parse(date)));

  }

  private String getYear(File f, String content) throws Exception {

    double year = 0;
    year = HTMLParseUtil.getDoubleStringByRegex(f.getName(), "[0-9]+年度全国棉花", 0, "年度全国棉花".length());
    if (year <= 0) {
      year = HTMLParseUtil.getDoubleStringByRegex(f.getName(), "[0-9]+年全国棉花", 0, "年全国棉花".length());
    }
    if (year <= 0) {
      year = HTMLParseUtil.getDoubleStringByRegex(content, "[0-9]+年度全国棉花", 0, "年度全国棉花".length());
    }
    if (year <= 0) {
      year = HTMLParseUtil.getDoubleStringByRegex(content, "[0-9]+年全国棉花", 0, "年全国棉花".length());
    }
    if (year <= 0) {
      LogService.err("getYear year <= 0 not found:" + f.getAbsolutePath());
      throw new RuntimeException();
    }
    return "" + ((int) year);
  }

  private String getDate(NodeList nl) {
    List<String[]> attrs = new ArrayList<String[]>();
    attrs.add(new String[] { "class", "time" });
    attrs.add(new String[] { "align", "right" });
    return HTMLParseUtil.getTag(nl, "div", attrs).toPlainTextString().trim();
  }

  @Override
  public String getSource() {
    return "cncotton";
  }
}
