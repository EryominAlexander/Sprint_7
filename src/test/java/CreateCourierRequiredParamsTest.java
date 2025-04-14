import io.restassured.RestAssured;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class CreateCourierRequiredParamsTest {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String POST_API_V1_COURIER = "/api/v1/courier";
    private final String courierLogin;
    private final String courierPassword;
    private final String courierFirstName;

    public CreateCourierRequiredParamsTest(String courierLogin, String courierPassword, String courierFirstName ){
        this.courierLogin = courierLogin;
        this.courierPassword = courierPassword;
        this.courierFirstName = courierFirstName;
    }
    @Parameterized.Parameters
    public static Object[][] getData(){
        return new Object[][]{
            {"", "TestPassword", "TestFirstName"},
            {"TestLogin" + RandomStringUtils.randomAlphabetic(10), "", "TestFirstName"}
        };
    }

    @Before
    public void setUp(){
        RestAssured.baseURI = BASE_URI;
    }
    @Test
    public void createCourier(){

        CourierData courier = new CourierData(courierLogin, courierPassword, courierFirstName);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(POST_API_V1_COURIER)
                .then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and().statusCode(400);


    }
}
