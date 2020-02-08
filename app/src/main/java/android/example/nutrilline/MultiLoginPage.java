package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MultiLoginPage extends AppCompatActivity {

    String visionText = "";
    String uniqueID = "";

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_login_page);

        mAuth = FirebaseAuth.getInstance();

        getIntentInformationFromScan();
    }

    public void userLogInClick(View v){
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public void nutritionistlogInClick(View v){
        Intent intent = new Intent(this, NutritionistHomePage.class);
        startActivity(intent);
    }

    public void getIntentInformationFromScan()
    {
        //getIntent
        try {
            visionText = getIntent().getStringExtra("VisionText");
            uniqueID = getIntent().getStringExtra("UniqueID");
            if ((mAuth.getCurrentUser() != null) && (uniqueID.equals("1234"))) {
                Intent intent = new Intent(this, LoginPage.class);
                intent.putExtra("VisionText", visionText);
                intent.putExtra("UniqueID", uniqueID);
                startActivity(intent);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
