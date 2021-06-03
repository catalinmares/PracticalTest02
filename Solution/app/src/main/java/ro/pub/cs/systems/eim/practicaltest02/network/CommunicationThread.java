package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class CommunicationThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;
    private String user;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
        this.user = socket.getInetAddress().toString();
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");

            String command = bufferedReader.readLine();

            if (command == null || command.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
                return;
            }

            printWriter.println("You sent the command " + command);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
