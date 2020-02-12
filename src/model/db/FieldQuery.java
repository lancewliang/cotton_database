package model.db;

import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import export.mapping.report.field.ReportObjectField;

public interface FieldQuery {
  public List<ReportObjectField> queryReportObjectFields(Commodity commodity, Country country, String source, String condition);

}
