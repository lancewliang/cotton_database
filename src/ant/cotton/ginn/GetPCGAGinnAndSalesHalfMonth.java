package ant.cotton.ginn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.file.FileStreamUtil;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.AntManger;
import ant.server.DayAnt;
import engine.util.DateUtil;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;

public class GetPCGAGinnAndSalesHalfMonth implements DayAnt {
	String listurl = "http://medialinepakistan.com/?s=Karachi+Cotton+Association+Official+Spot+Rate+for+Local+Dealings+in+Pak+Rupees";

	public static void main(String[] args) {
		SetENVUtil.setENV();
		GetPCGAGinnAndSalesHalfMonth mhhf = new GetPCGAGinnAndSalesHalfMonth();
		mhhf.doAnt();
	}

	@Override
	public void doAnt() {
		// TODO Auto-generated method stub
		try {

			Map<Long, YieldDay> outList1 = new HashMap<Long, YieldDay>();
			Map<Long, SaleDay> outList2 = new HashMap<Long, SaleDay>();
			getFileList(outList1, outList2);

			for (YieldDay obj : outList1.values()) {
				// System.out.println("YieldDay" + obj.getReportDate() + "|" +
				// obj.getTotal());
				YieldDaySQL.save(obj);
			}
			for (SaleDay obj : outList2.values()) {

				// System.out.println("SaleDay" + obj.getReportDate() + "|" +
				// obj.getTotal());
				SaleDaySQL.save(obj);
			}
		} catch (Exception e) {
			LogService.trace(e, null);
		}
	}

	private void getFileList(Map<Long, YieldDay> outList1,
			Map<Long, SaleDay> outList2) {

		try {

			long lastDay = YieldDaySQL.getLastDay(Commodity.getCommodity("棉花"),
					Country.getCountry("PK"), getSource());
			if (lastDay <= 0)
				lastDay = 20101001;

			for (int u = 1; u < 2; u++) {
				String url = "http://medialinepakistan.com/page/"
						+ u
						+ "/?s=Karachi+Cotton+Association+Official+Spot+Rate+for+Local+Dealings+in+Pak+Rupees";
				String content = engine.util.Util.getHTML(url, "UTF-8");

				Parser parser = Parser.createParser(content, "GB2312");
				NodeList nl = parser.parse(null);
				List<String[]> dirElcs = new ArrayList<String[]>();
				dirElcs.add(new String[] { "class", "post_title" });
				List<Tag> dirdivs = HTMLParseUtil.getTags(nl, "h4", dirElcs);
				for (Tag dir : dirdivs) {
					NodeList chs = dir.getChildren();
					Tag tag = HTMLParseUtil.getTag(chs, "a");
					String href = tag.getAttribute("href");

					String filename = "";
					String dateSTR = "";
					String year = "";

					try {
						dateSTR = tag
								.toPlainTextString()
								.substring(
										"Karachi Cotton Association Official Spot Rate for Local Dealings in Pak Rupees of"
												.length()).trim();
					} catch (Exception e) {
					}

					if (!None.isBlank(dateSTR)) {
						year = dateSTR.substring(6);
						filename = tag.toPlainTextString() + ".htm";

					} else {
						year = "o";
						String[] ss = href.split("/");
						filename = ss[ss.length - 1] + ".htm";
					}
					File f = AntLogic.getFile(getSource() + "/ARRIVAL CROP/"
							+ year, filename);
					if (!f.exists()) {
						AntLogic.saveFile(
								getSource() + "/ARRIVAL CROP/" + year,
								filename,
								engine.util.Util.getHTML(href, "UTF-8"));
					}
					InputStream in = new FileInputStream(f);

					String subcontent = FileStreamUtil.getFileContent(in);
					in.close();
					try {
						boolean v = praseContent(lastDay, subcontent, outList1,
								outList2);
						if (!v) {

							System.out.println("break");
							return;
						}
					} catch (Exception e) {
						LogService.trace(e, subcontent);
					}
				}
			}
		} catch (Exception e) {
			LogService.trace(e, null);
		}

	}

	private boolean praseContent(long lastDay, String content,
			Map<Long, YieldDay> outList1, Map<Long, SaleDay> outList2)
			throws ParserException {
		String str = content;
		int areaStart, areaEnd;
		String htmlStr, areaStr;
		while ((areaStart = str.indexOf("<pre>")) != -1) {

			areaEnd = str.indexOf("</pre>", areaStart + 1);
			String body = str.substring(areaStart + "<pre>".length(), areaEnd);
			if (body.contains("ARRIVAL CROP")) {

				return prasePre(lastDay, body, outList1, outList2);
			}

			str = str.substring(areaEnd + "</pre>".length());
		}
		while ((areaStart = str.indexOf("<p>")) != -1) {

			areaEnd = str.indexOf("</p>", areaStart + 1);
			String body = "";
			try {
				body = str.substring(areaStart + "<p>".length(), areaEnd);
			} catch (Exception e) {
			}
			if (body.contains("ARRIVAL CROP")) {

				return prasePre(lastDay, body, outList1, outList2);
			}
			try {
				str = str.substring(areaEnd + "</p>".length());
			} catch (Exception e) {
			}
		}
		return true;
	}

	private boolean prasePre(long lastDay, String content,
			Map<Long, YieldDay> outList1, Map<Long, SaleDay> outList2) {

		BufferedReader br = null;
		String line = "";
		ParseInfo parseInfo = new ParseInfo();
		try {

			br = new BufferedReader(new StringReader(content));
			long reportDate = 0;
			long ArrivalsTotal = 0;
			long SaleTotal = 0;
			while ((line = br.readLine()) != null) {
				if (parseInfo.stepIndex < 0) {
					if (line.indexOf("ARRIVAL CROP") != -1) {
						parseInfo.stepIndex = 0;

					}
				} else if (parseInfo.stepIndex == 0) {
					if (line.indexOf("As on") != -1) {
						String dateSTR = line.trim().replaceAll("(final)", "")
								.substring("As on ".length(), 50).trim();

						reportDate = getDate(dateSTR);

						if (lastDay > reportDate)
							return false;
						parseInfo.stepIndex = 1;

					}
				} else if (parseInfo.stepIndex == 1) {
					if (line.startsWith("Total Arrivals")) {
						line = HTMLParseUtil.trim2bank(line);
						String[] ss = line.split(" ");
						ArrivalsTotal += Long.parseLong(ss[4].replaceAll(",",
								""));
						parseInfo.stepIndex = 2;

					}
				} else if (parseInfo.stepIndex == 2) {
					line = line.replaceAll("\"", "");
					if (line.startsWith("Sales to TCP")) {
						line = HTMLParseUtil.trim2bank(line);
						String[] ss = line.split(" ");
						try {
							SaleTotal += Long.parseLong(ss[5].replaceAll(",",
									""));
						} catch (Exception e) {
						}
						// SaleTotal += Long.parseLong(ss[3]);
						parseInfo.stepIndex = 3;

					}
				} else if (parseInfo.stepIndex == 3) {
					line = line.replaceAll("\"", "");
					if (line.startsWith("to Exporters")) {
						line = HTMLParseUtil.trim2bank(line);
						String[] ss = line.split(" ");
						if (ss.length == 8) {
							SaleTotal += Long.parseLong(ss[3].replaceAll(",",
									""));
						} else if (ss.length == 9) {
							SaleTotal += Long.parseLong(ss[4].replaceAll(",",
									""));
						} else {

							SaleTotal += Long.parseLong(ss[4].replaceAll(",",
									""));
						}
						parseInfo.stepIndex = 4;

					}
				} else if (parseInfo.stepIndex == 4) {
					line = line.replaceAll("\"", "");
					if (line.startsWith("to Mills")) {
						line = HTMLParseUtil.trim2bank(line);
						String[] ss = line.split(" ");
						SaleTotal += Long.parseLong(ss[4].replaceAll(",", ""));
						parseInfo.stepIndex = 5;

					}
				} else if (parseInfo.stepIndex == 5) {

					if (line.startsWith("Source: PCGA")) {
						saveTotal(reportDate, ArrivalsTotal, SaleTotal,
								outList1, outList2);
						parseInfo.stepIndex = 6;
						return true;
					}
				}
			}

		} catch (Exception e) {
			LogService.trace(e, content);

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	private void saveTotal(long reportDate, long totalGinn, long totalSale,
			Map<Long, YieldDay> outList1, Map<Long, SaleDay> outList2)
			throws Exception {
		if (reportDate <= 0)
			return;
		Date now = new Date();

		YieldDay yieldDay = new YieldDay();
		yieldDay.setCommodity(Commodity.getCommodity("棉花"));
		yieldDay.setCountry(Country.getCountry("PK"));
		yieldDay.setReportDate(reportDate);
		yieldDay.setSource(getSource());
		yieldDay.setUpdatedAt(now);
		yieldDay.setUpdatedBy(AntManger.UPDATEBY);
		yieldDay.setWeightUnit(WeightUnit.getWeightUnit("包,170KG"));
		yieldDay.setTotal(totalGinn);
		outList1.put(reportDate, yieldDay);

		SaleDay saleDay = new SaleDay();
		saleDay.setCommodity(Commodity.getCommodity("棉花"));
		saleDay.setCountry(Country.getCountry("PK"));
		saleDay.setReportDate(reportDate);
		saleDay.setSource(getSource());
		saleDay.setUpdatedAt(now);
		saleDay.setUpdatedBy(AntManger.UPDATEBY);
		saleDay.setWeightUnit(WeightUnit.getWeightUnit("包,170KG"));
		saleDay.setTotal(totalSale);
		outList2.put(reportDate, saleDay);

	}

	private long getDate(String dateSTR) throws Exception {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		try {
			String str = "";
			if (dateSTR.indexOf(",") != -1) {
				String[] datestr = dateSTR.split(",");
				String[] md = datestr[0].trim().split(" ");

				String year = datestr[1];
				if (year.indexOf("-") != -1) {
					year = datestr[1].split("-")[1].trim();
				}

				str = DateUtil.getMonthByEN(md[0]) + "/" + md[1] + "/" + year;
			} else {
				String[] md = dateSTR.trim().split(" ");
				str = DateUtil.getMonthByEN(md[0]) + "/" + md[1] + "/" + md[2];
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			// 利用 DateFormat 來parse 日期的字串
			Date date = sdf.parse(str);

			calendar.setTime(date);

		} catch (Exception e) {
			System.out.println(dateSTR);
			throw e;
		}
		return Long.parseLong(sdf2.format(calendar.getTime()));
	}

	@Override
	public String getSource() {
		return "pcga";

	}

	class ParseInfo {
		int stepIndex = -100;
		int Crop_and_State_line_size = 0;

	}
}
