package com.example.ezystore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<Product> productList;
    private AppCompatActivity activity;
    public CartAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView productNameTextView;
        private TextView productPriceTextView;
        private ImageView productImageView;
        private TextView count;
        private ImageButton deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            productPriceTextView = itemView.findViewById(R.id.productPriceTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            count = itemView.findViewById(R.id.count);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Product product) {
            productNameTextView.setText("Ürün Adı: " + product.getProductName());
            productPriceTextView.setText("Ürün Fiyatı: " + product.getPrice());
            count.setText("Adet: " + product.getCount());

            Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .into(productImageView);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product clickedProduct = productList.get(position);
                        String productId = clickedProduct.getId();
                        deleteDocument(productId);
                    }
                }
            });
        }

        private void deleteDocument(String productId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Bag").document(productId)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(itemView.getContext(), "Ürün başarıyla silindi.", Toast.LENGTH_SHORT).show();
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                productList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, productList.size());
                                ((Cart) itemView.getContext()).refreshCart();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(itemView.getContext(), "Ürün silinirken bir hata oluştu.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}




