package com.openweathermap.tests.current;

import com.jayway.restassured.http.ContentType;
import com.openweathermap.City;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

//http://openweathermap.org/current#rectangle
public class BBoxCityTests {
    public final String endpointURL = BASE_API_URL+"/box/city?appid="+API_KEY+"&";

    private String cityName;
    private Float lat;
    private Float lon;
    private String bbox;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        City city = new City();
        cityName = city.getCityName();
        lat = city.getLat(2);
        lon = city.getLon(2);
        bbox = (lon-0.5f)+","+(lat-0.5f)+","+(lon+0.5f)+","+(lat+0.5f); //bounding box that include current city
    }

    @After
    public void tearDown() {
        cityName = null;
        lat = null;
        lon = null;
        bbox = null;
    }

    @Test
    public void status200WhenRectangleCorrect(){
        given().log().ifValidationFails().
                param("bbox", bbox).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void checkResponseContentTypeIsJSON(){
            given().log().ifValidationFails().
                    param("bbox", bbox).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(ContentType.JSON);
    }

    //Unfortunately it works fine only for Kiev.
    @Test
    public void checkCityInResponse(){
        given().log().ifValidationFails().
                param("bbox", bbox).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.name", hasItem(cityName));
    }

    //Unfortunately it works fine only for Kiev.
    @Test
    public void checkWeatherIsNotEmptyInJSON(){
        given().log().ifValidationFails().
                param("bbox", bbox).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.main.temp", not(empty())).and().
                body("list.weather.description", not(empty()));
    }
}
