import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestJunit {

    @BeforeAll
    public static void beforeAll() {
        System.out.println("before all");
    }

    @Test
    public void test01() {
        Calculator calc = new Calculator();

        int sum = calc.sum(4, 5);
        Assertions.assertEquals(9, sum);
    }

    @Test
    public void test02() {

    }

}
