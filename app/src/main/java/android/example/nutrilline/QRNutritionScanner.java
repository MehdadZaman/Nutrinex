package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
//import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.PendingIntent.getActivity;

public class QRNutritionScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    int[] nutritionIntegers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrnutrition_scanner);

        scannerView = findViewById(R.id.zxscan);
        txtResult = findViewById(R.id.txt_result);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        scannerView.setResultHandler(QRNutritionScanner.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(QRNutritionScanner.this, "You must accept this permission", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    public void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }

    @Override
    public void handleResult(Result rawResult) {
        processRawResult(rawResult.getText());
    }

    public void processRawResult(String s) {
        s = s.toLowerCase();
        if(s.startsWith("nutritional information:"))
        {
            String nutritionalNumbers = s.substring(s.indexOf('\n') + 1);
            txtResult.setText("Valid QR Code");
            processIngredients(nutritionalNumbers);
        }
        else
        {
            txtResult.setText("Invalid QR Code");
            showDialogueInvalidQRCode();
        }
    }

    public void processIngredients(String nutritionalNumbers)
    {
        String[] nutritionList = nutritionalNumbers.split(",");
        //Weird cases below
        //String[] ingredientList = ingredients.split("[^a-zA-Z \n]");
        for(int i = 0; i < nutritionList.length; i++) {
            nutritionList[i] = nutritionList[i].trim();
        }
        nutritionIntegers = new int[nutritionList.length];
        try{
            for(int i = 0; i < nutritionList.length; i++)
            {
                nutritionIntegers[i] = Integer.parseInt(nutritionList[i]);
            }
        }catch (Exception e){e.printStackTrace();}
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
                    ArrayList<Long> numbers = (ArrayList<Long>)dataMap.get("Current Daily Intakes");
                    ArrayList<Long> maxIntakes = (ArrayList<Long>) dataMap.get("Max Intakes");
                    processIngredients2(numbers, maxIntakes);
                }
            }
        });
    }

    public void processIngredients2(ArrayList<Long> nutNums, ArrayList<Long> maxIntakes)
    {
        for(int i = 0; i < nutritionIntegers.length; i++)
        {
            nutNums.set(i, (nutNums.get(i) + (int)((long)nutritionIntegers[i])));
        }

        DocumentReference df = db.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        df.update("Current Daily Intakes", nutNums);
        showDialogueNutritionAdded(nutNums, maxIntakes);
    }

    public void showDialogueInvalidQRCode()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(QRNutritionScanner.this);
        builder.setMessage("Invalid QR Code")
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, QRNutritionScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
    }

    public void showDialogueNutritionAdded(ArrayList<Long> nutNums, ArrayList<Long> maxIntakes)
    {
        PopNotificationWarning(nutNums, maxIntakes);
        AlertDialog.Builder builder = new AlertDialog.Builder(QRNutritionScanner.this);
        builder.setMessage("Nutritional Information Added")
                .setPositiveButton("Scan New Code", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, QRNutritionScanner.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Return Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(QRNutritionScanner.this, HomePage.class);
                        startActivity(intent);
                    }
                });
        builder.create().show();
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