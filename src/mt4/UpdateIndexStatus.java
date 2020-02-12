package mt4;

import engine.util.SetENVUtil;

public class UpdateIndexStatus {
  public static void main(String[] args) {
    SetENVUtil.setENV();
    USAEURUpdateIndexStatus.updateInference();
  }

}
