package ant.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.db.InstructionStatus;
import tcc.batch.server.Instruction;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "AntInstruction", alias = "bsm", tablespace = "ccdata")
public class AntInstruction implements Instruction {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(35)", primary = true)
  String name;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  String status = InstructionStatus.STATUS_START;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(255)")
  String log = null;
  @DB_FIELD(type = DB_FIELD.TYPE_DATE)
  Date updateAt = null;

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public List<DayAnt> getAnters() {
    return anters;
  }

  public void setAnters(List<DayAnt> anters) {
    this.anters = anters;
  }

  public List<DayAnt> anters = new ArrayList<DayAnt>();

  public AntInstruction() {

  }

  public AntInstruction(String name, List<DayAnt> _anters) {
    this.name = name;
    anters.addAll(_anters);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getUpdateAt() {
    return updateAt;
  }

  public void setUpdateAt(Date updateAt) {
    this.updateAt = updateAt;
  }

  public boolean isWorking() {
    if (status.equals(InstructionStatus.STATUS_START) || status.equals(InstructionStatus.STATUS_PROCESS)) {
      return true;
    } else {
      return false;
    }
  }
}
