package export.mapping.page;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;
import model.db.QuerySQLUtil;
import model.db.QueryTable;
import model.db.SQLFactory;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;
import tutami.fw.DB;

public class CreateReportFormatQueryCondition extends Page {

  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);
    String country = request.getParameter("country");
    String commodity = request.getParameter("commodity");
    String model_class = request.getParameter("model_class");
    String queryColValue = request.getParameter("queryColValue");
    String countrycondition = request.getParameter("countrycondition");
    List<QueryConditionField> qcfFields = new ArrayList<QueryConditionField>();
    QueryField qf = new QueryField(model_class, queryColValue);

    Enumeration<String> paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements()) {
      String name = paramNames.nextElement();
      if (name.startsWith("ConditionsFORM")) {
        String _name = name.substring("ConditionsFORM.".length());
        QueryConditionField qcf = new QueryConditionField();
        qcf.columName = _name;
        qcf.value = request.getParameter(name);
        qcfFields.add(qcf);
      }
    }
    List<QueryValueField> list = queryDB(countrycondition, country, commodity, model_class, qf, qcfFields);

    String str = "";
    for (QueryValueField ff : list) {
      str += "<option value=\"" + ff.value + "\">" + ff.display + "</option>";
    }
    session.setPageSessionValue(pfx + ".optionstr", str);
    return super.processCommand(command, session, request, response);
  }

  public class QueryField {
    String columName;
    Class colType;

    public QueryField(String model_class, String columName) {
      this.columName = columName;
      try {
        Class classC = Class.forName(model_class);
        Field field = null;
        try {
          field = classC.getDeclaredField(columName);
        } catch (Exception e) {
          // TODO Auto-generated catch block

          field = classC.getSuperclass().getDeclaredField(columName);
        }
        if (field != null)
          colType = field.getType();
        else
          throw new Exception();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public class QueryConditionField {
    String columName;
    String value;
  }

  public class QueryValueField {
    String display;
    String value;
  }

  /**
   * code auto write. ready only file. don't change any code.
   */
  protected static javax.sql.DataSource TTDB;
  static {
    TTDB = DB.getDBPoolADMIN();
  }

  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }

  private String getQueryConditionFieldSQL(List<QueryConditionField> qcfFields) {
    String sql = "";
    for (QueryConditionField a : qcfFields) {
      if (None.isNonBlank(sql)) {
        sql += " and ";
      }

      sql += a.columName + "='" + a.value + "' ";
    }
    return sql;
  }

  private String getDisplay(Class colType, String value) {
    Constant c = ConstantBase.getConstant(colType, value);
    if (c != null) {
      return c.getDisplay();
    } else {
      return value;
    }

  }

  public List<QueryValueField> queryDB(String countrycondition, String country, String commodity, String model_class, QueryField qf, List<QueryConditionField> qcfFields) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<QueryValueField> list = new ArrayList<QueryValueField>();
    try {
      String condition = getQueryConditionFieldSQL(qcfFields);
      conn = getConnection();
      String D_SQL1 = "select distinct " + qf.columName;
      QueryTable qDB = SQLFactory.getQueryTable(model_class);
      String[] tables = qDB.getTables();
      String D_SQL = "";
      for (String table : tables) {
        if (None.isNonBlank(D_SQL)) {
          D_SQL += " union  ";
        }
        D_SQL += " ( select " + qf.columName + " from " + table + " where 1=1 ";
        if (qDB.hasCommodity()) {
          if (None.isNonBlank(countrycondition)) {
            D_SQL += " and " + countrycondition + "='" + Country.getCountry(country).getCountry() + "' ";
            D_SQL += QuerySQLUtil.QuerySQLString(Commodity.getCommodity(commodity), null, condition);
          } else {
            D_SQL += QuerySQLUtil.QuerySQLString(Commodity.getCommodity(commodity), Country.getCountry(country), null, condition);
          }
        }
        D_SQL += " )";
      }
      D_SQL1 += " from (" + D_SQL + ") x";

      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL1);

      ps = conn.prepareStatement(D_SQL1);

      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        int col = 0;
        QueryValueField qvf = new QueryValueField();
        qvf.value = rs.getString(++col);
        qvf.display = getDisplay(qf.colType, qvf.value);

        list.add(qvf);
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return list;
  }
}
