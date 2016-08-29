package barryprojects.colorconverter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

public class ColorPickerActivity extends AppCompatActivity {
    // servers solely for the purpose of creating and showing the color picker dialog
    // is a separate activity because I want to try and create this dialog myself eventually

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String color = getIntent().getStringExtra("com.barryprojects.colorconverter.color");

        ChromaDialog.Builder builder = new ChromaDialog.Builder();
        builder.colorMode(ColorMode.RGB);
        builder.initialColor(Color.parseColor(color));
        builder.indicatorMode(IndicatorMode.DECIMAL);
        builder.onColorSelected(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                // convert the integer to a color and return it to the main activity
                // code snippet found on stackOverflow
                String hex = String.format("#%06X", (0xFFFFFF) & color);

                Intent intent = new Intent();
                intent.putExtra("com.barryprojects.colorconverter.color", hex);
                setResult(Activity.RESULT_OK, intent);
                finish();
            } // end public void onColorSelected
        }); // end anonymous OnColorSelectedListener

        builder.create().show(getSupportFragmentManager(), getString(R.string.picker));

    } // end protected void onCreate

} // end public class ColorPickerActivity
