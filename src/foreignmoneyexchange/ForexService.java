package foreignmoneyexchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForexService {

    public static String getRates() throws Exception {

        String apiKey = "do you think you can seeit?";
        String urlString = "https://v6.exchangerate-api.com/v6/"
                + apiKey + "/latest/USD";

        URL url = new URL(urlString);
        HttpURLConnection con =
                (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");

        BufferedReader in =
                new BufferedReader(
                        new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }
    
    public static void main(String[] args) throws Exception{
       String apiReponse = getRates();
       parseApi(apiReponse);
    }
    
    public static void parseApi(String apiReponse){
        
    }
}