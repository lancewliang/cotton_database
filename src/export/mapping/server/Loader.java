package export.mapping.server;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import model.db.InstructionStatus;

import export.mapping.db.ExportTaskStatusSQL;
import export.mapping.excell.ExportTaskStatus;

import tcc.batch.server.AbstractInstructionLoader;
import tcc.batch.server.Instruction;
import tcc.batch.server.ProcessingException;
import tcc.utils.log.LogService;

public class Loader extends AbstractInstructionLoader {

  @Override
  public void init(Properties arg0) throws ProcessingException {

    try {
      ExportTaskStatusSQL.reset();
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

  @Override
  public void load(List<Instruction> arg0) throws ProcessingException {

    try {
      List<ExportTaskStatus> set = ExportTaskStatusSQL.getObjs(InstructionStatus.STATUS_START);
      for (ExportTaskStatus status : set) {
        status.status = InstructionStatus.STATUS_PROCESS;
        ExportTaskStatusSQL.save(status);
      }
      arg0.addAll(set);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }
}
