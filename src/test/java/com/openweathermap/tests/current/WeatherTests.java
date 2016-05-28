package com.openweathermap.tests.current;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.openweathermap.Common.*;
import static com.openweathermap.Common.randomString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

//http://openweathermap.org/current
public class WeatherTests {
    public final String endpointURL = BASE_API_URL+"/weather?appid="+API_KEY+"&";

    private ArrayList<RequestSpecification> requestParams = new ArrayList<RequestSpecification>();

    private String cityName;
    private String countyCode;
    private Integer cityId;
    private Integer zipCode;
    private Float lat;
    private Float lon;

    //You can change to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        HashMap<String, Object> city = getRandomCity();
        cityName = getCityNameFromMap(city);
        countyCode = getCountryCodeFromMap(city);
        cityId = getCityIdFromMap(city);
        zipCode = getZipCodeFromMap(city);
        lat = getLatFromMap(city, 2);
        lon = getLonFromMap(city, 2);

        requestParams.add(new RequestSpecBuilder().addParam("id", cityId).setPort(DEFAULT_PORT).build());
        requestParams.add(new RequestSpecBuilder().addParam("q", cityName).setPort(DEFAULT_PORT).build());
        requestParams.add(new RequestSpecBuilder().addParam("q", cityName+","+countyCode).setPort(DEFAULT_PORT).build());
        requestParams.add(new RequestSpecBuilder().addParam("lat", lat).setPort(DEFAULT_PORT).addParam("lon", lon).build());
    }

    @After
    public void tearDown() {
        cityName = null;
        countyCode = null;
        cityId = null;
        lat = null;
        lon = null;
        zipCode = null;
        requestParams.clear();
        drawSeparator();
    }

    /* _______________________________________________________________
                    Metadata tests
    __________________________________________________________________ */

    @Test
    public void status200WhenIdCorrectByCityId(){
        given().log().all().
                param("id", cityId).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenNameCorrectByCityName(){
        given().log().all().
                param("q", cityName).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenNameWithCountyCodeCorrectByCityName(){
        given().log().all().
                param("q", cityName+","+countyCode).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenLatLonCorrectByLatLon(){
        given().log().all().
                param("lat", lat).
                param("lon", lon).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenZipCorrectByZip(){
        given().log().all().
                param("zip", zipCode+","+countyCode).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(200);
    }

    @Test
    public void status401WhenUnauthorized(){
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
            when().
                    get(BASE_API_URL+"/forecast").
            then().
                    log().ifValidationFails().
                    assertThat().statusCode(401);

            drawSeparator();
        }
    }

    @Test
    public void checkResponseContentTypesById(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().log().all().
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
    public void status404WhenIdIncorrectByCityId(){
          given().log().all().
                param("id", randomString()).
          when().
                get(endpointURL).
          then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenNameIncorrectByCityName(){
        given().log().all().
                param("q", randomString()+","+countyCode).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenLatIncorrectByLatLon(){
        given().log().all().
                param("lat", randomString()).
                param("lon", randomString()).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenZipIncorrectByZip(){
        given().log().all().
                param("zip", randomString()+","+countyCode).
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().statusCode(404);
    }

    /* _______________________________________________________________
                        Error messages tests
    __________________________________________________________________ */


    @Test
    public void checkBodyMessageWhenUnauthorized(){
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
            when().
                    get(BASE_API_URL+"/weather").
            then().
                    log().ifValidationFails().
                    assertThat().body("cod", equalTo(401)).and().
                    body("message", equalTo("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info."));

            drawSeparator();
        }
    }

    @Test
    public void checkBodyMessageWhenIdIncorrectByCityId(){
        given().log().all().
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
        given().log().all().
                param("q", randomString()+","+countyCode).
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
        given().log().all().
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
    public void checkBodyMessageWhenZipIncorrectByZip(){
        given().log().all().
                param("zip", randomString()+","+countyCode).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    /* _______________________________________________________________
                        Payload tests
    __________________________________________________________________ */

    @Test
    public void checkCityParamsInJsonResponse(){
        //Verify that response identical by all possibles request params
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("name", equalTo(cityName)).and().
                    body("id", equalTo(cityId)).and().
                    body("sys.country", equalTo(countyCode)).and().
                    body("coord.lat", equalTo(lat)).and().
                    body("coord.lon", equalTo(lon));

            drawSeparator();
        }
    }

    @Test
    public void checkCityNameInHtmlResponse(){
        //Verify that response identical by all possibles request params
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
                    param("mode", "html").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("html.body.div", hasItem(cityName));

            drawSeparator();
        }
    }

    @Test
    public void checkCityParamsInJsonResponseByZip(){
        given().log().all().
                param("zip", zipCode+","+countyCode).
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
    public void checkCityParamsInXmlResponse(){
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
                    param("mode", "xml").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("current.city.@name", equalTo(cityName)).and().
                    body("current.city.@id", equalTo(cityId.toString())).and().
                    body("current.city.country", equalTo(countyCode)).and().
                    body("current.city.coord.@lat", equalTo(lat.toString())).and().
                    body("current.city.coord.@lon", equalTo(lon.toString()));
        }
    }

    @Test
    public void checkCityParamsInXmlResponseByZip(){
        given().log().all().
                param("zip", zipCode+","+countyCode).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                log().ifValidationFails().
                assertThat().body("current.city.@name", equalTo(cityName)).and().
                body("current.city.@id", equalTo(cityId.toString())).and().
                body("current.city.country", equalTo(countyCode));
    }

    @Test
    public void checkWeatherIsNotEmptyInJsonResponse(){
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("main.temp", not(empty())).and().
                    body("weather.description", not(empty()));

            drawSeparator();
        }
    }

    @Test
    public void checkWeatherIsNotEmptyInXmlResponse(){
        for (RequestSpecification paramFromList: requestParams) {
            given().log().all().
                    spec(paramFromList).
                    param("mode", "xml").
            when().
                    get(endpointURL).
            then().
                    log().ifValidationFails().
                    assertThat().body("current.temperature.@value ", not(empty())).and().
                    body("current.clouds.@value ", not(empty()));

            drawSeparator();
        }
    }
}