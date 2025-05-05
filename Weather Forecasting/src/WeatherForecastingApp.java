import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class WeatherForecastingApp {
    private static JFrame frame; 
    private static JTextField locationField;
    private static JTextArea weatherDisplay;
    private static JButton fetchButton;
    private static String apikey = "2e0d48cc36df888e488e74xxxxxxxx"; //generate own apikey.

    private static String fetchWeatherData(String city){
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apikey);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null){
                response.append(line);
            }
            reader.close();

            JSONObject jsonObject = (JSONObject) JSONValue.parse(response.toString());
            JSONObject mainObj = (JSONObject) jsonObject.get("main");

            double temperatureKelvin = ((Number) mainObj.get("temp")).doubleValue();
            long humidity = ((Number) mainObj.get("humidity")).longValue();
            double temperatureCelsius = temperatureKelvin - 273.15;

            JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
            JSONObject weather = (JSONObject) weatherArray.get(0);
            String description = (String) weather.get("description");

            return "Description: " + description +
                   "\nTemperature: " + String.format("%.2f", temperatureCelsius) + " °C" +
                   "\nHumidity: " + humidity + "%";

        } catch (Exception e) {
            return "Failed to fetch weather data: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        frame = new JFrame("Weather Forecasting App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new FlowLayout());

        locationField = new JTextField(15);
        fetchButton = new JButton("Fetch Weather");
        weatherDisplay = new JTextArea(10, 30);
        weatherDisplay.setEditable(false);

        frame.add(new JLabel("Enter City Name: "));
        frame.add(locationField);
        frame.add(fetchButton);
        frame.add(new JScrollPane(weatherDisplay));

        fetchButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String city = locationField.getText().trim();
                if (!city.isEmpty()) {
                    String weatherInfo = fetchWeatherData(city);
                    weatherDisplay.setText(weatherInfo);
                } else {
                    weatherDisplay.setText("Please enter a city name.");
                }
            }
        });

        frame.setVisible(true);
    }
}
