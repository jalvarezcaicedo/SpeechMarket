package com.personal.jalvarez.speechmarket;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MarketActivity extends AppCompatActivity {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private FloatingActionButton speakButton;
    private TableLayout marketTable;
    private CoordinatorLayout contentCoord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
    }

    private void initUI() {
        marketTable = (TableLayout) findViewById(R.id.table_data_market);
        contentCoord = (CoordinatorLayout) findViewById(R.id.content_market);
        speakButton = (FloatingActionButton) findViewById(R.id.fab);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askSpeechInput();
            }
        });
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "¿Cual es tu siguiente producto?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    addProduct(result.get(0));
                }
                break;
            }

        }
    }

    private void addProduct(String dataUnfiltered) {
        Map.Entry<String, String> dataFiltered = getProductPrice(dataUnfiltered);
        if (dataFiltered != null)
            marketTable.addView(newTableRow(dataFiltered.getKey(), dataFiltered.getValue()));
        else
            Snackbar.make(contentCoord, getString(R.string.capture_failed), Snackbar.LENGTH_SHORT).show();
    }

    private Map.Entry<String, String> getProductPrice(String dataUnfiltered) {
        if (dataUnfiltered.contains("$")) {
            String[] dataFiltered = dataUnfiltered.split("\\$");
            if (!dataFiltered[0].isEmpty())
                return new AbstractMap.SimpleEntry<>(dataFiltered[0], dataFiltered[1]);

            Snackbar.make(contentCoord, getString(R.string.capture_prod_fail), Snackbar.LENGTH_SHORT).show();
        }
        return null;
    }


    private TableRow newTableRow(String product, String price) {
        final TableRow tbRow = new TableRow(this);
        TextView tvProd = new TextView(this);
        tvProd.setText(product);
        tvProd.setGravity(Gravity.CENTER);
        tbRow.addView(tvProd);
        TextView tvPrice = new TextView(this);
        tvPrice.setText(price);
        tvPrice.setGravity(Gravity.CENTER);
        tbRow.addView(tvPrice);
        ImageButton imbDelete = new ImageButton(this);
        imbDelete.setImageResource(R.drawable.ic_delete);
        imbDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marketTable.removeView(tbRow);
            }
        });
        tbRow.addView(imbDelete);

        return tbRow;
    }
}
