package com.startag.martguy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bullhead.equalizer.DialogEqualizerFragment;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.startag.martguy.R;
import com.startag.martguy.ads.Banner;
import com.startag.martguy.servicemusic.PlayerService;
import com.startag.martguy.utils.MusicUtils;
import com.startag.martguy.utils.Tools;

import static android.content.ContentValues.TAG;
import static com.startag.martguy.servicemusic.PlayerService.sessionId;
import static com.startag.martguy.servicemusic.PlayerService.totalduration;

public class PlayerMusicActivity extends AppCompatActivity {

    private View parent_view;
    private FloatingActionButton bt_play;
    private ProgressBar song_progressbar,progressBarplay;
    TextView title,artist,totaldura,currendura;
    ImageButton repeat,shuffle;
    ImageView imageView;
    private SeekBar seekBar;
    private InterstitialAd interstitialAd;
    com.google.android.gms.ads.InterstitialAd mInterstitialAd;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();
    String from;
    //private SongsManager songManager;
    private MusicUtils utils;
    int pos;
    KProgressHUD hud;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_music);
        initToolbar();
        initComponent();
        from=getIntent().getStringExtra("from");
        Banner banner = new Banner();
        Display display = getWindowManager().getDefaultDisplay();
       LinearLayout bannerlayout=findViewById(R.id.banner_container);

        banner.ShowBannerAds(PlayerMusicActivity.this,bannerlayout,getString(R.string.fbbanner),getString(R.string.admobbanner),display);


        hud = KProgressHUD.create(PlayerMusicActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setDetailsLabel("Downloading data")
                .setMaxProgress(100);


        if (from.equals("online")){
            pos=getIntent().getIntExtra("pos",0);
            getlocalbroadcaster();
            Intent playerservice= new Intent(PlayerMusicActivity.this, PlayerService.class);

            playerservice.putExtra("from",from);
            playerservice.putExtra("pos",pos);
            startService(playerservice);

        }

        else if (from.equals("offline")){
            pos=getIntent().getIntExtra("pos",0);
            getlocalbroadcaster();
            Intent playerservice= new Intent(PlayerMusicActivity.this, PlayerService.class);
            playerservice.putExtra("from",from);

            playerservice.putExtra("pos",pos);
            startService(playerservice);

        }

        else if (from.equals("player")){

            bt_play.setVisibility(View.VISIBLE);
            bt_play.setImageResource(R.drawable.ic_pause);
            title.setText(PlayerService.currenttitle);
            artist.setText(PlayerService.currentartist);
            mHandler.post(mUpdateTimeTask);
            progressBarplay.setVisibility(View.GONE);
        }

        else if (from.equals("search")){
            pos=getIntent().getIntExtra("pos",0);
            getlocalbroadcaster();
            Intent playerservice= new Intent(PlayerMusicActivity.this, PlayerService.class);

            playerservice.putExtra("from","online");
            playerservice.putExtra("pos",pos);
            startService(playerservice);

        }









    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
    }

    private void initComponent() {
        seekBar=findViewById(R.id.seekbar);

        parent_view = findViewById(R.id.parent_view);
        bt_play = (FloatingActionButton) findViewById(R.id.bt_play);
        shuffle=findViewById(R.id.bt_shuffle);
        repeat=findViewById(R.id.bt_repeat);
        totaldura=findViewById(R.id.timetotal);
        currendura=findViewById(R.id.timenow);
        imageView=findViewById(R.id.image);


        seekBar.setProgress(0);

        seekBar.setMax(MusicUtils.MAX_PROGRESS);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar1, int progress, boolean b) {

                if(b){

                    seekBar.setProgress(progress);
                    double currentseek = ((double) progress/(double)MusicUtils.MAX_PROGRESS);

                    int totaldura= (int) totalduration;
                    int seek= (int) (totaldura*currentseek);

                    Intent intent = new Intent("musicplayer");
                    intent.putExtra("status", "seek");
                    intent.putExtra("seektime",seek);
                    LocalBroadcastManager.getInstance(PlayerMusicActivity.this).sendBroadcast(intent);

                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Tools.displayImageOriginal(PlayerMusicActivity.this,imageView,PlayerService.currentimageurl);



        if (PlayerService.SHUFFLE.equals("OFF")){
            shuffle.setColorFilter(R.color.grey_700);
        }

        if (PlayerService.REPEAT.equals("OFF")){
            repeat.setColorFilter(R.color.grey_700);
        }
        song_progressbar = (ProgressBar) findViewById(R.id.song_progressbar);
        title=findViewById(R.id.txttitle);
        artist=findViewById(R.id.txtartist);
        progressBarplay=findViewById(R.id.progressplay);

        // set Progress bar values
        song_progressbar.setProgress(0);
        song_progressbar.setMax(MusicUtils.MAX_PROGRESS);



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
                    // Resume song
                    resume();

                }

            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void controlClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.bt_prev: {
              prev();
              progressBarplay.setVisibility(View.VISIBLE);
              bt_play.setVisibility(View.GONE);
                Snackbar.make(parent_view, "Previous", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_next: {
               next();
                bt_play.setVisibility(View.GONE);
                progressBarplay.setVisibility(View.VISIBLE);
                Snackbar.make(parent_view, "Next", Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_shuffle: {

                if (PlayerService.SHUFFLE.equals("OFF")){

                    PlayerService.SHUFFLE="ON";
                    shuffle.clearColorFilter();
                }

                else {
                    PlayerService.SHUFFLE="OFF";
                    shuffle.setColorFilter(R.color.grey_700);
                }



                Snackbar.make(parent_view, "Shuffle" +PlayerService.SHUFFLE, Snackbar.LENGTH_SHORT).show();
                break;
            }
            case R.id.bt_repeat: {



                if (PlayerService.REPEAT.equals("OFF")){

                    PlayerService.REPEAT="ON";
                    repeat.clearColorFilter();

                }

                else {
                    PlayerService.REPEAT="OFF";
                    repeat.setColorFilter(R.color.grey_700);

                }



                Snackbar.make(parent_view, "Repeat" +PlayerService.REPEAT, Snackbar.LENGTH_SHORT).show();


                break;
            }
        }
    }

    private boolean toggleButtonColor(ImageButton bt) {
        String selected = (String) bt.getTag(bt.getId());
        if (selected != null) { // selected
            bt.setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.SRC_ATOP);
            bt.setTag(bt.getId(), null);
            return false;
        } else {
            bt.setTag(bt.getId(), "selected");
            bt.setColorFilter(getResources().getColor(R.color.red_500), PorterDuff.Mode.SRC_ATOP);
            return true;
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
        currendura.setText(utils.milliSecondsToTimer(PlayerService.currentduraiton));
        totaldura.setText(utils.milliSecondsToTimer(totalduration));
        // Updating progress bar
        int progress = (int) (utils.getProgressSeekBar(PlayerService.currentduraiton, totalduration));
        song_progressbar.setProgress(progress);
        seekBar.setProgress(progress);
    }

    // stop player when destroy
    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mUpdateTimeTask);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting_round, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.colorPrimary));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View view =findViewById(R.id.mv);
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            PopupMenu popupMenu = new PopupMenu(PlayerMusicActivity.this,view );
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (item.getTitle().equals("Equalizer")){
                    showeq();
                    }

                    return true;
                }
            });
            popupMenu.inflate(R.menu.menu_player);
            popupMenu.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void  showeq(){




        if (PlayerService.PLAYERSTATUS.equals("PLAYING")){
            showinterfb(getString(R.string.interfb),getString(R.string.interadmob));

        }

        else {
            Snackbar.make(parent_view, "Please Play Music", Snackbar.LENGTH_SHORT).show();
        }





    }

    public void getlocalbroadcaster(){
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");
                if (status.equals("playing")){
                    progressBarplay.setVisibility(View.GONE);
                    bt_play.setVisibility(View.VISIBLE);
                    bt_play.setImageResource(R.drawable.ic_pause);
                    title.setText(PlayerService.currenttitle);
                    artist.setText(PlayerService.currentartist);
                    mHandler.post(mUpdateTimeTask);
                    Tools.displayImageOriginal(PlayerMusicActivity.this,imageView,PlayerService.currentimageurl);

                }
                else if (status.equals("stoping")){

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
    public void next (){
        currendura.setText("");
        totaldura.setText("");
        title.setText("Please Wait");
        artist.setText("");
        imageView.setImageResource(R.color.white_transparency);

        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "next");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);

    }
    public void prev (){
        currendura.setText("");
        totaldura.setText("");
        title.setText("Please Wait");
        artist.setText("");
        imageView.setImageResource(R.color.white_transparency);
        Intent intent = new Intent("musicplayer");
        intent.putExtra("status", "prev");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        mHandler.post(mUpdateTimeTask);

    }

    public void showinterfb( String interfb, final String inter){

        hud.show();

        interstitialAd = new InterstitialAd(PlayerMusicActivity.this, interfb);
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                hud.dismiss();
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
                DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                        .setAudioSessionId(sessionId)
                        .themeColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorPrimary))
                        .textColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.grey_1000))
                        .accentAlpha(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorAccentLight))
                        .darkColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorPrimaryDark))
                        .setAccentColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorAccent))
                        .build();
                fragment.show(getSupportFragmentManager(), "eq");


            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                showinter(inter);
                hud.dismiss();

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
                hud.dismiss();

            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();

    }


    public  void showinter( String inter) {
        hud.show();


        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(PlayerMusicActivity.this);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                hud.show();
                DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                        .setAudioSessionId(sessionId)
                        .themeColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorPrimary))
                        .textColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.grey_1000))
                        .accentAlpha(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorAccentLight))
                        .darkColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorPrimaryDark))
                        .setAccentColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorAccent))
                        .build();
                fragment.show(getSupportFragmentManager(), "eq");



                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                hud.show();
                DialogEqualizerFragment fragment = DialogEqualizerFragment.newBuilder()
                        .setAudioSessionId(sessionId)
                        .themeColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorPrimary))
                        .textColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.grey_1000))
                        .accentAlpha(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorAccentLight))
                        .darkColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorPrimaryDark))
                        .setAccentColor(ContextCompat.getColor(PlayerMusicActivity.this, R.color.colorAccent))
                        .build();
                fragment.show(getSupportFragmentManager(), "eq");

                // Code to be executed when the interstitial ad is closed.
            }
        });


    }

}

