package models;


import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "title",
        "path"
})
@JsonIgnoreProperties({
        "id",
        "summary",
        "tags",
        "images",
        "hot",
        "stick",
        "publish_at",
        "first_publish_at",
        "is_new",
        "publish_ts",
        "categories"})
public class KucoinModel {

    @JsonProperty("title")
    private String title;
    @JsonProperty("path")
    private String path;

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @JsonProperty("path")
    public void setPath(String path) {
        this.path = path;
    }
}