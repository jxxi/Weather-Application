package weatherapp.ui

import weatherapp.WeatherAPI
import weatherapp.WeatherApp

def weatherApp = new WeatherApp()
def weatherAPI = new WeatherAPI()

weatherApp.weatherService = weatherAPI
def cityList = []
def input = new File('cityList.txt')
input.eachLine{
    cityList << it
}

def result = weatherApp.getWeatherReport(cityList)

println("Hottest City (s): " + result.hottestCity)
println("Coldest City (s): " + result.coldestCity)
println("Weather Report: ")
println(result.weatherForCities)
println("Error Report: ")
println(result.errorCities)