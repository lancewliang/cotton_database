package ant.restaurant;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import model.constant.BaiduRegion;
import restaurant.db.RestaurantRecordSQL;
import restaurant.db.RestaurantSQL;
import restaurant.obj.Restaurant;
import restaurant.obj.RestaurantRecord;
import tcc.utils.StringUtil;
import tcc.utils.log.LogService;
import ui.util.CharSetUtil;
import ant.server.DayAnt;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetBaiduRestAurant implements DayAnt {

  public static void main(String[] args) throws FileNotFoundException {
    SetENVUtil.setENV();
    GetBaiduRestAurant p = new GetBaiduRestAurant();
    p.doAnt();
    // p.doAntURL("6900c39a595a3897", 1);

  }

  private String getURL(String area, int index) {

    return "http://waimai.baidu.com/waimai/shoplist/" + area + "?display=json&page=" + index + "&count=40";
  }

  public void doAnt() {

    for (BaiduRegion region : BaiduRegion.getRegions()) {

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
      String url = getURL(area, index);
      // System.out.println(url);
      content = Util.getHTML(url, "utf-8");
      // System.out.println("----------------------------------");
      // System.out.println(content);
      // System.out.println("----------------------------------");

      if (content == null)
        return false;

      // content2 = CharSetUtil.decodeUnicode(content);

      JsonParser parser = new JsonParser();
      JsonElement ps = parser.parse(content);
      JsonObject jsonObj = ps.getAsJsonObject();
      JsonObject rsObj = jsonObj.get("result").getAsJsonObject();
      JsonElement shop_infoels = rsObj.get("shop_info");

      if (shop_infoels.isJsonArray()) {
        JsonArray as = shop_infoels.getAsJsonArray();
        for (int i = 0; i < as.size(); i++) {

          JsonObject jObj = as.get(i).getAsJsonObject();
          parseObj(jObj);
        }
        if (as.size() == 0)
          return false;
        else
          return true;
      } else {
        JsonObject jObj = shop_infoels.getAsJsonObject();
        parseObj(jObj);
        return true;
      }

    } catch (Exception e) {
      LogService.trace(e, content);
      return false;
    }
  }

  private void antObj(Restaurant restaurant) {
    String url = "http://waimai.baidu.com/waimai/shop/" + restaurant.getKeyID();
    try { Thread.sleep(1500);
      String content = engine.util.Util.getHTML(url, "UTF-8");
      Parser parser = Parser.createParser(content, "UTF-8");
      NodeList nl = parser.parse(null);
      List<String[]> dirElcs = new ArrayList<String[]>();
      dirElcs.add(new String[] { "class", "b-info fl" });
      NodeList tt = HTMLParseUtil.getNodeList(nl, "div", dirElcs);

      List<Tag> dls = HTMLParseUtil.getTags(tt, "dl");
      Tag tag = dls.get(2);
      String txt = tag.toPlainTextString().trim();
      txt = StringUtil.replaceString(txt, "\n", "");
      txt = StringUtil.replaceString(txt, "&nbsp;", "");
      txt = txt.split(":")[1];
      restaurant.setAdress(txt);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private void parseObj(JsonObject jsonObj) {
    try {
      Restaurant restaurant = new Restaurant();
      restaurant.setAdress("unknow");
      restaurant.setKeyID(jsonObj.get("shop_id").getAsString());
      restaurant.setName(CharSetUtil.decodeUnicode(jsonObj.get("shop_name").getAsString()));
      restaurant.setReportDate(getDateSTR());
      restaurant.setSource("baidu");
      restaurant.setLatitude(jsonObj.get("shop_lat").getAsDouble());
      restaurant.setLongitude(jsonObj.get("shop_lng").getAsDouble());
      boolean isnew = true;
      if (RestaurantSQL.getObj(restaurant.getKeyID(), "baidu") != null) {
        isnew = false;
      } else {
        RestaurantSQL.insert(restaurant);
      }
      antObj(restaurant);
      if (isnew) {

        RestaurantSQL.update(restaurant);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    try {
      RestaurantRecord record = new RestaurantRecord();
      record.setDelivery_fee(jsonObj.get("takeout_cost").getAsInt());
      record.setKeyID(jsonObj.get("shop_id").getAsString());
      record.setMinimum_order_amount(jsonObj.get("takeout_price").getAsInt());
      record.setMonth_sales(jsonObj.get("saled_month").getAsInt());
      record.setRating_count(0);
      record.setRecent_order_num(jsonObj.get("saled").getAsInt());
      record.setReportDate(getDateSTR());
      record.setSource("baidu");
      if (RestaurantRecordSQL.getObj(record.getKeyID(), "baidu", getDateSTR()) != null) {
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
    // TODO Auto-generated method stub
    return "baidu";
  }
}
