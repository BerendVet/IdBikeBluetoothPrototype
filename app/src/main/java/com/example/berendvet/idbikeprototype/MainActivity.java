package com.example.berendvet.idbikeprototype;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class MainActivity extends Activity {

    public static final String TAG = "BTPrototypeIDBike";
    //Name of device we are looking for
    private static final String DEVICE_NAME = "BikeBT" ;

    public final static String UUID_DESCRIPTOR = "00002901-0000-1000-8000-00805f9b34fb";
    public final static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static final String[] HRS_SERVICES = { "0000180d-0000-1000-8000-00805f9b34fb", "0000180f-0000-1000-8000-00805f9b34fb" };   // Heart rate service, battery service
    public static final String[] HRS_CHARACTS = { "00002a37-0000-1000-8000-00805f9b34fb", "00002a19-0000-1000-8000-00805f9b34fb" };   // Heart rate measurement, SOC_level
    public static final String[] UART_SERVICES = { "6e400001-b5a3-f393-e0a9-e50e24dcca9e" };
    public static final String[] UART_CHARACTS = { "6e400002-b5a3-f393-e0a9-e50e24dcca9e", "6e400003-b5a3-f393-e0a9-e50e24dcca9e" };   // { TX, RX }

    public static final int MSG_PROGRESS = 0;
    public static final int MSG_CLEAR = 1;

    private BluetoothAdapter btAdapter;

    private BluetoothGatt mConnectedGatt;

    private Handler mHandler;

    private static BtleRequestArray bra = new BtleRequestArray();

    // UI Elements
    ListView listView;
    ArrayAdapter<String> adapter;
    String selectedDevice = "";
    Button discoverButton;
    Button connectButton;
    Button disconnectButton;
    Button powerButton;
    TextView bikeText;
    TextView discoverText;
    TextView speedLabel;

    Button increaseSupportButton;
    Button decreaseSupportButton;
    TextView supportModeView;


    public UartLinbus bike;

    public BicycleAdapter bikeAdapter;
//    boolean connected = false;
//    BluetoothDevice bike;
//    BluetoothGatt mBluetoothGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // set bluetooth adapter
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        btAdapter = manager.getAdapter();

        bike = new UartLinbus(this);
        bikeAdapter = new BicycleAdapter(this);

        // older method for Android version < 4.3
        //if (btAdapter==null) btAdapter = BluetoothAdapter.getDefaultAdapter();



        // set events for bluetooth adapters
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        // list adapter
        adapter = new ArrayAdapter<String>(this, R.layout.devicelistview, R.id.text1);


        // BluetoothLeService.addConnectedDevices(adapter);
        listView = (ListView) findViewById(R.id.deviceListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                selectedDevice = (String) listView.getItemAtPosition(i);
            }
        });

        discoverButton = (Button) findViewById(R.id.discoverButton);
        discoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
            }
        });

        connectButton = (Button) findViewById((R.id.connectButton));
        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (selectedDevice.length() == 0) {
                    Toast.makeText(getApplicationContext(), "No device selected" , Toast.LENGTH_LONG).show();
                    return;
                }
                //bikeText.setText("connecting...");
                connectBluetoothLE(getMACAddress(selectedDevice));
            }
        });

        disconnectButton = (Button) findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                disconnectGatt();
                // bikeText.setText("No device connected");
            }
        });

        powerButton = (Button) findViewById(R.id.powerButton);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bikeAdapter.setPower(!bikeAdapter.power);
                powerButton.setText( bikeAdapter.power ? "Turn off" : "Turn on");
            }
        });

        speedLabel = findViewById(R.id.SpeedLabel);

        //this.bikeText = findViewById(R.id.BikeText);
        this.discoverText = findViewById(R.id.DiscoverText);

        supportModeView = findViewById(R.id.SupportModeView);
        increaseSupportButton = findViewById(R.id.IncreaseSupportButton);
        decreaseSupportButton = findViewById(R.id.DecreaseSupportButton);

        increaseSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportModeView.setText(bikeAdapter.increaseDriveMode().name());
            }
        });

        decreaseSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportModeView.setText(bikeAdapter.decreaseDriveMode().name());
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(btAdapter == null || !btAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            finish();
            return;
        }

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


    }

    @Override
    protected void onPause(){
        super.onPause();
        btAdapter.cancelDiscovery();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bike.getConnected()){
            bike.setConnectionState(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    // Bluetooth scan receiver callback
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) adapter.clear();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);



                Log.v(TAG,"New LE Device: " + device.getName());

                // if check for when we know what device we are looking for
                // if(DEVICE_NAME.equals(device.getName())) {
                    showDevice(device);

                // }
            }
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) discoverText.setText("Scanning");
            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) discoverText.setText("");
        }
    };

    private void startScan() {
        adapter.clear();
        btAdapter.startDiscovery();

        //Toast.makeText(getApplicationContext(), new StringBuilder("yag si xam").reverse().toString(), Toast.LENGTH_LONG).show();
    }

    private void stopScan() {
        btAdapter.cancelDiscovery();
    }

    public String connectBluetoothLE(String addr) {
        if (!BluetoothAdapter.checkBluetoothAddress(addr)) return "Invalid address";
        BluetoothDevice dev = btAdapter.getRemoteDevice(addr);
        if (dev == null) return "Could not connect";
        stopScan();
        Log.v(TAG, "Connecting to "+ dev.getName());
        bike.bluetoothGatt = dev.connectGatt(this, false, mGattCallback);
        //bike.bluetoothGatt.connect();

        // mHandler.sendMessage(Message.obtain(null, MSG_PROGRESS, "Connecting to" +dev.getName() ));
        //listView.setVisibility(View.INVISIBLE);
        //bikeText.setText(bike.getName());
        return "Connected";
    }

    // Callback methods for GATT events.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) gatt.discoverServices();
            if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                bike.setConnectionState(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices = gatt.getServices();
                if (bike.getConnected()) return;
                int characteristicCount = 0;

                for (BluetoothGattService service : gattServices)
                    for (String s : bike.getServices()) if (service.getUuid().toString().equals(s)) {
                        Log.v(TAG, s);
                        List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
                        for (BluetoothGattCharacteristic charact : gattCharacteristics) for (String t : bike.getCharacteristics())
                            if (charact.getUuid().toString().equals(t)) {
                                bike.setCharacteristic(charact);
                                characteristicCount++;
                            }
                    }
                if (characteristicCount == bike.chrct.length) {   // All characteristics are found
                    bike.setConnectionState(true);  // Device found
                    return;  // Out of loop: for (MyBtleDevice dev : myBtleList)
                }
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            //printArray(characteristic.getValue());
            Log.v(TAG, "characterisc write: " + bytesToHex(characteristic.getValue()));
            BtleRequest req = bra.req[bra.readIndex];
            if ((req.type == 0) && (req.characteristic == characteristic)) {
                if (status == BluetoothGatt.GATT_SUCCESS) removeRequest();
                else transmitRequest(); // Try again
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "characterisc read: " + bytesToHex(characteristic.getValue()));

            BtleRequest req = bra.req[bra.readIndex];
            if ((req.type == 1) && (req.characteristic == characteristic)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bike.onCharacteristicRead(characteristic);
                    removeRequest();
                } else transmitRequest(); // Try again
                return;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // Log.e(TAG, "characterisc changed: " + bytesToHex(characteristic.getValue()));
            bike.onCharacteristicRead(characteristic);
            if (bra.req[bra.readIndex].type == 1) removeRequest();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "descriptor write: <" + bytesToHex(descriptor.getValue()) + ">");
            BtleRequest req = bra.req[bra.readIndex];
            if ((descriptor.getCharacteristic() != req.characteristic) || (req.type != 2)) return;
            if ((req.type == 2) && (req.gatt == gatt)) {
                if (status == BluetoothGatt.GATT_SUCCESS) removeRequest();
                else transmitRequest();
            }
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor desc, int status) {
            Log.e(TAG, "descriptor read: " + desc.getUuid());
            BtleRequest req = bra.req[bra.readIndex];
            if ((req.type == 3) && (req.gatt == gatt)) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bike.onDescriptorRead(desc);
                    removeRequest();
                } else transmitRequest();
                return;
            }
        }
    };

    BluetoothGattCallback getBluetoothGattCallback() {
        return mGattCallback;
    }

    public static class BtleRequest {
        public BluetoothGatt gatt;
        public BluetoothGattCharacteristic characteristic;
        int type = -1;
        byte[] data;
    }

    public static class BtleRequestArray {
        public static final int REQARRAYCOUNT = 64;
        int attempt = 0;
        int readIndex = 0;
        int writeIndex = 0;
        BtleRequest req[] = new BtleRequest[REQARRAYCOUNT];

        public BtleRequestArray() {
            for (int i = 0; i < REQARRAYCOUNT; i++) req[i] = new BtleRequest();
        }
    }

    public static void resetCommunication() {
        bra.readIndex = 0;
        bra.writeIndex = 0;   // Throw away all packets
        for (int i = 0; i < bra.REQARRAYCOUNT; i++) bra.req[i].type = -1;
        bra.attempt = 0;
    }

    public void addRequest(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int type, byte[] data) {
        if ((type < 0) || (type > 3) || (gatt == null) || (characteristic == null)) {
            Log.e(TAG, "Illegal request");
            return;
        }
        BtleRequest req = bra.req[bra.writeIndex];
        req.gatt = gatt;
        req.characteristic = characteristic;
        req.type = type;
        req.data = data;
        boolean isIdle = bra.readIndex == bra.writeIndex;
        bra.writeIndex++;
        if (bra.writeIndex == bra.REQARRAYCOUNT) bra.writeIndex = 0;
        if (isIdle) transmitRequest();
    }

    public void removeRequest() {
        bra.req[bra.readIndex].type = -1;
        bra.readIndex++;
        if (bra.readIndex == bra.REQARRAYCOUNT) bra.readIndex = 0;
        bra.attempt = 0;
        if (bra.readIndex != bra.writeIndex) transmitRequest();
    }

    void transmitRequest() {
        BtleRequest req = bra.req[bra.readIndex];
        if (bra.attempt++ > 3) {    //  Try at most three times
            Log.e(TAG, "Unable to process request of type " + req.type);
            removeRequest();
            return;
        }
        BluetoothGattCharacteristic btChar;
        switch (req.type) {
            case 0: // write characteristic request
                // Log.e(TAG, "write request : " +  bytesToHex(req.data) + "to char: " + req.characteristic.getUuid().toString());
                req.characteristic.setValue(req.data);
                if (req.gatt.writeCharacteristic(req.characteristic)) return;
                break;
            case 1: // read characteristic request

                Log.e(TAG, "read request : " +  req.characteristic.getUuid().toString());
                if (req.gatt.readCharacteristic(req.characteristic)) return;
                break;
            case 2: // write descriptor request


                BluetoothGattDescriptor desc = req.characteristic.getDescriptor(UUID.fromString(UUID_DESCRIPTOR));
                Log.e(TAG, "write descriptor request : " +  desc + " : " + "req.data");
                desc.setValue(req.data);
                if (req.gatt.writeDescriptor(desc)) return;
                break;
            case 3: // read descriptor request
                if (!readDescriptorRequest(req.characteristic.getDescriptor(UUID.fromString(UUID_DESCRIPTOR)), req.gatt)) {
                    Log.e(TAG, "Could not read descriptor of " + req.characteristic.getUuid().toString());
                    removeRequest();
                }
                return;
        }
        Log.e(TAG, "Illegal type in transmitRequest, type=" + req.type);
        removeRequest();
    }

    public boolean readDescriptorRequest(BluetoothGattDescriptor descr, BluetoothGatt gatt) {
        if (descr == null) {
            Log.e(TAG, "Null descriptor");
            return false;
        }
        for (int attempt = 0; attempt < 3; attempt++)
            if (gatt.readDescriptor(descr)) return true;
            else try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } // Give it a little delay before trying again
        return false;
    }


    public void disconnectGatt() {
        // disconnect gatt
        if(bike.bluetoothGatt != null) {
            bike.setConnectionState(false);
        }
        // unpair device
        try {
            Method method = bike.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(bike, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void setCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (gatt == null) return;

        // turn on display
        // bikeAdapter.setPower(true);



        gatt.setCharacteristicNotification(characteristic, enabled);
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
        Log.v(TAG, "Notify Charateristic: " + characteristic.getUuid().toString());
        Log.v(TAG, gatt.getDevice().getName());
    }

    public void showDevice(BluetoothDevice device) {
        String address = device.getAddress();
        int i, cnt = adapter.getCount();
        for (i=0 ; i<cnt ; i++) {
            String s = getMACAddress((String) listView.getItemAtPosition(i));
            if (address.equals(s)) return; // Already in list
        }
        adapter.add(device.getName() + ", " + device.getAddress());
    }

    public static String getMACAddress(String s) {
        int k1=-1, k2;
        while (true) {
            k2 = s.indexOf(", ");
            if (k2 == -1) break;
            k1 = k2 + 2;
            s = s.substring(k1);
        }
        return (k1==-1)? "" : s;
    }

    public final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 3];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = '-';
        }
        return new String(hexChars);
    }

}
