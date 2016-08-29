package barryprojects.colorconverter;


import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class Support {
    // class used for various checks and conversions

    public static boolean isValidRGB(String color) {
        // empty strings are invalid
        if(color.isEmpty()) return false;

        // input for this function should always come from the RGB EditText and therefore be convertible to an integer
        int value = Integer.valueOf(color);
        // obviously, RGB color values can't go above 255 or under zero
        if(value > 255 || value < 0) return false;

        // now all is fine
        return true;
    } // end public static boolean isValidRGB

    public static boolean isValidHex(String hex) {
        // excepts a string which, if valid, is 6 digits long (a hex code without the #)
        if(!(hex.length() == 6)) return false;

        // convert hex to lowercase and then check if the entered characters are valid hexadecimal characters
        hex = hex.toLowerCase();
        ArrayList<Character> hexChars = getHexChars();
        for(int i = 0; i < 6; i++) {
            if(!hexChars.contains(hex.charAt(i))) return false;
        } // end for int i

        // now the string is valid
        return true;
    } // end public static boolean isValidHex

    public static String RGBToHex(String red, String green, String blue) {
        // expects strings that are convertible to integers

        int redInt = Integer.valueOf(red);
        int greenInt = Integer.valueOf(green);
        int blueInt = Integer.valueOf(blue);

        // now convert these integers to their hexadecimal form
        ArrayList<Character> hexChars = getHexChars();
        String redHex = String.valueOf(hexChars.get(redInt / 16)) + String.valueOf(hexChars.get(redInt % 16));
        String greenHex = String.valueOf(hexChars.get(greenInt / 16)) + String.valueOf(hexChars.get(greenInt % 16));
        String blueHex = String.valueOf(hexChars.get(blueInt / 16)) + String.valueOf(hexChars.get(blueInt % 16));

        return Main.HASHTAG + redHex + greenHex + blueHex;
    } // end public static String RGBTohex

    public static String[] hexToRGB(String hex) {
        // expects a valid 6-digit hexadecimal string without the starting #
        hex = hex.toLowerCase();

        String redHex = hex.substring(0, 2);
        String greenHex = hex.substring(2, 4);
        String blueHex = hex.substring(4, 6);

        // convert these strings to their decimal form
        ArrayList<Character> hexChars = getHexChars();
        int redInt = hexChars.indexOf(redHex.charAt(0)) * 16 + hexChars.indexOf(redHex.charAt(1));
        int greenInt = hexChars.indexOf(greenHex.charAt(0)) * 16 + hexChars.indexOf(greenHex.charAt(1));
        int blueInt = hexChars.indexOf(blueHex.charAt(0)) * 16 + hexChars.indexOf(blueHex.charAt(1));

        // now create the array and return it
        String[] RGB = new String[3];
        RGB[0] = String.valueOf(redInt);
        RGB[1] = String.valueOf(greenInt);
        RGB[2] = String.valueOf(blueInt);

        return RGB;
    } // end public static String[] hexToRGB

    public static void showToast(Context context, String text, boolean longToast) {
        if(longToast) Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        else Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    } // end main public static void showToast

    public static void showToast(Context context, String text) {
        showToast(context, text, false);
    } // end public static void showToast

    public static void showToast(Context context, int stringId, boolean longToast) {
        showToast(context, context.getResources().getString(stringId), longToast);
    } // end public static void showToast

    public static void showToast(Context context, int stringId) {
        showToast(context, stringId, false);
    } // end public static void showToast




    // private helper methods

    private static ArrayList<Character> getHexChars() {
        ArrayList<Character> chars = new ArrayList<>(16);

        chars.add('0');
        chars.add('1');
        chars.add('2');
        chars.add('3');
        chars.add('4');
        chars.add('5');
        chars.add('6');
        chars.add('7');
        chars.add('8');
        chars.add('9');
        chars.add('a');
        chars.add('b');
        chars.add('c');
        chars.add('d');
        chars.add('e');
        chars.add('f');

        return chars;
    } // end private ArrayList<Character> getHexChars
} // end public class Support
