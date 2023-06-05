package com.example.ezystore;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private FirebaseFirestore firestore;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orders, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.productNameTextView.setText(order.getProductName());
        holder.priceTextView.setText(order.getFormattedDate());

        Glide.with(context)
                .load(order.getImageUrl())
                .into(holder.productImageView);

        holder.buttonCombined.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, past_order_details.class);
                intent.putExtra("productName", order.getProductName());
                intent.putExtra("price", order.getPrice());
                intent.putExtra("imageUrl", order.getImageUrl());
                intent.putExtra("count", order.getCount());
                intent.putExtra("email", order.getEmail());
                intent.putExtra("formattedDate", order.getFormattedDate());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView, priceTextView;
        Button buttonCombined;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            productImageView = itemView.findViewById(R.id.productImageView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            buttonCombined = itemView.findViewById(R.id.buttonCombined);
        }
    }
}
