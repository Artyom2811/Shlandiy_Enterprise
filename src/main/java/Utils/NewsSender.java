package Utils;

import models.News;
import priceSites.MarketInfoModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class NewsSender {
    static String recipientMail = AppProperties.prop.getProperty("recipient.mail");
    static String senderMail = AppProperties.prop.getProperty("sender.mail");
    static String senderPassword = AppProperties.prop.getProperty("sender.password");

    static EmailSender emailSender = new EmailSender(senderMail, senderPassword, recipientMail);
    static TelegramSender telegramSender = new TelegramSender();

    static String newRow = "\n";

    public static void sendNewsNotification(News news, BigDecimal percentOfChanges, List<MarketInfoModel> listOfMarketInfo) {

        //Email
        StringBuilder bodyOfMailMessage = new StringBuilder("<span><a href='" + news.getLinkOfNews() + "'>" +
                news.getSource() + "</a></span>" + "<span> - " + news.getTicker() + " - prirost - " +
                percentOfChanges + "%. " + "Vremya ot anonsa novosty - " + getMinuteDifferenceForNow(news.getDateTime()) + " minute </span>");

        for (MarketInfoModel e : listOfMarketInfo) {
            bodyOfMailMessage.append("<div>" + e.getMarket().getName() + ": " + "<a href=\"" + e.getTradeUrl() + "\">" + e.getBase() + "/" + e.getTarget() + "</a></div>" + "\n");
        }

        sentToMail(
                "Обнаружена валюта для покупки - " +  news.getSource() + " - " + news.getTicker(),
                bodyOfMailMessage.toString()
        );

        //Telegram
        StringBuilder bodyOfTelegramMessage = new StringBuilder(
                "Обнаружена валюта для покупки" + newRow +
                news.getLinkOfNews() + " " + news.getSource() + " " + news.getTicker() + newRow +
                "прирост " + percentOfChanges + "%. " + newRow +
                "Время от аносна новости - " + getMinuteDifferenceForNow(news.getDateTime()) + " минут(а)" + newRow
        );

        for (MarketInfoModel e : listOfMarketInfo) {
            bodyOfTelegramMessage.append(e.getMarket().getName() + ": " + e.getBase() + "/" + e.getTarget() + " - " + e.getTradeUrl() + newRow);
        }

        sentToTelegram(bodyOfTelegramMessage.toString());
//        sentToTelegram("Обнаружена валюта для покупки - " + news.getSource() + "(" + news.getTicker() + ")" + newRow + "Проверте почту!");
    }

    public static void sendErrorNotification(String subject, String textOfMessage) {
        sentToMail(subject, textOfMessage);
        sentToTelegram(textOfMessage);
    }

    static void sentToMail(String subject, String textOfMessage) {
        emailSender.send(subject, textOfMessage);
    }

    static void sentToTelegram(String textOfMessage) {
        try {
            telegramSender.send(textOfMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long getMinuteDifferenceForNow(LocalDateTime startDateTime) {
        LocalDateTime endDateTime = LocalDateTime.now();
        long f = (Duration.between(startDateTime, endDateTime).getSeconds() / 60);

        return f;
    }
}
