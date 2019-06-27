package trainproject;

public class CommonTools {
    public static String fixInputOutlook(String input){
        return input.toUpperCase().replaceAll("\\s+","");
    }
}