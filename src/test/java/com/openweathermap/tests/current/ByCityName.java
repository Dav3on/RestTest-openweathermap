package com.openweathermap.tests.current;

import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.XML;
import static org.hamcrest.CoreMatchers.equalTo;
import static com.openweathermap.Common.*;

public class ByCityName {
    String cityName;
    String countyCode;
    Integer cityCode;

    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        cityName = getCityNameFromMap(city);
        countyCode = getCountryCodeFromMap(city);
        cityCode = getCityCodeFromMap(city);
    }

    @Test
    public void status200WhenKeyCorrect(){
        given().
                param("q", cityName).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifError().
                statusCode(200);
    }

    @Test
    public void defaultResponseContentTypeIsJSON(){
        given().
                param("q", cityName).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifError().
                contentType(JSON);
    }

    @Test
    public void responseContentTypeIsJSON(){
        given().
                param("q", cityName).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifError().
                contentType(JSON);
    }

    @Test
    public void responseContentTypeIsXML(){
        given().
                param("q", cityName).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifError().
                contentType(XML);
    }

    @Test
    public void status404WhenCityNameIncorrect(){
        given().
                param("q", randomString()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    //This test fails sometimes. The system associate random string with some city even if city name has numbers. Magic.
    @Test
    public void checkBodyMessageWhenCityNameIncorrect(){
        given().
                param("q", randomString()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkCityIdInPayload(){
        given().
                param("q", cityName).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityCode));
    }

    @Test
    public void checkCountryCodeInPayload(){
        given().
                param("q", cityName+","+countyCode).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityCode));
    }
}
