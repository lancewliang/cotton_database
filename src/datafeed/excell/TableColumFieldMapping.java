package datafeed.excell;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TableColumFieldMapping {
  public int colIndex = 0;
  public Map<String, String> columfiledMapping = new HashMap<String, String>();

  public Set<String> getFiledKeys() {
    return columfiledMapping.keySet();
  }

  public void putMapping(String key, String fieldName) {
    columfiledMapping.put(key, fieldName);
  }

  public void putMapping(TableColumFieldMapping other) {
    columfiledMapping.putAll(other.columfiledMapping);
  }

}
