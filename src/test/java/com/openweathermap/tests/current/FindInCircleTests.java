package com.openweathermap.tests.current;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.openweathermap.City;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
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

    @BeforeClass
    public static void setUpBeforeClass(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    //You can move to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        City city = new City();
        lat = city.getLat(2);
        lon = city.getLon(2);
        cityName = city.getCityName();
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
                get(endpointURL).
        then().
                assertThat().statusCode(200);
    }

    @Test
    public void status400WhenCountIncorrect(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", 0).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(400);
    }

    /*Service doesn't support HTML for this method response, but there isn't any mention about that in documentation
    http://openweathermap.org/current#format */
    @Test
    public void checkResponseContentTypes(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
                given().
                        param("lat", lat).
                        param("lon", lon).
                        param("cnt", cnt).
                        param("mode", entry.getKey()).
                when().
                        get(endpointURL).
                then().
                        assertThat().contentType(entry.getValue());
        }
    }

    @Test
    public void checkBodyMessageWhenLonIncorrect(){
        given().
                param("lat", lat).
                param("lon", randomString()).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void countCitiesInResponseJSON(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
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
                get(endpointURL).
        then().
                assertThat().body("list.name", hasItem(cityName));
    }


    @Test
    public void countCitiesInResponseXML(){
        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
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
                get(endpointURL).
        then().
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
                get(endpointURL).
        then().
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
                get(endpointURL).
        then().
                assertThat().body("cities.list.item.temperature.@value", not(empty())).and().
                body("cities.list.item.clouds.@value", not(empty()));
    }
}
