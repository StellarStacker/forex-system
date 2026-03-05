package foreignmoneyexchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ForexService {

    public static double getRates(String fromCurrencyCode, String toCurrencyCode) throws Exception {

        String apiKey = "33d80f50a51f9f995885cf7f";

        String urlString =
        "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/"+fromCurrencyCode;

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
        System.out.println(response);
        return getCurrencyRate(response.toString(),toCurrencyCode);
    }

    // Extract rate from JSON response
    public static double getCurrencyRate(String apiResponse, String currency){

        String search = "\"" + currency + "\":";

        int index = apiResponse.indexOf(search);

        if(index == -1){
            return -1;
        }

        int start = index + search.length();

        int end = apiResponse.indexOf(",", start);

        if(end == -1){
            end = apiResponse.indexOf("}", start);
        }

        String rate = apiResponse.substring(start, end);                                                                                                                                         
        System.out.println(rate);
     
        return Double.parseDouble(rate);
    }
    public static void main(String args[]) throws Exception{
        System.out.println(ForexService.getRates("INR","USD"));
    }
}