package ant.cotton.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.custom.country.ImportExportMonth;
import model.entity.custom.country.db.ImportExportMonthSQL;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdfparser.PDFStreamParser;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.util.PDFOperator;

import tcc.utils.file.FileUtil;
import tcc.utils.log.LogService;
import ui.util.PDFBOX;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCustomExpINDMonthly implements DayAnt {

  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    GetCustomExpINDMonthly exp = new GetCustomExpINDMonthly();
    exp.doAnt();

  }

  @Override
  public void doAnt() {
    getExport();
  }

  // http://dgciskol.gov.in/data_information.asp
  public void getExport() {
    String href = "http://dgciskol.gov.in/pdfs/Emsft_04.pdf";
    File file = AntLogic.getFile(getSource() + "/export", "Emsft_04.pdf");

    try {
      if (!file.exists()) {
        Util.getFile(href, file);
      }
      long lastDay = ImportExportMonthSQL.getFROMCountryLastDay(Commodity.getCommodity("棉花"), Country.getCountry("IND"), getSource());
      // engine.util.Util.getFile(href, f);
      String str = getString(file);
      long fileDate = getFileDate(str);
      if (fileDate >= lastDay) {
        //praseContent(str);
      }
      if (fileDate > 0) {
        File newFile = new File(file.getParentFile(), "Emsft_04_" + fileDate + ".pdf");
        if (newFile.exists()) {

        } else {
          FileUtil.copyFile(file, newFile);
        }
        file.delete();
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public void praseContent(String str) {
    String line = "TABLE - 4     India's Export of Principal Commodities";
    int areaStart, areaEnd;
    String htmlStr, areaStr;
    while ((areaStart = str.indexOf(line)) != -1) {

      areaEnd = str.indexOf(line, areaStart + 1);
      String body = null;
      if (areaEnd < 0)
        body = str.substring(areaStart);
      else
        body = str.substring(areaStart, areaEnd);

      praseSubContent(body);

      if (areaEnd > 0) {
        str = str.substring(areaEnd);
      } else {
        str = "";
      }
    }
  }

  public long getFileDate(String content) {
    System.out.println("==================================================");
    BufferedReader br = null;
    String line = "";
    long fileDate = 0;
    ParseInfo parseInfo = new ParseInfo();
    try {

      Date now = new Date();
      br = new BufferedReader(new StringReader(content));

      int rowIndex = 0;
      int colrows = 0;
      int i = 0;
      while ((line = br.readLine()) != null) {
        if (parseInfo.stepIndex < 0) {
          if (line.indexOf("Commodities") != -1) {
            parseInfo.stepIndex = 0;

          }

        } else if (parseInfo.stepIndex == 0) {
          if (line.indexOf("UNIT") != -1) {
            parseInfo.stepIndex = 1;
            line = br.readLine();
            String dateSTR = line.split(" ")[1];
            String[] dateSTRs = dateSTR.split("-");
            fileDate = getDate(dateSTRs[1] + "-" + DateUtil.getMonthByEN(dateSTRs[0]));

          }

        }
      }
      return fileDate;
    } catch (Exception e) {
      LogService.trace(e, "");
      return fileDate;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }
  }

  public long praseSubContent(String content) {
    System.out.println("==================================================");
  
    System.out.println("==================================================");
    BufferedReader br = null;
    String line = "";
    long fileDate = 0;
    ParseInfo parseInfo = new ParseInfo();
    try {
      long reportDate2 = 0;
      long reportDate1 = 0;
      Date now = new Date();
      br = new BufferedReader(new StringReader(content));

      int rowIndex = 0;
      int colrows = 0;
      int i = 0;
      int fff=0;
      while ((line = br.readLine()) != null) {
      
        if (parseInfo.stepIndex == 2 || parseInfo.stepIndex == 3 || parseInfo.stepIndex == 4 || parseInfo.stepIndex == 5 || parseInfo.stepIndex == 8) {
          i++;
        }

        fff++;
        System.out.println(fff+"_"+i+":"+line);
        // take Crop and State
        if (parseInfo.stepIndex < 0) {
          if (line.indexOf("Commodities") != -1) {
            parseInfo.stepIndex = 0;

          }

        } else if (parseInfo.stepIndex == 0) {
          if (line.indexOf("UNIT") != -1) {
            parseInfo.stepIndex = 1;
            line = br.readLine();
            String dateSTR = line.split(" ")[1];
            String[] dateSTRs = dateSTR.split("-");
            reportDate1 = getDate(dateSTRs[1] + "-" + DateUtil.getMonthByEN(dateSTRs[0]));
            fileDate = reportDate1;
            LogService.msg("reportDate:" + reportDate1);
          }

        } else if (parseInfo.stepIndex == 1) {
          if (line.indexOf("PAGE NO") != -1) {
            parseInfo.stepIndex = 2;
          }

        } else if (parseInfo.stepIndex == 2) {
          if (line.indexOf("COTTON RAW") != -1) {
            parseInfo.stepIndex = 3;
            rowIndex = i;
          }
        } else if (parseInfo.stepIndex == 3) {
          if (line.endsWith("TON") || line.endsWith("KGS")) {
            colrows = i;
            i = 1;
            parseInfo.stepIndex = 4;
          }

        } else if (parseInfo.stepIndex == 4) {
          if (i == colrows) {
            i = 1;
           
            if (i == rowIndex) {
              parseInfo.stepIndex = 5;
              try {
                if (reportDate1 > 0)
                  saveMonthInfo(reportDate1, Double.parseDouble(HTMLParseUtil.trim2bank(line).split(" ")[1]));
              } catch (Exception e) {
                LogService.trace(e, "");
                return fileDate;
              }
              LogService.msg("rowIndex5=" + rowIndex + "|" + line);
              parseInfo.stepIndex = 6;
            }

          }
        } else if (parseInfo.stepIndex == 5) {
          if (i == rowIndex) {

            try {
              if (reportDate1 > 0)
                saveMonthInfo(reportDate1, Double.parseDouble(HTMLParseUtil.trim2bank(line).split(" ")[1]));
            } catch (Exception e) {
              LogService.trace(e, "");
              return fileDate;
            }
            LogService.msg("rowIndex5=" + rowIndex + "|" + line);
            parseInfo.stepIndex = 6;
          }

        } else if (parseInfo.stepIndex == 6) {
          if (line.startsWith("|rg||ET||RG||re||B*||rg||BT||TD||Tj|")) {
            String dateSTR = line.split(" ")[1];
            String[] dateSTRs = dateSTR.split("-");
            reportDate2 = getDate(dateSTRs[1] + "-" + DateUtil.getMonthByEN(dateSTRs[0]));
            LogService.msg("reportDate2:" + reportDate2);
            parseInfo.stepIndex = 7;
          }
        } else if (parseInfo.stepIndex == 7) {
          if (line.indexOf("|ET||G||re||S||rg||BT||TD||Tj|") != -1) {
            parseInfo.stepIndex = 8;
            i = 1;
            if (i == rowIndex) {
              LogService.msg("rowIndex8=" + rowIndex + "|" + line);
              try {
                if (reportDate2 > 0)
                  saveMonthInfo(reportDate2, Double.parseDouble(HTMLParseUtil.trim2bank(line).split(" ")[1]));
              } catch (Exception e) {
                LogService.trace(e, "");
                return fileDate;
              }
              parseInfo.stepIndex = 9;
            }

          }
        } else if (parseInfo.stepIndex == 8) {
          if (i == rowIndex) {
            try {
              if (reportDate2 > 0)
                saveMonthInfo(reportDate2, Double.parseDouble(HTMLParseUtil.trim2bank(line).split(" ")[1]));
            } catch (Exception e) {
              LogService.trace(e, "");
              return fileDate;
            }
            LogService.msg("rowIndex8=" + rowIndex + "|" + line);
            parseInfo.stepIndex = 9;
          }

        }

      }
      return fileDate;
    } catch (Exception e) {
      LogService.trace(e, "");
      return fileDate;
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

    }
  }

  private void saveMonthInfo(long reportDate, double total) throws SQLException {
    Date now = new Date();
    Country fromCountry = Country.getCountry("IND");
    Country toCountry = Country.getCountry("WHOLE");
    ImportExportMonth impexpDay = ImportExportMonthSQL.getObj(reportDate, toCountry, fromCountry, Commodity.getCommodity("棉花"), getSource());
    if (impexpDay != null) {
      impexpDay.setSource(getSource());
      impexpDay.setUpdatedAt(now);
      impexpDay.setUpdatedBy(AntManger.UPDATEBY);
      impexpDay.setWeightUnit(WeightUnit.getWeightUnit("吨"));
      impexpDay.setValue(total);
    } else {
      impexpDay = new ImportExportMonth();
      impexpDay.setCommodity(Commodity.getCommodity("棉花"));
      impexpDay.setFromCountry(fromCountry);
      impexpDay.setToCountry(toCountry);
      impexpDay.setReportDate(reportDate);
      impexpDay.setSource(getSource());
      impexpDay.setUpdatedAt(now);
      impexpDay.setUpdatedBy(AntManger.UPDATEBY);
      impexpDay.setWeightUnit(WeightUnit.getWeightUnit("吨"));
      impexpDay.setValue(total);
    }
    ImportExportMonthSQL.save(impexpDay);
  }

  class ParseInfo {
    int stepIndex = -100;
    int Crop_and_State_line_size = 0;

  }

  private String getString(File file) throws IOException {
    String sssss = "";
    PDFBOX parse = new PDFBOX();
    InputStream is = new FileInputStream(file);
    parse.document = parse.parseDocument(is);
    // 获取页数
    List<PDPage> pages = parse.document.getDocumentCatalog().getAllPages();
    for (PDPage page : pages) {
      PDStream contents = page.getContents();
      PDFStreamParser parser = new PDFStreamParser(contents.getStream());
      parser.parse();
      List tokens = parser.getTokens();
      for (int j = 0; j < tokens.size(); j++) {
        Object next = tokens.get(j);
        if (next instanceof PDFOperator) {
          PDFOperator op = (PDFOperator) next;
          sssss += "|" + op.getOperation() + "|";

          // Tj and TJ are the two operators that display strings in a PDF
          if (op.getOperation().equals("Tj")) {
            // Tj takes one operator and that is the string
            // to display so lets update that operator
            COSString previous = (COSString) tokens.get(j - 1);
            String string = previous.getString();
            sssss += " " + string + "\n";
            // Word you want to change. Currently this code changes word
            // "Solr" to "Solr123"

          } else if (op.getOperation().equals("TJ")) {
            COSArray previous = (COSArray) tokens.get(j - 1);
            for (int k = 0; k < previous.size(); k++) {
              Object arrElement = previous.getObject(k);
              if (arrElement instanceof COSString) {
                COSString cosString = (COSString) arrElement;
                String string = cosString.getString();
                sssss += " " + string + "\n";

                // Currently this code changes word "Solr" to "Solr123"

              }
            }
          }
        }
      }
    }
    return sssss;
  }

  private long getDate(String calendar) throws ParseException {
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM");
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
    return Long.parseLong(sdf1.format(sdf2.parse(calendar)));
  }

  @Override
  public String getSource() {

    return "dgciskol.gov.in";
  }
}
