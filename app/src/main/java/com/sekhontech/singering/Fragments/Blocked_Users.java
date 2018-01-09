package com.sekhontech.singering.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sekhontech.singering.R;

/**
 * Created by ST_004 on 27-04-2016.
 */
public class Blocked_Users extends Fragment
{
    public Blocked_Users(){}

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_blocked_users, container, false);
    }
}
