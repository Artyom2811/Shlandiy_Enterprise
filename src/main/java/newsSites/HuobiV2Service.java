package newsSites;

import models.News;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import util.JsoupService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HuobiV2Service {
    static Logger log = Logger.getLogger(HuobiV2Service.class.getName());

    JsoupService jsoupService = new JsoupService();

    public List<News> getNews() {
        List<Element> listOfNewsLinks = new ArrayList();
        try {
            Document parsedPage = jsoupService.getDocument("https://www.huobi.li/support/en-us/list/360000039942");
            listOfNewsLinks = parsedPage.select("a.list-field1");
        } catch (Exception ex) {
            System.out.println("Ошибка невозможно получить Новости из Huobi:" + ex.getMessage());
        }

        List<News> listOfNews = new ArrayList();

        if (listOfNewsLinks != null) {

            for (Element e : listOfNewsLinks) {
                String topicOfNews = e.text().toLowerCase();
                if (topicOfNews.contains("will launch ") || topicOfNews.contains("will list ")) {
                    try {
                        String findPhrase = topicOfNews.toLowerCase().contains("will launch ") ? "will launch " : "will list ";

                        String ticker = topicOfNews.split(findPhrase)[1]
                                .split(" ")[0]
                                .replace(",", "")
                                .replace("-", "");
                        String linkOfNews = "https://www.huobi.li" + e.attr("href");
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

        String desc = doc.select("div.detail-content-pc-box").text().toLowerCase();
//        String desc = doc.select("div.article-body>p>span[class^=ql-author-]").text().toLowerCase();
        return desc;
    }
}