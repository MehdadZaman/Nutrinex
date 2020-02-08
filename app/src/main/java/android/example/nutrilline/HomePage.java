package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    String visionText;
    String uniqueID;
    Button dailyInatakesButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final int REQUEST_CALL = 1;

    private TextView emailView;

    int[] nutritionIntegers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        emailView = findViewById(R.id.emailHomePage);
        try {
            emailView.setText(mAuth.getCurrentUser().getEmail().toString());
        }
        catch(Exception e)
        {
            emailView.setText("user not available");
        }

        try{
            visionText = getIntent().getStringExtra("VisionText");
            uniqueID = getIntent().getStringExtra("UniqueID");
            if (uniqueID.equals("1234")){
                parseText();
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        dailyInatakesButton = findViewById(R.id.dailyIntake);
        checkOverFlow();
    }

    public void onClickEmergencyCall(View view){
        makeCall();
    }

    public void addAllergiesClick(View view)
    {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }

    public void gotoQRScanner(View view)
    {
        Intent intent = new Intent(this, QRNutritionScanner.class);
        startActivity(intent);
    }

    public void makeCall(){
        String number = "+8801982991409";
        if(number.trim().length() >0){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{
                String dial = "tel:"+number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
        else{
            Toast.makeText(this, "Please Add a Emergency Contact", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL){
            if(grantResults.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                makeCall();
            }
            else{
                Toast.makeText(this,"Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickLogout(View v){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
        ActivityCompat.finishAffinity(this);
    }

    public void manualPageClick(View v){
        Intent intent = new Intent(this, ManualNutritionAdditionPage.class);
        startActivity(intent);
    }

    public void goToMessagePage(View v){
        Intent intent = new Intent(this, MessagesPage.class);
        startActivity(intent);
    }

    public void allNutritionalLogsClick(View v){
        Intent intent = new Intent(this, AllNutritionalLogsPage.class);
        startActivity(intent);
    }

    public void checkOverFlow()
    {
        final DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.SERVER;
        // Get the document, forcing the SDK to use the offline cache
        docRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> dataMap = document.getData();
                    ArrayList<Long> dailyIntakes = (ArrayList<Long>) dataMap.get("Current Daily Intakes");
                    ArrayList<Long> maxIntakes = (ArrayList<Long>) dataMap.get("Max Intakes");
                    compareValues(maxIntakes, dailyIntakes);
                }
            }
        });
    }

    public void compareValues(ArrayList<Long> maxIntakes, ArrayList<Long> dailyIntakes)
    {
        boolean setRed = false;
        for(int i = 0; i < maxIntakes.size(); i++)
        {
            if((maxIntakes.get(i) != 0) && (dailyIntakes.get(i) >= maxIntakes.get(i)))
            {
                setRed = true;
            }
        }

        if(setRed)
        {
            dailyInatakesButton.setBackgroundResource(R.drawable.rounded_corner_button2);
            dailyInatakesButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.alert_symbol, 0, R.drawable.alert_symbol, 0);
        }
    }

    public void currentNutritionalIntakeClick(View v){

        Intent intent = new Intent(this, CurrentNutritionalIntakesPage.class);
        startActivity(intent);
    }

    public void incrementTextualVision(View v){
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gms.samples.vision.ocrreader");
        startActivity(intent);
    }

    public void parseText(){
        visionText = visionText.toLowerCase();
        String[] stringInts = visionText.split("[a-z]");
        ArrayList<String> intList = new ArrayList<>();
        for(int i = 0; i < stringInts.length; i++)
        {
            if(!TextUtils.isEmpty(stringInts[i]))
            {
                intList.add(stringInts[i]);
            }
        }
        int[] integrs = new int[5];
        for(int i = 0; i < intList.size(); i++) {
            integrs[i] = Integer.parseInt(intList.get(i));
        }

        addToDatabase(integrs);
        Toast.makeText(this, "Daily Intakes Updated!", Toast.LENGTH_SHORT).show();

    }

    public void addToDatabase(int[] integrs)
    {
        nutritionIntegers = integrs;
        processIngredientsHome();
    }

    public void processIngredientsHome()
    {
        FirebaseUser user = mAuth.getCurrentUser();
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
                    ArrayList<Long> numbers = (ArrayList<Long>)document.get("Current Daily Intakes");
                    ArrayList<Long> maxNums = (ArrayList<Long>)document.get("Max Intakes");
                    processIngredients2Home(numbers, maxNums);

                }
            }
        });
    }

    public void processIngredients2Home(ArrayList<Long> nutNums, ArrayList<Long> maxIntakes)
    {
        for(int i = 0; i < nutritionIntegers.length; i++)
        {
            nutNums.set(i, (nutNums.get(i) + (int)((long)nutritionIntegers[i])));
        }
        DocumentReference df = db.collection("users").document(mAuth.getCurrentUser().getUid());
        df.update("Current Daily Intakes", nutNums);

        PopNotificationWarning(nutNums, maxIntakes);
    }

    public void PopNotificationWarning(ArrayList<Long> nutNums, ArrayList<Long> maxIntakes){
        String s="";
        boolean flag= false;
        for(int i =0; i< nutNums.size(); i++){
            if(nutNums.get(i)>maxIntakes.get(i)){
                s ="You have exceeded one of your nutrition's current maximum limit.";
                flag = true;
                break;
            }
        }

        if(flag == true){
            NotificationCompat.Builder builder;

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                String CHANNEL_ID = "my_channel_01";
                CharSequence name = getString(R.string.common_google_play_services_notification_channel_name);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,name,importance);
                manager.createNotificationChannel(mChannel);
                builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            }
            else{
                builder = new NotificationCompat.Builder(this);
            }
            builder.setSmallIcon(R.drawable.alert_symbol)
                    .setContentTitle("NutriLine")
                    .setContentText(s)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(s));
            Intent notificationIntent = new Intent(this, CurrentNutritionalIntakesPage.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            manager.notify(0, builder.build());
        }
    }
}
