package server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class UndertowHttpServer {
  public static final String WEBAPP_RESOURCES_LOCATION = "webapp";

  public static void main(final String[] args) {
    HttpHandler hw = new HttpHandler() {
      @Override
      public void handleRequest(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send("Hello World");
      }
    };
    Undertow server = Undertow.builder()
        .addHttpListener(8080, "localhost")
        .setHandler(hw)
        .build();
    server.start();
  }
}
