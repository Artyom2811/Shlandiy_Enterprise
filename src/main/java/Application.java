import Utils.AppProperties;
import Utils.NewsSender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.News;
import newsSites.BinanceService;
import priceSites.CoingeckoService;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Application {
    static boolean isLockApp = true;

    public static void main(String[] args) {

        int millisecondsOfPause = Integer.parseInt(AppProperties.prop.getProperty("milliseconds.pause"));
        BinanceService binanceService = new BinanceService();

//        checkLock();

        while (isLockApp) {
            List<News> newsFromBinance = binanceService.getNews();
            processingOfNews(newsFromBinance);

            try {
                Thread.sleep(millisecondsOfPause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            checkLock();
        }
    }

    private static void processingOfNews(List<News> newsFromNewsSite) {
        CoingeckoService coingeckoService = new CoingeckoService();

        List<News> newNews = removeCalculatedTickers(newsFromNewsSite);

        for (News news : newNews) {
            System.out.println("Проверяем " + news.getTicker());

            try {
                BigDecimal percentOfChanges = coingeckoService.getPercentOfChanges(news);

                System.out.println("У криптовалюты " + news.getTicker() + ". В периоде от "
                        + news.getDateTime().minusMinutes(10) + " до " + news.getDateTime() + " прирост " + percentOfChanges + "%");

                List<String> listMarketInfo = coingeckoService.getMarketInfoByTicker(news.getTicker().toLowerCase());

                NewsSender.sendNewsNotification(news, percentOfChanges, listMarketInfo);

            } catch (Exception e) {
                System.out.println("Ошибка во время обработки " + news.getLinkOfNews() + " - " + e.getMessage());
                NewsSender.sendErrorNotification("Ошибка во время обработки", news.getLinkOfNews() + " - " + e.getMessage());
            }

            saveCalculatedNewsInFile(news.getTicker());
        }

        System.out.println("Обработка завершена " + LocalDateTime.now());
    }

    private static List<String> getCalculatedNewsFromFile() {
        ObjectMapper om = new ObjectMapper();

        List<String> listOfCalculatedNews = null;
        try {
            listOfCalculatedNews = om.readValue(new File("save.json"), new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            listOfCalculatedNews = new ArrayList();
        }
        return listOfCalculatedNews;
    }

    private static void saveCalculatedNewsInFile(String newTicker) {
        ObjectMapper om = new ObjectMapper();

        List<String> listOfCalculatedNews = getCalculatedNewsFromFile();
        listOfCalculatedNews.add(newTicker);

        try {
            om.writeValue(new File("save.json"), listOfCalculatedNews);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<News> removeCalculatedTickers(List<News> listOfNews) {
        List<String> listOfCalculatedNews = getCalculatedNewsFromFile();

        List<News> newNews = new ArrayList();

        for (News n : listOfNews) {
            if (!listOfCalculatedNews.contains(n.getTicker())) {
                newNews.add(n);
            }
        }
        return newNews;
    }

    private static void checkLock() {
        int dateNow = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        if (dateNow > 20210100) {
            isLockApp = false;
        }
    }
}