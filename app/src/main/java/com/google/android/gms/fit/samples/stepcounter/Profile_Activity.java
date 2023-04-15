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
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataTypeCreateRequest;
import com.google.android.gms.fitness.request.DataUpdateRequest;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Profile_Activity extends AppCompatActivity implements OnSuccessListener{

    private FitnessDataModel fitnessDataModel;
    private ProfileActivityBinding p_binding;
    private FitnessOptions fitnessOptions;
    private GoogleSignInAccount account;
    private DatePickerDialog datePickerDialog;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    // Layouts Databinding Dialog
    private LayoutLinearCustomDialogBinding dialogBinding_weight; // Weight
    private LayoutLinearCustomDialog2Binding dialogBinding_height; // Height
    private LayoutLinearCustomDialog3Binding dialogBinding_gender; // Gender
    private CustomDialogStepsgoalBinding dialogBinding_stepsGoal; // Ziele

    // Prozentberechnung für das anzeigen des Symbol für das erreichen des Ziels
    private double goalPercent1;
    private double goalPercent2;
    private double goalPercent3;
    private double goalPercent4;
    private double goalPercent5;
    private double goalPercent6;
    private double goalPercent7;

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

    // Dialog Objekte
    private Dialog dialog_weight;
    private Dialog dialog_height;
    private Dialog dialog_gender;
    private Dialog dialog_stepsGoal;

    // Variabeln für Tagzuweisung bei Zielen
    private String MO = "MO";
    private String DI = "DI";
    private String MI = "MI";
    private String DO = "DO";
    private String FR = "FR";
    private String SA = "SA";
    private String SO = "SO";

    // Variabeln für tägliche Ziele
    private double MondayGoal;
    private double TuesdayGoal;
    private double WednesdayGoal;
    private double ThursdayGoal;
    private double FridayGoal;
    private double SaturdayGoal;
    private double SundayGoal;

    //Shared Prefs -
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MondayGoalValue = "mGoal";
    public static final String TuesdayGoalValue = "tGoal";
    public static final String WednesdayGoalValue = "wGoal";
    public static final String ThursdayGoalValue = "thGoal";
    public static final String FridayGoalValue = "fGoal";
    public static final String SaturdayGoalValue = "sGoal";
    public static final String SundayGoalValue = "soGoal";

    // Erstellete custom data DatenTypen
    private DataType dataTypeGender;

    // Variabeln für Prozesse
    int heightInt;
    int weightInt;
    int weightInt1;
    double totalStepsValue;
    private static int dailyTotalSteps;
    private String dayWeekText;

    public static final String TAG = "StepCounter";

    // Variablen zum Senden von Daten
    private double stepsGoal;
    private float heightDelta;
    private int weightDelta;
    private int weightDelta1;
    private float weightDeltaResult;
    private String weightDeltaString;
    private int totalStepsGoal;
    private String gender;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        p_binding = DataBindingUtil.setContentView(this, R.layout.profile_activity);

        initDatePicker();

        fitnessDataModel = new FitnessDataModel();

        // Ausgewähltes Menüitem hervorheben
        Menu menu = p_binding.bottomNav.getMenu();
        MenuItem menuitem = menu.getItem(1);
        menuitem.setChecked(true);

        // Menü Navigation
        p_binding.bottomNav.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
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

        // Test button für die Woche
        p_binding.btnLastWeek.setOnClickListener(v -> {
            startSecondActivity();
        });


        // Button für die Gewichtseingabe - Gewicht wird an Google gesendet
        p_binding.txtINWeightET.setOnClickListener(v -> {
            showDialog();
        });

        p_binding.txtInHeightET.setOnClickListener(v -> {
            showDialog1();
        });

        p_binding.txtINGenderET.setOnClickListener(v -> {
            showDialog2();
        });

        p_binding.txtINBirthdayET.setOnClickListener(v -> {
            openDatePicker(v);
        });

        p_binding.txtInStepgoalET.setOnClickListener(v -> {
            showDialog3();
        });

        if (ContextCompat.checkSelfPermission(this, String.valueOf(Permission.Permission_Array)) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST);
            checkGoogleFitPermission();
        }
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
                    Profile_Activity.this,
                    MY_PERMISSIONS_REQUEST,
                    account,
                    fitnessOptions);
        } else {
            readFitData();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readFitData() {

        // Holle Daten für heute
        getTodayData();

        // Wöchentliche Daten einführen
        requestForWeeklyData();

        // Holle heutiges Datum
        getCurrentDay();

        // Ziele Progressbars anordnung und Fortschritt
        getDailyStepsForGoals();

        // Lese wöchentlich gelaufene Schritte für die Zielanzeige - UI
        readWeeklyStepsForGoals();

        //Shared Preferences
        loadData();
        //Alle Ziele Laden
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
                    // Wichtig weeeklySteps7 ist immer die Schritte Anzahl von gestern egal welcher Tag ist Samstag. Die Anzahl der täglichen Schritte kann über die API abgefragt werden.
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

    // Holle den GoogleAccount
    private GoogleSignInAccount getGoogleAccount() {
        return GoogleSignIn.getAccountForExtension(Profile_Activity.this, fitnessOptions);
    }


    // Methoden für zweite Activity

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


    // Dialog für das Gewicht der Person zeige die größe an
    public void showDialog() {
        dialogBinding_weight = LayoutLinearCustomDialogBinding.inflate(getLayoutInflater());

        dialogBinding_weight.numberPicker1.setMinValue(1);
        dialogBinding_weight.numberPicker1.setMaxValue(450);
        dialogBinding_weight.numberPicker1.setWrapSelectorWheel(false);

        dialogBinding_weight.numberPicker2.setMinValue(0);
        dialogBinding_weight.numberPicker2.setMaxValue(9);
        dialogBinding_weight.numberPicker2.setWrapSelectorWheel(false);

        dialog_weight = new Dialog(Profile_Activity.this, R.style.DialogStyle);
        dialog_weight.setContentView(dialogBinding_weight.getRoot());
        dialog_weight.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.valueOf(i) + " " + Character.toString((char) 0x339D);
            }
        };


        /*
            Programmfehler beheben für die Anzeige von dem NumberPicker.
            Berichtigt die Anzeige in dem der erste Wert mit der jeweiligen Metrik angezeigt wird.
            Also z.B kg und cm ist direkt sichtbar, anstatt erst nach mehrmaligen laden des Dialogs.
         */


        View editView = dialogBinding_weight.numberPicker1.getChildAt(0);

        if (editView instanceof EditText) {
            ((EditText) editView).setFilters(new InputFilter[0]);
        }

        View editView1 = dialogBinding_weight.numberPicker2.getChildAt(0);

        if (editView1 instanceof EditText) {
            ((EditText) editView1).setFilters(new InputFilter[0]);
        }

        dialog_weight.show();

        dialogBinding_weight.btnCancel1.setOnClickListener(v -> {
            dialog_weight.dismiss();
        });

        // Bestätige und sende Daten an Google und bestätige diese für die Anwendung

        dialogBinding_weight.btnOk1.setOnClickListener(v -> {
            weightDelta = dialogBinding_weight.numberPicker1.getValue();
            weightDelta1 = dialogBinding_weight.numberPicker2.getValue();
            weightInt = weightDelta;
            weightInt1 = weightDelta1;
            p_binding.txtINWeightET.setText(String.valueOf(weightInt + "." + weightInt1));
            updateWeight();
            dialog_weight.dismiss();
        });
    }

    // Dialog für die Größe der Person zeige den Dialog Screen an.
    public void showDialog1() {

        dialogBinding_height = LayoutLinearCustomDialog2Binding.inflate(getLayoutInflater());
        dialogBinding_height.numberPicker1.setMinValue(30);
        dialogBinding_height.numberPicker1.setMaxValue(272);
        dialogBinding_height.numberPicker1.setWrapSelectorWheel(false);

        NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.valueOf(i) + " " + Character.toString((char) 0x339D);
            }
        };

        // Bug entfernen das der erste Wert nicht sichtbar ist.
        View editView = dialogBinding_height.numberPicker1.getChildAt(0);

        if (editView instanceof EditText) {
            ((EditText) editView).setFilters(new InputFilter[0]);
        }

        // Vordefinierter Wert
        dialogBinding_height.numberPicker1.setValue(173);

        dialogBinding_height.numberPicker1.setFormatter(formatter);

        dialog_height = new Dialog(Profile_Activity.this, R.style.DialogStyle);
        dialog_height.setContentView(dialogBinding_height.getRoot());
        dialog_height.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);

        dialog_height.show();

        dialogBinding_height.btnCancel.setOnClickListener(v -> {
            dialog_height.dismiss();
        });

        // Sende Daten and Google und bestätige Sie für die Anwendung
        dialogBinding_height.btnOk.setOnClickListener(v -> {
            // Muss ein float sein für das Hochladen
            heightDelta = (float) dialogBinding_height.numberPicker1.getValue();
            updateHeight();
            heightInt = (int) heightDelta;
            p_binding.txtInHeightET.setText(String.valueOf(heightInt + " " + "cm"));
            dialog_height.dismiss();
        });
    }

    // Dialog für das Geschlecht der Person
    private void showDialog2() {
        dialogBinding_gender = LayoutLinearCustomDialog3Binding.inflate(getLayoutInflater());
        dialog_gender = new Dialog(Profile_Activity.this, R.style.DialogStyle);
        dialog_gender.setContentView(dialogBinding_gender.getRoot());
        dialog_gender.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);
        dialog_gender.show();

        dialogBinding_gender.btnCancel2.setOnClickListener(v -> {
            dialog_gender.dismiss();
        });

        dialogBinding_gender.btnOk2.setOnClickListener(v -> {
            if (dialogBinding_gender.rbtnMale.isChecked()) {
                gender = (String) dialogBinding_gender.rbtnMale.getText();
                p_binding.txtINGenderET.setText(gender);
                try {
                    writeCustomDatatypeGender();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog_gender.dismiss();
            } else if (dialogBinding_gender.rbtnFemale.isChecked()) {
                gender = (String) dialogBinding_gender.rbtnFemale.getText();
                p_binding.txtINGenderET.setText(gender);
                try {
                    writeCustomDatatypeGender();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog_gender.dismiss();
            } else if (dialogBinding_gender.rbtnDiverse.isChecked()) {
                gender = (String) dialogBinding_gender.rbtnDiverse.getText();
                p_binding.txtINGenderET.setText(gender);
                try {
                    writeCustomDatatypeGender();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog_gender.dismiss();
            } else if (dialogBinding_gender.rbtnCustom.isChecked()) {
                gender = String.valueOf(dialogBinding_gender.editTxtVGender.getText());
                p_binding.txtINGenderET.setText(gender);
                try {
                    writeCustomDatatypeGender();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                dialog_gender.dismiss();
            }
        });
    }

    // Dialog für das gesetzte Ziel für wie viele Schritte gelaufen werden sollen.
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showDialog3() {

        dialogBinding_stepsGoal = CustomDialogStepsgoalBinding.inflate(getLayoutInflater());
        dialog_stepsGoal = new Dialog(Profile_Activity.this, R.style.DialogStyle);
        dialog_stepsGoal.setContentView(dialogBinding_stepsGoal.getRoot());
        dialog_stepsGoal.getWindow().setBackgroundDrawableResource(R.drawable.bg_window);
        dialog_stepsGoal.show();

        dialogBinding_stepsGoal.numberPicker.setMinValue(1);
        dialogBinding_stepsGoal.numberPicker.setMaxValue(40);
        dialogBinding_stepsGoal.numberPicker.setWrapSelectorWheel(false);

        NumberPicker.Formatter formatterSteps = new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                int temp = value * 1000;
                return "" + temp;
            }
        };
        dialogBinding_stepsGoal.numberPicker.setFormatter(formatterSteps);

        // Entferne Whitespace der zu Beginn entsteht durch einen Fehler. Macht den ersten Wert sichtbar des Numberpickers.
        View editView = dialogBinding_stepsGoal.numberPicker.getChildAt(0);

        if (editView instanceof EditText) {
            ((EditText) editView).setFilters(new InputFilter[0]);
        }

        dialogBinding_stepsGoal.btnCancel.setOnClickListener(v -> {
            dialog_stepsGoal.dismiss();
        });

        // Speichere Daten, Ziel Zuweisung für den jeweiligen Tag, Berechnung des prozentual erreichten Fortschritts für den jeweiligen Tag
        dialogBinding_stepsGoal.btnOk.setOnClickListener(v -> {
            stepsGoal = dialogBinding_stepsGoal.numberPicker.getValue();
            totalStepsGoal = (int) (stepsGoal * 1000);
            totalStepsValue = totalStepsGoal;
            // Anzahl der ausgewählten Schritte werden als Ziel für den jeweiligen Wochentag gespeichert.
            GoalToWeekDay();
            saveData();
            p_binding.txtInStepgoalET.setText(String.valueOf(totalStepsGoal));
            orderProgressBarGoals();
            dialog_stepsGoal.dismiss();
        });
    }

    // Holle das Datum von heute:

    public void getCurrentDay() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        dayWeekText = new SimpleDateFormat("EEEE").format(date);
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
        p_binding.incInformationLayout.pbSaturdaytxt.setText(MO);
        p_binding.incInformationLayout.pbFridaytxt.setText(SO);
        p_binding.incInformationLayout.pbThursdaytxt.setText(SA);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(FR);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(DO);
        p_binding.incInformationLayout.pbMondaytxt.setText(MI);
        p_binding.incInformationLayout.pbSundaytxt.setText(DI);

        // Zielberechnung für Montag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / MondayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / SundayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / SaturdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / FridayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / ThursdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / WednesdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / TuesdayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        }

        if (MondayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Dienstag Schritte - Ziele
    private void GoalPercentCalculationDailyDi() {

        p_binding.incInformationLayout.pbSaturdaytxt.setText(DI);
        p_binding.incInformationLayout.pbFridaytxt.setText(MO);
        p_binding.incInformationLayout.pbThursdaytxt.setText(SO);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(SA);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(FR);
        p_binding.incInformationLayout.pbMondaytxt.setText(DO);
        p_binding.incInformationLayout.pbSundaytxt.setText(MI);

        // Zielberechnung für Dienstag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / TuesdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / MondayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / SundayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / SaturdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / FridayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / ThursdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / WednesdayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (TuesdayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }


        if (WednesdayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }
    }

    // -----------------------------------------------------------------
    // Mittwoch Schritte - Ziele
    private void GoalPercentCalculationDailyMi() {

        p_binding.incInformationLayout.pbSaturdaytxt.setText(MI);
        p_binding.incInformationLayout.pbFridaytxt.setText(DI);
        p_binding.incInformationLayout.pbThursdaytxt.setText(MO);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(SO);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(SA);
        p_binding.incInformationLayout.pbMondaytxt.setText(FR);
        p_binding.incInformationLayout.pbSundaytxt.setText(DO);

        // Zielberechnung für Mittwoch für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / WednesdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }
        goalPercent2 = (weeklySteps7 / TuesdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / MondayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / SundayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / SaturdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / FridayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / ThursdayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (WednesdayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Donnerstag Schritte - Ziele
    private void GoalPercentCalculationDailyDo() {

        p_binding.incInformationLayout.pbSaturdaytxt.setText(DO);
        p_binding.incInformationLayout.pbFridaytxt.setText(MI);
        p_binding.incInformationLayout.pbThursdaytxt.setText(DI);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(MO);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(SO);
        p_binding.incInformationLayout.pbMondaytxt.setText(SA);
        p_binding.incInformationLayout.pbSundaytxt.setText(FR);

        // Zielberechnung für Donnerstag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / ThursdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / WednesdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / TuesdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / MondayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / SundayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / SaturdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / FridayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.

        if (ThursdayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Freitag Schritte - Ziele
    private void GoalPercentCalculationDailyFr() {

        p_binding.incInformationLayout.pbSaturdaytxt.setText(FR);
        p_binding.incInformationLayout.pbFridaytxt.setText(DO);
        p_binding.incInformationLayout.pbThursdaytxt.setText(MI);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(DI);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(MO);
        p_binding.incInformationLayout.pbMondaytxt.setText(SO);
        p_binding.incInformationLayout.pbSundaytxt.setText(SA);


        // Zielberechnung für Freitag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / FridayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / ThursdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / WednesdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / TuesdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / MondayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / SaturdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / SundayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        }

        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (FridayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Samstag Schritte
    private void GoalPercentCalculationDailySa() {
        p_binding.incInformationLayout.pbSaturdaytxt.setText(SA);
        p_binding.incInformationLayout.pbFridaytxt.setText(FR);
        p_binding.incInformationLayout.pbThursdaytxt.setText(DO);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(MI);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(DI);
        p_binding.incInformationLayout.pbMondaytxt.setText(MO);
        p_binding.incInformationLayout.pbSundaytxt.setText(SA);


        // Zielberechnung für Samstag für alle Tage im Cardview eingetragen 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / SaturdayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / FridayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / ThursdayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / WednesdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / TuesdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / MondayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / SundayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        }


        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.

        if (SaturdayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (SundayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }

    }

    // -----------------------------------------------------------------
    // Sonntag Schritte - Ziele
    private void GoalPercentCalculationDailySo() {

        p_binding.incInformationLayout.pbSaturdaytxt.setText(SO);
        p_binding.incInformationLayout.pbFridaytxt.setText(SA);
        p_binding.incInformationLayout.pbThursdaytxt.setText(FR);
        p_binding.incInformationLayout.pbWednesdaytxt.setText(DO);
        p_binding.incInformationLayout.pbTuesdaytxt.setText(MI);
        p_binding.incInformationLayout.pbMondaytxt.setText(DI);
        p_binding.incInformationLayout.pbSundaytxt.setText(MO);

        // Zielberechnung für Sonntag für alle Tage die im Cardview eingetragen sind 7 bis 1. (z.B So-Mo rückwärts)
        goalPercent1 = (dailyTotalSteps / SundayGoal) * 100;
        if (goalPercent1 >= 100 || goalPercent1 == 100) {
            goalPercent1 = 100;
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        } else if (goalPercent1 < 1) {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday7.setProgress((int) goalPercent1);
        }

        goalPercent2 = (weeklySteps7 / SaturdayGoal) * 100;
        if (goalPercent2 >= 100 || goalPercent2 == 100) {
            goalPercent2 = 100;
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        } else if (goalPercent2 < 1) {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday6.setProgress((int) goalPercent2);
        }

        goalPercent3 = (weeklySteps6 / FridayGoal) * 100;
        if (goalPercent3 >= 100 || goalPercent3 == 100) {
            goalPercent3 = 100;
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        } else if (goalPercent3 < 1) {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday5.setProgress((int) goalPercent3);
        }

        goalPercent4 = (weeklySteps5 / ThursdayGoal) * 100;
        if (goalPercent4 >= 100 || goalPercent4 == 100) {
            goalPercent4 = 100;
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        } else if (goalPercent4 < 1) {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday4.setProgress((int) goalPercent4);
        }

        goalPercent5 = (weeklySteps4 / WednesdayGoal) * 100;
        if (goalPercent5 >= 100 || goalPercent5 == 100) {
            goalPercent5 = 100;
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        } else if (goalPercent5 < 1) {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday3.setProgress((int) goalPercent5);
        }

        goalPercent6 = (weeklySteps3 / TuesdayGoal) * 100;
        if (goalPercent6 >= 100 || goalPercent6 == 100) {
            goalPercent6 = 100;
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        } else if (goalPercent6 < 1) {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
        } else {
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday2.setProgress((int) goalPercent6);
        }

        goalPercent7 = (weeklySteps2 / MondayGoal) * 100;
        if (goalPercent7 >= 100 || goalPercent7 == 100) {
            goalPercent7 = 100;
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.VISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
        } else if (goalPercent7 < 1) {
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
        } else {
            p_binding.incInformationLayout.progressbarday1.setProgress((int) goalPercent7);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }


        // Sofern Ziele oder Schritte 0 sind setze die Progressbar auf 0 und das Symbol für das erreichen des Ziels auf unsichtbar.
        if (SundayGoal == 0 || dailyTotalSteps == 0) {
            p_binding.incInformationLayout.progressbarday7.setProgress(1);
            p_binding.incInformationLayout.starStepGoal7.setVisibility(View.INVISIBLE);
        }

        if (SaturdayGoal == 0 || weeklySteps7 == 0) {
            p_binding.incInformationLayout.progressbarday6.setProgress(1);
            p_binding.incInformationLayout.starStepGoal6.setVisibility(View.INVISIBLE);
        }

        if (FridayGoal == 0 || weeklySteps6 == 0) {
            p_binding.incInformationLayout.progressbarday5.setProgress(1);
            p_binding.incInformationLayout.starStepGoal5.setVisibility(View.INVISIBLE);
        }

        if (ThursdayGoal == 0 || weeklySteps5 == 0) {
            p_binding.incInformationLayout.progressbarday4.setProgress(1);
            p_binding.incInformationLayout.starStepGoal4.setVisibility(View.INVISIBLE);
        }

        if (WednesdayGoal == 0 || weeklySteps4 == 0) {
            p_binding.incInformationLayout.progressbarday3.setProgress(1);
            p_binding.incInformationLayout.starStepGoal3.setVisibility(View.INVISIBLE);
        }

        if (TuesdayGoal == 0 || weeklySteps3 == 0) {
            p_binding.incInformationLayout.progressbarday2.setProgress(1);
            p_binding.incInformationLayout.starStepGoal2.setVisibility(View.INVISIBLE);
        }

        if (MondayGoal == 0 || weeklySteps2 == 0) {
            p_binding.incInformationLayout.progressbarday1.setProgress(1);
            p_binding.incInformationLayout.starStepGoal1.setVisibility(View.INVISIBLE);
        }
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

    private void startSecondActivity() {
        Intent intent = new Intent(this, Second_Activity.class);
        intent.putExtra("weeklySteps", fitnessDataModel.weeklySteps);
        intent.putExtra("weeklyCalories", fitnessDataModel.weeklyCalories);
        intent.putExtra("weeklyDistance", fitnessDataModel.weeklyDistance);
        startActivity(intent);
    }


    private void getTodayData() {
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_CALORIES_EXPENDED)
                .addOnSuccessListener(this);
        Fitness.getHistoryClient(this, getGoogleAccount())
                .readDailyTotal(DataType.TYPE_STEP_COUNT_CADENCE)
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
                p_binding.incInformationLayout.setFitnessdata(fitnessDataModel);
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
        p_binding.incInformationLayout.setFitnessdata(fitnessDataModel);
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

}

