package pt.ipbeja.pdm1.bluetooth6;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;



    public class FoundBTDevices extends ListActivity {


        private ArrayList<BluetoothObject> arrayOfFoundBTDevices;
        //ArrayList of RSSI
        ArrayList<Integer> RSSIArrayList = new ArrayList<>();
        //ArrayList of MacAdress
        ArrayList<String> MacAdressArrayList = new ArrayList<>();
        //ArrayList of device names
        ArrayList<String> DevicesArrayList = new ArrayList<>();
        BroadcastReceiver mReceiver;
        BluetoothAdapter mBluetoothAdapter1 = BluetoothAdapter.getDefaultAdapter();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            displayListOfFoundDevices();

        }

        private void displayListOfFoundDevices() {

            Toast.makeText(this, "displayListOfFoundDevices()", Toast.LENGTH_SHORT).show();
            arrayOfFoundBTDevices = new ArrayList<>();



            // Create a BroadcastReceiver for ACTION_FOUND
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, Intent intent) {
                    mBluetoothAdapter1.startDiscovery();
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

                        arrayOfFoundBTDevices.remove(bluetoothObject);
                        arrayOfFoundBTDevices.add(bluetoothObject);


                        DevicesArrayList.add(DevicesArrayList.size(), device.getName());
                        RSSIArrayList.add(RSSIArrayList.size(), rssi);
                        MacAdressArrayList.add(MacAdressArrayList.size(), device.getAddress());

                        // 1. Pass context and data to the custom adapter
                        FoundBTDevicesAdapter adapter = new FoundBTDevicesAdapter(getApplicationContext(), arrayOfFoundBTDevices);
                        // 2. setListAdapter

                        setListAdapter(adapter);
                    }

                }

            };

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

            registerReceiver(mReceiver, filter);


            // get the ListView

            ListView lv = getListView();
            // Responds to the click of a list element
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener()

            {
                @Override
                public void onItemClick(AdapterView<?> adapter, View v, int position,
                                        long id) {


                    Intent intent = new Intent(FoundBTDevices.this, TrakingActivity.class);
                    //Send name, macAdress and RSSI
                    intent.putExtra("name", DevicesArrayList.get(position));
                    intent.putExtra("Adress", MacAdressArrayList.get(position));
                    intent.putExtra("RSSI", RSSIArrayList.get(position));

                    startActivity(intent);

                }
            });

        }


        protected void onResume() {

            super.onResume();
             setListAdapter(null);
             arrayOfFoundBTDevices.clear();



            Toast.makeText(this, "ResumeStartDiscovery", Toast.LENGTH_SHORT).show();
            mBluetoothAdapter1.startDiscovery();


        }


        @Override
        protected void onPause() {


            super.onPause();
            mBluetoothAdapter1.cancelDiscovery();
            Toast.makeText(this, "cancelDiscovery", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected void onDestroy() {

            super.onDestroy();
            try {
                unregisterReceiver(mReceiver);
            } catch (Exception ignored) {

            }

        }
    }

