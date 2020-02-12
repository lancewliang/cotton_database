package tutami.fw;


import javax.sql.DataSource;

import tcc.utils.db.DataSourceManager;
import tcc.utils.property.PropertyManager;

public class DB {
  private static DataSource ccadminDB = null;

  static {
    ccadminDB = DataSourceManager.getDataSource(getDBPoolNameCCADMIN());

  }

  public static String getDBPoolNameCCADMIN() {
    return PropertyManager.getString("tutami.db.pool", "ccPool");
  }

  public static javax.sql.DataSource getDBPoolADMIN() {
    return ccadminDB;
  }

}
