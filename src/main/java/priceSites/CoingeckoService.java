package priceSites;

import util.JacksonService;
import util.JsoupService;
import util.RestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.News;
import org.jsoup.nodes.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CoingeckoService {
    JsoupService jsoupService = new JsoupService();
    RestService restService = new RestService();
    JacksonService jacksonService = new JacksonService();

    public BigDecimal getPercentOfChanges(News news) throws Exception {
        String codeOfCurrency = getCoingeckoCodeOfCurrency(news);
        LocalDateTime dateOfNews = news.getDateTime();
        Long dateTimeBefore10Min = dateOfNews.minusMinutes(10).toEpochSecond(ZoneOffset.of("+3"));

        String linkOfRequest = "https://www.coingecko.com/price_charts/" +
                codeOfCurrency +
                "/usd/custom.json?from=" +
                dateTimeBefore10Min +
                "&to=" +
                dateOfNews.toEpochSecond(ZoneOffset.of("+3"));

        String jsonAnswer = restService.getBodyFromGetRequest(linkOfRequest);

        CoingeckoPriceModel l = jacksonService.getMapFromJsonForCoingecko(jsonAnswer);

        BigDecimal result = null;

        if (l.getStats().size() != 0) {
            BigDecimal firstPrice = new BigDecimal(l.getStats().get(0).get(1)).setScale(6, RoundingMode.UP);
            BigDecimal lastPrice = new BigDecimal(l.getStats().get(l.getStats().size() - 1).get(1)).setScale(6, RoundingMode.UP);

            if (firstPrice.doubleValue() < lastPrice.doubleValue()) {
                BigDecimal a = firstPrice.divide(lastPrice, RoundingMode.DOWN);
                BigDecimal b = a.multiply(new BigDecimal(100));
                result = new BigDecimal(100).subtract(b);
            } else if (firstPrice.doubleValue() > lastPrice.doubleValue()) {
                BigDecimal a = firstPrice.divide(lastPrice, RoundingMode.DOWN);
                BigDecimal b = a.multiply(new BigDecimal(100));
                result = b.subtract(new BigDecimal(100));
            } else result = new BigDecimal(0);
        } else result = new BigDecimal(0);

        return result;
    }

    public List<MarketInfoModel> getMarketInfoByTicker(News news) throws Exception {
        String targetCodeOfCurrency = getTargetCodeOfCurrency(news);

        String answer = restService.getBodyFromGetRequest("https://api.coingecko.com/api/v3/coins/" + targetCodeOfCurrency + "?localization=false&tickers=true&market_data=false&community_data=false&developer_data=false&sparkline=false");
        List<MarketInfoModel> listOfMarketInfo = new ArrayList<>();

        ObjectNode node = null;

        node = new ObjectMapper().readValue(answer, ObjectNode.class);

        String ticker = "";

        if (node.has("tickers")) {
            ticker = node.get("tickers").toString();
            JacksonService jacksonService = new JacksonService();
            listOfMarketInfo = jacksonService.getMarketInfoFromJsonForCoingecko(ticker);
        }
        return listOfMarketInfo;
    }

    private List<CoingeckoTickerModel> getListAllCodeOfCurrency() {
        RestService restService = new RestService();
        String g = restService.getBodyFromGetRequest("https://api.coingecko.com/api/v3/coins/list");
        return jacksonService.getTickersFromJsonForCoingecko(g);
    }

    private String getTargetCodeOfCurrency(News news) throws Exception {
        List<CoingeckoTickerModel> listOfTickers = getListAllCodeOfCurrency();
        String targetCodeOfCurrency = null;

        List<CoingeckoTickerModel> foundCurrency = listOfTickers.stream().filter(a -> a.getSymbol().equals(news.getTicker().toLowerCase())).collect(Collectors.toList());

        if (foundCurrency.isEmpty()) throw new Exception("На Coingecko нет валюты " + news.getTicker());
        if (foundCurrency.size() > 1)
            throw new Exception("На Coingecko есть несколько похожих валюты " + foundCurrency);

        CoingeckoTickerModel relevantCurrency = foundCurrency.get(0);

        if (news.getDescription() != null) {
            if (news.getDescription().contains(relevantCurrency.getName().toLowerCase())) {
                targetCodeOfCurrency = relevantCurrency.getId();
            } else throw new Exception("Описание валюты не содержит Ticker");
        } else targetCodeOfCurrency = relevantCurrency.getId();

        return targetCodeOfCurrency;
    }

    private String getCoingeckoCodeOfCurrency(News news) throws Exception {
        String targetCodeOfCurrency = getTargetCodeOfCurrency(news);

        Document doc = jsoupService.getDocument("https://www.coingecko.com/en/coins/" + targetCodeOfCurrency);
        String coingeckoCodeOfCurrency = doc.selectFirst("#coin_id").attr("value");
        return coingeckoCodeOfCurrency;
    }
}