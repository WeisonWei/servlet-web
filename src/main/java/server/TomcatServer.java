package server;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import servlet.TomServlet;

import javax.servlet.ServletException;
import java.io.File;

public class TomcatServer {
  /**
   * https://www.codejava.net/servers/tomcat/how-to-embed-tomcat-server-into-java-web-applications
   * @param args
   * @throws ServletException
   */
  public static void main(final String[] args) throws ServletException {
    //String webappDirLocation = "src/main/webapp/";
    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir("temp");
    tomcat.setPort(8080);
    String docBase = new File(".").getAbsolutePath();

    tomcat.setHostname("localhost");
    tomcat.getHost().setAppBase(".");
    tomcat.getConnector();
    String contextPath = "/";
    Context context = tomcat.addContext(contextPath, docBase);
    TomServlet tomServlet = new TomServlet();
    String servletName = "tomServlet";
    String urlPattern = "/tom";

    tomcat.addServlet(contextPath, servletName, tomServlet);
    context.addServletMappingDecoded(urlPattern, servletName);
    try {
      tomcat.start();
    } catch (LifecycleException exception) {
      System.out.println(exception.getMessage());
      System.out.println("Exit...");
      System.exit(1);
    }

    System.out.println("Application started with URL {}.localhost:8080/");
    System.out.println("Hit Ctrl + D or C to stop it...");
    tomcat.getServer().await();

  }
}
