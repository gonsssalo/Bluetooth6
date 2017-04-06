package pt.ipbeja.pdm1.bluetooth6;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class TrakingActivity extends AppCompatActivity {

    private ArrayList<BluetoothObject> arrayOfFoundBTDevices;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<Integer> RSSIRegisterList = new ArrayList<>();
    BroadcastReceiver mReceiver;
    private String   MacAdress;
    private int xTimes = 1;
    MediaPlayer player;
    private Handler handler = new Handler();
    private Handler handler2 = new Handler();

    private Runnable runnable = new Runnable() {

        public void run() {

            handler.postDelayed(this, 2500);
            // Toast.makeText(TrakingActivity.this, "update", Toast.LENGTH_SHORT).show();
            //calls updateList() every 2.5 seconds
            updateList();

        }
    };

    private Runnable runnable2 = new Runnable() {

        public void run() {

            //Toast.makeText(TrakingActivity.this, "light", Toast.LENGTH_SHORT).show();
            // Toast.makeText(TrakingActivity.this, "update", Toast.LENGTH_SHORT).show();
            handler2.postDelayed(this, 100);
            if (isFlashOn) {
                // turn off flash
                ledoff();
            } else {
                // turn on flash
                ledon();
            }


        }
    };
    private Camera cam;
    private boolean isFlashOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traking);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        cam = null;
        // get the name/ RSSI of the device from FoundBTDevices class
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        //Integer RSSI = intent.getIntExtra("RSSI", 0);

        //get the text views
        TextView textViewname = (TextView) findViewById(R.id.TextViewNameDeviçe);
        TextView textViewRSSI = (TextView) findViewById(R.id.TextViewPower);

        //write into the text views
        textViewname.setText( "Device: " + name);
        textViewRSSI.setText("Distance: ");

        //start discovery
        displayListOfFoundDevices();
    }
    private void displayListOfFoundDevices() {

        arrayOfFoundBTDevices = new ArrayList<>();
        mBluetoothAdapter.startDiscovery();

        // Create a BroadcastReceiver for ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the bluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Get the "RSSI" to get the signal strength as integer,
                    // but should be displayed in "dBm" units
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);

                    // Create the device object and add it to the arrayList of devices
                    BluetoothObject bluetoothObject = new BluetoothObject();
                    bluetoothObject.setBluetooth_name(device.getName());
                    bluetoothObject.setBluetooth_address(device.getAddress());
                    bluetoothObject.setBluetooth_rssi(rssi);

                    //get devices Adresses
                     MacAdress = device.getAddress();

                    //get device adress from FoundBTDevices class
                    intent = getIntent();
                    String Adress = intent.getStringExtra("Adress");

                   //get text views
                    TextView txvw = (TextView) findViewById(R.id.TextViewPower);
                   // TextView textViewNumber = (TextView) findViewById(R.id.TextViewNumber);

                    boolean sucess = false;
                    // compare Adress from FoundBTDevices class with the devices the discovery finds
                    if (Adress.equalsIgnoreCase(MacAdress)) {
                        xTimes = 1;
                        handler2.removeCallbacks(runnable2);
                        if (isFlashOn) {
                            // turn off flash
                            ledoff();
                        }

                        try {
                           // textViewNumber.setText("size: " + RSSIRegisterList.size() + " RSSI: " + rssi);
                            if(RSSIRegisterList.get(4) != null) {
                                RSSIRegisterList.remove(4);
                                RSSIRegisterList.add(4, rssi);

                                // get Median
                                /* int[] values = {RSSIRegisterList.get(0)
                                         , RSSIRegisterList.get(1)
                                         , RSSIRegisterList.get(2)
                                         , RSSIRegisterList.get(3)
                                         , RSSIRegisterList.get(4)
                                 };
                                Arrays.sort(values);
                                double median;
                                if (values.length % 2 == 0)
                                    median = ((double)values[values.length/2] + (double)values[values.length/2 - 1])/2;
                                else
                                    median = (double) values[values.length/2];

*/

                                        int sum =(RSSIRegisterList.get(0)
                                        + RSSIRegisterList.get(1)
                                        + RSSIRegisterList.get(2)
                                        + RSSIRegisterList.get(3)
                                        + RSSIRegisterList.get(4)) / 5;

                              //  TextView TextViewName = (TextView) findViewById(R.id.TextViewName);
                               /* TextViewName.setText("Array" +RSSIRegisterList.get(0)
                                        + RSSIRegisterList.get(1)
                                        + RSSIRegisterList.get(2)
                                        + RSSIRegisterList.get(3)
                                        + RSSIRegisterList.get(4));*/

                                txvw.setText("Distance: " /*+ sum + "dbm" + "/"*/ + calculateDistance(sum) + "meters");
                                RSSIRegisterList.set(0, RSSIRegisterList.get(1));
                                RSSIRegisterList.set(1, RSSIRegisterList.get(2));
                                RSSIRegisterList.set(2, RSSIRegisterList.get(3));
                                RSSIRegisterList.set(3, RSSIRegisterList.get(4));
                                sucess = true;
                            }

                        }
                        catch (Exception e)
                        {


                         if(sucess != true) {
                              RSSIRegisterList.add(RSSIRegisterList.size(), rssi);
                             // textViewNumber.setText("size: " + RSSIRegisterList.size() + " RSSI: " + rssi);
                              //Toast.makeText(context, "catch " , Toast.LENGTH_SHORT).show();

                          }

                        }

                    }


                }

            }

        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

    }

// Convert dbm to meters
    protected double calculateDistance( double rssi) {

        int txPower = -60;
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }


        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.round(Math.pow(ratio,10));
        }
        else {
            return Math.round((0.89976)*Math.pow(ratio,7.7095) + 0.111);
       }

       /* -----------don't work---------
       return Math.pow(10,(double) txPower - rssi)/10*2;
       */
/* -----------don't work---------
      double exp = (27.55 - (20 * Math.log10(2412)) + Math.abs(rssi)) / 20.0;
        return Math.pow(10.0, exp);
       */

    }



    @Override
    protected void onPause()
    {
        super.onPause();
        mBluetoothAdapter.cancelDiscovery();
        //stop run
        handler.removeCallbacks(runnable);
        handler2.removeCallbacks(runnable2);
        if (isFlashOn) {
            // turn off flash
            ledoff();
        }
    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        }
        catch (Exception ignored)
        {

        }

    }

@Override
protected void onResume(){
    super.onResume();

    //create a run
    handler.postDelayed(runnable, 2500);
}
//stop discovery and inicialize it
    private void updateList(){

       // Toast.makeText(this, xTimes + "", Toast.LENGTH_SHORT).show();
        if(!BluetoothAdapter.checkBluetoothAddress(MacAdress))
        {

            /*It is possible that from time to time the scanner fails,
             the xTimes prevents the mobile phone from vibrating without being out of reach*/
            xTimes = xTimes + 1;
            if(xTimes > 2) {
                Toast.makeText(this, "Out of range", Toast.LENGTH_SHORT).show();
               /* Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                //v.vibrate(500);*/ //Vibrate for 500 milliseconds
                TextView textViewRSSI = (TextView) findViewById(R.id.TextViewPower);
                textViewRSSI.setText("Distance: Out of range");
                player=MediaPlayer.create(TrakingActivity.this,R.raw.industrial_alarm);
                player.start();

                handler2.postDelayed(runnable2, 100);

            }
        }


        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
        MacAdress = null;
    }


    void ledon() {

        if (cam == null) {

            cam = Camera.open();
            Camera.Parameters params = cam.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            cam.setParameters(params);
            cam.startPreview();
            isFlashOn = true;


        }
    }
    void ledoff(){

        if (cam != null) {

            cam.stopPreview();
            cam.release();
            cam = null;
            isFlashOn = false;

        }

    }


}

