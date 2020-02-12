package engine.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import tcc.utils.file.FileUtil;
import tcc.utils.log.LogService;

public class Util {
  public static void getFile(String getURL, File file) throws IOException {

    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
    // ������
    InputStream input = null;
    try {
      connection.connect();
      input = connection.getInputStream();

      if (!file.getParentFile().exists()) {
        file.getParentFile().mkdirs();
      }

      if (!file.exists()) {
        file.createNewFile();
      }

      FileUtil.copyFile(input, file);

    } catch (Exception e) {
      e.printStackTrace();
      LogService.msg("=============================Contents of IOException request:" + getURL + "=============================");
    } finally {
      if (input != null)
        input.close();
      if (connection != null)
        connection.disconnect();
    }
  }

  public static String getContent(String getURL, String encode) throws IOException {
    System.out.println("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
    // ������
    BufferedReader reader = null;
    try {
      connection.connect();
      // ȡ������������ʹ��Reader��ȡ

      try {
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encode));// ���ñ���,������������
      } catch (Exception e) {
      }
      if (reader == null) {
        LogService.msg("=============================Contents of no source end request:" + getURL + "=============================");

        return null;
      }

      String rs = "";
      String lines;
      while ((lines = reader.readLine()) != null) {
        // lines = new String(lines.getBytes(), "utf-8");
        rs += lines + "\n";
      }

      LogService.msg("=============================Contents of end request:" + getURL + "=============================");
      return rs;
    } catch (Exception e) {
      e.printStackTrace();
      LogService.msg("=============================Contents of IOException request:" + getURL + "=============================");
      return null;
    } finally {
      try {
        if (reader != null)
          reader.close();
      } catch (Exception e1) {
        // �Ͽ�����

      }
      try {
        if (connection != null)
          connection.disconnect();
        LogService.msg("=============================Contents of end request:" + getURL + "=============================");
      } catch (Exception e1) {
      }
    }
  }

  public static String getHTML(String getURL, String encode) throws IOException {
    return getHTML(getURL, encode, null);
  }

  public static String postHTML(String getURL, String encode, Map<String, String> propertys, Map<String, String> params) throws IOException {
    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
    // ������
    byte[] data = getRequestData(params, encode).toString().getBytes(); // ���������
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
    connection.setUseCaches(false);
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    if (propertys != null) {
      for (String k : propertys.keySet()) {
        connection.setRequestProperty(k, propertys.get(k));

      }
    }

    connection.connect();
    OutputStream outputStream = connection.getOutputStream();
    outputStream.write(data);

    // ȡ������������ʹ��Reader��ȡ
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encode));// ���ñ���,������������
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

    return rs;
  }

  public static StringBuffer getRequestData(Map<String, String> params, String encode) {
    StringBuffer stringBuffer = new StringBuffer(); // �洢��װ�õ���������Ϣ
    try {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
      }

      if (stringBuffer.toString().endsWith("&"))
        stringBuffer.deleteCharAt(stringBuffer.length() - 1); // ɾ������һ��"&"
    } catch (Exception e) {
      e.printStackTrace();
    }
    return stringBuffer;
  }

  public static String parseCookie(List<String> str2) {
    String str = "";
    for (String responseCookie : str2) {
      str += responseCookie.substring(0, responseCookie.indexOf(";")+1);
    }
    return str;
  }

  public static String getHTMLwithCookie(String getURL, String encode, Map propertys) throws IOException {
    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    String rs = "";
    BufferedReader reader = null;
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setConnectTimeout(60000);
      connection.setReadTimeout(60000);
      // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
      // ������
      connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
      connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

      connection.connect();
      // ȡ������������ʹ��Reader��ȡ
      Map<String, List<String>> str1 = connection.getHeaderFields();
      List<String> str2 = str1.get("Set-Cookie");
      propertys.put("Set-Cookie", str2);
      try {
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encode));// ���ñ���,������������
      } catch (Exception e) {
      }
      if (reader == null) {
        LogService.msg("=============================Contents of no source end request:" + getURL + "=============================");
        connection.disconnect();
        return null;
      }

      String lines;
      while ((lines = reader.readLine()) != null) {
        // lines = new String(lines.getBytes(), "utf-8");
        rs += lines + "\n";
      }

      // �Ͽ�����
    } catch (IOException e) {
      LogService.trace(e, null);
      LogService.msg("=============================Contents of IOException request:" + getURL + "=============================");

      return null;
    } finally {
      try {
        try {
          if (reader != null)
            reader.close();
        } catch (Exception e) {
        }
        if (connection != null)
          connection.disconnect();
      } catch (Exception e) {
      }
    }
    LogService.msg("=============================Contents of end request:" + getURL + "=============================");

    return rs;
  }

  public static String getHTML(String getURL, String encode, Map<String, String> propertys) throws IOException {
    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    String rs = "";
    BufferedReader reader = null;
    HttpURLConnection connection = null;
    try {
      connection = (HttpURLConnection) getUrl.openConnection();
      connection.setConnectTimeout(60000);
      connection.setReadTimeout(60000);
      // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
      // ������
      connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
      connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
      if (propertys != null) {
        for (String k : propertys.keySet()) {
          connection.setRequestProperty(k, propertys.get(k));

        }
      }

      connection.connect();
      // ȡ������������ʹ��Reader��ȡ

      try {
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encode));// ���ñ���,������������
      } catch (Exception e) {
      }
      if (reader == null) {
        LogService.msg("=============================Contents of no source end request:" + getURL + "=============================");
        connection.disconnect();
        return null;
      }

      String lines;
      while ((lines = reader.readLine()) != null) {
        // lines = new String(lines.getBytes(), "utf-8");
        rs += lines + "\n";
      }

      // �Ͽ�����
    } catch (IOException e) {
      LogService.trace(e, null);
      LogService.msg("=============================Contents of IOException request:" + getURL + "=============================");

      return null;
    } finally {
      try {
        try {
          if (reader != null)
            reader.close();
        } catch (Exception e) {
        }
        if (connection != null)
          connection.disconnect();
      } catch (Exception e) {
      }
    }
    LogService.msg("=============================Contents of end request:" + getURL + "=============================");

    return rs;
  }

  public static String getHTML(String getURL, String encode, int linesize) throws IOException {
    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // ����ƴ�յ�URL�������ӣ�URL.openConnection���������URL�����ͣ�
    // ���ز�ͬ��URLConnection����Ķ�������URL��һ��http�����ʵ�ʷ��ص���HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // �������ӣ�����ʵ����get requestҪ����һ���connection.getInputStream()�����вŻ���������
    // ������
    connection.setRequestProperty("User-agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
    connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    connection.connect();
    // ȡ������������ʹ��Reader��ȡ
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encode));// ���ñ���,������������
    } catch (Exception e) {
    }
    if (reader == null) {
      LogService.msg("=============================Contents of no source end request:" + getURL + "=============================");
      connection.disconnect();
      return null;
    }

    String rs = "";
    String lines;
    int i = 0;
    while ((lines = reader.readLine()) != null) {
      // lines = new String(lines.getBytes(), "utf-8");
      rs += lines + "\n";
      i++;
      if (i > linesize) {
        break;
      }
    }
    reader.close();
    // �Ͽ�����
    connection.disconnect();
    LogService.msg("=============================Contents of end request:" + getURL + "=============================");

    return rs;
  }
}
