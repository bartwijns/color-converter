package barryprojects.colorconverter;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Main extends AppCompatActivity {

    // UI elements
    private EditText hexEdit;
    private EditText redEdit;
    private EditText greenEdit;
    private EditText blueEdit;
    private TextView display;
    private CheckBox secondaryCheck;
    private Button pickerButton;
    
    // variables to store colors
    private String primaryColor;
    private String secondaryColor;
    
    // some booleans
    private boolean appEditing = false; // true when app is editing text fields
    private boolean secondary = false; // true when user is picking the secondary color
    
    // constants for the updateColor function to indicate the updated component
    private static final int FROM_HEX = 0;
    private static final int FROM_RED = 1;
    private static final int FROM_GREEN = 2;
    private static final int FROM_BLUE = 3;
    private static final int FROM_DLG = 4;
    private static final int FROM_CHANGE = 5;

    // request constants for launching other activities
    private static final int REQUEST_COLOR = 1;

    // number sign, to be put in front of every hex color code
    public static final String HASHTAG = "#";

    // keys to identify data stored in bundles
    private static final String COLOR_PRIMARY = "primary color";
    private static final String COLOR_SECONDARY = "secondary color";
    private static final String SECONDARY_STATE = "checkbox state";
    
    
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
        
        display = (TextView) findViewById(R.id.disp_color);
        secondaryCheck = (CheckBox) findViewById(R.id.check_secondary);
        pickerButton = (Button) findViewById(R.id.button_picker);
        
        // setting their listeners
        hexEdit.addTextChangedListener(new HexListener());
        redEdit.addTextChangedListener(new RedListener());
        greenEdit.addTextChangedListener(new GreenListener());
        blueEdit.addTextChangedListener(new BlueListener());

        ButtonListener listener = new ButtonListener();
        secondaryCheck.setOnClickListener(listener);
        pickerButton.setOnClickListener(listener);

        // initialize colors to their default values
        primaryColor = HASHTAG + getString(R.string.default_hex);
        secondaryColor = HASHTAG + getString(R.string.default_hex);
        
    } // end protected void onCreate

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        if(result == Activity.RESULT_OK) {
            switch(request) {

                case REQUEST_COLOR:
                    updateColor(FROM_DLG, data.getStringExtra("com.barryprojects.colorconverter.color"));

                    break;

            } // end switch request

        } // end if result

    } // end protected void onActivityResult

    // save and restore instance states
    // screen rotation should be disabled, but there are more cases where this might be needed
    // also, I plan to implement an alternative landscape layout and will need these when (if) I do
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(COLOR_PRIMARY, primaryColor);
        savedInstanceState.putString(COLOR_SECONDARY, secondaryColor);
        savedInstanceState.putBoolean(SECONDARY_STATE, secondaryCheck.isChecked());

        super.onSaveInstanceState(savedInstanceState);
    } // end public void onSaveInstanceState

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        primaryColor = savedInstanceState.getString(COLOR_PRIMARY);
        secondaryColor = savedInstanceState.getString(COLOR_SECONDARY);
        secondary = savedInstanceState.getBoolean(SECONDARY_STATE);

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

        String[] RGB;

        switch(from) {
            case FROM_HEX:
                RGB = Support.hexToRGB(colorTxt);
                appEditing = true;

                redEdit.setText(RGB[0]);
                greenEdit.setText(RGB[1]);
                blueEdit.setText(RGB[2]);

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

                // if they are, get their values and set colors accordingly
                green = greenEdit.getText().toString().trim();
                blue = blueEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    secondaryColor = Support.RGBToHex(colorTxt, green, blue);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor);
                } // end if secondary
                else {
                    primaryColor = Support.RGBToHex(colorTxt, green, blue);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else
                appEditing = false;

                break;

            case FROM_GREEN:
                // first check if the other RGB values are valid (this doesn't get called if the green one is invalid)
                if(!Support.isValidRGB(redEdit.getText().toString().trim()) || !Support.isValidRGB(blueEdit.getText().toString().trim())) return;

                // if they are, get their values and set colors accordingly
                red = redEdit.getText().toString().trim();
                blue = blueEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    secondaryColor = Support.RGBToHex(red, colorTxt, blue);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor.substring(1));
                } // end if secondary
                else {
                    primaryColor = Support.RGBToHex(red, colorTxt, blue);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else
                appEditing = false;

                break;

            case FROM_BLUE:
                // first check if the other RGB values are valid (this does't get called if the blue one is invalid)
                if(!Support.isValidRGB(redEdit.getText().toString().trim()) || !Support.isValidRGB(greenEdit.getText().toString().trim())) return;

                // if they are, get their values and set colors accordingly
                red = redEdit.getText().toString().trim();
                green = greenEdit.getText().toString().trim();

                appEditing = true;
                if(secondary) {
                    secondaryColor = Support.RGBToHex(red, green, colorTxt);
                    display.setTextColor(Color.parseColor(secondaryColor));
                    hexEdit.setText(secondaryColor.substring(1));
                } // end if secondary
                else {
                    primaryColor = Support.RGBToHex(red, green, colorTxt);
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                    hexEdit.setText(primaryColor.substring(1));
                } // end else
                appEditing = false;

                break;

            case FROM_DLG:
                // here, everything will be edited
                // first, the color screen
                if(secondary) {
                    secondaryColor = colorTxt;
                    display.setTextColor(Color.parseColor(secondaryColor));
                } // end if secondary
                else {
                    primaryColor = colorTxt;
                    display.setBackgroundColor(Color.parseColor(primaryColor));
                } // end else

                // now edit the text fields
                appEditing = true;

                hexEdit.setText(colorTxt.substring(1));
                RGB = Support.hexToRGB(colorTxt.substring(1));

                redEdit.setText(RGB[0]);
                greenEdit.setText(RGB[1]);
                blueEdit.setText(RGB[2]);

                appEditing = false;

                break;

            case FROM_CHANGE:
                // update all text fields
                // assuming colorTxt is a valid 6-digit hexadecimal string
                appEditing = true;

                hexEdit.setText(colorTxt);

                RGB = Support.hexToRGB(colorTxt.substring(1));
                redEdit.setText(RGB[0]);
                greenEdit.setText(RGB[1]);
                blueEdit.setText(RGB[2]);

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
                case R.id.button_picker:
                    // color picker dialog does not work in landscape mode
                    if(Main.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        Log.d("mymessage", "app is in landscape mode");
                        Support.showToast(getApplicationContext(), R.string.err_picker_orientation);
                        return;
                    } // end if orientation
                    // show the color picker dialog
                    Intent pickerIntent = new Intent(getApplicationContext(), ColorPickerActivity.class);
                    if(secondary) pickerIntent.putExtra("com.barryprojects.colorconverter.color", secondaryColor);
                    else pickerIntent.putExtra("com.barryprojects.colorconverter.color", primaryColor);
                    startActivityForResult(pickerIntent, REQUEST_COLOR);

                    break;
                
                case R.id.check_secondary:
                    secondary = !secondary;

                    // also update text fields
                    if(secondary) updateColor(FROM_CHANGE, secondaryColor);
                    else updateColor(FROM_CHANGE, primaryColor);
                    
                    break;
                
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
            if(!Support.isValidHex(hex)) return;
            
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
    
} // end public class Main
