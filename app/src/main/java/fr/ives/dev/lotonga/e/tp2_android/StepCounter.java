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
    int oldStep  = 0;
    double oldDistance = 0;
    int t;
    private boolean isSensorPresent = false;
    private TextView mStepsSinceReboot;
    private int step;
    private double distance;


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
        step = 0;
        distance = 0;
        t = 0;
    }
    //function to determine the distance run in kilometers using average step length for men and number of steps
    /*public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }*/

    /**
     * function to determine the distance run in meter using step sensor
     * @param p_step
     * @return
     */
    public double getDistanceStep(long p_step)
    {
        if( p_step == 0)
        {
            double distance = (this.step * 0.762);
            return distance;
        }
        else
        {
            double distance =(p_step * 0.762);
            return distance;
        }
        // 1 step = 0.762 meter
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

        if( t == 0)
        {
            oldStep =(int) event.values[0];
            oldDistance = this.getDistanceStep(oldStep);
        }
        this.step =(int) event.values[0] - oldStep;
        this.distance = this.getDistanceStep(0);
        String tmp_ch = String.format("Pas : %s\nKm : %s",String.valueOf(event.values[0]) ,String.valueOf(getDistanceStep((long)event.values[0])));
        mStepsSinceReboot.setText( tmp_ch);

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
