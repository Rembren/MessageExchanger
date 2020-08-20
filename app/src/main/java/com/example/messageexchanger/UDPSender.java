package com.example.messageexchanger;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Класс использует AsyncTask в качестве базового метода так как тот лучше всего подходит
 * для выполнения коротких задач
 */
public class UDPSender extends AsyncTask<String, Void, Void> {

    /**
     * Метод считывает данные с массива параметров, и использует DatagramPack и DatagramSocket
     * для передачи данных, так как они по умолчанию используют UDP
     *
     * @param strings массив строк, передающихся как параметры при создании объекта
     * @return Void
     */
    @Override
    protected Void doInBackground(String... strings) {

        String ip = strings[0];
        String message = strings[1];
        int port = Integer.parseInt(strings[2]);

        try {
            DatagramSocket clientSocket = new DatagramSocket();
            clientSocket.setBroadcast(true);
            byte[] sendData = message.getBytes();
            DatagramPacket datagramPacket = new DatagramPacket(
                    sendData, sendData.length, InetAddress.getByName(ip), port);
            clientSocket.send(datagramPacket);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
