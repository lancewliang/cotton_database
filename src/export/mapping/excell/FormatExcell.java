package export.mapping.excell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.QueryUnit;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import tcc.utils.None;
import tcc.utils.collections.UnguardedHashtable;
import tcc.utils.log.LogService;
import export.mapping.report.DimensionReportMapping;
import export.mapping.report.ReportMappings;
import export.mapping.report.dimension.CommodityDimension;
import export.mapping.report.dimension.ModeDimension;
import export.mapping.report.field.Chart;
import export.mapping.report.field.ReportField;

public class FormatExcell implements Runnable {

  DimensionReportMapping mapping;
  Workbook wb;
  Commodity commodity;
  public boolean end = false;
  ExportTaskStatus status;
  public File outputFile = null;

  public FormatExcell(DimensionReportMapping mapping, ExportTaskStatus status, Commodity commodity) {
    this.mapping = mapping;
    this.status = status;
    this.commodity = commodity;
    outputFile = getOutFile(status, mapping);
  }

  @Override
  public void run() {

    try {
      XSSFWorkbook wb = FormatExcell.createFile(outputFile, false);
      if (mapping.commodityDimension == null) {
        mapping.commodityDimension = new CommodityDimension(commodity.getCommodity());
      }
      if (mapping.ant != null)
        mapping.ant.doAnt();
      formatByCountry_Time(mapping, wb);
      FormatExcell.saveFile(outputFile, wb);
    } catch (Exception e) {
      LogService.trace(e, null);
    }

    end = true;
  }

  private File getOutFile(ExportTaskStatus status, DimensionReportMapping _mapping) {
    Commodity commodityObj = Commodity.getCommodity(status.commodity);
    return OutputReports.saveReport(commodityObj, status.format, status.date + "-" + _mapping.label);

  }

  public void formatByCountry_Time(DimensionReportMapping mapping, Workbook wb) throws Exception {

    QueryUnit unit = null;

    if (mapping.unit != null)
      unit = mapping.unit;
    else {
      unit = new QueryUnit();
    }
    Commodity commodity = null;
    if (mapping.commodityDimension != null) {
      commodity = mapping.commodityDimension.getCommodity();
    }
    List<Country> countrys = null;
    if (mapping.countryDimension != null) {
      if (mapping.countryDimension.isAll()) {
        if (commodity != null) {
          countrys = mapping.countryDimension.getCountrys(commodity);
        } else {
          countrys = mapping.countryDimension.getAllCountrys();
        }
      } else {
        List<Country> country = mapping.countryDimension.getCountrys();
        if (country != null) {
          countrys = new ArrayList<Country>();
          countrys.addAll(country);
        }
      }
    }
    UnguardedHashtable cachepool = new UnguardedHashtable();
    List<ReportField> fileds = mapping.fileds;
    List<Chart> charts = mapping.charts;
    if (!None.isEmpty(countrys)) {
      for (Country country : countrys) {
        mapping.timeDimension.initTimeList(commodity, country);
        if (!mapping.modeDimensionMap.isEmpty()) {
          for (ModeDimension v : mapping.modeDimensionMap.values()) {
            v.initFields(commodity, country);
          }
        }
        List<DateList> timeList = mapping.timeDimension.getTimeList();
        Sheet sheet = wb.createSheet(mapping.label + " (" + country.getCountry() + ")");
        FormatExcellSheet.doPage(mapping, sheet, commodity, country, fileds, charts, mapping.timeDimension, timeList, unit, cachepool);

      }
    } else {
      mapping.timeDimension.initTimeList(commodity, null);
      if (!mapping.modeDimensionMap.isEmpty()) {
        for (ModeDimension v : mapping.modeDimensionMap.values()) {
          v.initFields(commodity, null);
        }
      }
      List<DateList> timeList = mapping.timeDimension.getTimeList();
      Sheet sheet = wb.createSheet(mapping.label);
      FormatExcellSheet.doPage(mapping, sheet, commodity, null, fileds, charts, mapping.timeDimension, timeList, unit, cachepool);

    }
    cachepool.clear();
  }

  public static XSSFWorkbook createFile(File f, boolean loadTempalte) throws IOException {
    if (!f.exists()) {
      if (!f.getParentFile().exists()) {
        f.getParentFile().mkdirs();
      }
      f.createNewFile();
    }
    XSSFWorkbook wsl = null;
    if (loadTempalte) {
      InputStream fis = ReportMappings.class.getResourceAsStream("root_template.xlsm");

      wsl = new XSSFWorkbook(fis);
      fis.close();
    } else {
      wsl = new XSSFWorkbook();
    }
    return wsl;
  }

  public static void saveFile(File f, Workbook wb) throws IOException {
    // 创建一个文件 命名为workbook.xls
    FileOutputStream fileOut = new FileOutputStream(f);
    // 把上面创建的工作簿输出到文件中
    wb.write(fileOut);
    // 关闭输出流
    fileOut.close();
  }
}
