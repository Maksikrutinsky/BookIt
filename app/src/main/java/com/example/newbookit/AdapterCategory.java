package com.example.newbookit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory>implements Filterable {

    private final Context context;
    public ArrayList<ModelCaegory> caegoryArrayList,filterList;

    private FilterCategory filter;

    public AdapterCategory(Context context, ArrayList<ModelCaegory> caegoryArrayList) {
        this.context = context;
        this.caegoryArrayList = caegoryArrayList;
        this.filterList = caegoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new HolderCategory(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        ModelCaegory model = caegoryArrayList.get(position);
        String id = model.getId();
        String category = model.getCategory();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        // Set data
        holder.categoryTv.setText(category);

        // Handle click, delete category
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//confine delete dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Are you sure you want to delete this category")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context,"Deleting..",Toast.LENGTH_SHORT).show();
                             deleteCategory(model,holder);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }

    private void deleteCategory(ModelCaegory model, HolderCategory holder) {
        //get id of category to delete
        String id = model.getId();
        //firebase Db > categories > categoryid
       DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
       ref.child(id)
               .removeValue()
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
//deleted successfully
                       Toast.makeText(context,"Successfully deleted...",Toast.LENGTH_SHORT).show();
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
//failed to delete
                       Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                   }
               });
    }


    @Override
    public int getItemCount() {

        return caegoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new FilterCategory(filterList,this);
        }
        return filter;
    }

    // View holder class to hold UI views of row category
    class HolderCategory extends RecyclerView.ViewHolder {

        TextView categoryTv;
        ImageButton deleteBtn;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            categoryTv = itemView.findViewById(R.id.categoryTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
