package br.com.presba.livros_ti.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.base.ActivityBase;

public class Splash extends ActivityBase implements Runnable {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		initialize();
	}

	private void initialize() {
		Handler handler = new Handler();

		handler.postDelayed(Splash.this, 5000);
	}

	@Override
	public void run() {

		startActivity(new Intent(Splash.this, MainActivity.class));
		finish();
	}
}
