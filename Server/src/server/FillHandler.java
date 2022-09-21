package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.FillRequest;
import result.FillResult;
import service.FillService;

import java.io.*;
import java.net.HttpURLConnection;

public class FillHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling Fill Request");

        boolean success = false;

        try {

            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                //Get username for FillRequest and number of generations from Request URI
                String urlPath = exchange.getRequestURI().toString();

                //Split and initialize variables for username and generation Number from request
                String[] split = urlPath.split("/");
                String username;
                int generationNumber = 4;

                //Get username, try to get integer from split URI. If integer cannot be found, default to 4 generations.
                if (split.length == 4) {
                    username = split[2];
                    try {
                        generationNumber = Integer.parseInt(split[3]);
                    }
                    catch (NumberFormatException e) {
                        System.out.println("Generation Parameter not an integer, defaulting to 4 generations");
                        generationNumber = 4;
                    }
                }
                else {
                    username = split[2];
                }

                //Process FillRequest with parameters received from URI
                FillRequest fillReq = new FillRequest(username, generationNumber);

                FillResult rslt = FillService.fill(fillReq);

                success = rslt.isSuccess();

                //Check value of FillResult.success - Return appropriate HTTP Message.
                if (success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

                //Serialize FillResult, write to Response Body and close.
                String respData = JSONHandler.serializeFillResult(rslt);
                System.out.println(respData);

                OutputStream respBody = exchange.getResponseBody();
                writeString(respData, respBody);
                respBody.close();
            }

        }
        catch (IOException | DataAccessException e) {
            //Return 500 error message when connection to the server fails.
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
