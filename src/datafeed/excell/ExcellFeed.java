package datafeed.excell;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.db.SQLFactory;
import model.db.SaveDB;
import model.entity.Record;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import tcc.utils.None;
import tcc.utils.log.LogService;
import datafeed.mapping.DateFormatMapping;
import datafeed.mapping.ModelMapping;
import datafeed.mapping.ModelMappings;
import engine.util.ExcellUtil;
import engine.util.Object2XML;

public class ExcellFeed {
  //

  private Map<String, String> excellFiledMapping = new HashMap<String, String>();
  List<RecordMapping> recordsMapping = new ArrayList<RecordMapping>();
  private ModelMapping modelMapping = null;
  private Date now = new Date();
  public Sheet sheet = null;
  private int dimensionality = 0;
  private boolean shareDimensionalityColums = false;
  //
  private List<Record> datas = new ArrayList<Record>();

  //
  public File excel = null;

  public ExcellFeed(File excel, Sheet sheet) {
    this.sheet = sheet;
    modelMapping = null;
    this.excel = excel;
  }

  public void parseTable() throws Exception {
    int rowNum = sheet.getPhysicalNumberOfRows();

    int tableStartR = 0;
    tableStartR = parseHeader(tableStartR);

    tableStartR = parseTableHeader(tableStartR);
    setModelMapping();
    for (int r = tableStartR; r < rowNum; r++) {
      Row row = sheet.getRow(r);
      if (dimensionality == 0) {
        parseTableRow_dimensionality_0(row);
      } else if (dimensionality == 2) {
        parseTableRow_dimensionality_2(row);
      }

    }
  }

  public void saveDatas() throws Exception {
    Map<SaveDB, List<Record>> map = new HashMap<SaveDB, List<Record>>();

    for (Record d : datas) {

      SaveDB saveDB = SQLFactory.getSaveDB(d.getClass());
      if (saveDB == null) {
        LogService.err("saveDB is null:" + d.getClass());
      } else {
        List<Record> list = map.get(saveDB);
        if (list != null) {

        } else {
          list = new ArrayList<Record>();
          map.put(saveDB, list);
        }

        list.add(d);
      }

    }
    for (SaveDB saver : map.keySet()) {

      List<Record> _list = map.get(saver);

      List<Record> list = new ArrayList<Record>();
      list.addAll(_list);
      List<Record> templist = new ArrayList<Record>();

      while (!None.isEmpty(list)) {
        templist.clear();
        templist.addAll(list);
        List<Record> sublist_save = new ArrayList<Record>();
        List<Record> sublist_delete = new ArrayList<Record>();
        int i = 0;

        for (Record d : templist) {
          i++;
          if (d.ignoreSave()) {
            sublist_delete.add(d);
          } else {
            sublist_save.add(d);
          }

          list.remove(d);
          if (i % 100 == 0)
            break;
        }
        try {
          if (!None.isEmpty(sublist_save)) {
            saver.save(sublist_save);
            sublist_save.clear();
          }
          if (!None.isEmpty(sublist_delete)) {
            saver.delete(sublist_delete);
            sublist_delete.clear();
          }
        } catch (SQLException e) {
          LogService.trace(e, null);
          for (Record d : sublist_save) {
            LogService.err(Object2XML.toXMLString(d));
          }
          throw e;
        }
      }
    }

  }

  private void setModelMapping() {
    modelMapping = ModelMappings.getModelMapping(excellFiledMapping.get(KEYS.KEY_TYPE), getValue(KEYS.KEY_CYCLE, recordsMapping.get(0).getTableColumFieldMapping(KEYS.KEY_CYCLE)), dimensionality);
    if (modelMapping == null) {
      LogService.err(excellFiledMapping.get(KEYS.KEY_TYPE) + "," + getValue(KEYS.KEY_CYCLE, recordsMapping.get(0).getTableColumFieldMapping(KEYS.KEY_CYCLE)) + "," + dimensionality);
    }
  }

  private int parseHeader(int tableStartR) {
    while (addHeaderKeyValue(tableStartR)) {
      tableStartR++;
    }
    tableStartR++;
    return tableStartR;
  }

  private boolean addHeaderKeyValue(int tableStartR) {
    String key = ExcellUtil.getString(sheet, tableStartR, 0);

    if (!None.isBlank(key)) {
      if (key.equals(KEYS.KEY_TABLE)) {
        String dimensionalitySTR = ExcellUtil.getString(sheet, tableStartR, 1);
        if (KEYS.KEY_dimensionality.equals(dimensionalitySTR)) {
          dimensionality = 2;
          String nextStr = ExcellUtil.getString(sheet, tableStartR + 1, 0);
          if (KEYS.KEY_SHARE_COLUMS.equals(nextStr)) {
            this.shareDimensionalityColums = true;

          }
        }
        return false;
      }

      String value = ExcellUtil.getString(sheet, tableStartR, 1);
      if (value == null) {
        LogService.err("tableStartR:" + tableStartR + "sheet:" + sheet.getSheetName());
      }
      this.excellFiledMapping.put(key.trim(), value.trim());

    }
    return true;
  }

  private void setSharedFieldMapping(List<SharedFieldMapping> sfmpings, int tableStartR) {
    Row row = sheet.getRow(tableStartR);
    int cols = row.getPhysicalNumberOfCells();
    int start = 1;
    int count = 0;

    for (int i = 1; i < cols; i++) {
      Cell cell = row.getCell(i);
      if (cell != null && None.isNonBlank(ExcellUtil.getString(cell))) {

        if (start == 2) {
          TableColumFieldMapping colfmap = new TableColumFieldMapping();
          SharedFieldMapping sfmping = new SharedFieldMapping();
          sfmping.count = count;
          sfmping.startIndex = i - count;
          parseTableHeaderKeyValue(row, sfmping.startIndex, colfmap);
          sfmping.sharedfieldsMappings = colfmap;
          sfmpings.add(sfmping);
          count = 0;
        }

        start = 1;
      } else {
        start = 2;
      }
      count++;
    }
    SharedFieldMapping sfmping = new SharedFieldMapping();
    TableColumFieldMapping colfmap = new TableColumFieldMapping();
    sfmping.count = count;
    sfmping.startIndex = cols - count;
    parseTableHeaderKeyValue(row, sfmping.startIndex, colfmap);
    sfmping.sharedfieldsMappings = colfmap;
    sfmpings.add(sfmping);
  }

  private int parseTableHeader(int tableStartR) {
    List<SharedFieldMapping> sfmpings = new ArrayList<SharedFieldMapping>();
    if (this.shareDimensionalityColums && dimensionality == 2) {
      // 解析共享的字段，一个维度的数据有2个以上的字段
      setSharedFieldMapping(sfmpings, tableStartR);
      tableStartR++;
    }

    //
    Row headerRow = sheet.getRow(tableStartR);
    int cols = headerRow.getPhysicalNumberOfCells();
    if (dimensionality == 0) {
      RecordMapping rm = new RecordMapping();
      for (int i = 0; i < cols; i++) {
        TableColumFieldMapping colfmap = new TableColumFieldMapping();
        colfmap.colIndex = i;
        parseTableHeaderKeyValue(headerRow, i, colfmap);
        rm.addColsFiledMapping(colfmap);
      }
      recordsMapping.add(rm);
    } else if (dimensionality == 2) {
      TableColumFieldMapping firstColfmap = new TableColumFieldMapping();
      parseTableHeaderKeyValue(headerRow, 0, firstColfmap);

      if (shareDimensionalityColums) {
        for (SharedFieldMapping sfmp : sfmpings) {
          RecordMapping rm = new RecordMapping();
          rm.getSharedMappings().putMapping(sfmp.sharedfieldsMappings);
          rm.addColsFiledMapping(firstColfmap);
          for (int i = sfmp.startIndex; i < sfmp.startIndex + sfmp.count; i++) {
            TableColumFieldMapping colfmap = new TableColumFieldMapping();
            colfmap.colIndex = i;
            parseTableHeaderKeyValue(headerRow, i, colfmap);
            rm.addColsFiledMapping(colfmap);
          }

          recordsMapping.add(rm);
        }
      } else {
        for (int i = 1; i < cols; i++) {
          RecordMapping rm = new RecordMapping();
          TableColumFieldMapping colfmap = new TableColumFieldMapping();
          colfmap.colIndex = i;
          parseTableHeaderKeyValue(headerRow, i, colfmap);
          rm.addColsFiledMapping(firstColfmap);
          rm.addColsFiledMapping(colfmap);
          recordsMapping.add(rm);

        }
      }
    }
    tableStartR++;
    return tableStartR;
  }

  private void parseTableHeaderKeyValue(Row row, int tdC, TableColumFieldMapping colFiledMapping) {
    String cellString = ExcellUtil.getString(row, tdC);
    if (!None.isBlank(cellString)) {

      String[] lines = cellString.split("\n");
      try {
        for (String line : lines) {
          if (line.indexOf(":") != -1) {
            String[] lvs = line.split(":");
            colFiledMapping.putMapping(lvs[0].trim(), lvs[1].trim());
          } else {
            colFiledMapping.putMapping(line.trim(), line.trim());
          }
        }
      } catch (Exception e) {
        LogService.trace(e, "parseTableHeaderKeyValue:" + cellString);

      }
    }

  }

  private void parseTableRow_dimensionality_0(Row row) throws Exception {
    Record record = this.modelMapping.newRecord();
    record.setSource(getValue(KEYS.KEY_SOURCE, null));
    record.setUpdatedBy(getValue(KEYS.KEY_UPDATEDBY, null));
    record.setUpdatedAt(now);
    Set<String> set = new HashSet<String>();
    getFiledKeys(set);
    for (String fieldKey : set) {
      Field objField = this.modelMapping.getFiled(fieldKey);
      if (objField != null) {
        String valueString = excellFiledMapping.get(fieldKey);
        ModelMappings.setFieldValue(record, objField, valueString);
      }
    }

    for (TableColumFieldMapping colMap : recordsMapping.get(0).getAllFieldMappings()) {
      for (String fieldKey : colMap.getFiledKeys()) {
        Field objField = this.modelMapping.getFiled(colMap.columfiledMapping.get(fieldKey));
        if (objField != null) {
          String valueString = getValue(row, colMap, fieldKey);
          try {
            ModelMappings.setFieldValue(record, objField, valueString);
          } catch (Exception e) {
            LogService.err("row:" + row.getRowNum());
            throw e;
          }
        }
      }
    }
    datas.add(record);

  }

  private boolean checkRowisNotEmpty(Row row) {
    int cols = row.getPhysicalNumberOfCells();

    for (int i = 0; i < cols; i++) {
      Cell cell = row.getCell(i);
      if (cell != null && None.isNonBlank(ExcellUtil.getString(cell))) {
        return true;
      }
    }
    return false;
  }

  private void parseTableRow_dimensionality_2(Row row) throws Exception {
    if (!checkRowisNotEmpty(row)) {
      return;
    }
    for (RecordMapping rm : recordsMapping) {
      Record record = this.modelMapping.newRecord();

      record.setSource(getValue(KEYS.KEY_SOURCE, rm.getTableColumFieldMapping(KEYS.KEY_SOURCE)));
      record.setUpdatedBy(getValue(KEYS.KEY_UPDATEDBY, rm.getTableColumFieldMapping(KEYS.KEY_UPDATEDBY)));
      record.setUpdatedAt(now);
      Set<String> set = new LinkedHashSet<String>();
      getFiledKeys(set, rm.getAllFieldMappings());
      try {

        for (String fieldKey : set) {
          Field objField = this.modelMapping.getFiled(fieldKey);
          String valueString = getValue(fieldKey, rm.getTableColumFieldMapping(fieldKey));
          ModelMappings.setFieldValue(record, objField, valueString);
        }
        for (TableColumFieldMapping hasVfms : rm.getTableColsFiledMapping()) {
          for (String fieldKey : hasVfms.getFiledKeys()) {
            Field objField = this.modelMapping.getFiled(fieldKey);
            String valueString = getValue(row, hasVfms, fieldKey);
            ModelMappings.setFieldValue(record, objField, valueString);
          }
        }

        for (TableColumFieldMapping hasVfms : rm.getHasKEY_VALUEColsFiledMapping()) {
          String fieldKey = hasVfms.columfiledMapping.get(KEYS.KEY_VALUE);
          Field objField = this.modelMapping.getFiled(fieldKey);
          String valueString = getValue(row, hasVfms, fieldKey);
          ModelMappings.setFieldValue(record, objField, valueString);
          valueString = "";
        }
      } catch (Exception e) {
        Set<String> set2 = new LinkedHashSet<String>();
        getFiledKeys(set2, rm.getAllFieldMappings());

        LogService.err("row:" + row.getRowNum());
        throw e;
      }
      datas.add(record);

    }

  }

  private void getFiledKeys(Set<String> set, List<TableColumFieldMapping> colMaps) {
    getFiledKeys(set);
    for (TableColumFieldMapping colMap : colMaps) {
      Set<String> keys = colMap.getFiledKeys();
      if (keys.contains(KEYS.KEY_UNITTYPE))
        set.add(KEYS.KEY_UNITTYPE);
      for (String fieldKey : keys) {
        if (isIgnoreField(fieldKey)) {
          continue;
        }
        set.add(fieldKey);
      }
    }
  }

  private void getFiledKeys(Set<String> set) {
    Set<String> keys = excellFiledMapping.keySet();
    if (keys.contains(KEYS.KEY_UNITTYPE))
      set.add(KEYS.KEY_UNITTYPE);
    for (String fieldKey : keys) {

      if (isIgnoreField(fieldKey)) {
        continue;
      }
      set.add(fieldKey);
    }
  }

  private boolean isIgnoreField(String fieldKey) {
    String[] strs = { KEYS.KEY_TYPE, KEYS.KEY_VALUE, KEYS.KEY_TABLE, KEYS.KEY_CYCLE, KEYS.KEY_SOURCE, KEYS.KEY_UPDATEDBY };
    for (String s : strs) {
      if (s.equals(fieldKey)) {
        return true;
      }
    }

    return false;
  }

  private String getValue(String key, TableColumFieldMapping filedMapping) {

    String value = excellFiledMapping.get(key);
    if (value != null)
      return value;
    value = filedMapping.columfiledMapping.get(key);
    if (value != null)
      return value;

    return value;
  }

  private String getValue(Row row, TableColumFieldMapping filedMapping, String fieldKey) throws Exception {
    Cell cell = row.getCell(filedMapping.colIndex);
    if (cell == null) {
      LogService.warn("cell is null ,getValue() fieldKey:" + fieldKey + " filedMapping" + filedMapping.colIndex + " row:" + row.getRowNum());
      return null;
    }
    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC && HSSFDateUtil.isCellDateFormatted(cell)) {
      Date d = cell.getDateCellValue();
      String fieldmappingstring = filedMapping.columfiledMapping.get(fieldKey);
      if (fieldmappingstring == null) {
        fieldmappingstring = excellFiledMapping.get(KEYS.KEY_CYCLE);
      }
      String patten = DateFormatMapping.getCYCLEDateFormat(fieldmappingstring);
      return DateFormatMapping.convertCYCLEfromUCDate("" + d.getTime(), patten);
    } else {
      return ExcellUtil.getString(cell);
    }
  }

}
