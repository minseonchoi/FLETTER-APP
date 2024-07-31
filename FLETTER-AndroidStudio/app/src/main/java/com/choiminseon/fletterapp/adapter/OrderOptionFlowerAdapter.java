package com.choiminseon.fletterapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choiminseon.fletterapp.OrderOptionActivity;
import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.model.OrderOptionFlower;

import java.util.ArrayList;

public class OrderOptionFlowerAdapter extends RecyclerView.Adapter<OrderOptionFlowerAdapter.ViewHolder> {

    Context context;
    ArrayList<OrderOptionFlower> orderOptionFlowerArrayList;
    int selectedCount = 0;
    int maxSelectableFlowers;
    OnFlowerSelectedListener listener;

    public OrderOptionFlowerAdapter(Context context, ArrayList<OrderOptionFlower> orderOptionFlowerArrayList, int maxSelectableFlowers, OnFlowerSelectedListener listener) {
        this.context = context;
        this.orderOptionFlowerArrayList = orderOptionFlowerArrayList;
        this.maxSelectableFlowers = maxSelectableFlowers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_option_flower_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderOptionFlower orderOptionFlower = orderOptionFlowerArrayList.get(position);

        holder.txtFlowerName.setText(orderOptionFlower.flowerName);
        holder.txtFlowerPrice.setText("+ " + orderOptionFlower.flowerPrice);

        holder.checkBoxFlower.setOnCheckedChangeListener(null);
        holder.checkBoxFlower.setChecked(orderOptionFlower.isSelected);

        if (orderOptionFlower.isWish == 1) {
            holder.imgWish.setVisibility(View.VISIBLE);
        } else {
            holder.imgWish.setVisibility(View.GONE);
        }

        holder.checkBoxFlower.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedCount < maxSelectableFlowers) {
                    orderOptionFlower.isSelected = true;
                    selectedCount++;
                    if (listener != null) {
                        listener.onFlowerSelectedChanged();
                    }
                } else {
                    holder.checkBoxFlower.setChecked(false);
                    Toast.makeText(context, "꽃은 최대 " + maxSelectableFlowers + "개까지만 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            } else {
                orderOptionFlower.isSelected = false;
                selectedCount--;
                if (listener != null) {
                    listener.onFlowerSelectedChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderOptionFlowerArrayList.size();
    }

    public ArrayList<Integer> getSelectedFlowerIds() {
        ArrayList<Integer> selectedFlowerIds = new ArrayList<>();
        for (OrderOptionFlower flower : orderOptionFlowerArrayList) {
            if (flower.isSelected) {
                selectedFlowerIds.add(flower.flowerId);
            }
        }
        return selectedFlowerIds;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBoxFlower;
        TextView txtFlowerName;
        TextView txtFlowerPrice;
        ImageView imgWish;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxFlower = itemView.findViewById(R.id.checkBoxFlower);
            txtFlowerName = itemView.findViewById(R.id.txtFlowerName);
            txtFlowerPrice = itemView.findViewById(R.id.txtFlowerPrice);
            imgWish = itemView.findViewById(R.id.imgWish);
        }
    }

    public interface OnFlowerSelectedListener {
        void onFlowerSelectedChanged();
    }

    public void setRecommendedFlowers(String recommendedFlowers) {
        if (recommendedFlowers != null && !recommendedFlowers.isEmpty()) {
            String[] flowers = recommendedFlowers.split(",");
            for (String flowerName : flowers) {
                for (OrderOptionFlower flower : orderOptionFlowerArrayList) {
                    if (flower.flowerName.equals(flowerName.trim())) {
                        flower.isSelected = true;
                        selectedCount++;

                    }
                }
            }
            notifyDataSetChanged();
        }
    }
}
