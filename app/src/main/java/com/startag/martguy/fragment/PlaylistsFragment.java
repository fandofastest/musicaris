package com.startag.martguy.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.startag.martguy.BuildConfig;
import com.startag.martguy.R;
import com.startag.martguy.activity.MainActivity;
import com.startag.martguy.adapter.AdapterListMusicSong;
import com.startag.martguy.model.MusicSongOnline;
import com.startag.martguy.servicemusic.PlayerService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlaylistsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlaylistsFragment extends Fragment {
    AdapterListMusicSong mAdapter;
    Context context;

    public PlaylistsFragment() {
    }

    public static PlaylistsFragment newInstance() {
        PlaylistsFragment fragment = new PlaylistsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_music_song, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        context=getContext();



        //set data and list adapter
        mAdapter = new AdapterListMusicSong(getActivity(), PlayerService.listplaylist,R.menu.menu_more_playlists);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListMusicSong.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {

                if (context instanceof MainActivity) {
                    ((MainActivity)context).playmusic(pos,PlayerService.listplaylist);
                }

            }


        });

        mAdapter.setOnMoreButtonClickListener(new AdapterListMusicSong.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, int pos, MenuItem item) {


                MusicSongOnline musicSongOnline =PlayerService.listplaylist.get(pos);

              if (item.getTitle().equals("Remove")){

                    if (context instanceof MainActivity) {
                        ((MainActivity)context).removeplaylists(musicSongOnline);
                    }
                }

                else {
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


            }


        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);




        context=getContext();






        if (context instanceof MainActivity) {
            PlayerService.listplaylist=((MainActivity)context).getplaylists();
            mAdapter.notifyDataSetChanged();


        }






    }



}