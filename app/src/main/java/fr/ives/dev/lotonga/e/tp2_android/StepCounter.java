package fr.ives.dev.lotonga.e.tp2_android;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class StepCounter extends Activity implements SensorEventListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;
    private boolean isSensorPresent = false;
    private TextView mStepsSinceReboot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        mStepsSinceReboot =
                (TextView)findViewById(R.id.stepssincereboot);

        mSensorManager = (SensorManager)
                this.getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                != null)
        {
            mSensor =
                    mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }
        else
        {
            isSensorPresent = false;
        }

    }
    //function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isSensorPresent)
        {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isSensorPresent)
        {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String tmp_ch = String.format("Pas : %s\nKm : %s",String.valueOf(event.values[0]) ,String.valueOf(getDistanceRun((long)event.values[0])));
        mStepsSinceReboot.setText( tmp_ch);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
