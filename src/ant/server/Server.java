package ant.server;

import java.sql.SQLException;

import model.db.InstructionStatus;
import tcc.utils.log.LogService;
import ant.server.db.AntInstructionSQL;
import engine.util.SetENVUtil;

public class Server {

  /**
   * @param args
   * @throws SQLException
   */
  public static void main(String[] args) throws SQLException {
    SetENVUtil.setENV();

    tcc.batch.server.Bootstrap.main(new String[] { "D:\\lwwork\\ExamKing\\economics3\\src\\ant\\server\\site-server.xml" });
    LogService.msg("do AntInstruction");
    for (AntInstruction instracution : AntManger.getAnters()) {
      AntInstruction instracutionStatus = AntInstructionSQL.getObjByName(instracution.getName());
      if (instracutionStatus == null) {
        instracutionStatus = AntManger.getAnters(instracution.getName());
        AntInstructionSQL.insert(instracutionStatus);
      } else {
        instracutionStatus.setStatus(InstructionStatus.STATUS_START);
        AntInstructionSQL.update(instracutionStatus);
      }

    }
  }
}
