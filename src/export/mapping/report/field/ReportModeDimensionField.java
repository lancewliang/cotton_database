package export.mapping.report.field;

import org.w3c.dom.Element;

import tcc.utils.log.LogService;
import export.mapping.report.dimension.TimeDimension.Dimension;

public class ReportModeDimensionField implements ReportField {
  public final static String TYPE = "model-dimension";
  public String col;
  public String model;

  public ReportModeDimensionField(String model, String col) {

    this.model = model;
    this.col = col;
  }

  @Override
  public int getColIndex() {

    try {
      return FieldExcellUtil.map.get(col);
    } catch (Exception e) {
      LogService.err("no col");
    }
    return -1;
  }

  @Override
  public String getCol() {
    // TODO Auto-generated method stub
    return col;
  }

  @Override
  public String getColDisplayLabel(Dimension timeDimension) {
    return null;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public String getModel() {
    return model;
  }

  @Override
  public void toElement(Element fieldEle) {
    // TODO Auto-generated method stub

  }

}
