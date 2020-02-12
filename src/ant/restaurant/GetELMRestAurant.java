package ant.restaurant;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import model.constant.ELERegion;
import model.constant.WeatherRegion;
import restaurant.db.RestaurantRecordSQL;
import restaurant.db.RestaurantSQL;
import restaurant.obj.Restaurant;
import restaurant.obj.RestaurantRecord;
import tcc.utils.log.LogService;
import ui.util.CharSetUtil;
import ant.server.DayAnt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import engine.util.HttpsUtil;
import engine.util.SetENVUtil;

public class GetELMRestAurant implements DayAnt {
  String area = "wtw3uk9uqefv";
  String url = "https://www.ele.me/restapi/v4/restaurants?extras%5B%5D=food_activity&extras%5B%5D=restaurant_activity&extras%5B%5D=statistics&geohash=" + area
      + "&limit=24&offset=24&type=geohash";
  // https://www.ele.me/restapi/shopping/restaurants?extras%5B%5D=activities&geohash=wtw3uk9uqefv&latitude=31.270587&limit=24&longitude=121.478516&offset=24
  // https://www.ele.me/restapi/shopping/restaurants?extras%5B%5D=activities&geohash=wtw3uk9uqefv&latitude=31.270587&limit=24&longitude=121.478516&offset=48
  private String charset = "utf-8";
  static {

  }

  public static void main(String[] args) throws FileNotFoundException {
    SetENVUtil.setENV();
    GetELMRestAurant p = new GetELMRestAurant();
   // p.doAnt();
    for(int i=0;i<40;i++)
     p.doAntURL("wtw36fgvwbw", i);

  }

  private String getURL(String area, int index) {

    return "https://www.ele.me/restapi/v4/restaurants?extras%5B%5D=food_activity&extras%5B%5D=restaurant_activity&extras%5B%5D=statistics&geohash=" + area + "&limit=24&offset="
        + (24 * index) + "&type=geohash";
  }

  public void doAnt() {

    for (ELERegion region : ELERegion.getRegions()) {

      int page = 0;
      boolean load = true;
      int nodatatimes = 0;

      while (load) {
        boolean ret = doAntURL(region.getKey(), page);
        if (!ret) {
          nodatatimes++;
        }
        if (nodatatimes >= 3) {
          load = false;
          break;
        }
        page++;
      }
    }
  }

  public boolean doAntURL(String area, int index) {
	
    String content = null;
    try {
    	  Thread.sleep(1000);
      // System.out.println(url);
      content = HttpsUtil.get(getURL(area, index));
      // System.out.println("----------------------------------");
      // System.out.println(content);
      // System.out.println("----------------------------------");

      if (content == null)
        return false;

      // content2 = CharSetUtil.decodeUnicode(content);

      JsonParser parser = new JsonParser();
      JsonElement ps = parser.parse(content);
      if (ps.isJsonArray()) {
        JsonArray as = ps.getAsJsonArray();
        System.out.println(as.size());
        for (int i = 0; i < as.size(); i++) {

          JsonObject jsonObj = as.get(i).getAsJsonObject();
          parseObj(jsonObj);
        }
        if (as.size() == 0)
          return false;
        else
          return true;
      } else {

        JsonObject jsonObj = ps.getAsJsonObject();
        parseObj(jsonObj);
        return true;
      }

    } catch (Exception e) {
      LogService.trace(e, content);
      return false;
    }
  }

  private void parseObj(JsonObject jsonObj) {
    try {
      Restaurant restaurant = new Restaurant();
      restaurant.setAdress(CharSetUtil.decodeUnicode(jsonObj.get("address").getAsString()));
      restaurant.setKeyID(jsonObj.get("id").getAsString());
      restaurant.setName(CharSetUtil.decodeUnicode(jsonObj.get("name").getAsString()));
      restaurant.setReportDate(getDateSTR());
      restaurant.setSource("ele");
      restaurant.setLatitude(jsonObj.get("latitude").getAsDouble());
      restaurant.setLongitude(jsonObj.get("longitude").getAsDouble());
      if (RestaurantSQL.getObj(restaurant.getKeyID(), "ele") != null) {

      } else {
        RestaurantSQL.insert(restaurant);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    try {
      RestaurantRecord record = new RestaurantRecord();
      record.setDelivery_fee(jsonObj.get("delivery_fee").getAsInt());
      record.setKeyID(jsonObj.get("id").getAsString());
      record.setMinimum_order_amount(jsonObj.get("minimum_order_amount").getAsInt());
      record.setMonth_sales(jsonObj.get("month_sales").getAsInt());
      record.setRating_count(jsonObj.get("rating_count").getAsInt());
      record.setRecent_order_num(jsonObj.get("recent_order_num").getAsInt());
      record.setReportDate(getDateSTR());
      record.setSource("ele");
      if (RestaurantRecordSQL.getObj(record.getKeyID(), "ele", getDateSTR()) != null) {
      } else {
        RestaurantRecordSQL.insert(record);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private long getDateSTR() throws ParseException {

    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    Date date = new Date();
    return Long.parseLong(sdf.format(date));
  }

  @Override
  public String getSource() {

    return "ele";
  }
}
