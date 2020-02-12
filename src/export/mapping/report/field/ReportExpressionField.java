package export.mapping.report.field;

import org.apache.poi.ss.usermodel.Row;
import org.w3c.dom.Element;

import tcc.utils.StringUtil;
import export.mapping.report.dimension.TimeDimension.Dimension;

public class ReportExpressionField implements ReportField {
  public final static String TYPE = "expression";
  public String col;

  public String value;
  public String label;

  public ReportExpressionField(String col, String label, String value) {
    this.col = col;
    this.label = label;
    this.value = value;
  }

  public String getRTMValue(Row row) {
    String v = StringUtil.replaceString(value, "{$row+1}", "" + (row.getRowNum() + 2));

    v = StringUtil.replaceString(v, "{$row}", "" + (row.getRowNum() + 1));
    v = StringUtil.replaceString(v, "{$row-1}", "" + (row.getRowNum()));
    return v;
  }

  @Override
  public int getColIndex() {

    return FieldExcellUtil.map.get(col);
  }

  @Override
  public String getColDisplayLabel(Dimension time_dimension) {
    return label;
  }

  @Override
  public void toElement(Element fieldEle) {
    fieldEle.setAttribute("value", value);

    fieldEle.setAttribute("label", label);
  }

  @Override
  public String getCol() {

    return col;
  }

  @Override
  public String getType() {

    return TYPE;
  }
}
