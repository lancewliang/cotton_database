package ant.server;

import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import model.db.InstructionStatus;

import ant.server.db.AntInstructionSQL;

import tcc.batch.server.AbstractInstructionLoader;
import tcc.batch.server.Instruction;
import tcc.batch.server.ProcessingException;
import tcc.utils.log.LogService;

public class Loader extends AbstractInstructionLoader {

  @Override
  public void init(Properties arg0) throws ProcessingException {

  }

  @Override
  public void load(List<Instruction> arg0) throws ProcessingException {
    try {
      List<AntInstruction> sss = AntInstructionSQL.getStartedObjs();
      for (AntInstruction s : sss) {
        AntInstruction instion = AntManger.getAnters(s.getName());
        if (instion != null) {
          instion.setStatus(InstructionStatus.STATUS_PROCESS);
          AntInstructionSQL.update(instion);
          arg0.add(instion);

        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  @Override
  public void reset() throws ProcessingException {
    try {
      LogService.msg("do reset");
      AntInstructionSQL.reset();
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

}
