package ant.cotton.custom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import engine.util.Util;

public class CottonchinaUtil {

  public static void main(String[] args) throws IOException {
    getSessionId();
  }

  public static String getHTML(String url, String Cookie) throws IOException {
    Map<String, String> values = new HashMap<String, String>();
    values.put("Accept-Language", "zh-CN,zh;q=0.8");
    values.put("Cache-Control", "max-age=0");
    values.put("Connection", "keep-alive");
    values.put("Host", "www.cottonchina.org");
    values.put("Cookie", Cookie);

    return Util.getHTML(url, "gb2312", values);
  }

  public static String getSessionId() throws IOException {
    String url = "http://www.cottonchina.org/stat/usda/usda_show.php?ym=2014-03";
    String html = getHTML(url, "PHPSESSID=3450fuqttl1k7q584blc48kkp5");
    if (html.indexOf("����Ϣ���ڸ߼���Ա���Ȩ�ޡ�") != -1) {
      return login();
    }else{
      return "PHPSESSID=3450fuqttl1k7q584blc48kkp5";
    } 
  }

  private static String login() throws IOException {

    String getURL = "http://www.cottonchina.org/userknow.php?username=C0i&userpwd=asdfgh&imageField.x=21&imageField.y=4";

    Map<String, String> propertys = new HashMap<String, String>();
    propertys.put("Accept-Language", "zh-CN,zh;q=0.8");
    propertys.put("Cache-Control", "max-age=0");
    propertys.put("Connection", "keep-alive");
    propertys.put("Host", "www.cottonchina.org");
    propertys.put("Cookie", "PHPSESSID=3450fuqttl1k7q584blc48kkp5");
    System.out.println("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
    // ������
    connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    if (propertys != null) {
      for (String k : propertys.keySet()) {
        connection.setRequestProperty(k, propertys.get(k));

      }
    }
    String cookie = connection.getHeaderField("set-cookie");
    connection.connect();
    // ȡ������������ʹ��Reader��ȡ
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "gb2312"));// ���ñ���,������������
    } catch (Exception e) {
    }
    if (reader == null) {
      System.out.println("=============================Contents of no source end request:" + getURL + "=============================");
     
      connection.disconnect();
      return null;
    }

    String rs = "";
    String lines;
    while ((lines = reader.readLine()) != null) {
      // lines = new String(lines.getBytes(), "utf-8");
      rs += lines + "\n";
    }
    reader.close();
    // �Ͽ�����
    connection.disconnect();
    System.out.println("=============================Contents of end request:" + getURL + "=============================");

    return cookie;

  }
}
