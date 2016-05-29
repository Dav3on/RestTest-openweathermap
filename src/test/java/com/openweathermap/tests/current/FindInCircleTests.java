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
import static com.openweathermap.Common.getLatFromMap;
import static com.openweathermap.Common.getLonFromMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

//http://openweathermap.org/current#cycle
public class FindInCircleTests {
    public final String endpointURL = BASE_API_URL+"/find?appid="+API_KEY+"&";

    private Float lat;
    private Float lon;
    private String cityName;
    private Integer cnt;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        lat = getLatFromMap(city, 2);
        lon = getLonFromMap(city, 2);
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
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status400WhenCountIncorrect(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", 0).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(400);
    }

    /*Service doesn't support HTML for this method response, but there isn't any mention about that in documentation
    http://openweathermap.org/current#format */
    @Test
    public void checkResponseContentTypes(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
                given().log().ifValidationFails().
                        param("lat", lat).
                        param("lon", lon).
                        param("cnt", cnt).
                        param("mode", entry.getKey()).
                when().
                        get(endpointURL).
                then().
                        log().ifValidationFails().
                        assertThat().contentType(entry.getValue());
        }
    }

    @Test
    public void checkBodyMessageWhenLonIncorrect(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", randomString()).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void countCitiesInResponseJSON(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name.size()",equalTo(cnt));
    }

    @Test
    public void responseHasCityByCoordinatesJSON(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name", hasItem(cityName));
    }


    @Test
    public void countCitiesInResponseXML(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cities.list.item.city.@name.size()",equalTo(cnt));
    }

    @Test
    public void responseHasCityByCoordinatesXML(){
        cnt+=1; //to get range 2-12 (for getting Collection, not String)

        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cities.list.item.city.@name", hasItem(cityName));
    }

    @Test
    public void checkWeatherIsNotEmptyInJSON(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.main.temp", not(empty())).and().
                body("list.weather.description", not(empty()));
    }

    @Test
    public void checkWeatherIsNotEmptyInXML(){
        given().log().ifValidationFails().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cities.list.item.temperature.@value", not(empty())).and().
                body("cities.list.item.clouds.@value", not(empty()));
    }
}
