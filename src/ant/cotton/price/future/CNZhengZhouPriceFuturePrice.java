package ant.cotton.price.future;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Bourse;
import model.constant.Commodity;
import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.WeightUnit;
import model.entity.price.country.FuturePriceDay;
import model.entity.price.country.db.FuturePriceDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class CNZhengZhouPriceFuturePrice implements DayAnt {
  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    CNZhengZhouPriceFuturePrice exp = new CNZhengZhouPriceFuturePrice();
    exp.doAnt();
  }

  @Override
  public void doAnt() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
      cal.add(Calendar.DAY_OF_MONTH, 1);
      int currentDay = Integer.parseInt(sdf2.format(cal.getTime()));

      int lastDay = FuturePriceDaySQL.getLastDay(getSource());
      if (lastDay <= 0)
        lastDay = 20100101;
      boolean dotask = false;
      if (currentDay >= lastDay) {
        Calendar lastDayCalendar = getDate(lastDay);
        Calendar currentDayCalendar = getDate(currentDay);
        currentDayCalendar.add(Calendar.DAY_OF_MONTH, 1);

        int reportDate111 = Integer.parseInt(sdf2.format(lastDayCalendar.getTime()));
        int reportDate222 = Integer.parseInt(sdf2.format(currentDayCalendar.getTime()));
        getDayPage(lastDayCalendar.get(Calendar.YEAR), Integer.parseInt(sdf2.format(lastDayCalendar.getTime())), true);
        while (!lastDayCalendar.after(currentDayCalendar)) {
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
    String url = "http://www.czce.com.cn/portal/exchange/" + year + "/datadaily/" + reportDate + "CF.htm";
    LogService.msg(url);

    //
    File f = AntLogic.getFile(getSource() + "/cotton/" + year, "futureprice-" + reportDate + ".htm");
    String html = null;
    if (!f.exists() || get) {
      html = Util.getHTML(url, "GBK");
      if (None.isNonBlank(html) && html.indexOf("每日行情表") != -1) {
        if (html.indexOf("当日无行情数据") == -1) {

          if (f.exists()) {
            InputStream in = new FileInputStream(f);
            String html2 = FileStreamUtil.getFileContent(in);
            in.close();
            if (!html2.equals(html))
              AntLogic.saveFile(getSource() + "/cotton/" + year, "futureprice-" + reportDate + ".htm", html);
          } else {
            AntLogic.saveFile(getSource() + "/cotton/" + year, "futureprice-" + reportDate + ".htm", html);
          }
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
    Parser parser = Parser.createParser(content, "GB2312");
    NodeList nl = parser.parse(null);

    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "class", "table" });
    tableattrs.add(new String[] { "id", "senfe" });

    Tag table = HTMLParseUtil.getTag(nl, "table", tableattrs);

    List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
    int r = 0;
    for (Tag tr : trs) {
      try {
        r++;
        if (r == 1) {
          continue;
        }
        List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");
        String contract = tds.get(0).toPlainTextString();
        if (!contract.startsWith("CF")) {
          continue;
        }
        FuturePriceDay obj = FuturePriceDaySQL.getObj(reportDate, Country.getCountry("CHN"), contract, Commodity.getCommodity("棉花"), getSource());
        if (obj == null) {
          obj = new FuturePriceDay();
          obj.setReportDate(reportDate);
          obj.setBourse(Bourse.getBourse("郑商所"));
          obj.setCommodity(Commodity.getCommodity("棉花"));
          obj.setCountry(Country.getCountry("CHN"));
          obj.setContract(contract);
        }
        String openingValueSTR = tds.get(2).toPlainTextString();
        String topValueSTR = tds.get(3).toPlainTextString();
        String minimumValueSTR = tds.get(4).toPlainTextString();
        String closingValueSTR = tds.get(5).toPlainTextString();
        String volumesSTR = tds.get(9).toPlainTextString();
        obj.setOpeningValue(getIntI(openingValueSTR));
        obj.setTopValue(getIntI(topValueSTR));
        obj.setMinimumValue(getIntI(minimumValueSTR));
        obj.setClosingValue(getIntI(closingValueSTR));
        obj.setVolumes(getIntT(volumesSTR));
        obj.setWeightUnit(WeightUnit.getWeightUnit("吨"));
        obj.setPriceUnit(PriceUnit.getPriceUnit("元"));
        obj.setSource(getSource());
        obj.setUpdatedAt(now);
        obj.setUpdatedBy(AntManger.UPDATEBY);
        FuturePriceDaySQL.save(obj);
      } catch (Exception e) {
        LogService.trace(e, null);
      }
    }
  }

  private int getIntT(String str) {
    try {
      String ss = str.trim().replaceAll("&#176;", "").replaceAll(",", "");
      ss = ss.replaceAll("&nbsp;", "");
      return Integer.parseInt(ss);
    } catch (Exception e) {
      return DBUtil.NULLINT;
    }
  }

  private double getIntI(String str) {
    try {
      String ss = str.trim().replaceAll("&#176;", "").replaceAll(",", "");
      ss = ss.replaceAll("&nbsp;", "");
      return Double.parseDouble(ss);
    } catch (Exception e) {
      return DBUtil.NULLFLOAT;
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

  @Override
  public String getSource() {

    return "CZCE";
  }

}
