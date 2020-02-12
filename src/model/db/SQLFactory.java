package model.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tcc.utils.None;
import tcc.utils.xml.dom.DOMUtil;
import tcc.utils.xml.xpath.XmlOperator;

public class SQLFactory {
  static HashMap<String, DaylyQuery> daylyQuerys = new HashMap<String, DaylyQuery>();
  static HashMap<String, YearlyQuery> yearlyQuerys = new HashMap<String, YearlyQuery>();
  static HashMap<String, MonthlyQuery> monthlyQuerys = new HashMap<String, MonthlyQuery>();
  static HashMap<String, SaveDB> saveDBs = new HashMap<String, SaveDB>();
  static HashMap<String, QueryTable> queryTables = new HashMap<String, QueryTable>();
  static HashMap<String, FieldQuery> queryFields = new HashMap<String, FieldQuery>();

  static {
    try {
      Document doc = DOMUtil.inputStreamDoc(SQLFactory.class.getResourceAsStream("model-query-mapping.xml"));
      NodeList models = XmlOperator.selectNodeList(doc, "//models");
      for (int i = 0; i < models.getLength(); i++) {
        Element ele = (Element) models.item(i);
        String time_dimension = ele.getAttribute("time-dimension");
        NodeList modelEls = XmlOperator.selectNodeList(ele, "model");
        for (int f = 0; f < modelEls.getLength(); f++) {
          Element modelEl = (Element) modelEls.item(f);
          String modelObjClassStr = modelEl.getAttribute("model-class");
          String queryObjClassStr = modelEl.getAttribute("query-class");
          String saveObjClassStr = modelEl.getAttribute("save-class");
          Class modelObjClass = Class.forName(modelObjClassStr);

          if (None.isNonBlank(queryObjClassStr)) {
            Class queryObjClass = Class.forName(queryObjClassStr);
            Object queryObjInstance = queryObjClass.newInstance();
            if ("year".equals(time_dimension)) {
              addYearlyQueryClass(modelObjClass, (YearlyQuery) queryObjInstance);
            } else if ("month".equals(time_dimension)) {
              addMonthlyQueryClass(modelObjClass, (MonthlyQuery) queryObjInstance);
            } else if ("day".equals(time_dimension)) {
              addDaylyQueryClass(modelObjClass, (DaylyQuery) queryObjInstance);
            }
            if (queryObjInstance instanceof FieldQuery) {
              queryFields.put(modelObjClass.getName(), (FieldQuery) queryObjInstance);
              queryFields.put(modelObjClass.getSimpleName(), (FieldQuery) queryObjInstance);
            }
            String query_class_table = modelEl.getAttribute("query-class-table");
            if ("true".equals(query_class_table)) {
              addQueryTableClass(modelObjClass, (QueryTable) queryObjInstance);
            }
          }
          if (None.isNonBlank(saveObjClassStr)) {
            Class saveObjClass = Class.forName(saveObjClassStr);
            addSaveDBClass(modelObjClass, (SaveDB) saveObjClass.newInstance());

          }
        }

      }
    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  private static void addSaveDBClass(Class modelObjClass, SaveDB saveClass) {
    saveDBs.put(modelObjClass.getName(), saveClass);
    saveDBs.put(modelObjClass.getSimpleName(), saveClass);
  }

  private static void addDaylyQueryClass(Class modelObjClass, DaylyQuery queryClass) {
    daylyQuerys.put(modelObjClass.getName(), queryClass);
    daylyQuerys.put(modelObjClass.getSimpleName(), queryClass);
  }

  private static void addMonthlyQueryClass(Class modelObjClass, MonthlyQuery queryClass) {
    monthlyQuerys.put(modelObjClass.getName(), queryClass);
    monthlyQuerys.put(modelObjClass.getSimpleName(), queryClass);
  }

  private static void addYearlyQueryClass(Class modelObjClass, YearlyQuery queryClass) {
    yearlyQuerys.put(modelObjClass.getName(), queryClass);
    yearlyQuerys.put(modelObjClass.getSimpleName(), queryClass);
  }

  public static List<SaveDB> getAllSaveDBs() {
    List<SaveDB> list = new ArrayList<SaveDB>();
    list.addAll(saveDBs.values());
    return list;
  }

  public static SaveDB getSaveDB(Class modelObjClass) {
    return saveDBs.get(modelObjClass.getName());
  }

  private static void addQueryTableClass(Class modelObjClass, QueryTable queryClass) {
    queryTables.put(modelObjClass.getName(), queryClass);
  }

  public static QueryTable getQueryTable(String classname) {
    return queryTables.get(classname);
  }

  public static DaylyQuery getDaylyQuery(String classname) {
    return daylyQuerys.get(classname);
  }

  public static YearlyQuery getYearlyQuery(String classname) {
    return yearlyQuerys.get(classname);
  }

  public static FieldQuery getFieldQuery(String classname) {
    return queryFields.get(classname);
  }

  public static MonthlyQuery getMonthlyQuery(String classname) {
    return monthlyQuerys.get(classname);
  }
}
