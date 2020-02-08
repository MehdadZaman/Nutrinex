package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PatientsListPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_list_page);
    }

    public void goToPatient(View view){
        Intent intent = new Intent(this, AllNutritionalLogsDoctorPage.class);
        startActivity(intent);
    }

    public void goToEmail(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        intent.putExtra(Intent.EXTRA_EMAIL, "emailaddress@emailaddress.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(Intent.createChooser(intent, "Send Email"));
        /*Intent intent = new Intent(this, SendEmailPage.class);
        startActivity(intent);*/
    }

    public void backToDoctorHome(View view){
        Intent intent = new Intent(this, NutritionistHomePage.class);
        startActivity(intent);
    }
}
