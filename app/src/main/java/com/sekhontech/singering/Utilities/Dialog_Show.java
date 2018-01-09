package com.sekhontech.singering.Utilities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.sekhontech.singering.R;


/**
 * Created by ST_004 on 19-04-2016.
 */
public class Dialog_Show extends android.app.Dialog implements View.OnClickListener
{
    public Activity c;
    public Button no;

    public Dialog_Show(Activity a)
    {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_popup);

        no = (Button) findViewById(R.id.btn_no);

        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
                dismiss();
    }
}
