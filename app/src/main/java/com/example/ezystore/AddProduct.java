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
import com.google.android.gms.tasks.Tasks;
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
    private Button buttonUploadImage2;
    private Button buttonAddProduct;
    private ImageView imageProduct;
    private ImageView imageProduct2;
    private EditText editProductName;
    private EditText editDescription;
    private EditText categoryNameText;
    private EditText editProductPrice;
    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Spinner spinnerOptions;
    private Uri imageUri1;
    private Uri imageUri2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        buttonUploadImage2 = findViewById(R.id.buttonUploadImage2);
        buttonAddProduct = findViewById(R.id.buttonAddProduct);
        editProductName = findViewById(R.id.editProductName);
        editDescription = findViewById(R.id.editDescription);
        categoryNameText = findViewById(R.id.categoryNameText);
        buttonUploadImage = findViewById(R.id.buttonUploadImage);
        imageProduct = findViewById(R.id.imageProduct);
        imageProduct2 = findViewById(R.id.imageProduct2);
        spinnerOptions = findViewById(R.id.spinnerOptions);
        editProductPrice = findViewById(R.id.editProductPrice);

        ArrayList<String> options = new ArrayList<>();
        Map<String ,String > Category = new HashMap<>();

        db.collection("Category")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            options.clear(); // Önceki değerleri temizle
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String option = document.getString("CategoryName");
                                options.add(option);
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AddProduct.this,
                                    android.R.layout.simple_spinner_dropdown_item, options);
                            spinnerOptions.setAdapter(adapter);
                        }
                    }
                });

        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 1);
            }
        });

        buttonUploadImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, 2);
            }
        });

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri1 != null && imageUri2 != null) {
                    uploadToFirebase(imageUri1, imageUri2);
                } else {
                    Toast.makeText(AddProduct.this, "Lütfen Resim Seçiniz", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                imageUri1 = data.getData();
                imageProduct.setImageURI(imageUri1);
            } else if (requestCode == 2) {
                imageUri2 = data.getData();
                imageProduct2.setImageURI(imageUri2);
            }
        } else {
            Toast.makeText(AddProduct.this, "Seçili Resim Yok", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(Uri uri1, Uri uri2) {
        String productName = editProductName.getText().toString();
        String description = editDescription.getText().toString();
        String price = editProductPrice.getText().toString();
        String ticket = spinnerOptions.getSelectedItem().toString();

        final StorageReference imageReference1 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri1));
        UploadTask uploadTask1 = imageReference1.putFile(uri1);

        final StorageReference imageReference2 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri2));
        UploadTask uploadTask2 = imageReference2.putFile(uri2);

        Task<Uri> urlTask1 = uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return imageReference1.getDownloadUrl();
            }
        });

        Task<Uri> urlTask2 = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return imageReference2.getDownloadUrl();
            }
        });

        Task<List<Uri>> combinedTask = Tasks.whenAllSuccess(urlTask1, urlTask2);

        combinedTask.addOnCompleteListener(new OnCompleteListener<List<Uri>>() {
            @Override
            public void onComplete(@NonNull Task<List<Uri>> task) {
                if (task.isSuccessful()) {
                    List<Uri> downloadUris = task.getResult();
                    Uri downloadUri1 = downloadUris.get(0);
                    Uri downloadUri2 = downloadUris.get(1);

                    // Resim URL'lerini Firestore'a ekleme
                    DataClass dataClass = new DataClass(downloadUri1.toString(), productName, description, price, ticket, downloadUri2.toString());
                    db.collection("Products").add(dataClass)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(AddProduct.this, "Ürün Başarıyla Yüklendi", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddProduct.this, adminhomescreen.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddProduct.this, "Hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
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
                    }
                });
    }
}

