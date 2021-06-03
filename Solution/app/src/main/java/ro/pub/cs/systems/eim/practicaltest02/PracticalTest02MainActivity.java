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
    private TextView weatherForecastTextView;

    private ServerThread serverThread;
    private ClientThread clientThread;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();

            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port should be filled!", Toast.LENGTH_SHORT).show();
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

        }
    }

    private final ResetAlarmButtonClickListener resetAlarmButtonClickListener = new ResetAlarmButtonClickListener();
    private class ResetAlarmButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    private final PollAlarmButtonClickListener pollAlarmButtonClickListener = new PollAlarmButtonClickListener();
    private class PollAlarmButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

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