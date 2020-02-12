package ant.cotton.price.spot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.PortPriceType;
import model.constant.PriceUnit;
import model.constant.UnitType;
import model.constant.WeightUnit;
import model.entity.price.country.CountryPriceDay;
import model.entity.price.country.PortPriceDay;
import model.entity.price.country.db.CountryPriceDaySQL;
import model.entity.price.country.db.PortPriceDaySQL;

import org.htmlparser.util.ParserException;

import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetPCGASpotRates implements DayAnt {
  String listurl = "http://medialinepakistan.com/?s=Karachi+Cotton+Association+Official+Spot+Rate+for+Local+Dealings+in+Pak+Rupees";

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetPCGASpotRates mhhf = new GetPCGASpotRates();
    mhhf.doAnt();
  }

  @Override
  public void doAnt() {
    // TODO Auto-generated method stub
    try {

      getFileList();

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void getFileList() {

    try {

      long lastDay = CountryPriceDaySQL.getLastDay(Commodity.getCommodity("棉花"), getSource());
      if (lastDay <= 0)
        lastDay = 20101001;

      File folder = AntLogic.getFile(getSource() + "/ARRIVAL CROP/", "2014");
      if (folder.exists()) {

        for (File f : folder.listFiles()) {
          if (!f.isFile())
            continue;
          String filename = f.getName();
          InputStream in = new FileInputStream(f);
          long reportDate = getDate(filename.substring(filename.length() - 15, filename.length() - 4));
          String subcontent = FileStreamUtil.getFileContent(in);
          in.close();
          if (reportDate >= lastDay) {
            boolean v = praseContent(lastDay, reportDate, subcontent);
            if (!v) {

              System.out.println("break");
              return;
            }
          }

        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  private boolean praseContent(long lastDay, long currentDay, String content) throws ParserException {
    String str = content;
    int areaStart, areaEnd;
    String htmlStr, areaStr;

    while ((areaStart = str.indexOf("<pre>")) != -1) {

      areaEnd = str.indexOf("</pre>", areaStart + 1);
      String body = str.substring(areaStart + "<pre>".length(), areaEnd);
      if (body.contains("Equivalent")) {

        return prasePre(lastDay, currentDay, body);
      }

      str = str.substring(areaEnd + "</pre>".length());
    }
    while ((areaStart = str.indexOf("<p>")) != -1) {

      areaEnd = str.indexOf("</p>", areaStart + 1);
      String body = str.substring(areaStart + "<p>".length(), areaEnd);
      if (body.contains("Equivalent")) {

        return prasePre(lastDay, currentDay, body);
      }

      str = str.substring(areaEnd + "</p>".length());
    }
    return true;
  }

  private boolean prasePre(long lastDay, long currentDay, String content) {

    BufferedReader br = null;
    String line = "";
    ParseInfo parseInfo = new ParseInfo();
    try {

      br = new BufferedReader(new StringReader(content));
      long reportDate = 0;
      long ArrivalsTotal = 0;
      long SaleTotal = 0;
      while ((line = br.readLine()) != null) {
        if (parseInfo.stepIndex < 0) {
          if (line.indexOf("Ex-Gin Price") != -1) {
            parseInfo.stepIndex = 0;

          }
        } else if (parseInfo.stepIndex == 0) {
          if (line.indexOf("Equivalent") != -1) {

            parseInfo.stepIndex = 1;

          }
        } else if (parseInfo.stepIndex == 1) {
          if (line.startsWith("40 kgs")) {
            line = HTMLParseUtil.trim2bank(line);
            line = line.replaceFirst("40 kgs", "40kgs");
            line = line.replaceAll(" ,", "");
            line = line.replaceAll(",", "");
            String[] ss = line.split(" ");
            saveObj(currentDay, Double.parseDouble(ss[1]));
            saveObj2(currentDay, Double.parseDouble(ss[3]));
            return true;
          }
        }
      }

    } catch (Exception e) {
      LogService.trace(e, content);

    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  private void saveObj(long reportDate, double price) {
    try {
      if (price <= 0) {
        return;
      }
      String standard = "M 1-1/16";
      CountryPriceDay obj = CountryPriceDaySQL.getObj(reportDate, Country.getCountry("PK"), "COUNTRY", standard, Commodity.getCommodity("棉花"), getSource());
      if (obj == null) {
        obj = new CountryPriceDay();
        obj.setReportDate(reportDate);
        obj.setCommodity(Commodity.getCommodity("棉花"));
        obj.setCountry(Country.getCountry("PK"));
        UnitType unittype = UnitType.getUnitType("重量单位");
        obj.setUnitType(unittype);
        obj.setStandard(standard);
        obj.setState("COUNTRY");
      }

      Date now = new Date();
      obj.setUnit(WeightUnit.getWeightUnit("40KG"));
      obj.setPriceUnit(PriceUnit.getPriceUnit("PRK"));
      obj.setSource(getSource());
      obj.setUpdatedAt(now);
      obj.setUpdatedBy(AntManger.UPDATEBY);
      obj.setValue(price);

      CountryPriceDaySQL.save(obj);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void saveObj2(long reportDate, double price) throws SQLException {
    String standard = "M 1-1/16";
    String term = "Karachi";
    Country fromcountry = Country.getCountry("PK");
    PortPriceType portPriceType = PortPriceType.getPortPriceType("市场报价");
    Date now = new Date();
    PortPriceDay obj = PortPriceDaySQL.getObj(reportDate, Country.getCountry("PK"), fromcountry, standard, term, portPriceType, Commodity.getCommodity("棉花"), getSource());
    if (obj == null) {
      obj = new PortPriceDay();
      obj.setReportDate(reportDate);
      obj.setCommodity(Commodity.getCommodity("棉花"));
      obj.setCountry(Country.getCountry("PK"));
      obj.setFromCountry(fromcountry);

      obj.setPortPriceType(portPriceType);
      obj.setStandard(standard);
      obj.setTerm(term);
    }
    // 到港价

    if (price <= 0) {
      return;
    }

    obj.setWeightUnit1(WeightUnit.getWeightUnit("40KG"));

    obj.setSource(getSource());
    obj.setPriceUnit1(PriceUnit.getPriceUnit("PRK"));
    obj.setUpdatedAt(now);
    obj.setUpdatedBy(AntManger.UPDATEBY);
    obj.setValue1(price);
    PortPriceDaySQL.save(obj);

  }

  private long getDate(String dateSTR) throws Exception {
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(dateSTR);

    return Long.parseLong(sdf2.format(date));

  }

  @Override
  public String getSource() {
    return "pcga";

  }

  class ParseInfo {
    int stepIndex = -100;
    int Crop_and_State_line_size = 0;

  }
}
