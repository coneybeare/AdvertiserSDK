package com.vungle.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.vungle.sdk.download.attribution.Vungle;

public class Demo extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.trackAppInstall:
			Vungle.init(this);
			break;
		case R.id.button1:
			EditText appIdEditText = (EditText) v.findViewById(R.id.editText1);
			Vungle.event(this, appIdEditText.getText().toString());
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
