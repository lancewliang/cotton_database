package ant.macroeconomic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import engine.util.SetENVUtil;

import model.constant.Country;
import model.constant.PriceUnit;
import model.entity.macroeconomic.CountryMainIndex;
import model.entity.macroeconomic.db.CountryMainIndexSQL;
import tcc.utils.None;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.json.JSONArray;
import tcc.utils.json.JSONException;
import tcc.utils.json.JSONObject;
import tcc.utils.log.LogService;
import ui.util.Native2AsciiUtils;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;

public class GetWallstreetCN implements DayAnt {

  public static void main(String[] args) throws FileNotFoundException {
    SetENVUtil.setENV();
    GetWallstreetCN p = new GetWallstreetCN();

    p.doAnt();

  }

  @Override
  public void doAnt() {
    // TODO Auto-generated method stub
    try {

      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
      cal.add(Calendar.DATE, 1);
      int currentDay = Integer.parseInt(sdf2.format(cal.getTime()));

      int lastday = CountryMainIndexSQL.getLastDay(getSource());
      if (lastday <= 0)
        lastday = 20100101;
      boolean dotask = false;

      Calendar lastDayCalendar = getDate(lastday);
      lastDayCalendar.add(Calendar.DATE, -2);
      Calendar currentDayCalendar = getDate(currentDay);
      currentDayCalendar.add(Calendar.DATE, 1);
      while (!lastDayCalendar.after(currentDayCalendar)) {
        int y = lastDayCalendar.get(Calendar.YEAR);
        String d1 = sdf3.format(lastDayCalendar.getTime());
        lastDayCalendar.add(Calendar.DATE, 1);
        String d2 = sdf3.format(lastDayCalendar.getTime());
        getDay(y, d1, d2);
        dotask = true;
      }

      if (!dotask) {
        LogService.msg(" not dotask  ");
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void getDay(long year, String day, String nextday) throws ParseException {
    File f = AntLogic.getFile(getSource() + "/" + year, "event" + day + ".htm");
    try {
      if (f.exists()) {
        InputStream in = new FileInputStream(f);
        String content2 = FileStreamUtil.getFileContent(in);
        in.close();
        parseContent(content2);
      } else {

        String url = "http://api.markets.wallstreetcn.com/v1/calendar.json?start=" + day + "&end=" + nextday + "&callback=jQuery2030746527035953477_1406462385181&_=1406462385209";
        String content = engine.util.Util.getHTML(url, "UTF-8");
        if (parseContent(content)) {
          AntLogic.saveFile(getSource() + "/" + year, "event" + day + ".htm", content);
          File f2 = AntLogic.getFile(getSource() + "/" + year, "temp-event" + day + ".htm");
          if (f2.exists()) {
            f2.delete();
          }
        } else {
          AntLogic.saveFile(getSource() + "/" + year, "temp-event-" + day + ".htm", content);
        }

      }
    } catch (IOException e) {
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

  /**
   * {"id":"202295","eventRowId":null,"timestamp":"2014-07-22 16:00:00",
   * "localDateTime":"2014-07-23 00:00:00","importance":"3","title":
   * "\u6fb3\u6d32\u56fd\u5e93\u90e8\u957f\u970d\u57fa\u3001\u65b0\u897f\u5170\u8d22\u957f\u82f1\u683c\u5229\u5e0c\u5bf9\u6cdb\u5854\u65af\u66fc\u5546\u5708\u53d1\u8868\u8054\u5408\u8bb2\u8bdd\u3002"
   * ,"forecast":"NULL","actual":"NULL","previous":"NULL","category_id":"10249",
   * "relatedAssets"
   * :"NULL","remark":"\u60e0\u7075\u987f","mark":null,"calendarType"
   * :"FE","country"
   * :"\u7ebd\u897f\u5170","currency":"NZD","event_attr_id":null,"description"
   * :null}
   * 
   * 
   * 
   * 
   * "id":"201391","eventRowId":"eventRowId_39377","timestamp":
   * "2014-07-21 06:00:00"
   * ,"localDateTime":"2014-07-21 14:00:00","importance":"1"
   * ,"title":"6\u6708\u5fb7\u56fdPPI(\u5e74\u7387)"
   * ,"forecast":"-0.7%","actual":
   * "-0.7%","previous":"-0.8%","category_id":"11117"
   * ,"relatedAssets":"","remark"
   * :"","mark":"","calendarType":"FD","country":"\u5fb7\u56fd"
   * ,"currency":"EUR","event_attr_id":"739","description":
   * "\u5fb7\u56fd\u751f\u4ea7\u8005\u4ef7\u683c\u6307\u6570\uff08PPI\uff09
   * \u662f \u56fd \u5185 \u751f \u4ea7 \u8005 \u6240 \u83b7 \u5f97 \u7684
   * \u6d88 \u8d39 \u54c1 \u548c \u52b3 \u52a8 \u529b \u7684 \u9500 \u552e
   * \u4ef7 \u683c \u5e73 \u5747 \u53d8 \u52a8 \u7684 \u901a \u80c0 \u6307
   * \u6807 \u3002 <br>
   * PPI\u4ece\u6d88\u8d39\u8005\u7684\u89d2\u5ea6\u8861\u91cf\u4ef7\u683c\u53d8\u5316
   * \u3002 <br>
   * PPI\u5bf9\u4ee5\u4e0b\u4e09\u4e2a\u751f\u4ea7\u9886\u57df\u8fdb\u884c\u8c03\u67e5
   * \uff1a \u5de5\u4e1a\u3001
   * \u5546\u54c1\u53ca\u52a0\u5de5\u9636\u6bb5\u7684\u516c\u53f8\u3002 <br>
   * \u5f53\u751f\u4ea7\u8005\u5411\u6d88\u8d39\u54c1\u548c\u52b3\u52a8\u529b\u652f\u51fa\u66f4\u591a\u65f6
   * \uff0c
   * \u4ed6\u4eec\u5f88\u53ef\u80fd\u5c06\u8be5\u90e8\u5206\u589e\u52a0\u7684\u6210\u672c\u52a0\u7ed9\u6d88\u8d39\u8005
   * \uff0c
   * \u6240\u4ee5PPI\u88ab\u8ba4\u4e3a\u662f\u6d88\u8d39\u8005\u7269\u4ef7\u6307\u6570\u7684\u9886\u5148\u6307\u6807
   * \u3002 <br>
   * \u5982\u679c\u8be5\u6307\u6807\u6bd4\u9884\u671f\u66f4\u9ad8\uff0c
   * \u5219\u5e94\u8ba4\u4e3a\u6b27\u5143\u5f3a\u52bf\/\u770b\u6da8\uff0c
   * \u800c\u5982\u679c\u8be5\u6307\u6807\u6bd4\u9884\u671f\u66f4\u4f4e
   * \uff0c\u5219\u5e94\u8ba4\u4e3a\u6b27\u5143\u5f31\u52bf\/\u770b\u8dcc\u3002"
   * 
   * @param jsonStr
   */
  public boolean parseContent(String jsonStr) {
    jsonStr = getJson(jsonStr);
    Date now = new Date();
    boolean hasData = false;
    try {
      String format = "yyyy-MM-dd hh:mm:ss";
      SimpleDateFormat sdf1 = new SimpleDateFormat(format);

      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
      SimpleDateFormat sdf3 = new SimpleDateFormat("hhmm");
      JSONObject jo = new JSONObject(jsonStr);
      JSONArray data = jo.getJSONArray("results");
      if (jo.getInt("count") == 0) {
        return true;
      }
      for (int i = 0; i < data.length(); i++) {
        JSONObject ss = data.getJSONObject(i);
        String calendarType = ss.getString("calendarType");
        if ("NV".equals(calendarType)) {
          // 假期预告
        } else if ("SR".equals(calendarType)) {
          // 股票财报
        } else if ("FE".equals(calendarType)) {
        } else if ("FD".equals(calendarType)) {

          Country country = Country.getCountry(ss.getString("country"));
          if (country == null) {
            throw new Exception(ss.getString("country"));
          }
          PriceUnit priceunit = PriceUnit.getPriceUnit(ss.getString("currency"));
          if (priceunit == null) {
            if (getString(ss, "country").equals(ss.getString("currency"))) {

            } else
              throw new Exception(ss.getString("currency"));
          }

          Date date = sdf1.parse(ss.getString("localDateTime"));

          CountryMainIndex index = CountryMainIndexSQL.getObj(Integer.parseInt(sdf2.format(date)), Integer.parseInt(sdf3.format(date)), getSource(), country, getString(ss, "title"));

          if (index == null) {
            index = new CountryMainIndex();
            index.setReportDate(Integer.parseInt(sdf2.format(date)));
            index.setReportHour(Integer.parseInt(sdf3.format(date)));
            index.setSource(getSource());
            index.setCountry(country);
            index.setTitle(getString(ss, "title"));
          }
          index.setForecastValue(getString(ss, "forecast"));
          index.setPreviousValue(getString(ss, "previous"));
          index.setActualValue(getString(ss, "actual"));
          if (None.isNonBlank(index.getActualValue())) {
            hasData = true;
          } else {
            hasData = false;
          }
          if (priceunit != null)
            index.setCurrency(priceunit.getPriceUnit());
          else{
            index.setCurrency(country.getCountry());
          }
          index.setImportance(ss.getInt("importance"));

          index.setMark(getString(ss, "mark"));
          index.setRemark(getString(ss, "remark"));
          index.setDescription(getString(ss, "description"));
          index.setUpdatedAt(now);
          index.setUpdatedBy(AntManger.UPDATEBY);
          CountryMainIndexSQL.save(index);
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return hasData;
  }

  private String getString(JSONObject obj, String atr) throws JSONException {
    Object object = obj.get(atr);

    if (object instanceof String) {
      String str = obj.getString(atr);
      if (str != null)
        return Native2AsciiUtils.ascii2native(str);
      else {
        return "";
      }
    } else if (JSONObject.NULL.equals(object)) {
      return "";
    } else {
      throw new JSONException(atr);
    }

  }

  public String getJson(String str) {
    int s1 = str.indexOf("(");
    if (s1 != -1)
      str = str.substring(s1 + 1);
    int s2 = str.lastIndexOf(")");
    if (s2 != -1)
      str = str.substring(0, s2);
    return str;
  }

  @Override
  public String getSource() {
    return "Wallstartcn";
  }

}
