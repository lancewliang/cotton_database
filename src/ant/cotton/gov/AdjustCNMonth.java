package ant.cotton.gov;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.gov.country.GovMonth;
import model.entity.gov.country.db.GovDaySQL;
import model.entity.gov.country.db.GovMonthSQL;
import tcc.utils.log.LogService;
import ant.server.AntManger;
import ant.server.DayAnt;

public class AdjustCNMonth implements DayAnt {

  @Override
  public void doAnt() {
    // TODO Auto-generated method stub
    adjustMonth();
  }

  private void adjustMonth() {
    try {
      Calendar cal = Calendar.getInstance();
      cal.setTimeInMillis(System.currentTimeMillis());
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
      int currentMonth = Integer.parseInt(sdf2.format(cal.getTime()));

      Calendar lastMonthCalendar = getDate(currentMonth);
      Calendar currentMonthCalendar = getDate(currentMonth);
      lastMonthCalendar.add(Calendar.MONTH, -3);

      while (!lastMonthCalendar.after(currentMonthCalendar)) {

        lastMonthCalendar.add(Calendar.MONTH, -1);
        String ly = sdf2.format(lastMonthCalendar.getTime());
        lastMonthCalendar.add(Calendar.MONTH, 1);
        String y = sdf2.format(lastMonthCalendar.getTime());

        GovMonth lastmonthObj = GovMonthSQL.queryMonthlyObj(Long.parseLong(ly), Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource(), null);

        GovMonth monthObj = GovMonthSQL.queryMonthlyObj(Long.parseLong(y), Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource(), null);
        if (monthObj == null) {
          monthObj = new GovMonth();
          monthObj.setCommodity(Commodity.getCommodity("棉花"));
          monthObj.setReportDate(Long.parseLong(y));
          monthObj.setCountry(Country.getCountry("CHN"));
          monthObj.setSource(getSource());
        }
        GovDaySQL.getTotalByMonth(lastmonthObj, monthObj);

        monthObj.setReserveValue(lastmonthObj.getReserveValue() + monthObj.getBuyValue() - monthObj.getSellValue());

        monthObj.setUpdatedAt(cal.getTime());
        monthObj.setUpdatedBy(AntManger.UPDATEBY);
        monthObj.setWeightUnit(WeightUnit.getWeightUnit("吨"));
        System.out.println(monthObj.getReportDate() + "|" + monthObj.getBuyValue() + "|" + monthObj.getSellValue() + "|" + monthObj.getReserveValue());
        GovMonthSQL.save(monthObj);
        lastMonthCalendar.add(Calendar.MONTH, 1);
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private Calendar getDate(long month) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    // 利用 DateFormat 來parse 日期的字串
    Date date = sdf.parse(month + "01");
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar;
  }

  @Override
  public String getSource() {

    return "cottonchina";
  }
}
