package priceSites;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "stats",
        "total_volumes"
})
public class CoingeckoPriceModel {

    @JsonProperty("stats")
    private List<List<String>> stats = null;
    @JsonProperty("total_volumes")
    private List<List<String>> totalVolumes = null;

    @JsonProperty("stats")
    public List<List<String>> getStats() { return stats; }

    @JsonProperty("total_volumes")
    public List<List<String>> getTotalVolumes() {
        return totalVolumes;
    }
}