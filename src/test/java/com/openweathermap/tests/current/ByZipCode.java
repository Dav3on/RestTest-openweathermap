package com.openweathermap.tests.current;

//http://openweathermap.org/current#zip

import com.jayway.restassured.http.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.XML;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.randomString;
import static org.hamcrest.CoreMatchers.equalTo;

//The service isn't provide the list of postal codes so i get it from http://www.geopostcodes.com/Ukraine
public class ByZipCode {
    public final String endpointURL = BASE_API_URL+"/weather?appid="+API_KEY+"&";

    private String countyCode;
    private Integer zipCode;
    private String cityName;
    private Integer cityId;
    String zipWithCountyCode;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        countyCode = getCountryCodeFromMap(city);
        zipCode = getZipCodeFromMap(city);
        zipWithCountyCode = zipCode+","+countyCode;
        cityName = getCityNameFromMap(city);
        cityId = getCityIdFromMap(city);
    }

    @After
    public void tearDown() {
        countyCode = null;
        zipCode = null;
        zipWithCountyCode = null;
        cityName = null;
        cityId = null;
    }

    @Test
    public void status200WhenCityIdCorrect(){
        given().
                param("zip", zipWithCountyCode).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void checkResponseContentTypes(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    param("zip", zipWithCountyCode).
                    param("mode", entry.getKey()).
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().contentType(entry.getValue());
        }
    }

    @Test
    public void status404WhenZipCodeIncorrect(){
        given().
                param("zip", randomString()).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void checkBodyMessageWhenZipCodeIncorrect(){
        given().
                param("zip", randomString()).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city")).and().
                contentType(XML);
    }

    @Test
    public void checkCityDataInJSON(){
        given().
                param("zip", randomString()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityId)).and().
                body("sys.country", equalTo(countyCode));
    }

    @Test
    public void checkCityDataInXML(){
        given().
                param("zip", randomString()).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("current.city.@name", equalTo(cityName)).and().
                body("current.city.@id", equalTo(cityId.toString())).and().
                body("current.city.country", equalTo(countyCode));
    }
}
