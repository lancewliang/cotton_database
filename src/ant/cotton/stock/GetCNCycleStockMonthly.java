package ant.cotton.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.stock.BusinessStockMonth;
import model.entity.stock.CycleStockMonth;
import model.entity.stock.db.CycleStockMonthSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCNCycleStockMonthly implements DayAnt {
  // http://www.china-cotton.org/category-list.php?id=10&page=1

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCNCycleStockMonthly mhhf = new GetCNCycleStockMonthly();
    try {
      mhhf.doAnt();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public void doAnt() {
    try {
      doAntStockMonth();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void doAntStockMonth() throws Exception {
    String url = "http://www.china-cotton.org/category-list.php?id=10&page=1";
    String listpage = Util.getHTML(url, "gb2312");
    Parser parser = Parser.createParser(listpage, "GB2312");
    NodeList nl = parser.parse(null);
    int lastDay = CycleStockMonthSQL.getLastDay(Commodity.getCommodity("�޻�"), Country.getCountry("CHN"), getSource());
    if (lastDay <= 0)
      lastDay = 201001;
    List<String[]> tableattrs = new ArrayList<String[]>();
    tableattrs.add(new String[] { "target", "_blank" });
    List<Tag> lis = HTMLParseUtil.getTags(nl, "a", tableattrs);
    int row = 0;
    for (Tag tr : lis) {
      String linktitle = tr.toPlainTextString();
      if (linktitle.indexOf("ȫ���޻���ת����±�") != -1) {
        String href = tr.getAttribute("href");

        long date = getReportDate(linktitle);
        if (date >= lastDay) {
          String surl = "http://www.china-cotton.org" + href;
          File f = AntLogic.getFile(getSource() + "/CycleStock", date + "-" + linktitle + ".html");
          if (!f.exists()) {
            String html = Util.getHTML(surl, "gb2312");
            AntLogic.saveFile(getSource() + "/CycleStock", date + "-" + linktitle + ".html", html);
          }
          if (f.exists()) {
            InputStream in = new FileInputStream(f);
            String html = FileStreamUtil.getFileContent(in);
            in.close();
            parseContent1(date, html);
          }
        }
      }
    }
  }

  private void parseContent1(long reportDate, String html) throws Exception {
    if (None.isBlank(html))
      throw new Exception("dd");

    double weight1 = getWeight1(html);

    double weight2 = getWeight2(html);
    double weight3 = getWeight3(html);
    double weight4 = getWeight3(html);

    saveBusinessStockMonth(reportDate, "ȫ��", weight1, weight4);
    saveBusinessStockMonth(reportDate, "�ڵ�", weight2);
    saveBusinessStockMonth(reportDate, "�½�", weight3);
    System.out.println(reportDate + "|" + weight1);
  }

  private double getWeight1(String str) throws Exception {
    str = StringUtil.replaceString(str, "<b>", "");
    str = StringUtil.replaceString(str, "</b>", "");
    String regex = "��Ʒ����ת�������Ϊ[0-9]+(.[0-9]{1,2})?���";// �����������ʽ��
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }

    return Double.parseDouble(datastr.substring("��Ʒ����ת�������Ϊ".length(), datastr.length() - 2));

  }

  private double getWeight2(String str) throws Exception {
    str = StringUtil.replaceString(str, "<b>", "");
    str = StringUtil.replaceString(str, "</b>", "");
    String regex = "�����ڵؿ�[0-9]+(.[0-9]{1,2})?���";// �����������ʽ��
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }

    return Double.parseDouble(datastr.substring("�����ڵؿ�".length(), datastr.length() - 2));

  }

  private double getWeight4(String str) throws Exception {
    str = StringUtil.replaceString(str, "<b>", "");
    str = StringUtil.replaceString(str, "</b>", "");
    String regex = "ȫ����Ʒ����ת�������ԼΪ[0-9]+(.[0-9]{1,2})?���";// �����������ʽ��
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }

    return Double.parseDouble(datastr.substring("�ݴ����㣬ȫ����Ʒ����ת�������ԼΪ".length(), datastr.length() - 2));

  }

  private double getWeight3(String str) throws Exception {
    str = StringUtil.replaceString(str, "<b>", "");
    str = StringUtil.replaceString(str, "</b>", "");
    String regex = "�½���[0-9]+(.[0-9]{1,2})?���";// �����������ʽ��
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }

    return Double.parseDouble(datastr.substring("�½���".length(), datastr.length() - 2));

  }

  private void saveBusinessStockMonth(long reportDate, String state, double weight1, double weight2) throws SQLException {
    Date now = new Date();
    CycleStockMonth monthObj = CycleStockMonthSQL.getObj(reportDate, Country.getCountry("CHN"), state, Commodity.getCommodity("�޻�"), getSource());
    if (monthObj == null) {
      monthObj = new CycleStockMonth();
      monthObj.setState(state);
      monthObj.setCommodity(Commodity.getCommodity("�޻�"));
      monthObj.setCountry(Country.getCountry("CHN"));
      monthObj.setReportDate(reportDate);
      monthObj.setSource(getSource());
    }
    monthObj.setUpdatedAt(now);
    monthObj.setUpdatedBy(AntManger.UPDATEBY);
    monthObj.setValue(weight1);
    monthObj.setPredictedValue(weight2);
    monthObj.setWeightUnit(WeightUnit.getWeightUnit("���"));
    CycleStockMonthSQL.save(monthObj);
  }

  private void saveBusinessStockMonth(long reportDate, String state, double weight) throws SQLException {
    Date now = new Date();
    CycleStockMonth monthObj = CycleStockMonthSQL.getObj(reportDate, Country.getCountry("CHN"), state, Commodity.getCommodity("�޻�"), getSource());
    if (monthObj == null) {
      monthObj = new CycleStockMonth();
      monthObj.setState(state);
      monthObj.setCommodity(Commodity.getCommodity("�޻�"));
      monthObj.setCountry(Country.getCountry("CHN"));
      monthObj.setReportDate(reportDate);
      monthObj.setSource(getSource());
    }
    monthObj.setUpdatedAt(now);
    monthObj.setUpdatedBy(AntManger.UPDATEBY);
    monthObj.setValue(weight);
    monthObj.setWeightUnit(WeightUnit.getWeightUnit("���"));
    CycleStockMonthSQL.save(monthObj);
  }

  private long getReportDate(String str) throws NumberFormatException, ParseException {
    String tstr = str.split("��")[1];
    tstr = tstr.replaceAll("��", "");
    tstr = StringUtil.replaceString(tstr, "��", "/");
    tstr = StringUtil.replaceString(tstr, " ", "");
    tstr = tstr.replaceAll("�� ", "");

    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

    return Long.parseLong(sdf2.format(sdf1.parse(tstr)));
  }

  @Override
  public String getSource() {

    return "china-cotton";
  }

}
