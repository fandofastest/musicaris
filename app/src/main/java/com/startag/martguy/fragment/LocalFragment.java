package com.startag.martguy.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.startag.martguy.BuildConfig;
import com.startag.martguy.R;
import com.startag.martguy.activity.MainActivity;
import com.startag.martguy.adapter.AdapterOffline;
import com.startag.martguy.model.MusicSongOffline;
import com.startag.martguy.servicemusic.PlayerService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LocalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalFragment extends Fragment {
    AdapterOffline mAdapter;
    Context context;
    RecyclerView recyclerView;

    public LocalFragment() {
    }

    public static LocalFragment newInstance() {
        LocalFragment fragment = new LocalFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_music_song, container, false);


        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        context=getContext();



        //set data and list adapter
        mAdapter = new AdapterOffline(getActivity(), PlayerService.listlocal,R.menu.menu_more_local);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterOffline.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {


                ((MainActivity)context).playmusicoffline(pos,PlayerService.listlocal);

            }
        });



        mAdapter.setOnMoreButtonClickListener(new AdapterOffline.OnMoreButtonClickListener()  {
            @Override
            public void onItemClick(View view, int pos, MenuItem item) {


                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }






            }


        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMusic();



    }

    public void getMusic(){

        PlayerService.listlocal.clear();
        recyclerView.removeAllViews();


        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor =  getContext().getContentResolver().query(allSongsUri, null, null, null, selection);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    MusicSongOffline modalClass = new MusicSongOffline();
                    modalClass.setFilename(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                    modalClass.setFilepath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                    modalClass.setSize(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)));
                    modalClass.setDuration(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                    modalClass.setType("offline");
                    PlayerService.listlocal.add(modalClass);




                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        mAdapter.notifyDataSetChanged();
    }




}