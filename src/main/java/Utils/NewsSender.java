package Utils;

import models.News;

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

    static String newRow = "%0A";

    public static void sendNewsNotification(News news, BigDecimal percentOfChanges, List<String> listMarketInfo) {
        String subject = "Обнаружена валюта для покупки - " +  news.getSource() + " - " + news.getTicker();

        //Email
        StringBuilder bodyOfMailMessage = new StringBuilder("<span><a href='" + news.getLinkOfNews() + "'>" +
                news.getSource() + "</a></span>" + "<span> - " + news.getTicker() + " - prirost - " +
                percentOfChanges + "%. " + "Vremya ot anonsa novosty - " + getMinuteDifferenceForNow(news.getDateTime()) + " minute </span>");

        for (String s : listMarketInfo) {
            bodyOfMailMessage.append(s + "\n");
        }

        sentToMail(subject, bodyOfMailMessage.toString());

        //Telegram
//        StringBuilder bodyOfTelegramMessage = new StringBuilder(
//                subject + newRow +
//                news.getLinkOfNews() + " " + news.getSource() + " " + news.getTicker() + "прирост " + percentOfChanges + "%. " +
//                "Время от аносна новости - " + getMinuteDifferenceForNow(news.getDateTime()) + " минут(а)"
//        );
//        sentToTelegram(bodyOfTelegramMessage.toString());
        sentToTelegram("Обнаружена валюта для покупки - " + news.getTicker() + newRow + "Проверте почту.");
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
