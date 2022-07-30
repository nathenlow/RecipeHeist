package sg.edu.np.mad.recipeheist;

import static android.Manifest.permission.FOREGROUND_SERVICE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class CountdownTimerActivity extends AppCompatActivity implements View.OnClickListener {

    private long timeCountInMilliSeconds = 1 * 10000;
    private String previousTimeSet;
    private Intent intentService;

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown_timer);

        getSupportActionBar().setTitle("Timer");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // create intent to the service
        intentService = new Intent(this, TimerService.class);

        // function call to initialize the views
        initViews();

        // function call to initialize the listeners
        initListeners();


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //---------------------------------------------- Start of functions ----------------------------------------------

    // function to initialize the views
    private void initViews() {
        progressBarCircle = findViewById(R.id.progressBarCircle);
        editTextMinute = findViewById(R.id.editTextMinute);
        textViewTime = findViewById(R.id.textViewTime);
        imageViewReset = findViewById(R.id.imageViewReset);
        imageViewStartStop = findViewById(R.id.imageViewStartStop);
    }

    // function to initialize the click listeners
    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
    }

    // implemented function to listen clicks
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
        }
    }

    // function to reset count down timer
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }


    // function to start and stop count down timer
    private void startStop() {
        if (timerStatus == TimerStatus.STOPPED) {

            // call to initialize the timer values
            setTimerValues();
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            imageViewStartStop.setImageResource(R.drawable.pause_button);
            // making edit text not editable
            editTextMinute.setEnabled(false);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

        } else {

            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.play_button);
            // making edit text editable
            editTextMinute.setEnabled(true);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

        }

    }

    // function to initialize the values for count down timer
    private void setTimerValues() {
        int time = 0;
        if (!editTextMinute.getText().toString().isEmpty() && !editTextMinute.getText().toString().equals(previousTimeSet)) {
            // set previous time set
            previousTimeSet = editTextMinute.getText().toString().trim();
            // fetching value from edit text and type cast to integer
            time = Integer.parseInt(editTextMinute.getText().toString().trim());
            // call to initialize the progress bar values
            setProgressBarValues();
        }
        else if (editTextMinute.getText().toString().trim().equals(previousTimeSet)){
            // get values in current timer
            time = (int) minuteFormatter(textViewTime.getText().toString());

        }
        else {
            // toast message to fill edit text
            Toast.makeText(getApplicationContext(), "Timer ended!", Toast.LENGTH_LONG).show();
        }
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = time * 1000;
    }

    // function to start count down timer
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

                // check if user's sdk version compatible
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    // set time to intentService
                    intentService.putExtra("TimeValue", (int) millisUntilFinished/1000);
                    // start the intentService
                    startForegroundService(intentService);
                }
                else{
                    // set time to intentService
                    intentService.putExtra("TimeValue", (int) millisUntilFinished/1000);
                    // start the intentService
                    startService(intentService);
                }

            }

            @Override
            public void onFinish() {

                // stop the foreground service
                stopService(intentService);

                textViewTime.setText(hmsTimeFormatter(0));
                // hiding the reset icon
                imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.play_button);
                // making edit text editable
                editTextMinute.setEnabled(true);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;
            }

        }.start();
        countDownTimer.start();
    }

    // function to stop count down timer
    private void stopCountDownTimer() {
        // stop foreground service
        stopService(intentService);
        countDownTimer.cancel();
    }

    // function to set circular progress bar values
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    // function to convert millisecond to time format
    private String hmsTimeFormatter(long milliSeconds) {

        @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;

    }

    // function to convert time format back to seconds
    private long minuteFormatter(String hms){

        long seconds = 0;

        System.out.println(hms);

        String[] fileredhms = hms.split(":");

        // convert hour to minute
        long hour = Integer.parseInt(fileredhms[0]);
        seconds += (hour * 3600);

        System.out.println(seconds);

        // add minute
        long minute = Integer.parseInt(fileredhms[1]);
        seconds += (minute * 60);

        System.out.println(minute);

        // convert seconds to minute
        seconds += Integer.parseInt(fileredhms[2]);

        System.out.println(seconds);

        return seconds;
    }

}