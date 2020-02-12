package ant.restaurant;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import model.constant.MeituanRegion;
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

import engine.util.SetENVUtil;
import engine.util.Util;

public class GetMeituanRestAurant implements DayAnt {
  public static void main(String[] args) throws FileNotFoundException {
    SetENVUtil.setENV();
    GetMeituanRestAurant p = new GetMeituanRestAurant();
    p.doAntURL("wtw3uwxrw4nw");
    p.doAntURL("wtw3es2huz2j");
    p.doAntURL("wtw3sqr3kbfc");
    p.doAntURL("wtw3s1zxw00g");
    p.doAntURL("wtw3s124y5du");

    // p.doAnt();

  }

  public void doAnt() {

    for (MeituanRegion region : MeituanRegion.getRegions()) {
      System.out.println(region.getName());
      doAntURL(region.getKey());

    }

  }

  public void doAntURL(String area) {
    HashMap cokiee = new HashMap();
    String url = "http://waimai.meituan.com/home/" + area;
    try {
      String content = Util.getHTMLwithCookie(url, "utf-8", cokiee);
      String cookiee = Util.parseCookie((List<String>) cokiee.get("Set-Cookie"));
      // System.out.println(cookiee);
      int page = 0;
      boolean load = true;
      int nodatatimes = 0;
      while (load) {
        boolean ret = doAntURL(cookiee, page);
        if (!ret) {
          nodatatimes++;
        }
        if (nodatatimes >= 3) {
          load = false;
          break;
        }
        page++;
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public boolean doAntURL(String cookieeString, int index) {
    String content = null;
    try {
      Thread.sleep(1500);
      String url = "http://waimai.meituan.com/ajax/poilist";
      // System.out.println(url);
      HashMap cokiee = new HashMap();
      HashMap params = new HashMap();

      params.put("sort_type", "0");
      params.put("classify_type", "cate_all");
      params.put("price_type", "0");
      params.put("support_online_pay", "0");
      params.put("support_invoice", "0");
      params.put("support_logistic", "0");
      params.put("page_offset", "" + (21 * index));
      params.put("page_size", "20");
      cokiee.put("Host", "waimai.meituan.com");
      cokiee.put("Cookie", cookieeString);
      cokiee.put("Referer", "http://waimai.meituan.com/home/wtw3eh71z1bf");
      content = Util.postHTML(url, "utf-8", cokiee, params);
      // System.out.println("----------------------------------");
      // System.out.println(content);
      // System.out.println("----------------------------------");

      if (content == null)
        return false;

      JsonParser parser = new JsonParser();
      JsonElement ps = parser.parse(content);

      JsonObject jsonObj = ps.getAsJsonObject();
      JsonObject rsObj = jsonObj.get("data").getAsJsonObject();
      JsonElement shop_infoels = rsObj.get("poiList");

      if (shop_infoels.isJsonArray()) {
        JsonArray as = shop_infoels.getAsJsonArray();
        System.out.println("---index-----" + index + "---------------as.size():" + as.size() + "-----------");
        for (int i = 0; i < as.size(); i++) {

          JsonObject jObj = as.get(i).getAsJsonObject();
          parseObj(jObj.get("wmPoi4Web").getAsJsonObject());
        }

        if (as.size() == 0)
          return false;
        else
          return true;
      } else {
        JsonObject jObj = shop_infoels.getAsJsonObject();
        parseObj(jObj.get("wmPoi4Web").getAsJsonObject());
        return true;
      }

      // System.out.println("----------------------------------");
      // System.out.println(content);
      // System.out.println("----------------------------------");
    } catch (Exception e) {
      LogService.trace(e, content);
      return false;
    }
  }

  private void parseObj(JsonObject jsonObj) {
    try {
      Restaurant restaurant = new Restaurant();
      restaurant.setAdress(CharSetUtil.decodeUnicode(jsonObj.get("address").getAsString()));
      restaurant.setKeyID(jsonObj.get("wm_poi_id").getAsString());
      restaurant.setName(CharSetUtil.decodeUnicode(jsonObj.get("name").getAsString()));
      restaurant.setReportDate(getDateSTR());
      restaurant.setSource(getSource());
      restaurant.setLatitude(jsonObj.get("latitude").getAsDouble());
      restaurant.setLongitude(jsonObj.get("longitude").getAsDouble());
      if (RestaurantSQL.getObj(restaurant.getKeyID(), getSource()) != null) {

      } else {
        RestaurantSQL.insert(restaurant);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    try {
      RestaurantRecord record = new RestaurantRecord();

      JsonObject wmCPoiLbs = jsonObj.get("wmCPoiLbs").getAsJsonObject();

      record.setDelivery_fee(wmCPoiLbs.get("shipping_fee").getAsInt());
      record.setKeyID(jsonObj.get("wm_poi_id").getAsString());
      record.setMinimum_order_amount(wmCPoiLbs.get("min_price").getAsInt());
      record.setMonth_sales(jsonObj.get("month_sale_num").getAsInt());
      record.setRating_count(0);
      record.setRecent_order_num(jsonObj.get("month_sale_num").getAsInt());
      record.setReportDate(getDateSTR());
      record.setSource(getSource());
      if (RestaurantRecordSQL.getObj(record.getKeyID(), getSource(), getDateSTR()) != null) {
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

    return "meituan";
  }

}
