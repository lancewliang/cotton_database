package ant.server;

import java.sql.SQLException;
import java.util.Properties;

import tcc.batch.server.Instruction;
import tcc.batch.server.ProcessingException;
import tcc.batch.server.TimerEntity;
import tcc.batch.server.TimerExecutor;
import ant.server.db.AntInstructionSQL;

public class TimerExec implements TimerExecutor {
  boolean v = false;

  @Override
  public void init(Properties arg0) throws ProcessingException {
    // TODO Auto-generated method stub

  }

  @Override
  public void execute(TimerEntity arg0) throws ProcessingException {
    // TODO Auto-generated method stub
    if (v) {
      try {
        AntInstructionSQL.sch();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      v = true;
    }
  }
}
