package newsSites;

import models.KucoinModel;
import util.JacksonService;
import util.RestService;
import models.News;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KucoinService {
    RestService restService = new RestService();
    JacksonService jacksonService = new JacksonService();

    public List<News> getNews() {
        List<KucoinModel> listOfKucoinNews = null;
        try {
            String parsedBody = restService.getBodyFromGetRequest("https://www.kucoin.com/_api/cms/articles?page=1&pageSize=10&category=listing&lang=en");

            String listOfNewsText = getCutBodyString(parsedBody);

            listOfKucoinNews = jacksonService.getKucoinNews(listOfNewsText);

        } catch (Exception ex) {
            System.out.println("Ошибка невозможно получить Новости из Kucoin:" + ex.getMessage());
        }

        List<News> listOfNews = new ArrayList();

        for (KucoinModel e : listOfKucoinNews) {
            if (e.getTitle().contains("Listed")) {
                try {
                    String ticker = e.getTitle().split(" \\(")[1].split("\\) ")[0];
                    String linkOfNews = "https://www.kucoin.com/news" + e.getPath();

                    listOfNews.add(new News("www.kucoin.com", e.getTitle(), ticker, linkOfNews, LocalDateTime.now()));
                } catch (Exception ex) {
                    System.out.println("Ошибка невозможно получить данные Новости из " + e.getTitle());
                }
            }
        }

        //Информационный блок
        {
            System.out.println("Обнаруженно " + listOfNews.size() + " новости(ей) на Kucoin.");
            for (News n : listOfNews) {
                System.out.println(n.getTicker());
            }
        }

        return listOfNews;
    }

    private String getCutBodyString(String fullBody){
        return fullBody.substring(0, fullBody.length() - 1).split("\"items\"\\:")[1];

    }
}