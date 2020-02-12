package export.mapping.excell;

import model.db.InstructionStatus;
import tcc.batch.server.Instruction;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "ExportTaskStatus", alias = "bsm", tablespace = "ccdata")
public class ExportTaskStatus implements Instruction {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  public String type;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  public String commodity;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  public String format;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  public String date;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  public String status = InstructionStatus.STATUS_START;

  public ExportTaskStatus() {
  }

  public ExportTaskStatus(String type, String commodity, String format, String date) throws Exception {
    if (type == null)
      throw new Exception();
    this.type = type;
    this.commodity = commodity;
    this.format = format;
    this.date = date;

  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCommodity() {
    return commodity;
  }

  public void setCommodity(String commodity) {
    this.commodity = commodity;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
