package server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import servlet.HelloServlet;
import servlet.SqlServlet;

import javax.servlet.ServletException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class UndertowServer2 {
  public static final String WEBAPP_RESOURCES_LOCATION = "webapp";

  /**
   * //curl localhost:8080/myapp
   * @param args
   * @throws ServletException
   */
  public static void main(final String[] args) throws ServletException, UnknownHostException {
    DeploymentInfo servletBuilder = Servlets.deployment()
        .setClassLoader(UndertowServer2.class.getClassLoader())
        .setContextPath("/myapp")
        .setDeploymentName("test.war")
        .addServlets(
            Servlets.servlet("SqlServlet", SqlServlet.class)
                .addInitParam("message", "Hello World")
                .addMapping("/*"),
            Servlets.servlet("HelloServlet", HelloServlet.class)
                .addInitParam("message", "MyServlet")
                .addMapping("/a"));

    DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
    manager.deploy();
    PathHandler path = Handlers.path(Handlers.redirect("/myapp"))
        .addPrefixPath("/myapp", manager.start());
    InetAddress localHost = InetAddress.getLocalHost();
    String hostName = localHost.getHostName();
    byte[] address = localHost.getAddress();
    String hostAddress = localHost.getHostAddress();
    Undertow server = Undertow.builder()
        //.addHttpListener(8080, "localhost")
        .addHttpListener(8080, hostName)
        .setHandler(path)
        .build();
    server.start();
  }
}
