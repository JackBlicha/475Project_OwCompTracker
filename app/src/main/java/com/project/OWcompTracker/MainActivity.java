package com.project.OWcompTracker;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    EditText password, username;
    Button registerButton;
    Button loginButton;

    // sensor
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private TextView mAccelValues;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("login", "onCreate: called");
        setContentView(R.layout.activity_main);

        password = findViewById(R.id.register_password);
        username = findViewById(R.id.register_username);
        registerButton = findViewById(R.id.register_submit_button);
        loginButton = findViewById(R.id.login_submit_button);

        registerButton.setOnClickListener(v -> {
            Log.d("login", "registerButton.setOnClickListener: called");
            // Creating User Instance
            UserClass user = new UserClass();
            user.setPassword(password.getText().toString());
            user.setUsername(username.getText().toString());

            Log.d("login", "Registering user: " + user.username + "  " + user.password + ".");
            Log.d("login", "registerButton.setOnClickListener: validateInput(user): " + validateInput(user));
            if(validateInput(user)){
                Log.d("login", "registerButton.setOnClickListener: validateInput(user) was true");
                // Insert user in database
                UserDatabase userDB = UserDatabase.getInstance(getApplicationContext());
                UserDao userDao = userDB.UserDao();
                    // Register user
                    try {
                        Log.d("login", "registering a user: start");
                        userDao.insertUser(user);
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Successfully registered new user, welcome.", Toast.LENGTH_SHORT).show());
                    } catch (Exception e){
                        // User already exists w/ that username
                        Log.d("login", "registering a user: failed" + user.username);
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed to registered new user, an account already exists under that username.", Toast.LENGTH_SHORT).show());
                    }
            }
            else{
                Log.d("login", "registerButton.setOnClickListener: validateInput(user) was false");
                Toast.makeText(getApplicationContext(), "Please be sure to fill all registry fields.", Toast.LENGTH_SHORT).show();
            }
        });
        loginButton.setOnClickListener(v -> {
            Log.d("login", "loginButton.setOnClickListener: called");
            // Creating User Instance
            UserClass user = new UserClass();
            user.setPassword(password.getText().toString());
            user.setUsername(username.getText().toString());

            Log.d("login", "loginButton.setOnClickListener: validateInput(user): " + validateInput(user));
            if(validateInput(user)){
                Log.d("login", "loginButton.setOnClickListener: validateInput(user) was true");
                // Attempt login
                UserDatabase userDB = UserDatabase.getInstance(getApplicationContext());
                UserDao userDao = userDB.UserDao();
                    // Login user
                    UserClass mUser = userDao.login(user.getUsername(), user.getPassword());
                    if(mUser == null){
                        Log.d("login", "Failed login, invalid credentials. " + user.getUsername());
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Failed login, invalid credentials.", Toast.LENGTH_SHORT).show());
                    }
                    else {
                        Log.d("login", "Thread for logging in a user");
                        Log.d("login", "Successful login, welcome back " + user.getUsername() + ".");
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Successful login, welcome back " + user.getUsername() + ".", Toast.LENGTH_SHORT).show());
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                    }
            }
            else{
                Log.d("login", "loginButton.setOnClickListener: validateInput(user) was false");
                Toast.makeText(getApplicationContext(), "Be sure to fill at least the username and password fields.", Toast.LENGTH_SHORT).show();
            }
        });

        // sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccelValues = findViewById(R.id.accelerationValues);
    }

    private Boolean validateInput(UserClass user){
        return !user.getUsername().isEmpty() && !user.getPassword().isEmpty();
    }



    // sensor
    @Override
    protected void onResume() {
        super.onResume();

        // sensor
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // sensor
    @Override
    protected void onPause() {
        super.onPause();

        // sensor
        mSensorManager.unregisterListener(this, mAccelerometer);
    }

    // sensor
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        mAccelValues.setText(sensorEvent.values[0] + "\n" + sensorEvent.values[1] + "\n" + sensorEvent.values[2]);
    }

    // sensor
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) { }
}
