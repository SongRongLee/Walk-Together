package tw.com.chiaotung.walktogether;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.nctu1210.api.koala3x.KoalaDevice;
import cc.nctu1210.api.koala3x.SensorEvent;
import cc.nctu1210.api.koala3x.SensorEventListener;
import tw.com.chiaotung.walktogether.view.CustomAdapter;
import tw.com.chiaotung.walktogether.view.ModelObject;


public class ScanDevice extends Activity implements AdapterView.OnItemClickListener, SensorEventListener {

    LocalStoreController storeController;
    public static final String CONNECTION = "Connectioin_status";
    public static final String STEPS = "steps";
    private static final long SCAN_PERIOD = 2000;
    private static final int REQUEST_ENABLE_BT = 1;
    private final static String TAG = ScanDevice.class.getSimpleName();
    //Blue Tooth Scan
    private boolean startScan = false;
    public static BluetoothAdapter mBluetoothAdapter;
    public static BluetoothLeScanner mBLEScanner;
    public static ScanSettings settings;
    public static List<ScanFilter> filters;
    public static ArrayList<KoalaDevice> mDevices = new ArrayList<KoalaDevice>();  // Manage the devices
    public static ArrayList<AtomicBoolean> mFlags = new ArrayList<AtomicBoolean>();


    private Button btn_scan;
    private ProgressBar progressBar;
    private ListView device_list;
    private ListView used_device_list;
    private String[] list = {"Device1","Device2","Device3"};
    private ArrayAdapter<String> listAdapter;

    // ListView Related
    private String DEVICE_NAME = "name";
    private String DEVICE_ADDRESS = "address";
    private String DEVICE_RSSI = "rssi";
    private String DEVICE_GX = "gX";
    private String DEVICE_GY = "gY";
    private String DEVICE_GZ = "gZ";
    private ListView listView;
    private List<ModelObject> mObjects = new ArrayList<ModelObject>();
    private List<ModelObject> used_mObjects = new ArrayList<ModelObject>();
    private CustomAdapter mAdapter;
    private CustomAdapter used_mAdapter;

/*
    private void displayRSSI(final int position, final float rssi) {
        ModelObject object = mObjects.get(position);
        object.setRssi(rssi);
        mAdapter.notifyDataSetChanged();
    }


    private void displayPDRData(final int position, final float step) {
        ModelObject object = mObjects.get(position);
        object.setPedometerData(step, 0, 0, 0);
        mAdapter.notifyDataSetChanged();
    }


*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_device);
        storeController=new LocalStoreController(this);
        btn_scan = (Button)findViewById(R.id.btn_scan);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        device_list = (ListView)findViewById(R.id.device_list);
        mAdapter = new CustomAdapter(this, mObjects);
        device_list.setAdapter(mAdapter);
        device_list.setOnItemClickListener(this);

        btn_scan.setOnClickListener(scanListener);


        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Ble not supported", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        ModelObject returnedUsedDevice = storeController.getUsedDevice();
        //final KoalaDevice returnedKoala = storeController.getUsedDevice_Koala();
        //final AtomicBoolean returnFlag = storeController.getUsedFlag();
        if (returnedUsedDevice!=null) {
            used_device_list = (ListView)findViewById(R.id.used_device_list);
            used_mAdapter = new CustomAdapter(this, used_mObjects);
            used_device_list.setAdapter(used_mAdapter);
            used_mAdapter.getData().clear();
            used_mObjects.add(returnedUsedDevice);
            used_mAdapter.notifyDataSetChanged();
            final String used_address = returnedUsedDevice.getAddress();
            used_device_list.setOnItemClickListener(this);
            used_device_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    //returnedKoala.setConnectedTime();
                    //mFlags.clear();
                    //mFlags.add(returnFlag);
                    //UserStatus.mServiceManager.disconnect();
                    Log.d(TAG, "Connecting to device:" + used_address);
                    UserStatus.mServiceManager.connect(used_address);
                    Intent intent_connected = new Intent();
                    intent_connected.setClass(ScanDevice.this, UserStatus.class);
                    intent_connected.putExtra(CONNECTION, 1);
                    startActivity(intent_connected);
                    finish();
                }
            });
        }
        //UserStatus.mServiceManager = new KoalaServiceManager(this);
       // UserStatus.mServiceManager.registerSensorEventListener(this, SensorEvent.TYPE_PEDOMETER);
    }




    private Button.OnClickListener scanListener = new Button.OnClickListener() {
        public void onClick(View v) {
            if (!startScan) {
                startScan = true;
                btn_scan.setText("Stop scan");
                progressBar.setVisibility(View.VISIBLE);
                mDevices.clear();
                mFlags.clear();
                // Start to scan the ble device
                scanLeDevice();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setupListView();
                        btn_scan.setText("Scan");
                        progressBar.setVisibility(View.INVISIBLE);
                        startScan=false;
                    }
                }, SCAN_PERIOD+1000);
                /*try {
                    Thread.sleep(SCAN_PERIOD+1000);

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            } else {
                startScan = false;
                UserStatus.mServiceManager.disconnect();
                UserStatus.mServiceManager.close();
                mDevices.clear();
                mFlags.clear();
                setupListView();
                btn_scan.setText("Scan");
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    };

    private void setupListView() {
        mAdapter.getData().clear();
        Log.i(TAG, "Initializing ListView....."+ mAdapter.getData().size());
        for (int i = 0, size = mDevices.size(); i < size; i++) {
            KoalaDevice d = mDevices.get(i);
            ModelObject object = new ModelObject(d.getDevice().getName(), d.getDevice().getAddress(), String.valueOf(d.getRssi()));
            mObjects.add(object);
        }
        Log.i(TAG, "Initialized ListView....."+ mAdapter.getData().size());

        mAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (Build.VERSION.SDK_INT >= 21) {
                mBLEScanner =mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build();
                filters = new ArrayList<ScanFilter>();
            }
        }
    }

    private void scanLeDevice() {
        new Thread(){
            @Override
            public void run(){
                if (Build.VERSION.SDK_INT < 21) {
                    mBluetoothAdapter.startLeScan(mLeScanCallback);

                    try {
                        Thread.sleep(SCAN_PERIOD);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
                else {
                    mBLEScanner.startScan(mScanCallback);

                    try {
                        Thread.sleep(SCAN_PERIOD);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBLEScanner.stopScan(mScanCallback);
                }
            }
        }.start();
    }

    /**
     * The event callback to handle the found of near le devices
     * For SDK version < 21.
     *
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             final byte[] scanRecord) {

            new Thread() {
                @Override
                public void run() {
                    if (device != null) {
                        KoalaDevice p = new KoalaDevice(device, rssi, scanRecord);
                        int position = findKoalaDevice(device.getAddress());
                        if (position == -1) {
                            AtomicBoolean flag = new AtomicBoolean(false);
                            mDevices.add(p);
                            mFlags.add(flag);
                            Log.i(TAG, "Find device:"+p.getDevice().getAddress());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    setupListView();
                                }
                            });
                        }
                    }
                }
            }.start();
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());
            final ScanResult scanResult = result;
            final BluetoothDevice device = scanResult.getDevice();

            new Thread() {
                @Override
                public void run() {
                    if (device != null) {
                        KoalaDevice p = new KoalaDevice(device, scanResult.getRssi(), scanResult.getScanRecord().getBytes());
                        int position = findKoalaDevice(device.getAddress());
                        if (position == -1) {
                            AtomicBoolean flag = new AtomicBoolean(false);
                            mDevices.add(p);
                            mFlags.add(flag);
                            Log.i(TAG, "Find device:" + p.getDevice().getAddress());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // This code will always run on the UI thread, therefore is safe to modify UI elements.
                                    setupListView();
                                }
                            });
                        }
                    }
                }
            }.start();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private int findKoalaDevice(String macAddr) {
        if (mDevices.size() == 0)
            return -1;
        for (int i = 0; i < mDevices.size(); i++) {
            KoalaDevice tmpDevice = mDevices.get(i);
            if (macAddr.matches(tmpDevice.getDevice().getAddress()))
                return i;
        }
        return -1;
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i=0; i<mFlags.size(); i++) {
            AtomicBoolean flag = mFlags.get(i);
            flag.set(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_CANCELED) {
                //Bluetooth not enabled.
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //System.exit(0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

        String macAddress = mAdapter.getData().get(position).getAddress();
        storeController.clearDevice();
        KoalaDevice d = mDevices.get(position);
        storeController.storeDevice(mAdapter.getData().get(position));
        Log.d(TAG, "Connecting to device:" + macAddress);
        d.setConnectedTime();

        UserStatus.mServiceManager.connect(macAddress);
        Intent intent_connected = new Intent();
        intent_connected.setClass(this, UserStatus.class);
    //    intent_connected.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent_connected.putExtra(CONNECTION, 1);
        startActivity(intent_connected);
        finish();
    }

    @Override
    public void onSensorChange(final SensorEvent e) {
/*
        final int eventType = e.type;
        final double values [] = new double[3];
        final int position2 = findKoalaDevice(e.device.getAddress());
        if (position2 != -1) {
            Log.d(TAG, "time=" + System.currentTimeMillis() + "step counts:" + e.values[0] + "\n");
        }
*/

    }

    @Override
    public void onConnectionStatusChange(boolean status) {
    }

    @Override
    public void onRSSIChange(String addr, float rssi) {
        /*
        final int position = findKoalaDevice(addr);
        if (position != -1) {
            Log.d(TAG, "mac Address:"+addr+" rssi:"+rssi);
            displayRSSI(position, rssi);
        }*/
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        //System.exit(0);
    }

}

