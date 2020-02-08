package android.example.nutrilline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerifyEmailPage extends AppCompatActivity {

    private TextView message;
    private Button verifyEmail;
    private EditText email;
    private EditText password;

    private String emailStr;
    private String passwordStr;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email_page);
        message = findViewById(R.id.messageVerifyEmail);
        verifyEmail = findViewById(R.id.sendVerificationEmail);
        email = findViewById(R.id.emailEnterVerify);
        password = findViewById(R.id.passwordEnterVerify);

        mAuth = FirebaseAuth.getInstance();
    }

    public void logInBackVerify(View v)
    {
        Intent intent = new Intent(this, LoginPage.class);
        startActivity(intent);
    }

    public void sendVerify(View v)
    {
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();

        if(TextUtils.isEmpty(emailStr))
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Invalid email or password");
            return;
        }

        if(TextUtils.isEmpty(passwordStr))
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Invalid email or password");
            return;
        }

        mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            sendEmailVerification();
                            mAuth.signOut();
                        }
                        else
                        {
                            message.setText("Invalid email or password");
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    public void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        if(user.isEmailVerified())
        {
            message.setVisibility(View.VISIBLE);
            message.setText("Account has already been verified");
            verifyEmail.setEnabled(false);
            return;
        }
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            message.setText("Email Sent. Press back to login when account is verified");
                            message.setTextColor(Color.GREEN);
                            message.setVisibility(View.VISIBLE);
                            verifyEmail.setEnabled(false);
                        } else {
                            message.setText("Failed to send verification Email");
                            message.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
}
