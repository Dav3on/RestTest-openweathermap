package com.openweathermap.tests;

import com.openweathermap.tests.current.BBoxCityTests;
import com.openweathermap.tests.current.FindInCircleTests;
import com.openweathermap.tests.current.GroupByIdTests;
import com.openweathermap.tests.current.WeatherTests;
import com.openweathermap.tests.forecast5.ForecastTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BBoxCityTests.class,
        FindInCircleTests.class,
        GroupByIdTests.class,
        WeatherTests.class,
        ForecastTests.class

})

//Run this to start tests
public class TestSuite {

}
