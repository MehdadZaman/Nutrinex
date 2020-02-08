package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class NutritionistHomePage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView emailView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutritionist_home_page);
    }

    public void onClickPatientsButton(View view){
        Intent intent = new Intent(this, PatientsListPage.class);
        startActivity(intent);
    }
    public void onClickLogout2(View v){
        Intent intent = new Intent(this, MultiLoginPage.class);
        startActivity(intent);
    }
}
