package com.example.newbookit;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CategoryAddActivity extends AppCompatActivity {
    //view binding

    Button submitBtn;
    //firebase auth
ImageButton backBtn;
    EditText BookEt;
    private  FirebaseAuth firebaseAuth;

    //Progress dialog
    private ProgressDialog  progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add);

        submitBtn = (Button) findViewById(R.id.submitBtn);
        BookEt = (EditText) findViewById(R.id.BookEt);
        firebaseAuth = FirebaseAuth.getInstance();
        backBtn = (ImageButton)findViewById(R.id.backBtn);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);


        //handle Click,go back

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                       onBackPressed();
            }
        });


                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validataData();
                    }
                });
    }
private String category = "";
    private void validataData()
    {
        //get data
        category = BookEt.getText().toString().trim();
        //vvalidate if not empty

        if (TextUtils.isEmpty(category)){
            Toast.makeText(this, "Please enter category...!",Toast.LENGTH_SHORT).show();
        }
        else{
           addCategoryFirebase();
        }
    }

    private void addCategoryFirebase()
    {
        //Show progress
        progressDialog.setMessage("Adding category...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id",""+timestamp);
        hashMap.put("category",""+category);
        hashMap.put("timestamp",timestamp);
        hashMap.put("uid",""+firebaseAuth.getUid());

        //add to firebase db

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.child(""+timestamp)
        .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
progressDialog.dismiss();
Toast.makeText(CategoryAddActivity.this,"Category added successfully..",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CategoryAddActivity.this,"",Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
