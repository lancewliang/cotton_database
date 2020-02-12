package ant.cotton.ginn;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.production.country.YieldDay;
import model.entity.production.country.db.YieldDaySQL;
import model.entity.sale.country.SaleDay;
import model.entity.sale.country.db.SaleDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

//中国的扎棉花 进度，和销售进度
public class GetCNGinnAndSalesWeely implements DayAnt {

  String gurl = "http://www.cncotton.com/was5/web/search?searchscope=doctitle&timescope=&timescopecolumn=&orderby=-docreltime&channelid=200951&andsen=&total=&orsen=&exclude=&searchword=%E8%B4%AD%E9%94%80%E8%BF%9B%E5%BA%A6%7C&perpage=10&templet=&token=&timeline=";

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCNGinnAndSalesWeely mhhf = new GetCNGinnAndSalesWeely();
    try {
      String ssss = "ffdsfsdf将2013年全国棉花产量调增至699.7万吨asdfsadf";
      // Pattern p =
      // Pattern.compile("[0-9]{4}年[^/t/n/x0B/f/r]*产量[^/t/n/x0B/f/r]*至[0-9]+(.[0-9]{1,2})?万吨");
      // Matcher m = p.matcher(ssss);
      // String datastr = null;
      // if (m.find()) {
      // datastr = m.group();
      // if (None.isNonBlank(datastr)) {
      // System.out.println(datastr);
      // }
      // }

      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  @Override
  public void doAnt() {
    String url = gurl;
    try {
      long lastDay = YieldDaySQL.getLastDay(Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource());
      List<YieldDay> outList1 = new ArrayList<YieldDay>();

      List<SaleDay> outList2 = new ArrayList<SaleDay>();

      if (lastDay <= 0)
        lastDay = 20101001;
      String content = engine.util.Util.getContent(url, "UTF-8");
      Parser parser = Parser.createParser(content, "UTF-8");
      NodeList nl = parser.parse(null);

      List<String[]> attrs = new ArrayList<String[]>();
      attrs.add(new String[] { "class", "list1" });
      attrs.add(new String[] { "target", "_blank" });

      List<Tag> ass = HTMLParseUtil.getTags(nl, "a", attrs);
      for (Tag a : ass) {
        String title = a.toPlainTextString();
        String href = a.getAttribute("href");
        if (( title.indexOf("购销进度|") != -1) && title.endsWith("日）")) {
          String surl = href;
          String[] ss = surl.split("/");
          String fileName=ss[ss.length-1] ;
          File f = AntLogic.getFile(getSource() + "/Ginn/" + getMonthInfo(surl),fileName );
          if (!f.exists()) {
            engine.util.Util.getFile(surl, f);
          }
          InputStream in = new FileInputStream(f);
          String subContent = FileStreamUtil.getFileContent(in, "UTF-8");
          in.close();
          praseContent(surl, subContent, lastDay, true, outList1, outList2);
          
        }
      }

      for (YieldDay obj : outList1) {
        YieldDaySQL.save(obj);
      }
      for (SaleDay obj : outList2) {
        SaleDaySQL.save(obj);
      }
      System.out.println("end");
      // reest yyyymm01's record
      Date now = new Date();
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(now);
      calendar.add(Calendar.MONTH, -1);
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
      int hasMonth1 = 0;
      for (int i = 0; i < 100; i++) {
        long reportDate = Long.parseLong(sdf2.format(calendar.getTime()) + "01");
        checkandupdate01YieldDay(reportDate, now, calendar);
        checkandupdate01SaleDay(reportDate, now, calendar);
        hasMonth1++;

        if (hasMonth1 >= 12)
          break;
        calendar.add(Calendar.MONTH, -1);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  private String getMonthInfo(String url) {
    String[] ss = url.split("/");
    return ss[ss.length - 2];

  }

  private void checkandupdate01YieldDay(long reportDate, Date now, Calendar calendar) throws Exception {
    YieldDay _obj = YieldDaySQL.getObj(reportDate, Country.getCountry("CHN"), Commodity.getCommodity("棉花"), getSource());
    if (_obj == null) {

      long startday = Long.parseLong(calendar.get(Calendar.YEAR) + "0900");

      if (calendar.get(Calendar.MONTH) + 1 < 9) {
        startday = Long.parseLong((calendar.get(Calendar.YEAR) - 1) + "0900");
      }
      YieldDay lastobj = YieldDaySQL.getObjYieldDayByRange(startday, reportDate, Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource(), null, false);
      if (lastobj != null) {
        lastobj.setReportDate(reportDate);
        lastobj.setUpdatedAt(now);
        lastobj.setUpdatedBy(AntManger.UPDATEBY);
        YieldDaySQL.save(lastobj);
      } else {
        YieldDay yieldDay = new YieldDay();
        yieldDay.setCommodity(Commodity.getCommodity("棉花"));
        yieldDay.setCountry(Country.getCountry("CHN"));
        yieldDay.setReportDate(reportDate);
        yieldDay.setSource(getSource());
        yieldDay.setUpdatedAt(now);
        yieldDay.setUpdatedBy(AntManger.UPDATEBY);
        yieldDay.setWeightUnit(WeightUnit.getWeightUnit("万吨"));
        yieldDay.setTotal(0);
        YieldDaySQL.save(yieldDay);
      }
    }
  }

  private void checkandupdate01SaleDay(long reportDate, Date now, Calendar calendar) throws Exception {
    SaleDay _obj = SaleDaySQL.getObj(reportDate, Country.getCountry("CHN"), Commodity.getCommodity("棉花"), getSource());
    if (_obj == null) {

      long startday = Long.parseLong(calendar.get(Calendar.YEAR) + "0900");

      if (calendar.get(Calendar.MONTH) + 1 < 9) {
        startday = Long.parseLong((calendar.get(Calendar.YEAR) - 1) + "0900");
      }
      SaleDay lastobj = SaleDaySQL.getObjSaleDayByRange(startday, reportDate, Commodity.getCommodity("棉花"), Country.getCountry("CHN"), getSource(), null, false);
      if (lastobj != null) {
        lastobj.setReportDate(reportDate);
        lastobj.setUpdatedAt(now);
        lastobj.setUpdatedBy(AntManger.UPDATEBY);
        SaleDaySQL.save(lastobj);
      } else {
        SaleDay saleDay = new SaleDay();
        saleDay.setCommodity(Commodity.getCommodity("棉花"));
        saleDay.setCountry(Country.getCountry("CHN"));
        saleDay.setReportDate(reportDate);
        saleDay.setSource(getSource());
        saleDay.setUpdatedAt(now);
        saleDay.setUpdatedBy(AntManger.UPDATEBY);
        saleDay.setWeightUnit(WeightUnit.getWeightUnit("万吨"));
        saleDay.setTotal(0);

        SaleDaySQL.save(saleDay);
      }
    }
  }

  private void praseContent(String url, String content, long lastDay, boolean loadSub, List<YieldDay> outList1, List<SaleDay> outList2) {
 
 

    try {
      Parser parser = Parser.createParser(content , "UTF-8");
      NodeList nl = parser.parse(null);


      List<String[]> attrs1 = new ArrayList<String[]>();
      attrs1.add(new String[] { "class", "fr" });  
      String plainText1 =   HTMLParseUtil.getTag(nl, "div", attrs1).toPlainTextString().trim();
      plainText1 = plainText1.trim().split(" ")[0].replaceAll("\n", "");
      
      List<String[]> attrs = new ArrayList<String[]>();
      attrs.add(new String[] { "class", "content" });  
      
      String  plainText3 =HTMLParseUtil.getTag(nl, "div", attrs).toPlainTextString().trim();


      long reportDate = 0;
      
      reportDate =  Long.parseLong(plainText1.replaceAll("-", ""));
      System.out.println(lastDay + "|" + reportDate);
      if (lastDay > reportDate)
        return;

      praseMainContent3(plainText3, reportDate, outList1, outList2);
      
    } catch (Exception e) {
      LogService.trace(e, null);
    }


  }

  private void praseMainContent3(String content, long reportDate, List<YieldDay> outList1, List<SaleDay> outList2) throws Exception {
    Date now = new Date();
    double totalBuy = HTMLParseUtil.getDoubleStringByRegex(content, "累计收购皮棉[0-9]+(.[0-9]{1,2})?万吨", "累计收购皮棉".length(), "万吨".length());

    try {
      double totalGinn = HTMLParseUtil.getDoubleStringByRegex(content, "累计加工皮棉[0-9]+(.[0-9]{1,2})?万吨", "累计加工皮棉".length(), "万吨".length());
      YieldDay yieldDay = new YieldDay();
      yieldDay.setCommodity(Commodity.getCommodity("棉花"));
      yieldDay.setCountry(Country.getCountry("CHN"));
      yieldDay.setReportDate(reportDate);
      yieldDay.setSource(getSource());
      yieldDay.setUpdatedAt(now);
      yieldDay.setUpdatedBy(AntManger.UPDATEBY);
      yieldDay.setWeightUnit(WeightUnit.getWeightUnit("万吨"));
      yieldDay.setTotal(totalGinn);
      outList1.add(yieldDay);
      // YieldDaySQL.save(yieldDay);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    //
    try {
      double totalSale = HTMLParseUtil.getDoubleStringByRegex(content, "累计销售皮棉[0-9]+(.[0-9]{1,2})?万吨", "累计销售皮棉".length(), "万吨".length());
      SaleDay saleDay = new SaleDay();
      saleDay.setCommodity(Commodity.getCommodity("棉花"));
      saleDay.setCountry(Country.getCountry("CHN"));
      saleDay.setReportDate(reportDate);
      saleDay.setSource(getSource());
      saleDay.setUpdatedAt(now);
      saleDay.setUpdatedBy(AntManger.UPDATEBY);
      saleDay.setWeightUnit(WeightUnit.getWeightUnit("万吨"));
      saleDay.setTotal(totalSale);
      outList2.add(saleDay);
      // SaleDaySQL.save(saleDay);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

  @Override
  public String getSource() {
    return "cncotton";
  }

  // 累计加工皮棉
  // 累计收购皮棉
  // 累计销售皮棉

}
