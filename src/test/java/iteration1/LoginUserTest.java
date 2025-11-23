package iteration1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import models.LoginUserRequest;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import requests.AdminLoginUserRequest;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.requestSpecification;

public class LoginUserTest {
    @BeforeAll
    public static void setupRestAssured(){
        RestAssured.filters(
                List.of(new ResponseLoggingFilter(),
                        new RequestLoggingFilter())
        );

    }
    @Test
    public void adminCanGenerateAuthUserTest() {
        LoginUserRequest loginUserRequest = LoginUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        new AdminLoginUserRequest(
                RequestSpecs.unauthSpec(),
                ResponseSpecs.requestReturnsOK())
                .post(loginUserRequest);

    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        // создание пользователя
        given()
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "kate2000",
                          "password": "Kate2000#",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "kate2000",
                          "password": "Kate2000#"
                        }
                        """)
                .post("http://localhost:4111/api/v1/auth/login")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .header("Authorization", Matchers.notNullValue());
    }
}
