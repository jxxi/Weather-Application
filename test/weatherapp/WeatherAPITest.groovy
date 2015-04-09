package weatherapp

import spock.lang.*

class WeatherAPITest extends Specification{
    WeatherAPI weatherAPI
    def houston
    def houstonJsonData
    def parseJsonCalled
    def getRawDataCalled

    def setup() {
        weatherAPI = new WeatherAPI()
        houston = ["Houston", 60.50, ["cloudy"]]
        houstonJsonData = "{\"coord\":{\"lon\":-95.37,\"lat\":29.76},\"sys\":{\"message\":0.7572,\"country\":\"United States of America\",\"sunrise\":1426163646,\"sunset\":1426206485},\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"cloudy\",\"icon\":\"10n\"}],\"base\":\"cmc stations\",\"main\":{\"temp\":60.50,\"temp_min\":58.87,\"temp_max\":58.87,\"pressure\":1031.43,\"sea_level\":1032.84,\"grnd_level\":1031.43,\"humidity\":92},\"wind\":{\"speed\":9.74,\"deg\":1.00061},\"clouds\":{\"all\":92},\"rain\":{\"3h\":1.45},\"dt\":1426129082,\"id\":4699066,\"name\":\"Houston\",\"cod\":200}"
        parseJsonCalled = false
        getRawDataCalled = false
    }

    def "connectToAPI throws connectException when passed an invalid URL"(){
        when:
        weatherAPI.connectToAPI(new URL("http://thissiteisalwaysdown.com/"))
        then:
        thrown(ConnectException)
    }

    def mockOutConnectToAPI(instance){
        instance.metaClass.connectToAPI = {url ->
            houstonJsonData
        }
    }

    def "getRawData takes a city and returns a json string"(){
        mockOutConnectToAPI(weatherAPI)
        expect:
        weatherAPI.getRawData("Houston") == houstonJsonData
    }

    def mockOutConnectToAPIException(instance){
        instance.metaClass.connectToAPI = {url ->
            throw new ConnectException()
        }
    }

    def "getRawData throws a ConnectionException if the API is offline"(){
        mockOutConnectToAPIException(weatherAPI)
        when:
        weatherAPI.getRawData("Houston")
        then:
        thrown(ConnectException)
    }

    def "parseJson returns result of city name, temperature, and weather description when passed a valid json string"(){
        expect:
        weatherAPI.parseJson(houstonJsonData) == houston
    }

    def "weatherAPI parseJson throws exception if passed invalid string"(){
        when:
        weatherAPI.parseJson("")
        then:
        thrown(IllegalArgumentException)
    }

    def "parseJson throws exception if there are no valid weather fields in the json string"(){
        when:
        weatherAPI.parseJson("{\"coord\":{\"lon\":-95.37,\"lat\":29.76},\"sys\":{\"message\":0.7572,\"country\":\"United States of America\",\"sunrise\":1426163646,\"sunset\":1426206485}}")
        then:
        thrown(IllegalArgumentException)
    }

    def mockOutParseJson(instance){
        instance.metaClass.parseJson = {jsonText ->
            parseJsonCalled = true
        }
    }

    def mockOutGetRawData(instance){
        instance.metaClass.getRawData = {cityName ->
            getRawDataCalled = true
        }
    }

    def "when getCityData is called getRawData and parseJson are called"(){
        mockOutParseJson(weatherAPI)
        mockOutGetRawData(weatherAPI)
        weatherAPI.getCityData("Houston")
        expect:
        parseJsonCalled == true
        getRawDataCalled == true
    }

    def "when getCityData is called with a valid city it returns the correct tuple"(){
        mockOutConnectToAPI(weatherAPI)
        expect:
        weatherAPI.getCityData("Houston") == houston
    }

    def mockOutConnectToAPIinvalidCity(instance){
        instance.metaClass.connectToAPI = {url ->
            "{\"message\":\"Error: Not found city\",\"cod\":\"404\"}\n"
        }
    }

    def "when getCityData is called with an invalid city it returns a tuple with an exception in the 2 position"(){
        mockOutConnectToAPIinvalidCity(weatherAPI)
        when:
        def result = weatherAPI.getCityData("1234")
        then:
        (result[2] instanceof Exception)
    }
}
