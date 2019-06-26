package trainproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
Vaatii Jackson kirjaston:
File | Project Structure
Libraries >> Add >> Maven
Etsi "jackson-databind", valitse esimerkiksi versio 2.0.5
Asentuu Jacksonin databind, sekä core ja annotations
 */

public class JSON_pohja_junat {
    public static void main(String[] args) {
        createListOfTrainStations();
    }

    private static void lueJunanJSONData() {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä Lahteen)
            URL url = new URL(URI.create(String.format("%s/live-trains/station/HKI/LH", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;

            // for (Juna j : junat) {
            System.out.println("Junan numero on " + junat.get(i).getTrainNumber());
            System.out.println("Junan lähtöpäivä on " + junat.get(i).getDepartureDate());
            System.out.println("TimetableRow ekalle junalle: " + junat.get(i).getTimeTableRows());
            System.out.println("Eka juna lähtee ekalta asemalta: " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
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
            URL url = new URL(URI.create(String.format("%s/live-trains/station/HKI/OL", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;
            Date lahto = junat.get(0).getTimeTableRows().get(2).getScheduledTime();
            Date saapuminen = junat.get(0).getTimeTableRows().get(4).getScheduledTime();
            long ero = saapuminen.getTime() - lahto.getTime();
            System.out.println("Erotus " + ero);
            for (Juna j : junat) {
                System.out.println(j.isRunningCurrently());
                System.out.println("Juna on lähtenyt asemaltaan: " + junat.get(i).getTimeTableRows().get(0).getScheduledTime());
              /*  if(j.isRunningCurrently()) {
                    System.out.println("Junan numero on " + junat.get(i).getTrainNumber());
                    System.out.println("Junan lähtöpäivä on " + junat.get(i).getDepartureDate());
                    System.out.println("Juna on lähtenyt asemaltaan: " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
                    i++;
                }*/
                i++;
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static void createListOfTrainStations() {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä Lahteen)
            URL url = new URL(URI.create(String.format("%s/metadata/stations", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();

            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, TrainStations.class);
            //Map<String, Object> mapObject = new HashMap<String, Object>();

            //List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            List<TrainStations> asemat = mapper.readValue(url, tarkempiListanTyyppi);
            //int i = 0;
            System.out.println(asemat.get(0).getStationName());
            //System.out.println("Asemat: " + asemat.get(0));

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

