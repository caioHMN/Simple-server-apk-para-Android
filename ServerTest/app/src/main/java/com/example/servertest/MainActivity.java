package com.example.servertest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText portEditText;
    private Button startButton;
    private Button stopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        portEditText = findViewById(R.id.portEditText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String port = portEditText.getText().toString();
                if (!port.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ServerService.class);
                    intent.putExtra("PORT", Integer.parseInt(port));
                    startService(intent);
                    Toast.makeText(MainActivity.this, "Server started on port " + port, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter a port number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, ServerService.class));
                Toast.makeText(MainActivity.this, "Server stopped", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
