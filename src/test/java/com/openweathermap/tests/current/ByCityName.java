package com.openweathermap.tests.current;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.XML;
import static org.hamcrest.CoreMatchers.equalTo;

import static com.openweathermap.Common.*;

public class ByCityName {

    @Test
    public void status200WhenKeyCorrect(){
        given().
                param("q", getRandomCityName()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifError().
                statusCode(200);
    }

    @Test
    public void defaultResponseContentTypeIsJSON(){
        given().
                param("q", getRandomCityName()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifError().
                contentType(JSON);
    }

    @Test
    public void responseContentTypeIsJSON(){
        given().
                param("q", getRandomCityName()).
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
                param("q", getRandomCityName()).
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
    public void checkCityCountryCode(){
        HashMap<String, String> cityWithCode = getRandomCityNameWithCode();
        String cityName = getCityNameFromMap(cityWithCode);
        Integer cityCode = getCityCodeFromMap(cityWithCode);

        given().
                param("q", cityWithCode.keySet()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityCode));
    }
}
