package ui.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import engine.util.SetENVUtil;

import tcc.utils.log.Log2File;
import tcc.utils.log.LogService;
import tcc.utils.property.PropertyManager;

/**
 * Example filter that sets the character encoding to be used in parsing the
 * incoming request
 */
public class SetCharacterEncodingFilter implements Filter {
  public final static String LOG_PATH = "logservice.path";
  public final static String LOG_FILE = "logservice.file";
  public final static String LOG_TIMESTAMP_PROPERTY = "logservice.timestamp.enabled";
  public final static String LOG_CLUSTER_PROPERTY = "logservice.cluster.enabled";
  public final static String LOG_VERBOSE_PROPERTY = "logservice.verbose.enabled";
  public final static String LOG_ERROR_PROPERTY = "logservice.error.enabled";
  public final static String LOG_WARN_PROPERTY = "logservice.warn.enabled";
  public final static String LOG_TRACE_PROPERTY = "logservice.trace.enabled";
  public final static String LOG_MSG_PROPERTY = "logservice.msg.enabled";
  public final static String LOG_LOG_PROPERTY = "logservice.log.enabled";
  public final static String LOG_SQL_PROPERTY = "logservice.sql.enabled";
  static {
    SetENVUtil.setENV();
    String server_config_path = PropertyManager.getString("export.server.xml", "export.server.xml");
    tcc.batch.server.Bootstrap.main(new String[] { server_config_path });
    String logPath = PropertyManager.getString(LOG_PATH, null);
    if (logPath != null) {
      try {
        String logFile = PropertyManager.getString(LOG_FILE, "cclog");
        System.out.println("----- setLogPath: " + logPath + "/" + logFile);
        if (!logPath.endsWith("/")) {
          logPath += "/";
        }
        logFile = logPath + logFile;
        Log2File log2File = new Log2File();
        log2File.setLogFiles(logFile, logFile, logFile);
        LogService.registerProvider(log2File);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    boolean _verbose = true;
    boolean _logErrors = PropertyManager.getBoolean(LOG_ERROR_PROPERTY, _verbose);

    LogService.setQuiet(LogService.ERR, !_logErrors);

    _logErrors = PropertyManager.getBoolean(LOG_TRACE_PROPERTY, _verbose);

    LogService.setQuiet(LogService.TRACE, !_logErrors);

    _logErrors = PropertyManager.getBoolean(LOG_MSG_PROPERTY, _verbose);

    LogService.setQuiet(LogService.MSG, !_logErrors);

    _logErrors = PropertyManager.getBoolean(LOG_LOG_PROPERTY, _verbose);

    LogService.setQuiet(LogService.LOG, !_logErrors);

    _logErrors = PropertyManager.getBoolean(LOG_WARN_PROPERTY, _verbose);

    LogService.setQuiet(LogService.WARN, !_logErrors);
    _logErrors = PropertyManager.getBoolean(LOG_SQL_PROPERTY, _verbose);
    LogService.setQuiet(LogService.SQL, !_logErrors);
  }

  /**
   * Take this filter out of service.
   */
  public void destroy() {
  }

  /**
   * Select and set (if specified) the character encoding to be used to
   * interpret request parameters for this request.
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    // System.out.println(request.getParameter("Company_name"));
    // request.setCharacterEncoding("UTF-8");
    // request.setCharacterEncoding("gb2312");
    boolean _verbose = false, _logErrors = true;
    _logErrors = PropertyManager.getBoolean(LOG_MSG_PROPERTY, _verbose);
    LogService.setQuiet(LogService.TRACE, !_logErrors);
    LogService.setQuiet(LogService.WARN, !_logErrors);
    LogService.setQuiet(LogService.MSG, !_logErrors);
    LogService.setQuiet(LogService.ERR, !_logErrors);
    chain.doFilter(request, response);
  }

  public void init(FilterConfig filterConfig) throws ServletException {
  }
}
