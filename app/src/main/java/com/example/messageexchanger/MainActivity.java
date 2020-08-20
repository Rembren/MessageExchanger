package com.example.messageexchanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Главный класс нашей программы. Программа позволяет отправлять сообщения
 * от одного телефона к другому при условии правильного
 * порта, и правильного IP тих девайсов в локальной сети.
 * Так же она позволяет выбирать по какому протоколу будут передаваться данные
 * с одного устройства на другое.
 */
public class MainActivity extends AppCompatActivity {

    //тэг для вывода логов
    private static final String TAG = "myTag";
    //название Action, которое будет слушать наш BroadcastReceiver
    public static final String MESSAGE_RECEIVED = "com.example.messageexchanger.MESSAGE_RECEIVED";

    int port;

    TextView ipAddressDisplay;
    EditText inputPort, inputIP, inputText;
    RadioButton radioTCP, radioUDP;
    RadioGroup radioGroup;
    Button sendButton;
    BroadcastReceiver broadcastReceiver;
    Context myContext = this;

    AbstractServer server = null;



    /**
     * Используется для инициализации всех переменных и View объектов и подключения
     * BroadcastReceiver
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initBroadcastReceiver();
        setUpRadioGroup();

        String deviceIP = getString(R.string.ip_prefix) + getDeviceIP();
        ipAddressDisplay.setText(deviceIP);

    }




    /**
     * Метод сделан для лучшей декомпозиции, и вынесения логически связанного куска кода
     * за пределы метода onCreate();
     */
    private void initViews(){
        ipAddressDisplay = findViewById(R.id.text_ip);
        inputPort = findViewById(R.id.portInputString);
        inputText = findViewById(R.id.textInputString);
        inputIP = findViewById(R.id.ipInputString);
        radioTCP = findViewById(R.id.tcp_ip);
        radioUDP = findViewById(R.id.udp);
        radioGroup = findViewById(R.id.radioGroup);
        sendButton = findViewById(R.id.btnSend);
    }

    /**
     * етод создан для улучшения декомпозиции кода и вынесения логически связанных операций
     * по инициализации BroadcastReceiver а пределы метода onCreate()
     */
    private void initBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MESSAGE_RECEIVED);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(
                        myContext,
                        intent.getStringExtra("text"),
                        Toast.LENGTH_LONG).show();
            }
        };
        registerReceiver(broadcastReceiver, filter);
    }


    /**
     * Метод создан для улучшения декомпозиции и вынесения логики объекта RadioGroup за пределы
     * метода onCreate()
     */
    private void setUpRadioGroup() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setUpServer(sendButton);
            }
        });
    }



    /**
     * Для более удобного пользования сами узнаем IP девайса в локальной сети
     * и возвращаем его в точку вызова
     *
     * @return готовый для вывода на экран, отформатированнфый адрес IP
     */
    private String getDeviceIP() {
        WifiManager wifiManager = (WifiManager)
                getApplicationContext().getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }


    /**
     * Срабатывает при нажатии на кнопку "SEND", был создан из-за отсутствия необходимости
     * крепить OnClickListener  кнопке.
     * @param view кнопка, при нажатии на которую вызывается метод. Этот параметр не используется
     */
    public void sendMessage(View view) {
        AsyncTask<String, Void, Void> send = chooseSender();

        send.execute(
                inputIP.getText().toString(),
                inputText.getText().toString(),
                inputPort.getText().toString());

        inputText.setText("");
    }

    /**
     * Сделан для того, чтобы вынести логику определения протокола орбмена за пределы метода
     * отправки сообщений
     *
     * @return объект класса, который работает с нужным протоколом
     */
    private AsyncTask<String, Void, Void> chooseSender(){
        if (radioTCP.isChecked()) {
            return new TCPIPSender();
        } else {
            return new UDPSender();
        }
    }

    /**
     * Срабатывает при нажатии на кнопку "SET PORT", добавлен из-за отсутствия необходимости в
     * OnClickListener из-за простоты выполняемой задачи
     *
     * @param view кнопка, при нажатии на которую вызывается метод. Этот параметр не используется
     */
    public void setUpServer(View view) {
        port = Integer.parseInt(inputPort.getText().toString());

        if (server != null){
            Log.d(TAG, "setUpServer: stopping...");
            server.stopServer();
        }

        server = chooseServer();
        server.startServer();
    }

    /**
     * Сделан для вынесения логики выбора нужного класса за пределы метода, поднимающего сервер
     * @return класс, который работает с нужным протоколом
     */
    private AbstractServer chooseServer() {
        if (radioTCP.isChecked()) {
            return new TCPIPServer(this, port);
        } else {
            return new UDPServer(this, port);
        }
    }



}