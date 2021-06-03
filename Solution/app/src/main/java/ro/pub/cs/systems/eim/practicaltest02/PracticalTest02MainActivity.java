package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.network.ClientThread;
import ro.pub.cs.systems.eim.practicaltest02.network.ServerThread;

public class PracticalTest02MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private Button connectButton;

    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private EditText hourEditText;
    private EditText minuteEditText;
    private Button setButton;
    private Button resetButton;
    private Button pollButton;
    private TextView resultTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();

            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));

            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
            }

            serverThread.start();
        }
    }

    private final SetAlarmButtonClickListener setAlarmButtonClickListener = new SetAlarmButtonClickListener();
    private class SetAlarmButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String hour = hourEditText.getText().toString();
            String minute = minuteEditText.getText().toString();

            if (hour == null || hour.isEmpty() || minute == null || minute.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Parameters from client (hour / minute) should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Integer.parseInt(hour) < 0 || Integer.parseInt(hour) > 23) {
                Toast.makeText(getApplicationContext(), "Hour should respect the 24h format!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Integer.parseInt(minute) < 0 || Integer.parseInt(minute) > 59) {
                Toast.makeText(getApplicationContext(), "Minute should be between 0 and 59!", Toast.LENGTH_SHORT).show();
                return;
            }

            String command = "set," + hour + "," + minute + "\n";

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), command, resultTextView);
            clientThread.start();
        }
    }

    private final ResetAlarmButtonClickListener resetAlarmButtonClickListener = new ResetAlarmButtonClickListener();
    private class ResetAlarmButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String command = "reset\n";

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), command, resultTextView);
            clientThread.start();
        }
    }

    private final PollAlarmButtonClickListener pollAlarmButtonClickListener = new PollAlarmButtonClickListener();
    private class PollAlarmButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();

            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String command = "poll\n";

            resultTextView.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), command, resultTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onCreate() callback method has been invoked");
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = findViewById(R.id.server_port);
        connectButton = findViewById(R.id.server_button);
        connectButton.setOnClickListener(connectButtonClickListener);

        clientAddressEditText = findViewById(R.id.client_address);
        clientPortEditText = findViewById(R.id.client_port);
        hourEditText = findViewById(R.id.client_hour);
        minuteEditText = findViewById(R.id.client_minute);
        setButton = findViewById(R.id.set_button);
        setButton.setOnClickListener(setAlarmButtonClickListener);
        resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(resetAlarmButtonClickListener);
        pollButton = findViewById(R.id.poll_button);
        pollButton.setOnClickListener(pollAlarmButtonClickListener);
        resultTextView = findViewById(R.id.result);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");

        if (serverThread != null) {
            serverThread.stopThread();
        }

        super.onDestroy();
    }
}