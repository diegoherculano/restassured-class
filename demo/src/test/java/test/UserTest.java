package test;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;

import entity.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "https://serverest.dev";
    }

    @Test
    public void createUserWithSuccess() {
        User reqUser = generatorUser();

        Response resp = RestAssured
                .given().contentType(ContentType.JSON)
                .when()
                .body(reqUser)
                .post("/usuarios")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract().response();

        JsonPath path = resp.jsonPath();
        String message = path.get("message");
        Assertions.assertEquals("Cadastro realizado com sucesso", message);
    }

    public User generatorUser() {
        Faker faker = new Faker();
        User dummyUser = new User();
        dummyUser.setNome(faker.name().fullName());
        dummyUser.setEmail(faker.internet().emailAddress());
        dummyUser.setPassword("teste@123");
        dummyUser.setAdministrador("true");

        return dummyUser;
    }
}

interface ICreateUser {
    String message();

    String _id();
}