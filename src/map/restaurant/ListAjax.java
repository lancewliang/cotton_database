package map.restaurant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import restaurant.db.RestaurantSQL;
import restaurant.obj.Restaurant;
import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;

public class ListAjax extends Page {

  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);
    try {
      String cmd = request.getParameter("cmd");
      if ("ids".equals(cmd)) {
        String keyIds = request.getParameter("txtarea");
        String json = printjson(keyIds);

        session.setPageSessionValue(pfx + ".rs",json);
     
      } else if ("sql".equals(cmd)) {
        String sql = request.getParameter("txtarea");
        String json = printjsonbySQL(sql);
        session.setPageSessionValue(pfx + ".rs",json);
 
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return super.processCommand(command, session, request, response);
  }

  private String printjsonbySQL(String sql) {
    try {

      List<Restaurant> rss = RestaurantSQL.getObjsBySQL(sql);

      Gson gson = new Gson();
      // Java --> JSON
      String json = gson.toJson(rss);
      return json;
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return "";
  }

  private String printjson(String keyIds) {
    try {
      String[] keys = keyIds.split(",");
      Set<String> keyset = new HashSet<String>();
      for (int i = 0; i < keys.length; i++) {
        String k = keys[i];
        if (k != null)
          k.trim();
        if (!None.isBlank(k)) {
          keyset.add(k);
        }
      }
      List<String> ll = new ArrayList<String>();
      for (String l : keyset) {
        ll.add(l);
      }
      List<Restaurant> rss = RestaurantSQL.getObjsByID(ll);

      Gson gson = new Gson();
      // Java --> JSON
      String json = gson.toJson(rss);
      return json;
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return "";
  }

}
