package p.chapter8;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet4Address;
import java.util.Set;

public class MainActivity extends Activity {
    BluetoothAdapter bluetoothAdapter;
    TextView textView;
    WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.tv1);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.v("BLUETOOTH", "FOUND" + action);
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Log.i("BLUETOOTH", "FOUND");
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.i("BLUETOOTH", "FINISHED");
                }
            }
        };


        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, intentFilter1);

    }

    public void openBlueTooth(View v) {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, 1);   //用户允许才能打开

            bluetoothAdapter.enable();//静默打开

        } else {
            Toast.makeText(this, "Blue tooth already opened", Toast.LENGTH_LONG).show();
//
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getBoundDevices(View view) {
        Set<BluetoothDevice> bd = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : bd) {
            Log.v("address", bluetoothDevice.getAddress());
        }
    }

    public void startsearch(View view) {
        bluetoothAdapter.startDiscovery();

    }

    public void openWifiSetting(View view) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    public void openWifi(View view) {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "already opened", Toast.LENGTH_LONG).show();
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            textView.setText(wifiInfo.getBSSID() + "  " + wifiInfo.getMacAddress() + "  " + wifiInfo
                    .getSSID() + " " + getIPAddress(wifiInfo.getIpAddress()) + "  " + wifiInfo
                    .getLinkSpeed());
        } else {
            wifiManager.setWifiEnabled(true);
        }
    }

    public void showConnectedWifi(View view) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < wifiManager.getConfiguredNetworks().size(); i++) {
            WifiConfiguration wifiConfiguration = wifiManager.getConfiguredNetworks().get(i);

            stringBuilder.append(wifiConfiguration.SSID + ":" +
                    ((wifiConfiguration.status == WifiConfiguration.Status.CURRENT)
                            ? "connected" : "disconnected") + "\n");
        }
        textView.setText(stringBuilder.toString());
    }

    public String getIPAddress(int deciAddress) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & deciAddress);
            bytes[1] = (byte) ((0xff00 & deciAddress) >> 8);
            bytes[2] = (byte) ((0xff0000 & deciAddress) >> 16);
            bytes[3] = (byte) ((0xff000000 & deciAddress) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


}
