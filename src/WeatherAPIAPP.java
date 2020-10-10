/*
Develop an API proxy for acquiring weather data.

@author Daryl Howe, John Hanlon & Simon Monaghan.

*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherAPIAPP {

	public static void main(String[] args) {

		System.out.println("What city would you like the weather for? ");
		System.out.println("You can enter any city in the world and also specify a country by adding a comma and typing the country code. For example, for Dublin in Ireland: Dublin, IE ");
		System.out.println("Some commonly used country codes are: GB (Great Britain), US (United States), CA (Canada), JP (Japan), BR (Brazil), IN (India), CN (China), RU (Russia) ");
		System.out.println("Please enter a city (and country code if required): ");
		Scanner input = new Scanner(System.in);
		
		// The user makes a GET request to the application specifying a city and possibly also a country code.
		String city = input.nextLine();

		try {
			WeatherAPIAPP.callAPI(city);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	A method which calls the Open Weather Map API and sends a GET request for the city string input.
	@param city is a string for the city information to be retrieved.
	@throws Exception throws exception to calling method.
	*/
	public static void callAPI(String city) throws Exception {

		// The URL that we need to access the information is made up of Strings (Open Weather Map URL & API Key) and the user input.
		String APIUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city
				+ "&appid=bfc5bf63bd68e740142dcefdaa7a2b6d";

		// Create URL based on the above strings and user input.
		URL url = new URL(APIUrl);
		
		// Create client for the URL.
		HttpURLConnection apiConnection = (HttpURLConnection) url.openConnection();

		// Ensures we send out a GET request.
		apiConnection.setRequestMethod("GET");

		// Gets the response code status update - this tells us if the GET request was successful (indicated by a 'Status 200').
		int responseCode = apiConnection.getResponseCode();
		System.out.println("Sending 'GET' request to URL : " + url);
		System.out.println("Response Code Status : " + responseCode);
		System.out.println("");

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
		String inputLine;
		StringBuffer APIResponse = new StringBuffer();

		// While there is still data left..
		while ((inputLine = bufferedReader.readLine()) != null) {

			// Keep adding it to the end of the 'APIResponse' String.
			APIResponse.append(inputLine);
		}
		bufferedReader.close();

		// Print String to console - this is the entire response as JSON.
		System.out.println("JSON RESPONSE: " + APIResponse);
		System.out.println();
		
		extractData(APIResponse.toString());
	}
	
	
	/*
	A method which extracts the decided upon JSON data from the APIResponse.
	@param APIResponse is a string response from the API.
	*/
	// curl -vi -X GET -G "https://api.openweathermap.org/data/2.5/weather?q=Dublin,%20IE&appid=bfc5bf63bd68e740142dcefdaa7a2b6d"
	public static void extractData(String APIResponse) {
		
		try {

			// Create a jsonObject using 'APIResponse' stream.
			JSONObject jsonObject = new JSONObject(APIResponse.toString());

			// Get the 'sys' JSONObject from the jsonObject, get the 'country' value from the 'sys'.
			// This is a JSONObject nested within a JSONObject.
			String countryValue = jsonObject.getJSONObject("sys").getString("country");
			System.out.println("Country: " + countryValue);

			// Get the value from the 'name' category.
			String cityValue = jsonObject.getString("name");
			System.out.println("City: " + cityValue);

			// Get the 'wind' JSONObject from the jsonObject, get the 'speed' value from the 'wind'.
			double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
			// Convert wind-speed to Km/h.
			windSpeed = windSpeed * 3.6;
			windSpeed = (int) (windSpeed);
			System.out.println("Wind Speed: " + windSpeed + " Km/h");

			// The information within "weather" is stored as an s it contains many categories.
			String weatherInfo = jsonObject.getJSONArray("weather").toString();
			JSONArray jArray = new JSONArray(weatherInfo);

			// Iterate through the array..
			for (int i = 0; i < jArray.length(); i++) {

				JSONObject jsonPart = jArray.getJSONObject(i);
				String weatherDescription = jsonPart.getString("main");
				System.out.println("Weather Description: " + weatherDescription);

				double tempValue = jsonObject.getJSONObject("main").getDouble("temp");
				tempValue = tempValue - 273.15;
				tempValue = (int) tempValue;
				System.out.println("Temperature: " + tempValue + "°");

				double feelsLike = jsonObject.getJSONObject("main").getDouble("feels_like");
				feelsLike = feelsLike - 273.15;
				feelsLike = (int) feelsLike;
				System.out.println("Temperature (feels like): " + feelsLike + "°");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

