package trainproject;

import java.util.ArrayList;
import java.util.Scanner;

public class UserInterface {
    private static final String menutext = "\nChoose an option:\n"
            + "1: Find the next train based on departure and arrival stations\n"
            + "2: Get info of a specific train\n"
            + "3: Search trains that are on the move\n"
            + "0: Quit";

    public void run() {
        Scanner scanner = new Scanner(System.in);
        for (; ; ) {
            System.out.println(menutext);
            String input = scanner.nextLine();
            if ("1".equals(input)) {
                returnTrainByGivenStations();
            } else if ("2".equals(input)) {
                System.out.println("Enter train number");
                String trainnumber = scanner.nextLine();
                JSON_pohja_junat.ListInfoOfCertainTrain(trainnumber);
            } else if ("3".equals(input)) {
                findMovingTrainInfo();
            } else if ("0".equals(input)) {
                System.out.println("Thank you! Have a nice day!");
                break;
            } else {
                System.err.println(String.format("'" + input + "' is not a valid input. Please type again:"));
            }
        }
    }

    //Tuomas
    Scanner scanner = new Scanner(System.in);
    private void returnTrainByGivenStations() {

        System.out.println("Enter departure station:");
        String departureStation = scanner.nextLine();

        System.out.println("Enter arrival station");
        String arrivalStation = scanner.nextLine();

        JSON_pohja_junat.tulostaSeuraavaJunaLähtöJaMääräasemienPerusteella(departureStation, arrivalStation);
        System.out.println("Now returning back to main menu...");
    }

    private void findMovingTrainInfo() {
        System.out.println("Choose an option:\n"
                + "1: Find all the trains between station A and station B\n"
                + "2: Info based on your location\n"
                + "3: Find all the trains departed from your station\n"
                + "0: Return to main menu");

        String choice = scanner.nextLine();
        //Rico
        if ("1".equals(choice)){
            System.out.println("Find all the trains between station A and station B");
            System.out.print("Insert station A: ");
            String stationA = scanner.nextLine();
            stationA = CommonTools.fixInputOutlook(stationA);
            System.out.print("Insert station B: ");
            String stationB = scanner.nextLine();
            stationB = CommonTools.fixInputOutlook(stationB);
            JSON_pohja_junat.activeTrainsBetweenTwoStations(stationA, stationB);

        } else if ("2".equals(choice)){
            System.out.println("Find nearest trains based on your location");
            System.out.println("Enter your location:");
            String locationData = scanner.nextLine();
            System.out.println("Nearest trains and stations next to " + locationData + " are: ...");

        //Tuomas
        } else if ("3".equals(choice)){
            System.out.println("Find all the trains departed from your station");
            String departureStation = scanner.nextLine();
            JSON_pohja_junat.returnLiveDepartedTrainsFromStation(departureStation);

        } else if ("0".equals(choice)) {
            System.out.println("Now returning back to main menu...");
        } else {
            System.err.println(String.format("Unknown choice, please type again: '%s'", choice));
        }
    }
    public static void main(String[] args) {
        new UserInterface().run();
    }
}