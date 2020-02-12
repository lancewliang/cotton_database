package export.mapping.report.field;

import org.w3c.dom.Element;

import export.mapping.report.dimension.TimeDimension.Dimension;

public interface ReportField {
  public int getColIndex();

  public String getCol();

  public String getColDisplayLabel(Dimension timeDimension);

  public String getType();

  public void toElement(Element fieldEle);
}
