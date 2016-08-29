package barryprojects.colorconverter;


import java.util.ArrayList;

public class MiscFunctions {

    private static ArrayList<Character> getHexChars() {
        // to avoid clutter in isValidHex
        ArrayList<Character> accepted = new ArrayList<>(16);
        accepted.add('0');
        accepted.add('1');
        accepted.add('2');
        accepted.add('3');
        accepted.add('4');
        accepted.add('5');
        accepted.add('6');
        accepted.add('7');
        accepted.add('8');
        accepted.add('9');
        accepted.add('a');
        accepted.add('b');
        accepted.add('c');
        accepted.add('d');
        accepted.add('e');
        accepted.add('f');

        return accepted;
    } // end private HashSet<Character> getAcceptedChars

    public static boolean isValidHex(String hex) {
        // a hex color code is six digits long
        // this will change when I add support for argb color codes
        if(hex.length() != 6) return false;

        ArrayList<Character >acceptedChars = getHexChars();
        hex = hex.toLowerCase();
        for(int i = 0; i < hex.length(); i++) {
            if(!acceptedChars.contains(hex.charAt(i))) return false;
        } // end for int i

        return true;
    } // end public boolean isValidHex

    public static int hexToInt(String hex) {
        ArrayList<Character> hexChars = getHexChars();
        hex = hex.toLowerCase();
        char firstChar = hex.charAt(0);
        char secondChar = hex.charAt(1);

        return hexChars.indexOf(firstChar) * 16 + hexChars.indexOf(secondChar);
    } // end public static int hexToInt

    public static String intToHex(int val) {
        if(val > 255) return ""; // return empty string if val is too high

        ArrayList<Character> hexChars = getHexChars();
        return hexChars.get(val / 16).toString() + hexChars.get(val % 16).toString();
    } // end public static string intToHex


} // end public class MiscFunctions
