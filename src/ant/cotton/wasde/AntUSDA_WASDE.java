package ant.cotton.wasde;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import model.constant.Commodity;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.w3c.dom.Document;

import tcc.utils.log.LogService;
import tcc.utils.xml.dom.DOMUtil;
import ant.cotton.custom.CottonchinaUtil;
import ant.cotton.stock.GetCNStockMonthly;
import ant.server.AntLogic;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class AntUSDA_WASDE implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    AntUSDA_WASDE mhhf = new AntUSDA_WASDE();
    try {

      mhhf.doAnt();

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public String getSource() {
    return "USDA";
  }

  @Override
  public void doAnt() {
    parselist();
  }

  private void parselist() {
    String url = "http://usda.mannlib.cornell.edu/MannUsda/viewDocumentInfo.do?documentID=1194";
    try {
      String content = Util.getContent(url, "utf-8");

      Parser parser = Parser.createParser(content, "utf-8");
      List<String[]> tableattrs = new ArrayList<String[]>();

      tableattrs.add(new String[] { "class", "fileElement" });
      org.htmlparser.util.NodeList nl = parser.parse(null);
      List<Tag> divs = HTMLParseUtil.getTags(nl, "div", tableattrs);

      long maxReportDate = WorldSupplyDemandMonthlyHistorySQL.getLastDay(Commodity.getCommodity("ÃÞ»¨"), getSource());
      if (maxReportDate <= 0)
        maxReportDate = 20100101;
      for (Tag t : divs) {
        String id = t.getAttribute("id");
        if (id.endsWith("xml")) {
          if (id.startsWith("c-was")) {
            continue;
          }
          try {
            if (id.startsWith("ORIG")) {
              id = id.replaceAll("ORIG", "");
            }
            String dstr = id.substring(6, id.length() - 4);
            SimpleDateFormat sdf1 = new SimpleDateFormat("MM-dd-yyyy");
            Date dd = sdf1.parse(dstr);

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
            String _ReportDate = sdf2.format(dd);

            // if (Long.parseLong(_ReportDate) > maxReportDate) {
            NodeList childern = t.getChildren();
            for (int i = 0; i < childern.size(); i++) {
              Node child = childern.elementAt(i);
              if (child instanceof LinkTag) {
                LinkTag lch = (LinkTag) child;
                String href = lch.getAttribute("href");
                parseFileByHistory(href, dd, id);
              }
            }
            // }
          } catch (Exception e) {
            LogService.trace(e, id);
          }

        }
      }
      parseFiles(maxReportDate);

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void parseFileByHistory(String href, Date date, String filename) {
    try {
      GregorianCalendar calcurrent = new GregorianCalendar();
      calcurrent.setTime(date);

      File file = AntLogic.getFile(getSource() + "/WASDE/" + calcurrent.get(GregorianCalendar.YEAR), filename);
      if (!file.exists()) {
        Util.getFile(href, file);

      }
      if (file.exists()) {
        Document doc = DOMUtil.file2Doc(file);

      }
    } catch (Exception e) {
      LogService.trace(e, href);
    }
  }

  private File[] getFolder(long maxReportDate) throws ParseException {
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    GregorianCalendar calcurrent = new GregorianCalendar();
    calcurrent.setTime(sdf2.parse("" + maxReportDate));
    File folder1 = AntLogic.getFile(getSource() + "/WASDE", "" + calcurrent.get(GregorianCalendar.YEAR));
    File folder2 = AntLogic.getFile(getSource() + "/WASDE", "" + (calcurrent.get(GregorianCalendar.YEAR) + 1));
    return new File[] { folder1, folder2 };
  }

  private void parseFiles(long maxReportDate) throws ParseException {

    ParseXMLYearReport parse1 = new ParseXMLYearReport();
    ParseXMLMotherProjReport parse2 = new ParseXMLMotherProjReport();
    File[] folders = getFolder(maxReportDate);
    for (File folder : folders) {
      if (folder.exists()) {
        for (File file : folder.listFiles()) {
          if (file.isDirectory())
            continue;
          try {
            long reportDate1 = Long.parseLong(parse1.getReportDate(file.getName()));
            long reportDate2 = Long.parseLong(parse2.getReportDate(file.getName()));
            if (reportDate1 >= maxReportDate) {
              Document doc = DOMUtil.file2Doc(file);
              parse1.doParseXML(doc, reportDate1);
              String monthShortName2 = parse2.getMonthShortName(file.getName());
              parse2.doParseXML(doc, reportDate2, monthShortName2);
            }
          } catch (Exception e) {
            LogService.trace(e, null);
          }
        }
      }
    }
  }
}
