package ant.server;

import java.sql.SQLException;
import java.util.Properties;

import model.db.InstructionStatus;
import tcc.batch.server.Instruction;
import tcc.batch.server.InstructionExecutor;
import tcc.batch.server.ProcessingException;
import tcc.utils.log.LogService;
import ant.server.db.AntInstructionSQL;

public class Executor implements InstructionExecutor {

  @Override
  public void execute(Instruction arg0) throws ProcessingException {
    AntInstruction anters = (AntInstruction) arg0;
    String log = "";
    try {

      for (DayAnt ant : anters.anters) {
        log += ant.getClass().getSimpleName() + ",";
        anters.setLog(log);
        AntInstructionSQL.update(anters);
        ant.doAnt();

      }
      anters.setStatus(InstructionStatus.STATUS_SUCCESS);
      AntInstructionSQL.update(anters);
    } catch (Exception e) {
      anters.setStatus(InstructionStatus.STATUS_FAILED);
      try {
        AntInstructionSQL.update(anters);
      } catch (SQLException e1) {
        LogService.trace(e1, null);
      }
      LogService.trace(e, null);

    }
  }

  @Override
  public void init(Properties arg0) throws ProcessingException {
    // TODO Auto-generated method stub

  }

}
