package com.example.ezystore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class adminCategoryAdapter extends RecyclerView.Adapter<adminCategoryAdapter.ViewHolder> {
    private adminhomescreen adminhomescreen_;
    String ticket;


    private List<Category> categoryList;

    public adminCategoryAdapter(List<Category> categoryList, adminhomescreen adminhomescreen_) {
        this.categoryList = categoryList;
        this.adminhomescreen_ = adminhomescreen_;
        this.ticket ="Home";
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryNameTextView.setText(category.getCategoryName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // System.out.println(category.getCategoryName());
                ticket = category.getCategoryName();
                adminhomescreen_.loadDataFromFirestorefilter(ticket);

            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameTextView = itemView.findViewById(R.id.categoryName);
        }
    }
}