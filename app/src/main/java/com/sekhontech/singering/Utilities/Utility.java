package com.sekhontech.singering.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Utility {
	
	
	 public static void showAlert(final Activity activity, String title, String message, String firstButtonName, String seccondButtonName){
		 AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//		builder.setTitle(title);
		if (message != null) {
			builder.setMessage(message);
		}
		if (firstButtonName != null) {
			builder.setPositiveButton(firstButtonName,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							activity.finish();
						}
					});
		}

		builder.setCancelable(false)
				.setNegativeButton(seccondButtonName,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						}).show();

	}

}
