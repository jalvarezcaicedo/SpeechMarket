package com.personal.jalvarez.speechmarket;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class MarketActivity extends AppCompatActivity {
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private ArrayList<Integer> imageList;
    private ProductAdapter productAdapter;
    private RecyclerView recyclerProducts;
    private TextView emptyView;
    private ArrayList<Product> products;
    private CoordinatorLayout contentCoord;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initUI();
    }

    private void initUI() {
        products = new ArrayList<>();
        index = 0;
        imageList = new ArrayList<>();
        int[] imgTypes = {R.mipmap.ic_various, R.mipmap.ic_groceries, R.mipmap.ic_cleaner};
        for (int imgType : imgTypes) {
            imageList.add(imgType);
        }

        emptyView = (TextView) findViewById(R.id.empty_view);
        recyclerProducts = (RecyclerView) findViewById(R.id.recycler_products);
        LinearLayoutManager llm = new LinearLayoutManager(MarketActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerProducts.setLayoutManager(llm);

        verifyVisibilityDataState();

        contentCoord = (CoordinatorLayout) findViewById(R.id.content_market);
        productAdapter = new ProductAdapter(products, MarketActivity.this);
        productAdapter.notifyDataSetChanged();
        recyclerProducts.setAdapter(productAdapter);

        FloatingActionButton speakButton = (FloatingActionButton) findViewById(R.id.fab);
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askSpeechInput();
            }
        });
    }

    private boolean verifyVisibilityDataState() {
        if (products.size() > 0) {
            recyclerProducts.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            return true;
        } else {
            recyclerProducts.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            return false;
        }
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
        if (dataFiltered != null) {
            showCustomSelectorType(dataFiltered);
        } else
            Snackbar.make(contentCoord, getString(R.string.capture_failed), Snackbar.LENGTH_SHORT).show();
    }

    private void showCustomSelectorType(final Map.Entry<String, String> dataFiltered) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MarketActivity.this);
        final LayoutInflater inflater = MarketActivity.this.getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.dialog_type_selector, null);
        dialogBuilder.setView(dialogView);

        final ImageSwitcher switcher = dialogView.findViewById(R.id.img_type_switcher);
        final ImageView imgNext = dialogView.findViewById(R.id.img_next_type);
        final ImageView imgPrev = dialogView.findViewById(R.id.img_previous_type);

        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(MarketActivity.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                return imageView;
            }
        });
        switcher.setImageResource(imageList.get(index));

        imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switcher.setImageResource(getImageTypeResource(true));
            }
        });

        imgPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switcher.setImageResource(getImageTypeResource(false));
            }
        });

        dialogBuilder.setMessage(getString(R.string.select_type));
        dialogBuilder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                products.add(new Product(imageList.get(index), dataFiltered.getKey(), Long.parseLong(dataFiltered.getValue()), 1));
                productAdapter.updateList();
                verifyVisibilityDataState();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private Integer getImageTypeResource(boolean option) {
        if (option && index < (imageList.size() - 1)) {
            index += 1;
            return imageList.get(index);
        } else if (option && index == (imageList.size() - 1)) {
            index = 0;
            return imageList.get(index);
        } else if (!option && index > 0) {
            index -= 0;
            return imageList.get(index);
        } else {
            index = imageList.size() - 1;
        }

        return imageList.get(index);
    }

    private Map.Entry<String, String> getProductPrice(String dataUnfiltered) {
        String[] dataFiltered = dataUnfiltered.split("\\$");
        if (dataUnfiltered.contains("$") && !dataFiltered[0].isEmpty())
            return new AbstractMap.SimpleEntry<>(dataFiltered[0], dataFiltered[1]);

        Snackbar.make(contentCoord, getString(R.string.capture_prod_fail), Snackbar.LENGTH_SHORT).show();

        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_market, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                Log.e(getClass().getSimpleName(), "SAVE DATA");
                return true;
            case R.id.action_clean:
                productAdapter.clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
