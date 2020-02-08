package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AllNutritionalLogsDoctorPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_nutritional_logs_doctor_page);
    }

    public void goToLogFeb7_02(View v){
        Intent intent = new Intent(this, DailyLogPage2.class);
        startActivity(intent);
    }

    public void goToLogFeb6_02(View v){
        Intent intent = new Intent(this, DailyLogPage2.class);
        startActivity(intent);
    }

    public void goToLogFeb5_02(View v){
        Intent intent = new Intent(this, DailyLogPage2.class);
        startActivity(intent);
    }

    public void goToLogFeb4_02(View v){
        Intent intent = new Intent(this, DailyLogPage2.class);
        startActivity(intent);
    }

    public void goToLogFeb3_02(View v){
        Intent intent = new Intent(this, DailyLogPage2.class);
        startActivity(intent);
    }

    public void backToHomeLogs_02(View v){
        Intent intent = new Intent(this, PatientsListPage.class);
        startActivity(intent);
    }

    public void onClickSetMax(View view){
        Intent intent = new Intent(this, DoctorSetMaxIntakesPage.class);
        startActivity(intent);
    }
}
