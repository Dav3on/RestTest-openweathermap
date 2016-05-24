package com.openweathermap.tests.current;

import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.getLatNameFromMap;
import static com.openweathermap.Common.getLonNameFromMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

//http://openweathermap.org/current#cycle
public class CitiesInCycle {
    private Float lat;
    private Float lon;
    private String cityName;
    private Integer cnt;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        lat = getLatNameFromMap(city);
        lon = getLonNameFromMap(city);
        cityName = getCityNameFromMap(city);
        cnt = new Random().nextInt(10)+1; // for range 1-11
    }

    @After
    public void tearDown() {
        lat = null;
        lon = null;
        cityName = null;
        cnt = null;
    }

    @Test
    public void status200WhenLatLongCorrect(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void checkResponseContentTypes(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            if (entry.getValue() != ContentType.HTML) {     //HTML not supported
                given().
                        param("lat", lat).
                        param("lon", lon).
                        param("cnt", cnt).
                        param("mode", entry.getKey()).
                when().
                        get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
                then().
                        log().ifValidationFails().
                        assertThat().contentType(entry.getValue());
            }
        }
    }

    @Test
    public void countCitiesInResponseJSON(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name.size()",equalTo(cnt));
    }

    @Test
    public void responseHasCityByCoordinatesJSON(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name", hasItem(cityName));
    }

    //This test fails all the time because if lat/lon is incorrect - system insert 0
    @Test
    public void checkBodyMessageWhenLonIncorrect(){
        given().
                param("lat", lat).
                param("lon", randomString()).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void countCitiesInResponseXML(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cities.list.item.city.@name.size()",equalTo(cnt));
    }

    @Test
    public void responseHasCityByCoordinatesXML(){
        cnt+=1; //to get range 2-12 (for getting Collection, not String)

        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cities.list.item.city.@name", hasItem(cityName));
    }

    @Test
    public void checkWeatherIsNotEmptyInJSON(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("list.main.temp", not(empty())).and().
                body("list.weather.description", not(empty()));
    }

    @Test
    public void checkWeatherIsNotEmptyInXML(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("cities.list.item.temperature.@value", not(empty())).and().
                body("cities.list.item.clouds.@value", not(empty()));
    }
}
