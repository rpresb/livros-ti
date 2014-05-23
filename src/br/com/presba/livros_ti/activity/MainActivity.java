package br.com.presba.livros_ti.activity;

import java.io.IOException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.base.ActivityBase;
import br.com.presba.livros_ti.base.JSONManager;

public class MainActivity extends ActivityBase {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button searchButton = (Button) this.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					EditText searchText = (EditText) MainActivity.this
							.findViewById(R.id.searchText);
					
					String url = String.format("http://it-ebooks-api.info/v1/search/%s",
							searchText.getText().toString());
					
					Log.i("Presba", url);

					String result = JSONManager.execute(url);

					TextView resultTextView = (TextView) MainActivity.this
							.findViewById(R.id.resultTextView);
					resultTextView.setText(result);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
