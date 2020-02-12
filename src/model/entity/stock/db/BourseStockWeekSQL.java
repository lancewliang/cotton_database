package model.entity.stock.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.stock.BourseStockWeek;
import model.entity.stock.db.base.Base_BourseStockWeekSQL;

public class BourseStockWeekSQL extends Base_BourseStockWeekSQL implements SaveDB {
  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<BourseStockWeek> list = new ArrayList<BourseStockWeek>();
    for (Record ob : objs) {
      list.add((BourseStockWeek) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<BourseStockWeek> list = new ArrayList<BourseStockWeek>();
    for (Record ob : objs) {
      list.add((BourseStockWeek) ob);
    }
    return super.delete(list);
  }
  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {
    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }

}
