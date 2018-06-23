package fr.ives.dev.lotonga.e.tp2_android;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    private TextView distance;
    private double[] firstPositionNumb, secondPositionNumb;
    private Button firstPos, secondPos;
    private MyLocationListener current_location;
    private Button btnGetLocation = null;
    private Button stepCounter = null;
    private EditText editLocation = null;
    private ProgressBar pb = null;
    private static final String TAG = "debug_custom";
    private Boolean flag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current_location= new MyLocationListener(null);
        //location_text = (TextView)findViewById(R.id.location_text);
        //if you want to lock screen for always Portrait mode
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        editLocation = (EditText) findViewById(R.id.editTextLocation);

        btnGetLocation = (Button) findViewById(R.id.btnLocation);
        btnGetLocation.setOnClickListener(this);
        stepCounter = (Button) findViewById(R.id.stepCounter);
        stepCounter.setOnClickListener(this);
        firstPos =(Button) findViewById(R.id.firstPosition);
        secondPos = (Button) findViewById(R.id.secondPosition);
        firstPos.setOnClickListener(this);
        secondPos.setOnClickListener(this);
        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        this.distance = (TextView)findViewById(R.id.showDistance);
        this.firstPositionNumb = new double[]{0.0,0.0};
        this.secondPositionNumb = new double[]{0.0,0.0};
        Bundle extras = getIntent().getExtras();
        this.displayCoordinate( extras );


    }



  /*  private void checkIfGPSIsEnabled()
    {
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) { }
    }*/

    /*  public static boolean isLocationEnabled(Context context) {
          int locationMode = 0;
          String locationProviders;

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
              try {
                  locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

              } catch (Settings.SettingNotFoundException e) {
                  e.printStackTrace();
                  return false;
              }

              return locationMode != Settings.Secure.LOCATION_MODE_OFF;

          }else{
              locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
              return !TextUtils.isEmpty(locationProviders);
          }
      }
  */
    private void displayCoordinate(Bundle extras )
    {
        flag = displayGpsStatus();
        if (flag)
        {
            Log.v(TAG, "onClick");

            editLocation.setText("Please!! move your device to" +
                    " see the changes in coordinates." + "\nWait..");

            pb.setVisibility(View.VISIBLE);
            locationListener = new MyLocationListener(extras);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return;
            }
            locationMangaer.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, 2000, 10, locationListener);
        }
        else
            {
            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            alertbox("Gps Status!!", "Your GPS is: OFF");
        }

    }
    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.stepCounter:
                Intent intent = new Intent(this, StepCounter.class);
                startActivity(intent);
                break;
            case R.id.btnLocation:
                this.displayCoordinate(null);
                break;
            case R.id.firstPosition:
                if(this.secondPositionNumb[0] != 0.0 && this.secondPositionNumb[1] != 0.0)
                {
                    this.firstPositionNumb[0] = this.secondPositionNumb[0];
                    this.firstPositionNumb[1] = this.secondPositionNumb[1];
                    Toast.makeText(this, " Ok First Position", Toast.LENGTH_SHORT).show();

                    Intent intent2 = new Intent(this, StepCounter.class);
                    intent2.putExtra("firstPosition", this.firstPositionNumb);
                    startActivity(intent2);
                }
                else
                {
                    Toast.makeText(this, "Veuillez cliquer de nouveau !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.secondPosition:

                if(this.firstPositionNumb[0] != 0.0 && this.firstPositionNumb[1] != 0.0)
                {
                    Location first = new Location("");
                    Location second = new Location("");
                    first.setLatitude(this.firstPositionNumb[0]);
                    first.setLongitude(this.firstPositionNumb[1]);
                    second.setLatitude(this.secondPositionNumb[0]);
                    second.setLongitude(this.secondPositionNumb[1]);
                    float distance_parcourue = first.distanceTo(second);
                    distance.setText(String.valueOf(((int) distance_parcourue))+" mêtres");
                }
                else
                {
                    Toast.makeText(this, "You have to stop first the GPS before restart it", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        /*long start = System.currentTimeMillis();
        long end = 0L;
        while(true)
        {
            long tmp_seconde = (end - start)/1000;
            if( tmp_seconde%RAFRAICHISSEMENT == 0)
            {
                this.displayCoordinate();
            }
            Toast.makeText(this, "Seconde = "+ tmp_seconde, Toast.LENGTH_SHORT).show();
            end = System.currentTimeMillis();
        }*/

    }
    /*----Method to Check GPS is enable or disable ----- */
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    /*----------Method to create an AlertBox ------------- */
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable")
                .setCancelable(false)
                .setTitle("** Gps Status **")
                .setPositiveButton("Gps On",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // finish the current activity
                                // AlertBoxAdvance.this.finish();
                                Intent myIntent = new Intent(
                                        Settings.ACTION_SECURITY_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*----------Listener class to get coordinates ------------- */
    public class MyLocationListener implements LocationListener {

        Bundle extras = null;
        public MyLocationListener( Bundle p_extras )
        {
            this.extras = p_extras;
        }
        @Override
        public void onLocationChanged(Location loc) {

            editLocation.setText("");
            pb.setVisibility(View.INVISIBLE);

            String longitude = "Longitude: " +loc.getLongitude();
            Log.v(TAG, longitude);
            String latitude = "Latitude: " +loc.getLatitude();
            Log.v(TAG, latitude);
            /*----------to get City-Name from coordinates ------------- */
            String cityName=null;
            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    System.out.println(addresses.get(0).getLocality());
                cityName=addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String s = longitude+"\n"+latitude +
                    "\n\nMy Currrent City is: "+cityName;
            editLocation.setText(s);

            secondPositionNumb[0] = loc.getLatitude();
            secondPositionNumb[1] = loc.getLongitude();

            if (this.extras != null)
            {
                firstPositionNumb = this.extras.getDoubleArray("firstPosition");
                if(secondPositionNumb[0] != 0.0)
                {

                    float distance_step = getIntent().getFloatExtra("distanceStep",0);
                    int stepNumber = getIntent().getIntExtra("stepNumber",0);

                    Location first = new Location("");
                    Location second = new Location("");
                    first.setLatitude(firstPositionNumb[0]);
                    first.setLongitude(firstPositionNumb[1]);
                    second.setLatitude(secondPositionNumb[0]);
                    second.setLongitude(secondPositionNumb[1]);
                    int distance_gps = (int) first.distanceTo(second);
                    String ch = "Distance GPS = "+ distance_gps + "\nDistance PAS = "+ distance_step+ "\nNumbre PAS = "+ stepNumber;
                    distance.setText(ch);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
}
