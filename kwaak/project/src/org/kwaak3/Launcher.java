package org.kwaak3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class Launcher extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button button = (Button)findViewById(R.id.btnStartGame);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				CheckBox chk = (CheckBox)findViewById(R.id.chkEnableSound);
				intent.setClassName("org.kwaak3", "org.kwaak3.Game");
				intent.putExtra("sound", chk.isChecked());
				startActivity(intent);
			}});
		
		button = (Button)findViewById(R.id.btnVisitWebsite);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://code.google.com/p/kwaak3/"));
				startActivity(intent);
			}});
	}
}
