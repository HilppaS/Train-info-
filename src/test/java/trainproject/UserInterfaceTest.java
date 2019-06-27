package trainproject;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UserInterfaceTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }

    @Test
    public void userInputTest () {
        String input = "5";
        System.err.println(String.format("Unknown choice, please type again: '%s'", input));
    }

}

