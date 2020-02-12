package ant.exrate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.ReportStatus;
import model.entity.price.country.ExchangeRateDay;
import model.entity.price.country.db.CountryPriceDaySQL;
import model.entity.price.country.db.ExchangeRateDaySQL;
import model.entity.price.country.db.PortPriceDaySQL;
import model.entity.production.country.YieldYear;
import model.entity.production.country.db.YieldYearSQL;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.chemicalfiber.price.GetCottonChinaCNChemicalfiberPrice;
import ant.cotton.custom.CottonchinaUtil;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class MonthlyExchangeRate implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    MonthlyExchangeRate parse = new MonthlyExchangeRate();
    try {
      String SessionId = CottonchinaUtil.getSessionId();
      int lastDay = ExchangeRateDaySQL.getLastDay(parse.getSource());
      if (lastDay <= 0)
        lastDay = 200801;
      for (int i = 1; i < 4; i++) {
        parse.doAntIndex(lastDay, i, SessionId);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  @Override
  public void doAnt() {

    try {
      String SessionId = CottonchinaUtil.getSessionId();
      int lastDay = CountryPriceDaySQL.getLastDay(Commodity.getCommodity("涤纶短纤"), getSource());
      if (lastDay <= 0)
        lastDay = 200801;
      for (int i = 1; i < 2; i++) {
        doAntIndex(lastDay, i, SessionId);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  public void doAntIndex(int lastDay, int pageIndex, String SessionId) {
    try {
      int offset = (pageIndex - 1) * 25;
      String url = "http://www.cottonchina.org/news/newsser.php?newskey=%BD%F8%B3%F6%BF%DA%B9%D8%CB%B0%BB%E3%C2%CA&sertype=title&newstype=&imageField322.x=20&imageField322.y=8&offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
      for (Tag tg : tags) {
        String linktitle = tg.toPlainTextString();
        if (linktitle.indexOf("进出口关税汇率") != -1) {
          long reportDate = getMonthDay(linktitle);
          if (reportDate >= lastDay) {

            String href = tg.getAttribute("href");
            href = "http://www.cottonchina.org/news/" + href;
            parseDate(reportDate, href, linktitle, SessionId);
          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void parseDate(long reportDate, String url, String linktitle, String SessionId) throws Exception {
    File f = AntLogic.getFile(getSource() + "/MonthlyExchangeRate", linktitle + ".html");
    if (!f.exists()) {
      String html = CottonchinaUtil.getHTML(url, SessionId);
      AntLogic.saveFile(getSource() + "/MonthlyExchangeRate", linktitle + ".html", html);
    }
    if (f.exists()) {
      InputStream in = new FileInputStream(f);
      String html = FileStreamUtil.getFileContent(in);
      in.close();
      praseContent(reportDate, html, f);
    }
  }

  private void praseContent(long reportDate, String content, File f) {
    try {
      content=   content.replaceAll("\n", "");
      double rate = HTMLParseUtil.getDoubleStringByRegex(content, "月份进出口货物关税汇率将按1美元＝[0-9]+(.[0-9]{1,4})?元人民币计算", "月份进出口货物关税汇率将按1美元＝".length(), "元人民币计算".length());

      if (rate <= 0) {
        LogService.err("parse1 totalarea  not found:" + f.getAbsolutePath());
      } else {
        saveObj(reportDate, rate);
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void saveObj(long reportDate, double value) throws SQLException {

    Date now = new Date();
    ExchangeRateDay obj = ExchangeRateDaySQL.getObj(reportDate, "USD", "CNY", getSource());
    if (obj == null) {
      obj = new ExchangeRateDay();
      obj.setReportDate(reportDate);
      obj.setSource(getSource());
      obj.setFromCurreny("USD");
      obj.setToCurreny("CNY");
    }
    obj.setValue(value);
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    ExchangeRateDaySQL.save(obj);
  }

  private long getMonthDay(String linktitle) throws Exception {
    String regex = "[0-9]{4}年[0-1]{0,1}[0-9]{1}月";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(linktitle);
    if (m.find()) {
      String sttt = m.group();
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
      return Long.parseLong(sdf2.format(sdf1.parse(sttt)));
    } else {
      throw new Exception();
    }

  }

  @Override
  public String getSource() {

    return "customs.gov.cn";
  }
}
