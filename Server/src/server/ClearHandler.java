package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.ClearRequest;
import request.RegisterRequest;
import result.ClearResult;
import result.RegisterResult;
import service.ClearService;
import service.RegisterService;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;

public class ClearHandler implements HttpHandler {

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    System.out.println("Handling clear request.");

    boolean success = false;

    try {

      if (exchange.getRequestMethod().toLowerCase().equals("post")) {

        //Execute Clear, create ClearResult
        ClearResult rslt = ClearService.clear();
        success = rslt.isSuccess();

        //Check value of ClearResult.success - Return appropriate HTTP Message.
        if (success) {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        }
        else {
          exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
        }

        //Serialize ClearResult, write to Response Body and close.
        OutputStream respBody = exchange.getResponseBody();
        String respData = JSONHandler.serializeClearResult(rslt);
        writeString(respData, respBody);

        System.out.println(respData);

        exchange.getResponseBody().close();
      }
    }
    catch (IOException | DataAccessException e) {
      //Return 500 error message if connection to the server fails
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
