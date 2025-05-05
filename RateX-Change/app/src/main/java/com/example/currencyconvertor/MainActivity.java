package com.example.currencyconvertor;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText amountEditText;
    private Spinner sourceCurrencySpinner, targetCurrencySpinner;
    private Button convertButton;
    private ImageButton swapButton;
    private TextView resultTextView, exchangeRateTextView, lastUpdatedTextView;
    private SwitchMaterial themeSwitch;

    private Map<String, Double> exchangeRates;
    private List<String> currencies;

    // Preferences for theme
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "currency_converter_prefs";
    private static final String THEME_KEY = "dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load theme before setting content view
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean(THEME_KEY, false);
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI elements
        amountEditText = findViewById(R.id.amountEditText);
        sourceCurrencySpinner = findViewById(R.id.sourceCurrencySpinner);
        targetCurrencySpinner = findViewById(R.id.targetCurrencySpinner);
        convertButton = findViewById(R.id.convertButton);
        swapButton = findViewById(R.id.swapButton);
        resultTextView = findViewById(R.id.resultTextView);
        exchangeRateTextView = findViewById(R.id.exchangeRateTextView);
        lastUpdatedTextView = findViewById(R.id.lastUpdatedTextView);
        themeSwitch = findViewById(R.id.themeSwitch);

        // Set theme switch initial state based on preferences
        themeSwitch.setChecked(isDarkTheme);

        // Initialize exchange rates
        initializeExchangeRates();

        // Initialize currency list
        initializeCurrencies();

        // Set up the spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceCurrencySpinner.setAdapter(adapter);
        targetCurrencySpinner.setAdapter(adapter);

        // Set default selections
        sourceCurrencySpinner.setSelection(currencies.indexOf("USD"));
        targetCurrencySpinner.setSelection(currencies.indexOf("EUR"));

        // Set the last updated text
        updateLastUpdatedText();

        // Set up spinner listeners to show the exchange rate
        setupSpinnerListeners();

        // Set the click listener for the Convert button
        convertButton.setOnClickListener(v -> performConversion());

        // Set the click listener for the swap button
        swapButton.setOnClickListener(v -> swapCurrencies());

        // Set up theme switch listener
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            saveThemePreference(isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            // Activity will be recreated when theme changes
        });
    }

    private void saveThemePreference(boolean isDarkTheme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(THEME_KEY, isDarkTheme);
        editor.apply();
    }

    private void initializeExchangeRates() {
        exchangeRates = new HashMap<>();
        // Base currency pairs
        exchangeRates.put("USD_EUR", 0.88);
        exchangeRates.put("EUR_USD", 1.14);
        exchangeRates.put("USD_GBP", 0.75);
        exchangeRates.put("GBP_USD", 1.33);
        exchangeRates.put("USD_JPY", 142.38);
        exchangeRates.put("JPY_USD", 0.0070);
        exchangeRates.put("USD_CAD", 1.38);
        exchangeRates.put("CAD_USD", 0.72);
        exchangeRates.put("USD_AUD", 1.57);
        exchangeRates.put("AUD_USD", 0.64);
        exchangeRates.put("USD_CHF", 0.82);
        exchangeRates.put("CHF_USD", 1.22);
        exchangeRates.put("USD_CNY", 7.30);
        exchangeRates.put("CNY_USD", 0.14);
        exchangeRates.put("USD_INR", 85.39);
        exchangeRates.put("INR_USD", 0.012);

        // EUR pairs
        exchangeRates.put("EUR_GBP", 0.86);
        exchangeRates.put("GBP_EUR", 1.17);
        exchangeRates.put("EUR_JPY", 161.89);
        exchangeRates.put("JPY_EUR", 0.0062);
        exchangeRates.put("EUR_CAD", 1.57);
        exchangeRates.put("CAD_EUR", 0.64);
        exchangeRates.put("EUR_AUD", 1.78);
        exchangeRates.put("AUD_EUR", 0.56);
        exchangeRates.put("EUR_CHF", 0.93);
        exchangeRates.put("CHF_EUR", 1.07);
        exchangeRates.put("EUR_CNY", 8.30);
        exchangeRates.put("CNY_EUR", 0.12);
        exchangeRates.put("EUR_INR", 97.11);
        exchangeRates.put("INR_EUR", 0.01);

        // Additional common pairs
        exchangeRates.put("GBP_JPY", 188.83);
        exchangeRates.put("JPY_GBP", 0.0053);
        exchangeRates.put("GBP_CAD", 1.84);
        exchangeRates.put("CAD_GBP", 0.54);
        exchangeRates.put("GBP_AUD", 2.08);
        exchangeRates.put("AUD_GBP", 0.48);
    }

    private void initializeCurrencies() {
        currencies = new ArrayList<>();
        currencies.add("USD"); // US Dollar
        currencies.add("EUR"); // Euro
        currencies.add("GBP"); // British Pound
        currencies.add("JPY"); // Japanese Yen
        currencies.add("CAD"); // Canadian Dollar
        currencies.add("AUD"); // Australian Dollar
        currencies.add("CHF"); // Swiss Franc
        currencies.add("CNY"); // Chinese Yuan
        currencies.add("INR"); // Indian Rupee
    }

    private void setupSpinnerListeners() {
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateExchangeRateDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        };

        sourceCurrencySpinner.setOnItemSelectedListener(itemSelectedListener);
        targetCurrencySpinner.setOnItemSelectedListener(itemSelectedListener);
    }

    private void updateExchangeRateDisplay() {
        String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
        String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();

        if (sourceCurrency.equals(targetCurrency)) {
            exchangeRateTextView.setText(String.format("1 %s = 1 %s", sourceCurrency, targetCurrency));
            return;
        }

        String exchangeKey = sourceCurrency + "_" + targetCurrency;
        if (exchangeRates.containsKey(exchangeKey)) {
            double exchangeRate = exchangeRates.get(exchangeKey);
            exchangeRateTextView.setText(String.format("1 %s = %.4f %s", sourceCurrency, exchangeRate, targetCurrency));
        } else {
            exchangeRateTextView.setText("Exchange rate not available");
        }
    }

    private void updateLastUpdatedText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(new Date());
        lastUpdatedTextView.setText("Last updated: " + formattedDate);
    }

    private void swapCurrencies() {
        int sourcePosition = sourceCurrencySpinner.getSelectedItemPosition();
        int targetPosition = targetCurrencySpinner.getSelectedItemPosition();

        sourceCurrencySpinner.setSelection(targetPosition);
        targetCurrencySpinner.setSelection(sourcePosition);

        // Perform conversion with swapped currencies if amount is already entered
        if (!amountEditText.getText().toString().isEmpty()) {
            performConversion();
        }
    }

    private void performConversion() {
        String amountText = amountEditText.getText().toString();
        if (!amountText.isEmpty()) {
            double amount = Double.parseDouble(amountText);
            String sourceCurrency = sourceCurrencySpinner.getSelectedItem().toString();
            String targetCurrency = targetCurrencySpinner.getSelectedItem().toString();

            // If same currency, the rate is 1:1
            if (sourceCurrency.equals(targetCurrency)) {
                resultTextView.setText(String.format(Locale.getDefault(), "%.2f %s", amount, targetCurrency));
                return;
            }

            String exchangeKey = sourceCurrency + "_" + targetCurrency;
            if (exchangeRates.containsKey(exchangeKey)) {
                double exchangeRate = exchangeRates.get(exchangeKey);
                double convertedAmount = amount * exchangeRate;
                resultTextView.setText(String.format(Locale.getDefault(), "%.2f %s", convertedAmount, targetCurrency));
            } else {
                resultTextView.setText("Exchange rate not available");
            }
        } else {
            resultTextView.setText("Please enter a valid amount");
        }
    }
}