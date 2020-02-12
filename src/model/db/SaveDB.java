package model.db;

import java.sql.SQLException;
import java.util.List;

import model.constant.Commodity;
import model.entity.Record;

public interface SaveDB {
  public boolean save(List<Record> objs) throws SQLException;

  public boolean delete(List<Record> objs) throws SQLException;

  public boolean deleteAll(Commodity commodity) throws SQLException;

}
