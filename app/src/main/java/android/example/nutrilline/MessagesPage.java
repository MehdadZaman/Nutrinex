package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MessagesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages_page);
    }

    public void goToMessage(View v){
        Intent intent = new Intent(this, OneMessagePage.class);
        startActivity(intent);
    }

    public void backToHomeMessages(View v){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
