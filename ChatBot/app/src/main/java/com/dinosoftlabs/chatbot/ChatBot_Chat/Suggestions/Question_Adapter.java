package com.dinosoftlabs.chatbot.ChatBot_Chat.Suggestions;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dinosoftlabs.chatbot.R;

import java.util.ArrayList;
import java.util.List;



public class Question_Adapter extends RecyclerView.Adapter<Question_Adapter.CustomViewHolder >{

    public Context context;
    List<String> datalist = new ArrayList<>();
    private Question_Adapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public Question_Adapter(Context context, List<String> urllist, Question_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.datalist=urllist;
        this.listener = listener;

    }

    @Override
    public Question_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_question_hint_layout,null);
        Question_Adapter.CustomViewHolder viewHolder = new Question_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return datalist.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView question_txt;

        public CustomViewHolder(View view) {
            super(view);
            question_txt=view.findViewById(R.id.question_txt);
        }

        public void bind(final String item, final Question_Adapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });

        }

    }

    @Override
    public void onBindViewHolder(final Question_Adapter.CustomViewHolder holder, final int i) {
        holder.bind(datalist.get(i),listener);
        holder.question_txt.setText(datalist.get(i));
   }

}