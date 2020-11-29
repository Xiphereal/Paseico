package app.paseico.service;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

public class HttpsService {

    public static String executeGet(final String httpsUrl) {
        String response = "";

        try {
            URL url = new URL(httpsUrl);

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            response = (String) httpsURLConnection.getContent();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
