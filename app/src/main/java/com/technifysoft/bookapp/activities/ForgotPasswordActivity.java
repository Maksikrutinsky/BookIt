package com.technifysoft.bookapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.technifysoft.bookapp.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    // קישורים לממשק המשתמש
    private ActivityForgotPasswordBinding binding;

    // אותנטיקציה של Firebase
    private FirebaseAuth firebaseAuth;

    // דיאלוג התקדמות
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // אתחול Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // הגדרה ואתחול של דיאלוג התקדמות
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);

        // טיפול בלחיצה, חזרה אחורה
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // טיפול בלחיצה, התחלת שחזור סיסמה
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String email = "";
    private void validateData() {
        // קבלת הנתונים, כלומר האימייל
        email = binding.emailEt.getText().toString().trim();

        // אימות הנתונים, צריך להיות מלא ובפורמט חוקי
        if (email.isEmpty()){
            Toast.makeText(this, "Enter Email...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid email format...", Toast.LENGTH_SHORT).show();
        }
        else {
            recoverPassword();
        }
    }

    private void recoverPassword() {
        // הצגת התקדמות
        progressDialog.setMessage("Sending password recovery instructions to " + email);
        progressDialog.show();

        // התחלת השחזור
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // הודעת הצלחה
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Instructions to reset password sent to " + email, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // הודעת כישלון
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
