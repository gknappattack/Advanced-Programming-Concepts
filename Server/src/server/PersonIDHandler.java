package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.PersonIDRequest;
import result.PersonIDResult;
import service.PersonIDService;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class PersonIDHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling PersonIDRequest");

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
                    String personID = split[2];

                    //Use Auth Token to attempt to process PersonIDRequest
                    PersonIDRequest personReq = new PersonIDRequest(personID, authToken);

                    PersonIDResult rslt = PersonIDService.personID(personReq);

                    success = rslt.isSuccess();

                    //Check value of PersonIDResult.success - Return appropriate HTTP Message.
                    if (success) {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                    }

                    //Serialize PersonIDResult, write to Response Body and close.
                    String respData = JSONHandler.serializePersonIDResult(rslt);
                    System.out.println(respData);

                    OutputStream respBody = exchange.getResponseBody();
                    writeString(respData, respBody);

                    respBody.close();


                }
            }
        }
        catch (IOException | DataAccessException e) {
            //Return 500 error message when connection to server fails.
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