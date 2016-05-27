package com.openweathermap.tests.forecast5;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.getCityIdFromMap;
import static com.openweathermap.Common.getCountryCodeFromMap;
import static jdk.nashorn.internal.objects.NativeMath.round;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.IsNot.not;

//http://openweathermap.org/forecast5#cityid5
public class Forecast5Suite {
    public final String endpointURL = BASE_API_URL+"/forecast?appid="+API_KEY+"&";

    private HashMap<String, Object> city;
    private String cityName;
    private String countyCode;
    private Integer cityId;
    private Float lat;
    private Float lon;

    private ArrayList<RequestSpecification> params = new ArrayList<RequestSpecification>();

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {

        city = getRandomCity();
        cityName = getCityNameFromMap(city);
        countyCode = getCountryCodeFromMap(city);
        cityId = getCityIdFromMap(city);
        lat = getLatFromMap(city, 5);
        lon = getLonFromMap(city, 5);

        params.add(new RequestSpecBuilder().addParam("q", cityName).setPort(DEFAULT_PORT).build());
        params.add(new RequestSpecBuilder().addParam("id", cityId).setPort(DEFAULT_PORT).build());
        params.add(new RequestSpecBuilder().addParam("lat", lat).setPort(DEFAULT_PORT).addParam("lon", lon).build());
    }

    @After
    public void tearDown() {
        cityName = null;
        countyCode = null;
        cityId = null;
        lat = null;
        lon = null;
        params.clear();
    }

    @Test
    public void status200WhenIdCorrectByCityId(){
        given().
                log().all().
                param("id", cityId).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenNameCorrectByCityName(){
        given().
                log().all().
                param("q", cityName).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenNameCorrectByLatLon(){
        given().
                log().all().
                param("lat", lat).
                param("lon", lon).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    //Test fails all the time when verifying HTML content type. Documentation sad that HTML supported.
    @Test
    public void checkResponseContentTypesByCityId(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    log().all().
                    param("id", cityId).
                    param("mode", entry.getKey()).
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());

            drawSeparator();
        }
    }

    @Test
    public void checkResponseContentTypesByLatLon(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    log().all().
                    param("lat", lat).
                    param("lon", lon).
                    param("mode", entry.getKey()).
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());

            drawSeparator();
        }
    }

    @Test
    public void checkResponseContentTypesByCityName(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    log().all().
                    param("q", cityName).
                    param("mode", entry.getKey()).
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());

            drawSeparator();
        }
    }

    @Test
    public void status404WhenIdIncorrectByCityId(){
        given().
                log().all().
                param("id", randomString()).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenNameIncorrectByCityName(){
        given().
                log().all().
                param("q", randomString()).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenLatIncorrectByLatLon(){
        given().
                log().all().
                param("lat", randomString()).
                param("lon", lon).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void checkBodyMessageWhenIdIncorrectByCityId(){
        given().
                log().all().
                param("id", randomString()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkBodyMessageWhenNameIncorrectByCityName(){
        given().
                log().all().
                param("q", randomString()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkBodyMessageWhenLatIncorrectByLatLon(){
        given().
                log().all().
                param("lat", randomString()).
                param("lon", lon).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkCityParamsInResponseJSON(){
        //Verify that response identical by all possibles request params
        for (RequestSpecification paramFromList: params) {
            given().log().all().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("city.id", equalTo(cityId)).and().
                    body("city.name", equalTo(cityName)).and().
                    body("city.country", equalTo(countyCode)).and().
                    body("city.coord.lat", equalTo(lat)).and().
                    body("city.coord.lon", equalTo(lon));

            drawSeparator();
        }
    }

    @Test
    public void checkCityParamsInResponseXML(){
        Double RawLat = (Double) city.get("lat");       //I need to get raw double values for this test
        Double RawLon = (Double) city.get("lon");

        //Verify that response identical by all possibles request params
        for (RequestSpecification paramFromList: params) {
            given().
                    log().all().
                    spec(paramFromList).
                    param("mode", "xml").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("weatherdata.location.name", equalTo(cityName)).and().
                    body("weatherdata.location.country", equalTo(countyCode)).and().
                    body("weatherdata.location.location.@latitude", equalTo(RawLat.toString())).and().
                    body("weatherdata.location.location.@longitude", equalTo(RawLon.toString()));

            drawSeparator();
        }
    }

    @Test
    public void countElementsInListOfWeatherJSON(){
        for (RequestSpecification paramFromList: params) {
            given().
                    log().all().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("list.size()", is(both(greaterThan(31)).and(lessThan(41)))); /*5 day with 3 every hours range
                so it's probably "24 / 3 * 5 = 40" updates. But i'm not sure that my calculations are correct :) */

            drawSeparator();
        }
    }

    @Test
    public void countElementsInListOfWeatherXML(){
        for (RequestSpecification paramFromList: params) {
            given().
                    log().all().
                    spec(paramFromList).
                    param("mode", "xml").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("weatherdata.forecast.time.size()", is(both(greaterThan(31)).and(lessThan(41))));
                /*5 day with 3 every hours range so it's probably "24 / 3 * 5 = 40" updates.
                                                            But i'm not sure that my calculations are correct :) */
            drawSeparator();
        }
    }

    @Test
    public void checkFirstWeatherNotEmptyJSON(){
        for (RequestSpecification paramFromList: params) {
            given().
                    log().all().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("list.weather.main[0]", not(empty()));

            drawSeparator();
        }
    }

    @Test
    public void checkFirstWeatherNotEmptyXML(){
        for (RequestSpecification paramFromList: params) {
            given().
                    log().all().
                    param("id", cityId).
                    param("mode", "xml").
                    when().
                    get(endpointURL).
                    then().
                    log().ifValidationFails().
                    assertThat().body("weatherdata.forecast.time.clouds[0].@value", not(empty()));

            drawSeparator();
        }
    }
}
