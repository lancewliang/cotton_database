package mt4;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import model.entity.macroeconomic.CountryMainIndex;
import model.entity.macroeconomic.db.CountryMainIndexSQL;
import model.entity.macroeconomic.db.MTExchangeRateSQL;
import model.entity.price.country.ExchangeRateUnit;
import tcc.utils.None;
import tcc.utils.file.FileStreamUtil;
import engine.util.SetENVUtil;

public class ExportData1 {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    ExportData1 d = new ExportData1();
    d.exec();
  }

  public void exec() {

    try {
      File folder = new File("C:\\Users\\wliang\\Desktop\\enu_usd");
      FileOutputStream fos1 = new FileOutputStream(new File(folder, "ExportData1_USA_INDEX_1.csv"));
      print(fos1, 0);
      FileOutputStream fos2 = new FileOutputStream(new File(folder, "ExportData1_USA_INDEX_2.csv"));
      print(fos2, 1);
      FileOutputStream fos3 = new FileOutputStream(new File(folder, "ExportData1_USA_INDEX_3.csv"));
      print(fos3, 2);
    } catch (Exception e) {

      e.printStackTrace();
    }

  }

  public void print(FileOutputStream fos, int f) throws Exception {
    List<CountryMainIndex> events = CountryMainIndexSQL.getObjs("USD", "EUR", "Wallstartcn");
    String body = "";
    String line1 = "国家,币种,指标,发布日期,发布时间,指标值(实际|预测|上一次),判断,价格时间,开始价格,结束价格,最低价格,最高价格,价格结束差价\n";
    body += line1;
    double rightCount = 0;
    double wrongCount = 0;
    double rsRight = 0;
    double rsWrong = 0;

    //
    for (CountryMainIndex ci : events) {
      String line = "";
      line += ci.getCountry().getCountry() + "," + ci.getCurrency() + "," + ci.getTitle() + "," + ci.getReportDate() + "," + ci.getReportHour() + "," + ci.getActualValue() + "|" + ci.getForecastValue() + "|" + ci.getPreviousValue();
      line += "," + ci.getInference();
      double min = 0, max = 0;
      List<ExchangeRateUnit> mtrateunits = MTExchangeRateSQL.getObjs(ci.getReportDate(), ci.getReportHour(), "mt_eurusd30", "eur", "usd", f);
      if (None.isEmpty(mtrateunits)) {
        continue;
      }
      for (ExchangeRateUnit un : mtrateunits) {
        if (min == 0) {
          min = un.getMinimumValue();
        }
        if (max == 0) {
          min = un.getTopValue();
        }
        if (min > un.getMinimumValue()) {
          min = un.getMinimumValue();
        }
        if (max < un.getTopValue()) {
          max = un.getTopValue();
        }
      }
      double s = mtrateunits.get(0).getOpeningValue();
      ExchangeRateUnit un = mtrateunits.get(mtrateunits.size() - 1);
      line += "," + un.getReportHour() + "," + s + "," + un.getClosingValue() + "," + min + "," + max + "," + (s - un.getClosingValue());
      if (ci.getCountry().getCountry().equals("USD")) {
        if (ci.getInference().equals(CountryMainIndex.INFERENCE_DOWN)) {
          if (un.getClosingValue() - s <= 0) {
            // 做空
            rsRight += Math.abs(s - un.getClosingValue());
            line += ",RIGHT";
            rightCount++;
          } else {
            rsWrong += Math.abs(s - un.getClosingValue());
            line += ",WRONG";
            wrongCount++;
          }
        } else if (ci.getInference().equals(CountryMainIndex.INFERENCE_FLAT)) {

        } else if (ci.getInference().equals(CountryMainIndex.INFERENCE_UP)) {
          if (un.getClosingValue() - s <= 0) {
            line += ",RIGHT";
            rsRight += Math.abs(s - un.getClosingValue());
            rightCount++;
          } else {
            line += ",WRONG";
            rsWrong += Math.abs(s - un.getClosingValue());
            wrongCount++;
          }
        }
      } else {
        if (ci.getInference().equals(CountryMainIndex.INFERENCE_DOWN)) {
          if (un.getClosingValue() - s <= 0) {
            line += ",RIGHT";
            rsRight += Math.abs(s - un.getClosingValue());
            rightCount++;
          } else {
            line += ",WRONG";
            wrongCount++;
            rsWrong += Math.abs(s - un.getClosingValue());
          }
        } else if (ci.getInference().equals(CountryMainIndex.INFERENCE_FLAT)) {

        } else if (ci.getInference().equals(CountryMainIndex.INFERENCE_UP)) {
          if (un.getClosingValue() - s >= 0) {
            line += ",RIGHT";
            rsRight += Math.abs(s - un.getClosingValue());
            rightCount++;
          } else {
            line += ",WRONG";
            rsWrong += Math.abs(s - un.getClosingValue());
            wrongCount++;
          }
        }
      }
      line += "\n";
      body += line;

    }
    body += "\n";
    body += "\n";
    body += "正确," + rightCount + ",错误," + wrongCount + ",正确金额," + rsRight + ",错误金额," + rsWrong + ",利润," + (rsRight - rsWrong) + "\n";
    FileStreamUtil.outputString(fos, body, "GBK");
  }
}
