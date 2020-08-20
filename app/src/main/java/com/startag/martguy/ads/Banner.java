package com.startag.martguy.ads;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.LinearLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ixidev.gdpr.GDPRChecker;

public class Banner  {

    com.facebook.ads.AdView adViewfb;
    AdView mAdView;

    public  void ShowBannerAds(Context context, LinearLayout mAdViewLayout,String bannerfb,String banneradmob,Display display) {



        adViewfb = new com.facebook.ads.AdView(context, bannerfb, com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        adViewfb.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {


            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        adViewfb.loadAd();


        mAdView  = new AdView(context);
        AdSize adSize = getAdSize(context,display);

        mAdView.setAdSize(adSize);
        mAdView.setAdUnitId(banneradmob);
        AdRequest.Builder builder = new AdRequest.Builder();
        GDPRChecker.Request request = GDPRChecker.getRequest();
        if (request == GDPRChecker.Request.NON_PERSONALIZED) {
            // load non Personalized ads
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        } // else do nothing , it will load PERSONALIZED ads




        mAdView.loadAd(builder.build());
//        mAdViewLayout.addView(mAdView);
        System.out.println(bannerfb);
        if (bannerfb.equals("")){
            mAdViewLayout.addView(mAdView);

        }
        else {
            mAdViewLayout.addView(adViewfb);
        }






    }


    private AdSize getAdSize(Context context,Display display) {
        // Step 2 - Determine the screen width (less decorations) to use for the ad width.

        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
