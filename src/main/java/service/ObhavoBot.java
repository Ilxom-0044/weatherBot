package service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

public class ObhavoBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            String text = update.getMessage().getText();
            if (text.equals("/start")){
                try {
                    execute(Service.start(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }else {
                TgUser user=Service.addUser(update);
                if (user.getBotState().equals(BotState.ENTER_WORD)){
                    try {
                        execute(Service.getWeather(update));
                        execute(Service.start(update));
                    } catch (TelegramApiException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }else if (update.getCallbackQuery()!=null){
            String data = update.getCallbackQuery().getData();
            if (data.equals("city")){
                try {
                    execute(Service.enterCity(update));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public String getBotUsername() {

        return "WeatherG10Bot";
    }

    @Override
    public String getBotToken() {

        return "1777645462:AAHEpnpf8iED01rq_CKkdAQYpGkRrc2IUwI";
    }
}
