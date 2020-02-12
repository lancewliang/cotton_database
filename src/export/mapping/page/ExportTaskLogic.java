package export.mapping.page;

import java.util.List;

import model.constant.Commodity;
import tcc.utils.date.WMDate;
import tcc.utils.log.LogService;
import export.mapping.db.ExportTaskStatusSQL;
import export.mapping.excell.ExportTaskStatus;
import export.mapping.report.ReportMappings;

public class ExportTaskLogic {
  public static void addTasks(String type, Commodity commodityObj) {

    List<String> c_list = ReportMappings.getReports(type);
    for (String format : c_list) {
      addTask(type, format, commodityObj);
    }
  }

  public static void addTask(String type, String format, Commodity commodityObj) {
    WMDate date = new WMDate();
    try {
      ExportTaskStatus task = new ExportTaskStatus(type, commodityObj.getCommodity(), format, date.getYear() + "_" + (date.getMonth()+1) + "_" + date.getDate());
      ExportTaskStatusSQL.save(task);
    } catch (Exception e) {
      LogService.trace(e, "");
    }
  }
}
