package com.openweathermap.tests.current;

import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.getLatFromMap;
import static com.openweathermap.Common.getLonFromMap;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

//http://openweathermap.org/current#rectangle
public class CitiesWithinRectangleZone {
    public final String endpointURL = BASE_API_URL+"/box/city?appid="+API_KEY+"&";

    private String cityName;
    private Float lat;
    private Float lon;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        cityName = getCityNameFromMap(city);
        lat = getLatFromMap(city);
        lon = getLonFromMap(city);
    }

    @After
    public void tearDown() {
        cityName = null;
        lat = null;
        lon = null;
    }

    public String formCurrentCityBoundingBox(){
        return (this.lon-0.5)+","+(this.lat-0.5)+","+(this.lon+0.5)+","+(this.lat+0.5); //bounding box [lon-left,lat-bottom,lon-right,lat-top]
    }

    @Test
    public void status200WhenRectangleCorrect(){
        given().
                param("bbox", formCurrentCityBoundingBox()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void checkResponseContentTypeIsJSON(){
            given().
                    param("bbox", formCurrentCityBoundingBox()).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(ContentType.JSON);
    }

    //Unfortunately it works fine just for Kiev.
    @Test
    public void checkCityInResponse(){
        given().
                param("bbox", formCurrentCityBoundingBox()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name", hasItem(cityName));
    }

    @Test
    public void checkWeatherIsNotEmptyInJSON(){
        given().
                param("bbox", formCurrentCityBoundingBox()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.main.temp", not(empty())).and().
                body("list.weather.description", not(empty()));
    }
}
