package weatherapp


class WeatherApp{
    WeatherService weatherService

    def getWeatherReport(cityList){

        def weatherReport = [hottestCity: [], coldestCity: [], weatherForCities: [], errorCities: []]
        def revisedCityList = removeDuplicateCityNames(cityList)
        def listOfCityInformation = revisedCityList.collect { weatherService.getCityData(it) }
        
        if(!listOfCityInformation.isEmpty()) {

            def noErrorList = listOfCityInformation.findAll { !(it[2] instanceof Exception) }
            weatherReport.hottestCity = findHottestCity(noErrorList)
            weatherReport.coldestCity = findColdestCity(noErrorList)
            weatherReport.weatherForCities = sortByCity(noErrorList)
            weatherReport.errorCities = listOfCityInformation.findAll { (it[2] instanceof Exception) }

        }
        weatherReport
    }

    def removeDuplicateCityNames(cityList){
        cityList.unique()
    }

    def sortByCity(weatherData){
        weatherData.sort { it[0] }
    }

    def findColdestCity(weatherData){
        def coldestTemperature = weatherData.min { it[1] }?.get(1)
        weatherData.findAll { it[1] == coldestTemperature }.collect { it[0] }
    }

    def findHottestCity(weatherData){
        def hottestTemperature = weatherData.max { it[1] }?.get(1)
        weatherData.findAll { it[1] == hottestTemperature }.collect { it[0] }
    }

    def setWeatherService(weatherServ){
        weatherService = weatherServ
    }
}