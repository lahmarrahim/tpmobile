
package com.example.monderniertp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavsAdapter extends RecyclerView.Adapter<FavViewHolder> {

    Context context;
    ArrayList<Fav> favs;

    public FavsAdapter(Context context, ArrayList<Fav> favs) {
        this.favs = favs;
        this.context = context;
    }

    @Override
    public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View View = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new FavViewHolder(View);
    }

    @Override
    public void onBindViewHolder(FavViewHolder holder, final int position) {
        final Fav fav = favs.get(position);
        context = (FavsActivity) holder.itemView.getContext();




    }

    @Override
    public int getItemCount() {
        return favs.size();
    }

}
