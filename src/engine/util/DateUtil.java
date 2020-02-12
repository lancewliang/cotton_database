package engine.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtil {
  public static Map<Integer, String> dateformatmap1 = new HashMap<Integer, String>();
  static {
    dateformatmap1.put(1, "January");
    dateformatmap1.put(2, "February");
    dateformatmap1.put(3, "March");
    dateformatmap1.put(4, "April");
    dateformatmap1.put(5, "May");
    dateformatmap1.put(6, "June");
    dateformatmap1.put(7, "July");
    dateformatmap1.put(8, "August");
    dateformatmap1.put(9, "September");
    dateformatmap1.put(10, "October");
    dateformatmap1.put(11, "November");
    dateformatmap1.put(12, "December");
  }

  public static Map<String, Integer> dateformatmap2 = new HashMap<String, Integer>();
  static {
    dateformatmap2.put("Fabuary".toLowerCase(), 2);
    dateformatmap2.put("Sept.".toLowerCase(), 9);
    String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
    putMonthByEN(months);
  }

  private static void putMonthByEN(String[] month) {

    for (int i = 0; i < month.length; i++) {
      dateformatmap2.put(month[i].toLowerCase(), i + 1);
      if (month[i].length() > 3) {
        dateformatmap2.put(month[i].substring(0, 3).toLowerCase(), i + 1);
        dateformatmap2.put(month[i].substring(0, 3).toLowerCase() + ".", i + 1);
      }
    }
  }

  public static boolean hasMonthByEN(String month) {

    return dateformatmap2.get(month.toLowerCase()) != null;

  }

  public static int getMonthByEN(String month) {

    return dateformatmap2.get(month.toLowerCase());

  }

  public static String getENByMonth(int month) {
    return dateformatmap1.get(month);
  }

  public static long getYYYYMMDD(String str) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(str);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf2.format(calendar.getTime()));
  }
}
