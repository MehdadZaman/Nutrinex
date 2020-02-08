package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DoctorSetMaxIntakesPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_set_max_intakes_page);
    }

    public void setMaxIntakesDoctor(View v)
    {
        Toast.makeText(this, "User's maximum intakes updated!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PatientsListPage.class);
        startActivity(intent);
    }
}