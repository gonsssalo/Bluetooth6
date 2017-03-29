package pt.ipbeja.pdm1.bluetooth6;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class TrakingActivity extends AppCompatActivity {

    private ArrayList<BluetoothObject> arrayOfFoundBTDevices;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<Integer> RSSIRegisterList = new ArrayList<>();
    BroadcastReceiver mReceiver;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {

        public void run() {

            handler.postDelayed(this, 3000);
            Toast.makeText(TrakingActivity.this, "update", Toast.LENGTH_SHORT).show();
            updateList();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traking);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        Integer RSSI = intent.getIntExtra("RSSI", 0);

        TextView textViewname = (TextView) findViewById(R.id.TextViewNameDeviçe);
        TextView textViewRSSI = (TextView) findViewById(R.id.TextViewPower);

        textViewname.setText( "Dispositivo: " + name);
        textViewRSSI.setText("RSSI: " + RSSI.toString() + "dbm");
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

                    String MacAdress = device.getAddress();

                    intent = getIntent();

                    String Adress = intent.getStringExtra("Adress");
                    TextView txvw = (TextView) findViewById(R.id.TextViewPower);
                    TextView textViewNumber = (TextView) findViewById(R.id.TextViewNumber);

                    boolean sucess = false;
                    if (Adress.equalsIgnoreCase(MacAdress)) {
                        try {
                            textViewNumber.setText("size: " + RSSIRegisterList.size() + " RSSI: " + rssi);
                            Toast.makeText(context, "try", Toast.LENGTH_SHORT).show();
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

                                TextView TextViewName = (TextView) findViewById(R.id.TextViewName);
                                TextViewName.setText("Array" +RSSIRegisterList.get(0)
                                        + RSSIRegisterList.get(1)
                                        + RSSIRegisterList.get(2)
                                        + RSSIRegisterList.get(3)
                                        + RSSIRegisterList.get(4));

                                txvw.setText("RSSI: " + sum + "dbm" + "/" + calculateDistance(sum) + "meters");
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
                              textViewNumber.setText("size: " + RSSIRegisterList.size() + " RSSI: " + rssi);
                              Toast.makeText(context, "catch " , Toast.LENGTH_SHORT).show();
                          }

                        }

                    }


                }

            }

        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

        registerReceiver(mReceiver, filter);

    }


    protected double calculateDistance( double rssi) {

        int txPower = 64;
        if (rssi == 0) {
            return -1.0; // if we cannot determine distance, return -1.
        }


        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }



    @Override
    protected void onPause()
    {
        super.onPause();
        mBluetoothAdapter.cancelDiscovery();
        handler.removeCallbacks(runnable);
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
    handler.postDelayed(runnable, 3000);
}

    private void updateList(){

        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();

    }

}

