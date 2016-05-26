package com.openweathermap.tests.forecast5;

import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.getCityIdFromMap;
import static com.openweathermap.Common.getCountryCodeFromMap;
import static org.apache.commons.lang3.Range.between;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.CombinableMatcher.both;
import static org.hamcrest.core.IsNot.not;

//http://openweathermap.org/forecast5#cityid5
public class ByCityId {
    public final String endpointURL = BASE_API_URL+"/forecast?appid="+API_KEY+"&";

    private String cityName;
    private String countyCode;
    private Integer cityId;

    //You can change to @BeforeClass if needed.
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
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    //Test fails all the time when verifying HTML content type. Documentation sad that HTML supported.
    @Test
    public void checkResponseContentTypes(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    param("id", cityId).
                    param("mode", entry.getKey()).
            when().
                    get(endpointURL).
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
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void checkBodyMessageWhenCityIdIncorrect(){
        given().
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
    public void checkCityInResponseJSON(){
        given().
                param("id", cityId).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("city.id", equalTo(cityId)).and().
                body("city.name", equalTo(cityName)).and().
                body("city.country", equalTo(countyCode));
    }

    @Test
    public void checkCityInResponseXML(){
        given().
                param("id", cityId).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("weatherdata.location.name", equalTo(cityName)).and().
                body("weatherdata.location.country", equalTo(countyCode));
    }

    @Test
    public void countElementsInListOfWeatherJSON(){
        given().
                param("id", cityId).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.size()", is(both(greaterThan(31)).and(lessThan(41)))); /*5 day with 3 every hours range
                so it's probably "24 / 3 * 5 = 40" updates. But i'm not sure that my calculations are correct :) */
    }

    @Test
    public void countElementsInListOfWeatherXML(){
        given().
                param("id", cityId).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("weatherdata.forecast.time.size()", is(both(greaterThan(31)).and(lessThan(41))));
                /*5 day with 3 every hours range so it's probably "24 / 3 * 5 = 40" updates.
                                                            But i'm not sure that my calculations are correct :) */
    }

    @Test
    public void checkFirstWeatherNotEmptyJSON(){
        given().
                param("id", cityId).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("list.weather.main[0]", not(empty()));
    }

    @Test
    public void checkFirstWeatherNotEmptyXML(){
        given().
                param("id", cityId).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("weatherdata.forecast.time.clouds[0].@value", not(empty()));
    }
}
