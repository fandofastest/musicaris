package com.startag.martguy.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.startag.martguy.BuildConfig;
import com.startag.martguy.R;
import com.startag.martguy.adapter.AdapterListMusicSong;
import com.startag.martguy.ads.Interstitial;
import com.startag.martguy.model.MusicSongOnline;
import com.startag.martguy.servicemusic.PlayerService;
import com.startag.martguy.utils.MusicUtils;
import com.startag.martguy.utils.RealmHelper;
import com.startag.martguy.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.widget.Toast.LENGTH_LONG;
import static com.startag.martguy.utils.Tools.KEYSC;

public class SearchActivity extends AppCompatActivity {

    AdapterListMusicSong adapterListMusicSong;
    List <MusicSongOnline> listmysong = new ArrayList<>();
    RecyclerView recyclerView;
    String type,query;
    Realm realm;
    RealmHelper realmHelper;
    public View parent_view;
    TextView hometitle,homeartist;

    private ViewPager view_pager;
    private TabLayout tab_layout;

    private ImageButton bt_play;
    private ProgressBar song_progressbar;
    private AdapterListMusicSong mAdapter;

    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    private MusicUtils utils;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initToolbar();
        initComponent();
        initrealm();
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        adapterListMusicSong = new AdapterListMusicSong(getApplicationContext(), listmysong,R.menu.menu_song_more);
        recyclerView.setAdapter(adapterListMusicSong);
        adapterListMusicSong.setOnItemClickListener(new AdapterListMusicSong.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                playmusic(pos,listmysong);
            }
        });
        adapterListMusicSong.setOnMoreButtonClickListener(new AdapterListMusicSong.OnMoreButtonClickListener() {
            @Override
            public void onItemClick(View view, int pos, MenuItem item) {

                if (item.getTitle().equals("Add to playlist")){
                    MusicSongOnline musicSongOnline =listmysong.get(pos);
                    addtoplaylits(musicSongOnline);

                }

                else if (item.getTitle().equals("Play")){

                    playmusic(pos,listmysong);
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

        type=getIntent().getStringExtra("type");

        System.out.println("zcz "+type);

        if (type.equals("genre")){

            String genre=getIntent().getStringExtra("genrename");
            Objects.requireNonNull(getSupportActionBar()).setTitle(genre);
            String genreori=getIntent().getStringExtra("genreorigin");
            getsongs(genreori,type);

        }
        else if (type.equals("search")){

            String q=getIntent().getStringExtra("q");
            Objects.requireNonNull(getSupportActionBar()).setTitle(q);

            getsongs(q,type);

        }

        if (PlayerService.PLAYERSTATUS.equals("PLAYING")){
            bt_play.setImageResource(R.drawable.ic_pause);
            hometitle.setText(PlayerService.currenttitle);
            homeartist.setText(PlayerService.currentartist);
        }
        else {
            bt_play.setImageResource(R.drawable.ic_play_arrow);
            hometitle.setText("No Song");
            homeartist.setText("");
        }



    }

    private void initrealm() {
        Realm.init(SearchActivity.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);
    }

    public  void addtoplaylits(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saveplaylists(musicSongOnline);
        adapterListMusicSong.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Added to Playlists",LENGTH_LONG).show();


    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Music Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);
    }


    public void getsongs(final String q, final String type){
        listmysong.clear();
        recyclerView.removeAllViews();
        String url;
        if (type.equals("genre")){
             url="https://api-v2.soundcloud.com/charts?genre=soundcloud:genres:"+q+"&high_tier_only=false&kind=top&limit=100&client_id="+KEYSC;
        }
        else{
             url="https://api-v2.soundcloud.com/search/tracks?q="+q+"&client_id="+KEYSC+"&limit=100";

        }
        final JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

//                linearLayout.setVisibility(View.GONE);

                if (type.equals("genre")){
                    try {
                        JSONArray jsonArray1=response.getJSONArray("collection");

                        for (int i = 0;i<jsonArray1.length();i++){
                            JSONObject jsonObject1=jsonArray1.getJSONObject(i);
                            JSONObject jsonObject=jsonObject1.getJSONObject("track");
                            MusicSongOnline listModalClass = new MusicSongOnline();
                            listModalClass.setId(jsonObject.getInt("id"));
                            listModalClass.setTitle(jsonObject.getString("title"));
                            listModalClass.setImageurl(jsonObject.getString("artwork_url"));
                            listModalClass.setDuration(jsonObject.getString("full_duration"));
                            listModalClass.setType("online");
                            listModalClass.setArtist(q);
                         listmysong.add(listModalClass);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }}

               else if (type.equals("search")){
                    try {
                        JSONArray jsonArray1=response.getJSONArray("collection");

                        for (int i = 0;i<jsonArray1.length();i++){
                            JSONObject jsonObject=jsonArray1.getJSONObject(i);
                            MusicSongOnline listModalClass = new MusicSongOnline();
                            listModalClass.setId(jsonObject.getInt("id"));
                            listModalClass.setTitle(jsonObject.getString("title"));
                            listModalClass.setImageurl(jsonObject.getString("artwork_url"));
                            listModalClass.setDuration(jsonObject.getString("full_duration"));
                            listModalClass.setType("online");


                            try {
                                JSONObject jsonArray3=jsonObject.getJSONObject("publisher_metadata");
                                listModalClass.setArtist(jsonArray3.getString("artist"));

                            }
                            catch (JSONException e){
                                listModalClass.setArtist("Artist");

                            }
                         listmysong.add(listModalClass);


                        }





                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }



                adapterListMusicSong.notifyDataSetChanged();
//                songAdapter.notifyDataSetChanged();
                //    System.out.println("update"+listsongModalSearch);




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);


    }

    public void playmusic (int position ,List<MusicSongOnline> listsong){
        PlayerService.currentlist=listsong;
        Log.e("errr", String.valueOf(position));
        Intent intent = new Intent(SearchActivity.this, PlayerMusicActivity.class);
        intent.putExtra("from","search");
        intent.putExtra("pos",position);
        Interstitial interstitial = new Interstitial();
        interstitial.showinter(SearchActivity.this,getString(R.string.interadmob),intent);

    }

    private void initComponent() {

        hometitle=findViewById(R.id.titlehomeplayer);
        homeartist=findViewById(R.id.artishomeplayer);


        bt_play = (ImageButton) findViewById(R.id.bt_play);
        song_progressbar = (ProgressBar) findViewById(R.id.song_progressbar);

        // set Progress bar values
        song_progressbar.setProgress(0);
        song_progressbar.setMax(MusicUtils.MAX_PROGRESS);
        getlocalbroadcaster();




        utils = new MusicUtils();

        buttonPlayerAction();
    }

    public void getlocalbroadcaster(){
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                if (status.equals("playing")){

                    bt_play.setVisibility(View.VISIBLE);
                    bt_play.setImageResource(R.drawable.ic_pause);
                    hometitle.setText(PlayerService.currenttitle);
                    homeartist.setText(PlayerService.currentartist);
                    mHandler.post(mUpdateTimeTask);

                }
                else if (status.equals("pause")){
                    bt_play.setImageResource(R.drawable.ic_play_arrow);
                }

            }
        }, new IntentFilter("musicplayer"));

    }

    public void pause (){
        bt_play.setImageResource(R.drawable.ic_play_arrow);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "pause");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    public void resume (){
        bt_play.setImageResource(R.drawable.ic_pause);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "resume");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);

    }


    private void buttonPlayerAction() {
        bt_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (PlayerService.PLAYERSTATUS.equals("PLAYING")) {
                    pause();
                } else {
                    resume();
                    // Resume song

                }

            }
        });
    }

    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_expand: {
                if (PlayerService.PLAYERSTATUS.equals("PLAYING")){
                    Intent intent = new Intent(SearchActivity.this, PlayerMusicActivity.class);
                    intent.putExtra("from","player");
                    startActivity(intent);
                }
                else {
                    Snackbar.make(parent_view, "No Music Was Playing", Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    /**
     * Background Runnable thread
     */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            updateTimerAndSeekbar();
            // Running this thread after 10 milliseconds
            if (PlayerService.PLAYERSTATUS.equals("PLAYING")) {
                mHandler.postDelayed(this, 100);
            }
        }
    };

    private void updateTimerAndSeekbar() {
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "getduration");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(PlayerService.currentduraiton, PlayerService.totalduration));
        song_progressbar.setProgress(progress);
    }

}
