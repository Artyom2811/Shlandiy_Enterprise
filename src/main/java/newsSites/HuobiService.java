package newsSites;

import util.JsoupService;
import models.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HuobiService {
    JsoupService jsoupService = new JsoupService();

    public List<News> getNews() {
        List<Element> listOfNewsLinks = new ArrayList();
        try {
            Document parsedPage = jsoupService.getDocument("https://support.hbfile.net/hc/en-us/sections/360000039481-Important-Announcements?page=1#articles");
            listOfNewsLinks = parsedPage.select("a.article-list-link");
        } catch (Exception ex) {
            System.out.println("Ошибка невозможно получить Новости из Huobi:" + ex.getMessage());
        }

        List<News> listOfNews = new ArrayList();

        if (listOfNewsLinks != null) {

            for (Element e : listOfNewsLinks) {
                String topicOfNews = e.text();
                if (topicOfNews.contains("Will Launch")) {
                    try {
                        String ticker = topicOfNews.split("Will Launch ")[1].split(" ")[0].replace(",", "");
                        String linkOfNews = "https://support.hbfile.net/" + e.attr("href");
                        String description = getDescription(linkOfNews);


                        listOfNews.add(new News("www.huobi.com", topicOfNews, ticker, linkOfNews, description, LocalDateTime.now()));
                    } catch (Exception ex) {
                        System.out.println("Ошибка невозможно получить данные Новости из " + topicOfNews);
                    }
                }
            }
        }

        //Информационный блок
        {
            System.out.println("Обнаруженно " + listOfNews.size() + " новости(ей) на Huobi.");
            for (News n : listOfNews) {
                System.out.println(n.getTicker());
            }
        }

        return listOfNews;
    }

    private String getDescription(String path){
        Document doc = jsoupService.getDocument(path);

        String desc = doc.select("div.article-body").text().toLowerCase();
//        String desc = doc.select("div.article-body>p>span[class^=ql-author-]").text().toLowerCase();
        return desc;
    }
}