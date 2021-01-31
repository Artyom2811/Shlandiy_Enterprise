package newsSites;

import models.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.JsoupService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OkexService {
    JsoupService jsoupService = new JsoupService();

    public List<News> getNews() {
        List<Element> listOfNewsLinks = new ArrayList();
        try {
            Document parsedPage = jsoupService.getDocument("https://www.okex.com/support/hc/en-us/sections/360000030652-Latest-Announcements");
            listOfNewsLinks = parsedPage.select("li.article-list-item");
        } catch (Exception ex) {
            System.out.println("Ошибка невозможно получить Новости из Okex:" + ex.getMessage());
        }

        List<News> listOfNews = new ArrayList();

        if (listOfNewsLinks != null) {

            for (Element e : listOfNewsLinks) {
                String topicOfNews = e.text();
                if (topicOfNews.contains("lists")) {
                    try {
                        String ticker = getTicker(topicOfNews);

                        String linkOfNews = "https://www.okex.com/support" + e.select("a").attr("href");

                        listOfNews.add(new News("www.okex.com", topicOfNews, ticker, linkOfNews, LocalDateTime.now()));
                    } catch (Exception ex) {
                        System.out.println("Ошибка невозможно получить данные Новости из " + topicOfNews + " " + ex);
                    }
                }
            }
        }

        //Информационный блок
        {
            System.out.println("Обнаруженно " + listOfNews.size() + " новости(ей) на Okex.");
            for (News n : listOfNews) {
                System.out.println(n.getTicker());
            }
        }

        return listOfNews;
    }

    private String getTicker(String topic){
        String firstStep = topic
                .replace("(", "")
                .replace(")", "")
                .split(" token")[0];
        String[] secondStep = firstStep.split(" ");

        return secondStep[secondStep.length - 1];
    }
}