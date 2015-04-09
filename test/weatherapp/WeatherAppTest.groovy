package weatherapp

import spock.lang.*

class WeatherAppTest extends Specification{
    WeatherApp weatherapp
    WeatherService weatherService
    def weatherData
    def cityList
    def houston
    def dallas
    def austin

    def setup() {
        weatherapp = new WeatherApp()
        weatherService = Mock()
        houston = ["Houston", 60.50, ["cloudy"]]
        dallas = ["Dallas", 54.90, ["overcast"]]
        austin = ["Austin", 56.70, ["light drizzle"]]
        weatherData = [houston, dallas, austin]
    }

    def "canary test"(){
        expect:
        true
    }

    def "sortByCity sorts data by city name"(){
        expect:
        [austin, dallas, houston] == weatherapp.sortByCity(weatherData)
    }

    def "sortByCity returns empty weatherData as empty"(){
        weatherData = []
        expect:
        [] == weatherapp.sortByCity(weatherData)
    }

    def "sortByCity returns data already in order by city name as the same"(){
        weatherData = [austin, dallas, houston]
        expect:
        [austin, dallas, houston] == weatherapp.sortByCity(weatherData)
    }

    def "findColdestCity returns the city with the coldest temperature"(){
        expect:
        ["Dallas"] == weatherapp.findColdestCity(weatherData)
    }

    def "findColdestCity returns an empty list if weatherData is empty"(){
        weatherData = []
        expect:
        [] == weatherapp.findColdestCity(weatherData)
    }

    def "findColdestCity returns a list of coldest cities if coldest temperatures are repeated"(){
        weatherData = [["Dallas", 54.90, "light drizzle"], ["San Antonio", 54.90, "overcast"], ["Corpus Christi", 54.90, "overcast"]]
        expect:
        ["Dallas", "San Antonio", "Corpus Christi"] == weatherapp.findColdestCity(weatherData)
    }

    def "findColdestCity returns the only city if only one city is passed"(){
        weatherData = [houston]
        expect:
        ["Houston"] == weatherapp.findColdestCity(weatherData)
    }

    def "findHottestCity returns the name of the city with the hottest temperature"(){
        expect:
        ["Houston"] == weatherapp.findHottestCity(weatherData)
    }

    def "findHottestCity returns an empty list if weatherData is empty"(){
        weatherData = []
        expect:
        [] == weatherapp.findHottestCity(weatherData)
    }

    def "findHottestCity returns a list of hottest cities if hottest temperatures are repeated"(){
        weatherData << ["Fort Worth", 60.50, "light rain"]
        expect:
        ["Houston", "Fort Worth"] == weatherapp.findHottestCity(weatherData)
    }

    def "findHottestCity returns the only city if only one city is passed"(){
        weatherData = [houston]
        expect:
        ["Houston"] == weatherapp.findHottestCity(weatherData)
    }

    def "getWeatherReport takes a list of cities and returns all of the city information sorted by name, as well as hottest and coldest city name"() {
        weatherService.getCityData("Houston") >> houston
        weatherService.getCityData("Dallas") >> dallas
        weatherService.getCityData("Austin") >> austin

        weatherapp.setWeatherService(weatherService)

        cityList = ["Houston", "Austin", "Dallas"]
        expect:
        def result = weatherapp.getWeatherReport(cityList)
        result.hottestCity == ["Houston"]
        result.coldestCity == ["Dallas"]
        result.weatherForCities == [austin, dallas, houston]
        result.errorCities == []
    }

    def "getWeatherReport takes an empty list of cities and returns with empty results"(){
        cityList = []
        expect:
        def result = weatherapp.getWeatherReport(cityList)
        result.hottestCity == []
        result.coldestCity == []
        result.weatherForCities == []
        result.errorCities == []
    }

    def "getWeatherReport takes one city and returns all of the city information sorted by name, and that city as hottest and coldest"(){
        weatherService.getCityData("Houston") >> houston
        weatherapp.setWeatherService(weatherService)
        cityList = ["Houston"]
        expect:
        def result = weatherapp.getWeatherReport(cityList)
        result.hottestCity == ["Houston"]
        result.coldestCity == ["Houston"]
        result.weatherForCities == [houston]
        result.errorCities == []
    }

    def "getWeatherReport returns empty for hottest coldest and weather if the cities do not exist"(){
        weatherService.getCityData("Galactic City") >> ["Galactic City", 0.00, new RuntimeException()]
        weatherService.getCityData("Gotham") >> ["Gotham", 0.00, new RuntimeException()]
        weatherapp.setWeatherService(weatherService)
        cityList = ["Galactic City", "Gotham"]

        def result = weatherapp.getWeatherReport(cityList)
        expect:
        result.hottestCity == []
        result.coldestCity == []
        result.weatherForCities == []
        result.errorCities[0][2] instanceof Exception
        result.errorCities[1][2] instanceof Exception
    }

    def "getWeatherReport returns the existing city data even if one of the cities does not exist "(){
        weatherService.getCityData("Dallas") >> dallas
        weatherService.getCityData("Galactic City") >> ["Galactic City", 0.00, new RuntimeException()]
        weatherapp.setWeatherService(weatherService)
        cityList = ["Galactic City", "Dallas"]

        def result = weatherapp.getWeatherReport(cityList)
        expect:
        result.hottestCity == ["Dallas"]
        result.coldestCity == ["Dallas"]
        result.weatherForCities == [dallas]
        result.errorCities[0][2] instanceof Exception
    }

    def "removeDuplicateCityNames removes duplications if a city is entered more than once"(){
        cityList = ["Houston", "Houston", "Austin"]
        expect:
        ["Houston", "Austin"] == weatherapp.removeDuplicateCityNames(cityList)
    }

    def "removeDuplicateCityNames returns an empty cityList as empty"(){
        cityList = []
        expect:
        [] == weatherapp.removeDuplicateCityNames(cityList)
    }


}
