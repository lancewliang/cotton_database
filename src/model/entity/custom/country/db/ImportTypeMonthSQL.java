package model.entity.custom.country.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.custom.country.ImportTypeMonth;
import model.entity.custom.country.db.base.Base_ImportTypeMonthSQL;

public class ImportTypeMonthSQL extends Base_ImportTypeMonthSQL implements SaveDB {
  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<ImportTypeMonth> list = new ArrayList<ImportTypeMonth>();
    for (Record ob : objs) {
      list.add((ImportTypeMonth) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<ImportTypeMonth> list = new ArrayList<ImportTypeMonth>();
    for (Record ob : objs) {
      list.add((ImportTypeMonth) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
