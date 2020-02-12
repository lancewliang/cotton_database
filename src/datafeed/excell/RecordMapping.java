package datafeed.excell;

import java.util.ArrayList;
import java.util.List;

public class RecordMapping {
  private TableColumFieldMapping sharedfieldsMappings = new TableColumFieldMapping();
  private List<TableColumFieldMapping> tableColsFiledMapping = new ArrayList<TableColumFieldMapping>();

  private List<TableColumFieldMapping> hasKEY_VALUEColsFiledMapping = new ArrayList<TableColumFieldMapping>();

  public TableColumFieldMapping getSharedMappings() {
    return sharedfieldsMappings;
  }

  public void addColsFiledMapping(TableColumFieldMapping obj) {

    if (obj.columfiledMapping.get(KEYS.KEY_VALUE) != null) {
      hasKEY_VALUEColsFiledMapping.add(obj);
    } else {
      tableColsFiledMapping.add(obj);
    }
  }

  public boolean isSharedFieldEmpty() {
    return sharedfieldsMappings.columfiledMapping.size() == 0;
  }

  public List<TableColumFieldMapping> getAllFieldMappings() {
    List<TableColumFieldMapping> list = new ArrayList<TableColumFieldMapping>();
    list.add(sharedfieldsMappings);
    list.addAll(tableColsFiledMapping);
    list.addAll(hasKEY_VALUEColsFiledMapping);
    return list;
  }

  public TableColumFieldMapping getTableColumFieldMapping(String fieldKey) {
    List<TableColumFieldMapping> list = getAllFieldMappings();

    for (TableColumFieldMapping columfiledMapping : list) {
      if (columfiledMapping.columfiledMapping.get(fieldKey) != null) {
        return columfiledMapping;
      }
    }
    return null;
  }

  public List<TableColumFieldMapping> getTableColsFiledMapping() {
    return tableColsFiledMapping;
  }

  public List<TableColumFieldMapping> getHasKEY_VALUEColsFiledMapping() {
    return hasKEY_VALUEColsFiledMapping;
  }

}
