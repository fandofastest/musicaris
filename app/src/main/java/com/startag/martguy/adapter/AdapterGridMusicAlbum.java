package com.startag.martguy.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.startag.martguy.R;
import com.startag.martguy.model.MusicAlbum;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AdapterGridMusicAlbum extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MusicAlbum> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, MusicAlbum obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGridMusicAlbum(Context context, List<MusicAlbum> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView brief;
        public LinearLayout lyt_bottom;
        public View lyt_parent;
        LinearLayout bggenre;
        TextView genreicon;

        public OriginalViewHolder(View v) {
            super(v);
//            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            bggenre=v.findViewById(R.id.bggenre);

//            lyt_bottom = (LinearLayout) v.findViewById(R.id.lyt_bottom);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            genreicon=v.findViewById(R.id.genreicon);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_album, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MusicAlbum obj = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.name.setText(obj.getGenrename());
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            view.bggenre.setBackgroundColor(color);
            char ch1 = obj.getGenrename().charAt(0);

            view.genreicon.setText(String.valueOf(ch1));

//            view.image.setImageResource(obj.getImage());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}