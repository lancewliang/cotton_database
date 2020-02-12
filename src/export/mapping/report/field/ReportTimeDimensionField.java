package export.mapping.report.field;

import org.w3c.dom.Element;

import export.mapping.report.dimension.TimeDimension.Dimension;

public class ReportTimeDimensionField implements ReportField {
  public final static String TYPE = "time-dimension";
  public String col;

  public String label;

  public ReportTimeDimensionField(String col, String label) {
    this.col = col;
    this.label = label;
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
  public String getCol() {
    return col;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  @Override
  public void toElement(Element fieldEle) {
    fieldEle.setAttribute("label", label);
  }
}
