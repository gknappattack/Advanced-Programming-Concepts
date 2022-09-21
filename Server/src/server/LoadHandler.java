package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.DataAccessException;
import request.LoadRequest;
import result.LoadResult;
import service.LoadService;

import java.io.*;
import java.net.HttpURLConnection;

public class LoadHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Handling Load Request");

        boolean success = false;

        try {
            if (exchange.getRequestMethod().toLowerCase().equals("post")) {

                //Read Request Body, deserialize into Load Request and process
                InputStream reqBody = exchange.getRequestBody();
                String reqData = readString(reqBody);

                LoadRequest loadReq = JSONHandler.deserializeLoad(reqData);

                LoadResult rslt = LoadService.load(loadReq);
                success = rslt.isSuccess();

                //Check value of LoadResult.success - Return appropriate HTTP Message.
                if (success) {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                }
                else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
                }

                //Write Output from LoadResult to ResponseBody and close
                OutputStream respBody = exchange.getResponseBody();
                String respData = JSONHandler.serializeLoadResult(rslt);
                System.out.println(respData);

                writeString(respData, respBody);

                exchange.getResponseBody().close();
            }

        }
        catch (IOException | DataAccessException e) {
            //Return a 500 Server error when connection fails
            exchange.sendResponseHeaders(HttpURLConnection.HTTP_SERVER_ERROR, 0);
            exchange.getResponseBody().close();

            e.printStackTrace();
        }
    }

    private String readString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader sr = new InputStreamReader(is);
        char[] buf = new char[1024];
        int len;
        while ((len = sr.read(buf)) > 0) {
            sb.append(buf, 0, len);
        }
        return sb.toString();
    }

    private void writeString(String str, OutputStream os) throws IOException {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        sw.write(str);
        sw.flush();
    }
}
