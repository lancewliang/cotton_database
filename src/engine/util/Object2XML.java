package engine.util;

import java.io.StringWriter;

import tcc.utils.log.LogService;

import com.thoughtworks.xstream.XStream;

public class Object2XML {
  public static String toXMLString(Object obj) {
    XStream sm = new XStream();
    try {
      StringWriter ops = new StringWriter();
      sm.toXML(obj, ops);
      ops.close();
      return ops.toString();
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return null;
  }
}
