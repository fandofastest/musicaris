package com.uiuxdesign.uiuxdesignmaterials.fragment;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.uiuxdesign.uiuxdesignmaterials.R;
import com.uiuxdesign.uiuxdesignmaterials.activity.SearchActivity;
import com.uiuxdesign.uiuxdesignmaterials.adapter.AdapterGridMusicAlbum;
import com.uiuxdesign.uiuxdesignmaterials.model.MusicAlbum;
import com.uiuxdesign.uiuxdesignmaterials.servicemusic.PlayerService;
import com.uiuxdesign.uiuxdesignmaterials.utils.Tools;
import com.uiuxdesign.uiuxdesignmaterials.widget.SpacingItemDecoration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FragmentMusicAlbum extends Fragment {

    public FragmentMusicAlbum() {
    }


    AdapterGridMusicAlbum mAdapter;

    List <MusicAlbum> listgenre = new ArrayList<>();

    public static FragmentMusicAlbum newInstance() {
        FragmentMusicAlbum fragment = new FragmentMusicAlbum();
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_product_grid, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 4), true));
        recyclerView.setHasFixedSize(true);


        //set data and list adapter
        mAdapter = new AdapterGridMusicAlbum(getActivity(), listgenre);
        recyclerView.setAdapter(mAdapter);
        getallgenre();
        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridMusicAlbum.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MusicAlbum obj, int position) {

                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("type","genre");
                intent.putExtra("genrename",obj.getGenrename());
                intent.putExtra("genreorigin",obj.getOrigenrename());
                startActivity(intent);

            }
        });

        return root;
    }

    public void  getallgenre (){
        listgenre.clear();

        listgenre.add(new MusicAlbum(R.drawable.image_1,"Alternative Rock"));
        listgenre.add(new MusicAlbum(R.drawable.image_1,"Ambient"));
        listgenre.add(new MusicAlbum(R.drawable.image_1,"Audiobooks"));
        listgenre.add(new MusicAlbum(R.drawable.image_1,"Business"));
        listgenre.add(new MusicAlbum(R.drawable.image_1,"Classical"));
        listgenre.add(new MusicAlbum(R.drawable.image_1,"Comedy"));
        listgenre.add(new MusicAlbum(R.drawable.image_1,"Country"));

        mAdapter.notifyDataSetChanged();


    }
}