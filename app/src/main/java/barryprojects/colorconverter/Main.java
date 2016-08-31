package barryprojects.colorconverter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    // UI elements
    private EditText hexEdit;
    private EditText redEdit;
    private EditText greenEdit;
    private EditText blueEdit;
    private EditText alphaEdit;
    private SeekBar redBar;
    private SeekBar greenBar;
    private SeekBar blueBar;
    private SeekBar alphaBar;
    private LinearLayout rgbFields;
    private LinearLayout alphaField;
    private TextView display;
    private CheckBox secondaryCheck;
    private CheckBox alphaCheck;
    
    // variables to store colors
    private String primaryColor;
    private String secondaryColor;
    private String savedPrimaryAlpha;
    private String savedSecondaryAlpha;
    
    // some booleans
    private boolean appEditing = false; // true when app is editing text fields
    private boolean secondary = false; // true when user is picking the secondary color
    private boolean alphaPrimary = false; // true when primary color uses an alpha channel
    private boolean alphaSecondary = false; // true when secondary color uses an alpha channel
    private boolean useAlpha = false; // true when currently selected color uses an alpha channel
    
    // constants for the updateColor function to indicate the updated component
    private static final int FROM_HEX = 0;
    private static final int FROM_RED = 1;
    private static final int FROM_GREEN = 2;
    private static final int FROM_BLUE = 3;
    private static final int FROM_ALPHA = 4;
    private static final int FROM_CHANGE = 5;
    private static final int FROM_ALPHA_CHANGE = 6;

    // number sign, to be put in front of every hex color code
    public static final String HASHTAG = "#";

    // keys to identify data stored in bundles
    private static final String COLOR_PRIMARY = "primary color";
    private static final String COLOR_SECONDARY = "secondary color";
    private static final String ALPHA_PRIMARY = "saved primary alpha";
    private static final String ALPHA_SECONDARY = "saved secondary alpha";
    private static final String SECONDARY_STATE = "checkbox state";
    private static final String USING_ALPHA_PRIMARY = "using alpha for primary";
    private static final String USING_ALPHA_SECONDARY = "using alpha for secondary";
    private static final String USING_ALPHA_THIS = "using alpha for this color";
    
    
    // overridden methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // getting UI elements
        hexEdit = (EditText) findViewById(R.id.txt_hex);
        redEdit = (EditText) findViewById(R.id.txt_red);
        greenEdit = (EditText) findViewById(R.id.txt_green);
        blueEdit = (EditText) findViewById(R.id.txt_blue);
        alphaEdit = (EditText) findViewById(R.id.txt_alpha);

        rgbFields = (LinearLayout) findViewById(R.id.rgb_fields);
        alphaField = (LinearLayout) findViewById(R.id.field_alpha);

        redBar = (SeekBar) findViewById(R.id.bar_red);
        greenBar = (SeekBar) findViewById(R.id.bar_green);
        blueBar = (SeekBar) findViewById(R.id.bar_blue);
        alphaBar = (SeekBar) findViewById(R.id.bar_alpha);
        
        display = (TextView) findViewById(R.id.disp_color);
        secondaryCheck = (CheckBox) findViewById(R.id.check_secondary);
        alphaCheck = (CheckBox) findViewById(R.id.check_alpha);
        
        // setting their listeners
        hexEdit.addTextChangedListener(new HexListener());
        redEdit.addTextChangedListener(new RedListener());
        greenEdit.addTextChangedListener(new GreenListener());
        blueEdit.addTextChangedListener(new BlueListener());
        alphaEdit.addTextChangedListener(new AlphaListener());

        ButtonListener listener = new ButtonListener();
        secondaryCheck.setOnClickListener(listener);
        alphaCheck.setOnClickListener(listener);

        SeekBarListener barListener = new SeekBarListener();
        redBar.setOnSeekBarChangeListener(barListener);
        greenBar.setOnSeekBarChangeListener(barListener);
        blueBar.setOnSeekBarChangeListener(barListener);
        alphaBar.setOnSeekBarChangeListener(barListener);

        // initialize colors to their default values
        primaryColor = HASHTAG + getString(R.string.default_hex);
        secondaryColor = HASHTAG + getString(R.string.default_hex);

        // default alpha state is off
        rgbFields.removeView(alphaField);

        // set default alpha values for saved alpha variables
        savedPrimaryAlpha = getString(R.string.default_alpha);
        savedSecondaryAlpha = getString(R.string.default_alpha);
        
    } // end protected void onCreate

    // save and restore instance states
    // screen rotation should be disabled, but there are more cases where this might be needed
    // also, I plan to implement an alternative landscape layout and will need these when (if) I do
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(COLOR_PRIMARY, primaryColor);
        savedInstanceState.putString(COLOR_SECONDARY, secondaryColor);
        savedInstanceState.putString(ALPHA_PRIMARY, savedPrimaryAlpha);
        savedInstanceState.putString(ALPHA_SECONDARY, savedSecondaryAlpha);
        savedInstanceState.putBoolean(SECONDARY_STATE, secondaryCheck.isChecked());
        savedInstanceState.putBoolean(USING_ALPHA_PRIMARY, alphaPrimary);
        savedInstanceState.putBoolean(USING_ALPHA_SECONDARY, alphaSecondary);
        savedInstanceState.putBoolean(USING_ALPHA_THIS, useAlpha);

        super.onSaveInstanceState(savedInstanceState);
    } // end public void onSaveInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        primaryColor = savedInstanceState.getString(COLOR_PRIMARY);
        secondaryColor = savedInstanceState.getString(COLOR_SECONDARY);
        savedPrimaryAlpha = savedInstanceState.getString(ALPHA_PRIMARY);
        savedSecondaryAlpha = savedInstanceState.getString(ALPHA_SECONDARY);
        secondary = savedInstanceState.getBoolean(SECONDARY_STATE);
        alphaPrimary = savedInstanceState.getBoolean(USING_ALPHA_PRIMARY);
        alphaSecondary = savedInstanceState.getBoolean(USING_ALPHA_SECONDARY);
        useAlpha = savedInstanceState.getBoolean(USING_ALPHA_THIS);

        alphaCheck.setChecked(useAlpha);

        if(useAlpha && rgbFields.indexOfChild(alphaField) == -1) rgbFields.addView(alphaField);
        else if(!useAlpha && rgbFields.indexOfChild(alphaField) != -1) rgbFields.removeView(alphaField);

        if(secondary) updateColor(FROM_CHANGE, secondaryColor);
        else updateColor(FROM_CHANGE, primaryColor);

        display.setBackgroundColor(Color.parseColor(primaryColor));
        display.setTextColor(Color.parseColor(secondaryColor));

    } // end public void onRestoreInstanceState
    
    // helper methods
    private void updateColor(int from, String colorTxt) {
        String red;
        String green;
        String blue;
        String alpha = "";

        String[] RGB;

        switch(from) {
            case FROM_HEX:
                RGB = Support.hexToRGB(colorTxt);
                appEditing = true;

                redEdit.setText(RGB[0]);
                greenEdit.setText(RGB[1]);
                blueEdit.setText(RGB[2]);
                if(useAlpha) alphaEdit.setText(RGB[3]);

                redBar.setProgress(Integer.valueOf(RGB[0]));
                greenBar.setProgress(Integer.valueOf(RGB[1]));
                blueBar.setProgress(Integer.valueOf(RGB[2]));
                if(useAlpha) alphaBar.setProgress(Integer.valueOf(RGB[3]));

                appEditing = false;

                if(secondary) {
                    secondaryColor = HASHTAG + colorTxt;
                    display.setTextColor(Color.parseColor(secondaryColor));
                } // end if secondary
                else {
                    primaryColor = HASHTAG + colorTxt;
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                } // end else

                break;

            case FROM_RED:
                // first check if the other RGB values are valid (this doesn't get called if the red one is invalid)
                if(!Support.isValidRGB(greenEdit.getText().toString().trim()) || !Support.isValidRGB(blueEdit.getText().toString().trim())) return;
                else if(useAlpha)
                    if(!Support.isValidRGB(alphaEdit.getText().toString().trim())) return;

                // if they are, get their values and set colors accordingly
                green = greenEdit.getText().toString().trim();
                blue = blueEdit.getText().toString().trim();
                if(useAlpha) alpha = alphaEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    if(useAlpha) secondaryColor = Support.RGBToHex(colorTxt, green, blue, alpha);
                    else secondaryColor = Support.RGBToHex(colorTxt, green, blue);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor);
                } // end if secondary
                else {
                    if(useAlpha) primaryColor = Support.RGBToHex(colorTxt, green, blue, alpha);
                    else primaryColor = Support.RGBToHex(colorTxt, green, blue);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else

                redBar.setProgress(Integer.valueOf(colorTxt));
                appEditing = false;

                break;

            case FROM_GREEN:
                // first check if the other RGB values are valid (this doesn't get called if the green one is invalid)
                if(!Support.isValidRGB(redEdit.getText().toString().trim()) || !Support.isValidRGB(blueEdit.getText().toString().trim())) return;
                else if(useAlpha)
                    if(!Support.isValidRGB(alphaEdit.getText().toString().trim())) return;

                // if they are, get their values and set colors accordingly
                red = redEdit.getText().toString().trim();
                blue = blueEdit.getText().toString().trim();
                if(useAlpha) alpha = alphaEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    if(useAlpha) secondaryColor = Support.RGBToHex(red, colorTxt, blue, alpha);
                    else secondaryColor = Support.RGBToHex(red, colorTxt, blue);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor.substring(1));
                } // end if secondary
                else {
                    if(useAlpha) primaryColor = Support.RGBToHex(red, colorTxt, blue, alpha);
                    else primaryColor = Support.RGBToHex(red, colorTxt, blue);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else

                greenBar.setProgress(Integer.valueOf(colorTxt));
                appEditing = false;

                break;

            case FROM_BLUE:
                // first check if the other RGB values are valid (this does't get called if the blue one is invalid)
                if(!Support.isValidRGB(redEdit.getText().toString().trim()) || !Support.isValidRGB(greenEdit.getText().toString().trim())) return;
                else if(useAlpha)
                    if(!Support.isValidRGB(alphaEdit.getText().toString().trim())) return;

                // if they are, get their values and set colors accordingly
                red = redEdit.getText().toString().trim();
                green = greenEdit.getText().toString().trim();
                if(useAlpha) alpha = alphaEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    if(useAlpha) secondaryColor = Support.RGBToHex(red, green, colorTxt, alpha);
                    else secondaryColor = Support.RGBToHex(red, green, colorTxt);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor.substring(1));
                } // end if secondary
                else {
                    if(useAlpha) primaryColor = Support.RGBToHex(red, green, colorTxt, alpha);
                    else primaryColor = Support.RGBToHex(red, green, colorTxt);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else

                blueBar.setProgress(Integer.valueOf(colorTxt));
                appEditing = false;

                break;

            case FROM_ALPHA:
                // first check if the other RGB values are valid (this doesn't get called if the alpha one is invalid)
                if(!Support.isValidRGB(redEdit.getText().toString().trim()) || !Support.isValidRGB(greenEdit.getText().toString().trim()) || !Support.isValidRGB(blueEdit.getText().toString().trim())) return;

                // if they are, get their values and set colors accordingly
                red = redEdit.getText().toString().trim();
                green = greenEdit.getText().toString().trim();
                blue = blueEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    secondaryColor = Support.RGBToHex(red, green, blue, colorTxt);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor.substring(1));
                } // end if secondary
                else {
                    primaryColor = Support.RGBToHex(red, green, blue, colorTxt);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else

                alphaBar.setProgress(Integer.valueOf(colorTxt));
                appEditing = false;

                break;

            case FROM_CHANGE:
                // update all text fields
                // assuming colorTxt is a valid 6-digit (or 8-digit) hexadecimal string
                appEditing = true;

                hexEdit.setText(colorTxt);

                RGB = Support.hexToRGB(colorTxt.substring(1));
                redEdit.setText(RGB[0]);
                greenEdit.setText(RGB[1]);
                blueEdit.setText(RGB[2]);
                if(useAlpha) alphaEdit.setText(RGB[3]);

                redBar.setProgress(Integer.valueOf(RGB[0]));
                greenBar.setProgress(Integer.valueOf(RGB[1]));
                blueBar.setProgress(Integer.valueOf(RGB[2]));
                if(useAlpha) alphaBar.setProgress(Integer.valueOf(RGB[3]));
                appEditing = false;

                break;

            case FROM_ALPHA_CHANGE:
                // update the the hex field to add or remove the alpha channel
                // if present, colorTxt is the string in alphaEdit

                // first check if the other RGB values are valid (this doesn't get called if the alpha one is invalid)
                if(!Support.isValidRGB(redEdit.getText().toString().trim()) || !Support.isValidRGB(greenEdit.getText().toString().trim()) || !Support.isValidRGB(blueEdit.getText().toString().trim())) return;

                // now update color of display and text of hexEdit
                appEditing = true;

                if(useAlpha) {
                    if(secondary) {
                        secondaryColor = HASHTAG + Support.addAlphaToHex(secondaryColor.substring(1), colorTxt);
                        display.setTextColor(Color.parseColor(secondaryColor));
                        hexEdit.setText(secondaryColor.substring(1));
                    } // end if secondary
                    else {
                        primaryColor = HASHTAG + Support.addAlphaToHex(primaryColor.substring(1), colorTxt);
                        display.setBackgroundColor(Color.parseColor(primaryColor));
                        hexEdit.setText(primaryColor.substring(1));
                    } // end else

                    alphaBar.setProgress(Integer.valueOf(colorTxt));

                } // end if useAlpha
                else {
                    if(secondary) {
                        secondaryColor = HASHTAG + Support.removeAlphaFromHex(secondaryColor.substring(1));
                        display.setTextColor(Color.parseColor(secondaryColor));
                        hexEdit.setText(secondaryColor.substring(1));
                    } // end if secondary
                    else {
                        primaryColor = HASHTAG + Support.removeAlphaFromHex(primaryColor.substring(1));
                        display.setBackgroundColor(Color.parseColor(primaryColor));
                        hexEdit.setText(primaryColor.substring(1));
                    } // end else

                } // end else

                appEditing = false;

                break;

        } // end switch from

    } // end private void updateColor
    
    
    // listeners
    // OnClickListener for both buttons
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                
                case R.id.check_secondary:
                    secondary = !secondary;

                    // add or remove alpha views if necessary, and update useAlpha variable
                    if(secondary) {
                        useAlpha = alphaSecondary;
                        if(alphaSecondary && !alphaPrimary) rgbFields.addView(alphaField);
                        else if(!alphaSecondary && alphaPrimary) rgbFields.removeView(alphaField);
                    } // end if secondary
                    else {
                        useAlpha = alphaPrimary;
                        if(alphaPrimary && !alphaSecondary) rgbFields.addView(alphaField);
                        else if(!alphaPrimary && alphaSecondary) rgbFields.removeView(alphaField);
                    } // end else

                    alphaCheck.setChecked(useAlpha);

                    // update text fields
                    if(secondary) updateColor(FROM_CHANGE, secondaryColor);
                    else updateColor(FROM_CHANGE, primaryColor);
                    
                    break;

                case R.id.check_alpha:
                    String savedAlpha = "";
                    // change booleans related to alpha channel
                    useAlpha = !useAlpha;
                    if(secondary){
                        alphaSecondary = !alphaSecondary;
                        if(useAlpha) savedAlpha = savedSecondaryAlpha;
                    } // end if secondary
                    else {
                        alphaPrimary = !alphaPrimary;
                        if(useAlpha) savedAlpha = savedPrimaryAlpha;
                    } // end else

                    // update the color to add or remove the alpha value
                    updateColor(FROM_ALPHA_CHANGE, savedAlpha);

                    // add or remove EditText and SeekBar for alpha channel and save or restore the alpha value
                    if(useAlpha) {
                        rgbFields.addView(alphaField);
                        alphaEdit.setText(savedAlpha);
                    } // end if useAlpha
                    else {
                        if(secondary) savedSecondaryAlpha = alphaEdit.getText().toString().trim();
                        else savedPrimaryAlpha = alphaEdit.getText().toString().trim();
                        rgbFields.removeView(alphaField);
                    } // end else

                
            } // end switch view.getId
            
        } // end public void onClick
    } // end private class ButtonListener

    // TextWatcher for the EditTexts, will be subclassed for each EditText
    private abstract class TextListener implements TextWatcher {
        @Override
        public void afterTextChanged(Editable e) {} // just an empty method
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {} // just an empty method
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {} // just an empty method
    } // end private abstract class TextListener
    
    private class HexListener extends TextListener {
        @Override
        public void afterTextChanged(Editable e) {
            // do nothing if the app edited this
            if(appEditing) return;
            
            // check if entered color is a valid hex color code, return if it is not
            String hex = e.toString().trim();
            if(!Support.isValidHex(hex, useAlpha)) return;
            
            // update the colors
            updateColor(FROM_HEX, hex);            
        } // end public void afterTextChanged
    } // end private class HexListener
    
    private class RedListener extends TextListener {
        @Override
        public void afterTextChanged(Editable e) {
            // do nothing if the app edited this
            if(appEditing) return;
            
            // check if entered value is valid, return if it is not
            String red = e.toString().trim();
            if(!Support.isValidRGB(red)) return;
            
            // update the colors
            updateColor(FROM_RED, red);
        } // end public void afterTextChanged
    } // end private class RedListener
    
    private class GreenListener extends TextListener {
        @Override
        public void afterTextChanged(Editable e) {
            // do nothing if the app edited this
            if(appEditing) return;
            
            // check if entered value is valid, return if it is not
            String green = e.toString().trim();
            if(!Support.isValidRGB(green)) return;
            
            // update the colors
            updateColor(FROM_GREEN, green);
        } // end public void afterTextChanged
    } // end private class GreenListener
    
    private class BlueListener extends TextListener {
        @Override
        public void afterTextChanged(Editable e) {
            // do nothing if the app edited this
            if (appEditing) return;

            // check if entered value is valid, return if it is not
            String blue = e.toString().trim();
            if (!Support.isValidRGB(blue)) return;

            // update the colors
            updateColor(FROM_BLUE, blue);
        } // end public void afterTextChanged
    } // end private class BlueListener

    private class AlphaListener extends TextListener {
        @Override
        public void afterTextChanged(Editable e) {
            // do nothing if the app edited this
            if(appEditing) return;

            // check if the entered value is valid, return if it is not
            String alpha = e.toString().trim();
            if(!Support.isValidRGB(alpha)) return;

            // update the colors
            updateColor(FROM_ALPHA, alpha);
        } // end public void afterTextChanged
    } // end private class AlphaListener

    // SeekBarListener to listen to the seekbars
    private class SeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
            if(fromUser) {
                switch(bar.getId()) {
                    case R.id.bar_red:

                        redEdit.setText(String.valueOf(progress));
                        updateColor(FROM_RED, String.valueOf(progress));

                        break;

                    case R.id.bar_green:
                        greenEdit.setText(String.valueOf(progress));
                        updateColor(FROM_GREEN, String.valueOf(progress));

                        break;

                    case R.id.bar_blue:
                        blueEdit.setText(String.valueOf(progress));
                        updateColor(FROM_BLUE, String.valueOf(progress));

                        break;

                    case R.id.bar_alpha:
                        alphaEdit.setText(String.valueOf(progress));
                        updateColor(FROM_ALPHA, String.valueOf(progress));

                        break;

                } // end switch bar.getId
            } // end if fromUser
        } // end public void onProgressChanged

        @Override
        public void onStartTrackingTouch(SeekBar bar) {} // empty method

        @Override
        public void onStopTrackingTouch(SeekBar bar) {} // empty method

    } // end private class SeekBarListener
    
} // end public class Main
