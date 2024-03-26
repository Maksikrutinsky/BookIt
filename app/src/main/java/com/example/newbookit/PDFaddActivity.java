package com.example.newbookit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class PDFaddActivity extends AppCompatActivity {

    //ImageButton PDF
    ImageButton backbtnPDF;
    ImageButton attachBtnPDF;

    Button submitBtnPDF;
    TextView categoryPDF;
    EditText titleEtPDF,descriptionEtPDF;

//arrayList to hold PDF categories
    private ArrayList<ModelCaegory> caegoryArrayList;
    //uri of Picked
    private Uri pdfUri = null;

    private static final int PDF_PICK_CODE = 1000;
    //TAG
    private static final String TAG = "Add_PDF_Tag";
    //firebase auth
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfadd);

        backbtnPDF = (ImageButton) findViewById(R.id.backbtnpdf);
        attachBtnPDF = (ImageButton) findViewById(R.id.attachBtnpdf);
        categoryPDF = (TextView) findViewById(R.id.categoryTv);
        submitBtnPDF = (Button)findViewById(R.id.submitBtnPdf);
        titleEtPDF =(EditText)findViewById(R.id.titleEtPdf);
        descriptionEtPDF = (EditText)findViewById(R.id.descriptionEtPdf);

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        loadPdfcategories();
        //setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);



        //handle Click
        backbtnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //handle Click (PDF)
        attachBtnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfPickIntent();
            }
        });
        //Handle Click (catgory)
        categoryPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryPickDialog();
            }
        });
        //handle Ckick upload PDF
        submitBtnPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validataData();
            }
        });

    }

    private void loadPdfcategories() {
        Log.d(TAG, "loadPdfcategories: Loading PDF Categoires....");
        caegoryArrayList = new ArrayList<>();

        //db refernce to load categories...db > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                caegoryArrayList.clear(); //clear before adding
                for(DataSnapshot ds:snapshot.getChildren()){
                    //get data
                    ModelCaegory model= ds.getValue(ModelCaegory.class);
                    //add to arrayList
                    caegoryArrayList.add(model);
                    Log.d(TAG, "onDataChange: "+model.getCategory());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                validataData();
            }
        });

    }
    private String title="",description="",category = "";

    private void validataData() {
        //Step 1 : validate data
        Log.d(TAG, "validataData: validating data...");


        //get data
        title =titleEtPDF.getText().toString().trim();
        description = descriptionEtPDF.getText().toString().trim();
        category = categoryPDF.getText().toString().trim();

        //validate data
        if(TextUtils.isEmpty(title)){
            Toast.makeText(this,"Enter Title...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(description)){
            Toast.makeText(this,"Enter Description...",Toast.LENGTH_SHORT).show();
        }   else if (TextUtils.isEmpty(category)){
            Toast.makeText(this,"Pick category...",Toast.LENGTH_SHORT).show();
        } else if (pdfUri == null)
        {
            Toast.makeText(this,"Pick PDF....",Toast.LENGTH_SHORT).show();
        }
        else{
            //all data is valid,can upload now
            UploadpdfToStorage();
        }

    }

    private void UploadpdfToStorage()
    {
        //Step2: Upload pdf to firebase
        Log.d(TAG, "UploadpdfToStorage: uploading to Storage....");
        //show progerss
        progressDialog.setMessage("Uploading  PDF..");
        progressDialog.show();

long timestamp =  System.currentTimeMillis();
        //parth of pdf in firebase storage
        String filepathAndName = "Books/"+timestamp;
        //storage refernce
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filepathAndName);
        storageReference.putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            Log.d(TAG, "onSuccess: PDF uploaded to storage....");
            Log.d(TAG, "onSuccess: getting PDF url...");


            //get PDF Url
            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
            while (!uriTask.isSuccessful());
            String uploadedPdfurl = ""+uriTask.getResult();

            //upload to firebase db
            uploadPdfinfoTodb(uploadedPdfurl,timestamp);
            
        }

    })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: PDF Upload failed due to"+e.getMessage());
                        Toast.makeText(PDFaddActivity.this, "PDF Upload failed due to"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void uploadPdfinfoTodb(String uploadedPdfurl, long timestamp)
    {
        Log.d(TAG, "UploadpdfToStorage: uploading Pdf info to firebase db....");
        progressDialog.setMessage("Uploading pdf info..");

        String uid = firebaseAuth.getUid();

        //setuo data to upload
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid",""+uid);
        hashMap.put("id", ""+timestamp);
        hashMap.put("title", ""+title);
        hashMap.put("description", ""+title);
        hashMap.put("category", ""+category);
        hashMap.put("url", ""+uploadedPdfurl);
        hashMap.put("timestamp", ""+timestamp);

        //db reference DB > Books
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Books");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Successfully uploaded...");
                        Toast.makeText(PDFaddActivity.this, "Successfully uploaded...", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed to upload to db due to"+e.getMessage());
                        Toast.makeText(PDFaddActivity.this, "onFailure: Failed to upload to db due to", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void CategoryPickDialog()
    {
        Log.d(TAG, "CategoryPickDialog: showing category dialog");


        //get string  array
        String[] categoriesArray = new String[caegoryArrayList.size()];
        for (int i = 0; i < caegoryArrayList.size(); i++) {
            categoriesArray[i] = caegoryArrayList.get(i).getCategory();
        }
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Category").setItems(categoriesArray, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle item Clock
                //get Clicked item from list
String category = categoriesArray[which];
//set to category textview
                categoryPDF.setText(category);

                Log.d(TAG, "onClick: Selected  Category "+category);
            }
        })
                .show();
    }

    private void pdfPickIntent() {
        Log.d(TAG, "pdfPickIntent: Starting PDF Pick intent");
        Intent intent = new Intent();
        intent.setType("Application/PDF");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
           if(resultCode == PDF_PICK_CODE){
               Log.d(TAG, "onActivityResult: PDF Picked");
               pdfUri = data.getData();
               Log.d(TAG, "onActivityResult: URI: "+pdfUri);
               
           }
        }
        else
        {
            Log.d(TAG, "onActivityResult:cancelled picking pdf");
            Toast.makeText(this, "cancelled picking pdf", Toast.LENGTH_SHORT).show();
        }
    }
}
// add FireBase Storage to Our Project