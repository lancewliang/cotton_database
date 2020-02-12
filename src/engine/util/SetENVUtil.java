package engine.util;

public class SetENVUtil {
  
  public static void setENV() {
    System.setProperty("javax.net.ssl.trustStore", "D:\\jdk6\\jre\\lib\\security\\jssecacerts");
    System.setProperty("jsse.enableSNIExtension", "false");
    
    System.setProperty("property_manager.properties", "D:\\lwwork\\ExamKing\\economics3\\apps\\site.properties,D:\\lwwork\\ExamKing\\economics3\\apps\\pageloader.properties");


  }
}
