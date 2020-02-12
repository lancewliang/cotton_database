package ant.cotton.price.spot;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.UnitType;
import model.constant.WeightUnit;
import model.entity.price.country.CountryPriceDay;
import model.entity.price.country.db.CountryPriceDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class IndiaSpotRatesAchive implements DayAnt {
  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    IndiaSpotRatesAchive exp = new IndiaSpotRatesAchive();
    exp.doAnt();
  }

  @Override
  public void doAnt() {
    try {

      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

      // int currentDay = 20120505;
      // int lastDay = 20010205;
      int lastDay = 20060401;
      int currentDay = 20060421;
      boolean dotask = false;
      if (currentDay >= lastDay) {
        Calendar lastDayCalendar = getDate(lastDay);
        Calendar currentDayCalendar = getDate(currentDay);

        int reportDate111 = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
        int reportDate222 = Integer.parseInt(sdf2.format(currentDayCalendar.getTime()));
        while (currentDayCalendar.after(lastDayCalendar)) {
          int y = lastDayCalendar.get(Calendar.YEAR);
          int m = lastDayCalendar.get(Calendar.MONTH) + 1;
          int d = lastDayCalendar.get(Calendar.DAY_OF_MONTH);
          int reportDate = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
          getDayPage(y, reportDate, false);
          lastDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
          dotask = true;
        }

      }

      if (!dotask) {
        LogService.msg(" not dotask :");
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void getDayPage(long year, long reportDate, boolean get) throws Exception {
    String url = "http://www.caionline.in/spot_rates_archives.asp?ddd=" + reportDate;
    File f = AntLogic.getFile(getSource() + "/cotton/" + year, "archives-spotreprice-" + reportDate + ".htm");
    String html = null;
    if (!f.exists() || get) {
      Map<String, String> params = new HashMap<String, String>();
      params.put("options", getDateSTR(reportDate));
      html = Util.postHTML(url, "iso-8859-1", null, params);
      if (None.isNonBlank(html)) {

        if (f.exists()) {
          InputStream in = new FileInputStream(f);
          String html2 = FileStreamUtil.getFileContent(in);
          in.close();
          if (!html2.equals(html))
            AntLogic.saveFile(getSource() + "/cotton/" + year, "archives-spotreprice-" + reportDate + ".htm", html);
        } else {
          AntLogic.saveFile(getSource() + "/cotton/" + year, "archives-spotreprice-" + reportDate + ".htm", html);
        }

      }
      if (f.exists()) {
        praseContent(html, reportDate, f);
      }
    } else {
      InputStream in = new FileInputStream(f);
      String html2 = FileStreamUtil.getFileContent(in);
      in.close();

      praseContent(html2, reportDate, f);
    }
  }

  private void praseContent(String content, long reportDate, File f) throws ParserException, SQLException {
    Date now = new Date();
    Parser parser = Parser.createParser(content, "iso-8859-1");
    NodeList nl = parser.parse(null);

    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "border", "1" });
    tableattrs.add(new String[] { "align", "center" });
    tableattrs.add(new String[] { "cellpadding", "0" });
    tableattrs.add(new String[] { "cellspacing", "1" });

    Tag table = HTMLParseUtil.getTag(nl, "table", tableattrs);

    List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
    if (trs.size() <= 5)
      return;
    int r = 0;
    for (Tag tr : trs) {
      try {
        r++;
        List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");
        if (r == 3) {
          if (!("No.".equals(tds.get(0).toPlainTextString().trim()) && "Grade Standard".equals(tds.get(1).toPlainTextString().trim()) && "Grade".equals(tds.get(2).toPlainTextString().trim()) && "Staple".equals(tds.get(3).toPlainTextString().trim()))) {
            throw new RuntimeException("");
          }

        }
        if (r <= 3) {
          continue;
        }

        if (tds.size() < 5)
          continue;
        // No.
        // Standard
        // Grade
        // Staple
        // Micronaire
        // Strength/ GPT
        // Trade Name
        // Per Quintal
        // Per Candy
        // Per Quintal
        // Per Candy
        String Micronaire = tds.get(4).toPlainTextString().trim();
        Micronaire = Micronaire.replaceAll("�", "-");
        String standard = tds.get(1).toPlainTextString().trim() + "|" + tds.get(3).toPlainTextString().trim() + "|" + Micronaire + "|" + tds.get(5).toPlainTextString().trim();
        standard = standard.replaceAll(" ", "");
        CountryPriceDay obj = CountryPriceDaySQL.getObj(reportDate, Country.getCountry("IND"), "COUNTRY", standard, Commodity.getCommodity("棉花"), getSource());
        if (obj == null) {
          obj = new CountryPriceDay();
          obj.setReportDate(reportDate);
          obj.setCommodity(Commodity.getCommodity("棉花"));
          obj.setCountry(Country.getCountry("IND"));
          UnitType unittype = UnitType.getUnitType("重量单位");
          obj.setUnitType(unittype);
          obj.setStandard(standard);
          obj.setState("COUNTRY");
        }
        String valueSTR = tds.get(9).toPlainTextString().trim();
        String valueSTR2 = tds.get(7).toPlainTextString().trim();
        if (None.isBlank(valueSTR) && None.isBlank(valueSTR2)) {
          continue;
        }
        if ("-".equals(valueSTR) || None.isBlank(valueSTR)) {

          if (None.isNonBlank(valueSTR2)) {
            valueSTR = valueSTR2;
          }
        }
        valueSTR = valueSTR.replaceAll("N", "");
        valueSTR = valueSTR.replaceAll("ne", "");
        valueSTR = StringUtil.replaceString(valueSTR, "*", "");
        valueSTR = StringUtil.replaceString(valueSTR, "(o", "");
        valueSTR = StringUtil.replaceString(valueSTR, "(0", "");
        if (None.isBlank(valueSTR)) {
          LogService.warn("banl" + f.getAbsolutePath());
          continue;
        }

        if (valueSTR.length() <= 3 || "-".equals(valueSTR) || "N.Q.".equals(valueSTR)) {

          continue;
        }
        double price = 0;
        try {
          price = Double.parseDouble(valueSTR);
        } catch (Exception e) {
          LogService.trace(e, f.getAbsolutePath() + "valueSTR" + valueSTR);
        }
        if (price <= 0) {
          continue;
        }
        obj.setUnit(WeightUnit.getWeightUnit("100KG"));
        obj.setPriceUnit(PriceUnit.getPriceUnit("INR"));
        obj.setSource(getSource());
        obj.setUpdatedAt(now);
        obj.setUpdatedBy(AntManger.UPDATEBY);
        obj.setValue(price);
        CountryPriceDaySQL.save(obj);
      } catch (Exception e) {
        LogService.trace(e, null);
      }
    }
  }

  private String getDateSTR(long day) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse("" + day);

    return sdf2.format(date);
  }

  private Calendar getDate(long day) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse("" + day);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  @Override
  public String getSource() {
    return "CottonAssociationOfINDIA";
  }
}
