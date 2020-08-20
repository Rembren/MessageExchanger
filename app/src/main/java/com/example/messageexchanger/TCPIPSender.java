package com.example.messageexchanger;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Класс, расширивающий AsyncTask, который создан для быстрой отправки сообщения.
 * AsyncTask был выбран как базовый, потому что он больше всего подходит выполнения быстрой задаче
 * на фоне
 */
public class TCPIPSender extends AsyncTask<String, Void, Void> {

    /**
     * Метод для инициализации объектов связанных с отправкой сообщения и непосредственно
     * самой отправки
     *
     * @param strings массив объектов string которые передаются при нициализации класса
     * @return void
     */
    @Override
    protected Void doInBackground(String... strings) {

        String ip = strings[0];
        String message = strings[1];
        int port = Integer.parseInt(strings[2]);

        try {

            Socket socket = new Socket(ip, port);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(message);
            dataOutputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
