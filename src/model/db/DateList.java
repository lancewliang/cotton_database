package model.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tcc.utils.None;

public class DateList {
  List<Long> dates = new ArrayList<Long>();
  String condition = null;
  String conditionlabel = null;

  public String getConditionlabel() {
    return conditionlabel;
  }

  public DateList(List<Long> _dates) {
    if (!None.isEmpty(_dates)) {
      dates.addAll(_dates);
    }
  }

  public DateList(List<Long> _dates, String condition, String label) {
    if (!None.isEmpty(_dates)) {
      dates.addAll(_dates);
    }
    setCondition(condition, label);
  }

  public void setCondition(String condition, String label) {
    this.condition = condition;
    this.conditionlabel = label;
  }

  public void addDates(List<Long> _ndates) {
    for (Long l : _ndates) {
      if (!dates.contains(l)) {
        dates.add(l);
      }
    }
    Collections.sort(dates);
  }

  public List<Long> getDates() {
    return dates;
  }

  public String getCondition() {
    return condition;
  }

  public boolean isSameCondition(DateList d) {

    if (condition != null && d.condition != null && !d.condition.equals(condition)) {
      return false;
    }
    if (conditionlabel != null && d.conditionlabel != null && !d.conditionlabel.equals(conditionlabel)) {
      return false;
    }
    return true;
  }
}
