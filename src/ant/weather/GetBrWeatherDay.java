package ant.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.constant.LengthUnit;
import model.constant.WeatherRegion;
import model.entity.weather.WeatherDay;
import model.entity.weather.db.WeatherDaySQL;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.file.FileUtil;
import tcc.utils.log.LogService;
import ant.server.AntFTPLogic;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetBrWeatherDay implements DayAnt {
  public static void main(String[] args) throws Exception {
    SetENVUtil.setENV();
    GetBrWeatherDay exp = new GetBrWeatherDay();
    exp.doAnt();
  }

  @Override
  public void doAnt() {

    File brweather = AntFTPLogic.getFolder(getSource());
    File[] files = brweather.listFiles();
    if (!None.isEmpty(files))
      for (File from : files) {
        if (from.isFile()) {
          String filename = from.getName();
          if (filename.indexOf("pdf") != -1 && filename.indexOf("巴西") != -1) {
            try {
              boolean isforecast = filename.indexOf("预测") != -1;
              long year = getYear(filename);
              File parseFIle = getParseFile(from, isforecast);

              if (isforecast) {
                convertFile(from, parseFIle);
                boolean rs = parseForecastFile(parseFIle, year);
                if (rs)
                  from.delete();
              } else {
                convertFile(from, parseFIle);
                boolean rs = parseActFile(parseFIle, year);
                if (rs)
                  from.delete();
              }

            } catch (Exception e) {
              LogService.trace(e, null);
            }
          }
        }
      }
  }

  private File getParseFile(File from, boolean isforecast) throws IOException {
    File parseFile = AntLogic.getFile(getSource() + "/" + (isforecast ? "forecast" : "act"), from.getName() + ".txt");
    return parseFile;
  }

  private File convertFile(File from, File parseFIle) throws Exception {
    FileUtil.copyFile(from, new File(parseFIle.getParentFile(), from.getName()));
    parseTextFile(from, parseFIle);

    return parseFIle;
  }

  private long getYear(String f) throws IOException {
    return Long.parseLong(f.substring(4, 8));
  }

  private boolean parseForecastFile(File f, long year) throws IOException {
    InputStream in = new FileInputStream(f);
    String html = FileStreamUtil.getFileContent(in);
    in.close();
    return parseForecastContext(html, year);
  }

  private boolean parseForecastContext(String content, long year) throws IOException {
    BufferedReader br = null;
    String line = "";
    try {

      Date now = new Date();
      br = new BufferedReader(new StringReader(content));

      line = br.readLine();

      String[] dates = HTMLParseUtil.trim2bank(line).trim().split(" ");

      while ((line = br.readLine()) != null) {
        String[] cols1 = HTMLParseUtil.trim2bank(line, 4).trim().split("    ");
        String citystr = HTMLParseUtil.trim2bank(cols1[0]);

        String[] citystrs = citystr.split("-");
        String city = citystrs[0].trim() + "," + citystrs[1].trim().toLowerCase() + "," + "Brazil";
        WeatherRegion region = WeatherRegion.getWeatherRegion(city);
        if (region == null) {
          continue;
        }

        for (int i = 1; i < dates.length; i++) {
          String date = year + "/" + dates[i];// yyyy/dd/mm
          long reportDate = getYYYYMMDD(date);
          String dayINfo = cols1[i];
          String[] cols = StringUtil.split(dayINfo.replaceAll(" ", ""), "|", false);
          WeatherDay weatherDay = WeatherDaySQL.getObj(reportDate, region, getSource());
          if (weatherDay == null) {
            weatherDay = new WeatherDay();
            weatherDay.setWeatherRegion(region);
            weatherDay.setReportDate(reportDate);
            weatherDay.setSource(getSource());
            weatherDay.setForecast("true");
          } else {
            if (!"true".equals(weatherDay.getForecast())) {
              continue;
            }
          }

          weatherDay.setHigh(getIntC(cols[0]));
          weatherDay.setLow(getIntC(cols[1]));
          weatherDay.setPrecip(getIntMM(cols[2]));
          weatherDay.setSnowUnit(LengthUnit.getLengthUnit("in"));
          weatherDay.setPrecipUnit(LengthUnit.getLengthUnit("mm"));
          weatherDay.setUpdatedAt(now);
          weatherDay.setUpdatedBy(AntManger.UPDATEBY);
          WeatherDaySQL.save(weatherDay);
        }

        // take Crop and State

      }
      return true;
    } catch (Exception e) {
      LogService.trace(e, "");
      return false;
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

  private boolean parseActFile(File f, long year) throws IOException {
    InputStream in = new FileInputStream(f);
    String html = FileStreamUtil.getFileContent(in);
    in.close();
    return parseActContext(html, year);
  }

  private boolean parseActContext(String content, long year) throws IOException {
    BufferedReader br = null;
    String line = "";
    try {

      Date now = new Date();
      br = new BufferedReader(new StringReader(content));

      line = br.readLine();

      int s = line.indexOf("("), e = line.indexOf(")");

      long reportDate = getYYYYMMDD2(line.substring(s + 1, e));
      line = br.readLine();
      while ((line = br.readLine()) != null) {
        String[] cols1 = HTMLParseUtil.trim2bank(line, 6).trim().split("      ");
        String citystr = HTMLParseUtil.trim2bank(cols1[0]);

        String[] citystrs = citystr.split("-");
        String city = citystrs[0].trim() + "," + citystrs[1].trim().toLowerCase() + "," + "Brazil";
        WeatherRegion region = WeatherRegion.getWeatherRegion(city);
        if (region == null) {
          continue;
        }

        String dayINfo = cols1[1];
        String[] cols = StringUtil.split(dayINfo.replaceAll(" ", ""), "|", false);
        try {
          WeatherDay weatherDay = WeatherDaySQL.getObj(reportDate, region, getSource());
          if (weatherDay == null) {
            weatherDay = new WeatherDay();
            weatherDay.setWeatherRegion(region);
            weatherDay.setReportDate(reportDate);
            weatherDay.setSource(getSource());
            weatherDay.setForecast("");
          }
          weatherDay.setForecast("");
          weatherDay.setHigh(getIntC2(cols[0]));
          weatherDay.setLow(getIntC2(cols[1]));
          weatherDay.setPrecip(getIntMM2(cols[2]));
          weatherDay.setSnowUnit(LengthUnit.getLengthUnit("in"));
          weatherDay.setPrecipUnit(LengthUnit.getLengthUnit("mm"));
          weatherDay.setUpdatedAt(now);
          weatherDay.setUpdatedBy(AntManger.UPDATEBY);
          WeatherDaySQL.save(weatherDay);
        } catch (Exception e2) {
          LogService.trace(e2, line);
          return false;
        }
      }

      // take Crop and State
      return true;
    } catch (Exception e) {
      LogService.trace(e, line);
      return false;
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

  public int getIntMM(String c) {
    return Integer.parseInt(c.replaceAll("mm", ""));
  }

  public int getIntC(String c) {
    return Integer.parseInt(c.replaceAll("C", ""));
  }

  public int getIntMM2(String c) {
    double dd = Double.parseDouble(c.replaceAll("mm", ""));
    return (int) Math.round(dd);
  }

  public int getIntC2(String c) {
    double dd = Double.parseDouble(c.replaceAll("C", ""));
    return (int) Math.round(dd);
  }

  public long getYYYYMMDD(String str) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/dd/MM");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(str);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf2.format(calendar.getTime()));
  }

  public long getYYYYMMDD2(String str) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(str);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    return Long.parseLong(sdf2.format(calendar.getTime()));
  }

  private void parseTextFile(File infile, File outfile) throws IOException {
    if (outfile.exists()) {
      return;
    }

    int startPage = 1;
    // 结束提取页数
    int endPage = Integer.MAX_VALUE;
    FileInputStream is = null;
    PDDocument document = null;
    boolean sort = false;
    OutputStreamWriter output = null;
    try {
      is = new FileInputStream(infile);
      PDFParser parser = new PDFParser(is);
      parser.parse();
      document = parser.getPDDocument();

      output = new OutputStreamWriter(new FileOutputStream(outfile), "utf-8");
      // PDFTextStripper来提取文本
      PDFTextStripper stripper = null;
      stripper = new PDFTextStripper();
      // 设置是否排序
      stripper.setSortByPosition(sort);
      // 设置起始页
      stripper.setStartPage(startPage);
      // 设置结束页
      stripper.setEndPage(endPage);
      // 调用PDFTextStripper的writeText提取并输出文本
      stripper.writeText(document, output);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (document != null) {
        try {
          document.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (output != null) {
        // 关闭输出流
        output.close();
      }
    }
  }

  @Override
  public String getSource() {
    return "brweather";

  }

}
