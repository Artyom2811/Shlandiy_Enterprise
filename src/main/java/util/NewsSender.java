package util;

import models.News;
import priceSites.MarketInfoModel;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsSender {
    static String recipientMail = AppProperties.prop.getProperty("recipient.mail");
    static String senderMail = AppProperties.prop.getProperty("sender.mail");
    static String senderPassword = AppProperties.prop.getProperty("sender.password");

    static EmailSender emailSender = new EmailSender(senderMail, senderPassword, recipientMail);
    static TelegramSender telegramSender = new TelegramSender();

    static String nextRow = "\n";

    public static void sendNewsNotification(News news, BigDecimal percentOfChanges, List<MarketInfoModel> listOfMarketInfo) {

        //Email
        StringBuilder bodyOfMailMessage = new StringBuilder("<span><a href='" + news.getLinkOfNews() + "'>" +
                news.getSource() + "</a></span>" + "<span> - " + news.getTicker() + " - prirost - " +
                percentOfChanges + "%. " + "Vremya ot anonsa novosty - " + getMinuteDifferenceForNow(news.getDateTime()) + " minute </span>");

        for (MarketInfoModel e : listOfMarketInfo) {
            bodyOfMailMessage.append("<div>" + e.getMarket().getName() + ": " + "<a href=\"" + e.getTradeUrl() + "\">" + e.getBase() + "/" + e.getTarget() + "</a></div>" + "\n");
        }

        sentToMail(
                "Обнаружена валюта для покупки - " + news.getSource() + " - " + news.getTicker(),
                bodyOfMailMessage.toString()
        );

        //Telegram
        String headerOfTelegramMessage =
                "Обнаружена валюта для покупки" + nextRow +
                        news.getLinkOfNews() + " " + news.getSource() + " " + news.getTicker() + nextRow +
                        "прирост " + percentOfChanges + "%. " + nextRow +
                        "Время от аносна новости - " + getMinuteDifferenceForNow(news.getDateTime()) + " минут(а)" + nextRow;

        List<String> listOfBodyOfTelegramMessage = new ArrayList<>();

        StringBuilder tempTextForToSplit = new StringBuilder(headerOfTelegramMessage);

        for (MarketInfoModel e : listOfMarketInfo) {
            String newRow = e.getMarket().getName() + ": " + e.getBase() + "/" + e.getTarget() + " - " + e.getTradeUrl() + nextRow;

            if ((tempTextForToSplit.length() + newRow.length()) < 4095) {
                tempTextForToSplit.append(newRow);
            } else {
                listOfBodyOfTelegramMessage.add(tempTextForToSplit.toString());
                tempTextForToSplit = new StringBuilder();
            }
        }

        listOfBodyOfTelegramMessage.add(tempTextForToSplit.toString());

        sentToTelegram(listOfBodyOfTelegramMessage);
//        sentToTelegram("Обнаружена валюта для покупки - " + news.getSource() + "(" + news.getTicker() + ")" + newRow + "Проверте почту!");
    }

    public static void sendErrorNotification(String subject, String textOfMessage) {
        sentToMail(subject, textOfMessage);
        telegramSender.send(textOfMessage);
    }

    static void sentToMail(String subject, String textOfMessage) {
        emailSender.send(subject, textOfMessage);
    }

    static void sentToTelegram(List<String> listOfPartsOfMessage) {
        telegramSender.send(listOfPartsOfMessage);
    }

    private static long getMinuteDifferenceForNow(LocalDateTime startDateTime) {
        LocalDateTime endDateTime = LocalDateTime.now();
        long f = (Duration.between(startDateTime, endDateTime).getSeconds() / 60);

        return f;
    }
}
