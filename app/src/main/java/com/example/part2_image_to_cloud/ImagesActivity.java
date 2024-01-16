package com.example.part2_image_to_cloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {
    private RecyclerView m_recyclerView;
    private ImageAdapter m_adapter;
    private DatabaseReference my_database_ref;
    private List<Upload> m_upload;
    private ProgressBar m_progressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        m_recyclerView = findViewById(R.id.recycler_view);
        m_recyclerView.setHasFixedSize(true);
        m_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        m_progressbar = findViewById(R.id.progress_circle);

        m_upload = new ArrayList<>();
        my_database_ref = FirebaseDatabase.getInstance().getReference("uploads");

        my_database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot postSnapShot: snapshot.getChildren()){
                    Upload upload = postSnapShot.getValue(Upload.class);
                    m_upload.add(upload);
                }
                m_adapter = new ImageAdapter(ImagesActivity.this , m_upload);
                m_recyclerView.setAdapter(m_adapter);
                m_progressbar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                m_progressbar.setVisibility(View.INVISIBLE);

            }
        });
    }
}