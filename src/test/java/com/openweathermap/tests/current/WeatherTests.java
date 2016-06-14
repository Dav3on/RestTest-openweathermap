package com.openweathermap.tests.current;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.specification.RequestSpecification;
import com.openweathermap.City;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
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

    @BeforeClass
    public static void setUpBeforeClass(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    //You can move to @BeforeClass if needed.
    @Before
    public void setUp()
    {
        City city = new City();
        cityName = city.getCityName();
        countyCode = city.getCountryCode();
        cityId = city.getCityId();
        zipCode = city.getZipCode();
        lat = city.getLat(2);
        lon = city.getLon(2);

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
    }

    /* _______________________________________________________________
                    Metadata tests
    __________________________________________________________________ */

    @Test
    public void status200WhenIdCorrectByCityId(){
        given().
                param("id", cityId).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenNameCorrectByCityName(){
        given().
                param("q", cityName).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenNameWithCountyCodeCorrectByCityName(){
        given().
                param("q", cityName+","+countyCode).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenLatLonCorrectByLatLon(){
        given().
                param("lat", lat).
                param("lon", lon).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(200);
    }

    @Test
    public void status200WhenZipCorrectByZip(){
        given().
                param("zip", zipCode+","+countyCode).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(200);
    }

    @Test
    public void status401WhenUnauthorized(){
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
            when().
                    get(BASE_API_URL+"/forecast").
            then().
                    assertThat().statusCode(401);
        }
    }

    @Test
    public void checkResponseContentTypesById(){
        //Verify all possible content types even with default (watch CONTENT_TYPES)
        for (Map.Entry<String, ContentType> entry: CONTENT_TYPES.entrySet()){
            given().
                    param("id", cityId).
                    param("mode", entry.getKey()).
            when().
                    get(endpointURL).
            then().
                    assertThat().contentType(entry.getValue());
        }
    }

    @Test
    public void status404WhenIdIncorrectByCityId(){
          given().
                param("id", randomString()).
          when().
                get(endpointURL).
          then().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenNameIncorrectByCityName(){
        given().
                param("q", randomString()+","+countyCode).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenLatIncorrectByLatLon(){
        given().
                param("lat", randomString()).
                param("lon", randomString()).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(404);
    }

    @Test
    public void status404WhenZipIncorrectByZip(){
        given().
                param("zip", randomString()+","+countyCode).
        when().
                get(endpointURL).
        then().
                assertThat().statusCode(404);
    }

    /* _______________________________________________________________
                        Error messages tests
    __________________________________________________________________ */

    //Im not sure about message. While i writing this test they switch it twice from json to html and back
    @Test
    public void checkBodyMessageWhenUnauthorized(){
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
            when().
                    get(BASE_API_URL+"/weather").
            then().
                    assertThat().body("cod", equalTo(401)).and().
                    body("message", equalTo("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info."));
        }
    }

    @Test
    public void checkBodyMessageWhenIdIncorrectByCityId(){
        given().
                param("id", randomString()).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    @Test
    public void checkBodyMessageWhenNameIncorrectByCityName(){
        given().
                param("q", randomString()+","+countyCode).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }
    @Test
    public void checkBodyMessageWhenLatIncorrectByLatLon(){
        given().
                param("lat", randomString()).
                param("lon", lon).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }
    @Test
    public void checkBodyMessageWhenZipIncorrectByZip(){
        given().
                param("zip", randomString()+","+countyCode).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().body("cod", equalTo("404")).and().
                body("message", equalTo("Error: Not found city"));
    }

    /* _______________________________________________________________
                        Payload tests
    __________________________________________________________________ */

    //By same coordinates sometimes they return "Kiev" or "Misto Kyyiv"
    @Test
    public void checkCityParamsInJsonResponse(){
        //Verify that response identical by all possibles request params
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    assertThat().body("name", equalTo(cityName)).and().
                    body("id", equalTo(cityId)).and().
                    body("sys.country", equalTo(countyCode)).and().
                    body("coord.lat", equalTo(lat)).and().
                    body("coord.lon", equalTo(lon));
        }
    }

    @Test
    public void checkCityNameInHtmlResponse(){
        //Verify that response identical by all possibles request params
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
                    param("mode", "html").
            when().
                    get(endpointURL).
            then().
                    assertThat().body("html.body.div", hasItem(cityName));
        }
    }

    @Test
    public void checkCityParamsInJsonResponseByZip(){
        given().
                param("zip", zipCode+","+countyCode).
                param("mode", "json").
        when().
                get(endpointURL).
        then().
                assertThat().body("name", equalTo(cityName)).and().
                body("id", equalTo(cityId)).and().
                body("sys.country", equalTo(countyCode));
    }

    @Test
    public void checkCityParamsInXmlResponse(){
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
                    param("mode", "xml").
            when().
                    get(endpointURL).
            then().
                    assertThat().body("current.city.@name", equalTo(cityName)).and().
                    body("current.city.@id", equalTo(cityId.toString())).and().
                    body("current.city.country", equalTo(countyCode)).and().
                    body("current.city.coord.@lat", equalTo(lat.toString())).and().
                    body("current.city.coord.@lon", equalTo(lon.toString()));
        }
    }

    @Test
    public void checkCityParamsInXmlResponseByZip(){
        given().
                param("zip", zipCode+","+countyCode).
                param("mode", "xml").
        when().
                get(endpointURL).
        then().
                assertThat().body("current.city.@name", equalTo(cityName)).and().
                body("current.city.@id", equalTo(cityId.toString())).and().
                body("current.city.country", equalTo(countyCode));
    }

    @Test
    public void checkWeatherIsNotEmptyInJsonResponse(){
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
                    param("mode", "json").
            when().
                    get(endpointURL).
            then().
                    assertThat().body("main.temp", not(empty())).and().
                    body("weather.description", not(empty()));
        }
    }

    @Test
    public void checkWeatherIsNotEmptyInXmlResponse(){
        for (RequestSpecification paramFromList: requestParams) {
            given().
                    spec(paramFromList).
                    param("mode", "xml").
            when().
                    get(endpointURL).
            then().
                    assertThat().body("current.temperature.@value ", not(empty())).and().
                    body("current.clouds.@value ", not(empty()));
        }
    }
}
