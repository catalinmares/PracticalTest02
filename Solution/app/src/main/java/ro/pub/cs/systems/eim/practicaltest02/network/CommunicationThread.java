package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.Alarm;

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

            HashMap<String, Alarm> data = serverThread.getData();
            Alarm alarm;

            if (command.startsWith("set")) {
                String[] parts = command.split(",");
                alarm = new Alarm(
                        parts[1],
                        parts[2],
                        false
                );

                if (data.containsKey(user)) {
                    printWriter.println("Old alarm has been replaced with alarm at " + alarm.getHour() + ":" + alarm.getMinute());
                } else {
                    printWriter.println("Created a new alarm at " + alarm.getHour() + ":" + alarm.getMinute());
                }

                serverThread.setData(user, alarm);
                printWriter.flush();
            } else if (command.startsWith("reset")) {
                if (data.containsKey(user)) {
                    printWriter.println("Alarm reset.");
                    serverThread.removeData(user);
                } else {
                    printWriter.println("No alarm was set before.");
                }

                printWriter.flush();
            } else if (command.startsWith("poll")) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received a poll request");
                if (data.containsKey(user)) {
                    alarm = data.get(user);

                    if (alarm.isActive()) {
                        printWriter.println("The server cache says that the alarm at " + alarm.getHour() + ":" + alarm.getMinute() + " is active");
                    } else {
                        try {
                            Socket serviceSocket = new Socket(Constants.SERVER_HOST, Constants.SERVER_PORT);

                            if (serviceSocket == null) {
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Could not create socket!");
                                return;
                            }

                            BufferedReader serviceReader = Utilities.getReader(serviceSocket);

                            if (serviceReader == null) {
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                                return;
                            }

                            String result;

                            while ((result = serviceReader.readLine()) != null) {
                                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Received from service: " + result);

                                if (result != null && !result.isEmpty()) {
                                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Hello!");
                                    String[] parts = result.split(" ");
                                    String time = parts[2];

                                    String[] timeParts = time.split(":");
                                    String timeHour = timeParts[0];
                                    String timeMinute = timeParts[1];

                                    Log.i(Constants.TAG, timeHour + ":" + timeMinute);

                                    if (Integer.parseInt(alarm.getHour()) < Integer.parseInt(timeHour) || Integer.parseInt(alarm.getHour()) == Integer.parseInt(timeHour) && Integer.parseInt(alarm.getMinute()) < Integer.parseInt(timeMinute)) {
                                        alarm.setActive(true);
                                        serverThread.setData(user, alarm);
                                        Log.i(Constants.TAG, "Printing active");
                                        printWriter.println(Constants.ACTIVE);
                                    } else {
                                        Log.i(Constants.TAG, "Printing inactive");
                                        printWriter.println(Constants.INACTIVE);
                                    }

                                    printWriter.flush();
                                }
                            }
                        } catch (IOException ioException) {
                            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                            if (Constants.DEBUG) {
                                ioException.printStackTrace();
                            }
                        } finally {
                            if (socket != null) {
                                try {
                                    socket.close();
                                } catch (IOException ioException) {
                                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                                    if (Constants.DEBUG) {
                                        ioException.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } else {
                    printWriter.println(Constants.NONE);
                }

                printWriter.flush();
            }
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
