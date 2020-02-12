package datafeed.excell;

import model.db.InstructionStatus;
import tcc.batch.server.Instruction;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "FeedTaskStatus", alias = "bsm", tablespace = "ccdata")
public class FeedTaskStatus implements Instruction {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  String type;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(35)", primary = true)
  String filename;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(35)", primary = true)
  String sheetname;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  String status = InstructionStatus.STATUS_START;

  public FeedTaskStatus() {

  }

  public FeedTaskStatus(String type, String filename, String sheetname) {
    this.type = type;
    this.filename = filename;
    this.sheetname = sheetname;
  }

  public String getType() {
    return type;
  }

  public String toString() {
    return type + "|" + filename + "|" + sheetname;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getSheetname() {
    return sheetname;
  }

  public void setSheetname(String sheetname) {
    this.sheetname = sheetname;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

}
