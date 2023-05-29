package com.startag.martguy.ads;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.startag.martguy.R;

import java.util.Arrays;


public class Ads {
    AdView mAdView;
    Activity context;
    InterstitialAd mInterstitialAd;
    KProgressHUD hud;
    boolean displayed=false;
    static  int COUNTADS = -1;
    static  int LIMITADS = 3;
    public interface MyCustomObjectListener {
         void onAdsfinish();
    }

    private MyCustomObjectListener listener;

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.listener = listener;
    }

    public Ads(Activity activity) {
        this.context = activity;
            hud = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Loading")
                    .setCancellable(true)
                    .setDetailsLabel("Please Wait")
                    .setMaxProgress(100);



    }



    public  void showinteradmob(String inter) {
        COUNTADS++;
        Log.e("COUNTADS", COUNTADS+"" );
        if (COUNTADS %LIMITADS==0) {
            hud.show();
            AdRequest adRequest = new AdRequest.Builder().build();
            InterstitialAd.load(context,inter, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAd = interstitialAd;
                            mInterstitialAd.show(context);

                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                                @Override
                                public void onAdClicked() {
                                    // Called when a click is recorded for an ad.
                                    Log.d(TAG, "Ad was clicked.");
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when ad is dismissed.
                                    // Set the ad reference to null so you don't show the ad a second time.
                                    Log.d(TAG, "Ad dismissed fullscreen content.");
                                    mInterstitialAd = null;
                                    hud.dismiss();
                                    listener.onAdsfinish();
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when ad fails to show.
                                    Log.e(TAG, "Ad failed to show fullscreen content.");
                                }

                                @Override
                                public void onAdImpression() {
                                    // Called when an impression is recorded for an ad.
                                    Log.d(TAG, "Ad recorded an impression.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    displayed=true;
                                    hud.dismiss();

                                    // Called when ad is shown.
                                    Log.d(TAG, "Ad showed fullscreen content.");
                                }
                            });



                            Log.i(TAG, "onAdLoaded");

                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.d(TAG, loadAdError.toString());
                            mInterstitialAd = null;


                        }
                    });




        }
        else {

            listener.onAdsfinish();
        }



    }

    public  void showBannerAds(LinearLayout mAdViewLayout, Display display) {
        mAdViewLayout.removeAllViews();
        mAdView  = new AdView(context);
        AdSize adSize = getAdSize(context,display);
        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(context.getString(R.string.admobbanner));
        AdRequest.Builder builder = new AdRequest.Builder();
        mAdView.loadAd(builder.build());
//        mAdViewLayout.addView(mAdView);
            mAdViewLayout.addView(mAdView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {

                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
    }


    private AdSize getAdSize(Context context, Display display) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;
        int adWidth = (int) (widthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }


}