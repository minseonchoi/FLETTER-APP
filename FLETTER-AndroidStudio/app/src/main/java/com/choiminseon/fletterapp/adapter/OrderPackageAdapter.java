package com.choiminseon.fletterapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.choiminseon.fletterapp.OrderOptionActivity;
import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.model.PackageType;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class OrderPackageAdapter extends RecyclerView.Adapter<OrderPackageAdapter.ViewHolder>{

    Context context;
    ArrayList<PackageType> packageTypeArrayList;

    public OrderPackageAdapter(Context context, ArrayList<PackageType> packageTypeArrayList) {
        this.context = context;
        this.packageTypeArrayList = packageTypeArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_package_list_row, parent, false);
        return new OrderPackageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PackageType packageType =packageTypeArrayList.get(position);

        holder.txtPackageType.setText(packageType.packagingType);

        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add(packageType.firstPackagePhotoUrl);
        imageUrls.add(packageType.secondPackagePhotoUrl);
        imageUrls.add(packageType.thridPackagePhotoUrl);
        imageUrls.add(packageType.forthPackagePhotoUrl);

        // ViewPager2 어댑터 설정
        ImageSliderAdapter adapter = new ImageSliderAdapter(context, imageUrls, packageType.packageId);
        holder.viewPager.setAdapter(adapter);

        holder.dotsIndicator.setViewPager2(holder.viewPager);

    }

    @Override
    public int getItemCount() {
        return packageTypeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPackageType;
        Button btnChoice;
        ViewPager2 viewPager;
        DotsIndicator dotsIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPackageType = itemView.findViewById(R.id.txtPackageType);
            btnChoice = itemView.findViewById(R.id.btnChoice);
            viewPager = itemView.findViewById(R.id.viewPager);
            dotsIndicator = itemView.findViewById(R.id.dotsIndicator);

            btnChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (index != RecyclerView.NO_POSITION) {
                        PackageType packageType = packageTypeArrayList.get(index);

                        Intent intent = new Intent(context, OrderOptionActivity.class);
                        intent.putExtra("packageId", packageType.packageId);
                        context.startActivity(intent);
                    }
                }
            });



        }
    }

}
