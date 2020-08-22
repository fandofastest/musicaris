package com.startag.martguy.servicemusic;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.startag.martguy.model.MusicSongOffline;
import com.startag.martguy.model.MusicSongOnline;
import com.startag.martguy.utils.RealmHelper;
import com.startag.martguy.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class PlayerService extends Service {
    public static List<MusicSongOffline> listlocal = new ArrayList<>();
    public static List<MusicSongOnline> listtopsong = new ArrayList<>();
    public static List<MusicSongOnline> listrecent = new ArrayList<>();
    public static List<MusicSongOnline> listplaylist = new ArrayList<>();
    public static List<MusicSongOnline> currentlist = new ArrayList<>();
    public static List<MusicSongOffline> currentlistoffline = new ArrayList<>();
    public  static String PLAYERSTATUS="STOP",REPEAT="OFF",SHUFFLE="OFF",CURRENTTYPE="OFF";
    public static int totalduration,currentduraiton,currentpos;
    String from;
    public static String currenttitle,currentartist,currentimageurl;
    Realm realm;
    public  static int sessionId;

    //player
    private MediaPlayer mp = new MediaPlayer();

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getStringExtra("status");

                if (status.equals("pause")){
                    mp.pause();
                    PLAYERSTATUS="PAUSE";
                }
                else if (status.equals("resume")){
                    PLAYERSTATUS="PLAYING";
                    mp.start();

                }

                else  if (status.equals("seek")){
                    int seek = intent.getIntExtra("seektime",0);

                    mp.pause();
                    mp.seekTo(seek);
                    mp.start();

                }
                else if (status.equals("stopmusic")){
                    PLAYERSTATUS="STOPING";
                    mp.release();
                }
                else if (status.equals("getduration")){
                    totalduration=mp.getDuration();
                    currentduraiton=mp.getCurrentPosition();
                }
                else if (status.equals("next")){
                    if (CURRENTTYPE.equals("ON")){
                        playsong(currentpos+1);
                    }

                    else {
                        playsongoff(currentpos+1);
                    }

                }

                else if (status.equals("prev")){

                    if (CURRENTTYPE.equals("ON")){
                        playsong(currentpos-1);
                    }

                    else {
                        playsongoff(currentpos-1);
                    }

                }

                else if (status.equals("settimer")){
                    Long end= intent.getLongExtra("end",0);
//                    Toast.makeText(getApplicationContext(),"Timer set : "+end,Toast.LENGTH_LONG).show();

                    new CountDownTimer(end, 1000) {


                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {
                            PLAYERSTATUS="STOPING";
                            mp.release();


                        }
                    }.start();
                }



            }
        }, new IntentFilter("musicplayer"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initrealm();
        from=intent.getStringExtra("from");

        if (from.equals("online")){
            CURRENTTYPE="ON";
            playsong(intent.getIntExtra("pos",0));


        }
        else if (from.equals("offline")){
            playsongoff(intent.getIntExtra("pos",0));
            CURRENTTYPE="OFF";

        }






        return START_STICKY;
    }
    public void  initrealm(){
        Realm.init(PlayerService.this);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(configuration);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void playsong(int pos){
        currentpos=pos;
        try {

            final MusicSongOnline musicSongOnline =currentlist.get(pos);
            currentartist=musicSongOnline.getArtist();
            currenttitle=musicSongOnline.getTitle();
            currentimageurl=musicSongOnline.getImageurl();




            mp.stop();
            mp.reset();
            mp.release();



            Uri myUri = Uri.parse(Tools.SERVERMUSIC+musicSongOnline.getId());
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mp.prepareAsync(); //don't use prepareAsync for mp3 playback

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    return true;
                }
            });


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp1) {

                    if (REPEAT.equals("ON")){
                        playsong(currentpos);
                    }
                    else if (SHUFFLE.equals("ON")){

                        int pos= (int) (Math.random() * (listtopsong.size()));

                        playsong(pos);
                    }
                    else {

                        playsong(currentpos+1);
                    }






                }



            });



            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPrepared(MediaPlayer mplayer) {


                    RealmHelper realmHelper = new RealmHelper(realm,getApplication());
                    realmHelper.saverecent(musicSongOnline);
                    sessionId=mp.getAudioSessionId();

                    if (mplayer.isPlaying()) {
                        mp.pause();

                    } else {
                        mp.start();
                        PLAYERSTATUS="PLAYING";
                        Intent intent = new Intent("musicplayer");
                        intent.putExtra("status", "playing");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);



                    }

                }


            });





            mp.prepareAsync();


        }
        catch (Exception e){
            System.out.println(e);
        }

    }


    public void playsongoff(int pos){
        currentpos=pos;
        try {

            final MusicSongOffline musicSongOffline = currentlistoffline.get(pos);
            currentartist="Local";
            currenttitle=musicSongOffline.getFilename();
            currentimageurl="";




            mp.stop();
            mp.reset();
            mp.release();



            Uri myUri = Uri.parse(musicSongOffline.getFilepath());
            mp = new MediaPlayer();
            mp.setDataSource(this, myUri);
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mp.prepareAsync(); //don't use prepareAsync for mp3 playback

            mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {

                    return true;
                }
            });


            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp1) {

                    if (REPEAT.equals("ON")){
                        playsongoff(currentpos);
                    }
                    else if (SHUFFLE.equals("ON")){

                        int pos= (int) (Math.random() * (listlocal.size()));

                        playsongoff(pos);
                    }
                    else {

                        playsongoff(currentpos+1);
                    }






                }



            });



            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @SuppressLint("RestrictedApi")
                @Override
                public void onPrepared(MediaPlayer mplayer) {



                    sessionId=mp.getAudioSessionId();

                    if (mplayer.isPlaying()) {
                        mp.pause();

                    } else {
                        mp.start();
                        PLAYERSTATUS="PLAYING";
                        Intent intent = new Intent("musicplayer");
                        intent.putExtra("status", "playing");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);



                    }

                }


            });





            mp.prepareAsync();


        }
        catch (Exception e){
            System.out.println(e);
        }

    }


}
