package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class SettingsPage extends AppCompatActivity {

    LinearLayout manNutrition;

    private EditText ageText;
    private EditText weightText;
    private EditText heightFeetText;
    private EditText heightInchesText;

    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;

    private CheckBox checkManual;

    private EditText calorieText;
    private EditText fatText;
    private EditText fiberText;
    private EditText sodiumText;
    private EditText proteinText;

    private int age;
    private int weight;
    private int heightFeet;
    private int heightInches;
    String gender;

    private int calorie;
    private int fat;
    private int fiber;
    private int sodium;
    private int protein;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);

        ageText = findViewById(R.id.ageNumber);
        weightText = findViewById(R.id.weightNumber);
        heightFeetText = findViewById(R.id.heightNumberFeet);
        heightInchesText = findViewById(R.id.heightNumberInches);

        genderRadioGroup = findViewById(R.id.radio_group);

        checkManual = findViewById(R.id.manualFillNutrition);

        calorieText = findViewById(R.id.maxCalories);
        fatText = findViewById(R.id.maxFat);
        fiberText = findViewById(R.id.maxFiber);
        sodiumText = findViewById(R.id.maxSodium);
        proteinText = findViewById(R.id.maxProtein);

        manNutrition = findViewById(R.id.manualNutritionInput);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setOriginalValues1();
    }

    public void saveSettingsClick(View v)
    {
        try {age = Integer.parseInt(ageText.getText().toString());} catch (Exception e){e.printStackTrace();}
        try {weight = Integer.parseInt(weightText.getText().toString());} catch (Exception e){e.printStackTrace();}
        try {
            heightFeet = Integer.parseInt(heightFeetText.getText().toString());
            heightInches = Integer.parseInt(heightInchesText.getText().toString());
            if((heightFeet > 7) || (heightInches > 12)) { throw new Exception();}
            height = (heightFeet * 12) + heightInches;
        }
        catch (Exception e){e.printStackTrace();}
        try {
            int genderNum = genderRadioGroup.getCheckedRadioButtonId();
            genderRadioButton = findViewById(genderNum);
            if(genderRadioButton != null)
                gender = genderRadioButton.getText().toString();
        } catch (Exception e){e.printStackTrace();}
        if(checkManual.isChecked())
        {
            try {calorie = Integer.parseInt(calorieText.getText().toString());} catch (Exception e){e.printStackTrace();}
            try {fat = Integer.parseInt(fatText.getText().toString());} catch (Exception e){e.printStackTrace();}
            try {fiber = Integer.parseInt(fiberText.getText().toString());} catch (Exception e){e.printStackTrace();}
            try {sodium = Integer.parseInt(sodiumText.getText().toString());} catch (Exception e){e.printStackTrace();}
            try {protein = Integer.parseInt(proteinText.getText().toString());} catch (Exception e){e.printStackTrace();}
        }
        autoFillNutrition();
        fillDatabase();
        Toast.makeText(this, "User settings saved!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void setOriginalValues1()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        final DocumentReference docRef = db.collection("users").document(user.getUid());
        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.SERVER;
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> dataMap = document.getData();
                    setOriginalValues2(dataMap);
                }
            }
        });
    }

    public void setOriginalValues2(Map<String, Object> dataMap)
    {
        age = (int)((long)dataMap.get("Age"));
        weight = (int)((long)dataMap.get("Weight"));
        height = (int)((long)dataMap.get("Height"));
        gender = (String)dataMap.get("Gender");

        ArrayList<Long> maxIntakes= (ArrayList<Long>)dataMap.get("Max Intakes");

        calorie = (int)((long)maxIntakes.get(0));
        fat = (int)((long)maxIntakes.get(1));
        fiber = (int)((long)maxIntakes.get(2));
        sodium = (int)((long)maxIntakes.get(3));
        protein = (int)((long)maxIntakes.get(4));
    }

    public void fillDatabase()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference df = db.collection("users").document(userID);

        df.update("Age", age);
        df.update("Weight", weight);
        df.update("Height", height);
        df.update("Gender", gender);

        df.update("Max Intakes", Arrays.asList(calorie, fat, fiber, sodium, protein));
    }

    public void autoFillNutrition()
    {
        if(calorie == 0)
        {
            if((gender.equals("Male")) && (age <= 3)) calorie = 1000;
            else if((gender.equals("Male")) && (age <= 8)) calorie = 1500;
            else if((gender.equals("Male")) && (age <= 13)) calorie = 1800;
            else if((gender.equals("Male")) && (age <= 30)) calorie = 2600;
            else if((gender.equals("Male")) && (age <= 50)) calorie = 2200;
            else if((gender.equals("Male"))) calorie = 2000;
            else if((!gender.equals("Male")) && (age <= 3)) calorie = 1000;
            else if((!gender.equals("Male")) && (age <= 8)) calorie = 1200;
            else if((!gender.equals("Male")) && (age <= 13)) calorie = 1600;
            else if((!gender.equals("Male")) && (age <= 30)) calorie = 1800;
            else if((!gender.equals("Male")) && (age <= 50)) calorie = 1000;
            else if((!gender.equals("Male"))) calorie = 1600;
        }

        if(fat == 0)
        {
            fat = 30;
        }

        if(fiber == 0)
        {
            if((gender.equals("Male")) && (age <= 3)) fiber = 14;
            else if((gender.equals("Male")) && (age <= 8)) fiber = 20;
            else if((gender.equals("Male")) && (age <= 13)) fiber = 25;
            else if((gender.equals("Male")) && (age <= 30)) fiber = 34;
            else if((gender.equals("Male")) && (age <= 50)) fiber = 31;
            else if((gender.equals("Male"))) fiber = 38;
            else if((!gender.equals("Male")) && (age <= 3)) fiber = 14;
            else if((!gender.equals("Male")) && (age <= 8)) fiber = 17;
            else if((!gender.equals("Male")) && (age <= 13)) fiber = 22;
            else if((!gender.equals("Male")) && (age <= 30)) fiber = 28;
            else if((!gender.equals("Male")) && (age <= 50)) fiber = 25;
            else if((!gender.equals("Male"))) fiber = 22;
        }

        if(sodium == 0)
        {
            if(age <= 3) sodium = 1500;
            else if(age <= 8) sodium = 1900;
            else if(age <= 13) sodium = 2200;
            else fiber = 2300;
        }

        if(protein == 0)
        {
            if((gender.equals("Male")) && (age <= 3)) protein = 13;
            else if((gender.equals("Male")) && (age <= 8)) protein = 19;
            else if((gender.equals("Male")) && (age <= 13)) protein = 34;
            else if((gender.equals("Male")) && (age <= 30)) protein = 52;
            else if((gender.equals("Male"))) protein = 56;
            else if((!gender.equals("Male")) && (age <= 3)) protein = 13;
            else if((!gender.equals("Male")) && (age <= 8)) protein = 19;
            else if((!gender.equals("Male")) && (age <= 13)) protein = 34;
            else if((!gender.equals("Male"))) protein = 46;
        }
    }

    public void setManualVisibility(View v)
    {
        if(checkManual.isChecked())
        {
            manNutrition.setVisibility(View.VISIBLE);
        }
        else
        {
            manNutrition.setVisibility(View.INVISIBLE);
        }
    }
}