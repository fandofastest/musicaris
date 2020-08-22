package com.startag.martguy.ads;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.startag.martguy.R;

import static android.content.ContentValues.TAG;

public class Interstitial {
    com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    KProgressHUD hud;


    public  void showinter(final Context context, String inter, final Intent intent) {
        hud = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(context.getString(R.string.loadingtitle))
                .setDetailsLabel(context.getString(R.string.loadingdesc))
                .setMaxProgress(100);
        hud.show();


        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(context);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mInterstitialAd.show();
                hud.dismiss();
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                context.startActivity(intent);
                hud.dismiss();

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
                context.startActivity(intent);
                hud.dismiss();
                // Code to be executed when the interstitial ad is closed.
            }
        });


    }


}