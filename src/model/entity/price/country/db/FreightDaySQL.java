package model.entity.price.country.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.price.country.FreightDay;
import model.entity.price.country.FreightDay;
import model.entity.price.country.db.base.Base_FreightDaySQL;

public class FreightDaySQL extends Base_FreightDaySQL implements SaveDB {
  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<FreightDay> list = new ArrayList<FreightDay>();
    for (Record ob : objs) {
      list.add((FreightDay) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<FreightDay> list = new ArrayList<FreightDay>();
    for (Record ob : objs) {
      list.add((FreightDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(null, super.SQL_TABLE);
  }
}
