package export.mapping.server;

import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import model.constant.Commodity;
import model.db.InstructionStatus;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import tcc.batch.server.Instruction;
import tcc.batch.server.InstructionExecutor;
import tcc.batch.server.ProcessingException;
import tcc.utils.log.LogService;
import ui.util.PoiUtil;
import export.mapping.db.ExportTaskStatusSQL;
import export.mapping.excell.ExportTaskStatus;
import export.mapping.excell.FormatExcell;
import export.mapping.excell.OutputReports;
import export.mapping.report.DimensionReportMapping;
import export.mapping.report.ReportMapping;
import export.mapping.report.ReportMappings;

public class Executor implements InstructionExecutor {

  @Override
  public void execute(Instruction arg0) throws ProcessingException {
    ExportTaskStatus status = (ExportTaskStatus) arg0;
    try {
      File outputFile = getOutFile(status);
      String reportType = null;
      if (ReportMappings.TYPE_COMMON.equals(status.type)) {
        reportType = ReportMappings.TYPE_COMMON;
      } else if (ReportMappings.TYPE_commodity.equals(status.type)) {
        reportType = status.getCommodity();
      } else {
        throw new Exception();
      }
      List<ReportMapping> mappings = ReportMappings.getReport(reportType, status.format);
      List<FormatExcell> ss = new ArrayList<FormatExcell>();

      for (ReportMapping _mapping : mappings) {
        DimensionReportMapping mapping = (DimensionReportMapping) _mapping;
        FormatExcell exec = new FormatExcell(mapping, status, Commodity.getCommodity(status.getCommodity()));
        Thread thread1 = new Thread(exec);
        thread1.start();
        ss.add(exec);

      }
      while (true) {
        boolean isEnd = true;
        for (FormatExcell sh : ss) {
          if (!sh.end) {
            isEnd = false;
          }
        }
        if (isEnd) {
          break;
        } else {
          try {
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
      ArrayList<FileInputStream> inList = new ArrayList<FileInputStream>();
      for (FormatExcell sh : ss) {
        inList.add(new FileInputStream(sh.outputFile));
      }
      XSSFWorkbook book = FormatExcell.createFile(outputFile, true);
      PoiUtil.mergeExcelFiles(book, inList);
      LogService.msg("mergeExcelFiles");
      FormatExcell.saveFile(outputFile, book);
      LogService.msg("save");

      status.status = InstructionStatus.STATUS_SUCCESS;
      for (FileInputStream in : inList) {
        in.close();
      }
      for (FormatExcell sh : ss) {
        sh.outputFile.delete();
      }
      LogService.msg("delete");
      status.status = InstructionStatus.STATUS_SUCCESS;
    } catch (Exception e) {
      LogService.trace(e, "");
      status.status = InstructionStatus.STATUS_FAILED;
    } finally {
      try {
        ExportTaskStatusSQL.save(status);
      } catch (SQLException e) {
        LogService.trace(e, "");
      }
    }

  }

  private File getOutFile(ExportTaskStatus status) {
    Commodity commodityObj = Commodity.getCommodity(status.commodity);
    return OutputReports.saveReport(commodityObj, status.format, status.date);

  }

  @Override
  public void init(Properties arg0) throws ProcessingException {

  }

}
