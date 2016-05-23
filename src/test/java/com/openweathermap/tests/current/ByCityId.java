package com.openweathermap.tests.current;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.CURRENT_WEATHER_URL;
import static org.hamcrest.CoreMatchers.equalTo;

import com.jayway.restassured.http.ContentType;

//http://openweathermap.org/current#cityid
public class ByCityId {
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
    public void status200WhenCityIdCorrect(){
        given().
                param("id", cityId).
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
                    param("id", cityId).
                    param("mode", entry.getKey()).
            when().
                    get(CURRENT_WEATHER_URL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());
        }
    }

    @Test
    public void status404WhenCityIdIncorrect(){
        given().
                param("id", randomString()).
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void checkBodyMessageWhenCityIdIncorrect(){
        given().
                param("id", randomString()).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkCityIdAndNameInJSONPayload(){
        given().
                param("id", cityId).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_URL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityId)).and().
                body("sys.country", equalTo(countyCode));
    }

}
