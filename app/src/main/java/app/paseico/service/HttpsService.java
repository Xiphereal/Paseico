package app.paseico.service;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class HttpsService {

    public static String executeGet(final String httpsUrl) {
        String response = "";

        try {
            URL url = new URL(httpsUrl);

            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

            InputStream inputStream = (InputStream) httpsURLConnection.getContent();

            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            response = s.hasNext() ? s.next() : "";

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
