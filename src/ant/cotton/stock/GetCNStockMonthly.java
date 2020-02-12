package ant.cotton.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.stock.BusinessStockMonth;
import model.entity.stock.IndustrialStockDayMonth;
import model.entity.stock.IndustrialStockMonth;
import model.entity.stock.db.BusinessStockMonthSQL;
import model.entity.stock.db.IndustrialStockDayMonthSQL;
import model.entity.stock.db.IndustrialStockMonthSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.StringUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.cotton.custom.CottonchinaUtil;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetCNStockMonthly implements DayAnt {
	public static void main(String[] args) {
		SetENVUtil.setENV();
		GetCNStockMonthly mhhf = new GetCNStockMonthly();
		try {
			String SessionId = CottonchinaUtil.getSessionId();
			mhhf.doAntIndustrialStockMonth(SessionId);
			mhhf.doAntBusinessStockMonth(SessionId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doAnt() {
		try {
			String SessionId = CottonchinaUtil.getSessionId();
			doAntIndustrialStockMonth(SessionId);
			doAntBusinessStockMonth(SessionId);
		} catch (Exception e) {
			LogService.trace(e, null);
		}

	}

	private void doAntBusinessStockMonth(String SessionId) throws Exception {
		// offset=25
		String url = "http://www.cottonchina.org/news/newsser.php?newskey=%C3%DE%C6%F3%B5%F7%B2%E9&sertype=title&newstype=&imageField322.x=0&imageField322.y=0";
		String listpage = CottonchinaUtil.getHTML(url, SessionId);
		Parser parser = Parser.createParser(listpage, "GB2312");
		NodeList nl = parser.parse(null);
		int lastDay = BusinessStockMonthSQL.getLastDay(
				Commodity.getCommodity("�޻�"), Country.getCountry("CHN"),
				getSource());
		List<String[]> tableattrs = new ArrayList<String[]>();
		tableattrs.add(new String[] { "target", "_blank" });
		tableattrs.add(new String[] { "class", "a3" });
		List<Tag> lis = HTMLParseUtil.getTags(nl, "a", tableattrs);
		int row = 0;
		for (Tag tr : lis) {
			String linktitle = tr.toPlainTextString();
			if (linktitle.indexOf("��������飺") != -1) {
				String href = tr.getAttribute("href");
				String[] params = href.split("=");
				long date = getReportDate(params[params.length - 1]);
				if (date >= lastDay) {
					String surl = "http://www.cottonchina.org/news/" + href;
					File f = AntLogic.getFile("cottonchina/BusinessStock", date
							+ "-" + linktitle + ".html");
					if (!f.exists()) {
						String html = CottonchinaUtil.getHTML(surl, SessionId);
						AntLogic.saveFile("cottonchina/BusinessStock", date
								+ "-" + linktitle + ".html", html);
					}
					if (f.exists()) {
						InputStream in = new FileInputStream(f);
						String html = FileStreamUtil.getFileContent(in);
						in.close();
						parseContent1(html);
					}
				}
			}
		}
	}

	private void doAntIndustrialStockMonth(String SessionId) throws Exception {
		String url = "http://www.cottonchina.org/news/newsser.php?newskey=%B7%C4%C6%F3%B5%F7%B2%E9&sertype=title&newstype=&imageField322.x=9&imageField322.y=13";
		String listpage = CottonchinaUtil.getHTML(url, SessionId);
		Parser parser = Parser.createParser(listpage, "GB2312");
		NodeList nl = parser.parse(null);
		int lastDay = IndustrialStockMonthSQL.getLastDay(
				Commodity.getCommodity("�޻�"), Country.getCountry("CHN"),
				getSource());
		List<String[]> tableattrs = new ArrayList<String[]>();
		tableattrs.add(new String[] { "target", "_blank" });
		tableattrs.add(new String[] { "class", "a3" });
		List<Tag> lis = HTMLParseUtil.getTags(nl, "a", tableattrs);
		int row = 0;
		for (Tag tr : lis) {
			String linktitle = tr.toPlainTextString();
			if (linktitle.indexOf("�·�����飺") != -1) {
				String href = tr.getAttribute("href");
				String[] params = href.split("=");
				long date = getReportDate(params[params.length - 1]);
				if (date >= lastDay) {
					String surl = "http://www.cottonchina.org/news/" + href;
					File f = AntLogic.getFile(getSource() + "/IndustrialStock",
							date + "-" + linktitle + ".html");
					if (!f.exists()) {
						String html = CottonchinaUtil.getHTML(surl, SessionId);
						AntLogic.saveFile(getSource() + "/IndustrialStock",
								date + "-" + linktitle + ".html", html);
					}
					if (f.exists()) {
						InputStream in = new FileInputStream(f);
						String html = FileStreamUtil.getFileContent(in);
						in.close();
						parseContent2(html);
					}
				}
			}
		}

	}

	private void parseContent1(String html) throws Exception {
		long reportDate = getDate(html, "����");
		double weight = getWeight1(html);
		if (weight <= 0) {
			weight = getWeight11(html);
		}
		saveBusinessStockMonth(reportDate, weight);
		System.out.println(reportDate + "|" + weight);
	}

	private void parseContent2(String html) throws Exception {
		long reportDate = getDate(html, "����");
		double weight = getWeight2(html);
		double day1 = getDays1(html);
		double day2 = getDays2(html);
		saveIndustrialStockDayMonth(reportDate, Commodity.getCommodity("ɴ��"),
				day1);
		saveIndustrialStockDayMonth(reportDate, Commodity.getCommodity("��"),
				day2);
		saveIndustrialStockMonth(reportDate, weight);
		System.out.println(reportDate + "|" + weight + "|" + day1 + "|" + day2);
	}

	private void saveIndustrialStockMonth(long reportDate, double weight)
			throws SQLException {
		Date now = new Date();
		IndustrialStockMonth monthObj = IndustrialStockMonthSQL.getObj(
				reportDate, Country.getCountry("CHN"),
				Commodity.getCommodity("�޻�"), getSource());
		if (monthObj == null) {
			monthObj = new IndustrialStockMonth();
			monthObj.setCommodity(Commodity.getCommodity("�޻�"));
			monthObj.setCountry(Country.getCountry("CHN"));
			monthObj.setReportDate(reportDate);
			monthObj.setSource(getSource());
		}
		monthObj.setUpdatedAt(now);
		monthObj.setUpdatedBy(AntManger.UPDATEBY);
		monthObj.setValue(weight);
		monthObj.setWeightUnit(WeightUnit.getWeightUnit("���"));
		IndustrialStockMonthSQL.save(monthObj);
	}

	private void saveIndustrialStockDayMonth(long reportDate, Commodity c,
			double days) throws SQLException {
		Date now = new Date();
		IndustrialStockDayMonth monthObj = IndustrialStockDayMonthSQL.getObj(
				reportDate, Country.getCountry("CHN"), c, getSource());
		if (monthObj == null) {
			monthObj = new IndustrialStockDayMonth();
			monthObj.setCommodity(c);
			monthObj.setCountry(Country.getCountry("CHN"));
			monthObj.setReportDate(reportDate);
			monthObj.setSource(getSource());
		}
		monthObj.setUpdatedAt(now);
		monthObj.setUpdatedBy(AntManger.UPDATEBY);
		monthObj.setDays(days);
		IndustrialStockDayMonthSQL.save(monthObj);
	}

	private void saveBusinessStockMonth(long reportDate, double weight)
			throws SQLException {
		Date now = new Date();
		BusinessStockMonth monthObj = BusinessStockMonthSQL.getObj(reportDate,
				Country.getCountry("CHN"), Commodity.getCommodity("�޻�"),
				getSource());
		if (monthObj == null) {
			monthObj = new BusinessStockMonth();
			monthObj.setCommodity(Commodity.getCommodity("�޻�"));
			monthObj.setCountry(Country.getCountry("CHN"));
			monthObj.setReportDate(reportDate);
			monthObj.setSource(getSource());
		}
		monthObj.setUpdatedAt(now);
		monthObj.setUpdatedBy(AntManger.UPDATEBY);
		monthObj.setValue(weight);
		monthObj.setWeightUnit(WeightUnit.getWeightUnit("���"));
		BusinessStockMonthSQL.save(monthObj);
	}

	private long getDate(String str, String key) throws Exception {
		String regex = "[0-9]{4}��[0-1]{0,1}[0-9]{1}��[0-9]{0,1}[0-9]{1}��";// �����������ʽ��
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		if (m.find()) {
			String sttt = m.group();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy��MM��dd��");

			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(sdf1.parse(sttt));
			calendar.add(Calendar.MONTH, -1);
			int mm = calendar.get(Calendar.MONTH) + 1;
			if (str.indexOf(mm + "��" + key + "����") != -1) {

				return Long.parseLong(sdf2.format(calendar.getTime()));
			}
			throw new Exception();
		} else {
			LogService.err("get wrong date|"+ str + "|" + key);
			throw new Exception();
		}

	}

	private double getWeight1(String str) throws Exception {
		str = StringUtil.replaceString(str, "<b>", "");
		str = StringUtil.replaceString(str, "</b>", "");
		String regex = "�µ�ȫ���޻���ҵ���[0-9]+���";// �����������ʽ��
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String datastr = null;
		if (m.find()) {
			datastr = m.group();
		} else {
			return -1;
		}

		return Double.parseDouble(datastr.substring("�µ�ȫ���޻���ҵ���".length(),
				datastr.length() - 2));

	}

	private double getWeight11(String str) throws Exception {
		str = StringUtil.replaceString(str, "<b>", "");
		str = StringUtil.replaceString(str, "</b>", "");
		String regex = "��ҵ�������[0-9]+���";// �����������ʽ��
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String datastr = null;
		if (m.find()) {
			datastr = m.group();
		} else {
			return -1;
		}

		return Double.parseDouble(datastr.substring("��ҵ�������".length(),
				datastr.length() - 2));

	}

	private double getWeight2(String str) throws Exception {
		String regex = "��֯��ҵ�ڿ��޻������Ϊ[0-9]+\\.?[0-9]+���";// �����������ʽ��
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String datastr = null;
		if (m.find()) {
			datastr = m.group();
		} else {
			return -1;
		}
		int x = datastr.indexOf("��֯��ҵ�ڿ��޻������Ϊ");
		if (x != -1) {
			return Double.parseDouble(datastr.substring(
					x + "��֯��ҵ�ڿ��޻������Ϊ".length(), datastr.length() - 2));
		} else {
			throw new Exception();
		}

	}

	private double getDays1(String str) throws Exception {
		String regex = "��֯��ҵɴ�߿��[0-9]+\\.?[0-9]+��";// �����������ʽ��
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String datastr = null;
		if (m.find()) {
			datastr = m.group();
		} else {
			return -1;
		}

		return Double.parseDouble(datastr.substring("��֯��ҵɴ�߿��".length(),
				datastr.length() - 2));

	}

	private double getDays2(String str) throws Exception {
		String regex = "�������[0-9]+\\.?[0-9]+��";// �����������ʽ��
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		String datastr = null;
		if (m.find()) {
			datastr = m.group();
		} else {
			return -1;
		}

		return Double.parseDouble(datastr.substring("�������".length(),
				datastr.length() - 2));

	}

	private long getReportDate(String str) throws NumberFormatException,
			ParseException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

		return Long.parseLong(sdf2.format(sdf1.parse(str)));
	}

	@Override
	public String getSource() {

		return "cottonchina";
	}
	//
}
