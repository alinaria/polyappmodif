package com.example.myapplication;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    private Button mButtonlisten;
    private Button mButtongetnewdevices;
    private Button mButtongetvisible;
    private Button mButtonsend;
    private TextView mTextreceived;
    private TextView mTextreceiveddisplayed;
    private TextView mTextstatus;
    private EditText mTexttosend;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice[] btArray;
    private ListView mListdevices;
    private ArrayAdapter<String> mArrayAdapter;
    private IntentFilter filter;

    int REQUEST_ENABLE_BLUETOOTH = 1;
    int REQUEST_ENABLE_BLUETOOTH_SCAN = 2;
    int ACTION_REQUEST_DISCOVERABLE = 3;
    int REQUEST_ENABLE_ACCESS_FINE_LOCATION = 4;
    int REQUEST_ENABLE_ACCESS_COARSE_LOCATION = 5;
    int REQUEST_ENABLE_BLUETOOTH_CONNECT = 6;

    private static final UUID MY_UUID = UUID.fromString("8b8eb972-ac3e-11eb-8529-0242ac130003");

    SendReceive sendReceive;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        mButtonlisten = (Button) findViewById(R.id.buttonlisten);
        mButtongetnewdevices = (Button) findViewById(R.id.buttongetnewdevices);
        mButtongetvisible = (Button) findViewById(R.id.buttongetvisible);
        mButtonsend = (Button) findViewById(R.id.buttonsend);
        mTextreceived = (TextView) findViewById(R.id.textreceived);
        mTextreceiveddisplayed = (TextView) findViewById(R.id.textreceiveddisplayed);
        mTextstatus = (TextView) findViewById(R.id.textstatus);
        mTexttosend = (EditText) findViewById(R.id.texttosend);
        mListdevices = (ListView) findViewById(R.id.listdevices);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        ServerClass serverClass = new ServerClass();
        serverClass.start();

        if (mBluetoothAdapter == null) {
            // affichage de courte durée
            Toast.makeText(getApplicationContext(), "Votre appareil ne prend pas en charge le Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_ENABLE_BLUETOOTH);
            }
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);

        }


        mButtonlisten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_ENABLE_BLUETOOTH);
                }

                if (!mArrayAdapter.isEmpty()) mArrayAdapter.clear();
                Set<BluetoothDevice> bt = mBluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress();
                        strings[index] = deviceName + "\n" + deviceHardwareAddress;
                        index++;
                    }
                    //mArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    mArrayAdapter.addAll(strings);
                    mListdevices.setAdapter(mArrayAdapter);
                }
            }
        });

        mButtongetvisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeDiscoverable();
            }
        });

        mButtongetnewdevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_SCAN},
                            REQUEST_ENABLE_BLUETOOTH_SCAN);
                }
                mArrayAdapter.clear();

                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
                Log.d("INFO", "ça marche pas ");
                filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                Log.d("INFO", BluetoothDevice.ACTION_FOUND.toString());
                registerReceiver(receiver, filter);
                Log.d("INFO", "ça marche pas2 ");

            }
        });

        mListdevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass = new ClientClass(btArray[i]);
                clientClass.start();

                mTextstatus.setText("Connecting");
            }
        });

        mButtonsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = String.valueOf(mTexttosend.getText());
                sendReceive.write(string.getBytes());
            }
        });

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case STATE_LISTENING:
                    mTextstatus.setText("Listening");
                    break;
                case STATE_CONNECTING:
                    mTextstatus.setText("Connecting");
                    break;
                case STATE_CONNECTED:
                    mTextstatus.setText("Connected");
                    break;
                case STATE_CONNECTION_FAILED:
                    mTextstatus.setText("Connection Failed");
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    mTextreceiveddisplayed.setText(tempMsg);
                    break;
            }
            return true;
        }
    });

    /** Rendre le périphérique découvrable via les paramètres du système.*/
    private void makeDiscoverable() {
        Intent discoverableIntent = new Intent(
                BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(
                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 180);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BluetoothActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE},
                    ACTION_REQUEST_DISCOVERABLE);
        }
        startActivity(discoverableIntent);
    }


    /* Création d’un BroadcastReceiver pour ACTION_FOUND.*/
    public final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("INFO", "ça marche");
            String action = intent.getAction();
            // si un appareil est découvert
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // obtenir l’objet BluetoothDevice et ses informations
                // à partir de l’intent
                BluetoothDevice device =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_ENABLE_BLUETOOTH);
                }
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                mArrayAdapter.add(deviceName + "\n" + deviceHardwareAddress);
                mListdevices.setAdapter(mArrayAdapter);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(BluetoothActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_ENABLE_BLUETOOTH_SCAN);
        }
        // ne pas oublier de désenregistrer le récepteur ACTION_FOUND
        unregisterReceiver(receiver);
    }


    private class ServerClass extends Thread {
        private BluetoothServerSocket serverSocket;

        public ServerClass() {
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_ENABLE_BLUETOOTH);
                }
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Android_project", MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket socket = null;

            while (socket == null) {
                try {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTING;
                    handler.sendMessage(message);

                    socket = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if (socket != null) {
                    Message message = Message.obtain();
                    message.what = STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive = new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass(BluetoothDevice device1) {
            device = device1;

            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_ENABLE_BLUETOOTH);
                }
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                if (ActivityCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BluetoothActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                            REQUEST_ENABLE_BLUETOOTH);
                }
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}