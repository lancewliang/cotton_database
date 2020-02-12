package ant.cotton.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.custom.country.ImportExportMonth;
import model.entity.custom.country.db.ImportExportMonthSQL;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.log.LogService;
import ui.util.PDFBOX;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCustomPKMonthly implements DayAnt {

  public static void main(String[] args) throws Exception {
		SetENVUtil.setENV();
		GetCustomPKMonthly exp = new GetCustomPKMonthly();
		exp.praselist(1);
		// exp.praselist(3);
		// exp.praselist(3);
		// exp.praselist(4);
		// exp.praselist(5);
	}

	@Override
	public void doAnt() {
		praselist(1);

	}

	private void praselist(int pagei) {
		String url = "http://www.pbs.gov.pk/trade-detail";
		if (pagei > 1) {
			url += "?page=" + (pagei - 1);
		}
		try {
			long lastDay = ImportExportMonthSQL.getFROMCountryLastDay(
					Commodity.getCommodity("棉花"), Country.getCountry("PK"),
					getSource());
			String listpage = null;
			int getsize = 0;
			while (true) {
				try {
					listpage = Util.getHTML(url, "utf-8");
					getsize++;
				} catch (ConnectException ee) {

				} catch (UnknownHostException ee) {

				}
				if (None.isNonBlank(listpage) || getsize >= 3) {
					break;
				}
			}
			if (None.isBlank(listpage)) {
				throw new Exception(" no listpage");
			}
			Parser parser = Parser.createParser(listpage, "GB2312");
			NodeList nl = parser.parse(null);
			List<Tag> tags = HTMLParseUtil.getTags(nl, "a");
			for (Tag tag : tags) {
				String href = tag.getAttribute("href");
				if (None.isNonBlank(href) && href.endsWith("pdf")) {
					try {

						String text = tag.toPlainTextString();
						File f = null;
						String[] sss = href.split("/");
						if (text.toLowerCase().startsWith(
								"IMPORT_".toLowerCase())) {
							f = AntLogic.getFile(getSource() + "/import/"
									+ sss[sss.length - 3], text);
						} else if (text.toLowerCase().startsWith(
								"EXPORT_".toLowerCase())) {
							f = AntLogic.getFile(getSource() + "/export/"
									+ sss[sss.length - 3], text);
						}
						if (f != null) {
							if (!f.exists()) {
								engine.util.Util.getFile(href, f);
							}

							if (text.toLowerCase().startsWith(
									"IMPORT_".toLowerCase())) {
								praseContent(lastDay, Country.getCountry("全球"),
										Country.getCountry("PK"), f);

							} else if (text.toLowerCase().startsWith(
									"EXPORT_".toLowerCase())) {
								praseContent(lastDay, Country.getCountry("PK"),
										Country.getCountry("全球"), f);
							}

						}
					} catch (Exception e) {
						LogService.trace(e, null);
					}
				}
			}
		} catch (Exception e) {
			LogService.trace(e, null);
		}
	}

	private void praseContent(long lastDay, Country fromCountry,
			Country toCountry, File f) {
		BufferedReader br = null;
		String line = "";
		ParseInfo parseInfo = new ParseInfo();
		try {
			String monthStr = f.getName().replaceAll(".pdf", "")
					.substring(f.getName().indexOf('_') + 1);
			Calendar calendar = getDate(monthStr);
			long toMonthDay = getDate(calendar);

			if (!(toMonthDay >= lastDay))
				return;

			PDFBOX parse = new PDFBOX();
			String content = parse.getPDFText(f);
			br = new BufferedReader(new StringReader(content));

			while ((line = br.readLine()) != null) {
				if (parseInfo.stepIndex < 0) {
					if (line.indexOf("RAW COTTON") != -1) {
						line = line.substring(line.indexOf("RAW COTTON"));
						parseInfo.stepIndex = 0;
						line = HTMLParseUtil.trim2bank(line);
						line = StringUtil.replaceString(line, ",", "");
						String[] sss = line.trim().split(" ");
						LogService.msg(f.getAbsolutePath());
						LogService.msg(line);

						double currentMonthTotal = Double.parseDouble(sss[3]);
						saveMonthInfo(toMonthDay, currentMonthTotal,
								fromCountry, toCountry);
						calendar.add(calendar.MONTH, -1);
						long lastMonthDay = getDate(calendar);
						double lastMonthTotal = Double.parseDouble(sss[6]);
						saveMonthInfo(lastMonthDay, lastMonthTotal,
								fromCountry, toCountry);
						calendar.add(calendar.MONTH, 1);
						calendar.add(calendar.YEAR, -1);
						long lastYearDay = getDate(calendar);
						double lastyearMonthTotal = Double.parseDouble(sss[9]);
						saveMonthInfo(lastYearDay, lastyearMonthTotal,
								fromCountry, toCountry);
						break;
					}

				}
				// take Crop and State

			}

		} catch (Exception e) {
			LogService.trace(e, "");
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

	private void saveMonthInfo(long reportDate, double total,
			Country fromCountry, Country toCountry) throws SQLException {
		Date now = new Date();

		ImportExportMonth impexpDay = ImportExportMonthSQL.getObj(reportDate,
				toCountry, fromCountry, Commodity.getCommodity("棉花"),
				getSource());
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

	private long getDate(Calendar calendar) throws ParseException {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

		return Long.parseLong(sdf2.format(calendar.getTime()));
	}

	private Calendar getDate(String dateSTR) throws Exception {
		String[] dateSTRs = null;

		if (dateSTR.indexOf('_') != -1) {
			dateSTRs = dateSTR.split("_");
		} else if(dateSTR.indexOf(',') != -1){
			dateSTRs = dateSTR.split(",") ;
		} else {
			 
			dateSTRs = new String[] {
					dateSTR.substring(0, dateSTR.length() - 4),
					dateSTR.substring(dateSTR.length() - 4) };
		}
		Calendar calendar = null;
		try {
			int mm = DateUtil.getMonthByEN(dateSTRs[0].trim());
		
		String str = mm + "/" + dateSTRs[1].trim();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");

		// 利用 DateFormat 來parse 日期的字串
		Date date = sdf.parse(str);
		calendar =Calendar.getInstance();
		calendar.setTime(date);
		} catch (Exception e) {
			LogService.trace(e, dateSTR);
			throw e;
		}
		return calendar;

	}

	@Override
	public String getSource() {

		return "pbs.gov.pk";
	}
}
