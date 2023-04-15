/*
 * Copyright (C) 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.fit.samples.stepcounter;

import static java.text.DateFormat.getDateInstance;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogView;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fit.samples.stepcounter.databinding.ActivityMainBinding;
import com.google.android.gms.fit.samples.stepcounter.databinding.CustomDialogStepsgoalBinding;
import com.google.android.gms.fit.samples.stepcounter.databinding.InformationActivityBinding;
import com.google.android.gms.fit.samples.stepcounter.databinding.LayoutLinearCustomDialog2Binding;
import com.google.android.gms.fit.samples.stepcounter.databinding.LayoutLinearCustomDialog3Binding;
import com.google.android.gms.fit.samples.stepcounter.databinding.LayoutLinearCustomDialogBinding;
import com.google.android.gms.fit.samples.stepcounter.databinding.ProfileActivityBinding;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Goal;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
import com.google.android.gms.fitness.request.GoalsReadRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements OnSuccessListener {

    /*

        Variabeln der Klasse MainActivity

     */

    private FitnessDataModel fitnessDataModel;
    private ProfileActivityBinding p_binding;
    private InformationActivityBinding i_binding;
    private GoogleSignInAccount account;
    private DatePickerDialog datePickerDialog;

    private FitnessOptions fitnessOptions;

    public static final String TAG = "StepCounter";
    private static final int MY_PERMISSIONS_REQUEST = 1;

    // Variablen zum Senden von Daten
    private double stepsGoal;
    private float heightDelta;
    private int weightDelta;
    private int weightDelta1;
    private float weightDeltaResult;
    private String weightDeltaString;
    private int totalStepsGoal;
    private String gender;

    // Erstellete custom data DatenTypen
    private DataType dataTypeGender;

    // Variabeln für Prozesse
    int heightInt;
    int weightInt;
    int weightInt1;
    double totalStepsValue;
    private static int dailyTotalSteps;


    // Variabeln für Wöchentliche Schritte
    private static int countWalkDay = 0;
    // Wir zählen erneut rückwärts, also 7,6,5,4,3,2,1
    private static int weeklySteps1;
    private static int weeklySteps2;
    private static int weeklySteps3;
    private static int weeklySteps4;
    private static int weeklySteps5;
    private static int weeklySteps6;
    private static int weeklySteps7;


    // Variabeln für tägliche Ziele
    private double MondayGoal;
    private double TuesdayGoal;
    private double WednesdayGoal;
    private double ThursdayGoal;
    private double FridayGoal;
    private double SaturdayGoal;
    private double SundayGoal;

    // Prozentberechnung für das anzeigen des Symbol für das erreichen des Ziels
    private double goalPercent1;
    private double goalPercent2;
    private double goalPercent3;
    private double goalPercent4;
    private double goalPercent5;
    private double goalPercent6;
    private double goalPercent7;

    // Variabeln für Tagzuweisung bei Zielen
    private String MO = "MO";
    private String DI = "DI";
    private String MI = "MI";
    private String DO = "DO";
    private String FR = "FR";
    private String SA = "SA";
    private String SO = "SO";

    //Bundle-Objekte und Intents für CardViews
    Bundle cardBundle;
    Intent cardIntent;

    //Shared Prefs -
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MondayGoalValue = "mGoal";
    public static final String TuesdayGoalValue = "tGoal";
    public static final String WednesdayGoalValue = "wGoal";
    public static final String ThursdayGoalValue = "thGoal";
    public static final String FridayGoalValue = "fGoal";
    public static final String SaturdayGoalValue = "sGoal";
    public static final String SundayGoalValue = "soGoal";

    // Variabeln die das Datum und den Tag etc. betreffen.
    private String monthName;
    private String dayWeekText;
    int dayOfMonth;

    private String monthName1;
    private String dayWeekText1;
    int dayOfMonth1;

    private String monthName2;
    private String dayWeekText2;
    int dayOfMonth2;

    private String monthName3;
    private String dayWeekText3;
    int dayOfMonth3;

    private String monthName4;
    private String dayWeekText4;
    int dayOfMonth4;

    private String monthName5;
    private String dayWeekText5;
    int dayOfMonth5;

    private String monthName6;
    private String dayWeekText6;
    int dayOfMonth6;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* DataBinding von Widgets, Erstellung von OnClicklistener, Permissions überprüfen  */

        i_binding = DataBindingUtil.setContentView(this, R.layout.information_activity);

        initDatePicker();
        fitnessDataModel = new FitnessDataModel();

        // Ausgewähltes Menüitem hervorheben
        Menu menu = i_binding.bottomNav.getMenu();
        MenuItem menuitem = menu.getItem(0);
        menuitem.setChecked(true);

        // Menü Navigation
        i_binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.person:
                        Intent intent_profile = new Intent(getApplicationContext(), Profile_Activity.class);
                        startActivity(intent_profile);
                        return true;
                }
                return true;
            }
        });

        // Erster Tag in der Cardview - rechts nach links eingetragen
        i_binding.cardviewSaturday.setOnClickListener(v -> {
           startBarGraph1();
        });

        // Zweiter Tag in der Cardview - rechts nach links eingetragen
        i_binding.cardviewFriday.setOnClickListener(v ->{
            startBarGraph2();
        });

        // Dritter Tag in der CardView - rechts nach links eingetragen
        i_binding.cardviewThursday.setOnClickListener(v ->{
            startBarGraph3();
        });

        // Vierter Tag in der CardView - rechts nach links eingetragen
        i_binding.cardviewWednesday.setOnClickListener(v -> {
            startBarGraph4();
        });

        // Fünfter Tag in der CardView - rechts nach links eingetragen
        i_binding.cardviewTuesday.setOnClickListener(v -> {
            startBarGraph5();
        });

        // Sechster Tag in der CardView - rechts nach links eingetragen
        i_binding.cardviewMonday.setOnClickListener(v -> {
            startBarGraph6();
        });

        // Siebter Tag in der CardView - rechts nach links eingetragen
        i_binding.cardviewSunday.setOnClickListener(v -> {
            startBarGraph7();
        });


        initializeLogging();

        if (ContextCompat.checkSelfPermission(this, String.valueOf(Permission.Permission_Array)) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
            checkGoogleFitPermission();
        }
    }


    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                p_binding.txtINBirthdayET.setText(date);
            }
        };

        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private String makeDateString(int day, int month, int year) {
        return day + "." + getMonthFormat(month) + "." + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "01";
        if (month == 2)
            return "02";
        if (month == 3)
            return "03";
        if (month == 4)
            return "04";
        if (month == 5)
            return "05";
        if (month == 6)
            return "06";
        if (month == 7)
            return "07";
        if (month == 8)
            return "08";
        if (month == 9)
            return "09";
        if (month == 10)
            return "10";
        if (month == 11)
            return "11";
        if (month == 12)
            return "12";

        // Sollte nie passieren
        return "01";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }


    // - Öffne die Seite mit den Graphen für den ersten Eintrag in der CardView - Eintragung ist rechts nach links
    private void startBarGraph1() {
        if (i_binding.pbSaturdaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSaturdaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("TuesdayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSaturdaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSaturdaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("ThursdayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSaturdaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSaturdaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSaturdaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day1BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay1", dailyTotalSteps);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }


    // - Öffne die Seite mit den Graphen für den zweiten Eintrag in der CardView.
    private void startBarGraph2() {
        if (i_binding.pbFridaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbFridaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("TuesDayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbFridaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbFridaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("ThursDayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbFridaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbFridaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbFridaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day2BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay2", weeklySteps7);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText1);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-1));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }


    // - Öffne die Seite mit den Graphen für den dritten Eintrag in der CardView.
    private void startBarGraph3() {
        if (i_binding.pbThursdaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbThursdaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("TuesdayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbThursdaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbThursdaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("ThursdayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbThursdaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbThursdaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbThursdaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day3BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay3", weeklySteps6);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText2);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-2));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }

    // - Öffne die Seite mit den Graphen für den dritten Eintrag in der CardView.
    private void startBarGraph4() {
        if (i_binding.pbWednesdaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbWednesdaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("TuesdayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbWednesdaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbWednesdaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("ThursdayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbWednesdaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbWednesdaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbWednesdaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day4BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay4", weeklySteps5);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText3);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-3));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }

    // - Öffne die Seite mit den Graphen für den dritten Eintrag in der CardView.
    private void startBarGraph5() {
        if (i_binding.pbTuesdaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbTuesdaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("TuesdayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbTuesdaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbTuesdaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("ThursdayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbTuesdaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbTuesdaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbTuesdaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day5BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay5", weeklySteps4);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText4);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-4));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }

    private void startBarGraph6(){
        if (i_binding.pbMondaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbMondaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("TuesdayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbMondaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbMondaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("ThursdayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbMondaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbMondaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbMondaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day6BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay6", weeklySteps3);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText5);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-5));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }

    private void startBarGraph7(){
        if (i_binding.pbSundaytxt.getText().toString().equals(MO)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("MondayGoalValue", MondayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSundaytxt.getText().toString().equals(DI)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("TuesdayGoalValue", TuesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSundaytxt.getText().toString().equals(MI)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("WednesdayGoalValue", WednesdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSundaytxt.getText().toString().equals(DO)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("ThursdayGoalValue", ThursdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSundaytxt.getText().toString().equals(FR)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("FridayGoalValue", FridayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSundaytxt.getText().toString().equals(SA)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("SaturdayGoalValue", SaturdayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        } else if (i_binding.pbSundaytxt.getText().toString().equals(SO)) {
            cardIntent = new Intent(this, Day7BarGraph.class);
            cardBundle = new Bundle();
            cardBundle.putInt("stepsDay7", weeklySteps2);
            cardBundle.putString("month", monthName);
            cardBundle.putString("day", dayWeekText6);
            cardBundle.putString("dayNumber", String.valueOf(dayOfMonth-6));
            cardBundle.putDouble("SundayGoalValue", SundayGoal);
            cardIntent.putExtras(cardBundle);
            startActivity(cardIntent);
        }
    }

    // - Intent für das Testen der aggregierten Daten
    private void startSecondActivity() {
        Intent intent = new Intent(this, Second_Activity.class);
        intent.putExtra("weeklySteps", fitnessDataModel.weeklySteps);
        intent.putExtra("weeklyCalories", fitnessDataModel.weeklyCalories);
        intent.putExtra("weeklyDistance", fitnessDataModel.weeklyDistance);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkGoogleFitPermission() {
        // Scopes read and write we either read or write datatype from sensor

        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_WEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_HEIGHT, FitnessOptions.ACCESS_WRITE)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_SPEED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CADENCE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_BASAL_METABOLIC_RATE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

        account = getGoogleAccount();

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    MainActivity.this,
                    MY_PERMISSIONS_REQUEST,
                    account,
                    fitnessOptions);
        } else {
            readFitData();
        }
    }


    // Shared Preferences
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(MondayGoalValue, (int) MondayGoal);
        editor.putInt(TuesdayGoalValue, (int) TuesdayGoal);
        editor.putInt(WednesdayGoalValue, (int) WednesdayGoal);
        editor.putInt(ThursdayGoalValue, (int) ThursdayGoal);
        editor.putInt(FridayGoalValue, (int) FridayGoal);
        editor.putInt(SaturdayGoalValue, (int) SaturdayGoal);
        editor.putInt(SundayGoalValue, (int) SundayGoal);

        editor.apply();
    }


    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        MondayGoal = sharedPreferences.getInt(MondayGoalValue, 0);
        TuesdayGoal = sharedPreferences.getInt(TuesdayGoalValue, 0);
        WednesdayGoal = sharedPreferences.getInt(WednesdayGoalValue, 0);
        ThursdayGoal = sharedPreferences.getInt(ThursdayGoalValue, 0);
        FridayGoal = sharedPreferences.getInt(FridayGoalValue, 0);
        SaturdayGoal = sharedPreferences.getInt(SaturdayGoalValue, 0);
        SundayGoal = sharedPreferences.getInt(SundayGoalValue, 0);
    }


    private void orderProgressBarGoals() {
        if (dayWeekText.equals("Montag")) {
            GoalPercentCalculationDailyMo();
        } else if (dayWeekText.equals("Dienstag")) {
            GoalPercentCalculationDailyDi();
        } else if (dayWeekText.equals("Mittwoch")) {
            GoalPercentCalculationDailyMi();
        } else if (dayWeekText.equals("Donnerstag")) {
            GoalPercentCalculationDailyDo();
        } else if (dayWeekText.equals("Freitag")) {
            GoalPercentCalculationDailyFr();
        } else if (dayWeekText.equals("Samstag")) {
            GoalPercentCalculationDailySa();
        } else if (dayWeekText.equals("Sonntag")) {
            GoalPercentCalculationDailySo();
        }
    }

    // -----------------------------------------------------------------
    // Montag Schritte - Ziele
    private void GoalPercentCalculationDailyMo() {
        //
        i_binding.pbSaturdaytxt.setText(MO);
        i_binding.pbFridaytxt.setText(SO);
        i_binding.pbThursdaytxt.setText(SA);
        i_binding.pbWednesdaytxt.setText(FR);
        i_binding.pbTuesdaytxt.setText(DO);
        i_binding.pbMondaytxt.setText(MI);
        i_binding.pbSundaytxt.setText(DI);

        // Zielberechnung für Montag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / MondayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / SundayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / SaturdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / FridayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / ThursdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / WednesdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / TuesdayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        }

        if (MondayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Dienstag Schritte - Ziele
    private void GoalPercentCalculationDailyDi() {

        i_binding.pbSaturdaytxt.setText(DI);
        i_binding.pbFridaytxt.setText(MO);
        i_binding.pbThursdaytxt.setText(SO);
        i_binding.pbWednesdaytxt.setText(SA);
        i_binding.pbTuesdaytxt.setText(FR);
        i_binding.pbMondaytxt.setText(DO);
        i_binding.pbSundaytxt.setText(MI);

        // Zielberechnung für Dienstag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / TuesdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / MondayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / SundayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / SaturdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / FridayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / ThursdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / WednesdayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (TuesdayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }


        if (WednesdayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }
    }

    // -----------------------------------------------------------------
    // Mittwoch Schritte - Ziele
    private void GoalPercentCalculationDailyMi() {

        i_binding.pbSaturdaytxt.setText(MI);
        i_binding.pbFridaytxt.setText(DI);
        i_binding.pbThursdaytxt.setText(MO);
        i_binding.pbWednesdaytxt.setText(SO);
        i_binding.pbTuesdaytxt.setText(SA);
        i_binding.pbMondaytxt.setText(FR);
        i_binding.pbSundaytxt.setText(DO);

        // Zielberechnung für Mittwoch für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / WednesdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }
        goalPercent2 = (weeklySteps7 / TuesdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / MondayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / SundayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / SaturdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / FridayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / ThursdayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (WednesdayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Donnerstag Schritte - Ziele
    private void GoalPercentCalculationDailyDo() {

        i_binding.pbSaturdaytxt.setText(DO);
        i_binding.pbFridaytxt.setText(MI);
        i_binding.pbThursdaytxt.setText(DI);
        i_binding.pbWednesdaytxt.setText(MO);
        i_binding.pbTuesdaytxt.setText(SO);
        i_binding.pbMondaytxt.setText(SA);
        i_binding.pbSundaytxt.setText(FR);

        // Zielberechnung für Donnerstag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / ThursdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / WednesdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / TuesdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / MondayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / SundayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / SaturdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / FridayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.

        if (ThursdayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Freitag Schritte - Ziele
    private void GoalPercentCalculationDailyFr() {

        i_binding.pbSaturdaytxt.setText(FR);
        i_binding.pbFridaytxt.setText(DO);
        i_binding.pbThursdaytxt.setText(MI);
        i_binding.pbWednesdaytxt.setText(DI);
        i_binding.pbTuesdaytxt.setText(MO);
        i_binding.pbMondaytxt.setText(SO);
        i_binding.pbSundaytxt.setText(SA);


        // Zielberechnung für Freitag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / FridayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / ThursdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / WednesdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / TuesdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / MondayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / SaturdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / SundayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (FridayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Samstag Schritte
    private void GoalPercentCalculationDailySa() {
        i_binding.pbSaturdaytxt.setText(SA);
        i_binding.pbFridaytxt.setText(FR);
        i_binding.pbThursdaytxt.setText(DO);
        i_binding.pbWednesdaytxt.setText(MI);
        i_binding.pbTuesdaytxt.setText(DI);
        i_binding.pbMondaytxt.setText(MO);
        i_binding.pbSundaytxt.setText(SA);


        // Zielberechnung für Samstag für alle Tage im Cardview eingetragen 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / SaturdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / FridayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / ThursdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / WednesdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / TuesdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / MondayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / SundayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        }


        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.

        if (SaturdayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Sonntag Schritte - Ziele
    private void GoalPercentCalculationDailySo() {

        i_binding.pbSaturdaytxt.setText(SO);
        i_binding.pbFridaytxt.setText(SA);
        i_binding.pbThursdaytxt.setText(FR);
        i_binding.pbWednesdaytxt.setText(DO);
        i_binding.pbTuesdaytxt.setText(MI);
        i_binding.pbMondaytxt.setText(DI);
        i_binding.pbSundaytxt.setText(MO);

        // Zielberechnung für Sonntag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / SundayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            i_binding.starStepGoal7.setVisibility(View.VISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress(1);
        } else {
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
            i_binding.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / SaturdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            i_binding.starStepGoal6.setVisibility(View.VISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress(1);
        } else {
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
            i_binding.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / FridayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            i_binding.starStepGoal5.setVisibility(View.VISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress(1);
        } else {
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
            i_binding.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / ThursdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            i_binding.starStepGoal4.setVisibility(View.VISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress(1);
        } else {
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
            i_binding.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / WednesdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            i_binding.starStepGoal3.setVisibility(View.VISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress(1);
        } else {
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
            i_binding.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / TuesdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            i_binding.starStepGoal2.setVisibility(View.VISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress(1);
        } else {
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
            i_binding.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / MondayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            i_binding.starStepGoal1.setVisibility(View.VISIBLE);
            i_binding.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
            i_binding.progressbarday1.setProgress(1);
        } else {
            i_binding.progressbarday1.setProgress((int) goalPercent7);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }


        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (SundayGoal == 0 || dailyTotalSteps == 0) {
            i_binding.progressbarday7.setProgress(1);
            i_binding.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps7 == 0) {
            i_binding.progressbarday6.setProgress(1);
            i_binding.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps6 == 0) {
            i_binding.progressbarday5.setProgress(1);
            i_binding.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps5 == 0) {
            i_binding.progressbarday4.setProgress(1);
            i_binding.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps4 == 0) {
            i_binding.progressbarday3.setProgress(1);
            i_binding.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps3 == 0) {
            i_binding.progressbarday2.setProgress(1);
            i_binding.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps2 == 0) {
            i_binding.progressbarday1.setProgress(1);
            i_binding.starStepGoal1.setVisibility(View.INVISIBLE);
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readFitData() {
        // Alle Daten von heute holen
        getTodayData();

        // Holle heutiges Datum
        getCurrentDay();

        //Holle vergange Tage der Woche
        getCurrentDay1();
        getCurrentDay2();
        getCurrentDay3();
        getCurrentDay4();
        getCurrentDay5();
        getCurrentDay6();

        // Ziele Progressbars anordnung und Fortschritt
        getDailyStepsForGoals();


        // Frage nach den wöchentlichen Daten nach
        requestForWeeklyData();

        // Lese wöchentlich gelaufene Schritte für die Zielanzeige - UI
        readWeeklyStepsForGoals();

        // Echtzeitdaten beschaffen
        getRealTimeDataSteps(DataType.TYPE_STEP_COUNT_DELTA);
        getRealTimeDataSpeed(DataType.TYPE_SPEED);

        //Shared Preferences
        loadData();
        //Alle Ziele Laden
    }

    // - Holle tägliche Daten
    private void getTodayData() {
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_DISTANCE_DELTA)
                .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_STEP_COUNT_CADENCE)
                .addOnSuccessListener(this);
    }


    // - Schritteziel für den jeweiligen Tag festlegen, also wie viele Schritte man für Tag X z.B laufen will.
    public void GoalToWeekDay() {
        if (dayWeekText.equals("Montag")) {
            MondayGoal = totalStepsValue;
        } else if (dayWeekText.equals("Dienstag")) {
            TuesdayGoal = totalStepsValue;
        } else if (dayWeekText.equals("Mittwoch")) {
            WednesdayGoal = totalStepsValue;
        } else if (dayWeekText.equals("Donnerstag")) {
            ThursdayGoal = totalStepsValue;
        } else if (dayWeekText.equals("Freitag")) {
            FridayGoal = totalStepsValue;
        } else if (dayWeekText.equals("Samstag")) {
            SaturdayGoal = totalStepsValue;
        } else if (dayWeekText.equals("Sonntag")) {
            SundayGoal = totalStepsValue;
        }
    }

    // Holle tägliche Schritte für die Ziele
    private void getDailyStepsForGoals() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                                if (dataSet.isEmpty()) {
                                    dailyTotalSteps = 0;
                                } else {
                                    dailyTotalSteps = (int) total;
                                }
                                orderProgressBarGoals();
                                Log.i(TAG, "Total steps: " + total + "Daily steps " + dailyTotalSteps);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }


    // Schreibe einen eigenen DatenTyp für das Geschlecht
    public void writeCustomDatatypeGender() throws ExecutionException, InterruptedException {

        // Schreibe bzw. erstelle den Datentyp
        Task<DataType> response = Fitness.getConfigClient(this, getGoogleAccount())
                .createCustomDataType(new DataTypeCreateRequest.Builder()
                        .setName("com.google.android.gms.fit.samples.stepcounter.CustomDataGender0")
                        .addField("Geschlecht", Field.FORMAT_STRING)
                        .build());

        // Holle den erstellten Datentyp
        response = Fitness.getConfigClient(this, getGoogleAccount())
                .readDataType("com.google.android.gms.fit.samples.stepcounter.CustomDataGender0")
                .addOnSuccessListener(new OnSuccessListener<DataType>() {
                    @Override
                    public void onSuccess(DataType dataType) {
                        dataTypeGender = dataType;
                        updateGender();
                        Log.w(TAG, "Success in getting the gender" + dataType);
                    }
                }).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the gender.", e);
                            }
                        });
    }


    // Update das Geschlecht
    private Task<Void> updateGender() {
        // Erstelle ein neues Datenset und einen update request.
        DataSet dataSet = updateGenderData();
        long startTime = 0;
        long endTime = 0;

        // Holle die Startzeit und Endzeit von dem Datenset.
        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
            endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
        }

        // [START update_data_request]
        Log.i(TAG, "Updating the Gender! - dataset in the History API.");

        DataUpdateRequest request =
                new DataUpdateRequest.Builder()
                        .setDataSet(dataSet)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();


        // Rufe die History API auf um die Daten zu aktuallisieren
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .updateData(request)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point the data has been updated and can be read.
                                    Log.i(TAG, "Data update was successful for Gender.");
                                } else {
                                    Log.e(TAG, "There was a problem updating the dataset.", task.getException());
                                }
                            }
                        });
    }


    // Aktuallisiere die Daten für das Geschlecht
    private DataSet updateGenderData() {
        Log.i(TAG, "Creating a new data update request.");

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 0);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, 0);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(dataTypeGender)
                        .setStreamName(TAG + " - Gender")
                        .setType(DataSource.TYPE_RAW)
                        .build();

        DataSet dataSet = DataSet.create(dataSource);

        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(dataSet.getDataType().getFields().get(0)).setString(gender);

        // Setze den Wert von eigenen Daten (custom data)

        dataSet.add(dataPoint);
        // [END build_update_data_request]
        return dataSet;
    }


    // Holle das Gewicht und die Größe
    public void getHeightAndWeight() {

        DataReadRequest dataReadResponse = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEIGHT)
                .read(DataType.TYPE_WEIGHT)
                .setLimit(1)
                .setTimeRange(1, Calendar.getInstance().getTimeInMillis(), TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, getGoogleAccount())
                .readData(dataReadResponse)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                DataSet dataSet_0 = dataReadResponse.getDataSet(DataType.TYPE_HEIGHT);
                                DataSet dataSet_1 = dataReadResponse.getDataSet(DataType.TYPE_WEIGHT);

                                float height = dataSet_0.isEmpty() ? 0 : dataSet_0.getDataPoints().get(0).getValue(Field.FIELD_HEIGHT).asFloat();
                                float weight = dataSet_1.isEmpty() ? 0 : dataSet_1.getDataPoints().get(0).getValue(Field.FIELD_WEIGHT).asFloat();
                                Log.i(TAG, "Current height " + height);
                                Log.i(TAG, "Current weight " + weight);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step counter.", e);
                            }
                        });
    }

// - Aktualisiere das Gewicht - Gewicht wird aktualisiert selbst in der Anwendung von Google FIT.
    private Task<Void> updateWeight() {
        // Erstelle ein neuen Datensatz und einen aktualisierungs anfrage.
        DataSet dataSet = updateWeightData();
        long startTime = 0;
        long endTime = 0;


        // Holle die Anfangs und Endzeiten vpm dem Datenset
        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
            endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
        }

        // [START update_data_request]
        Log.i(TAG, "Updating the Weight! dataset in the History API.");

        DataUpdateRequest request =
                new DataUpdateRequest.Builder()
                        .setDataSet(dataSet)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();


        // - Rufe die History API auf um die Daten für das Gewicht zu aktualisieren.
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .updateData(request)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point the data has been updated and can be read.
                                    Log.i(TAG, "Data update was successful for Height.");
                                } else {
                                    Log.e(TAG, "There was a problem updating the dataset.", task.getException());
                                }
                            }
                        });
    }


    // - Aktualisiere die Daten für das Gewicht.
    private DataSet updateWeightData() {
        Log.i(TAG, "Creating a new data update request.");

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 0);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, 0);
        long startTime = cal.getTimeInMillis();

        // Erstelle data source
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_WEIGHT)
                        .setStreamName(TAG + " - Weight")
                        .setType(DataSource.TYPE_RAW)
                        .build();

        DataSet dataSet = DataSet.create(dataSource);

        weightDeltaString = String.valueOf(weightDelta) + String.valueOf(weightDelta1);
        weightDeltaResult = Float.parseFloat(weightDeltaString);

        BigDecimal bigDecimal = new BigDecimal(weightDeltaResult);
        BigDecimal changedValue = bigDecimal.movePointLeft(1);
        Float weightDecimalMoved = changedValue.floatValue();

        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_WEIGHT).setFloat(weightDecimalMoved);

        dataSet.add(dataPoint);
        // [END build_update_data_request]
        return dataSet;
    }


    // Führe eine Aktualisierung von den Daten aus die für die Größe zuständig sind.
    // Diese Daten werden dann selbst in der Google Fit Anwendung von Google verändert.
    private Task<Void> updateHeight() {

        // Erstelle ein neues Datenset und eine aktualisierungs anfrage
        DataSet dataSet = updateHeightData();
        long startTime = 0;
        long endTime = 0;

        // Holle die Start- und Endzeiten des Datensets.
        for (DataPoint dataPoint : dataSet.getDataPoints()) {
            startTime = dataPoint.getStartTime(TimeUnit.MILLISECONDS);
            endTime = dataPoint.getEndTime(TimeUnit.MILLISECONDS);
        }

        // [START update_data_request]
        Log.i(TAG, "Updating the Height! dataset in the History API.");

        DataUpdateRequest request =
                new DataUpdateRequest.Builder()
                        .setDataSet(dataSet)
                        .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                        .build();

        // Rufe die History API auf um Daten zu aktualisieren.
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .updateData(request)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // At this point the data has been updated and can be read.
                                    Log.i(TAG, "Data update was successful for Weight.");
                                } else {
                                    Log.e(TAG, "There was a problem updating the dataset.", task.getException());
                                }
                            }
                        });
    }


    // - Methode um Daten zu aktualisieren des Datensatzes für die Größe
    private DataSet updateHeightData() {
        Log.i(TAG, "Creating a new data update request.");
        // [START build_update_data_request]

        // - Setze einen Start- und Endzeitpunkt für die Daten die innerhalb der Zeitspanne erstellt wurden der originalen Einfügung d.h direkt bei der Eingabe.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, 0);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, 0);
        long startTime = cal.getTimeInMillis();


        // - Erstelle eine DataSource
        DataSource dataSource =
                new DataSource.Builder()
                        .setAppPackageName(this)
                        .setDataType(DataType.TYPE_HEIGHT)
                        .setStreamName(TAG + " - Height")
                        .setType(DataSource.TYPE_RAW)
                        .build();


        // Erstelle ein DataSet
        DataSet dataSet = DataSet.create(dataSource);


        // Für jeden Datenpunkt spezifiziere einen Startzeitpunkt und Endzeitpunkt und holle den Wert des gelesenen Datenpunktes, in diesem Fall für Größe.
        BigDecimal bigDecimal = new BigDecimal(heightDelta);
        BigDecimal changedValue = bigDecimal.movePointLeft(2);
        Float heightDecimalMoved = changedValue.floatValue();

        DataPoint dataPoint =
                dataSet.createDataPoint().setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_HEIGHT).setFloat(heightDecimalMoved);


        dataSet.add(dataPoint);
        // [END build_update_data_request]
        return dataSet;
    }


    // Anfrage für den wöchentlichen Wert von verschiedenen DatenTypen
    private void requestForWeeklyData() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis(); //
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

    /*
        Liest Daten von letzter Woche
     */

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
                .aggregate(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED)
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA)
                .aggregate(DataType.AGGREGATE_DISTANCE_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, getGoogleAccount())
                .readData(readRequest)
                .addOnSuccessListener(this);
    }

    // Liest wöchtenliche Daten für verbrauchte Kalorie, Anzahl der Schritte und gelaufene Distanz.
    @Override
    public void onSuccess(Object o) {
        if (o instanceof DataSet) {
            DataSet dataSet = (DataSet) o;
            Log.e(TAG, "Object DataSet : " + o);
            if (dataSet != null) {
                getDataFromDataSet(dataSet);
            }
        } else if (o instanceof DataReadResponse) {
        /*
            - Setze Daten bei Aggregations Datenpunkt auf 0
            - Objekt DataReadresponse erstellen
            - BucketListe erstellen alle Buckets aus der Liste holen
        */
            DataReadResponse dataReadResponse = (DataReadResponse) o;
            Log.e(TAG, "Object DataReadResponse : " + o);
            if (dataReadResponse.getBuckets() != null && !dataReadResponse.getBuckets().isEmpty()) {
                List<Bucket> bucketList = dataReadResponse.getBuckets();
                if (bucketList != null && !bucketList.isEmpty()) {
                    for (Bucket bucket : bucketList) {
                        DataSet stepsDataSet = bucket.getDataSet(DataType.TYPE_STEP_COUNT_DELTA);
                        getDataFromDataReadResponse(stepsDataSet);
                        DataSet caloriesDataSet = bucket.getDataSet(DataType.TYPE_CALORIES_EXPENDED);
                        getDataFromDataReadResponse(caloriesDataSet);
                        DataSet distanceDataSet = bucket.getDataSet(DataType.TYPE_DISTANCE_DELTA);
                        getDataFromDataReadResponse(distanceDataSet);
                    }
                }
            }
        }
    }

    // - Holle Daten vom Datenset die täglich neu erstellt werden
    private void getDataFromDataSet(DataSet dataSet) {

        List<DataPoint> dataPoints = dataSet.getDataPoints();
        for (DataPoint dataPoint : dataPoints) {
            Log.e(TAG, "data manual : " + dataPoint.getOriginalDataSource().getStreamName());

            for (Field field : dataPoint.getDataType().getFields()) {

                float value = Float.parseFloat(dataPoint.getValue(field).toString());
                Log.e(TAG, "Type : " + ((Object) value).getClass().getSimpleName());
                Log.e(TAG, "data : " + value);

                if (field.getName().equals(Field.FIELD_STEPS.getName())) {
                    fitnessDataModel.steps = Float.parseFloat(new DecimalFormat("#.##").format(value));
                } else if (field.getName().equals(Field.FIELD_CALORIES.getName())) {
                    fitnessDataModel.calories = Float.parseFloat(new DecimalFormat("###").format(value));
                } else if (field.getName().equals(Field.FIELD_DISTANCE.getName())) {
                    fitnessDataModel.distance = Float.parseFloat(new DecimalFormat("###").format(value));
                }
                i_binding.setFitnessdata(fitnessDataModel);
            }
        }
    }

    // - Holle wöchentliche Daten von DataReadResponse
    private void getDataFromDataReadResponse(DataSet dataSet) {
        List<DataPoint> dataPoints = dataSet.getDataPoints();
        for (DataPoint dataPoint : dataPoints) {
            for (Field field : dataPoint.getDataType().getFields()) {
                Log.e(TAG, "data manual : " + dataPoint.getOriginalDataSource().getStreamName());
                float value = Float.parseFloat(dataPoint.getValue(field).toString());
                Log.e(TAG, "Type : " + ((Object) value).getClass().getSimpleName());
                Log.e(TAG, "data : " + value);

                if (field.getName().equals(Field.FIELD_STEPS.getName())) {
                    fitnessDataModel.weeklySteps = Float.parseFloat(new DecimalFormat("#.##").format(value));
                } else if (field.getName().equals(Field.FIELD_CALORIES.getName())) {
                    fitnessDataModel.weeklyCalories = Float.parseFloat(new DecimalFormat("###").format(value));
                } else if (field.getName().equals(Field.FIELD_DISTANCE.getName())) {
                    fitnessDataModel.weeklyDistance = Float.parseFloat(new DecimalFormat("###").format(value));
                }
            }
        }
        i_binding.setFitnessdata(fitnessDataModel);
    }

    // Heute - z.B Montag
    public void getCurrentDay() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        monthName = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText = new SimpleDateFormat("EEEE").format(date);
    }

    // Gestern - z.B Sonntag
    public void getCurrentDay1() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -1);
        dayOfMonth1 = c.get(Calendar.DAY_OF_MONTH);
        monthName1 = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText1 = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    // Vorgestern - z.B Samstag
    public void getCurrentDay2() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -2);
        dayOfMonth2 = c.get(Calendar.DAY_OF_MONTH);
        monthName2 = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText2 = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    //  Tag -3 z.B Freitag
    public void getCurrentDay3() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -3);
        dayOfMonth3 = c.get(Calendar.DAY_OF_MONTH);
        monthName3 = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText3 = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    //  Tag -4 - z.B Donnerstag
    public void getCurrentDay4() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -4);
        dayOfMonth4 = c.get(Calendar.DAY_OF_MONTH);
        monthName4 = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText4 = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    // Tag -5  z.B Mittwoch
    public void getCurrentDay5() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -5);
        dayOfMonth5 = c.get(Calendar.DAY_OF_MONTH);
        monthName5 = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText5 = new SimpleDateFormat("EEEE").format(c.getTime());
    }

    // Tag -6  z.B Dienstag
    public void getCurrentDay6() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -6);
        dayOfMonth6 = c.get(Calendar.DAY_OF_MONTH);
        monthName6 = new SimpleDateFormat("MMM").format(c.getTime());
        dayWeekText6 = new SimpleDateFormat("EEEE").format(c.getTime());
    }


    /**
     * Reads the current daily step total, computed from midnight of the current day on the device's
     * current timezone.
     */


    // Holle den GoogleAccount
    private GoogleSignInAccount getGoogleAccount() {
        return GoogleSignIn.getAccountForExtension(MainActivity.this, fitnessOptions);
    }

    // Lese die täglichen Schritte
    private void readData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(
                        new OnSuccessListener<DataSet>() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void onSuccess(DataSet dataSet) {
                                long total =
                                        dataSet.isEmpty()
                                                ? 0
                                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                                Log.i(TAG, "Total steps: " + total);
                                dailyTotalSteps = (int) total;
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "There was a problem getting the step count.", e);
                            }
                        });
    }


    // - Menü erstellen
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // - Menüauswahl
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_read_data) {
            readData();
            getHeightAndWeight();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // - Holle Echtzeitdaten mit der Recording API für die Anzahl der momentanigen Schritte
    public void getRealTimeDataSteps(DataType dataType) {
        Fitness.getRecordingClient(this, getGoogleAccount())
                .subscribe(dataType)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            }
                        });
        getDataUsingSensor(dataType);
    }


    // - Holle Echtzeitdaten mit der Sensor API für die Anzahl der momentanigen Schritte
    private void getDataUsingSensor(DataType dataType) {
        Fitness.getSensorsClient(this, getGoogleAccount())
                .add(new SensorRequest.Builder()
                                .setDataType(dataType)
                                .setSamplingRate(1, TimeUnit.SECONDS)
                                .build(),
                        new OnDataPointListener() {
                            @Override
                       /*
                           Listener used to register live data updates from a DataSource
                           Data from SensorsClient can be deliverd as data point in real time/live
                        */

                            public void onDataPoint(@NonNull DataPoint dataPoint) {
                                float value = Float.parseFloat(dataPoint.getValue(Field.FIELD_STEPS).toString());
                                Log.e(TAG, "Sensor Data - Steps " + value);
                                fitnessDataModel.currentsteps = Float.parseFloat(new DecimalFormat("#.##").format(value));
                                i_binding.setFitnessdata(fitnessDataModel);
                            }
                        }
                );
    }


    // - Echtzeit Daten für die aktuelle Geschwindigkeit
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getRealTimeDataSpeed(DataType dataType) {
        Fitness.getSensorsClient(this, getGoogleAccount())
                .findDataSources(
                        new DataSourcesRequest.Builder()
                                .setDataTypes(dataType)
                                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                                .build())
                .addOnSuccessListener(dataSources -> dataSources.forEach(dataSource -> {
                    Log.i(TAG, "Data source found: " + dataSource.toString());
                    Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                    if (dataSource.getDataType().equals(DataType.TYPE_SPEED)) {
                        Log.i(TAG, "Data source for TYPE_SPEED FOUND!");
                        registerFitnessDataListener(dataSource, dataType);
                    }
                }))
                .addOnFailureListener(e ->
                        Log.e(TAG, "Find data sources request failed ", e));
    }


    // - Registriere die Sensor API und speichere die Daten in parseFloat.
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        OnDataPointListener listener = dataPoint -> {
            for (Field field : dataPoint.getDataType().getFields()) {
                float value = Float.parseFloat(dataPoint.getValue(Field.FIELD_SPEED).toString());
                fitnessDataModel.speed = value;
                i_binding.setFitnessdata(fitnessDataModel);
                Log.i(TAG, "Detected DataPoint field: " + field.getName());
                Log.i(TAG, "Detected DataPoint value: " + value);
                Log.e(TAG, "Sensor Data - Speed " + value);
            }
        };
        Fitness.getSensorsClient(this, getGoogleAccount())
                .add(
                        new SensorRequest.Builder()
                                .setDataSource(dataSource) // Optional but recommended
                                // for custom data sets.
                                .setDataType(dataType) // Can't be omitted.
                                .setSamplingRate(1, TimeUnit.SECONDS)
                                .build(),
                        listener
                )
                .addOnSuccessListener(unused ->
                        Log.i(TAG, "Type: Listener registered!"))
                .addOnFailureListener(task ->
                        Log.e(TAG, "Listener not registered.", task.getCause()));
    }


    // - Lese die Anzahl der gesamten wöchentlichen Schritte für die Wöchentlichenziele
    private Task<DataReadResponse> readWeeklyStepsForGoals() {

        DataReadRequest readRequest = queryFitnessData();

        // Rufe die History API auf um Daten zu besorgen mit einer Anfrage
        return Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .readData(readRequest)
                .addOnSuccessListener(
                        new OnSuccessListener<DataReadResponse>() {
                            @Override
                            public void onSuccess(DataReadResponse dataReadResponse) {
                                printData(dataReadResponse);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "There was a problem reading the data.", e);
                            }
                        });
    }

    // - Zeige die Anzahl der Datensets an und die buckets von Datensets.
    public void printData(DataReadResponse dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(
                    TAG, "Number of returned buckets of DataSets is: " + dataReadResult.getBuckets().size());
            // - Iteration über DataReadResponse mit Buckets
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSet(dataSet);
            }
        }
    }

    // - Speichere wöchentliche Daten der vergangen Woche (7 Tage) und gebe Datenpunkte im Logcat aus.
    private void dumpDataSet(DataSet dataSet) {
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance(); // Datum
        DateFormat timeFormat = DateFormat.getTimeInstance(); // Zeit

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:" + countWalkDay++);
            Log.e("History", "TimeStamp " + dateFormat.format(dp.getTimestamp(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                if (countWalkDay == 1) {
                    weeklySteps1 = dp.getValue(field).asInt();
                } else if (countWalkDay == 2) {
                    weeklySteps2 = dp.getValue(field).asInt();
                } else if (countWalkDay == 3) {
                    weeklySteps3 = dp.getValue(field).asInt();
                } else if (countWalkDay == 4) {
                    weeklySteps4 = dp.getValue(field).asInt();
                } else if (countWalkDay == 5) {
                    weeklySteps5 = dp.getValue(field).asInt();
                } else if (countWalkDay == 6) {
                    weeklySteps6 = dp.getValue(field).asInt();
                } else if (countWalkDay == 7) {
                    weeklySteps7 = dp.getValue(field).asInt();
                }

                orderProgressBarGoals();

                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                Log.e("History", "Data point:");
            }

        }
    }


    // - Holle die gesammelten Schritte der Woche und schaue nach wie viele Schritte gelaufen worden sind in den letzten 7 Tage
    private static DataReadRequest queryFitnessData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTime(now);
        Log.e("History", "SDF 1: " + sdf.format(cal.getTime()));
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Log.e("History", "SDF 2: " + sdf.format(cal.getTime()));
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }


    /**
     * Initialisiert einen eigene log Klasse welche Ausgaben in der logcat anzeigt.
     */

    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = (LogView) findViewById(R.id.sample_logview);

        // Fixing this lint error adds logic without benefit.
        // noinspection AndroidLintDeprecation
        logView.setTextAppearance(R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");
    }
}
