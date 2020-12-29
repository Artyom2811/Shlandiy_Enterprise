package models;

import java.time.LocalDateTime;

public class News {
    private String source;
    private String fullName;
    private String ticker;
    private String linkOfNews;
    private LocalDateTime dateTime;

    public String getSource() {
        return source;
    }

    public String getTicker() {
        return ticker;
    }

    public String getLinkOfNews() { return linkOfNews; }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public News(String source, String fullName, String ticker, String linkOfNews, LocalDateTime dateTime) {
        this.source = source;
        this.fullName = fullName;
        this.ticker = ticker;
        this.linkOfNews = linkOfNews;
        this.dateTime = dateTime;
    }
}
