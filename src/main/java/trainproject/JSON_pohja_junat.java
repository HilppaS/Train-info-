package trainproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.commons.lang3.time.DateUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
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
        activeTrainsFromSingleStation("HKI");
    }

    public static void lueJunanJSONData() {

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
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    //SANNAN KOODIA:
    //-> asemat joilla juna pysähtyy, sekä niille arvioidut saapumisajat
    // -> pysähdysten kestot eri asemilla minuuteissa
    // -> pääteasema, saapumisaika
    //-> jos juna on jo kulussa, tieto todellisista ja arvioiduista ajoista asemilla

    public static void ListInfoOfCertainTrain(String trainnumber) {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            List<Juna> junat = getInfoByTrainNr(trainnumber, baseurl);
            int i = 0;
            List<TimeTableRow> kasiteltavajuna = junat.get(0).getTimeTableRows();
            Date junanAika = kasiteltavajuna.get(0).getScheduledTime();

            System.out.println("Train nr: " + junat.get(i).getTrainNumber());
            System.out.println("Departure station: " + kasiteltavajuna.get(0).getStationShortCode() + ", departure: " + junanAika);

            getStopStationsOfCertainTrainNr(kasiteltavajuna, junanAika);

            System.out.println("Arrival station: " + kasiteltavajuna.get(kasiteltavajuna.size() - 1).getStationShortCode() + ", estimated arrival time: " + junanAika);

        } catch (Exception e) {
            System.out.println("Train number not valid");
        }
    }

    private static void getStopStationsOfCertainTrainNr(List<TimeTableRow> kasiteltavajuna, Date junanAika) {
        for (int b = 1; b < kasiteltavajuna.size() - 1; b = b + 2) {
            Date saapuminen = kasiteltavajuna.get(b + 1).getScheduledTime();
            Date lahto = kasiteltavajuna.get(b).getScheduledTime();
            long erotus = (saapuminen.getTime() - lahto.getTime());
            erotus = erotus / 60000;
            if (kasiteltavajuna.get(b).trainStopping) {
                System.out.println("Train stops: " + kasiteltavajuna.get(b).getStationShortCode() + ", estimated arrival time: " + junanAika + ", stop length: " + erotus + " min");
            }
        }
    }

    private static List<Juna> getInfoByTrainNr(String trainnumber, String baseurl) throws IOException {
        {
            URL url = new URL(URI.create(String.format("%s/trains/latest/" + trainnumber, baseurl)).toASCIIString());
            return MapperMethod(url);
        }
    }

    private static List<Juna> MapperMethod(URL url) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
        return mapper.readValue(url, tarkempiListanTyyppi);
    }


    //TUOMAKSEN KOODIA:
    // -> Metodi pyytää käyttäjää syöttämään haluamansa lähtö- ja pääteaseman.
    // -> Käyttäjän syötteet lisätään url-osoitteeseen, jonka jälkeen digitraffic.fi -sivulta haetaan seuraavat viisi junaa ja niiden lähtöajat
    public static void tulostaSeuraavaJunaLähtöJaMääräasemienPerusteella(String departureStation, String arrivalStation) {
        String baseurl = "https://rata.digitraffic.fi/api/v1";

        try {
            URL url = new URL(URI.create(String.format("%s/live-trains/station/" + departureStation + "/" + arrivalStation + "?limit=5", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi

            System.out.println("Next suitable trains are:");

            for (Juna j : junat) {
                System.out.println("Train " + j.getTrainNumber() + " from " + departureStation + " to " + arrivalStation + " leaves at " + j.getTimeTableRows().get(0).getScheduledTime());
            }
        } catch (
                JsonMappingException ex) {
            System.out.println("Our system only shows direct trains. It seems there are no direct trains traveling between " + departureStation + " and " + arrivalStation);
        } catch (
                MalformedURLException ex) {
            System.out.println("Our system only shows direct trains. It seems there are no direct trains traveling between " + departureStation + " and " + arrivalStation);
        } catch (
                IOException ex) {
            System.out.println("Our system only shows direct trains. It seems there are no direct trains traveling between " + departureStation + " and " + arrivalStation);
        }
    }

    //TUOMAS (plagioiden)

    public static void activeTrainsFromSingleStation(String departureStation) {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä)
            URL url = new URL(URI.create(String.format("%s/live-trains/station/" + departureStation + "/" + "?arrived_trains=0&arriving_trains=0&departed_trains=50&departing_trains=50&include_nonstopping=false", baseurl)).toASCIIString());

            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int junienklm = 0;
            for (Juna j : junat) {
                List<TimeTableRow> aikataulu = j.getTimeTableRows();
                boolean lahto = false;
                boolean maaranpaa = false;
                Date korjattulahtoaika = new Date();
                Date korjattusaapumisaika = new Date();
                Date junanlahtoaika = new Date();
                Date korjatturivinaika;
                boolean onkoJunaJoPrintattu = false;
                junanlahtoaika = DateUtils.addHours(aikataulu.get(0).getScheduledTime(), -3);
                if (junanlahtoaika.before(new Date())) {
                    for (TimeTableRow rivi : aikataulu) {
                        korjatturivinaika = DateUtils.addHours(rivi.getScheduledTime(), -3);
                        if (!onkoJunaJoPrintattu && korjatturivinaika.after(new Date())) {
                            System.out.println("Train number " + j.getTrainNumber() + " has left " + departureStation + " on " + junanlahtoaika + " and is on its way to " + rivi.getStationShortCode() + ". The train is scheduled to arrive on " + korjatturivinaika);
                            junienklm++;
                            onkoJunaJoPrintattu=true;
                        }
                    }
                }
            }
                        if (junienklm == 0) {
                            System.out.println("There does not seem to be any active trains that have left " + departureStation);
            }
        } catch (
                JsonMappingException ex) {
            System.out.println("Our system only shows direct trains. It seems there are no direct active trains from " + departureStation);
        } catch (
                MalformedURLException ex) {
            System.out.println("Our system only shows direct trains. It seems there are no direct active trains from " + departureStation);
        } catch (
                IOException ex) {
            System.out.println("Our system only shows direct trains. It seems there are no direct active trains from " + departureStation);
        }
    }


        //RICON KOODIA:
        public static void activeTrainsBetweenTwoStations (String departureStation, String arrivalStation){
            // Määritetään API:n osoite, mistä JSON-datat haetaan
            String baseurl = "https://rata.digitraffic.fi/api/v1";
            try {
                // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä Lahteen)
                URL url = new URL(URI.create(String.format("%s/live-trains/station/" + departureStation + "/" + arrivalStation, baseurl)).toASCIIString());
                ObjectMapper mapper = new ObjectMapper();
                CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
                List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
                int junienklm = 0;
                for (Juna j : junat) {
                    List<TimeTableRow> aikataulu = j.getTimeTableRows();
                    boolean lahto = false;
                    boolean maaranpaa = false;
                    Date korjattulahtoaika = new Date();
                    Date korjattusaapumisaika = new Date();
                    Date korjatturivinaika = new Date();
                    for (TimeTableRow rivi : aikataulu) {
                        korjatturivinaika = DateUtils.addHours(rivi.getScheduledTime(), -3);
                        if (departureStation.equals(rivi.getStationShortCode()) && rivi.isTrainStopping() && korjatturivinaika.before(new Date())) {
                            lahto = true;
                            korjattulahtoaika = DateUtils.addHours(rivi.getScheduledTime(), -3);
                        }
                        if (arrivalStation.equals(rivi.getStationShortCode()) && rivi.isTrainStopping() && korjatturivinaika.after(new Date())) {
                            maaranpaa = true;
                            korjattusaapumisaika = DateUtils.addHours(rivi.getScheduledTime(), -3);
                        }
                    }
                    if (lahto && maaranpaa) {
                        System.out.println("Train number " + j.getTrainNumber() + " has left " + departureStation + " on " + korjattulahtoaika + " and is on its way to " + arrivalStation + ". The train is scheduled to arrive on " + korjattusaapumisaika);
                        junienklm++;
                    }
                }
                if (junienklm == 0) {
                    System.out.println("There does not seem to be any trains currently traveling between " + departureStation + " and " + arrivalStation);
                }
            } catch (
                    JsonMappingException ex) {
                System.out.println("Our system only shows direct trains. It seems there are no direct trains traveling between " + departureStation + " and " + arrivalStation);
            } catch (
                    MalformedURLException ex) {
                System.out.println("Our system only shows direct trains. It seems there are no direct trains traveling between " + departureStation + " and " + arrivalStation);
            } catch (
                    IOException ex) {
                System.out.println("Our system only shows direct trains. It seems there are no direct trains traveling between " + departureStation + " and " + arrivalStation);
            }
        }
    }
