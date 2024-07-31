package com.choiminseon.fletterapp.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.choiminseon.fletterapp.R;
import com.choiminseon.fletterapp.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER_MESSAGE = 1;
    private static final int VIEW_TYPE_BOT_MESSAGE = 2;
    private static final int VIEW_TYPE_BOT_MESSAGE_WITH_OPTIONS = 3;

    private ArrayList<ChatMessage> chatMessages;
    private OnOptionClickListener optionClickListener;

    public interface OnOptionClickListener {
        void onOptionClicked(String option);
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;

        public UserMessageViewHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
        }
    }

    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;

        public BotMessageViewHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
        }
    }

    public static class BotMessageWithOptionsViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public LinearLayout optionsLayout;

        public BotMessageWithOptionsViewHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            optionsLayout = view.findViewById(R.id.optionsLayout);
        }
    }

    public ChatAdapter(ArrayList<ChatMessage> chatMessages, OnOptionClickListener optionClickListener) {
        this.chatMessages = chatMessages;
        this.optionClickListener = optionClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.isSentByUser()) {
            return VIEW_TYPE_USER_MESSAGE;
        } else if (message.hasOptions()) {
            return VIEW_TYPE_BOT_MESSAGE_WITH_OPTIONS;
        } else {
            return VIEW_TYPE_BOT_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_BOT_MESSAGE_WITH_OPTIONS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message_with_options, parent, false);
            return new BotMessageWithOptionsViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bot_message, parent, false);
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).textViewMessage.setText(message.getMessage());
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).textViewMessage.setText(message.getMessage());
        } else if (holder instanceof BotMessageWithOptionsViewHolder) {
            BotMessageWithOptionsViewHolder viewHolder = (BotMessageWithOptionsViewHolder) holder;
            viewHolder.textViewMessage.setText(message.getMessage());
            viewHolder.optionsLayout.removeAllViews();
            for (String option : message.getOptions()) {
                Button button = new Button(viewHolder.itemView.getContext());
                button.setText(option);

                Typeface typeface = ResourcesCompat.getFont(viewHolder.itemView.getContext(), R.font.font);
                button.setTypeface(typeface);

                // 버튼 백그라운드를 커스텀 XML drawable로 설정
                button.setBackgroundResource(R.drawable.solid_button);

                // 버튼 간의 간격을 추가
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5, viewHolder.itemView.getContext().getResources().getDisplayMetrics()
                );
                params.setMargins(marginInDp, marginInDp, marginInDp, marginInDp);

                // 버튼의 높이를 조정
                int buttonHeightInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 40, viewHolder.itemView.getContext().getResources().getDisplayMetrics()
                );
                params.height = buttonHeightInDp;

                button.setLayoutParams(params);

                button.setOnClickListener(v -> optionClickListener.onOptionClicked(option));
                viewHolder.optionsLayout.addView(button);
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
}

