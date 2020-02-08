package android.example.nutrilline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class AllNutritionalLogsPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_nutritional_logs_page);
    }

    public void goToLogFeb7(View v){
        Intent intent = new Intent(this, DailyLogPage.class);
        intent.putExtra("Nutritional Numbers", new double[]{1, 1.02, 1.4, .98, .95});
        startActivity(intent);
    }

    public void goToLogFeb6(View v){
        Intent intent = new Intent(this, DailyLogPage.class);
        intent.putExtra("Nutritional Numbers", new double[]{.98, 1.02, .94, .98, .95});
        startActivity(intent);
    }

    public void goToLogFeb5(View v){
        Intent intent = new Intent(this, DailyLogPage.class);
        intent.putExtra("Nutritional Numbers", new double[]{.98, .95, .93, .98, .95});

        startActivity(intent);
    }

    public void goToLogFeb4(View v){
        Intent intent = new Intent(this, DailyLogPage.class);
        intent.putExtra("Nutritional Numbers", new double[]{1, 1.02, 1.4, 1.3, 1.15});
        startActivity(intent);
    }

    public void goToLogFeb3(View v){
        Intent intent = new Intent(this, DailyLogPage.class);
        intent.putExtra("Nutritional Numbers", new double[]{1.2, 1.02, 1.05, .98, .95});
        startActivity(intent);
    }

    public void backToHomeLogs(View v){
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }
}
