package export.mapping.report.field;

import java.lang.reflect.Field;

import model.constant.Commodity;
import model.constant.Country;

import org.apache.poi.ss.usermodel.Cell;
import org.w3c.dom.Element;

import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import export.mapping.report.DimensionReportMappingDumper;
import export.mapping.report.ReportFieldMappingInfo;
import export.mapping.report.dimension.TimeDimension.Dimension;

public class ReportObjectField implements ReportField {
  public final static String TYPE = "model";
  public String col;
  public String value;
  public String country;
  public String commodity;
  public String model;
  public String condition;
  public String conditionlabel;
  public String label;
  public String source;

  public ReportObjectField(String col, String label, String model, String condition, String conditionlabel, String value, String commodity, String country, String source) {
    this.col = col;
    this.label = label;
    this.model = model;
    this.conditionlabel = conditionlabel;
    this.condition = condition;
    this.value = value;
    this.source = source;
    this.commodity = commodity;
    this.country = country;
  }

  public void getObjectAttr(Object obj, Cell cell) {
    if (obj == null)
      return;
    try {
      Class cc = obj.getClass();

      Field field = null;

      try {
        field = cc.getDeclaredField(None.isNonBlank(this.value) ? value : "value");
      } catch (NoSuchFieldException ee) {

      }
      if (field == null) {
        field = cc.getSuperclass().getDeclaredField(None.isNonBlank(this.value) ? value : "value");
      }
      field.setAccessible(true);
      Class c = field.getType();
      if (c.equals(long.class)) {
        long l = field.getLong(obj);
        if (l != DBUtil.NULLINT)
          cell.setCellValue(l);
      } else if (c.equals(double.class)) {
        double l = field.getDouble(obj);
        if (l != DBUtil.NULLDOUBLE)
          cell.setCellValue(l);
      } else if (c.equals(float.class)) {

        float l = field.getFloat(obj);
        if (l != DBUtil.NULLFLOAT)
          cell.setCellValue(l);
      } else if (c.equals(String.class)) {
        String s = field.get(obj).toString();
        cell.setCellValue(s);
      } else {
        cell.setCellValue(field.get(obj).toString());
      }
    } catch (Exception e) {
      LogService.trace(e, obj.toString());
    }

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

  public String getColDisplayLabel(Dimension time_dimension) {
    String ret = label + "\n";
    ret += addColDisplayLabel("国家", country);
   // ret += addColDisplayLabel("商品", commodity);
   // ret += addColDisplayLabel("字段项", ReportFieldMappingInfo.getValueName(time_dimension.toString().toLowerCase(), model, value));
    ret += addColDisplayLabel("数据源", source);
   // ret += addColDisplayLabel("额外数据::", conditionlabel);
    return ret;
  }

  private String addColDisplayLabel(String key, String attr) {
    if (None.isNonBlank(attr))
      return key + ":" + attr + "\n";
    return "";
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
    DimensionReportMappingDumper.setAttribute(fieldEle, "source", source);

    DimensionReportMappingDumper.setAttribute(fieldEle, "value", value);

    DimensionReportMappingDumper.setAttribute(fieldEle, "conditionlabel", conditionlabel);
    DimensionReportMappingDumper.setAttribute(fieldEle, "condition", condition);
    DimensionReportMappingDumper.setAttribute(fieldEle, "model", model);
    DimensionReportMappingDumper.setAttribute(fieldEle, "country", country);
    DimensionReportMappingDumper.setAttribute(fieldEle, "commodity", commodity);

    fieldEle.setAttribute("label", label);
  }

  public Country getCountry() {
    return Country.getCountry(this.country);
  }

  public Commodity getCommodity() {
    return Commodity.getCommodity(commodity);
  }
}
