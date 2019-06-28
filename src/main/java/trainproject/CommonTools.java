package trainproject;


import java.util.HashMap;
import java.util.Map;

public class CommonTools {
    public static String fixInputOutlook(String input) {
        return input.toUpperCase().replaceAll("\\s+", "");
    }

    public static String checkIfStringIsValidShortcodeOrStationNameAndChangeToShortCode(String input) {
        try {
            HashMap trainStationHashMap = TrainStations.createListOfTrainStations();
            System.out.println(trainStationHashMap);
            if (trainStationHashMap.containsKey(input)) {
                System.out.println("Tää tuli" + input);
                return input;
            }
            if (trainStationHashMap.containsValue(input))
                for (Object name : trainStationHashMap.entrySet()) {
                    if (name.toString().equals(input)) {
                        System.out.println("Ny tuli tää toka");
                        return name.toString();
                    }
                }
            System.out.println("Ny tuli kolmas" + input);
            return input;
        } catch (
                NullPointerException ex) {
            System.out.println("Error occurred.");
        }
        return null;

    }
}