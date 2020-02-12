package engine.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import tcc.utils.None;

public class HTMLParseUtil {

  public static Tag getTable(NodeList nl, String className) {
    List<String[]> attrs = new ArrayList<String[]>();
    attrs.add(new String[] { "class", className });
    return getTag(nl, "table", attrs);
  }

  public static Tag getTag(NodeList nl, String tagname) {
    return getTag(nl, tagname, null);
  }

  public static Tag getTag(NodeList nl, String tagname, List<String[]> attrs) {
    int size = 1;
    if (!None.isEmpty(attrs)) {
      size += attrs.size();
    }
    NodeFilter tagFilter[] = new NodeFilter[size];
    tagFilter[0] = new TagNameFilter(tagname);
    if (!None.isEmpty(attrs)) {
      int i = 1;
      for (String[] attr : attrs) {
        if(None.isNonBlank(attr[1])){
        tagFilter[i] = new HasAttributeFilter(attr[0], attr[1]);
        }else{
          tagFilter[i] = new HasAttributeFilter(attr[0]);
        }
        i++;
      }
    }
    AndFilter andFilter = new AndFilter();
    andFilter.setPredicates(tagFilter);
    NodeList tl = nl.extractAllNodesThatMatch(andFilter, true);
    if (tl.size() == 0)
      return null;
    Node[] nodes = tl.toNodeArray();
    for (Node n : nodes) {
      Tag t = (Tag) n;
      return t;

    }
    return null;
  }

  public static List<Tag> getTags(Tag tag, String tagname) {
    NodeList nll = new NodeList();
    nll.add(tag);
    return getTags(nll, tagname, null);
  }

  public static List<Tag> getTags(NodeList nl, String tagname) {
    return getTags(nl, tagname, null);
  }

  public static Tag getLink(NodeList nl, String link) {
    NodeFilter tagFilter[] = new NodeFilter[1];
    tagFilter[0] = new TagNameFilter("a");

    AndFilter andFilter = new AndFilter();
    andFilter.setPredicates(tagFilter);
    NodeList tl = nl.extractAllNodesThatMatch(andFilter, true);
    if (tl.size() == 0)
      return null;
    Node[] nodes = tl.toNodeArray();
    for (Node n : nodes) {
      Tag t = (Tag) n;
      String str = t.toPlainTextString();
      if (None.isNonBlank(str) && str.equals(link)) {
        return t;
      }
    }
    return null;
  }

  public static Tag getLinkBySubString(NodeList nl, String link) {
    NodeFilter tagFilter[] = new NodeFilter[1];
    tagFilter[0] = new TagNameFilter("a");

    AndFilter andFilter = new AndFilter();
    andFilter.setPredicates(tagFilter);
    NodeList tl = nl.extractAllNodesThatMatch(andFilter, true);
    if (tl.size() == 0)
      return null;
    Node[] nodes = tl.toNodeArray();
    for (Node n : nodes) {
      Tag t = (Tag) n;
      String str = t.toPlainTextString();
      if (None.isNonBlank(str)) {
        str = str.replace('\n', ' ');
        str = trim2bank(str);
      }
      if (None.isNonBlank(str) && str.indexOf(link) > 0) {
        return t;
      }
    }
    return null;
  }

  public static NodeList getNodeList(NodeList nl, String tagname, List<String[]> attrs) {

    int size = 1;
    if (!None.isEmpty(attrs)) {
      size += attrs.size();
    }

    NodeFilter tagFilter[] = new NodeFilter[size];
    tagFilter[0] = new TagNameFilter(tagname);
    if (!None.isEmpty(attrs)) {
      int i = 1;
      for (String[] attr : attrs) {
        tagFilter[i] = new HasAttributeFilter(attr[0], attr[1]);
        i++;
      }
    }
    AndFilter andFilter = new AndFilter();
    andFilter.setPredicates(tagFilter);
    return nl.extractAllNodesThatMatch(andFilter, true);
  }

  public static List<Tag> getTags(NodeList nl, String tagname, List<String[]> attrs) {
    List<Tag> tags = new ArrayList<Tag>();

    NodeList tl = getNodeList(nl, tagname, attrs);
    if (tl.size() == 0)
      return tags;
    Node[] nodes = tl.toNodeArray();
    for (Node n : nodes) {
      Tag t = (Tag) n;
      tags.add(t);

    }
    return tags;
  }

  public static double getDoubleStringByRegex(String str, String regex, int start, int end) throws Exception {
    // 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    String datastr = null;
    if (m.find()) {
      datastr = m.group();
    } else {
      return -1;
    }

    return Double.parseDouble(datastr.substring(start, datastr.length() - end));

  }

  public static long getDate(String str) throws Exception {
    String regex = "[0-9]{4}年[0-1]{0,1}[0-9]{1}月[0-9]{0,1}[0-9]{1}日";// 这就是正则表达式了
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(str);
    if (m.find()) {
      String sttt = m.group();
      SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
      Date date = sdf1.parse(sttt);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

      return Long.parseLong(sdf2.format(calendar.getTime()));
    } else {
      throw new Exception();
    }

  }

  public static String trim2bank(String str) {
    return trim2bank(str, 1);
  }

  public static String trim2bank(String str, int s) {
    String ret = str;
    String fs = "";
    String ts = "";
    for (int i = 0; i <= s; i++) {
      fs += " ";
    }
    for (int i = 0; i < s; i++) {
      ts += " ";
    }
    if (ret.indexOf(fs) != -1) {
      ret = str.replaceAll(fs, ts);
      ret = trim2bank(ret, s);
    }
    return ret;
  }
}
