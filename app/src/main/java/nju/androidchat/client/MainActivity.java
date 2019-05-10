package nju.androidchat.client;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Button btn1 = findViewById(R.id.btn_link);
        EditText editText1 = findViewById(R.id.ip_input);


        btn1.setOnClickListener(v -> {
            // TODO Auto-generated method stub

            Intent intent = new Intent(MainActivity.this, TalkActivity.class);
            startActivity(intent);
        });
    }

    private boolean link_to_server(String ip, String username) {
        boolean successful = false;

        return successful;
    }

    ;
}
