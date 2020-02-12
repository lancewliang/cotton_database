package mt4;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.entity.macroeconomic.CountryMainIndex;
import model.entity.macroeconomic.db.CountryMainIndexSQL;
import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.log.LogService;

public class USAEURUpdateIndexStatus {

  public static void updateInference() {

    try {
      List<CountryMainIndex> objs = CountryMainIndexSQL.getNULLinferenceObjs("USD", "EUR", "Wallstartcn");
      Map<String, String> unknowMap = new HashMap();
      for (CountryMainIndex cmi : objs) {
        if (cmi.getTitle().indexOf("制造业指数") != -1 || cmi.getTitle().indexOf("制造业PMI") != -1 || cmi.getTitle().indexOf("费城联邦储备银行制造业指数") != -1 || cmi.getTitle().indexOf("德国制造业采购经理人指数") != -1) {

          update1(cmi);
        } else if (cmi.getTitle().indexOf("待定住房销售 (月度环比)") != -1) {
          update2(cmi);
        } else if (cmi.getTitle().indexOf("消费者物价指数") != -1 || cmi.getTitle().indexOf("生产物价指数 (月度环比)") != -1) {
          update3(cmi);
        } else if (cmi.getTitle().indexOf("美国供应管理协会非制造业指数") != -1) {
          update4(cmi);
        } else if (cmi.getTitle().indexOf("ADP非农制造业就业变动") != -1 || cmi.getTitle().indexOf("ADP就业人数") != -1) {
          update5(cmi);
        } else if (cmi.getTitle().indexOf("失业率") != -1) {
          update6(cmi);
        } else if (cmi.getTitle().indexOf("非农就业变化") != -1 || cmi.getTitle().indexOf("非农就业人口") != -1) {
          update7(cmi);
        } else if (cmi.getTitle().indexOf("核心零售 (月度环比)") != -1 || cmi.getTitle().indexOf("核心零售 (月率)") != -1 || cmi.getTitle().indexOf("核心耐用商品订单 (月度环比)") != -1) {
          update8(cmi);
        } else if (cmi.getTitle().indexOf("零售 (月度环比)") != -1 || cmi.getTitle().indexOf("零售销售 (月率)") != -1) {
          update9(cmi);
        } else if (cmi.getTitle().indexOf("核心消费价格指数 (月度环比)") != -1 || cmi.getTitle().indexOf("消费价格指数 (同比)") != -1 || cmi.getTitle().indexOf("消费者信心") != -1) {
          update10(cmi);
        } else if (cmi.getTitle().indexOf("国内生产总值") != -1) {
          update11(cmi);
        } else if (cmi.getTitle().indexOf("PPI (月率)") != -1||cmi.getTitle().indexOf("PPI") != -1) {
          update12(cmi);
        } else if (cmi.getTitle().indexOf("CPI") != -1 || cmi.getTitle().indexOf("核心CPI (月率)") != -1||cmi.getTitle().indexOf("CPI (月率)")!=-1) {
          update13(cmi);
        } else if (cmi.getTitle().indexOf("德国Ifo商业景气指数") != -1 || cmi.getTitle().indexOf("德国IFO商业景气指数") != -1 || cmi.getTitle().indexOf("ZEW经济景气指数") != -1) {
          update14(cmi);
        } else if (cmi.getTitle().indexOf("营建许可总数") != -1 || cmi.getTitle().indexOf("建造许可证") != -1 || cmi.getTitle().indexOf("成屋销售") != -1 || cmi.getTitle().indexOf("成品房销售额") != -1) {
          update15(cmi);
        } else if (cmi.getTitle().indexOf("新屋销售") != -1) {
          update16(cmi);
        } else {
          unknowMap.put(cmi.getTitle(), "true");

        }
      }
      for (String s : unknowMap.keySet()) {
        LogService.warn("unknown title:" + s);
      }
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

  public static void _updateflat(CountryMainIndex cmi) {
    try {
      cmi.setInference(CountryMainIndex.INFERENCE_FLAT);
      CountryMainIndexSQL.save(cmi);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
  }

  private static double getDoubleData(String data, String removeString) {
    if (None.isNonBlank(removeString)) {
      data = StringUtil.replaceString(data, removeString, "");
    }
    data = data.trim();
    return Double.parseDouble(data);
  }

  public static void _updateDownUp(CountryMainIndex cmi, String removeString) {
    try {
      if (cmi.getCurrency().equals("USD") || cmi.getCurrency().equals("EUR")) {
        if (getDoubleData(cmi.getActualValue(), removeString) < getDoubleData(cmi.getForecastValue(), removeString)) {
          cmi.setInference(CountryMainIndex.INFERENCE_UP);
        } else if (getDoubleData(cmi.getActualValue(), removeString) == getDoubleData(cmi.getForecastValue(), removeString)) {
          if (getDoubleData(cmi.getActualValue(), removeString) < getDoubleData(cmi.getPreviousValue(), removeString)) {
            cmi.setInference(CountryMainIndex.INFERENCE_UP);
          } else {
            cmi.setInference(CountryMainIndex.INFERENCE_FLAT);
          }
        } else {
          if (getDoubleData(cmi.getActualValue(), removeString) < getDoubleData(cmi.getPreviousValue(), removeString)) {
            cmi.setInference(CountryMainIndex.INFERENCE_UP);
          } else {
            cmi.setInference(CountryMainIndex.INFERENCE_DOWN);
          }
        }

        CountryMainIndexSQL.save(cmi);

      } else {
        LogService.warn("unknown 美国供应管理协会制造业指数 getCurrency:" + cmi.getCurrency());
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public static void _updateHighUp(CountryMainIndex cmi, String removeString) {
    try {
      if (cmi.getCurrency().equals("USD") || cmi.getCurrency().equals("EUR")) {
        if (getDoubleData(cmi.getActualValue(), removeString) > getDoubleData(cmi.getForecastValue(), removeString)) {
          cmi.setInference(CountryMainIndex.INFERENCE_UP);
        } else if (getDoubleData(cmi.getActualValue(), removeString) == getDoubleData(cmi.getForecastValue(), removeString)) {
          if (getDoubleData(cmi.getActualValue(), removeString) >= getDoubleData(cmi.getPreviousValue(), removeString)) {
            cmi.setInference(CountryMainIndex.INFERENCE_UP);
          } else {
            cmi.setInference(CountryMainIndex.INFERENCE_FLAT);
          }
        } else {
          if (getDoubleData(cmi.getActualValue(), removeString) >= getDoubleData(cmi.getPreviousValue(), removeString)) {
            cmi.setInference(CountryMainIndex.INFERENCE_UP);
          } else {
            cmi.setInference(CountryMainIndex.INFERENCE_DOWN);
          }
        }

        CountryMainIndexSQL.save(cmi);

      } else {
        LogService.warn("unknown 美国供应管理协会制造业指数 getCurrency:" + cmi.getCurrency());
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private static void update1(CountryMainIndex cmi) {
    /**
     * 供应管理研究所（ISM）制造业指数所跟踪的是上月发生的制造业活动的数量。 <br>
     * <b>此数据被认为是十分重要且十分可信的经济数据。 </b>
     * 如果此指数低于50，由于活动的增加，它倾向于表示经济不景气，特别是该趋势持续几个月的时候。 高企于50以上的指数表示很可能有一段时间的经济增长。
     * ISM指数是通过向50个国家的20种行业中超过400公司进行调查而获得的。 <br>
     * ISM的领先性质已经多次被证实。 在衰退期，ISM的底部可能会领先于经济循环转折点之前几个月出现。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     * 
     * @param cmi
     */
    _updateHighUp(cmi, "");
  }

  private static void update2(CountryMainIndex cmi) {
    /**
     * 美国房地产交易商协会成屋销售指数（PHSI）是衡量签约房屋动态的指标。 它被用作为房地产动态的领先指标。
     * 该数据来自于现有单户住宅、公寓和共有公寓的签约房地产合同。 交易完成之前，签约合同不能看作为销售。 该指数未包含新建筑。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     * 
     */
    _updateHighUp(cmi, "%");
  }

  private static void update3(CountryMainIndex cmi) {
    /**
     * 消费者物价指数（CPI）是衡量消费品和劳务价格变化的指标。 <br>
     * CPI从消费者的角度衡量价格变化。 <br>
     * 它是衡量欧元区购买趋势变化和通货膨胀变动的重要方法。
     * 如果指数比预期更高，则应认为欧元强势/看涨（通常对抗通货膨胀的方法就是提高利率，这可以吸引外资），而如果指数比预期更低，则应认为欧元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update4(CountryMainIndex cmi) {
    /**
     * 供应管理研究所（ISM）非制造业指数（也称为ISM服务业）所跟踪的是上月发生的非制造业活动的数量。 <br>
     * 任何超过50的指数表示扩大，小于50的表示缩小。 <br>
     * 它对于GDP的影响比ISM制造业指数的要小得多。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateflat(cmi);

  }

  private static void update5(CountryMainIndex cmi) {
    /**
     * ADP（自动数据处理公司）全美就业报告根据代表着大约40万美国企业客户的汇总和匿名人数数据的子集，衡量了非农业<b>私营部门</b>
     * 就业情况的月度变化。 此报告在政府发布就业数据的两天前出版，是对政府非农业就业人数数据进行预测的一个很好的参考指标。 该指标可能极不稳定。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "K");
  }

  private static void update6(CountryMainIndex cmi) {
    /**
     * 失业率是衡量美国失业的但仍在积极寻找就业并愿意工作的人数占总劳动力的百分比。 <br>
     * 高百分比表示劳动力市场薄弱。 低百分比对美国的劳动力市场是一种正面的指示，并应认为美元看好。
     */
    _updateDownUp(cmi, "%");
  }

  private static void update7(CountryMainIndex cmi) {
    /**
     * 非农业就业人数是衡量所有非农业企业中上月就业人数变化的指标。 总非农业就业人数是指生产大约整个美国国内生产总值80％的工人数量。 <br>
     * <b>它是用工报告中最重要的单项数据，该报告被认为提供了最佳经济观点。</b>. <br>
     * 就业人数的月度变化和修改可能很不稳定。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateDownUp(cmi, "K");
  }

  private static void update8(CountryMainIndex cmi) {
    /**
     * 核心零售销售额是根据美国各种类型和规模的零售商店（不含汽车销售商店）抽样调查中所获得的零售商所售出的所有消费品销售额变动而作出的月度衡量。
     * 它是消费开支的重要指标，并与消费者信心指数有关，被认为是美国经济的速度指标。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update9(CountryMainIndex cmi) {
    /**
     * 零售销售额是根据美国各种类型和规模的零售商店抽样调查中所获得的零售商所售出的所有消费品金额而获得的月度衡量。
     * 它是消费开支的重要指标，并与消费者信心指数有关，被认为是美国经济的速度指标。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update10(CountryMainIndex cmi) {
    /**
     * 核心消费者物价指数（CPI）是衡量除食品和能源之外的消费品和劳务价格的指标。 <br>
     * CPI从消费者的角度衡量价格变化。 <br>
     * 它是衡量美国购买趋势变化和通货膨胀的重要方法。 <br>
     * 如果指数比预期更高，则应认为美元强势/看涨（通常对抗通货膨胀的方法就是提高利率，这可以吸引外资），而如果指数比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update11(CountryMainIndex cmi) {
    /**
     * 国内生产总值（GDP）是对于经济活动的最广泛的测定，也是经济健康度的一个重要指标。 <br>
     * GDP每年（季度变化 x 4）的百分比变化显示了整体经济的增长率。 <br>
     * 现在消费是美国GDP中最大的一个部分，对GDP的影响最大。 <br>
     * 该数据每季之间都可能会有很大不同。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update12(CountryMainIndex cmi) {
    /**
     * 生产者价格指数（PPI）是国内生产者所获得的消费品和劳动力的销售价格平均变动的通胀指标。 <br>
     * PPI从消费者的角度衡量价格变化。 <br>
     * PPI对以下三个生产领域进行调查： 工业、商品及加工阶段的公司。 <br>
     * 当生产者向消费品和劳动力支出更多时，他们很可能将该部分增加的成本加给消费者，所以PPI被认为是消费者物价指数的领先指标。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update13(CountryMainIndex cmi) {
    /**
     * 核心消费者物价指数（CPI）是衡量除食品和能源之外的消费品和劳务价格的指标。 <br>
     * CPI从消费者的角度衡量价格变化。 <br>
     * 它是衡量美国购买趋势变化和通货膨胀的重要方法。 <br>
     * 如果指数比预期更高，则应认为美元强势/看涨（通常对抗通货膨胀的方法就是提高利率，这可以吸引外资），而如果指数比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "%");
  }

  private static void update14(CountryMainIndex cmi) {
    /**
     * 德国欧洲研究中心（ZEW）的经济景气决定德国机构投资者的情绪。 <br>
     * 指数在0以上表示乐观，之下表示悲观。 <br>
     * 它是经营状况的领先指标。 该数据是通过对大约350位德国的机构投资者和分析家进行调查而获得的。 <br>
     * 如果该指标比预期更高，则应认为欧元强势/看涨，而如果该指标比预期更低，则应认为欧元弱势/看跌。
     */
    _updateHighUp(cmi, "");
  }

  private static void update15(CountryMainIndex cmi) {
    /**
     * 营建许可说明了政府许可的新建筑项目数量。 营建许可是衡量房地产市场状况的重要指标。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "M");
  }

  private static void update16(CountryMainIndex cmi) {
    /**
     * 新屋销售是衡量上月售出的新住房按年计算的数据的指标。 <br>
     * 此报告有助于分析美国房地产市场的实力，进而有助于分析整体经济。 <br>
     * 新屋销售报告很不稳定，会有很大程度的修改。 <br>
     * 如果该指标比预期更高，则应认为美元强势/看涨，而如果该指标比预期更低，则应认为美元弱势/看跌。
     */
    _updateHighUp(cmi, "K");
  }
}
