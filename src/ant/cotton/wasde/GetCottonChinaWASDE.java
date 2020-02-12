package ant.cotton.wasde;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.log.LogService;
import ui.util.PDFBOX;
import ant.cotton.custom.CottonchinaUtil;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCottonChinaWASDE implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCottonChinaWASDE mhhf = new GetCottonChinaWASDE();
    try {
      mhhf.doAnt();
      // PDFBOX parse = new PDFBOX();
      // File f = new
      // File("D:\\lwwork\\ExamKing\\economics3-data\\ant\\cottonchina\\WASDE\\20130311-中国棉花产消存量资源表(2013年3月).pdf");
      // String contentstring = parse.getPDFText(f);
      // System.out.println(contentstring);
      // mhhf.antReportDetailPDF(20130311, contentstring, f);
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
      long maxReportDate = WorldSupplyDemandMonthlyHistorySQL.getLastDay(Commodity.getCommodity("棉花"), getSource());
      if (maxReportDate <= 0)
        maxReportDate = 20000101;
      doListAnt(1, maxReportDate, SessionId);

      File f1 = AntLogic.getFile("cottonchina/WASDE", "");

      if (f1.exists()) {
        for (File file : f1.listFiles()) {
          try {
            if (file.isDirectory())
              continue;
            String name = file.getName();
            LogService.log(name);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
            long reportDate = Long.parseLong(sdf2.format(sdf2.parse(name.split("-")[0])));
            if (reportDate >= maxReportDate) {
              PDFBOX parse = new PDFBOX();
              String contentstring = parse.getPDFText(file);
              antReportDetailPDF(reportDate, contentstring, file);
            }
          } catch (Exception e) {
            LogService.trace(e, null);
          }
        }
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void doListAnt(int page, long lastDay, String SessionId) {
    try {
      //

      int offset = (page - 1) * 25;
      String url = "http://www.cottonchina.org/news/newsser.php?relnews=%C6%BD%BA%E2%B1%ED&offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "target", "_blank" });
      tableattrs.add(new String[] { "class", "a1" });
      List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
      for (Tag tg : tags) {
        String linktitle = tg.toPlainTextString();
        String href = tg.getAttribute("href");
        if (linktitle.startsWith("中国棉花产消存量资源表")) {
          long reportDate = getMonthDay(href);

          if (isUpdate(reportDate, lastDay)) {
            String uuu = "http://www.cottonchina.org/news/" + href;
            antReportDetail(reportDate, uuu, linktitle, SessionId);
          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void antReportDetail(long reportDate, String url, String linktitle, String SessionId) throws Exception {
    String html = CottonchinaUtil.getHTML(url, SessionId);
    Parser parser = Parser.createParser(html, "GB2312");
    NodeList nl = parser.parse(null);
    List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
    for (Tag tg : tags) {
      String linktitle22 = tg.toPlainTextString();
      String href = tg.getAttribute("href");
      if (linktitle22.indexOf("中国棉花产消存量资源表") != -1 && href.endsWith("pdf")) {

        antReportDetailPDF(reportDate, href, linktitle, SessionId);

      }
    }

  }

  private void antReportDetailPDF(long reportDate, String url, String linktitle, String SessionId) throws Exception {
    File f = AntLogic.getFile("cottonchina/WASDE", reportDate + "-" + linktitle + ".pdf");
    if (!f.exists()) {
      Util.getFile(url, f);
    }

  }

  class ParseInfo {
    int stepIndex = -100;
    int Crop_and_State_line_size = 0;

  }

  private void antReportDetailPDF(long reportDate, String content, File f) throws Exception {
    if (None.isBlank(content))
      return;
    BufferedReader br = null;
    String line = "";
    ParseInfo parseInfo = new ParseInfo();
    try {

      Date now = new Date();
      br = new BufferedReader(new StringReader(content));
      String[] years = null;
      List<WorldSupplyDemandMonthlyHistory> objs = new ArrayList<WorldSupplyDemandMonthlyHistory>();
      while ((line = br.readLine()) != null) {
        line = line.trim();
        // take Crop and State
        if (parseInfo.stepIndex < 0) {
          if (line.startsWith("中国棉花产消存量资源表")) {
            parseInfo.stepIndex = 0;

          }

        } else if (parseInfo.stepIndex == 0) {
          if (line.startsWith("单位")) {
            parseInfo.stepIndex = 1;
            line = br.readLine().trim();
            if (None.isBlank(line)) {
              line = br.readLine().trim();
              if (None.isBlank(line)) {
                line = br.readLine().trim();
                if (None.isBlank(line)) {
                  line = br.readLine().trim();
                }
              }
            }
            years = line.split(" ");
            for (String year : years) {
              WorldSupplyDemandMonthlyHistory record = WorldSupplyDemandMonthlyHistorySQL.getObj(Country.getCountry("CHN"), year, reportDate, 0, Commodity.getCommodity("棉花"), getSource());
              if (record == null) {
                record = new WorldSupplyDemandMonthlyHistory();
                record.setReportDate(reportDate);
                record.setCountry(Country.getCountry("CHN"));
                record.setYear(year);
                record.setCommodity(Commodity.getCommodity("棉花"));
                record.setSource(getSource());

              }

              objs.add(record);
            }
          }

        } else if (parseInfo.stepIndex == 1) {
          if (line.startsWith("期初库存")) {
            String[] strs = line.substring("期初库存".length()).trim().split(" ");
            int i = 0;
            for (WorldSupplyDemandMonthlyHistory record : objs) {
              record.setBeginStock(Double.parseDouble(strs[i]));
              i++;
            }
            parseInfo.stepIndex = 2;
          }

        } else if (parseInfo.stepIndex == 2) {
          if (line.startsWith("产量")) {
            String[] strs = line.substring("产量".length()).trim().split(" ");
            int i = 0;
            for (WorldSupplyDemandMonthlyHistory record : objs) {
              record.setProduction(Double.parseDouble(strs[i]));
              i++;
            }
            parseInfo.stepIndex = 3;
          }

        } else if (parseInfo.stepIndex == 3) {

          if (line.startsWith("进口量")) {
            String[] strs = line.substring("进口量".length()).trim().split(" ");
            int i = 0;
            for (WorldSupplyDemandMonthlyHistory record : objs) {
              record.setImports(Double.parseDouble(strs[i]));
              i++;
            }
            parseInfo.stepIndex = 4;
          }
        } else if (parseInfo.stepIndex == 4) {
          if (line.startsWith("消费量")) {
            String[] strs = line.substring("消费量".length()).trim().split(" ");
            int i = 0;
            for (WorldSupplyDemandMonthlyHistory record : objs) {
              record.setUses(Double.parseDouble(strs[i]));
              i++;
            }
            parseInfo.stepIndex = 5;
          }
        } else if (parseInfo.stepIndex == 5) {
          if (line.startsWith("出口量")) {
            String[] strs = line.substring("出口量".length()).trim().split(" ");
            int i = 0;
            for (WorldSupplyDemandMonthlyHistory record : objs) {
              record.setExports(Double.parseDouble(strs[i]));
              i++;
            }
            parseInfo.stepIndex = 6;
          }

        } else if (parseInfo.stepIndex == 6) {
          if (line.startsWith("期末库存")) {
            String[] strs = line.substring("期末库存".length()).trim().split(" ");
            int i = 0;
            for (WorldSupplyDemandMonthlyHistory record : objs) {
              record.setEndStock(Double.parseDouble(strs[i]));
              i++;
            }
            parseInfo.stepIndex = 7;
          }

        }

      }
      if (!None.isEmpty(objs)) {
        for (WorldSupplyDemandMonthlyHistory record : objs) {
          record.setUpdatedAt(now);
          record.setUpdatedBy(AntManger.UPDATEBY);
          record.setWeightUnit(WeightUnit.getWeightUnit("万吨"));
          if (!WorldSupplyDemandMonthlyHistorySQL.isSameWithLast(record)) {
            WorldSupplyDemandMonthlyHistorySQL.save(record);
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

  public long getMonthDay(String href) throws NumberFormatException, ParseException {
    int i = href.indexOf("newstime=");
    String datestr = href.substring(i + "newstime=".length());
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    return Long.parseLong(sdf2.format(sdf1.parse(datestr)));

  }

  private boolean isUpdate(long reportDate, long lastDay) {
    return reportDate >= lastDay;
  }

  @Override
  public String getSource() {

    return "cottonchina";
  }
}
