package com.example.ezystore;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private ArrayList<DataClass> dataList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public String Email;
    private Context context;
    public MyAdapter(ArrayList<DataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    LayoutInflater layoutInflater;

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView gridImage = convertView.findViewById(R.id.gridImage);
        TextView gridCaption = convertView.findViewById(R.id.gridCaption);
        TextView gridCaption2 = convertView.findViewById(R.id.gridCaption2);
        Glide.with(context).load(dataList.get(position).getImageURL()).into(gridImage);
        gridCaption.setText(dataList.get(position).getProductName());
        gridCaption2.setText(dataList.get(position).getPrice());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String productName = dataList.get(position).getProductName();
                Email = user.getEmail();
                Intent intent = new Intent(context, ProductDescriptionsActivity.class);
                intent.putExtra("productName", productName);
                intent.putExtra("Email",Email);

                context.startActivity(intent);
            }
        });

        return convertView;
    }
}

