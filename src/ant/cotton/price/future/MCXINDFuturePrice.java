package ant.cotton.price.future;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import tcc.utils.StringUtil;
import tcc.utils.db.DBUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.json.JSONArray;
import tcc.utils.json.JSONException;
import tcc.utils.json.JSONObject;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class MCXINDFuturePrice implements DayAnt {
  public static void main(String[] args) throws Exception {
     SetENVUtil.setENV();
    MCXINDFuturePrice exp = new MCXINDFuturePrice();
    exp.doAnt();
  }

  @Override
  public void doAnt() {
    String url = "http://www.quandl.com/api/v2/datasets?query=Cotton&page=1&per_page=10&source_ids=3109&request_source=next&request_version=0.1.9";
    // "http://www.quandl.com/search/Cotton?page=1&source_ids=3109";

    try {

      String html = Util.getHTML(url, "utf-8");
      JSONObject jo = new JSONObject(html);
      JSONArray docs = jo.getJSONArray("docs");
      for (int i = 0; i < docs.length(); i++) {
        JSONObject obj = docs.getJSONObject(i);
        String urlize_name = obj.getString("urlize_name");
        String updated_at = obj.getString("updated_at");
        String code = obj.getString("code");
        long lastUpdateDate = FuturePriceDaySQL.getLastDay(code, getSource());

        if (lastUpdateDate <= 0)
          lastUpdateDate = 20100101;
        // CTF2014-Cotton-Futures-January-2014-CTF2014-MCX
        if (isUpdate(lastUpdateDate, updated_at)) {
          String surl = "http://www.quandl.com/MCX/" + code + "-" + urlize_name;
          doAntContract(code, urlize_name, lastUpdateDate, surl);

         LogService.msg(code + "|" + urlize_name + "|" + updated_at + "|" + surl);
        }

      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void doAntContract(String contract, String urlize_name, long lastUpdateDate, String url) throws IOException, Exception {

    File f = AntLogic.getFile(getSource() + "/cotton", urlize_name + ".htm");
    if (!f.exists()) {
      engine.util.Util.getFile(url, f);
    }
    InputStream in = new FileInputStream(f);
    String html = FileStreamUtil.getFileContent(in);
    in.close();

    Parser parser = Parser.createParser(html, "UTF-8");
    NodeList nl = parser.parse(null);
    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "data-controller", "datasets" });
    tableattrs.add(new String[] { "data-action", "show" });
    Tag div = HTMLParseUtil.getTag(nl, "div", tableattrs);
    String data_id = div.getAttribute("data-id");
    praseContent(data_id, contract, urlize_name, lastUpdateDate);
  }

  private void praseContent(String data_id, String contract, String urlize_name, long lastUpdateDate) throws ParserException, IOException, JSONException, ParseException, SQLException {

    String suburl = "https://www.quandl.com/datasets/" + data_id + ".json?";

    File f2 = AntLogic.getFile(getSource() + "/cotton", "Data-" + urlize_name + "-" + data_id + ".json");

    String html = engine.util.Util.getHTML(suburl, "utf-8");
    if (f2.exists()) {
      InputStream in = new FileInputStream(f2);
      String html2 = FileStreamUtil.getFileContent(in);
      in.close();
      if (!html2.equals(html)) {
        AntLogic.saveFile(getSource() + "/cotton", "Data-" + urlize_name + "-" + data_id + ".json", html);
      }
    } else {
      AntLogic.saveFile(getSource() + "/cotton", "Data-" + urlize_name + "-" + data_id + ".json", html);
    }

    Date now = new Date();
    JSONObject jo = null;
    try{
    jo=new JSONObject(html);
    }catch(Exception e){
    	LogService.trace(e, html);
    }
    JSONArray data = jo.getJSONArray("data");
    for (int i = 0; i < data.length(); i++) {
      JSONArray ss = data.getJSONArray(i);
      long reportDate = getdate(ss.getString(0));
      if (reportDate < lastUpdateDate) {
        continue;
      }
      FuturePriceDay obj = FuturePriceDaySQL.getObj(reportDate, Country.getCountry("IND"), contract, Commodity.getCommodity("棉花"), getSource());
      if (obj == null) {
        obj = new FuturePriceDay();
        obj.setReportDate(reportDate);
        obj.setBourse(Bourse.getBourse("MCX"));
        obj.setCommodity(Commodity.getCommodity("棉花"));
        obj.setCountry(Country.getCountry("IND"));
        obj.setContract(contract);
        obj.setWeightUnit(WeightUnit.getWeightUnit("包,170KG"));
        obj.setPriceUnit(PriceUnit.getPriceUnit("INR"));
        obj.setSource(getSource());
      }
      String openingValueSTR = ss.get(1).toString();
      String topValueSTR = ss.get(2).toString();
      String minimumValueSTR = ss.get(3).toString();
      String closingValueSTR = ss.get(4).toString();
      obj.setOpeningValue(getIntI(openingValueSTR));
      obj.setTopValue(getIntI(topValueSTR));
      obj.setMinimumValue(getIntI(minimumValueSTR));
      obj.setClosingValue(getIntI(closingValueSTR));
      obj.setVolumes(getIntT(ss.get(5).toString()));

      obj.setUpdatedAt(now);
      obj.setUpdatedBy(AntManger.UPDATEBY);
      FuturePriceDaySQL.save(obj);

    }

  }

  private int getIntT(String str) {
    try {
      String ss = str.trim().replaceAll("&#176;", "").replaceAll(",", "");
      ss = ss.replaceAll("&nbsp;", "");
      ss = StringUtil.replaceLastString(ss, ".0", "");
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

  private long getdate(String d) throws ParseException {
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    Date updated_Date = sdf1.parse(d);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    return Long.parseLong(sdf2.format(updated_Date));
  }

  private boolean isUpdate(long lastDate, String updated_at) throws ParseException {

    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
    // 利用 DateFormat 來parse 日期的字串
    Date updated_Date = sdf1.parse(updated_at.split("T")[0]);
    Date last_Date = sdf2.parse("" + lastDate);
    Calendar updateAt = Calendar.getInstance();
    updateAt.setTime(updated_Date);
    Calendar lastAt = Calendar.getInstance();
    lastAt.setTime(last_Date);
    return lastAt.before(updateAt);
  }

  @Override
  public String getSource() {
    return "MCX";
  }

}
