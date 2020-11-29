package app.paseico.service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpService {

    public static String executeGet(final String httpUrl) {
        String response = "";

        try {
            URL url = new URL(httpUrl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            response = (String) httpURLConnection.getContent();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
