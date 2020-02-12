package export.mapping.report.dimension;

import java.util.ArrayList;
import java.util.List;

import tcc.utils.log.LogService;

import model.constant.Commodity;
import model.constant.Country;
import model.db.SQLFactory;
import export.mapping.report.field.FieldExcellUtil;
import export.mapping.report.field.ReportObjectField;

public class ModeDimension {
  public String value;

  public String model;
  public String condition;
  public String conditionlabel;
  public String label;
  public String source;
  public String key;

  public ModeDimension(String key, String label, String model, String condition, String conditionlabel, String value, String source) {
    this.key = key;
    this.label = label;
    this.model = model;
    this.conditionlabel = conditionlabel;
    this.condition = condition;
    this.value = value;
    this.source = source;
  }

  public List<ReportObjectField> getReportObjectFields(String col) {
    int i = FieldExcellUtil.map.get(col);
    for (ReportObjectField field : fields) {
      try {
        String d = FieldExcellUtil.map2.get(i);
        if (d == null) {
          LogService.err("getReportObjectFields   getReportObjectFields no col");
        }
        field.col = d;
        i++;
      } catch (Exception e) {
        LogService.err("no col");
      }
    }
    return fields;
  }

  List<ReportObjectField> fields = new ArrayList<ReportObjectField>();

  public void initFields(Commodity fCommodity, Country fCountry) {
    List<ReportObjectField> fff = SQLFactory.getFieldQuery(model).queryReportObjectFields(fCommodity, fCountry, source, condition);
    for (ReportObjectField field : fff) {
      field.label += label;
      field.value = value;
      field.model = model;
    }
    fields.addAll(fff);
  }
}
