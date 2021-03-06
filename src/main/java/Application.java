import newsSites.*;
import org.apache.log4j.Logger;
import util.AppProperties;
import util.NewsSender;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.News;
import priceSites.CoingeckoService;
import priceSites.MarketInfoModel;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Application {
    static Logger log = Logger.getLogger(Application.class.getName());

    static boolean isLockApp = true;

    public static void main(String[] args) {

        int millisecondsOfPause = Integer.parseInt(AppProperties.prop.getProperty("milliseconds.pause"));
        BinanceService binanceService = new BinanceService();
//        HuobiService huobiService = new HuobiService();
        HuobiV2Service huobiV2Service = new HuobiV2Service();
        BitmaxService bitmaxService = new BitmaxService();
        KucoinService kucoinService = new KucoinService();
        OkexService okexService = new OkexService();

//        checkLock();

        while (isLockApp) {
            List<News> newsFromBinance = binanceService.getNews();
            processingOfNews(newsFromBinance, "Binance");

//            List<News> newsFromHuobi = huobiService.getNews();
//            processingOfNews(newsFromHuobi, "Huobi");

            List<News> newsFromHuobi = huobiV2Service.getNews();
            processingOfNews(newsFromHuobi, "Huobi");

            List<News> newsFromBitmax = bitmaxService.getNews();
            processingOfNews(newsFromBitmax, "Bitmax");

            List<News> newsFromKucoin = kucoinService.getNews();
            processingOfNews(newsFromKucoin, "Kucoin");

            List<News> newsFromOkex = okexService.getNews();
            processingOfNews(newsFromOkex, "Okex");

            log.info("Завершена обработка всех новостей");

            try {
                Thread.sleep(millisecondsOfPause);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            checkLock();
        }
    }

    private static void processingOfNews(List<News> newsFromNewsSite, String nameOfNewsSource) {
        CoingeckoService coingeckoService = new CoingeckoService();
        List<News> newNews = new ArrayList<>();

        if (newsFromNewsSite.size() > 0) {
            newNews = removeCalculatedTickers(newsFromNewsSite.get(0).getSource(), newsFromNewsSite);
        }

        for (News news : newNews) {
            System.out.println("Проверяем " + news.getTicker());

            try {
                BigDecimal percentOfChanges = coingeckoService.getPercentOfChanges(news);

                System.out.println("У криптовалюты " + news.getTicker() + ". В периоде от "
                        + news.getDateTime().minusMinutes(10) + " до " + news.getDateTime() + " прирост " + percentOfChanges + "%");

                List<MarketInfoModel> listMarketInfo = coingeckoService.getMarketInfoByTicker(news);

                NewsSender.sendNewsNotification(news, percentOfChanges, listMarketInfo);

            } catch (Exception e) {
                System.out.println("Ошибка во время обработки " + news.getLinkOfNews() + " - " + e.getMessage());
                NewsSender.sendErrorNotification("Ошибка во время обработки", news.getLinkOfNews() + " - " + e.getMessage());
            }

            saveCalculatedNewsInFile(news.getSource(), news.getTicker());
        }

        log.info("Завершена обработка " + nameOfNewsSource);
    }

    private static List<String> getCalculatedNewsFromFile(String source) {
        ObjectMapper om = new ObjectMapper();

        List<String> listOfCalculatedNews;
        try {
            listOfCalculatedNews = om.readValue(new File(source + "-save.json"), new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            listOfCalculatedNews = new ArrayList();
        }
        return listOfCalculatedNews;
    }

    private static void saveCalculatedNewsInFile(String source, String newTicker) {
        ObjectMapper om = new ObjectMapper();

        List<String> listOfCalculatedNews = getCalculatedNewsFromFile(source);
        listOfCalculatedNews.add(newTicker);

        try {
            om.writeValue(new File(source + "-save.json"), listOfCalculatedNews);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<News> removeCalculatedTickers(String source, List<News> listOfNews) {
        List<String> listOfCalculatedNews = getCalculatedNewsFromFile(source);

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