package model.entity.gov.country.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.gov.country.GovBatch;
import model.entity.gov.country.db.base.Base_GovBatchSQL;
import model.entity.price.country.CountryPriceDay;

public class GovBatchSQL extends Base_GovBatchSQL implements SaveDB {
  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<GovBatch> list = new ArrayList<GovBatch>();
    for (Record ob : objs) {
      list.add((GovBatch) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<GovBatch> list = new ArrayList<GovBatch>();
    for (Record ob : objs) {
      list.add((GovBatch) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
