package com.bit7skes.bake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bit7skes.bake.models.Cake;

import java.util.ArrayList;
import java.util.List;

public class CakeAdapter extends RecyclerView.Adapter<CakeAdapter.PlaceViewHolder> {

    private Context mContext;
    private final CakeAdapterOnClickHandler mClickHandler;
    private List<Integer> cakeIds = new ArrayList<>();
    private List<String> cakeNames = new ArrayList<>();
    private List<Cake> cakeList = new ArrayList<>();
    private Cake clickedCake;

    public interface CakeAdapterOnClickHandler {
        void onClick(Cake cake);
    }

    public CakeAdapter(CakeAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_main_page, parent, false);
        return new PlaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceViewHolder holder, int position) {
        holder.mCakeNameTextView.setText(cakeNames.get(position));
    }

    @Override
    public int getItemCount() {
        if (cakeIds != null) {
            return cakeIds.size();
        } else return 0;
    }

    void setData(List<Cake> cakes) {
        cakeList = cakes;
        for (Cake cake: cakes) {
            cakeIds.add(cake.getId());
            cakeNames.add(cake.getName());
        }
        notifyDataSetChanged();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mCakeNameTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            mCakeNameTextView = itemView.findViewById(R.id.cake_name_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            clickedCake = cakeList.get(adapterPosition);
            try {
                mClickHandler.onClick(clickedCake);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
