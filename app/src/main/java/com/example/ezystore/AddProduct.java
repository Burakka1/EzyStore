package com.example.ezystore;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    private Button buttonUploadImage;
    Button addbutton;
    private ImageView imageProduct;
    EditText editProductName;
    EditText editDescription;
    EditText categoryNameText;
    private Button buttonAddProduct;
    private Uri ImageUri;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        addbutton = findViewById(R.id.addbutton);
        editProductName = findViewById(R.id.editProductName);
        editDescription = findViewById(R.id.editDescription);
        categoryNameText = findViewById(R.id.categoryNameText);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        imageProduct = findViewById(R.id.imageProduct);
        spinnerOptions = findViewById(R.id.spinnerOptions);

        ArrayList<String> options = new ArrayList<>();
        Map<String ,String > Category = new HashMap<>();
        options.add("Bilgisayar");



        db.collection("Category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<String> options = new ArrayList<>();
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String option = document.getString("CategoryName");
                                options.add(option);
                            }
                            // Verileri kullanarak istediğiniz işlemleri yapabilirsiniz
                        } else {
                            // Veri yoksa veya sorgu başarısız olduysa yapılacak işlemler
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Sorgu başarısız olduysa yapılacak işlemler
                    }
                });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, options);
        spinnerOptions.setAdapter(adapter);


        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                options.add(categoryNameText.getText().toString());
                Category.put("CategoryName",categoryNameText.getText().toString());
                categoryNameText.setText("");
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            ImageUri = data.getData();
                            imageProduct.setImageURI(ImageUri);
                        } else {
                            Toast.makeText(AddProduct.this, "Seçili Resim Yok", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ImageUri != null) {
                    uploadToFirebase(ImageUri);
                    SaveFirestore(Category);

                } else {
                    Toast.makeText(AddProduct.this, "Lütfen Resim Seçiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadToFirebase(Uri uri) {
        String productName = editProductName.getText().toString();
        String description = editDescription.getText().toString();
        final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        UploadTask uploadTask = imageReference.putFile(uri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Resmin indirme URL'sini alma
                return imageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    // Resmin URL'sini Firestore'a ekleme
                    DataClass dataClass = new DataClass(downloadUri.toString(), productName, description);
                    db.collection("Products").add(dataClass)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Başarı durumunda yapılacak işlemler
                                    Toast.makeText(AddProduct.this, "Resim Başarıyla Yüklendi", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddProduct.this, HomeScreen.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Hata durumunda yapılacak işlemler
                                    Toast.makeText(AddProduct.this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    // Hata durumunda yapılacak işlemler
                    Toast.makeText(AddProduct.this, "Resim yüklenirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    private void SaveFirestore(Map Name){


        db.collection("Category")
                .add(Name)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        documentReference.update(Name);
                        System.out.println("EKLENDİ");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("HATA");
                    }
                });

    }
}
