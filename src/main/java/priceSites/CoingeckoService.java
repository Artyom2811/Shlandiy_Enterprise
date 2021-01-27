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

public class CoingeckoService {
    JsoupService jsoupService = new JsoupService();
    RestService restService = new RestService();
    JacksonService jacksonService = new JacksonService();

    public BigDecimal getPercentOfChanges(News news) throws Exception {
        String codeOfCurrency = getCoingeckoCodeOfCurrency(news.getTicker().toLowerCase());
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
            } else result =  new BigDecimal(0);
        } else result = new BigDecimal(0);

        return result;
    }

    public List<MarketInfoModel> getMarketInfoByTicker(String ticker) throws Exception {
        String targetCodeOfCurrency = getTargetCodeOfCurrency(ticker);

        String answer = restService.getBodyFromGetRequest("https://api.coingecko.com/api/v3/coins/" + targetCodeOfCurrency + "?localization=false&tickers=true&market_data=false&community_data=false&developer_data=false&sparkline=false");
        List<MarketInfoModel> listOfMarketInfo = new ArrayList<>();

        ObjectNode node = null;
        try {
            node = new ObjectMapper().readValue(answer, ObjectNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String a = "";

        if (node.has("tickers")) {
            a = node.get("tickers").toString();
            JacksonService jacksonService = new JacksonService();
            listOfMarketInfo = jacksonService.getMarketInfoFromJsonForCoingecko(a);
        }
        return listOfMarketInfo;
    }

    private List<CoingeckoTickerModel> getListAllCodeOfCurrency() {
        RestService restService = new RestService();
        String g = restService.getBodyFromGetRequest("https://api.coingecko.com/api/v3/coins/list");
        return jacksonService.getTickersFromJsonForCoingecko(g);
    }

    private String getTargetCodeOfCurrency(String ticker) throws Exception {
        List<CoingeckoTickerModel> listOfTickers = getListAllCodeOfCurrency();
        String targetCodeOfCurrency = null;
        try {
            targetCodeOfCurrency = listOfTickers.stream().filter(a -> a.getSymbol().equals(ticker)).findFirst().get().getId();
        } catch (Exception e){
            throw new Exception("Coingecko has not ticker:" + ticker);
        }
        return targetCodeOfCurrency;
    }

    private String getCoingeckoCodeOfCurrency(String ticker) throws Exception {
        String targetCodeOfCurrency = getTargetCodeOfCurrency(ticker);

        Document doc = jsoupService.getDocument("https://www.coingecko.com/en/coins/" + targetCodeOfCurrency);
        String coingeckoCodeOfCurrency = doc.selectFirst("#coin_id").attr("value");
        return coingeckoCodeOfCurrency;
    }
}