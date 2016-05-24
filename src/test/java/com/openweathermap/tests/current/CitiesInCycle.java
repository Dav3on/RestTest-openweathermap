package com.openweathermap.tests.current;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.getLatNameFromMap;
import static com.openweathermap.Common.getLonNameFromMap;
import static org.hamcrest.CoreMatchers.equalTo;

//http://openweathermap.org/current#cycle
public class CitiesInCycle {
    private Float lat;
    private Float lon;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        lat = getLatNameFromMap(city);
        lon = getLonNameFromMap(city);
    }

    @After
    public void tearDown() {
        lat = null;
        lon = null;
    }

    @Test
    public void countCitiesInResponse(){
        int cnt = 1;

        given().
                param("lat", lat).
                param("lon", lon).
                param("cnt", cnt).
        when().
                get(CURRENT_WEATHER_FOR_SEVERAL_CITIES_URL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name.size()",equalTo(1));
    }
}
