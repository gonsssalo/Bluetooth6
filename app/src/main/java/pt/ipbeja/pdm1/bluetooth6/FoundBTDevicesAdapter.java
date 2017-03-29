package pt.ipbeja.pdm1.bluetooth6;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


class FoundBTDevicesAdapter extends ArrayAdapter<BluetoothObject> {

    private Context context;
    private ArrayList<BluetoothObject> arrayFoundDevices;

    FoundBTDevicesAdapter(Context context, ArrayList<BluetoothObject> arrayOfAlreadyPairedDevices)
    {
        super(context, R.layout.activity_found_btdevices_adapter, arrayOfAlreadyPairedDevices);

        this.context = context;
        this.arrayFoundDevices = arrayOfAlreadyPairedDevices;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {

        BluetoothObject bluetoothObject = arrayFoundDevices.get(position);

        // 1. Create Inflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.activity_found_btdevices_adapter, parent, false);

        // 3. Get the widgets from the rowView
        TextView bt_name = (TextView) rowView.findViewById(R.id.textview_bt_scan_name);
        TextView bt_address = (TextView) rowView.findViewById(R.id.textview_bt_scan_address);
        TextView bt_signal_strength = (TextView) rowView.findViewById(R.id.textview_bt_scan_signal_strength);

        // 4. Set the text for each widget
        bt_name.setText(bluetoothObject.getBluetooth_name());
        bt_address.setText("address: " + bluetoothObject.getBluetooth_address());
        bt_signal_strength.setText("RSSI: " + bluetoothObject.getBluetooth_rssi() + "dbm");



        // 5. return rowView
        return rowView;

    }//end getView()


}//end class AlreadyPairedAdapter

