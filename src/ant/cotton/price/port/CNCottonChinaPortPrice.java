package ant.cotton.price.port;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PortPriceType;
import model.constant.PriceUnit;
import model.constant.WeightUnit;
import model.entity.price.country.PortPriceDay;
import model.entity.price.country.db.PortPriceDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.cotton.custom.CottonchinaUtil;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

/**
 * 
 SJV--美国西部圣约金流域；CA--美国加利弗尼亚和亚利桑纳地区；EMOT--美国孟菲斯和德克萨斯地区；GM--1级；SM--2级；M--3级；SLM
 * --4级。1.1/16'--27毫米；1.3/32'--28毫米；1.1/8'--29毫米。
 * (注：表中报价为CNF中国主要港口报价，包括成本和运费，不含保险
 * ；完税价包含关税、增值税和港口费用。表中报价为外商平均报价，仅供参考，具体询盘请联系外棉供应商。）中国棉花信息网
 * 
 */
public class CNCottonChinaPortPrice implements DayAnt {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    CNCottonChinaPortPrice parse = new CNCottonChinaPortPrice();
    try {
//      String SessionId = CottonchinaUtil.getSessionId();
//      for (int i = 99; i < 120; i++) {
//        parse.doAntIndex(i, SessionId);
//      }
      parse.doAnt();
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void doAnt() {
    try {
      String SessionId = CottonchinaUtil.getSessionId();
      doAntIndex(1, SessionId);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void doAntIndex(int pageIndex, String SessionId) {
    try {
      int offset = (pageIndex - 1) * 25;
      String url = "http://www.cottonchina.org/newprice/cot_land_more.php?pageIndex=" + pageIndex + "&offset=" + offset;
      String listpage = CottonchinaUtil.getHTML(url, SessionId);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
      int lastDay = PortPriceDaySQL.getLastDay(Commodity.getCommodity("棉花"),getSource());
      for (Tag tg : tags) {
        String linktitle = tg.toPlainTextString();
        if (linktitle.indexOf("外棉到港报价统计表") != -1) {
          long reportDate = getMonthDay(linktitle);
          if (reportDate >= lastDay) {
            Calendar reportDateCalendar = getDate(reportDate);
            int y = reportDateCalendar.get(Calendar.YEAR);
            String href = tg.getAttribute("href");

            parseDate(y, reportDate, href, linktitle, SessionId);
          }
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private long getMonthDay(String linktitle) throws Exception {
    String regex = "[0-9]{2}/[0-1]{0,1}[0-9]{1}/[0-9]{0,1}[0-9]{1}";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(linktitle);
    if (m.find()) {
      String sttt = m.group();
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
      return Long.parseLong(sdf2.format(sdf1.parse("20" + sttt)));
    } else {
      throw new Exception();
    }

  }

  public void parseDate(int year, long reportDate, String href, String title, String SessionId) {
    String url = "http://www.cottonchina.org/newprice/" + href;
    File f = AntLogic.getFile(getSource() + "/Portnewprice/" + year, "portnewprice-" + reportDate + ".htm");

    try {
      if (!f.exists()) {
        long reportDate2 = getMonthDay(title);
        String listpage = CottonchinaUtil.getHTML(url, SessionId);
        if (None.isBlank(listpage))
          return;
        AntLogic.saveFile(getSource() + "/Portnewprice/" + year, "portnewprice-" + reportDate + ".htm", listpage);

      }
      if (f.exists()) {
        InputStream in = new FileInputStream(f);
        String content2 = FileStreamUtil.getFileContent(in);
        in.close();
        boolean is = praseContent(content2, reportDate, f);
        if (!is) {
          f.delete();
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private boolean praseContent(String content, long reportDate, File f) throws ParserException, SQLException {
    try {
      Parser parser = Parser.createParser(content, "GB2312");
      NodeList nl = parser.parse(null);

      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "border", "1" });
      tableattrs.add(new String[] { "align", "center" });
      tableattrs.add(new String[] { "cellpadding", "0" });
      tableattrs.add(new String[] { "cellspacing", "0" });
      tableattrs.add(new String[] { "class", "blacknew" });

      Tag table = HTMLParseUtil.getTag(nl, "table", tableattrs);
      if (table == null) {
        throw new RuntimeException(f.getAbsolutePath() + " table==null");

      }
      List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
      if (trs.size() <= 4)
        return false;
      int r = 0;
      Country lastCountry = null;
      for (Tag tr : trs) {
        try {
          r++;
          List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(), "td");

          if (r <= 2) {
            continue;
          }
          int start = 0;

          Country fromcountry = null;
          String standard = "";
          String term = "";
          if (tds.size() == 9) {
            String fcountrySTR = tds.get(0).toPlainTextString();
            if (fcountrySTR.indexOf("SLM") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "SLM", "");
              standard = "SLM ";
            } else if (fcountrySTR.indexOf("M1.1/8") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "M1.1/8", "");
              standard = "M1.1/8 ";
            } else if (fcountrySTR.indexOf("（远月）") != -1 || fcountrySTR.indexOf("(远月)") != -1) {
              term = "远月";
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "（远月）", "");
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "(远月)", "");
            } else if (fcountrySTR.indexOf("澳大利亚（远月）") != -1 || fcountrySTR.indexOf("澳大利亚(远月)") != -1) {
              term = "远月";
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "（远月）", "");
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "(远月)", "");
            } else if (fcountrySTR.indexOf("远月2012") != -1) {
              term = "远月2012";
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "（远月2012）", "");
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "(远月2012)", "");
            } else if (fcountrySTR.indexOf("12月底到港新花") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "（12月底到港新花）", "");
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "(12月底到港新花)", "");
              term = "12月底到港新花";
            } else if (fcountrySTR.indexOf("Shankar-6") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "Shankar-6", "");
              standard = "SM ";
            } else if (fcountrySTR.indexOf("Shankar-") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "Shankar-", "");
            } else if (fcountrySTR.indexOf("澳大利亚2013") != -1) {
              fcountrySTR = "澳大利亚";
              term = "2013";
            } else if (fcountrySTR.indexOf("津巴布韦RG") != -1) {
              fcountrySTR = "津巴布韦";
            } else if (fcountrySTR.indexOf("新棉") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "（新棉）", "");
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "(新棉)", "");
              term = "新棉";
            } else if (fcountrySTR.indexOf("T/AETHON") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "T/AETHON", "");
            } else if (fcountrySTR.indexOf("新花") != -1) {
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "（新花）", "");
              fcountrySTR = StringUtil.replaceString(fcountrySTR, "(新花)", "");
              term = "新花";
            } else if (fcountrySTR.indexOf("美国") != -1 && fcountrySTR.length() > 3) {
              String temp = fcountrySTR.replaceAll("美国", "").trim();
              if (temp.indexOf("（") != -1) {
                String[] ss = temp.split("（");
                standard = ss[0];
                term = ss[1].replaceAll("）", "");
              }
              fcountrySTR = "美国";
            }

            fcountrySTR = StringUtil.replaceString(fcountrySTR, "SM", "").trim();

            fromcountry = Country.getCountry(fcountrySTR);
            start = 1;
          } else if (tds.size() == 8) {
            fromcountry = lastCountry;
          } else {
            throw new RuntimeException(f.getAbsolutePath() + " tds.size wrong");

          }
          lastCountry = null;
          standard += tds.get(start).toPlainTextString();

          if (fromcountry == null) {
            LogService.err(f.getAbsolutePath() + " no country:" + tds.get(0).toPlainTextString());
            continue;
          }
          lastCountry = fromcountry;

          double price1 = 0, price2 = 0, price3 = 0;
          try {
            price1 = Double.parseDouble(tds.get(start + 1).toPlainTextString());

          } catch (Exception e) {
          }
          try {

            price2 = Double.parseDouble(tds.get(start + 4).toPlainTextString());
          } catch (Exception e) {
          }
          try {

            price3 = Double.parseDouble(tds.get(start + 6).toPlainTextString());
          } catch (Exception e) {
          }
          saveObj(reportDate, standard, term, fromcountry, price1, PortPriceType.getPortPriceType("到岸价"), WeightUnit.getWeightUnit("pound"), PriceUnit.getPriceUnit("CENTS"));
          saveObj(reportDate, standard, term, fromcountry, price2, PortPriceType.getPortPriceType("滑准税提货价"), WeightUnit.getWeightUnit("吨"), PriceUnit.getPriceUnit("元"));
          saveObj(reportDate, standard, term, fromcountry, price3, PortPriceType.getPortPriceType("配额关税提货价"), WeightUnit.getWeightUnit("吨"), PriceUnit.getPriceUnit("元"));

        } catch (Exception e) {
          LogService.trace(e, null);

        }

      }
    } catch (Exception e) {
      LogService.trace(e, null);
      LogService.err(f.getAbsolutePath());

      return false;
    }
    return true;
  }

  private void saveObj(long reportDate, String standard, String term, Country fromcountry, double price, PortPriceType portPriceType, WeightUnit weightUnit, PriceUnit priceUnit) throws SQLException {

    standard = adjuststand(standard);
    Date now = new Date();
    PortPriceDay obj = PortPriceDaySQL.getObj(reportDate, Country.getCountry("CHN"), fromcountry, standard, term, portPriceType, Commodity.getCommodity("棉花"), getSource());
    if (obj == null) {
      obj = new PortPriceDay();
      obj.setReportDate(reportDate);
      obj.setCommodity(Commodity.getCommodity("棉花"));
      obj.setCountry(Country.getCountry("CHN"));
      obj.setFromCountry(fromcountry);

      obj.setPortPriceType(portPriceType);
      obj.setStandard(standard);

    }
    // 到港价

    if (price <= 0) {
      return;
    }
    obj.setTerm(term);
    obj.setWeightUnit1(weightUnit);
    obj.setPriceUnit1(priceUnit);
    obj.setSource(getSource());
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    obj.setValue1(price);
    PortPriceDaySQL.save(obj);

  }

  private String adjuststand(String stand) {
    stand = stand.toUpperCase();
    if ("SJVSM1-5/32".equals(stand.replaceAll(" ", ""))) {
      stand = "SJV SM 1-5/32";
    } else if ("PIMA 2级 1.7/16".equals(stand)) {
      stand = "PIMA 2级 1-7/16";
    } else if ("E/MOT SLM".equals(stand)) {
      stand = "EMOT SLM";
    } else if ("E/MOT M".equals(stand)) {
      stand = "EMOT M";
    } else if ("CASM".equals(stand)) {
      stand = "CA SM";
    }
    return stand;
  }

  @Override
  public String getSource() {
    return "cottonchina";
  }

  private Calendar getDate(long day) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(day + "");
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

}
