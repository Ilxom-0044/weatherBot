import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import service.ObhavoBot;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi=new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new ObhavoBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
