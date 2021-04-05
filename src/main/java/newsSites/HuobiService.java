package newsSites;

import org.apache.log4j.Logger;
import util.JsoupService;
import models.News;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HuobiService {
    static Logger log = Logger.getLogger(HuobiService.class.getName());

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
                        String findPhrase = topicOfNews.contains("Will Launch ") ? "Will Launch " : "Will list ";

                        String ticker = topicOfNews.split(findPhrase)[1].split(" ")[0].replace(",", "");
                        String linkOfNews = "https://support.hbfile.net/" + e.attr("href");
                        String description = getDescription(linkOfNews);


                        listOfNews.add(new News("www.huobi.com", topicOfNews, ticker, linkOfNews, description, LocalDateTime.now()));
                    } catch (Exception ex) {
                        System.out.println("Ошибка невозможно получить данные Новости из " + topicOfNews);
                    }
                }
            }
            //Информационный блок
            {
                log.info("Обнаруженно " + listOfNews.size() + " новости(ей) на Huobi. (" + listOfNews.stream().map(e -> e.getTicker()).collect(Collectors.joining(", ")) + ")");
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