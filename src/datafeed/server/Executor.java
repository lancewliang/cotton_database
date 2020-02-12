package datafeed.server;

import java.io.File;
import java.sql.SQLException;
import java.util.Properties;

import model.db.InstructionStatus;
import tcc.batch.server.Instruction;
import tcc.batch.server.InstructionExecutor;
import tcc.batch.server.ProcessingException;
import tcc.utils.log.LogService;
import datafeed.db.DataFeedSQL;
import datafeed.excell.ExcellFeed;
import datafeed.excell.FeedTaskStatus;
import datafeed.page.FeedLogic;

public class Executor implements InstructionExecutor {

  @Override
  public void execute(Instruction arg0) throws ProcessingException {
    FeedTaskStatus task = (FeedTaskStatus) arg0;
    ExcellFeed feed = FeedLogic.getFeed(task);

    try {
      
      feed.parseTable();
      feed.saveDatas();
      task.setStatus(InstructionStatus.STATUS_SUCCESS);
      LogService.msg("STATUS_SUCCESS:"+task);
    } catch (Exception e) {
      task.setStatus(InstructionStatus.STATUS_FAILED);
      LogService.trace(e, "excel:" + feed.excel.getAbsolutePath() + "sheet:" + feed.sheet.getSheetName());

    } finally {
      try {
        DataFeedSQL.save(task);
      } catch (SQLException e1) {
        LogService.trace(e1, "excel:" + feed.excel.getAbsolutePath());

      }
    }
  }

  @Override
  public void init(Properties arg0) throws ProcessingException {
    // TODO Auto-generated method stub

  }

}
