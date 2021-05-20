package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import weatherthisngs.WeatherThings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Service {
    public static List<TgUser> userList=new ArrayList<>();
    public static SendMessage start(Update update){
        SendMessage sendMessage=new SendMessage();
        String firstName = update.getMessage().getFrom().getFirstName();
        sendMessage.setText("Hello, "+"<b>"+firstName+"</b>"+ " Welcome to our bot");
        sendMessage.setParseMode("HTML");
        InlineKeyboardMarkup inlineKeyboardMarkup=new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList=new ArrayList<>();
        List<InlineKeyboardButton> row=new ArrayList<>();
        row.add(new InlineKeyboardButton()
        .setText("Click this button to find out the weather")
        .setCallbackData("city"));
        rowList.add(row);
        inlineKeyboardMarkup.setKeyboard(rowList);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        sendMessage.setChatId(update.getMessage().getChatId());
        return sendMessage;
    }
        public static TgUser addUser(Update update){
        long chatId=update.getCallbackQuery()!=null?update.getCallbackQuery().getMessage().getChatId():update.getMessage().getChatId();
            for (TgUser user : userList) {
                if (user.getChatId()==chatId){
                return user;
                }
            }
            TgUser tgUser=new TgUser(chatId, BotState.SELECT_CITY,null);
            userList.add(tgUser);
            return tgUser;
        }
        public static void updateUser(TgUser user){
            for (TgUser tgUser : userList) {
             if (user.getChatId()==tgUser.getChatId()){
                 tgUser=user;
                 break;
             }
            }
        }
        public static SendMessage enterCity(Update update){
        SendMessage sendMessage=new SendMessage();
        long  chatId=update.getCallbackQuery().getMessage().getChatId();
            String data = update.getCallbackQuery().getData();
        TgUser user=addUser(update);
        user.setCity(data);
        user.setBotState(BotState.ENTER_WORD);
        updateUser(user);
        if (data.equals("city")){
            sendMessage.setText("Please select the city you want");
            sendMessage.setChatId(chatId);
        }
            return sendMessage;
        }
        public static SendMessage getWeather(Update update) throws IOException {
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
            String text = update.getMessage().getText();
        TgUser user=addUser(update);
        String region=user.getCity();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            WeatherThings weather;
            List<WeatherThings> weatherList = new ArrayList<>();
            try {
                URL   url = new URL("http://api.openweathermap.org/data/2.5/weather?q="+text+"&units=metric&appid=fc83374508520df78815015a2cf80ea7");
                URLConnection urlConnection= url.openConnection();
                JsonReader reader=new JsonReader(new InputStreamReader(urlConnection.getInputStream()));
                weather=gson.fromJson(reader,WeatherThings.class);
                weatherList.add(weather);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (weatherList.size()!=0){
                sendMessage.setText("<b>"+weatherList.get(0).getName()+"</b>"+"\n"+"Temperatura "+weatherList.get(0).getMain().getTemp()+"C\n"+
                        "Wind speed "+weatherList.get(0).getWind().getSpeed()+"\n"+"Id ➖ "+weatherList.get(0).getId()+"\n"+
                        "Lon ➖ "+weatherList.get(0).getCoord().getLon()+"\n"+
                        "Lat ➖ "+weatherList.get(0).getCoord().getLat());
                sendMessage.setParseMode("HTML");
            }else {
                sendMessage.setText("No such city was found. Please enter another city");
            }


            return sendMessage;
        }

}
