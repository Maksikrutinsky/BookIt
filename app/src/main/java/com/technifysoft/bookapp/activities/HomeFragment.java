
package com.technifysoft.bookapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technifysoft.bookapp.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    View view;


    private TextView textViewTime;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String currentTime = "היי, הסיפריה תיהיה סגורה היום!  " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
            textViewTime.setText(currentTime);
            // תזמן את עצמך לרוץ שוב בעוד שנייה
            handler.postDelayed(this, 1000);
        }
    };



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.recycler_item, container, false);
        imageSlider = (ImageSlider)view.findViewById(R.id.image_slider);
        loadImages();
        textViewTime = view.findViewById(R.id.textViewToUpdate);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // התחל את הריצה של העדכונים
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // הסר את ה-Runnable כאשר ה-Fragment מתפרק
        handler.removeCallbacks(runnable);
    }

    private void loadImages() {
        final List<SlideModel> remoteimages = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                            remoteimages.add(new SlideModel(data.child("url").getValue().toString(), data.child("title").getValue().toString(), ScaleTypes.FIT));

                        imageSlider.setImageList(remoteimages,ScaleTypes.FIT);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle potential errors
                    }
                });
    }
}