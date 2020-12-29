package priceSites;

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "base",
        "target",
        "trade_url",
        "market"
})
@JsonIgnoreProperties({
        "last",
        "volume",
        "converted_last",
        "converted_volume",
        "trust_score",
        "bid_ask_spread_percentage",
        "timestamp",
        "last_traded_at",
        "last_fetch_at",
        "is_anomaly",
        "is_stale",
        "token_info_url",
        "coin_id",
        "target_coin_id"})
public class MarketInfoModel {

    @JsonProperty("base")
    private String base;
    @JsonProperty("target")
    private String target;
    @JsonProperty("trade_url")
    private String tradeUrl;
    @JsonProperty("market")
    private MarketModel market;

    @JsonProperty("base")
    public String getBase() {
        return base;
    }

    @JsonProperty("target")
    public String getTarget() {
        return target;
    }

    @JsonProperty("trade_url")
    public String getTradeUrl() { return tradeUrl; }

    @JsonProperty("market")
    public MarketModel getMarket() { return market; }
}