package ant.textile.custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import tcc.utils.None;
import tcc.utils.log.LogService;
import ant.server.AntLogic;
import ant.server.DayAnt;
import engine.util.HTMLParseUtil;
import engine.util.SetENVUtil;
import engine.util.Util;

public class GetCNGeneralCustomData implements DayAnt {

  public static void main(String[] args) {
    SetENVUtil.setENV();
    GetCNGeneralCustomData parse = new GetCNGeneralCustomData();
    try {

      for (int i = 1; i < 20; i++) {
        parse.doAntPages(i);
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

  }

  String queryurl = "http://ccct.org.cn/ICClist.aspx?queryStr=x08x12o8q7x09x01w1z2508y1512z2479z5745zO3w8w1vS9u9v5z8p2x01q9p4x2X12x01w1u9z8w7x08q7x15x15p3x0X14x18x0X14o3w8w1p3p9p3p3x0X14x18x0X14z8w7x08q7x15x15p4q7q8x08x01o8q7x09x01w1w7o7p3q5o6q7x10o7x17z8p5x10x05x13x17x01o3w8w1v2v5v4u8v7v3v0u8u9v2v0v6u8u9v7u8vS8v0v1v6u8u8v0v6v3v4u8v1v1v1v0u8v1v6u8v6u8v2v5v4u8v2v7v3v0v1u8u9v2v3v0v6u8v5v6u8u8v0v6v3v4u8v1v1v1v0u8v1v6u8v6u8v2v0v6vS8v2v5v4u8u8v3v0v1v4u8v3v5u9v0u8v4u9v6v6u8vVVVS8v5u9vS8u8v5v4z8w8q7x16q7p3x0X14x18x0X14o3w8w1p3p9p3p3x0X14x18x0X14z8w8q7x16q7p4q7q8x08x01o8q7x09x01w1w7o7p3q5p7x0X10w8q7x10x0Z8w7x08q7x15x15o3w8w1vS9u9v5";

  public void doAntPages(int i) {
    try {
      Map<String, String> params = new HashMap<String, String>();
      // params.put("__VIEWSTATEGENERATOR", "C6E45410");
      // params.put("__VIEWSTATE",
      // "=/wEPDwULLTE1NTI3NzQ2MDAPZBYCZg9kFgICAQ9kFgYCCw8WAh4HVmlzaWJsZWhkAg0PFgIfAGhkAg8PFgIfAGhkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYBBRRjb250cm9saGVhZCRsb2dvbkJ0blC4fkT0Pd+oDj+ruQi3XInQIIbU");
      // params.put("__EVENTVALIDATION",
      // "/wEWBAKM0ODYDgKmjrPvCwLh/ImPBgLh+qP2CTUaxu5O0+vC/3Z7DEw33i4Cbr6C");
      // params.put("categoryType", "0");
      params.put("lsttable_CurrentPageIndex", String.valueOf(i - 1));
      params.put("lsttable_pageNum", String.valueOf(i));

      // Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like
      // Gecko) Chrome/33.0.1750.146 Safari/537.36

      String listpage = Util.postHTML(queryurl, "GBK", null, params);
      Parser parser = Parser.createParser(listpage, "GB2312");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "class", "DocTitle" });

      List<Tag> tags = HTMLParseUtil.getTags(nl, "td", tableattrs);
      for (Tag tg : tags) {
        Tag atag = HTMLParseUtil.getTag(tg.getChildren(), "a");
        String href = atag.getAttribute("href");
        String title = atag.getAttribute("title");
        System.out.println(title + href);
        File f = getDetailPage(title, "http://ccct.org.cn" + href);
      }

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public File getDetailPage(String title, String href) throws IOException, ParserException {
    File f = AntLogic.getFile(getSource() + "/General/Customer", title + ".html");
    if (!f.exists()) {
      String content1 = Util.getContent(href, "GBK");
      if (None.isBlank(content1))
        return f;
      Parser parser = Parser.createParser(content1, "GBK");
      NodeList nl = parser.parse(null);
      List<String[]> tableattrs = new ArrayList<String[]>();
      tableattrs.add(new String[] { "class", "news" });

      Tag tags = HTMLParseUtil.getTag(nl, "div", tableattrs);
      String content = tags.toHtml();
      AntLogic.saveFile(getSource() + "/General/Customer", title + ".html", content);
    }
    return f;
  }

  @Override
  public String getSource() {

    return "customs.gov.cn";
  }

  @Override
  public void doAnt() {
    doAntPages(1);
  }

}
