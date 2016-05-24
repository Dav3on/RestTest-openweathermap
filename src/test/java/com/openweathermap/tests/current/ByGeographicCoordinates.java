package com.openweathermap.tests.current;

import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.CURRENT_WEATHER_URL;
import static com.openweathermap.Common.randomString;
import static java.util.Optional.empty;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.not;

//http://openweathermap.org/current#geo
public class ByGeographicCoordinates {
    private String cityName;
    private String countyCode;
    private Integer cityId;
    private Float lat;
    private Float lon;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        cityName = getCityNameFromMap(city);
        countyCode = getCountryCodeFromMap(city);
        cityId = getCityIdFromMap(city);
        lat = getLatNameFromMap(city);
        lon = getLonNameFromMap(city);
    }

    @After
    public void tearDown() {
        cityName = null;
        countyCode = null;
        cityId = null;
        lat = null;
        lon = null;
    }

    @Test
    public void status200WhenLatLonCorrect(){
        given().
                param("lat", lat).
                param("lon", lon).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void checkResponseContentTypes(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    param("lat", lat).
                    param("lon", lon).
                    param("mode", entry.getKey()).
            when().
                    get(CURRENT_WEATHER_URL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());
        }
    }

    @Test
    public void status404WhenLatIncorrect(){
        given().
                param("lat", randomString()).
                param("lon", lon).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenLonIncorrect(){
        given().
                param("lat", lat).
                param("lon", randomString()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void checkBodyMessageWhenLonIncorrect(){
        given().
                param("lat", lat).
                param("lon", randomString()).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkLatAndLonInResponse(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("coord.lat", equalTo(lat)).and().
                body("coord.lon", equalTo(lon));
    }

    //This test fails sometimes. Somehow in ISO standard by Kiev lat and lon both "Kiev" and "Misto Kyyiv"
    @Test
    public void checkCityNameIdAndCountyCodyByLatLonJSON(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityId)).and().
                body("sys.country", equalTo(countyCode));
    }

    //The same as previous test fails sometimes because its both "Kiev" and "Misto Kyyiv" by Kiev lat and lon

    @Test
    public void checkCityNameIdAndCountyCodyByLatLonXML(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("current.city.@name", equalTo(cityName)).and().
                body("current.city.@id", equalTo(cityId.toString())).and().
                body("current.city.country", equalTo(countyCode));
    }

    @Test
    public void checkWeatherIsNotEmptyInJSON(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("main.temp", not(empty())).and().
                body("weather.description", not(empty()));
    }

    @Test
    public void checkWeatherIsNotEmptyInXML(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("current.temperature.@value ", not(empty())).and().
                body("current.clouds.@value ", not(empty()));
    }
}
