package model.entity.production.country.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.production.country.AgricultureYieldWeek;
import model.entity.production.country.db.base.Base_AgricultureYieldWeekSQL;

public class AgricultureYieldWeekSQL extends Base_AgricultureYieldWeekSQL implements SaveDB {

  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<AgricultureYieldWeek> list = new ArrayList<AgricultureYieldWeek>();
    for (Record ob : objs) {
      list.add((AgricultureYieldWeek) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<AgricultureYieldWeek> list = new ArrayList<AgricultureYieldWeek>();
    for (Record ob : objs) {
      list.add((AgricultureYieldWeek) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
