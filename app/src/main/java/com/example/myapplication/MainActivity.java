package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView firstCityName, secondCityName, firstCityTemp, secondCityTemp;
    OkHttpClient client = new OkHttpClient();
    public static final String API_KEY = "68b6a7f8992316297289257461ea82a0";
    String[] currencies = { "EUR", "KZT", "USD", "RUB", "UAH"};
    Spinner firstCurrency, secondCurrency;
    ArrayAdapter<?> firstCurrencyAdapter, secondCurrencyAdapter;
    int firstSelectedCurrency = 0, secondSelectedCurrency = 0;
    EditText firstAmount, secondAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
        firstCurrencyAdapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        secondCurrencyAdapter = ArrayAdapter.createFromResource(this, R.array.currencies, android.R.layout.simple_spinner_item);
        firstCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondCurrencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstCurrency.setAdapter(firstCurrencyAdapter);
        secondCurrency.setAdapter(secondCurrencyAdapter);
        firstAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //secondAmount.setText();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        firstCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstSelectedCurrency = position;
                Toast.makeText(MainActivity.this, currencies[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondSelectedCurrency = position;
                Toast.makeText(MainActivity.this, currencies[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Request request = new Request.Builder()
                .url(getPath("1526384"))
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.fillInStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()){
                    final String myResponse = Objects.requireNonNull(response.body()).string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Best response " + myResponse);
                        }
                    });

                }
            }
        });
    }

    private String getPath(String id){
        return "http://api.openweathermap.org/data/2.5/weather?id=" + id + "&appid=" + API_KEY;
    }

//    private double getCurrencyAmount(){
//        if (firstSelectedCurrency == 0 && secondSelectedCurrency == 1){
//
//        }
//    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }

    private void initialization(){
        firstCityName = findViewById(R.id.firstCityName);
        secondCityName = findViewById(R.id.secondCityName);
        firstCityTemp = findViewById(R.id.firstCityTemp);
        secondCityTemp = findViewById(R.id.secondCityTemp);
        firstCurrency = findViewById(R.id.firstCurrency);
        secondCurrency = findViewById(R.id.secondCurrency);
        firstAmount = findViewById(R.id.firstAmount);
        secondAmount = findViewById(R.id.secondAmount);
    }
}