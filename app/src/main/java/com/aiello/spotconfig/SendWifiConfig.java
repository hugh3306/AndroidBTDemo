package com.aiello.spotconfig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnDragInitiatedListener;
import androidx.recyclerview.selection.OnItemActivatedListener;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class SendWifiConfig extends AppCompatActivity {

    private static final String TAG = "spotconfig";
    static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 101;
    private BluetoothAdapter bluetoothAdapter = null;

    private RecyclerView recyclerView;
    private RvScanResultAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<DeviceItem> deviceItemList;
    SelectionTracker selectionTracker;
    private boolean testFlag = false;

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.i(TAG, "found device " + deviceName + " MAC: " + deviceHardwareAddress);
                DeviceItem newDevice = new DeviceItem(deviceName, deviceHardwareAddress);
                deviceItemList.add(newDevice);
                mAdapter.setItems(deviceItemList);
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Enable Bluetooth successfully");
            } else {
                Log.i(TAG, "Enable Bluetooth failed, resultCode = " + resultCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "REQUEST_ACCESS_FINE_LOCATION ok");
                } else {
                    Log.i(TAG, "REQUEST_ACCESS_FINE_LOCATION denied");
                }
                return;
            }
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_wifi_config);

        recyclerView = findViewById(R.id.id_rv_bt_scan_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        deviceItemList = new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new RvScanResultAdapter(deviceItemList);
        recyclerView.setAdapter(mAdapter);

        selectionTracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(1, deviceItemList),
                new MyItemLookup(recyclerView),
                StorageStrategy.createLongStorage()
        )
                .withOnItemActivatedListener(new OnItemActivatedListener<Long>() {
                    @Override
                    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails<Long> item, @NonNull MotionEvent e) {
                        Log.i(TAG, "Selected ItemId: " + item.getPosition());
                        return true;
                    }
                })
                .withOnDragInitiatedListener(new OnDragInitiatedListener() {
                    @Override
                    public boolean onDragInitiated(@NonNull MotionEvent e) {
                        Log.i(TAG, "onDragInitiated");
                        return true;
                    }

                })
                .build();
        mAdapter.setSelectionTracker(selectionTracker);

        /*
        selectionTracker.addObserver(new SelectionTracker.SelectionObserver() {
            @Override
            public void onItemStateChanged(@NonNull Object key, boolean selected) {
                super.onItemStateChanged(key, selected);
            }

            @Override
            public void onSelectionRefresh() {
                super.onSelectionRefresh();
            }

            @Override
            public void onSelectionChanged() {
                super.onSelectionChanged();
                if (selectionTracker.hasSelection() && !testFlag) {
                    Log.i(TAG, "mike1 + size = " + selectionTracker.getSelection().size());
                    testFlag = true;
                } else if (!selectionTracker.hasSelection() && testFlag) {
                    Log.i(TAG, "mike2");
                    testFlag = false;
                } else {
                    Log.i(TAG, "mike3 + size = " + selectionTracker.getSelection().size());
                }
                Iterator<DeviceItem> itemIterable = selectionTracker.getSelection().iterator();
                while (itemIterable.hasNext()) {
                    Log.i(TAG, itemIterable.next().getAddress());
                }
            }

            @Override
            public void onSelectionRestored() {
                super.onSelectionRestored();
            }
        });
        */

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Log.w(TAG, "Device doesn't support Bluetooth");
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    protected  void onPause() {
        super.onPause();
        if (bluetoothAdapter.isDiscovering()) {
            Log.i(TAG, "stop BT discovery");
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

    public void pushFileOverOpp(String filename) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setPackage("com.android.bluetooth"); //bluetooth package name, default opp

        File path = getFilesDir();//Environment.getExternalStoragePublicDirectory("/");
        Log.i(TAG, "mike = " + path.getAbsolutePath());
        File file = new File(path, filename);
        if (!file.exists()) {
            Log.w(TAG, "No such file " + filename + " exists!");
            return;
        }

        Log.i(TAG, "Sharing file over bluetooth " + file.getAbsolutePath());

        Uri u = FileProvider.getUriForFile(this, "com.aiello.spotconfig.fileprovider", file);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, u);
        //intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
        //intent.setType("*/*"); // supports all mime types

        //getApplicationContext().grantUriPermission("com.android.bluetooth", u,
        //        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //startActivity(Intent.createChooser(intent, "Share file"));
        startActivity(intent);
    }

    public void onBtScanBtnClick(View view) {
        Log.i(TAG, "onBtScanBtnClick");
        deviceItemList.clear();
        mAdapter.setItems(deviceItemList);
        mAdapter.notifyDataSetChanged();
        if (bluetoothAdapter.isDiscovering() != true) {
            Log.i(TAG, "start BT discovery");
            bluetoothAdapter.startDiscovery();
        }
    }

    public void onSendBtnClick(View view) {
        pushFileOverOpp("test.txt");
    }
}
