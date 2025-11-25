package iteration1;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest(){
        //собирает результаты всех ассертов в тесте
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest(){
        //делает, чтобы тест упал, если какой-то из ассертов не выполнился
        softly.assertAll();
    }
}
