package engine.util;

import java.util.Date;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import tcc.utils.date.WMDate;

public class ExcellUtil {
  public static double getDouble(Row row, int i) {
    String str = getString(row, i);
    str = str.replaceAll(" ", "");
    str = str.trim();
    return Double.parseDouble(str);
  }

  public static long getLong(Row row, int i) {

    if (row.getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
      return (long) row.getCell(i).getNumericCellValue();
    } else {
      return Long.parseLong(row.getCell(i).getStringCellValue());

    }

  }

  public static int getInt(Row row, int i) {
    if (row.getCell(i).getCellType() == Cell.CELL_TYPE_NUMERIC) {
      return (int) row.getCell(i).getNumericCellValue();
    } else {
      return Integer.parseInt(row.getCell(i).getStringCellValue());
    }
  }

  public static String getString(Sheet sheet, int r, int c) {
    return getString(sheet.getRow(r), c);
  }

  public static String getString(Row row, int i) {
    Cell cell = row.getCell(i);
    return getString(cell);
  }

  public static String getString(Cell cell) {

    if (cell == null)
      return null;

    if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
      if (HSSFDateUtil.isCellDateFormatted(cell)) {
        Date d = cell.getDateCellValue();

        return "" + d.getTime();
      } else {
        double v = cell.getNumericCellValue();
        String vs = "" + v;
        if (vs.endsWith(".0"))
          vs = vs.substring(0,vs.length() - 2);
        return vs;
      }
    } else if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
      return "" + (float) cell.getNumericCellValue();
    } else {

      return cell.getStringCellValue();
    }
  }
}
