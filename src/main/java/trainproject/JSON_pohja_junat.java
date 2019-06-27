package trainproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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

  /*  public static void main(String[] args) {
        kahdenAsemanValillaLiikkeessaOlevatJunat("OL", "TKU");
    }*/

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

    ///SANNAN KOODIA... TESTIT: MM. PYSÄHTYMINEN, TOIMIIKO PYSÄHTYMISAIKA

    public static void printTrainInfo(String trainnumber) {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";

        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat tietyllä numerolla)
            URL url = new URL(URI.create(String.format("%s/trains/2019-06-27/" + trainnumber, baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;
            //  for (Juna j : junat) {
            System.out.println("Junan numero on " + junat.get(i).getTrainNumber());
            System.out.println("Junan lähtöpäivä on " + junat.get(i).getDepartureDate());
            System.out.println("lähtöasema: " + junat.get(0).getTimeTableRows().get(0).getStationShortCode());
            for (int b = 1; b < junat.get(0).getTimeTableRows().size() - 1; b = b + 2) {
                Date saapuminen = junat.get(0).getTimeTableRows().get(b + 1).getScheduledTime();
                Date lahto = junat.get(0).getTimeTableRows().get(b).getScheduledTime();
                long erotus = (saapuminen.getTime() - lahto.getTime());
                erotus = erotus / 60000;
            /*    String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(erotus),
                        TimeUnit.MILLISECONDS.toMinutes(erotus) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(erotus) % TimeUnit.MINUTES.toSeconds(1));
             */
                if (junat.get(0).getTimeTableRows().get(b).trainStopping) {
                    System.out.println("asema: " + junat.get(0).getTimeTableRows().get(b).getStationShortCode() + " pysähtymisaika: " + erotus + " min");
                }
            }
            System.out.println("pääteasema: " + junat.get(0).getTimeTableRows().get(junat.get(0).getTimeTableRows().size() - 1).getStationShortCode());

            System.out.println("Juna lähtee ekalta asemalta: " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
            //   i++;
            // }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }


    public static void tulostaSeuraavaJunaLähtöJaMääräasemienPerusteella(String departureStation, String arrivalStation) {
        String baseurl = "https://rata.digitraffic.fi/api/v1";

        try {
            URL url = new URL(URI.create(String.format("%s/live-trains/station/" + departureStation + "/" + arrivalStation, baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;

            // for (Juna j : junat) {
            System.out.println("Train " + junat.get(0).getTrainNumber() + " from " + departureStation + " to " + arrivalStation + " leaves at " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
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
        } catch (Exception ex) {
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

    public static void activeTrainsBetweenTwoStations(String departureStation, String arrivalStation) {
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

