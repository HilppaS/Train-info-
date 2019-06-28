package trainproject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainStations {
    public static void main(String[] args) {
        createListOfTrainStations();
    }

    boolean passengerTraffic;
    String type;
    String stationName;
    String stationShortCode;
    int stationUICCode;
    String countryCode;
    double longitude;
    double latitude;

    public String getStationName() {
        return stationName;
    }

    public String getStationShortCode() {
        return stationShortCode;
    }

    private static void createListOfTrainStations() {
        // Määritetään API:n osoite, mistä JSON-datat haetaan
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        try {
            // Määritetään url-parametriin haettava asia (esim. live-junat, Helsingistä Lahteen)
            URL url = new URL(URI.create(String.format("%s/metadata/stations", baseurl)).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();

            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, TrainStations.class);

            //List<Juna> junat = mapper.readValue(url, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            List<TrainStations> asemat = mapper.readValue(url, tarkempiListanTyyppi);
            //int i = 0;
            //System.out.println(asemat.get(0).getStationName());
            //System.out.println("Asemat: " + asemat.get(0));

            Map<String, Object> asemaTaulukko = new HashMap<String, Object>();

            for(TrainStations asema : asemat) {
                asemaTaulukko.put(asema.getStationName(), asema.getStationShortCode());
            }

            // Lyhenne on value (containsValue), kaupungin nimi on key (containsKey)
            System.out.println(asemaTaulukko);
            System.out.println(asemaTaulukko.containsKey("Nastola"));
            System.out.println(asemaTaulukko.containsValue("HKI"));
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // }

    }
}