package com.choiminseon.fletterapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.model.OrderOptionSize;

import java.util.ArrayList;

public class OrderOptionSizeAdapter extends RecyclerView.Adapter<OrderOptionSizeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderOptionSize> orderOptionSizeArrayList;
    private OnSizeSelectedListener listener;
    private int selectedPosition = -1; // 선택된 위치

    public OrderOptionSizeAdapter(Context context, ArrayList<OrderOptionSize> orderOptionSizeArrayList, OnSizeSelectedListener listener) {
        this.context = context;
        this.orderOptionSizeArrayList = orderOptionSizeArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_option_size_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderOptionSize orderOptionSize = orderOptionSizeArrayList.get(position);

        holder.txtSizeType.setText(orderOptionSize.sizeType);
        holder.txtSizePrice.setText("+ " + orderOptionSize.sizePrice);
        holder.checkBoxSize.setOnCheckedChangeListener(null); // 리스너 초기화
        holder.checkBoxSize.setChecked(position == selectedPosition);

        holder.checkBoxSize.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (selectedPosition != position) {
                    if (selectedPosition != -1) {
                        // 기존에 선택된 항목이 있다면 선택 해제
                        OrderOptionSize previouslySelected = orderOptionSizeArrayList.get(selectedPosition);
                        previouslySelected.isSelected = false;
                        notifyItemChanged(selectedPosition);
                    }
                    selectedPosition = position;
                    orderOptionSize.isSelected = true;
                    if (listener != null) {
                        listener.onSizeSelectedChanged();
                    }
                }
            } else {
                selectedPosition = -1;
                orderOptionSize.isSelected = false;
                if (listener != null) {
                    listener.onSizeSelectedChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderOptionSizeArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBoxSize;
        TextView txtSizeType;
        TextView txtSizePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxSize = itemView.findViewById(R.id.checkBoxSize);
            txtSizeType = itemView.findViewById(R.id.txtSizeType);
            txtSizePrice = itemView.findViewById(R.id.txtSizePrice);





        }
    }

    public interface OnSizeSelectedListener {
        void onSizeSelectedChanged();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public int getSelectedSizeId() {
        if (selectedPosition != -1) {
            return orderOptionSizeArrayList.get(selectedPosition).sizeId;
        }
        return -1; // 선택되지 않은 경우
    }
}
