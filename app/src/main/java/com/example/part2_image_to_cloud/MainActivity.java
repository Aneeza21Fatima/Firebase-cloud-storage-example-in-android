package com.example.part2_image_to_cloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button choose_file_button;
    private EditText title;
    private ImageView image_view;
    private ProgressBar progress_bar;
    private Button upload_button;
    private TextView show_uploads_text_view;
    private Uri image_uri;
    private StorageReference storage_ref;
    private DatabaseReference database_ref;
    private StorageTask upload_task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        choose_file_button = findViewById(R.id.button_choose_image);
        title = findViewById(R.id.edit_text_name);
        image_view = findViewById(R.id.image_view);
        progress_bar = findViewById(R.id.progress_bar);
        upload_button = findViewById(R.id.button_upload);
        show_uploads_text_view = findViewById(R.id.view_uploads);

        storage_ref = FirebaseStorage.getInstance().getReference("uploads/");
        database_ref = FirebaseDatabase.getInstance().getReference("uploads");

        choose_file_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(upload_task != null && upload_task.isInProgress()){
                    Toast.makeText(MainActivity.this,"Upload in Progress", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                }
            }
        });

        show_uploads_text_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewUploads();

            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_uri = data.getData();
            //check tomorrow

            Picasso.with(this).load(image_uri).into(image_view);

        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
    private void uploadFile() {
        if (image_uri != null) {
            StorageReference file_ref = storage_ref.child(System.currentTimeMillis() + "." + getFileExtension(image_uri));
            upload_task = file_ref.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progress_bar.setProgress(0);
                                }
                            }, 500);

                            // Get the download URL for the uploaded image
                            file_ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Create an Upload object with the title and download URL
                                    Upload upload = new Upload(title.getText().toString().trim(), uri.toString());

                                    // Generate a unique key for the database entry
                                    String uploadId = database_ref.push().getKey();

                                    // Save the Upload object in the database under the generated key
                                    database_ref.child(uploadId).setValue(upload);
                                }
                            });

                            Toast.makeText(MainActivity.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            progress_bar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void viewUploads(){
        Intent intent = new Intent(MainActivity.this, ImagesActivity.class);
        startActivity(intent);
    }


}