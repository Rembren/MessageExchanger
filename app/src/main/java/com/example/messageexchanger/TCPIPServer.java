package com.example.messageexchanger;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Класс был создан для того, чтобы быть своеобразным сервером, который будет принимать
 * сообщения, которые разсылаются по заданому порту, данынй класс принимает пакеты по TCP/IP
 */
public class TCPIPServer extends AbstractServer {

    private int port;

    private AtomicBoolean run = new AtomicBoolean(false);

    private Context context;

    /**
     * Такой конструктор использован чтобы получить доступ к context от Activity, которая создает
     * объект этого класса, а так же получить значение порта, который будет слушать ServerSocket
     *
     * @param myContext context Activity, которая вызывает данный конструктор
     * @param port      порт, который нужно слушать
     */
    public TCPIPServer(Context myContext, int port) {
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
        //условие выхода - переключение флага. Флаг переключается при переходе из одного
        //порта на другой или при переходе между протоколами передачи данных
        while (run.get()) {
            try {
                //По умолчанию сокеты в андроид используют протокол передачи данных TCP/IP
                ServerSocket serverSocket = new ServerSocket(port);
                //Без этого сокет будет ждать сообщение пока оно не придет, это создает баги при
                //перейти на новый порт или протокол т.к. поток не остановится пока не примет еще
                //одно сообщение
                serverSocket.setSoTimeout(1000);
                String message;
                Log.d("myTag", "run: tcp/ip");
                Socket socket = serverSocket.accept();
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                message = dataInputStream.readUTF();
                Intent intent = new Intent(MainActivity.MESSAGE_RECEIVED);
                intent.putExtra("text", message);
                context.sendBroadcast(intent);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("myTag", "run: stop tcp/ip");
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
