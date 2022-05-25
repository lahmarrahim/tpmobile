package com.example.monderniertp;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class FavViewHolder extends RecyclerView.ViewHolder{
    public TextView title;

    public FavViewHolder(View itemView) {
        super(itemView);
        findViews(itemView);
    }
    private void findViews(View view) {
        title = (TextView) view.findViewById(R.id.title);
    }
    public void setItem(final Fav fav) {
        title.setText(fav.getTitle());
    }
}
