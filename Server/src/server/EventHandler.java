package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.EventRequest;
import result.EventResult;
import service.EventService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class EventHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling Event Request");

        boolean success = false;
        try {

            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                Headers reqHeaders = exchange.getRequestHeaders();

                //Check if Authorization Token Value was sent with Request
                if (reqHeaders.containsKey("Authorization")) {

                    //Get Authorization Token Value
                    String authToken = reqHeaders.getFirst("Authorization");

                    //Use Auth Token to attempt to process Person Request
                    EventRequest eventReq = new EventRequest(authToken);

                    EventResult rslt = EventService.event(eventReq);

                    success = rslt.isSuccess();

                    //Check value of EventResult.success - Return appropriate HTTP Message.
                    if (success) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }

                    //Serialize EventResult, write to Response Body and close.
                    String respData = JSONHandler.serializeEventResult(rslt);
                    System.out.println(respData);

                    OutputStream respBody = exchange.getResponseBody();
                    writeString(respData, respBody);

                    respBody.close();
                }
            }
        }
        catch (IOException | DataAccessException e) {
            //Send 500 error message when connection to the server fails.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }

    /*
        The writeString method shows how to write a String to an OutputStream.
    */
    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
