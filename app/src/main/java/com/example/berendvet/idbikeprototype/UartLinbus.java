package com.example.berendvet.idbikeprototype;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

import static com.example.berendvet.idbikeprototype.MainActivity.TAG;

public class UartLinbus {

    public static final String[] UART_SERVICES = { "6e400001-b5a3-f393-e0a9-e50e24dcca9e" };
    public static final String[] UART_CHARACTS = { "6e400002-b5a3-f393-e0a9-e50e24dcca9e", "6e400003-b5a3-f393-e0a9-e50e24dcca9e" };   // { TX, RX }

    public BluetoothDevice bluetoothDevice;
    public BluetoothGatt bluetoothGatt;
    public BluetoothGattCharacteristic chrct[];
    public String deviceDescriptor;
    public Activity act;
    private boolean connected;

    boolean snifferMode = false;

    public String[] getServices() {  return UART_SERVICES; }
    public String[] getCharacteristics() {  return UART_CHARACTS; }

    public UartLinbus(Activity act) {
        this.deviceDescriptor = "bike";
        this.act = act;
        chrct = new BluetoothGattCharacteristic[getCharacteristics().length];
    }

    void setCharacteristic(BluetoothGattCharacteristic characteristic){
        for (int i = 0; i< chrct.length ; i++) {
            if(characteristic.getUuid().toString().equals(getCharacteristics()[i])){
                chrct[i] = characteristic;
                return;
            }
        }
    }

    public void onCharacteristicRead(BluetoothGattCharacteristic characteristic) {
        int index = getCharacteristicIndex(characteristic);
        byte[] data = characteristic.getValue();
        if (index==1) if (data != null) ((MainActivity)act).bikeAdapter.readBuffer(data);
        else Log.e("Infox", "Illegal index in Uart of onCharacteristicRead, index=" + index + ": Value=" + new String(data));
    }

    public void onDescriptorRead(BluetoothGattDescriptor desc) {
//        int index = getCharacteristicIndex(desc.getCharacteristic());
//        if (index!=-1) onDescriptorRead(index, desc.getValue());
    }

    public void onServicesDiscovered() {
        if (chrct[1]!=null) ((MainActivity )act).setCharacteristicNotification(bluetoothGatt, chrct[1], true);
    }

    int getCharacteristicIndex(BluetoothGattCharacteristic characteristic) {
        for (int i=0 ;i<chrct.length ; i++) if (chrct[i]==characteristic) return i;
        return -1;
    }

    public void setConnectionState(boolean newConnected) {
        if (newConnected != connected) {
            connected = newConnected;
            if (connected) onServicesDiscovered();
        }
        if (!connected) if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt = null;
            bluetoothDevice = null;
        }
        String s = deviceDescriptor + ((connected)? " connected":" disconnected");
        act.runOnUiThread(new UartLinbus.ToastMessage(s));
    }

    public void write(byte data[], int cnt) {
        if (snifferMode || (!getConnected())) return;    // In sniffer mode, it is not allowed to transmit data
        String s = "";
        for (int i=0 ; i<cnt ; i++) if (data[i]>=0) s+= (int)(data[i])+" "; else s+= (int)(data[i]+255) + " ";
        // Log.e("Infox", "TX: " + s);
        int index = 0, dn;
        while (true) {
            dn = cnt - index;
            if (dn <= 0) return;
            if (dn > 20) dn = 20;
            byte x[] = new byte[dn];
            System.arraycopy(data, index, x, 0, dn);

            chrct[0].setValue(x);
            if (bluetoothGatt.writeCharacteristic(chrct[0]));
            // mBluetoothLeService.addRequest(bluetoothGatt,chrct[0], 0, x);
            index += dn;
        }
    }


    public class ToastMessage implements Runnable {
        String text;
        public ToastMessage(String s) { text = s; }
        public void run() {
            Toast.makeText(act.getApplicationContext(), text, Toast.LENGTH_LONG).show();
        }
    }




    public boolean getConnected() { return connected; }
}
