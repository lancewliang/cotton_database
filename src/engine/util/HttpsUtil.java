package engine.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ant.restaurant.CustomizedHostnameVerifier;

public class HttpsUtil {
   

  public static String get(String url) {
    try {

      URL obj = new URL(url);
      HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();
      conn.setHostnameVerifier(new CustomizedHostnameVerifier());
      conn.setReadTimeout(5000);
      conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
      conn.addRequestProperty("User-Agent", "Mozilla");
      conn.addRequestProperty("Referer", "google.com");

      System.out.println("Request URL ... " + url);

      boolean redirect = false;

      // normally, 3xx is redirect
      int status = conn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        if (status == HttpURLConnection.HTTP_MOVED_TEMP || status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_SEE_OTHER)
          redirect = true;
      }

      System.out.println("Response Code ... " + status);

      if (redirect) {

        // get redirect url from "location" header field
        String newUrl = conn.getHeaderField("Location");

        // get the cookie if need, for login
        String cookies = conn.getHeaderField("Set-Cookie");

        // open the new connnection again
        conn = (HttpsURLConnection) new URL(newUrl).openConnection();
        conn.setRequestProperty("Cookie", cookies);
        conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");

        System.out.println("Redirect to URL : " + newUrl);

      }

      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      StringBuffer html = new StringBuffer();

      while ((inputLine = in.readLine()) != null) {
        html.append(inputLine);
      }
      in.close();

      // System.out.println("URL Content... \n" + html.toString());
      // System.out.println("Done");
      return html.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }

}
