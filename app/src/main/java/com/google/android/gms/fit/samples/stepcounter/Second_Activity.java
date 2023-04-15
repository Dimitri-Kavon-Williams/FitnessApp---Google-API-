package com.google.android.gms.fit.samples.stepcounter;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.google.android.gms.fit.samples.stepcounter.databinding.ActivitySecondBinding;

public class Second_Activity extends AppCompatActivity {
    private ActivitySecondBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_second);

        // Aggregierte Datenpunkte einholen

        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            float weeklySteps = bundle.getFloat("weeklySteps");
            float weeklyCalories = bundle.getFloat("weeklyCalories");
            float weeklyDistance = bundle.getFloat("weeklyDistance");
            // Wertinitialisierung von Datenpunkten dynamisch
            binding.weeklySteps.setText(String.valueOf(weeklySteps));
            binding.weeklyCalories.setText(String.valueOf(weeklyCalories));
            binding.weeklyDistance.setText(String.valueOf(weeklyDistance));
        } else{
            binding.weeklySteps.setText(String.valueOf("Test Successful"));
        }
    }
}
