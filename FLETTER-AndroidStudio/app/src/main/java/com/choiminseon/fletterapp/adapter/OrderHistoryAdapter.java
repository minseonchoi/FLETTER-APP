package com.choiminseon.fletterapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.choiminseon.fletterapp.OrderHistoryDetailActivity;
import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.OrderApi;
import com.choiminseon.fletterapp.model.OrderHistory;
import com.choiminseon.fletterapp.model.OrderHistoryDetail;

import java.util.ArrayList;

import retrofit2.Retrofit;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder>{

    Context context;
    ArrayList<OrderHistory> orderHistoryArrayList;

    public OrderHistoryAdapter(Context context, ArrayList<OrderHistory> orderHistoryArrayList) {
        this.context = context;
        this.orderHistoryArrayList = orderHistoryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_history_list_row, parent, false);
        return new OrderHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistory orderHistory = orderHistoryArrayList.get(position);

        holder.txtOrderDate.setText(orderHistory.createdAt);
        holder.txtStatus.setText(orderHistory.status);
        Glide.with(context).load(orderHistory.packageUrl).into(holder.imgPhoto);
        holder.txtPackageType.setText(orderHistory.packagingType);
        holder.txtAddFlower.setText(orderHistory.orderFlower);
        holder.txtSize.setText(orderHistory.sizeType);
        holder.txtTotalPrice.setText(orderHistory.totalPrice + "");

    }

    @Override
    public int getItemCount() {
        return orderHistoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtOrderDate;
        TextView txtStatus;
        ImageView imgPhoto;
        TextView txtPackageType;
        TextView txtAddFlower;
        TextView txtQuantity;
        TextView txtSize;
        TextView txtTotalPrice;
        Button btnOrderHistoryDetail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);
            txtPackageType = itemView.findViewById(R.id.txtPackageType);
            txtAddFlower = itemView.findViewById(R.id.txtAddFlower);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtSize = itemView.findViewById(R.id.txtSize);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            btnOrderHistoryDetail = itemView.findViewById(R.id.btnOrderHistoryDetail);

            btnOrderHistoryDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (index != RecyclerView.NO_POSITION) {
                        OrderHistory orderHistory = orderHistoryArrayList.get(index);

                        Intent intent = new Intent(context, OrderHistoryDetailActivity.class);
                        intent.putExtra("orderId", orderHistory.orderId);
//                        intent.putExtra("createdAt", orderHistory.createdAt);
//                        intent.putExtra("status", orderHistory.status);
//                        intent.putExtra("packagingType", orderHistory.packagingType);
//                        intent.putExtra("orderFlower", orderHistory.orderFlower);
//                        intent.putExtra("quantity", orderHistory.quantity);
//                        intent.putExtra("sizeType", orderHistory.sizeType);
//                        intent.putExtra("totalPrice", orderHistory.totalPrice);
//                        intent.putExtra("receive", orderHistory.receive);
//                        intent.putExtra("packageUrl", orderHistory.packageUrl);
                        context.startActivity(intent);
                    }

                }
            });

        }
    }

}
