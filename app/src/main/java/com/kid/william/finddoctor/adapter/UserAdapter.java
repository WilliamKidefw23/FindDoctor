package com.kid.william.finddoctor.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.kid.william.finddoctor.R;

public class UserAdapter {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textemail;
        public ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            //textemail = itemView.findViewById(R.id.text_email);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view,getAdapterPosition());
        }
    }
}
