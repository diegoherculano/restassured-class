package test;

import java.io.File;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.github.javafaker.Faker;
import com.google.gson.Gson;

import entity.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import response.CreateUser;

public class UserTest {
    private Gson gson = new Gson();

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:3000";
    }

    @Test
    public void createUserWithSuccess() {
        User reqUser = generatorUser();
        File schemaFile = new File("src/test/resources/schemas/createUser.json");

        Response resp = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .body(reqUser)
                .post("/usuarios")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(schemaFile))
                .and().extract().response();

        CreateUser body = gson.fromJson(resp.asString(), CreateUser.class);
        Assertions.assertEquals("Cadastro realizado com sucesso", body.getMessage());
    }

    @Test
    public void getUsersWithSuccess() {
        File schemaFile = new File("src/test/resources/schemas/getUsers.json");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/usuarios")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
    }

    @Test
    public void getUserByIdWithSuccess() {
        JsonPath createUser = createUser();
        File schemaFile = new File("src/test/resources/schemas/getUser.json");
        String userId = createUser.getJsonObject("_id");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/usuarios/" + userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
    }

    @Test
    public void getUserByIdNonexistentWithSuccess() {
        String userId = "XXXX";

        JsonPath response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/usuarios/" + userId)
                .then()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .log().all()
                .and().extract().response().jsonPath();

        Assertions.assertEquals(response.getJsonObject("message"), "Usuário não encontrado");
    }

    @Test
    public void deleteUserByIdWithSuccess() {
        JsonPath createUser = createUser();
        String userId = createUser.getJsonObject("_id");

        JsonPath response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/usuarios/" + userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .and().extract().response().jsonPath();

        Assertions.assertEquals(response.getJsonObject("message"), "Registro excluído com sucesso");
    }

    @Test
    public void getUserById() {
        JsonPath createUser = createUser();
        File schemaFile = new File("src/test/resources/schemas/getUser.json");
        String userId = createUser.getJsonObject("_id");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get("/usuarios/" + userId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log().all()
                .and().body(JsonSchemaValidator.matchesJsonSchema(schemaFile));
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

    public JsonPath createUser() {
        User reqUser = generatorUser();

        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .body(reqUser)
                .post("/usuarios")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .log().all()
                .and().extract().response().jsonPath();
    }
}