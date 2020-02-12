package datafeed.mapping;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import tcc.utils.log.LogService;

import model.entity.Record;

public class ModelMapping {
  String type;
  String timeDimension;
  int dimension = 0;
  private String modelClassType;
  private Class classC = null;
  private Map<String, String> filedMapping = new HashMap<String, String>();
  private Map<String, String> ignoredfiledMapping = new HashMap<String, String>();

  public Record newRecord() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

    return (Record) classC.newInstance();
  }

  public void putMapping(String key, String fieldName) {
    filedMapping.put(key, fieldName);
  }

  public void putIgnoreMapping(String key, String fieldName) {
    ignoredfiledMapping.put(key, fieldName);
  }

  public void setClassType(String classType) throws ClassNotFoundException {
    modelClassType = classType;
    classC = Class.forName(modelClassType);
  }

  public Field getFiled(String fieldName) throws Exception {
    if (fieldName == null) {
      LogService.err("get null field fieldName is null");
      throw new NoSuchFieldException(fieldName);
    }
    if (filedMapping.get(fieldName) == null) {
      if (ignoredfiledMapping.get(fieldName) == null) {
        LogService.err("get null field fieldName:" + fieldName);
        throw new NoSuchFieldException(fieldName);
      } else {
        return null;
      }
    }
    try {
      return classC.getDeclaredField(filedMapping.get(fieldName));
    } catch (Exception e) {
      try {
        return classC.getSuperclass().getDeclaredField(filedMapping.get(fieldName));
      } catch (Exception e1) {
        LogService.err("getFiled " + fieldName);
        throw e1;
      }
    }
  }
}
