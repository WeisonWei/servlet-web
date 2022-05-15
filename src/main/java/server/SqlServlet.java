package server;


import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class SqlServlet extends HttpServlet {

  /**
   * for java.io.Serializable
   */
  private static final long serialVersionUID = 1L;
  public SqlServlet() {
  }

  /**
   * Handle query region request
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Map<String, String[]> params = request.getParameterMap();
    PrintWriter out = response.getWriter();
    ServletContext context = getServletContext();

    try {
      out.write("Hello World!");
    } catch (Exception e) {
      response.sendError(400, e.getMessage());
    }
  }
}
