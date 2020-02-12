package ui.util;

public class Native2AsciiUtils {

  public static String ascii2native(String str) {

    String hex = "0123456789ABCDEF";

    StringBuffer buf = new StringBuffer();

    int ptn = 0;

    for (int i = 0; i < str.length(); i++) {

      char c = str.charAt(i);

      if (c == '\\' && i + 1 <= str.length() && str.charAt(i + 1) == '\\') {

        buf.append("\\\\");

        i += 1;

      } else if (c == '\\' && i + 6 <= str.length() && str.charAt(i + 1) == 'u') {

        String sub = str.substring(i + 2, i + 6).toUpperCase();

        int i0 = hex.indexOf(sub.charAt(0));

        int i1 = hex.indexOf(sub.charAt(1));

        int i2 = hex.indexOf(sub.charAt(2));

        int i3 = hex.indexOf(sub.charAt(3));

        if (i0 < 0 || i1 < 0 || i2 < 0 || i3 < 0) {

          buf.append("\\u");

          i += 1;

        } else {

          byte[] data = new byte[2];

          data[0] = i2b(i1 + i0 * 16);

          data[1] = i2b(i3 + i2 * 16);

          try {

            buf.append(new String(data, "UTF-16BE").toString());

          } catch (Exception ex) {

            buf.append("\\u" + sub);

          }

          i += 5;

        }

        // 怎加了对标点符号!判断，对其他标点没测试过

      } else if (c == '\\' && i + 1 <= str.length() && str.charAt(i + 1) == '!') {

        buf.append("!");

        i += 1;

      }

      else {

        buf.append(c);

      }

    }

    return buf.toString();

  }

  /**
   * 091 * unsigned integer to binary
   * 
   * 092 *
   * 
   * 093 *
   * 
   * 094 * @param i
   * 
   * 095 * unsigned integer
   * 
   * 096 * @return binary
   * 
   * 097
   */

  private static byte i2b(int i) {

    return (byte) ((i > 127) ? i - 256 : i);
  }
}
