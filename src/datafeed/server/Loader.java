package datafeed.server;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import model.db.InstructionStatus;

import tcc.batch.server.AbstractInstructionLoader;
import tcc.batch.server.Instruction;
import tcc.batch.server.ProcessingException;
import tcc.utils.log.LogService;
import datafeed.db.DataFeedSQL;
import datafeed.excell.ExcellFeed;
import datafeed.excell.FeedTaskStatus;
import datafeed.page.FeedLogic;

public class Loader extends AbstractInstructionLoader {

  @Override
  public void init(Properties arg0) throws ProcessingException {
    try {
      DataFeedSQL.reset();
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

  @Override
  public void load(List<Instruction> arg0) throws ProcessingException {
    try {
      List<FeedTaskStatus> ret = DataFeedSQL.getStartedObjs();

      for (FeedTaskStatus task : ret) {
        task.setStatus(InstructionStatus.STATUS_PROCESS);
        LogService.msg("STATUS_PROCESS:" + task);
        DataFeedSQL.save(task);
      }
      arg0.addAll(ret);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

}
