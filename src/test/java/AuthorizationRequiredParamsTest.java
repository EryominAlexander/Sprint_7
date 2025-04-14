import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class AuthorizationRequiredParamsTest {
    private static final String BASE_URI = "https://qa-scooter.praktikum-services.ru/";
    private static final String POST_API_V1_LOGIN = "/api/v1/courier/login";

    private final String login;
    private final String password;

    public AuthorizationRequiredParamsTest(String login, String password){
        this.login = login;
        this.password = password;
    }

    @Parameterized.Parameters
    public static Object[][] getData(){
        return new Object[][]{
                {"testLogin", ""},
                {"", "testPassword"}
        };
    }
    @Before
    public void set(){
        RestAssured.baseURI = BASE_URI;
    }

    @Test
    public void authorizationRequiredParamsTest(){
        CourierAuth courierAuth = new CourierAuth(login, password);

        given()
                .header("Content-Type", "application/json")
                .and()
                .body(courierAuth)
                .when()
                .post(POST_API_V1_LOGIN)
                .then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and().statusCode(400);
    }
}
