package pt.ipbeja.pdm1.bluetooth6;

import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;


class BluetoothObject implements Parcelable {

    private String bluetooth_name;
    private String bluetooth_address;
    private ParcelUuid[] bluetooth_uuids;
    private int bluetooth_rssi;

    String getBluetooth_name() {
        return bluetooth_name;
    }

    void setBluetooth_name(String bluetooth_name) {
        this.bluetooth_name = bluetooth_name;
    }

    String getBluetooth_address() {
        return bluetooth_address;
    }

    void setBluetooth_address(String bluetooth_address) {
        this.bluetooth_address = bluetooth_address;
    }
    int getBluetooth_rssi() {
        return bluetooth_rssi;
    }

    void setBluetooth_rssi(int bluetooth_rssi) {
        this.bluetooth_rssi = bluetooth_rssi;
    }

    public ParcelUuid[] getBluetooth_uuids() {
        return bluetooth_uuids;
    }

    public void setBluetooth_uuids(ParcelUuid[] bluetooth_uuids) {
        this.bluetooth_uuids = bluetooth_uuids;
    }

    // Parcelable stuff
    BluetoothObject()
    {}  //empty constructor

    private BluetoothObject(Parcel in)
    {
        super();
        readFromParcel(in);
    }

    private void readFromParcel(Parcel in)
    {
        bluetooth_name = in.readString();
    }

    public static final Creator<BluetoothObject> CREATOR = new Creator<BluetoothObject>()
    {
        public BluetoothObject createFromParcel(Parcel in) {
            return new BluetoothObject(in);
        }

        public BluetoothObject[] newArray(int size) {

            return new BluetoothObject[size];
        }

    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
