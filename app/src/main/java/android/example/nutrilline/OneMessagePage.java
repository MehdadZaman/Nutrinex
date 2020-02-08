package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class OneMessagePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_message_page);
    }

    public void backToMessages(View v)
    {
        Intent intent = new Intent(this, MessagesPage.class);
        startActivity(intent);
    }
}
