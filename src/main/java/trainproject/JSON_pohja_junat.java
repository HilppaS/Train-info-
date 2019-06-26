package trainproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

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
    public static void main(String[]args){
        printTrainInfo("67");
    }
    private static void lueJunanJSONData() {
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

        ///SANNAN KOODIA...

    public static void printTrainInfo(String trainnumber) {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";

        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat tietyllä numerolla)
            URL url = new URL(URI.create(String.format("%s/trains/2019-06-27/"+ trainnumber, baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            int i = 0;
          //  for (Juna j : junat) {
            System.out.println("Junan numero on " + junat.get(i).getTrainNumber());
            System.out.println("Junan lähtöpäivä on " + junat.get(i).getDepartureDate());
            System.out.println("lähtöasema: " + junat.get(0).getTimeTableRows().get(0).getStationShortCode());
            for ( int b=0; b<junat.get(0).getTimeTableRows().size(); b++) {
                System.out.println("asema: " + junat.get(0).getTimeTableRows().get(b).getStationShortCode());
            }
            System.out.println("pääteasema: " + junat.get(junat.size()-1).getTimeTableRows().get(0).getStationShortCode());
            Date saapuminen =junat.get(0).getTimeTableRows().get(2).getScheduledTime();
            Date lahto = junat.get(0).getTimeTableRows().get(1).getScheduledTime();
            long erotus =(saapuminen.getTime() - lahto.getTime());
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(erotus),
                    TimeUnit.MILLISECONDS.toMinutes(erotus) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(erotus) % TimeUnit.MINUTES.toSeconds(1));

            System.out.println("Junan pysähtymisaika asemalla X( tällä hetkellä 1. asema ): " + hms);
            System.out.println("Juna lähtee ekalta asemalta: " + junat.get(0).getTimeTableRows().get(0).getScheduledTime());
            //   i++;
            // }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

}

