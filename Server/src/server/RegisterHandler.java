package server;

import java.io.*;
import java.net.*;
import com.sun.net.httpserver.*;
import dao.DataAccessException;
import request.RegisterRequest;
import result.RegisterResult;
import service.RegisterService;


class RegisterHandler implements HttpHandler {

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("Handling Register Request");
		
		boolean success = false;
		
		try {
			if (exchange.getRequestMethod().toLowerCase().equals("post")) {

				//Read Request Body, deserialize into RegisterRequest and process
				InputStream reqBody = exchange.getRequestBody();
				String reqData = readString(reqBody);

				RegisterRequest regReq = JSONHandler.deserializeRegister(reqData);

				RegisterResult rslt = RegisterService.register(regReq);

				success = rslt.isSuccess();

				//Check value of RegisterResult.success - Return appropriate HTTP Message.
				if (success) {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
				}
				else {
					exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_REQUEST, 0);
				}

				//Serialize RegisterResult and write to Response Body, then close.
				OutputStream respBody = exchange.getResponseBody();
				String respData = JSONHandler.serializeRegResult(rslt);
				System.out.println(respData);

				writeString(respData, respBody);

				exchange.getResponseBody().close();


			}
		}
		catch (IOException | DataAccessException e) {
			//Send 500 error message when connection to server fails.
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
