/*
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

package com.sekhontech.singering.admobadapter;

import android.content.Context;
import android.util.Log;
import android.widget.AbsListView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.sekhontech.singering.admobadapter.expressads.ExpressAdPreset;
import com.sekhontech.singering.admobadapter.expressads.NativeExpressAdViewHolder;

public class AdViewHelper {

    public static NativeExpressAdViewHolder getExpressAdViewEx(Context context, ExpressAdPreset expressAdPreset) {
        NativeExpressAdView adView = null;
        try {
            adView = new NativeExpressAdView(context);
            AdSize adSize = expressAdPreset.getAdSize();
            adView.setAdSize(adSize);
            adView.setAdUnitId(expressAdPreset.getAdUnitId());
            adView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                    adSize.getHeightInPixels(context)));
        } catch (NullPointerException e) {
            Log.e("Nullpoint", "" + e);
        }
        return new NativeExpressAdViewHolder(adView);
    }
}
