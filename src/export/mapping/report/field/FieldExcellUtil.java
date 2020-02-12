package export.mapping.report.field;

import java.util.HashMap;

public class FieldExcellUtil {
  public static HashMap<String, Integer> map = new HashMap<String, Integer>();
  public static HashMap<Integer, String> map2 = new HashMap<Integer, String>();
  static {

    String[] str = new String[] { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", };
    int i = 0;
    for (String s : str) {
      map.put(s, i);
      map2.put(i, s);
      i++;
    }
    for (String s : str) {
      map.put("A" + s, i);
      map2.put(i, "A" + s);
      i++;
    }
    for (String s : str) {
      map.put("B" + s, i);
      map2.put(i, "B" + s);
      i++;
    }
    for (String s : str) {
      map.put("C" + s, i);
      map2.put(i, "C" + s);
      i++;
    }
    for (String s : str) {
      map.put("D" + s, i);
      map2.put(i, "D" + s);
      i++;
    }
    for (String s : str) {
      map.put("E" + s, i);
      map2.put(i, "E" + s);
      i++;
    }
    for (String s : str) {
      map.put("F" + s, i);
      map2.put(i, "F" + s);
      i++;
    }
    for (String s : str) {
      map.put("G" + s, i);
      map2.put(i, "G" + s);
      i++;
    }
    for (String s : str) {
      map.put("H" + s, i);
      map2.put(i, "H" + s);
      i++;
    }
    for (String s : str) {
      map.put("I" + s, i);
      map2.put(i, "I" + s);
      i++;
    }
    for (String s : str) {
      map.put("J" + s, i);
      map2.put(i, "J" + s);
      i++;
    }
  }
}
