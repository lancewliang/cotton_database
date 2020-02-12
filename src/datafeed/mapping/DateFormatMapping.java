package datafeed.mapping;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateFormatMapping {
  private static Map<String, String> dateformatmap = new HashMap<String, String>();
  static {
    dateformatmap.put("��ʼ��", "yyyyMM");
    dateformatmap.put("������", "yyyyMM");
    dateformatmap.put("��", "yyyyMM");
    dateformatmap.put("�·�", "yyyyMM");
    dateformatmap.put("��", "yyyyMMdd");
    dateformatmap.put("��", "yyyyMMdd");
    dateformatmap.put("����", "yyyyMMdd");
  }

  public static String getCYCLEDateFormat(String fieldKey) throws Exception {
    String r = dateformatmap.get(fieldKey);
    if (r == null) {
      throw new Exception("no date format :" + fieldKey);
    }
    return r;
  }

  public static String convertCYCLEfromUCDate(String valueString, String pattern) {

    Date d = new Date(Long.parseLong(valueString));
    DateFormatSymbols symbols = new DateFormatSymbols(_lc);
    Calendar cal = Calendar.getInstance(_tz, _lc);
    SimpleDateFormat df = new SimpleDateFormat(pattern, symbols);
    df.setCalendar(cal);

    return df.format(d);
  }

  static TimeZone _tz = TimeZone.getDefault();
  static Locale _lc = Locale.getDefault();
}
