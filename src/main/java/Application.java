import io.helidon.common.http.Http;
import io.helidon.common.http.Http.Status;
import io.helidon.media.jsonp.server.JsonSupport;
import io.helidon.webserver.Routing;
import io.helidon.webserver.WebServer;
import java.util.concurrent.TimeUnit;

public class Application {

  public static void main(String... arguments) throws Exception {
    Routing routing = Routing.builder()
        .register(JsonSupport.create())
        .get("/", (serverRequest, serverResponse) -> {
          serverResponse.status(Status.OK_200);
          serverResponse.send("Hello World!");
        })
        .any((serverRequest, serverResponse) -> {
          System.out.println("Received request - " + serverRequest.method() + " - "
              + serverRequest.uri());
          serverRequest.next();
        })
        .error(Exception.class, (serverRequest, serverResponse, exception) -> {
          serverResponse.status(Http.Status.BAD_REQUEST_400);
          serverResponse.send("Unable to parse request. Message: " + exception.getMessage());
        })
        .build();

    WebServer webServer = WebServer.create(routing)
        .start()
        .toCompletableFuture()
        .get(10, TimeUnit.SECONDS);

    System.out.println("Server started at: http://localhost:" + webServer.port());
  }

}