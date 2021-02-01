package newsSites;

import models.News;
import util.JsoupService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BinanceService {
    JsoupService jsoupService = new JsoupService();

    public List<News> getNews() {
        List<Element> listOfNewsLinks = new ArrayList();
        try {
            Document parsedPage = jsoupService.getDocument("https://www.binance.com/en/support/announcement/c-48?navid=48");
            listOfNewsLinks = parsedPage.select("a.css-1neg3js");
        } catch (Exception ex) {
            System.out.println("Ошибка невозможно получить Новости из Binance" + ex.getMessage());
        }

        List<News> listOfNews = new ArrayList();

        if (listOfNewsLinks != null) {

            for (Element e : listOfNewsLinks) {
                String topicOfNews = e.text();
                if (topicOfNews.contains("List")) {
                    try {
                        String ticker = topicOfNews.split("\\(")[1].split("\\)")[0];
                        String linkOfNews = "https://www.binance.com" + e.attr("href");

                        LocalDateTime dateTimeOfTicker = getDateTimeOfNews(linkOfNews);
                        listOfNews.add(new News("www.binance.com", topicOfNews, ticker, linkOfNews, dateTimeOfTicker));
                    } catch (Exception ex) {
                        System.out.println("Ошибка невозможно получить данные Новости из " + topicOfNews);
                    }
                }
            }
            //Информационный блок
            {
                System.out.println("Обнаруженно " + listOfNews.size() + " новости(ей) на Binance.");
                for (News n : listOfNews) {
                    System.out.println(n.getTicker());
                }
            }
        }

        return listOfNews;
    }

    private LocalDateTime getDateTimeOfNews(String path) {
        Document doc = jsoupService.getDocument(path);

        String dateTimeOfTicker = doc.select("div.css-f1q2g4").text();

        return LocalDateTime.parse(dateTimeOfTicker, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).plusHours(3);
    }
}