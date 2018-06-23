package fr.ives.dev.lotonga.e.tp2_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StepCounter extends Activity implements SensorEventListener, View.OnClickListener {


    private SensorManager mSensorManager;
    private Sensor mSensor;
    int t;
    private double distance = 0;
    private long step = 0;
    private int oldStepCounter= 0;
    private double oldDistance= 0;
    private boolean isSensorPresent = false;
    private TextView mStepsSinceReboot;
    private Button startGPS;
    private double[] firstPosition = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);

        mStepsSinceReboot =
                (TextView)findViewById(R.id.stepssincereboot);

        startGPS = (Button) findViewById(R.id.gpsStart);
        startGPS.setOnClickListener(this);
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
        this.t = 0;
        Bundle extra = getIntent().getExtras();
        if( extra != null)
        {
            this.firstPosition = extra.getDoubleArray("firstPosition");
        }
    }
    /*//function to determine the distance run in kilometers using average step length for men and number of steps
    public float getDistanceRun(long p_step){
        float distance = 0;
        if( p_step == 0)
        {
            distance = (float)(this.step*78)/(float)100000;
        }
        else
        {
            distance = (float)(p_step*78)/(float)100000;
        }
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
        if(event.sensor.getType()==Sensor.TYPE_STEP_COUNTER )
        {

            if( t == 0 )
            {
                this.oldStepCounter = (int) event.values[0];
                this.oldDistance = this.getDistanceStep((int)event.values[0]);
                Toast.makeText(this, "T = "+ t, Toast.LENGTH_SHORT).show();
                t++;
            }
            this.step = (long)(event.values[0]- this.oldStepCounter);
            this.distance = this. getDistanceStep(0) - oldDistance ;
            String tmp_ch = String.format("Numbre Pas : %s\nKm parcourus : %s",String.valueOf(step) ,String.valueOf( (distance ))  );
            mStepsSinceReboot.setText( tmp_ch);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Toast.makeText(this, "onAccuracyChanged ????", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.gpsStart:
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("firstPosition",this.firstPosition );
                intent.putExtra("distanceStep", this.distance);
                intent.putExtra("stepNumber", this.step);
                startActivity(intent);
                break;
        }
    }
}
