package com.choiminseon.fletterapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.api.WishApi;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.Flower;
import com.choiminseon.fletterapp.model.Res;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TodayFlowerAdapter extends RecyclerView.Adapter<TodayFlowerAdapter.ViewHolder>{

    Context context;
    ArrayList<Flower> flowerArrayList;

    public TodayFlowerAdapter(Context context, ArrayList<Flower> flowerArrayList) {
        this.context = context;
        this.flowerArrayList = flowerArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.today_flower_row, parent, false);
        return new TodayFlowerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Flower flower = flowerArrayList.get(position);

        Glide.with(context).load(flower.flowerPhotoUrl).into(holder.imgFlower);

        holder.txtFlowerName.setText(flower.flowerName);
        holder.txtStatus.setText(flower.status);
        holder.txtOrigin.setText(flower.origin);

        if (flower.isWish == 0) {
            holder.imgWish.setImageResource(R.drawable.wish_black);
        } else {
            holder.imgWish.setImageResource(R.drawable.wish_full);
        }

    }

    @Override
    public int getItemCount() {
        return flowerArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgFlower;
        TextView txtFlowerName;
        TextView txtStatus;
        TextView txtOrigin;
        ImageView imgWish;
        String token;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgWish = itemView.findViewById(R.id.imgWish);
            imgFlower = itemView.findViewById(R.id.imgFlower);
            txtFlowerName = itemView.findViewById(R.id.txtSizeType);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtOrigin = itemView.findViewById(R.id.txtOrigin);

            SharedPreferences sp = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            token = sp.getString("token", "");

            imgWish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Flower flower = flowerArrayList.get(index);


                    if (flower.isWish == 0) {
                        setFlowerWish(flower);
                    } else {
                        deleteFlowerWish(flower);
                    }
                    imgWish.setEnabled(false);
                }
            });
        }

        private void setFlowerWish(Flower flower) {
            Retrofit retrofit = NetworkClient.getRetrofitClient(context);
            WishApi api = retrofit.create(WishApi.class);

            Call<Res> call = api.addWishList(flower.flowerId, "Bearer " + token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if (response.isSuccessful()) {
                        flower.isWish = 1;
                        notifyDataSetChanged();
                    }
                    imgWish.setEnabled(true);
                }

                @Override
                public void onFailure(Call<Res> call, Throwable throwable) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void deleteFlowerWish(Flower flower) {
            Retrofit retrofit = NetworkClient.getRetrofitClient(context);
            WishApi api = retrofit.create(WishApi.class);

            Call<Res> call = api.deleteWishList(flower.flowerId, "Bearer " + token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if (response.isSuccessful()) {
                        flower.isWish = 0;
                        notifyDataSetChanged();
                    }
                    imgWish.setEnabled(true);
                }

                @Override
                public void onFailure(Call<Res> call, Throwable throwable) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}