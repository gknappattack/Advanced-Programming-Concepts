package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.Files;

class FileHandler implements HttpHandler {


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling File Request");

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("get")) {

                //Get path for file name from Request URI
                String urlPath = exchange.getRequestURI().toString();

                //Check for empty requests, default to homepage at index.html
                if ((urlPath == null) || (urlPath.equals("/"))) {
                    urlPath = "/index.html";
                }

                //Append web to urlpath and search for file
                String filePath = "web" + urlPath;
                File requestFile = new File(filePath);

                //If valid file is requested, output and return the file, else redirect to 404 Not Found page
                if (requestFile.exists()) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);

                    OutputStream respBody=exchange.getResponseBody();
                    Files.copy(requestFile.toPath(), respBody);

                    respBody.close();
                }
                else {
                    String notFoundPath = "web/HTML/404.html";
                    File notFound = new File(notFoundPath);

                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, 0);

                    OutputStream respBody=exchange.getResponseBody();
                    Files.copy(notFound.toPath(), respBody);

                    exchange.getResponseBody().close();
                }
            }
        }
        catch (IOException e) {
            //Return 500 error message when connection to server fails.
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }
}
