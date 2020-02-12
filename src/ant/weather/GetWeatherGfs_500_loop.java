package ant.weather;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tcc.utils.file.FileUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.DayAnt;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetWeatherGfs_500_loop implements DayAnt {

  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    GetWeatherGfs_500_loop exp = new GetWeatherGfs_500_loop();
    exp.doAnt();

  }

  @Override
  public void doAnt() {
    getExport();
  }

  // http://dgciskol.gov.in/data_information.asp
  public void getExport() {
    String href = "http://50.206.172.197/gfs/loop/gfs_500_loop.gif";

    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(System.currentTimeMillis());
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd-HH");

    String currentMonth = sdf2.format(cal.getTime());

    File file = AntLogic.getFile(getSource() + "/gfs_500_loop", currentMonth + ".gif");

    try {
      URL url = new URL(href);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(5000);
      connection.setRequestMethod("GET");

      // connection.setRequestProperty("Accept-Encoding", "identity");
      connection.connect();

      int fileSize = connection.getContentLength();
      System.out.println(connection.getResponseCode());
      System.out.println(fileSize);
      connection.disconnect();
      if (file.length() < fileSize) {
        download(0, file.length(), fileSize, href, file);
      }
      // while (true && len >= 0) {
      // if (!file.exists()) {
      // getFile(href, file, 12000);
      // } else {
      // if (file.length() >= len) {
      // break;
      // } else {
      // getFile(href, file, file.length() + 12000);
      // }
      // }
      // }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  @Override
  public String getSource() {

    return "weather.unisys.com";
  }

  public static long getLength(String getURL, File file) throws IOException {

    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
    // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
    // 服务器
    InputStream input = null;
    try {
      connection.setRequestProperty("Cache-Control", "max-age=0");
      connection.setRequestProperty("Connection", "keep-alive");
      connection.connect();
      input = connection.getInputStream();
      int i = connection.getResponseCode();

      String s = connection.getResponseMessage();
      long l = connection.getContentLength();
      return l;

    } catch (Exception e) {
      e.printStackTrace();
      LogService.msg("=============================Contents of IOException request:" + getURL + "=============================");
    } finally {
      LogService.msg("=============================Contents of end request :" + getURL + "=============================");
      if (input != null)
        input.close();
      if (connection != null)
        connection.disconnect();
    }
    return -1;
  }

  public static void getFile(String getURL, File file, long size1, long size2) throws IOException {

    LogService.msg("=============================Contents of get request :" + getURL + "=============================");
    URL getUrl = new URL(getURL);
    // 根据拼凑的URL，打开连接，URL.openConnection函数会根据URL的类型，
    // 返回不同的URLConnection子类的对象，这里URL是一个http，因此实际返回的是HttpURLConnection
    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
    // 进行连接，但是实际上get request要在下一句的connection.getInputStream()函数中才会真正发到
    // 服务器
    InputStream input = null;
    try {

      // connection.setRequestProperty("Accept",
      // "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
      // connection.setRequestProperty("User-Agent",
      // "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.93 Safari/537.36");
      // connection.setRequestProperty("If-Range", "4081be-147705-e18abc40");
      // connection.setRequestProperty("Cache-Control", "max-age=0");
      connection.setRequestProperty("Range", "bytes=" + size1 + "-" + size2);
      connection.setRequestProperty("Connection", "keep-alive");
      connection.connect();
      input = connection.getInputStream();
      int i = connection.getResponseCode();

      String s = connection.getResponseMessage();
      int l = connection.getContentLength();
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
      LogService.msg("=============================Contents of end request :" + getURL + "=============================");
      if (input != null)
        input.close();
      if (connection != null)
        connection.disconnect();
    }
  }

  private static void download(int startPos, long compeleteSize, long endPos, String urlstr, File f) {
    HttpURLConnection connection = null;
    RandomAccessFile randomAccessFile = null;
    InputStream is = null;
    boolean doagain = false;
    try {
      System.out.println("compeleteSize:" + compeleteSize);
      URL url = new URL(urlstr);
      connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(10000);
      connection.setRequestMethod("GET");
      // 设置范围，格式为Range：bytes x-y;
      connection.setRequestProperty("Range", "bytes=" + (startPos + compeleteSize) + "-" + endPos);

      randomAccessFile = new RandomAccessFile(f, "rwd");
      randomAccessFile.seek(startPos + compeleteSize);
      // 将要下载的文件写到保存在保存路径下的文件中
      is = connection.getInputStream();
      byte[] buffer = new byte[4096];
      int length = -1;
      while ((length = is.read(buffer)) != -1) {
        randomAccessFile.write(buffer, 0, length);
        compeleteSize += length;

      }
      System.out.println("OVER:" + compeleteSize);
    } catch (SocketTimeoutException x) {
      System.out.println("timeout goon :" + compeleteSize);
      doagain = true;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (is != null)
          is.close();
        if (randomAccessFile != null)
          randomAccessFile.close();
        if (connection != null)
          connection.disconnect();
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (doagain) {
        download(startPos, f.length(), endPos, urlstr, f);
        doagain = false;
      }
    }
  }
}
