package com.example.messageexchanger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Класс был создан для того, чтобы быть своеобразным сервером, который будет принимать
 * сообщения, которые разсылаются по заданому порту, данынй класс принимает пакеты по UDP
 */
public class UDPServer extends AbstractServer {

    private Context context;

    private int port;

    private AtomicBoolean run = new AtomicBoolean(false);

    /**
     * Конструктор создан таким для получения контекста вызывающей его activity, чтобы иметь доступ
     * к методу sendBroadcast()
     *
     * @param myContext контекс activity, вызивающей данный конструктор
     * @param port порт, который будет слушать сервер
     */
    public UDPServer(Context myContext, int port) {
        context = myContext;
        this.port = port;
    }

    /**
     * Используя цикл и ограниченный по времени socket ы обеспечиваем возможность стабиль принимать
     * данные и убираем баги связанные с переходом между портами / протоколами
     */
    @Override
    public void run() {
        run.set(true);
        while (run.get()) {
            try {
                //DatagramSocket в отличии от ServerSocket использует UDP как протокол по умолчанию
                //сокеты Datagram принимают и передают данные используя UDP
                DatagramSocket datagramSocket = new DatagramSocket(port);
                //Без этого сокет будет ждать сообщение пока оно не придет, это создает баги при
                //перейти на новый порт или протокол т.к. поток не остановится пока не примет еще
                //одно сообщение
                datagramSocket.setSoTimeout(1000);

                Log.d("myTag", "run: udp");

                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                String message = new String(buffer);

                Intent intent = new Intent(MainActivity.MESSAGE_RECEIVED);
                intent.putExtra("text", message);
                context.sendBroadcast(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("myTag", "run: stop udp");
    }

    /**
     * Метод создан для более удобной остановки сервера, использует для этого флаг
     */
    public void stopServer() {
        run.set(false);
    }

    /**
     * Метод создан для более удобного включения сервера приема сообщений из внешних классов
     */
    public void startServer() {
        Thread serverThread = new Thread(this);
        serverThread.start();
    }
}
