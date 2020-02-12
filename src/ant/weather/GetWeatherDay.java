package ant.weather;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import model.constant.LengthUnit;
import model.constant.WeatherRegion;
import model.entity.weather.WeatherDay;
import model.entity.weather.db.WeatherDaySQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetWeatherDay implements DayAnt {
	List<WeatherRegion> regions = null;
	boolean isAlwaysGet = true;

	public static void main(String[] args) throws IOException {
		SetENVUtil.setENV();
		String[] gg = new String[] { "us" };
		for (String g : gg) {
			GetWeatherDay sss = new GetWeatherDay(g);
			sss.doAnt();
		}

		// WeatherRegion region =
		// WeatherRegion.getWeatherRegion("BurVT,America");

		// sss.getMonth(2013, 1, region);
		// File f = new File("C:\\Users\\Administrator\\Desktop\\a.htm");
		// InputStream in = new FileInputStream(f);
		// String content = FileStreamUtil.getFileContent(in);
		// in.close();
		// sss.praseContent(region, content, f);
	}

	public GetWeatherDay(String contry) {
		regions = new ArrayList();

		for (WeatherRegion region : WeatherRegion.getWeatherRegions()) {
			if (region.getContry().equals(contry)) {
				if ((region.getNumber1() == region.getNumber2() && region
						.getNumber1() == -1)) {

				} else {
					regions.add(region);
				}
			}
		}
	}

	public void doAnt() {

		try {
			LogService.msg("   WeatherRegion count :" + regions.size());
			int i = 0;
			for (WeatherRegion region : regions) {
				i++;
				LogService.msg("   WeatherRegion  " + i + " :"
						+ region.getWeatherRegion());
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");
				cal.add(Calendar.MONTH, 1);
				int currentMonth = Integer.parseInt(sdf2.format(cal.getTime()));

				int lastMonth = WeatherDaySQL.getLastMonth(region);
				if (lastMonth <= 0)
					lastMonth = 201201;
				boolean dotask = false;

				Calendar lastMonthCalendar = getDate(lastMonth);
				lastMonthCalendar.add(Calendar.MONTH, -2);
				Calendar currentMonthCalendar = getDate(currentMonth);
				currentMonthCalendar.add(Calendar.MONTH, 2);
				while (!lastMonthCalendar.after(currentMonthCalendar)) {
					int y = lastMonthCalendar.get(Calendar.YEAR);
					int m = lastMonthCalendar.get(Calendar.MONTH) + 1;
					getMonth(y, m, region, isAlwaysGet, 0);
					lastMonthCalendar.add(Calendar.MONTH, 1);
					dotask = true;
				}

				if (!dotask) {
					LogService.msg(" not dotask :" + region.getWeatherRegion());
				}
			}

			LogService.msg(" get WeatherRegion count :" + i);
		} catch (Exception e) {
			LogService.trace(e, null);
		}
	}

	private void getMonth(int year, int month, WeatherRegion region,
			boolean isGet, int time) throws IOException {
		try {
			String[] ss = region.getWeatherRegion().split(",");
			String c = ss[ss.length - 1].trim();

			File f = AntLogic.getFile(
					getSource() + "/" + c + "/" + region.getWeatherRegion(),
					year + "-" + month + "-" + region.getContry() + "-"
							+ region.getWeatherRegion() + ".htm");

			String url = getURL(year, month, region);

			String content = null;
			try {Map<String, String> propertys = new HashMap();
			propertys.put("Host", "www.accuweather.com");
				content = engine.util.Util.getHTML(url, "UTF-8",propertys);
			} catch (IOException e) {
				getMonth(year, month, region, true, ++time);
			}
			if (None.isNonBlank(content)) {
				content = removeScript(content);
				if (f.exists() && !isGet) {
					InputStream in = new FileInputStream(f);
					String content2 = FileStreamUtil.getFileContent(in);
					in.close();

					if (!content2.equals(content)) {
						AntLogic.saveFile(
								getSource() + "/" + c + "/"
										+ region.getWeatherRegion(), year + "-"
										+ month + "-" + region.getContry()
										+ "-" + region.getWeatherRegion()
										+ ".htm", content);
					}

				} else {
					AntLogic.saveFile(
							getSource() + "/" + c + "/"
									+ region.getWeatherRegion(), year + "-"
									+ month + "-" + region.getContry() + "-"
									+ region.getWeatherRegion() + ".htm",
							content);

				}
				boolean value = praseContent(region, content, f);
				if (!value && time <= 5) {
					getMonth(year, month, region, true, ++time);
				}
			}
		} catch (Exception e) {
			LogService.trace(e, null);
		}
	}

	private String removeScript(String str) {
		while (true) {
			int s = str.indexOf("<script");
			int e = str.indexOf("</script", s);
			if (s <= 0 && e <= 0) {
				break;
			}
			str = str.substring(0, s) + str.substring(e + "</script>".length());
		}
		str = removeExtar(str);
		return str;
	}

	private String removeExtar(String str) {
		int s = str.indexOf("<div id=\"offers\" class=\"offers-extras\">");
		int e = str.indexOf("<div class=\"panel scroll-panel\">", s);
		if (s <= 0 && e <= 0) {
			return str;
		}
		str = str.substring(0, s) + str.substring(e);

		return str;

	}

	private boolean praseContent(WeatherRegion region, String content, File f) {

		Date now = new Date();
		Parser parser = Parser.createParser(content, "GB2312");
		NodeList nl;
		try {
			nl = parser.parse(null);

			Tag table = HTMLParseUtil.getTable(nl, "stats");
			List<WeatherDay> list = new ArrayList<WeatherDay>();
			List<Tag> trs = HTMLParseUtil.getTags(table.getChildren(), "tr");
			int r = 0;
			for (Tag tr : trs) {
				r++;
				if (r == 1) {
					continue;
				}
				try {
					Tag th = HTMLParseUtil.getTag(tr.getChildren(), "th");
					String thText = th.toPlainTextString();
					long reportDate = DateUtil.getYYYYMMDD(thText.substring(3));
					List<Tag> tds = HTMLParseUtil.getTags(tr.getChildren(),
							"td");
					WeatherDay weatherDay = WeatherDaySQL.getObj(reportDate,
							region, getSource());
					if (weatherDay == null) {
						weatherDay = new WeatherDay();
						weatherDay.setWeatherRegion(region);
						weatherDay.setReportDate(reportDate);
					}
					String highStr = tds.get(0).toPlainTextString();
					String lowStr = tds.get(1).toPlainTextString();
					lowStr = lowStr.replaceAll("&nbsp;", "").trim();
					highStr = highStr.replaceAll("&nbsp;", "").trim();
					if (None.isBlank(lowStr) && None.isBlank(highStr)) {
						continue;
					}
					weatherDay.setHigh(getIntT(highStr));
					weatherDay.setLow(getIntT(lowStr));
					String setPrecipSTRs = tds.get(2).toPlainTextString()
							.trim();
					String[] setPrecipStr = setPrecipSTRs.toLowerCase().trim()
							.split(" ");
					String snowSTRs = tds.get(3).toPlainTextString().trim();

					String[] setSnowStr = snowSTRs.toLowerCase().trim()
							.split(" ");
					if (!"N/A".toLowerCase()
							.equals(setPrecipSTRs.toLowerCase())) {
						weatherDay.setPrecip(getIntI(setPrecipStr[0]));
						weatherDay.setPrecipUnit(LengthUnit
								.getLengthUnit(setPrecipStr[1]));
					} else {
						weatherDay
								.setPrecipUnit(LengthUnit.getLengthUnit("in"));
					}
					if (!"N/A".toLowerCase().equals(snowSTRs.toLowerCase())) {
						weatherDay.setSnow(getIntI(setSnowStr[0]));
						weatherDay.setSnowUnit(LengthUnit
								.getLengthUnit(setSnowStr[1]));
					} else {
						weatherDay.setSnowUnit(LengthUnit.getLengthUnit("in"));
					}
					weatherDay.setForecast(tds.get(4).toPlainTextString()
							.replaceAll("&nbsp;", ""));
					weatherDay.setAvgHigh(getIntT(tds.get(5)));
					weatherDay.setAvgLow(getIntT(tds.get(6)));
					weatherDay.setSource(getSource());
					weatherDay.setUpdatedAt(now);
					weatherDay.setUpdatedBy(AntManger.UPDATEBY);
					list.add(weatherDay);

				} catch (Exception e) {
					LogService.trace(e, f.getAbsolutePath());
				}
			}
			boolean remove = false;
			for (Iterator<WeatherDay> it = list.iterator(); it.hasNext();) {
				WeatherDay weatherDay = it.next();
				if (weatherDay.getAvgHigh() == 0 && weatherDay.getAvgLow() == 0
						&& weatherDay.getHigh() == 0
						&& weatherDay.getLow() == 0
						&& weatherDay.getPrecip() == 0
						&& weatherDay.getSnow() == 0) {

					it.remove();
					remove = true;
				}
			}
			if (remove && None.isEmpty(list)) {
				return false;
			} else {
				for (WeatherDay weatherDay : list) {
					WeatherDaySQL.save(weatherDay);
				}
			}
			return true;
		} catch (Exception e1) {
			LogService.trace(e1, f.getAbsolutePath());
		}
		return true;
	}

	private int getIntT(Tag td) {
		try {
			String str = td.toPlainTextString();
			str = str.replaceAll("&#176;", "");
			String ss = str.replaceAll("&nbsp;", "");
			return Integer.parseInt(ss);
		} catch (Exception e) {
			return DBUtil.NULLINT;
		}
	}

	private int getIntT(String td) {
		try {
			String ss = td.replaceAll("&#176;", "");
			ss = ss.replaceAll("&nbsp;", "");
			return Integer.parseInt(ss);
		} catch (Exception e) {
			return DBUtil.NULLINT;
		}
	}

	private double getIntI(String str) {
		try {
			String ss = str.replaceAll("&#176;", "");
			ss = ss.replaceAll("&nbsp;", "");
			return Double.parseDouble(ss);
		} catch (Exception e) {
			return DBUtil.NULLFLOAT;
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

	private String getURL(int year, int month, WeatherRegion region) {

		return "http://173.205.6.98/en/" + region.getContry() + "/"
				+ region.getKey() + "/" + region.getNumber1() + "/"
				+ DateUtil.getENByMonth(month).toLowerCase() + "-weather/"
				+ region.getNumber2() + "?monyr=" + month + "/1/" + year
				+ "&view=table";
	}

	@Override
	public String getSource() {
		return "accuweather";

	}

}
