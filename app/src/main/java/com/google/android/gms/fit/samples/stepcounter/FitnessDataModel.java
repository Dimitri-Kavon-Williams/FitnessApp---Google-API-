package com.google.android.gms.fit.samples.stepcounter;

public class FitnessDataModel {
    // Daily
    public float currentsteps;
    public float steps;
    public float distance;
    public float calories;
    public float countertester;
    public float speed;
    public float stepCadence;
    public float bmr;

    // Weekly
    public float weeklySteps;
    public float weeklyCalories;
    public float weeklyDistance;


    //Personal data
    public int height;

    // Constructor
    public FitnessDataModel() {
        // Daily data
        steps = 0f;
        distance = 0f;
        calories = 0f;
        currentsteps = 0f;
        speed = 0f;
        stepCadence = 0f;
        bmr = 0f;

        // Weekly data
        weeklySteps = 0f;
        weeklyCalories = 0f;
        weeklyDistance = 0f;

        // Personal data
        height = 0;

    }


    // Daily Getter&Setter
    public float getSpeed() { return speed; }

    public void setSpeed(float speed) { this.speed = speed; }

    public float getSteps() { return steps; }

    public void setSteps(float steps) { this.steps = steps; }

    public float getDistance() { return distance; }

    public void setDistance(float distance) { this.distance = distance; }

    public float getCalories() { return calories; }

    public void setCalories(float calories) { this.calories = calories; }

    public float getCurrentsteps() { return currentsteps; }

    public void setCurrentsteps(float currentsteps) { this.currentsteps = currentsteps; }


    // Weekly Getter&Setter

    public float getWeeklySteps() { return weeklySteps; }

    public void setWeeklySteps(float weeklySteps) { this.weeklySteps = weeklySteps; }

    public float getWeeklyCalories() { return weeklyCalories; }

    public void setWeeklyCalories(float weeklyCalories) { this.weeklyCalories = weeklyCalories; }

    public float getWeeklyDistance() { return weeklyDistance; }

    public void setWeeklyDistance(float weeklyDistance) { this.weeklyDistance = weeklyDistance; }

    // Personal Getter&Setter

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    // Methods available for class
    public Float add (Float addsteps){
        steps += addsteps;
        return steps;
    }

    public Float addCurrent (Float stepcounter){
        countertester +=stepcounter;
        return countertester;
    }

}



