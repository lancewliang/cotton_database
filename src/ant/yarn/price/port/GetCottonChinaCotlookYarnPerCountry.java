package ant.yarn.price.port;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.entity.price.country.db.CountryPriceDaySQL;
import model.entity.price.country.db.PortPriceDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.log.LogService;
import ant.cotton.custom.CottonchinaUtil;
import ant.server.AntLogic;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCottonChinaCotlookYarnPerCountry implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCottonChinaCotlookYarnPerCountry parse = new GetCottonChinaCotlookYarnPerCountry();
    try {
      String SessionId = CottonchinaUtil.getSessionId();
      int lastDay = PortPriceDaySQL.getLastDay(Commodity.getCommodity("É´Ïß"), parse.getSource());
      if (lastDay <= 0)
        lastDay = 20080101;
      for (int i = 1; i < 7; i++) {
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
      int lastDay = PortPriceDaySQL.getLastDay(Commodity.getCommodity("É´Ïß"), getSource());
      if (lastDay <= 0)
        lastDay = 20080101;
      for (int i = 1; i < 2; i++) {
        doAntIndex(lastDay, i, SessionId);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  public void doAntIndex(int lastDay, int pageIndex, String SessionId) {
    try {
      int offset = pageIndex == 0 ? 0 : (pageIndex - 1) * 25;
      String url = "http://www.cottonchina.org/news/newsser.php?relnews=%C3%DE%C9%B4%D6%B8%CA%FD&px=" + pageIndex + "&offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "target", "_blank" });
      tableattrs.add(new String[] { "class", "a2" });
      List<Tag> lis = HTMLParseUtil.getTags(nl, "a", tableattrs);
      int row = 0;
      for (Tag tr : lis) {
        String linktitle = tr.toPlainTextString();
        if (linktitle.indexOf("Cotlook") != -1) {
          String href = tr.getAttribute("href");
          String[] params = href.split("=");
          long date = getDateSTR(params[params.length - 1]);
          if (date >= lastDay) {
            String surl = "http://www.cottonchina.org/news/" + href;

            praseContent(lastDay, date, SessionId, surl);
          }
        }
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void praseContent(int lastDay, long reportDate, String SessionId, String url) throws ParserException, IOException {
    File file = AntLogic.getFile(getSource() + "/YarnPerCountry", reportDate + "-YarnPerCountry.jpg");
    if (!file.exists()) {
      String content = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(content, "GB2312");
      NodeList nl = parser.parse(null);
      boolean ret = false;
      List<Tag> lis = HTMLParseUtil.getTags(nl, "img");
      List<String> lll = new ArrayList<String>();
      for (Tag tr : lis) {
        String href = tr.getAttribute("src");
        if (None.isNonBlank(href) && href.indexOf("picture") != -1 && (href.endsWith("jpg") || href.endsWith("JPG"))
            && (href.endsWith("cotlook1.jpg") || href.endsWith("cotlook.jpg") || href.indexOf("cly") != -1 || href.endsWith("cot1.jpg") || href.indexOf("clk") != -1 || href.indexOf("cy") != -1 || href.indexOf("ms") != -1 || href.indexOf("cotlk") != -1 || href.indexOf("ctlok") != -1 || (href.indexOf("cotlook") != -1 && href.endsWith("1.jpg")))) {

          if (!href.startsWith("http")) {
            if (href.startsWith("..")) {
              href = "http://www.cottonchina.org" + href.substring(2);
            }
         
          }
          lll.add(href);
        }
      }
      if (lll.size() == 1) {
        Util.getFile(lll.get(0), file);
        ret = true;

      } else if (lll.size() == 2) {
        Util.getFile(lll.get(0), file);
        ret = true;

      } else if (lll.size() == 3) {
        Util.getFile(lll.get(1), file);
        ret = true;

      }

      if (!ret) {
        LogService.err("not parse" + url);
      }
    }
  }

  private long getDateSTR(String daystr) throws ParseException {

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date date = sdf2.parse(daystr);
    return Long.parseLong(sdf.format(date));
  }

  @Override
  public String getSource() {
    return "Cotlook";
  }
}
