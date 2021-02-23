package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    TextView firstCityTemp, secondCityTemp;
    Spinner firstCitySelect, secondCitySelect;
    OkHttpClient client = new OkHttpClient();
    public static final String API_KEY = "68b6a7f8992316297289257461ea82a0";
    String[] currencies = { "EUR", "KZT", "USD", "RUB", "UAH" };
    Spinner firstCurrency, secondCurrency;
    ArrayAdapter<?> firstCurrencyAdapter, secondCurrencyAdapter;
    int firstSelectedCurrency = 0, secondSelectedCurrency = 0;
    EditText firstAmount, secondAmount;
    RadioGroup radioGroup1, radioGroup2;
    double firstTemp, secondTemp;
    int firstUnit = 0, secondUnit = 0;
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
                if (s.length() == 0){
                    secondAmount.setText("0");
                    return;
                }
                double final_amount_in_tenge = getTenge(firstSelectedCurrency) * Double.parseDouble(String.valueOf(s));
                String result = "";
                if (getTenge(firstSelectedCurrency) <= getTenge(secondSelectedCurrency)){
                    result = String.format("%.3f", (double) final_amount_in_tenge / getTenge(secondSelectedCurrency));
                }else{
                    result = String.format("%.3f", (double) final_amount_in_tenge * getTenge(secondSelectedCurrency));
                }
                secondAmount.setText(result);
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
                firstAmount.setText("");
                secondAmount.setText("");
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
                firstAmount.setText("");
                secondAmount.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                if (checkedRadioButton.getText().toString().equals("Celsius")){
                    firstCityTemp.setText(String.format("%.1f", firstTemp) + "°C");
                    firstUnit = 0;
                }else if(checkedRadioButton.getText().toString().equals("Fahrenheit")){
                    firstUnit = 1;
                    firstCityTemp.setText(String.format("%.1f", celsiusToFahrenheit(firstTemp)) + "°F");
                }else{
                    firstUnit = 2;
                    firstCityTemp.setText(firstTemp + 273.15 + "K");
                }
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                if (checkedRadioButton.getText().toString().equals("Celsius")){
                    firstUnit = 0;
                    secondCityTemp.setText(String.format("%.1f", secondTemp) + "°C");
                }else if(checkedRadioButton.getText().toString().equals("Fahrenheit")){
                    firstUnit = 1;
                    secondCityTemp.setText(String.format("%.1f", celsiusToFahrenheit(secondTemp)) + "°F");
                }else{
                    firstUnit = 2;
                    secondCityTemp.setText(secondTemp + 273.15 + "K");
                }
            }
        });
        updateTemp1("1520240");
        updateTemp2("1520240");
        firstCitySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cityId = getCityId(position);
                updateTemp1(cityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        secondCitySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cityId = getCityId(position);
                updateTemp2(cityId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private double celsiusToFahrenheit(double celsius){
        return (9.0/5.0) * celsius + 32;
    }

    private void updateTemp1(String cityId){
        Request request2 = new Request.Builder()
                .url(getPath(cityId))
                .get()
                .build();
        client.newCall(request2).enqueue(new Callback() {
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
                            try{
                                JSONObject obj = new JSONObject(myResponse);
                                JSONObject main = obj.getJSONObject("main");
                                firstTemp = main.getDouble("temp") - 273.15;
                                double temp;
                                String unit;
                                if (firstUnit == 0){
                                    temp = main.getDouble("temp") - 273.15;
                                    unit = "°C";
                                }else if(firstUnit == 1){
                                    temp = celsiusToFahrenheit(main.getDouble("temp") - 273.15);
                                    unit = "°F";
                                }else{
                                    temp = main.getDouble("temp");
                                    unit = "K";
                                }
                                firstCityTemp.setText(String.format("%.1f", temp) + unit);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }

    private void updateTemp2(String cityId){
        Request request2 = new Request.Builder()
                .url(getPath(cityId))
                .get()
                .build();
        client.newCall(request2).enqueue(new Callback() {
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
                            try{
                                JSONObject obj = new JSONObject(myResponse);
                                JSONObject main = obj.getJSONObject("main");
                                secondTemp = main.getDouble("temp") - 273.15;
                                double temp;
                                String unit;
                                if (secondUnit == 0){
                                    temp = main.getDouble("temp") - 273.15;
                                    unit = "°C";
                                }else if(secondUnit == 1){
                                    temp = celsiusToFahrenheit(main.getDouble("temp") - 273.15);
                                    unit = "°F";
                                }else{
                                    temp = main.getDouble("temp");
                                    unit = "K";
                                }
                                secondCityTemp.setText(String.format("%.1f", temp) + unit);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }
        });
    }

    private String getCityId(int position){
        switch (position){
            case 1:
                return "1526273";
            case 2:
                return "1526384";
            case 3:
                return "609655";
            case 4:
                return "610611";
            default: return "1520240";

        }
    }

    private String getPath(String id){
        return "http://api.openweathermap.org/data/2.5/weather?id=" + id + "&appid=" + API_KEY;
    }

    private double getTenge(int currency){
        switch (currency){
            case 0:
                return 504.78;
            case 2:
                return 415.50;
            case 3:
                return 5.60;
            case 4:
                return 14.94;
            default: return 1.0;
        }
    }

    private void initialization(){
        firstCitySelect = findViewById(R.id.firstCitySelect);
        secondCitySelect = findViewById(R.id.secondCitySelect);
        firstCityTemp = findViewById(R.id.firstCityTemp);
        secondCityTemp = findViewById(R.id.secondCityTemp);
        firstCurrency = findViewById(R.id.firstCurrency);
        secondCurrency = findViewById(R.id.secondCurrency);
        firstAmount = findViewById(R.id.firstAmount);
        secondAmount = findViewById(R.id.secondAmount);
        radioGroup1 = findViewById(R.id.firstTempUnit);
        radioGroup2 = findViewById(R.id.secondTempUnit);
    }
}