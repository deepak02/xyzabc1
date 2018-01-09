/*
 * Copyright 2015 Yahoo Inc. All rights reserved.
 * Copyright 2015 Clockbyte LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sekhontech.singering.admobadapter.expressads;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.ListView;

import com.google.android.gms.ads.AdListener;
import com.sekhontech.singering.admobadapter.AdPresetCyclingList;
import com.sekhontech.singering.admobadapter.AdmobFetcherBase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AdmobFetcherExpress extends AdmobFetcherBase {

    private final String TAG = com.sekhontech.singering.admobadapter.expressads.AdmobFetcherExpress.class.getCanonicalName();

    /**
     * Maximum number of ads to prefetch.
     */
    public static final int PREFETCHED_ADS_SIZE = 2;

    /**
     * Maximum number of times to try fetch an ad after failed attempts.
     */
    private static final int MAX_FETCH_ATTEMPT = 4;

    public AdmobFetcherExpress(Context context){
        super();
        mContext = new WeakReference<Context>(context);
    }

    private List<NativeExpressAdViewHolder> mPrefetchedAds = new ArrayList<NativeExpressAdViewHolder>();
    private AdPresetCyclingList mAdPresetCyclingList = new AdPresetCyclingList();

    /*
* Gets next ad preset for Admob banners from FIFO .
* It works like cycling FIFO (first in = first out, cycling from end to start).
* Each ad block will get one from the queue.
* If the desired count of ad blocks is greater than this collection size
* then it will go again to the first item and iterate as much as it required.
* ID should be active, please check it in your Admob's account.
 */
    public ExpressAdPreset takeNextAdPreset() {
        return this.mAdPresetCyclingList.get();
    }

    /*
   * Sets an unit IDs for admob banners.
* It works like cycling FIFO (first in = first out, cycling from end to start).
* Each ad block will get one from the queue.
* If the desired count of ad blocks is greater than this collection size
* then it will go again to the first item and iterate as much as it required.
* ID should be active, please check it in your Admob's account.
 */
    public void setAdPresets(Collection<ExpressAdPreset> adPresets) {
        Collection<ExpressAdPreset> mAdPresets = adPresets==null||adPresets.size() == 0
                ? Collections.singletonList(ExpressAdPreset.DEFAULT)
                :adPresets;
        mAdPresetCyclingList.clear();
        mAdPresetCyclingList.addAll(mAdPresets);
    }

    public int getAdPresetsCount(){
        return this.mAdPresetCyclingList.size();
    }

    /**
     * Gets native ad at a particular index in the fetched ads list.
     *
     * @param adPos the index of ad in the fetched ads list
     * @return the native ad in the list
     * @see #getFetchedAdsCount()
     */
    public synchronized NativeExpressAdViewHolder getAdForIndex(int adPos) {
        if(adPos >= 0 && mPrefetchedAds.size() > adPos)
            return mPrefetchedAds.get(adPos);
        return null;
    }

    /**
     * Fetches a new native ad.
     */
    protected synchronized void fetchAd(final NativeExpressAdViewHolder adViewEx) {
        if(mFetchFailCount > MAX_FETCH_ATTEMPT)
            return;

        Context context = mContext.get();

        if (context != null) {
            Log.i(TAG, "Fetching Ad now");
            new Handler(context.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    adViewEx.getAdView().loadAd(getAdRequest()); //Fetching the ads item
                }
            });
        } else {
            mFetchFailCount++;
            Log.i(TAG, "Context is null, not fetching Ad");
        }
    }

    /**
     * Subscribing to the native ads events
     * @param adViewEx
     */
    protected synchronized void setupAd(final NativeExpressAdViewHolder adViewEx) {
        if(mFetchFailCount > MAX_FETCH_ATTEMPT)
            return;

        if(!mPrefetchedAds.contains(adViewEx))
            mPrefetchedAds.add(adViewEx);
        adViewEx.getAdView().setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
                // Handle the failure by logging, altering the UI, etc.
                onFailedToLoad(adViewEx, errorCode);
            }
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                onFetched(adViewEx);
            }
        });
    }

    /**
     * A handler for received native ads
     * @param adViewEx
     */
    private synchronized void onFetched(NativeExpressAdViewHolder adViewEx) {
        Log.i(TAG, "onAdFetched");
        adViewEx.setFailedToLoad(false);
        mFetchFailCount = 0;
        mNoOfFetchedAds++;
        notifyObserversOfAdSizeChange(mNoOfFetchedAds - 1);
    }

    /**
     * A handler for failed native ads
     * @param adViewEx
     */
    private synchronized void onFailedToLoad(NativeExpressAdViewHolder adViewEx, int errorCode) {
        Log.i(TAG, "onAdFailedToLoad " + errorCode);
        mFetchFailCount++;
        mNoOfFetchedAds = Math.max(mNoOfFetchedAds - 1, -1);
        //Since Fetch Ad is only called once without retries
        //hide ad row / rollback its count if still not added to list
        adViewEx.setFailedToLoad(true);
        mPrefetchedAds.remove(adViewEx);
        notifyObserversOfAdSizeChange(mNoOfFetchedAds - 1);
        ViewParent parent = adViewEx.getAdView().getParent();
        //parent is not empty and not an instance of ListView/RecyclerView
        if(parent!=null
                && !(parent instanceof RecyclerView
                        || parent instanceof ListView))
            ((View) adViewEx.getAdView().getParent()).setVisibility(View.GONE);
        else adViewEx.getAdView().setVisibility(View.GONE);
    }

    @Override
    public synchronized int getFetchingAdsCount() {
        return mPrefetchedAds.size();
    }

    @Override
    public synchronized void destroyAllAds() {
        super.destroyAllAds();
        for(NativeExpressAdViewHolder ad:mPrefetchedAds){
            ad.getAdView().destroy();
        }
        mPrefetchedAds.clear();
    }

    @Override
    public void release() {
        super.release();
        mAdPresetCyclingList.clear();
    }
}
