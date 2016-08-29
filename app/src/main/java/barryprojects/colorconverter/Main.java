package barryprojects.colorconverter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.danielnilsson9.colorpickerview.view.ColorPanelView;
import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

import java.util.Locale;

public class Main extends AppCompatActivity {

    // elements for the main window
    private EditText hexEdit;
    private EditText redEdit;
    private EditText greenEdit;
    private EditText blueEdit;
    private Button pickerButton;
    private ColorPanelView colorShow;

    private String hexColor;
    private int redColor;
    private int greenColor;
    private int blueColor;

    private boolean invalidRGB; // true when at least one of the RGB values is invalid

    // constants for putting values into a bundle and recreating instance state
    private static final String HEXTAG = "hex";
    private static final String REDTAG = "red";
    private static final String GREENTAG = "green";
    private static final String BLUETAG = "blue";


    // overridden methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hexEdit = (EditText) findViewById(R.id.txt_hex);
        redEdit = (EditText) findViewById(R.id.txt_red);
        greenEdit = (EditText) findViewById(R.id.txt_green);
        blueEdit = (EditText) findViewById(R.id.txt_blue);
        pickerButton = (Button) findViewById(R.id.button_picker);
        colorShow = (ColorPanelView) findViewById(R.id.color_preview);

        // all EditTexts should have a default value
        hexColor = "#" + hexEdit.getText().toString().trim();
        redColor = Integer.valueOf(redEdit.getText().toString().trim());
        greenColor = Integer.valueOf(greenEdit.getText().toString().trim());
        blueColor = Integer.valueOf(blueEdit.getText().toString().trim());
        invalidRGB = false;

        colorShow.setColor(Color.parseColor(hexColor));

        hexEdit.addTextChangedListener(new HexCodeListener());
        redEdit.addTextChangedListener(new RedCodeListener());
        greenEdit.addTextChangedListener(new GreenCodeListener());
        blueEdit.addTextChangedListener(new BlueCodeListener());

        // if tag is not null, it is the app who edited the text
        hexEdit.setTag(null);
        redEdit.setTag(null);
        greenEdit.setTag(null);
        blueEdit.setTag(null);

        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerDialog();
            } // end public void onClick
        }); // end anonymous OnClickListener

    } // end protected void onCreate

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(HEXTAG, hexColor);
        savedInstanceState.putInt(REDTAG, redColor);
        savedInstanceState.putInt(GREENTAG, greenColor);
        savedInstanceState.putInt(BLUETAG, blueColor);

        super.onSaveInstanceState(savedInstanceState);
    } // end public void onSaveInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        hexColor = savedInstanceState.getString(HEXTAG);
        redColor = savedInstanceState.getInt(REDTAG);
        greenColor = savedInstanceState.getInt(GREENTAG);
        blueColor = savedInstanceState.getInt(BLUETAG);
        updateColor();

    } // end public void onRestoreInstanceState


    // Helper functions

    private void checkRGB() {
        // if any text field is empty, the RGB color codes are invalid
        invalidRGB = redEdit.getText().toString().trim().isEmpty() || greenEdit.toString().trim().isEmpty() || blueEdit.getText().toString().trim().isEmpty();

        // if they are not empty, check their value to see if they are really valid
        if(!invalidRGB) {
            int red = Integer.valueOf(redEdit.getText().toString().trim());
            int green = Integer.valueOf(greenEdit.getText().toString().trim());
            int blue = Integer.valueOf(blueEdit.getText().toString().trim());

            String redStr = MiscFunctions.intToHex(red);
            String greenStr = MiscFunctions.intToHex(green);
            String blueStr = MiscFunctions.intToHex(blue);

            invalidRGB = redStr.isEmpty() || greenStr.isEmpty() || blueStr.isEmpty();
        } // end if invalidRGB

    } // end private void checkRGB

    private void updateColor() {

        int red = Integer.valueOf(redEdit.getText().toString().trim());
        int green = Integer.valueOf(greenEdit.getText().toString().trim());
        int blue = Integer.valueOf(blueEdit.getText().toString().trim());

        String redStr = MiscFunctions.intToHex(red);
        String greenStr = MiscFunctions.intToHex(green);
        String blueStr = MiscFunctions.intToHex(blue);

        hexColor = "#" + redStr + greenStr + blueStr;
        colorShow.setColor(Color.parseColor(hexColor));

        // tags are used to tell the TextWatchers the app is editing this field instead of the user
        hexEdit.setTag(getString(R.string.editing_tag));
        hexEdit.setText(hexColor.substring(1));
        hexEdit.setTag(null);

    } // end private void updateColor

    private void colorPickerDialog() {

        ChromaDialog.Builder builder = new ChromaDialog.Builder();
        builder.initialColor(Color.parseColor(hexColor));
        builder.colorMode(ColorMode.RGB);
        builder.indicatorMode(IndicatorMode.DECIMAL);
        builder.onColorSelected(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                hexColor = String.format(Locale.getDefault(), "#%06X", (0xFFFFFF & color));
                hexEdit.setText(hexColor.substring(1)); // omit the starting #
            } // end public void onColorSelected
        }); // end anonymous OnColorSelectedListener

        builder.create().show(getSupportFragmentManager(), getString(R.string.picker));

    } // end private void colorPickerDialog


    // listeners

    private abstract class ColorCodesListener implements TextWatcher {
        // all methods are empty because the required methods will be overridden in the subclasses
        @Override
        public void afterTextChanged(Editable e) {} // empty method
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {} // empty method
        @Override
        public void onTextChanged(CharSequence s, int start, int beofre, int count) {} // empty method
    } // end private abstract class ColorCodesListener

    private class HexCodeListener extends ColorCodesListener {
        @Override
        public void afterTextChanged(Editable e) {
            // if the app edited this field, return
            if(hexEdit.getTag() != null) return;
            // first check if entered text is a valid hex code
            if(!MiscFunctions.isValidHex(e.toString())) return;


            // if it is, convert it to RGB and show it in the RGB text fields and the colorShow
            String redStr = e.toString().substring(0, 2);
            String greenStr = e.toString().substring(2, 4);
            String blueStr = e.toString().substring(4, 6);

            hexColor = "#" + e.toString();
            redColor = MiscFunctions.hexToInt(redStr);
            greenColor = MiscFunctions.hexToInt(greenStr);
            blueColor = MiscFunctions.hexToInt(blueStr);

            // tags are used to tell the TextWatchers the app is editing the text instead of the user
            redEdit.setTag(getString(R.string.editing_tag));
            greenEdit.setTag(getString(R.string.editing_tag));
            blueEdit.setTag(getString(R.string.editing_tag));

            redEdit.setText(String.valueOf(redColor));
            greenEdit.setText(String.valueOf(greenColor));
            blueEdit.setText(String.valueOf(blueColor));

            redEdit.setTag(null);
            greenEdit.setTag(null);
            blueEdit.setTag(null);

            colorShow.setColor(Color.parseColor(hexColor));

        } // end public void afterTextChanged
    } // end private class HexCodeListener

    private class RedCodeListener extends ColorCodesListener {
        @Override
        public void afterTextChanged(Editable e) {
            // if the app edited this field, do nothing
            if(redEdit.getTag()!= null) return;

            if(e.toString().isEmpty()) {
                invalidRGB = true;
                return;
            } // end if e.toString

            int color = Integer.valueOf(e.toString().trim());
            String hex = MiscFunctions.intToHex(color);
            // if returned string is empty, entered integer was too high
            if(hex.isEmpty()) {
                invalidRGB = true;
                return;
            } // end if hex.isEmpty

            checkRGB();
            if(!invalidRGB) updateColor();
        } // end public void afterTextChanged
    } // end private class RedCodeListener

    private class GreenCodeListener extends ColorCodesListener {
        @Override
        public void afterTextChanged(Editable e) {
            // if the app edited this field, do nothing
            if(greenEdit.getTag() != null) return;

            if(e.toString().isEmpty()) {
                invalidRGB = true;
                return;
            } // end if e.toString

            int color = Integer.valueOf(e.toString().trim());

            String hex = MiscFunctions.intToHex(color);
            // if returned string is empty, entered integer was too high
            if(hex.isEmpty()) {
                invalidRGB = true;
                return;
            } // end if hex.isEmpty

            checkRGB();
            if(!invalidRGB) updateColor();

        } // end public void afterTextChanged
    } // end private class GreenCodeListener

    private class BlueCodeListener extends ColorCodesListener {
        @Override
        public void afterTextChanged(Editable e) {
            // if the app edited this field, do nothing
            if(blueEdit.getTag() != null) return;
            if(e.toString().isEmpty()) {
                invalidRGB = true;
                return;
            } // end if e.toString

            int color = Integer.valueOf(e.toString().trim());

            String hex = MiscFunctions.intToHex(color);
            // if returned string is empty, entered integer was too high
            if(hex.isEmpty()) {
                invalidRGB = true;
                return;
            } // end if hex.isEmpty
            checkRGB();
            if(!invalidRGB) updateColor();
        } // end public void afterTextChanged
    } // end private class BlueCodeListener

} // end public class Main
