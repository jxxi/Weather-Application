package weatherapp

import groovy.json.JsonSlurper

class WeatherAPI implements WeatherService{
    final API_URL = "http://api.openweathermap.org/data/2.5/weather?q="
    def units

    WeatherAPI(){
        units = ",us&units=imperial"
    }

    def getRawData(cityName){
        def urlWithCity = API_URL + cityName + units
        def url = new URL(urlWithCity)
        connectToAPI(url)
    }

    def connectToAPI(url){
        try {
            url.getText()
        }
        catch(e){
            throw new ConnectException(e.message)
        }
    }

    def getCityData(cityName){
        try {
            parseJson(getRawData(cityName))
        }
        catch(e){
            [cityName, 0.00, e]
        }
    }

    def parseJson(jsonText){
        def jsonParser = new JsonSlurper()
        def result = jsonParser.parseText(jsonText)
        if(result.name == null || result.main.temp == null || result.weather.description == null)
            throw new IllegalArgumentException("Error: City Not Found")
        [result.name, result.main.temp, result.weather.description]
    }
}
