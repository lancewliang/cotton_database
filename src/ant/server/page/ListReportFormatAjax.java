package ant.server.page;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.db.InstructionStatus;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.utils.xml.dom.DOMUtil;
import tcc.webfw.page.Page;
import ant.server.AntInstruction;
import ant.server.AntManger;
import ant.server.db.AntInstructionSQL;

public class ListReportFormatAjax extends Page {

  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);
    try {

      if ("doAnt".equals(command)) {
        String name = request.getParameter("name");
        if (None.isNonBlank(name)) {
          AntInstruction instracutionStatus = AntInstructionSQL.getObjByName(name);
          if (instracutionStatus == null) {
            instracutionStatus = AntManger.getAnters(name);
            instracutionStatus.setStatus(InstructionStatus.STATUS_START);
            AntInstructionSQL.insert(instracutionStatus);
          } else {
            if (!instracutionStatus.isWorking()) {
              instracutionStatus.setStatus(InstructionStatus.STATUS_START);
              AntInstructionSQL.update(instracutionStatus);
            }
          }
        }
      }

      print(pfx, session);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return super.processCommand(command, session, request, response);
  }

  private void print(String pfx, SessionObj session) {
    try {
      Document doc = DOMUtil.createDocument();

      Element rootEl = doc.createElement("root");

      doc.appendChild(rootEl);

      for (AntInstruction instracution : AntManger.getAnters()) {
        Element formatEl = doc.createElement("AntInstruction");
        formatEl.setAttribute("name", instracution.getName());
        AntInstruction instracutionStatus = AntInstructionSQL.getObjByName(instracution.getName());
        if (instracutionStatus == null) {
          instracutionStatus = AntManger.getAnters(instracution.getName());
          instracutionStatus.setStatus("STATUS_RESET");
          AntInstructionSQL.insert(instracutionStatus);
        }
        setStatus(instracutionStatus, formatEl);
        rootEl.appendChild(formatEl);
      }

      session.setPageSessionValue(pfx + ".pfdoc", doc);

    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  //
  private void setStatus(AntInstruction instracution, Element element) throws SQLException {

    if (instracution != null) {
      String statuses = instracution.getStatus();
      if (statuses.equals(InstructionStatus.STATUS_FAILED)) {
        element.setAttribute("status", "FAILED");
      } else if (statuses.equals("STATUS_RESET")) {
        element.setAttribute("status", "RESET");
      } else if (statuses.equals(InstructionStatus.STATUS_START)) {
        element.setAttribute("status", "START & WAITING");

      } else if (statuses.equals(InstructionStatus.STATUS_PROCESS)) {
        element.setAttribute("status", "PROCESS");
      } else {
        element.setAttribute("status", "SUCCESS");
      }
      element.setAttribute("isWorking", instracution.isWorking() ? "TRUE" : "FALSE");
    } else {
      element.setAttribute("status", "NONE");
    }
  }
}
