package export.mapping.page;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.db.QueryUnit;
import tcc.utils.None;
import tcc.utils.json.JSONArray;
import tcc.utils.json.JSONObject;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;
import export.mapping.report.DimensionReportMapping;
import export.mapping.report.ReportMappings;
import export.mapping.report.dimension.CommodityDimension;
import export.mapping.report.dimension.CountryDimension;
import export.mapping.report.dimension.TimeDimension;
import export.mapping.report.field.ReportExpressionField;
import export.mapping.report.field.ReportField;
import export.mapping.report.field.ReportObjectField;
import export.mapping.report.field.ReportTimeDimensionField;

public class CreateAndSaveFormat extends Page {
  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);

    if ("createFormat".equals(command)) {
      String data = request.getParameter("data");

      JSONObject obj;
      try {
        obj = new JSONObject(data);
        String reportType = obj.getString("reportType");
        QueryUnit unit = new QueryUnit(obj.getString("weightUnit"), obj.getString("lengthUnit"), obj.getString("priceUnit"));
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date reportStartDate = null;
        Date reportEndDate = null;
        if (None.isNonBlank(obj.getString("reportStartDate"))) {
          reportStartDate = dateformat.parse(obj.getString("reportStartDate"));
        }

        if (None.isNonBlank(obj.getString("reportEndDate"))) {
          reportEndDate = dateformat.parse(obj.getString("reportEndDate"));
        }
        String time_dimensionSTR = obj.getString("time_dimension");
        TimeDimension timeDimension = null;

        timeDimension = new TimeDimension(time_dimensionSTR, reportStartDate, reportEndDate);
        DimensionReportMapping reportMapping = new DimensionReportMapping(obj.getString("reportLabel").trim(), timeDimension, unit);

        if (obj.has("commodity_dimension") && None.isNonBlank(obj.getString("commodity_dimension"))) {
          reportMapping.commodityDimension = new CommodityDimension(obj.getString("commodity_dimension"));
        }
        if (obj.has("country_dimension") && None.isNonBlank(obj.getString("country_dimension"))) {
          reportMapping.countryDimension = new CountryDimension(obj.getString("country_dimension"));
        }

        JSONArray cols = obj.getJSONArray("cols");
        for (int i = 0; i < cols.length(); i++) {
          JSONObject colObj = cols.getJSONObject(i);
          String type = colObj.getString("type");
          String label = colObj.getString("label");
          String col = colObj.getString("col");
          ReportField field = null;
          if (ReportTimeDimensionField.TYPE.equals(type)) {
            field = new ReportTimeDimensionField(col, label);
          } else if (ReportExpressionField.TYPE.equals(type)) {
            String value = colObj.getString("value");
            field = new ReportExpressionField(col, label, value);
          } else if (ReportObjectField.TYPE.equals(type)) {

            String model = colObj.getString("model");

            String value = null;
            if (colObj.has("value")) {
              value = colObj.getString("value");
            }
            String condition = null;
            if (colObj.has("condition")) {
              condition = colObj.getString("condition");
            }
            String conditionlabel = null;
            if (colObj.has("condition_label")) {
              conditionlabel = colObj.getString("condition_label");
            }

            String source = null;
            if (colObj.has("source")) {
              source = colObj.getString("source");
            }
            String commoditySTR = null;
            if (colObj.has("commodity") && None.isNonBlank(colObj.getString("commodity"))) {
              commoditySTR = colObj.getString("commodity");

            }
            String countrySTR = null;
            if (colObj.has("country") && None.isNonBlank(colObj.getString("country"))) {
              countrySTR = colObj.getString("country");
            }

            field = new ReportObjectField(col, label, model, condition, conditionlabel, value, commoditySTR, countrySTR, source);
          }
          if (field != null)
            reportMapping.fileds.add(field);
        }
        ReportMappings.saveReport(reportType, reportMapping);

        session.setPageSessionValue(pfx + ".rs", "true");
      } catch (Exception e) {
        LogService.trace(e, null);

      }
    }

    return super.processCommand(command, session, request, response);
  }
}
