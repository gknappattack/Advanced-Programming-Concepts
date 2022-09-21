package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.EventIDRequest;
import result.EventIDResult;
import service.EventIDService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class EventIDHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling EventID Request");

        boolean success = false;

        try {

            if (exchange.getRequestMethod().toLowerCase().equals("get")) {


                Headers reqHeaders = exchange.getRequestHeaders();

                //Check if Authorization Token Value was sent with Request
                if (reqHeaders.containsKey("Authorization")) {

                    //Get Authorization Token Value
                    String authToken = reqHeaders.getFirst("Authorization");

                    //Split and save PersonID to use in search from Request URI
                    String urlPath = exchange.getRequestURI().toString();
                    String[] split = urlPath.split("/");
                    String eventID = split[2];

                    //Use Auth Token to attempt to process EventIDRequest
                    EventIDRequest eventReq = new EventIDRequest(eventID, authToken);

                    EventIDResult rslt = EventIDService.eventID(eventReq);

                    success = rslt.isSuccess();

                    //Check value of EventIDResult.success - Return appropriate HTTP Message.
                    if (success) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }

                    //Serialize EventIDResult, write to Response Body and close.
                    String respData = JSONHandler.serializeEventIDResult(rslt);
                    System.out.println(respData);

                    OutputStream respBody = exchange.getResponseBody();
                    writeString(respData, respBody);

                    respBody.close();

                }
            }
        }
        catch (IOException | DataAccessException e) {
            //Send 500 error message when connection to server fails.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
