package ui.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class PDFBOX {
  public PDDocument document = null;

  public static void main(String[] args) throws IOException {
    String file = "C:/Users/Administrator/Desktop/01May13ArrivalReport.pdf";
    PDFBOX parse = new PDFBOX();
    String ddddddd = parse.getPDFText(new File(file));

    System.out.println(ddddddd);
  }

  public String getPDFText(File file) throws IOException {
    InputStream is = new FileInputStream(file);
    this.document = this.parseDocument(is);
    // 获取页数
    List pages = this.document.getDocumentCatalog().getAllPages();
    int pageSize = pages.size();
    // System.out.println("pdf页数:" + pageSize);
    return this.getPdfText();

  }

  public PDDocument parseDocument(InputStream input) throws IOException {
    PDDocument document = PDDocument.load(input);
    if (document.isEncrypted()) {
      try {
        document.decrypt("");
      } catch (CryptographyException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (InvalidPasswordException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return document;
  }

  /*
   * 抽取pdf文本内容
   */
  private String getPdfText() throws IOException {
    StringWriter osw = new StringWriter();
    PDFTextStripper stripper = new PDFTextStripper();
    BufferedWriter bw = new BufferedWriter(osw);
    stripper.setShouldSeparateByBeads(true);
    stripper.writeText(document, bw);
    bw.close();
    document.close();
    return osw.toString();

  }
}
