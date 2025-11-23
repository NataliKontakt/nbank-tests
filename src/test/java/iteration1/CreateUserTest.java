package iteration1;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

public class CreateUserTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.filters(
                List.of(new ResponseLoggingFilter(),
                        new RequestLoggingFilter())
        );

    }

    @Test
    public void adminCanCreateUserWithCorrectData() {

        // создание пользователя
        given()
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("""
                        {
                          "username": "kate20003",
                          "password": "Kate2000#P",
                          "role": "USER"
                        }
                        """)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_CREATED)
                .body("username", Matchers.equalTo("kate20003"))
                .body("password", Matchers.not(Matchers.equalTo("Kate2000#P")))
                .body("role", Matchers.equalTo("USER"));
    }

    public static Stream<Arguments> userInvalidData() {
        //username field validation
        return Stream.of(
                Arguments.of(" ", "Password23#", "USER", "username",
                        List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots",
                        "Username must be between 3 and 15 characters",
                        "Username cannot be blank"
                )),
                Arguments.of("ad", "Password23#", "USER", "username", List.of(
                        "Username must be between 3 and 15 characters"
                )),
                Arguments.of("ad1!", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1@", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1$", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1%", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1^", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1&", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1*", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1(", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1)", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1=", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                )),
                Arguments.of("ad1+", "Password23#", "USER", "username", List.of(
                        "Username must contain only letters, digits, dashes, underscores, and dots"
                ))

        );
    }

    @MethodSource("userInvalidData")
    @ParameterizedTest
    public void adminCanNotCreateUserWithInvalidData(String username, String password, String role, String errorKey, List<String>  errorValue) {
        String requestBody = String.format("""
                {
                  "username": "%s",
                   "password": "%s",
                   "role": "%s"
                }
                """, username, password, role);
        // создание пользователя
        given()
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(requestBody)
                .post("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .body(errorKey, Matchers.containsInAnyOrder(errorValue.toArray()));
    }
}
