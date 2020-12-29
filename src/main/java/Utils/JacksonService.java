package Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import priceSites.CoingeckoPriceModel;
import priceSites.CoingeckoTickerModel;
import priceSites.MarketInfoModel;

import java.util.List;

public class JacksonService {
    ObjectMapper om = new ObjectMapper();
    public CoingeckoPriceModel getMapFromJsonForCoingecko(String json){
        CoingeckoPriceModel coingeckoAnswerModel = null;
        try {
            coingeckoAnswerModel = om.readValue(json, CoingeckoPriceModel.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return coingeckoAnswerModel;
    }

    public List<CoingeckoTickerModel> getTickersFromJsonForCoingecko(String json){
        List<CoingeckoTickerModel> coingeckoAnswerModel = null;
        try {
            coingeckoAnswerModel = om.readValue(json, new TypeReference<List<CoingeckoTickerModel>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return coingeckoAnswerModel;
    }

    public List<MarketInfoModel> getMarketInfoFromJsonForCoingecko(String json){
        List<MarketInfoModel> coingeckoAnswerModel = null;
        try {
            coingeckoAnswerModel = om.readValue(json, new TypeReference<List<MarketInfoModel>>() {
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return coingeckoAnswerModel;
    }
}

