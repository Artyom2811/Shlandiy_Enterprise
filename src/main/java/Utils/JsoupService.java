package Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupService {
    public JsoupService() {
    }

    public Document getDocument(String path) {
        Document doc = null;

        try {
            doc = Jsoup.connect(path).userAgent("Mozilla").get();
        } catch (IOException e) {
            System.out.println("Error getDocument:" + e);
            e.printStackTrace();
        }

        return doc;
    }
}