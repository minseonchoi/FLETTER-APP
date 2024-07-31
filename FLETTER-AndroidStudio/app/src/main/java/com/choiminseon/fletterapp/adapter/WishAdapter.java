package com.choiminseon.fletterapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.choiminseon.fletterapp.model.Res;
import com.choiminseon.fletterapp.model.Wish;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WishAdapter extends RecyclerView.Adapter<WishAdapter.ViewHolder>{

    Context context;
    ArrayList<Wish> wishArrayList;

    public WishAdapter(Context context, ArrayList<Wish> wishArrayList) {
        this.context = context;
        this.wishArrayList = wishArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wish_list_row, parent, false);
        return new WishAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Wish wish = wishArrayList.get(position);

        Glide.with(context).load(wish.flowerPhotoUrl).into(holder.imgFlower);

        holder.txtFlowerName.setText(wish.flowerName);
        holder.txtStatus.setText(wish.status);
        holder.txtOrigin.setText(wish.origin);
    }

    @Override
    public int getItemCount() {
        return wishArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        ImageView imgFlower;
        TextView txtFlowerName;
        TextView txtStatus;
        TextView txtOrigin;
        ImageView imgWish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            imgWish = itemView.findViewById(R.id.imgWish);
            imgFlower = itemView.findViewById(R.id.imgFlower);
            txtFlowerName = itemView.findViewById(R.id.txtSizeType);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtOrigin = itemView.findViewById(R.id.txtOrigin);

            imgWish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteWishList();
                }
            });


        }

        private void deleteWishList() {
            Retrofit retrofit = NetworkClient.getRetrofitClient(context);
            WishApi api = retrofit.create(WishApi.class);
            int index = getAdapterPosition();
            Wish wish = wishArrayList.get(index);

            Log.i("WISH", String.valueOf(wish.flowerId));

            SharedPreferences sp = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            String token = sp.getString("token", "");

            Call<Res> call = api.deleteWishList(wish.flowerId, "Bearer " + token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if (response.isSuccessful()) {
                        Res res = response.body();
                        if (res != null) {
                            wishArrayList.remove(index);
                            notifyDataSetChanged();
                        } else {
                            Log.i("WISH API", "Response body is null");
                        }
                    }
                }

                @Override
                public void onFailure(Call<Res> call, Throwable throwable) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        }




    }
}
