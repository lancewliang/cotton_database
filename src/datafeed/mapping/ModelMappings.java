package datafeed.mapping;

import java.lang.reflect.Field;
import java.util.HashMap;

import model.constant.UnitType;
import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;
import model.constant.dao.Unit;
import model.entity.DifferentUnitType;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tcc.utils.xml.dom.DOMUtil;
import tcc.utils.xml.xpath.XmlOperator;

public class ModelMappings {
  private static HashMap<String, ModelMapping> map = new HashMap<String, ModelMapping>();

  static {
    init();
  }

  private static void init() {
    try {
      Document doc = DOMUtil.inputStreamDoc(ModelMappings.class.getResourceAsStream("datafeed-obj-field-mapping.xml"));
      NodeList list = XmlOperator.selectNodeList(doc, "//model-mapping");
      for (int i = 0; i < list.getLength(); i++) {
        Element node = (Element) list.item(i);
        Element fnode2 = (Element) XmlOperator.selectSingleNode(node, "ignored-field-mapping");
        Element fnode1 = (Element) XmlOperator.selectSingleNode(node, "field-mapping");
        ModelMapping modelMapping = new ModelMapping();
        modelMapping.type = node.getAttribute("type");
        modelMapping.timeDimension = node.getAttribute("time-dimension");
        modelMapping.dimension = Integer.parseInt(node.getAttribute("dimension"));
        modelMapping.setClassType(node.getAttribute("model-class"));
        NamedNodeMap attrs1 = fnode1.getAttributes();
        for (int f = 0; f < attrs1.getLength(); f++) {
          Attr attribute = (Attr) attrs1.item(f);
          modelMapping.putMapping(attribute.getName(), attribute.getValue());
        }
        if (fnode2 != null) {
          NamedNodeMap attrs2 = fnode2.getAttributes();
          for (int f = 0; f < attrs2.getLength(); f++) {
            Attr attribute = (Attr) attrs2.item(f);
            modelMapping.putIgnoreMapping(attribute.getName(), attribute.getValue());
          }
        }
        putModelMapping(modelMapping);
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public static void setFieldValue(Object obj, Field field, String valueString) throws Exception {

    try {
      field.setAccessible(true);
      Class c = field.getType();
      Class s = c.getSuperclass();
      if (c.equals(long.class)) {
        if (None.isBlank(valueString)) {
          field.setLong(obj, DBUtil.NULLINT);
        } else {
          if (valueString.indexOf('/') != -1) {
            field.setLong(obj, Long.parseLong(StringUtil.replaceString(valueString, "/", "")));
          } else {
            field.setLong(obj, Long.parseLong(valueString));
          }
        }
      } else if (c.equals(double.class)) {
        field.setDouble(obj, Double.parseDouble(None.isBlank(valueString) ? DBUtil.NULLINT + "" : valueString));
      } else if (c.equals(float.class)) {
        field.setFloat(obj, Float.parseFloat(None.isBlank(valueString) ? DBUtil.NULLINT + "" : valueString));
      } else if (c.equals(String.class)) {
        field.set(obj, valueString);
      } else if (valueString != null && (c.getName().equals(Unit.class.getName()))) {
        UnitType unitType = ((DifferentUnitType) obj).getUnitType();
        if (unitType != null) {
          Unit unit = unitType.getUnit(valueString);
          field.set(obj, unit);
        } else
          throw new Exception("no Unit:" + valueString + "  class is:" + c.getName());
      } else if (valueString != null && (c.getName().equals(UnitType.class.getName()))) {
        field.set(obj, UnitType.getUnitType(valueString));
      } else if (valueString != null && (c.isInstance(Constant.class) || s.isInstance(Constant.class))) {
        Constant v = ConstantBase.getConstant(c, valueString);
        if (v == null)
          throw new Exception("no constant:" + valueString + "  class is:" + c.getName());
        field.set(obj, v);
      } else {
        field.set(obj, valueString);
      }
    } catch (Exception e) {
      LogService.err("field:" + field.getName() + " value=" + valueString);
      throw e;
    }
  }

  private static void putModelMapping(ModelMapping modelMapping) {
    map.put(modelMapping.type + "|" + modelMapping.dimension + "|" + modelMapping.timeDimension, modelMapping);
  }

  public static ModelMapping getModelMapping(String type, String timeDimension, int dimension) {
    return map.get(type + "|" + dimension + "|" + timeDimension);
  }

}
