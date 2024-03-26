package com.example.newbookit;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    TextView subTitleTv;
    ImageButton logoutBtn;
     EditText searchEt;
    Button addCategoryBtn;
    RecyclerView categoriesRv;
    FloatingActionButton addPdfFab;
//arrayList to store category
    private ArrayList<ModelCaegory> categoryArrayList;
    private AdapterCategory adapterCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);
//        super.onCreate(savedInstanceState);
//        View rootView = new View(this);
//        setContentView(rootView);


        searchEt = (EditText)findViewById(R.id.searchEt);
        subTitleTv = (TextView)findViewById(R.id.subTitleTv);
        logoutBtn = (ImageButton)findViewById(R.id.logoutBtn);
        addCategoryBtn = (Button)findViewById(R.id.addCategoryBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        categoriesRv = (RecyclerView)findViewById(R.id.CategoriesRv);
        addPdfFab = (FloatingActionButton)findViewById(R.id.addP);
        checkUser();
        loadCategories();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
                loadCategories();
            }
        });
        //handle Click,start Book add screen
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //called as and when user type each letter
                try {
                    adapterCategory.getFilter().filter(s);
                }
                catch (Exception e)
                {

                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });





        addCategoryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this,CategoryAddActivity.class));
            }
        });
          //handle Click,Start PDF add Screen
        addPdfFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( DashboardAdminActivity.this,PDFaddActivity.class));
            }
        });


    }
    private void loadCategories() {
        //init arayList
        categoryArrayList = new ArrayList<>();
        //get all categories from firebase Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               //clear arraylist before adding data into it;
                categoryArrayList.clear();
                for(DataSnapshot ds:snapshot.getChildren())
                {
                    //get data
                    ModelCaegory model= ds.getValue(ModelCaegory.class);

                    //add to arrayList
                    categoryArrayList.add(model);

                }
                adapterCategory =  new AdapterCategory(DashboardAdminActivity.this,categoryArrayList);
                categoriesRv.setAdapter(adapterCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser == null){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        else {
            String email = firebaseUser.getEmail();
            subTitleTv.setText(email);
        }
    }
}