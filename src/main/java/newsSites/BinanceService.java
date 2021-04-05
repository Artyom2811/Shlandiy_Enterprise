package newsSites;

import models.News;
import org.apache.log4j.Logger;
import util.JsoupService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BinanceService {
    static Logger log = Logger.getLogger(BinanceService.class.getName());

    JsoupService jsoupService = new JsoupService();

    public List<News> getNews() {
        List<Element> listOfNewsLinks = new ArrayList();
        try {
            Document parsedPage = jsoupService.getDocument("https://www.binance.com/en/support/announcement/c-48?navid=48");
            listOfNewsLinks = parsedPage.select("a.css-1ej4hfo");
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
                log.info("Обнаруженно " + listOfNews.size() + " новости(ей) на Binance. (" + listOfNews.stream().map(e -> e.getTicker()).collect(Collectors.joining(", ")) + ")");
            }
        }

        return listOfNews;
    }

    private LocalDateTime getDateTimeOfNews(String path) {
        Document doc = jsoupService.getDocument(path);

        String dateTimeOfTicker = doc.select("div.css-17s7mnd").text();

        return LocalDateTime.parse(dateTimeOfTicker, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).plusHours(3);
    }
}