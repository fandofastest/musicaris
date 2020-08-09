package com.uiuxdesign.uiuxdesignmaterials.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;


import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import com.uiuxdesign.uiuxdesignmaterials.R;
import com.uiuxdesign.uiuxdesignmaterials.fragment.FragmentMusicAlbum;
import com.uiuxdesign.uiuxdesignmaterials.fragment.FragmentMusicSong;
import com.uiuxdesign.uiuxdesignmaterials.adapter.AdapterListMusicSong;
import com.uiuxdesign.uiuxdesignmaterials.fragment.PlaylistsFragment;
import com.uiuxdesign.uiuxdesignmaterials.fragment.RecentFragment;
import com.uiuxdesign.uiuxdesignmaterials.model.MusicSongOnline;
import com.uiuxdesign.uiuxdesignmaterials.servicemusic.PlayerService;
import com.uiuxdesign.uiuxdesignmaterials.utils.MusicUtils;
import com.uiuxdesign.uiuxdesignmaterials.utils.RealmHelper;
import com.uiuxdesign.uiuxdesignmaterials.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {

    public View parent_view;
    TextView hometitle,homeartist;

    private ViewPager view_pager;
    private TabLayout tab_layout;
    androidx.appcompat.widget.SearchView mysearchview;

    private ImageButton bt_play;
    private ProgressBar song_progressbar;
    private AdapterListMusicSong mAdapter;

    // Media Player
    private MediaPlayer mp;
    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();

    //private SongsManager songManager;
    private MusicUtils utils;
    SectionsPagerAdapter adapter;
    Realm realm;
    RealmHelper realmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent_view = findViewById(R.id.parent_view);
        initToolbar();
        initComponent();
        initrealm();
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



    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Music Player");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);






    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(view_pager);
        hometitle=findViewById(R.id.titlehomeplayer);
        homeartist=findViewById(R.id.artishomeplayer);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        tab_layout.setupWithViewPager(view_pager);

        bt_play = (ImageButton) findViewById(R.id.bt_play);
        song_progressbar = (ProgressBar) findViewById(R.id.song_progressbar);

        // set Progress bar values
        song_progressbar.setProgress(0);
        song_progressbar.setMax(MusicUtils.MAX_PROGRESS);
        getlocalbroadcaster();




        utils = new MusicUtils();

        buttonPlayerAction();
    }

    /**
     * Play button click event plays a song and changes button to pause image
     * pauses a song and changes button to play image
     */













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
                    Intent intent = new Intent(MainActivity.this, PlayerMusicActivity.class);
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

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    private void setupViewPager(ViewPager viewPager) {
       adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(FragmentMusicSong.newInstance(), "SONGS");
        adapter.addFragment(FragmentMusicAlbum.newInstance(), "GENRE");
        adapter.addFragment(RecentFragment.newInstance(), "RECENT");
        adapter.addFragment(PlaylistsFragment.newInstance(), "PLAYLIST");
        adapter.addFragment(PlaylistsFragment.newInstance(), "LOCAL MUSIC");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        MenuItem searchIem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchIem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onQueryTextSubmit(String query) {

               Intent intent = new Intent(MainActivity.this,SearchActivity.class);
               intent.putExtra("type","search");
               intent.putExtra("q",query);
               startActivity(intent);

                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {



        }
        return super.onOptionsItemSelected(item);
    }



    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE ;
        }
    }


    public void playmusic (int position ,List<MusicSongOnline> listsong){

        PlayerService.currentlist=listsong;


        Intent intent = new Intent(MainActivity.this, PlayerMusicActivity.class);
        intent.putExtra("from","select");
        intent.putExtra("pos",position);
        startActivity(intent);








    }
    public void  initrealm(){
        Realm.init(MainActivity.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

    }


    public List<MusicSongOnline> getrecent(){
        realmHelper = new RealmHelper(realm,getApplication());
        PlayerService.listrecent=  realmHelper.getAllSongsrecent();

        return  PlayerService.listrecent;

    }

    public List<MusicSongOnline> getplaylists(){
        realmHelper = new RealmHelper(realm,getApplication());
        PlayerService.listplaylist=  realmHelper.getallplaylists();

        return  PlayerService.listplaylist;

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

    public  void addtoplaylits(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.saveplaylists(musicSongOnline);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Added to Playlists",LENGTH_LONG).show();


    }


    public  void removerecent(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromrecent(musicSongOnline);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Removed",LENGTH_LONG).show();


    }

    public  void removeplaylists(MusicSongOnline musicSongOnline){
        realmHelper = new RealmHelper(realm,getApplication());
        realmHelper.removefromplaylists(musicSongOnline);
        adapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(),"Removed",LENGTH_LONG).show();


    }
}
