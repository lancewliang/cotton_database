package export.mapping.report.field;


public class Chart {
  public String value;

  // xChart:xlLineMarkers:B,C,D
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getOutputValue() {
    String[] vs = value.split(":");
    String[] vs2 = vs[2].split(",");
    String ret = "xChart:" + vs[1] + ":";
    int x = 0;
    for (String s : vs2) {
      if (x > 0) {

        ret += ",";
      }
      ret += (FieldExcellUtil.map.get(s)+1);
      x++;
    }
    return ret;
  }
}
