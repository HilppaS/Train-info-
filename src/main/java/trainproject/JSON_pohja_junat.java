package trainproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/*
Vaatii Jackson kirjaston:
File | Project Structure
Libraries >> Add >> Maven
Etsi "jackson-databind", valitse esimerkiksi versio 2.0.5
Asentuu Jacksonin databind, sekä core ja annotations
 */


public class JSON_pohja_junat {

    public static void main(String[] args) {
        //kahdenAsemanValillaLiikkeessaOlevatJunat();
        //  ListInfoOfCertainTrain("8");
    }

    public static void lueJunanJSONDataAsemaltaAasemalleB() {

        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä Lahteen)
            URL url = new URL(URI.create(String.format("%s/live-trains/station/hki/LH", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi

            int i = 0;
            // for (Juna j : junat) {
            System.out.println("Junan numero on " + junat.get(i).getTrainNumber());
            System.out.println("Junan lähtöpäivä on " + junat.get(i).getDepartureDate());
            System.out.println("TimetableRow ekalle junalle: " + junat.get(i).getTimeTableRows());
            System.out.println("Juna lähtee asemalta: " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
            System.out.println("");
            //   i++;
            // }
        } catch (Exception e) {
            System.out.println("");
        }
    }

    //SANNA:
    // Metodi:
    // -listaa lähtö- sekä saapumisasemat, sekä kellonajat
    // Kutsuu metodia getInfoByTrainNr, joka hakee syötteen perusteella junan tiedot URLin takaa ja Mappaa ne
    // Kutsuu metodia getStopStationsOfCertainTrainNr, joka hakee URLin takaa asemat, joilla syötetty juna pysähtyy, niille arvioidut saapumisajat sekä pysähdysten kestot eri asemilla minuuteissa

    public static void ListInfoOfCertainTrain(String trainnumber) {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            List<Juna> junat = getInfoByTrainNr(trainnumber, baseurl);
            int i = 0;
            List<TimeTableRow> kasiteltavajuna= junat.get(0).getTimeTableRows();
            Date junanAika = kasiteltavajuna.get(0).getScheduledTime();

            System.out.println("Train nr: " + junat.get(i).getTrainNumber());
            System.out.println("Departure station: " + kasiteltavajuna.get(0).getStationShortCode() + ", departure: " + junanAika );

            getStopStationsOfCertainTrainNr(kasiteltavajuna, junanAika);

            System.out.println("Arrival station: " + kasiteltavajuna.get(kasiteltavajuna.size()-1).getStationShortCode()+ ", estimated arrival time: " + junanAika );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //SANNA:
    //-> Metodi hakee asemat, joilla juna pysähtyy, niille arvioidut saapumisajat sekä pysähdysten kestot eri asemilla minuuteissa
    private static void getStopStationsOfCertainTrainNr(List<TimeTableRow> kasiteltavajuna, Date junanAika) {
        for ( int b=1; b<kasiteltavajuna.size()-1; b=b+2) {
            Date saapuminen =kasiteltavajuna.get(b+1).getScheduledTime();
            Date lahto = kasiteltavajuna.get(b).getScheduledTime();
            long erotus =(saapuminen.getTime() - lahto.getTime());
            erotus = erotus/60000;
            if ( kasiteltavajuna.get(b).trainStopping){
                System.out.println("Train stops: " + kasiteltavajuna.get(b).getStationShortCode()+ ", estimated arrival time: " + junanAika + ", stop length: " + erotus +" min");
            }
        }
    }
    //SANNA:
    // hakee syötteen perusteella junan tiedot URLin takaa ja Mappaa ne
    private static List<Juna> getInfoByTrainNr(String trainnumber, String baseurl) throws IOException {
        { URL url = new URL(URI.create(String.format("%s/trains/latest/"+ trainnumber, baseurl)).toASCIIString());
            return MapperMethod(url);
        }
    }

    private static List<Juna> MapperMethod(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
        return mapper.readValue(url, tarkempiListanTyyppi);
    }


    public static void tulostaSeuraavaJunaLähtöJaMääräasemienPerusteella(String departureStation, String arrivalStation) {
        String baseurl = "https://rata.digitraffic.fi/api/v1";

        try {
            URL url = new URL(URI.create(String.format("%s/live-trains/station/"+ departureStation + "/" +arrivalStation, baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;

            // for (Juna j : junat) {
            System.out.println("Train " +junat.get(0).getTrainNumber() + " from " + departureStation + " to " + arrivalStation + " leaves at " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
            //   i++;
            // }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public static void returnDepartedTrainsFromStation(String departureStation) {
        String baseurl = "https://rata.digitraffic.fi/api/v1";

        try {
            URL url = new URL(URI.create(String.format("%s/live-trains/station/" + departureStation, baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;

            // for (Juna j : junat) {
            if (junat.get(0).runningCurrently) {
                System.out.println("The latest train departed from " + departureStation + " is: Train " + junat.get(0).getTrainNumber());
                // Tähän lisättävä "junat.get(0).get.Timetablerows(0) stationShortCode" eli mille asemalle juna saapuu seuraavaksi + get.ScheduledTime
            } else {
                System.out.println("Train not running currently");
            }
        } catch(Exception ex){
            System.out.println(ex);
        }
    }

    public static void tulostaHyvaksyttavatAsemat() {
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            URL url = new URL(URI.create(String.format("%s/live-trains/station/hki/LH", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;

            // for (Juna j : junat) {
            System.out.println("Station codes are: " + junat.get(0).getTimeTableRows().get(0).getStationShortCode());
            //   i++;
            // }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static void kahdenAsemanValillaLiikkeessaOlevatJunat() {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä Lahteen)
            URL url = new URL(URI.create(String.format("%s/live-trains/station/HKI/TPE", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi

            for (Juna j : junat) {
                Date departure =j.getTimeTableRows().get(0).getScheduledTime();
                Date arrival = j.getTimeTableRows().get(j.getTimeTableRows().size()-1).getScheduledTime();
                System.out.println("lähtö" + departure);
                System.out.println("arrival " + arrival);
                System.out.println(new Date());
                if(j.getTimeTableRows().get(0).getScheduledTime().after(new Date()) && j.getTimeTableRows().get(j.getTimeTableRows().size()-1).getScheduledTime().before(new Date())) {
                    System.out.println("Junan numero on " + j.getTrainNumber());
                    System.out.println("Junan lähtöpäivä on " + j.getDepartureDate());
                    System.out.println("Juna on lähtenyt asemaltaan: " + j.getTimeTableRows().get(0).getScheduledTime());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}