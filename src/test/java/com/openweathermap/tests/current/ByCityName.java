package com.openweathermap.tests.current;

import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.equalTo;
import static com.openweathermap.Common.*;

//http://openweathermap.org/current#name
public class ByCityName {
    private String cityName;
    private String countyCode;
    private Integer cityId;

    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        cityName = getCityNameFromMap(city);
        countyCode = getCountryCodeFromMap(city);
        cityId = getCityIdFromMap(city);
    }

    @After
    public void tearDown() {
        cityName = null;
        countyCode = null;
        cityId = null;
    }

    @Test
    public void status200WhenCityNameCorrect(){
        given().
                param("q", cityName).
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
                    param("q", cityName).
                    param("mode", entry.getKey()).
            when().
                    get(CURRENT_WEATHER_URL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());
        }
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
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkCityIdInJSONPayload(){
        given().
                param("q", cityName).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityId));
    }

    @Test
    public void checkCountryCodeInJSONPayload(){
        given().
                param("q", cityName+","+countyCode).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityId));
    }

    @Test
    public void checkCityIdInXMLPayload(){
        given().
                param("q", cityName).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("current.city.@name", equalTo(cityName)).and().
                body("current.city.@id", equalTo(cityId.toString()));
    }

    @Test
    public void checkCountryCodeInXMLPayload(){
        given().
                param("q", cityName+","+countyCode).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("current.city.@name", equalTo(cityName)).and().
                body("current.city.@id", equalTo(cityId.toString()));
    }
}
