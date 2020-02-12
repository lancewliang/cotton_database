package export.mapping.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.db.QueryUnit;
import ant.server.DayAnt;
import export.mapping.report.dimension.CommodityDimension;
import export.mapping.report.dimension.CountryDimension;
import export.mapping.report.dimension.ModeDimension;
import export.mapping.report.dimension.TimeDimension;
import export.mapping.report.field.Chart;
import export.mapping.report.field.ReportField;

public class DimensionReportMapping implements ReportMapping {
  public CountryDimension countryDimension = null;
  public TimeDimension timeDimension = null;
  public CommodityDimension commodityDimension = null;
  public List<ReportField> fileds = new ArrayList<ReportField>();
  public List<Chart> charts = new ArrayList<Chart>();

  public Map<String, ModeDimension> modeDimensionMap = new HashMap<String, ModeDimension>();
  public String label;

  public QueryUnit unit = null;
  public DayAnt ant = null;

  public DimensionReportMapping(String label, TimeDimension timeDimension, QueryUnit unit) {
    this.label = label;
    if (unit != null) {
      this.unit = unit;
    } else {
      this.unit = new QueryUnit();
    }
    this.timeDimension = timeDimension;
  }
}
