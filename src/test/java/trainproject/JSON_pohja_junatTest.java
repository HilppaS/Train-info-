package trainproject;

import org.junit.Test;

import static org.junit.Assert.*;

public class JSON_pohja_junatTest {

    @Test
    public void getInfoByTrainNrTest(){

    }
    @Test
    public void getStopStationsOfCertainTrainNrTest(){

    }
    @Test
    public void ListInfoOfCertainTrainTest(){
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        String comparable = "https://rata.digitraffic.fi/api/v1";
        assertEquals(baseurl, comparable);

    }

    @Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }
}