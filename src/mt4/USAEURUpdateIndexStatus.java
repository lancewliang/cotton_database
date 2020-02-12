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
        if (cmi.getTitle().indexOf("����ҵָ��") != -1 || cmi.getTitle().indexOf("����ҵPMI") != -1 || cmi.getTitle().indexOf("�ѳ��������������ҵָ��") != -1 || cmi.getTitle().indexOf("�¹�����ҵ�ɹ�������ָ��") != -1) {

          update1(cmi);
        } else if (cmi.getTitle().indexOf("����ס������ (�¶Ȼ���)") != -1) {
          update2(cmi);
        } else if (cmi.getTitle().indexOf("���������ָ��") != -1 || cmi.getTitle().indexOf("�������ָ�� (�¶Ȼ���)") != -1) {
          update3(cmi);
        } else if (cmi.getTitle().indexOf("������Ӧ����Э�������ҵָ��") != -1) {
          update4(cmi);
        } else if (cmi.getTitle().indexOf("ADP��ũ����ҵ��ҵ�䶯") != -1 || cmi.getTitle().indexOf("ADP��ҵ����") != -1) {
          update5(cmi);
        } else if (cmi.getTitle().indexOf("ʧҵ��") != -1) {
          update6(cmi);
        } else if (cmi.getTitle().indexOf("��ũ��ҵ�仯") != -1 || cmi.getTitle().indexOf("��ũ��ҵ�˿�") != -1) {
          update7(cmi);
        } else if (cmi.getTitle().indexOf("�������� (�¶Ȼ���)") != -1 || cmi.getTitle().indexOf("�������� (����)") != -1 || cmi.getTitle().indexOf("����������Ʒ���� (�¶Ȼ���)") != -1) {
          update8(cmi);
        } else if (cmi.getTitle().indexOf("���� (�¶Ȼ���)") != -1 || cmi.getTitle().indexOf("�������� (����)") != -1) {
          update9(cmi);
        } else if (cmi.getTitle().indexOf("�������Ѽ۸�ָ�� (�¶Ȼ���)") != -1 || cmi.getTitle().indexOf("���Ѽ۸�ָ�� (ͬ��)") != -1 || cmi.getTitle().indexOf("����������") != -1) {
          update10(cmi);
        } else if (cmi.getTitle().indexOf("����������ֵ") != -1) {
          update11(cmi);
        } else if (cmi.getTitle().indexOf("PPI (����)") != -1||cmi.getTitle().indexOf("PPI") != -1) {
          update12(cmi);
        } else if (cmi.getTitle().indexOf("CPI") != -1 || cmi.getTitle().indexOf("����CPI (����)") != -1||cmi.getTitle().indexOf("CPI (����)")!=-1) {
          update13(cmi);
        } else if (cmi.getTitle().indexOf("�¹�Ifo��ҵ����ָ��") != -1 || cmi.getTitle().indexOf("�¹�IFO��ҵ����ָ��") != -1 || cmi.getTitle().indexOf("ZEW���þ���ָ��") != -1) {
          update14(cmi);
        } else if (cmi.getTitle().indexOf("Ӫ���������") != -1 || cmi.getTitle().indexOf("�������֤") != -1 || cmi.getTitle().indexOf("��������") != -1 || cmi.getTitle().indexOf("��Ʒ�����۶�") != -1) {
          update15(cmi);
        } else if (cmi.getTitle().indexOf("��������") != -1) {
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
        LogService.warn("unknown ������Ӧ����Э������ҵָ�� getCurrency:" + cmi.getCurrency());
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
        LogService.warn("unknown ������Ӧ����Э������ҵָ�� getCurrency:" + cmi.getCurrency());
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  private static void update1(CountryMainIndex cmi) {
    /**
     * ��Ӧ�����о�����ISM������ҵָ�������ٵ������·���������ҵ��������� <br>
     * <b>�����ݱ���Ϊ��ʮ����Ҫ��ʮ�ֿ��ŵľ������ݡ� </b>
     * �����ָ������50�����ڻ�����ӣ��������ڱ�ʾ���ò��������ر��Ǹ����Ƴ��������µ�ʱ�� ������50���ϵ�ָ����ʾ�ܿ�����һ��ʱ��ľ���������
     * ISMָ����ͨ����50�����ҵ�20����ҵ�г���400��˾���е������õġ� <br>
     * ISM�����������Ѿ���α�֤ʵ�� ��˥���ڣ�ISM�ĵײ����ܻ������ھ���ѭ��ת�۵�֮ǰ�����³��֡� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     * 
     * @param cmi
     */
    _updateHighUp(cmi, "");
  }

  private static void update2(CountryMainIndex cmi) {
    /**
     * �������ز�������Э���������ָ����PHSI���Ǻ���ǩԼ���ݶ�̬��ָ�ꡣ ��������Ϊ���ز���̬������ָ�ꡣ
     * ���������������е���סլ����Ԣ�͹��й�Ԣ��ǩԼ���ز���ͬ�� �������֮ǰ��ǩԼ��ͬ���ܿ���Ϊ���ۡ� ��ָ��δ�����½����� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     * 
     */
    _updateHighUp(cmi, "%");
  }

  private static void update3(CountryMainIndex cmi) {
    /**
     * ���������ָ����CPI���Ǻ�������Ʒ������۸�仯��ָ�ꡣ <br>
     * CPI�������ߵĽǶȺ����۸�仯�� <br>
     * ���Ǻ���ŷԪ���������Ʊ仯��ͨ�����ͱ䶯����Ҫ������
     * ���ָ����Ԥ�ڸ��ߣ���Ӧ��ΪŷԪǿ��/���ǣ�ͨ���Կ�ͨ�����͵ķ�������������ʣ�������������ʣ��������ָ����Ԥ�ڸ��ͣ���Ӧ��ΪŷԪ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update4(CountryMainIndex cmi) {
    /**
     * ��Ӧ�����о�����ISM��������ҵָ����Ҳ��ΪISM����ҵ�������ٵ������·����ķ�����ҵ��������� <br>
     * �κγ���50��ָ����ʾ����С��50�ı�ʾ��С�� <br>
     * ������GDP��Ӱ���ISM����ҵָ����ҪС�öࡣ <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateflat(cmi);

  }

  private static void update5(CountryMainIndex cmi) {
    /**
     * ADP���Զ����ݴ���˾��ȫ����ҵ������ݴ����Ŵ�Լ40��������ҵ�ͻ��Ļ��ܺ������������ݵ��Ӽ��������˷�ũҵ<b>˽Ӫ����</b>
     * ��ҵ������¶ȱ仯�� �˱���������������ҵ���ݵ�����ǰ���棬�Ƕ�������ũҵ��ҵ�������ݽ���Ԥ���һ���ܺõĲο�ָ�ꡣ ��ָ����ܼ����ȶ��� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "K");
  }

  private static void update6(CountryMainIndex cmi) {
    /**
     * ʧҵ���Ǻ�������ʧҵ�ĵ����ڻ���Ѱ�Ҿ�ҵ��Ը�⹤��������ռ���Ͷ����İٷֱȡ� <br>
     * �߰ٷֱȱ�ʾ�Ͷ����г������� �Ͱٷֱȶ��������Ͷ����г���һ�������ָʾ����Ӧ��Ϊ��Ԫ���á�
     */
    _updateDownUp(cmi, "%");
  }

  private static void update7(CountryMainIndex cmi) {
    /**
     * ��ũҵ��ҵ�����Ǻ������з�ũҵ��ҵ�����¾�ҵ�����仯��ָ�ꡣ �ܷ�ũҵ��ҵ������ָ������Լ������������������ֵ80���Ĺ��������� <br>
     * <b>�����ù�����������Ҫ�ĵ������ݣ��ñ��汻��Ϊ�ṩ����Ѿ��ù۵㡣</b>. <br>
     * ��ҵ�������¶ȱ仯���޸Ŀ��ܺܲ��ȶ��� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateDownUp(cmi, "K");
  }

  private static void update8(CountryMainIndex cmi) {
    /**
     * �����������۶��Ǹ��������������ͺ͹�ģ�������̵꣨�������������̵꣩��������������õ����������۳�����������Ʒ���۶�䶯���������¶Ⱥ�����
     * �������ѿ�֧����Ҫָ�꣬��������������ָ���йأ�����Ϊ���������õ��ٶ�ָ�ꡣ <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update9(CountryMainIndex cmi) {
    /**
     * �������۶��Ǹ��������������ͺ͹�ģ�������̵��������������õ����������۳�����������Ʒ������õ��¶Ⱥ�����
     * �������ѿ�֧����Ҫָ�꣬��������������ָ���йأ�����Ϊ���������õ��ٶ�ָ�ꡣ <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update10(CountryMainIndex cmi) {
    /**
     * �������������ָ����CPI���Ǻ�����ʳƷ����Դ֮�������Ʒ������۸��ָ�ꡣ <br>
     * CPI�������ߵĽǶȺ����۸�仯�� <br>
     * ���Ǻ��������������Ʊ仯��ͨ�����͵���Ҫ������ <br>
     * ���ָ����Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ�ͨ���Կ�ͨ�����͵ķ�������������ʣ�������������ʣ��������ָ����Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update11(CountryMainIndex cmi) {
    /**
     * ����������ֵ��GDP���Ƕ��ھ��û����㷺�Ĳⶨ��Ҳ�Ǿ��ý����ȵ�һ����Ҫָ�ꡣ <br>
     * GDPÿ�꣨���ȱ仯 x 4���İٷֱȱ仯��ʾ�����徭�õ������ʡ� <br>
     * ��������������GDP������һ�����֣���GDP��Ӱ����� <br>
     * ������ÿ��֮�䶼���ܻ��кܴ�ͬ�� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update12(CountryMainIndex cmi) {
    /**
     * �����߼۸�ָ����PPI���ǹ�������������õ�����Ʒ���Ͷ��������ۼ۸�ƽ���䶯��ͨ��ָ�ꡣ <br>
     * PPI�������ߵĽǶȺ����۸�仯�� <br>
     * PPI��������������������е��飺 ��ҵ����Ʒ���ӹ��׶εĹ�˾�� <br>
     * ��������������Ʒ���Ͷ���֧������ʱ�����Ǻܿ��ܽ��ò������ӵĳɱ��Ӹ������ߣ�����PPI����Ϊ�����������ָ��������ָ�ꡣ <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update13(CountryMainIndex cmi) {
    /**
     * �������������ָ����CPI���Ǻ�����ʳƷ����Դ֮�������Ʒ������۸��ָ�ꡣ <br>
     * CPI�������ߵĽǶȺ����۸�仯�� <br>
     * ���Ǻ��������������Ʊ仯��ͨ�����͵���Ҫ������ <br>
     * ���ָ����Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ�ͨ���Կ�ͨ�����͵ķ�������������ʣ�������������ʣ��������ָ����Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "%");
  }

  private static void update14(CountryMainIndex cmi) {
    /**
     * �¹�ŷ���о����ģ�ZEW���ľ��þ��������¹�����Ͷ���ߵ������� <br>
     * ָ����0���ϱ�ʾ�ֹۣ�֮�±�ʾ���ۡ� <br>
     * ���Ǿ�Ӫ״��������ָ�ꡣ ��������ͨ���Դ�Լ350λ�¹��Ļ���Ͷ���ߺͷ����ҽ��е������õġ� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��ΪŷԪǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��ΪŷԪ����/������
     */
    _updateHighUp(cmi, "");
  }

  private static void update15(CountryMainIndex cmi) {
    /**
     * Ӫ�����˵����������ɵ��½�����Ŀ������ Ӫ������Ǻ������ز��г�״������Ҫָ�ꡣ <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "M");
  }

  private static void update16(CountryMainIndex cmi) {
    /**
     * ���������Ǻ��������۳�����ס�������������ݵ�ָ�ꡣ <br>
     * �˱��������ڷ����������ز��г���ʵ�������������ڷ������徭�á� <br>
     * �������۱���ܲ��ȶ������кܴ�̶ȵ��޸ġ� <br>
     * �����ָ���Ԥ�ڸ��ߣ���Ӧ��Ϊ��Ԫǿ��/���ǣ��������ָ���Ԥ�ڸ��ͣ���Ӧ��Ϊ��Ԫ����/������
     */
    _updateHighUp(cmi, "K");
  }
}
