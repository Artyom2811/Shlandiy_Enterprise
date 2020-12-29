package Utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class TelegramSender {
    String URL_PATTERN = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
    String API_TOKEN = AppProperties.prop.getProperty("telegram.api.token");
    String CHAT_ID = AppProperties.prop.getProperty("telegram.chat.id");

    public void send(String messageText) throws IOException {
        URL_PATTERN = String.format(URL_PATTERN, API_TOKEN, CHAT_ID, messageText);
        URL url = new URL(URL_PATTERN);
        URLConnection conn = url.openConnection();
        StringBuilder sb = new StringBuilder();
        InputStream is = new BufferedInputStream(conn.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String inputLine = "";
        while ((inputLine = br.readLine()) != null) {
            sb.append(inputLine);
        }
        String response = sb.toString();
    }
}
