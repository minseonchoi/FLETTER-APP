package com.choiminseon.fletterapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.choiminseon.fletterapp.CartActivity;
import com.choiminseon.fletterapp.EmptyCartActivity;
import com.choiminseon.fletterapp.MainActivity;
import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.api.CartApi;
import com.choiminseon.fletterapp.api.NetworkClient;
import com.choiminseon.fletterapp.config.Config;
import com.choiminseon.fletterapp.model.Cart;
import com.choiminseon.fletterapp.model.Res;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    ArrayList<Cart> cartArrayList;
    private OnPriceChangeListener onPriceChangeListener;

    public interface OnPriceChangeListener {
        void onPriceChanged(int totalPrice);
    }

    public void setOnPriceChangeListener(OnPriceChangeListener listener) {
        this.onPriceChangeListener = listener;
    }

    public CartAdapter(Context context, ArrayList<Cart> cartArrayList) {
        this.context = context;
        this.cartArrayList = cartArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_list_row, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cart = cartArrayList.get(position);

        holder.txtPackageType.setText(cart.packagingType);
        holder.txtPackagePrice.setText(cart.packagePrice + "원");
        holder.txtSizeType.setText(cart.sizeType + " (" + cart.sizePrice + "원)");
        holder.txtAddFlower.setText(cart.orderFlower + " (" + cart.orderFlowerPrice + "원)");
        Glide.with(context).load(cart.packageUrl).into(holder.imgPackage);
        holder.txtQuantity.setText(cart.quantity + "");

        // 초기 상태에서 txtQuantity 값에 따라 Visibility 설정
        int initialQuantity = cart.quantity;
        holder.updateSubDeleteVisibility(initialQuantity);

        // 초기 총 가격 계산 및 설정
        int totalPrice = calculateTotalPrice(cart);
        holder.txtPrice.setText(totalPrice + " 원");

        // 아이템이 바인딩될 때마다 총 가격을 업데이트하도록 설정
        if (position == getItemCount() - 1 && onPriceChangeListener != null) {
            onPriceChangeListener.onPriceChanged(calculateTotalCartPrice());
        }

        // 가격이 변경될 때 이벤트 트리거
        holder.txtPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (onPriceChangeListener != null) {
                    onPriceChangeListener.onPriceChanged(calculateTotalPrice(cart));
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtPackageType;
        TextView txtPackagePrice;
        TextView txtAddFlower;
        ImageView imgPackage;
        public TextView txtPrice;
        ImageView imgDelete;
        TextView txtSub;
        TextView txtQuantity;
        TextView txtAdd;
        TextView txtSizeType;
        String token;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPackageType = itemView.findViewById(R.id.txtPackageType);
            txtPackagePrice = itemView.findViewById(R.id.txtPackagePrice);
            txtAddFlower = itemView.findViewById(R.id.txtAddFlower);
            imgPackage = itemView.findViewById(R.id.imgPackage);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            txtSub = itemView.findViewById(R.id.txtSub);
            txtQuantity = itemView.findViewById(R.id.textViewQuantity);
            txtAdd = itemView.findViewById(R.id.txtAdd);
            txtSizeType = itemView.findViewById(R.id.txtSizeType);

            SharedPreferences sp = context.getSharedPreferences(Config.SP_NAME, Context.MODE_PRIVATE);
            token = sp.getString("token", "");


            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCart();
                    Intent intent = new Intent(context, EmptyCartActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            });

            txtSub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    subCartQuantity();
                }
            });

            txtAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCartQuantity();
                }
            });

        }

        private void updateSubDeleteVisibility(int quantity) {
            if (quantity == 1) {
                txtSub.setVisibility(View.GONE);
                imgDelete.setVisibility(View.VISIBLE);
            } else {
                txtSub.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.GONE);
            }
        }

        private void subCartQuantity() {
            Retrofit retrofit = NetworkClient.getRetrofitClient(context);
            CartApi api = retrofit.create(CartApi.class);

            int index = getAdapterPosition();
            Cart cart = cartArrayList.get(index);

            Call<Res> call = api.subCartQuantity(cart.cartId, "Bearer " + token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    cart.quantity--;
                    txtQuantity.setText(cart.quantity + "");
                    txtPrice.setText(calculateTotalPrice(cart) + " 원");
                    updateSubDeleteVisibility(cart.quantity);
                }

                @Override
                public void onFailure(Call<Res> call, Throwable throwable) {

                }
            });
        }

        private int calculateTotalPrice(Cart cart) {
            return (cart.packagePrice + cart.sizePrice + cart.orderFlowerPrice) * cart.quantity;
        }

        private void addCartQuantity() {
            Retrofit retrofit = NetworkClient.getRetrofitClient(context);
            CartApi api = retrofit.create(CartApi.class);

            int index = getAdapterPosition();
            Cart cart = cartArrayList.get(index);

            Call<Res> call = api.addCartQuantity(cart.cartId, "Bearer " + token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    cart.quantity++;
                    txtQuantity.setText(cart.quantity + "");
                    txtPrice.setText(calculateTotalPrice(cart) + " 원");
                    updateSubDeleteVisibility(cart.quantity);
                }

                @Override
                public void onFailure(Call<Res> call, Throwable throwable) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });

        }


        private void deleteCart() {
            Retrofit retrofit = NetworkClient.getRetrofitClient(context);
            CartApi api = retrofit.create(CartApi.class);

            int index = getAdapterPosition();
            Cart cart = cartArrayList.get(index);
            Log.i("CART ID", String.valueOf(cart.cartId));


            Call<Res> call = api.deleteCart(cart.cartId, "Bearer " + token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if (response.isSuccessful()) {
                        cartArrayList.remove(index);
                        notifyDataSetChanged();
                        updateSubDeleteVisibility(cart.quantity);
                    } else {
                        Log.i("DELETE CART", response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<Res> call, Throwable throwable) {
                    Toast.makeText(context, "네트워크 오류", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private int calculateTotalPrice(Cart cart) {
        return (cart.packagePrice + cart.sizePrice + cart.orderFlowerPrice) * cart.quantity;
    }

    // 총 가격을 계산하는 메서드
    private int calculateTotalCartPrice() {
        int totalPrice = 0;
        for (Cart cart : cartArrayList) {
            totalPrice += (cart.packagePrice + cart.sizePrice + cart.orderFlowerPrice) * cart.quantity;
        }
        return totalPrice;
    }
}
