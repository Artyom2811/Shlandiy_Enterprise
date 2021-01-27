package newsSites;

import util.JsoupService;
import models.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BitmaxService {
    JsoupService jsoupService = new JsoupService();

    public List<News> getNews() {
        List<Element> listOfNewsLinks = new ArrayList();
        try {
            Document parsedPage = jsoupService.getDocument("https://bitmaxhelp.zendesk.com/hc/en-us/sections/360003095033-New-Listing");
            listOfNewsLinks = parsedPage.select("a.article-list-link");
        } catch (Exception ex) {
            System.out.println("Ошибка невозможно получить Новости из Bitmax:" + ex.getMessage());
        }

        List<News> listOfNews = new ArrayList();

        if (listOfNewsLinks != null) {

            for (Element e : listOfNewsLinks) {
                String topicOfNews = e.text();
                if (topicOfNews.contains("Listing")) {
                    try {
                        String ticker = topicOfNews
                                .replace("（", "(")
                                .replace("）", ")")

                                .split("\\(")[1].split("\\)")[0];
                        String linkOfNews = "https://bitmaxhelp.zendesk.com/" + e.attr("href");

                        listOfNews.add(new News("www.bitmax.com", topicOfNews, ticker, linkOfNews, LocalDateTime.now()));
                    } catch (Exception ex) {
                        System.out.println("Ошибка невозможно получить данные Новости из " + topicOfNews + " " + ex);
                    }
                }
            }
        }

        //Информационный блок
        {
            System.out.println("Обнаруженно " + listOfNews.size() + " новости(ей) на Bitmax.");
            for (News n : listOfNews) {
                System.out.println(n.getTicker());
            }
        }

        return listOfNews;
    }
}