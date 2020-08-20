package com.startag.martguy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.startag.martguy.R;
import com.startag.martguy.model.MusicSongOffline;
import com.startag.martguy.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterOffline extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MusicSongOffline> items = new ArrayList<>();

    private Context ctx;
    private  int menures;
    private AdapterOffline.OnItemClickListener mOnItemClickListener;
    private AdapterOffline.OnMoreButtonClickListener onMoreButtonClickListener;

    public void setOnItemClickListener(final AdapterOffline.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public void setOnMoreButtonClickListener(final AdapterOffline.OnMoreButtonClickListener onMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener;
    }

    public AdapterOffline(Context context, List<MusicSongOffline> items, int menures) {
        this.items = items;
        this.menures=menures;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView brief;
        public ImageButton more;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            title = (TextView) v.findViewById(R.id.title);
            brief = (TextView) v.findViewById(R.id.brief);
            more = (ImageButton) v.findViewById(R.id.more);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_song, parent, false);
        vh = new AdapterOffline.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterOffline.OriginalViewHolder) {
            AdapterOffline.OriginalViewHolder view = (AdapterOffline.OriginalViewHolder) holder;

            final MusicSongOffline musicSongOffline = items.get(position);
            view.title.setText(musicSongOffline.getFilename());
            view.brief.setText("Local");
            Tools.displayImageRoundlocal(ctx, view.image, R.drawable.img_social_android);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, position);
                    }
                }
            });

            view.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onMoreButtonClickListener == null) return;
                    onMoreButtonClick(view, position,menures);
                }
            });
        }
    }

    private void onMoreButtonClick(final View view, final int pos,int menures) {
        PopupMenu popupMenu = new PopupMenu(ctx, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onMoreButtonClickListener.onItemClick(view, pos, item);
                return true;
            }
        });
        popupMenu.inflate(menures);
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public interface OnMoreButtonClickListener {
        void onItemClick(View view, int pos, MenuItem item);
    }

}