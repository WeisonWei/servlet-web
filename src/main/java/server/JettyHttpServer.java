package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class JettyHttpServer {
  public static final String WEBAPP_RESOURCES_LOCATION = "webapp";

  public static void main(String[] args) throws Exception {
    Server server = new Server(8080);
    //ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
    WebAppContext context = new WebAppContext();
    // Or ServletContextHandler.NO_SESSIONS
    ServletHolder holder = new ServletHolder(new DefaultServlet());
    Map<String, String> params = new HashMap<String, String>();
    params.put("acceptRanges", "true");
    params.put("dirAllowed", "false");
    params.put("gzip", "true");
    params.put("useFileMappedBuffer", "false");
    holder.setInitParameters(params);

    context.setContextPath("/");
    context.setDescriptor(WEBAPP_RESOURCES_LOCATION + "/WEB-INF/web.xml");
    URL webAppDir = Thread.currentThread().getContextClassLoader().getResource(WEBAPP_RESOURCES_LOCATION);
    context.setResourceBase(webAppDir.toURI().toString());
    context.setParentLoaderPriority(true);
    context.setWelcomeFiles(new String[]{"index.html"});
    context.addServlet(holder, "/");

    // http://localhost:8080/hello
    //context.addServlet(new ServletHolder(new SqlServlet()), "/hello");
    context.addServlet(SqlServlet.class, "/hello");
    server.setHandler(context);

    server.start();
    server.join();
  }

  protected static String getWebAppsPath(String appName) throws FileNotFoundException {
    URL resourceUrl = null;
    File webResourceDevLocation = new File("src/main/webapp", appName);
    if (webResourceDevLocation.exists()) {
   /*   LOG.info("Web server is in development mode. Resources "
          + "will be read from the source tree.");*/
      try {
        resourceUrl = webResourceDevLocation.getParentFile().toURI().toURL();
      } catch (MalformedURLException e) {
        throw new FileNotFoundException("Mailformed URL while finding the "
            + "web resource dir:" + e.getMessage());
      }
    } else {
      resourceUrl = JettyHttpServer.class.getClassLoader().getResource("webapp/" + appName);

      if (resourceUrl == null) {
        throw new FileNotFoundException("webapp/" + appName +
            " not found in CLASSPATH");
      }
    }
    String urlString = resourceUrl.toString();
    return urlString.substring(0, urlString.lastIndexOf('/'));
  }
}
