package ant.yarn.custom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.custom.country.ImportExportMonth;
import model.entity.custom.country.db.ImportExportMonthSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetINDYarnMonthly implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetINDYarnMonthly mhhf = new GetINDYarnMonthly();
    try {
      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void doAnt() {
    getExport();
  }

  // http://dgciskol.gov.in/data_information.asp
  public void getExport() {
    Date now = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

    long fileDate = Long.parseLong(sdf.format(now));
    String href = "http://dgft.gov.in/exim/2000/cottonyarn.htm";

    File file = AntLogic.getFile(getSource() + "/export", "cottonyarn" + fileDate + ".htm");

    try {
      String content = Util.getContent(href, "windows-1252");
      long lastDay = ImportExportMonthSQL.getFROMCountryLastDay(Commodity.getCommodity("É´Ïß"), Country.getCountry("IND"), getSource());
      if (lastDay <= 0) {
        lastDay = 20100101;
      }
      if (file.exists()) {
        InputStream in = new FileInputStream(file);
        String content1 = FileStreamUtil.getFileContent(in);
        in.close();
        if (content.equals(content1)) {
          praseContent(lastDay, content);
        } else {
          AntLogic.saveFile(getSource() + "/export", "cottonyarn" + fileDate + ".htm", content);
          praseContent(lastDay, content);
        }

      } else {
        AntLogic.saveFile(getSource() + "/export", "cottonyarn" + fileDate + ".htm", content);
        praseContent(lastDay, content);
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void praseContent(long lastDay, String content) throws ParserException, NumberFormatException, ParseException {
    Parser parser = Parser.createParser(content, "utf-8");
    NodeList nl = parser.parse(null);

    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "border", "1" });
    tableattrs.add(new String[] { "class", "MsoNormalTable" });

    Tag table = HTMLParseUtil.getTag(nl, "table", tableattrs);
    int r = 0;
    List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
    for (Tag tr : trs) {
      List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");
      r++;
      if (r == 1) {
        continue;
      }
      String monthStr = tds.get(0).toPlainTextString();
      String value = tds.get(1).toPlainTextString();
      try {

        long reportDate = getMonthDate(monthStr);
        double total = Double.parseDouble(value);
        saveMonthInfo(reportDate, total);
      } catch (Exception e) {
        LogService.trace(e, monthStr + "|" + value);
      }
    }
  }

  private long getMonthDate(String monthStr) throws NumberFormatException, ParseException {

    monthStr = StringUtil.replaceString(monthStr, "May 2", "May'2").trim();
    monthStr = StringUtil.replaceString(monthStr.split("/")[1], "(Provisional)", "").trim();

    monthStr = StringUtil.replaceString(monthStr, "&nbsp;", "").trim();

    monthStr = StringUtil.replaceString(monthStr, " ", "").trim();
    monthStr = StringUtil.replaceString(monthStr, "¡¯", "'").trim();

    String[] ms = monthStr.split("'");

    int m = DateUtil.getMonthByEN(ms[0].trim());
    long y = Long.parseLong(ms[1].trim());
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM");
    String dateSTR = y + "/" + m;
    return Long.parseLong(sdf1.format(sdf2.parse(dateSTR)));

  }

  private void saveMonthInfo(long reportDate, double total) throws SQLException {
    Date now = new Date();
    Country fromCountry = Country.getCountry("IND");
    Country toCountry = Country.getCountry("WHOLE");
    ImportExportMonth impexpDay = ImportExportMonthSQL.getObj(reportDate, toCountry, fromCountry, Commodity.getCommodity("É´Ïß"), getSource());
    if (impexpDay != null) {
      impexpDay.setSource(getSource());
      impexpDay.setUpdatedAt(now);
      impexpDay.setUpdatedBy(AntManger.UPDATEBY);
      impexpDay.setWeightUnit(WeightUnit.getWeightUnit("Ç§¶Ö"));
      impexpDay.setValue(total);
    } else {
      impexpDay = new ImportExportMonth();
      impexpDay.setCommodity(Commodity.getCommodity("É´Ïß"));
      impexpDay.setFromCountry(fromCountry);
      impexpDay.setToCountry(toCountry);
      impexpDay.setReportDate(reportDate);
      impexpDay.setSource(getSource());
      impexpDay.setUpdatedAt(now);
      impexpDay.setUpdatedBy(AntManger.UPDATEBY);
      impexpDay.setWeightUnit(WeightUnit.getWeightUnit("Ç§¶Ö"));
      impexpDay.setValue(total);
    }
    ImportExportMonthSQL.save(impexpDay);
  }

  @Override
  public String getSource() {

    return "dgft.gov.in";
  }
}
