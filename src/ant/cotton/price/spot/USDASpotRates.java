package ant.cotton.price.spot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.UnitType;
import model.constant.WeightUnit;
import model.entity.price.country.CountryPriceDay;
import model.entity.price.country.db.CountryPriceDaySQL;

import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class USDASpotRates implements DayAnt {
  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    USDASpotRates exp = new USDASpotRates();
    exp.doAnt();
  }

  @Override
  public void doAnt() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

      int currentDay = Integer.parseInt(sdf2.format(cal.getTime()));

      int lastDay = CountryPriceDaySQL.getLastDay(Commodity.getCommodity("棉花"), getSource());
      if (lastDay <= 0)
        lastDay = 20070101;
      boolean dotask = false;
      if (currentDay >= lastDay) {
        Calendar lastDayCalendar = getDate(lastDay);
        Calendar currentDayCalendar = getDate(currentDay);

        int reportDateLast = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
        int reportDateCurrent = Integer.parseInt(sdf2.format(currentDayCalendar.getTime()));
        while (reportDateCurrent >= reportDateLast) {
          int y = lastDayCalendar.get(Calendar.YEAR);
          int reportDate = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
          getDayPage(y, reportDate, false);
          lastDayCalendar.add(Calendar.DAY_OF_MONTH, 1);
          dotask = true;
          reportDateLast = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
          reportDateCurrent = Integer.parseInt(sdf2.format(currentDayCalendar.getTime()));
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
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM");

    String url = "http://search.ams.usda.gov/mndms/" + sdf2.format(sdf1.parse("" + reportDate)) + "/MP_CN001" + reportDate + ".TXT";
    File f = AntLogic.getFile(getSource() + "/mndms/MP_CN001/" + year, "MP_CN001-" + reportDate + ".htm");
    String html = null;
    if (!f.exists() || get) {

      html = Util.getHTML(url, "iso-8859-1");
      if (None.isNonBlank(html) && html.indexOf("MP_CN001") != -1) {

        if (f.exists()) {
          InputStream in = new FileInputStream(f);
          String html2 = FileStreamUtil.getFileContent(in);
          in.close();
          if (!html2.equals(html))
            AntLogic.saveFile(getSource() + "/mndms/MP_CN001/" + year, "MP_CN001-" + reportDate + ".htm", html);
        } else {
          AntLogic.saveFile(getSource() + "/mndms/MP_CN001/" + year, "MP_CN001-" + reportDate + ".htm", html);
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
    BufferedReader br = null;
    String line = "";
    ParseInfo parseInfo = new ParseInfo();
    try {

      br = new BufferedReader(new StringReader(content));
      int i = 0;
      while ((line = br.readLine()) != null) {

        line = HTMLParseUtil.trim2bank(line).trim();
        // take Crop and State
        if (parseInfo.stepIndex < 0) {
          if (line.startsWith("MARKET")) {

            if (line.indexOf("41-4/3431-3/35") != -1 || line.indexOf("41-4/34 31-3/35") != -1) {
              parseInfo.stepIndex = 0;
            } else {
              throw new ParserException();
            }

          }

        } else if (parseInfo.stepIndex == 0) {

          String[] ss = new String[] { "SOUTHEAST", "NORTH DELTA", "SOUTH DELTA", "EAST TX-OK", "WEST TEXAS", "DESERT SW", "SJ VALLEY" };
          for (String s : ss) {
            if (line.startsWith(s)) {
              String[] linestrs = line.substring(s.length()).trim().split(" ");
              save(s, "41-4/34", reportDate, Double.parseDouble(linestrs[0]));
              save(s, "31-3/35", reportDate, Double.parseDouble(linestrs[1]));

              i++;
              break;
            }
          }
          if (i == 7) {
            parseInfo.stepIndex = 1;
          }
        } else if (parseInfo.stepIndex == 1) {
          if (line.startsWith("AVERAGE")) {
            String[] linestrs = line.split(" ");
            save("COUNTRY", "41-4/34", reportDate, Double.parseDouble(linestrs[1]));
            save("COUNTRY", "31-3/35", reportDate, Double.parseDouble(linestrs[2]));
            return;
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

        }
      }
    }

  }

  private void save(String state, String standard, long reportDate, double price) {
    try {
      Date now = new Date();
      CountryPriceDay obj = CountryPriceDaySQL.getObj(reportDate, Country.getCountry("USA"), state, standard, Commodity.getCommodity("棉花"), getSource());
      if (obj == null) {
        obj = new CountryPriceDay();
        obj.setReportDate(reportDate);
        obj.setCommodity(Commodity.getCommodity("棉花"));
        obj.setCountry(Country.getCountry("USA"));
        UnitType unittype = UnitType.getUnitType("重量单位");
        obj.setUnitType(unittype);
        obj.setStandard(standard);
        obj.setState(state);
      }

      if (price <= 0) {
        return;
      }
      obj.setUnit(WeightUnit.getWeightUnit("pound"));
      obj.setPriceUnit(PriceUnit.getPriceUnit("CENTS"));
      obj.setSource(getSource());
      obj.setUpdatedAt(now);
      obj.setUpdatedBy(AntManger.UPDATEBY);
      obj.setValue(price);
      CountryPriceDaySQL.save(obj);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private Calendar getDate(long day) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse("" + day);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
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
