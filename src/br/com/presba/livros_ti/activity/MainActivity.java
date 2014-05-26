package br.com.presba.livros_ti.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.adapter.SearchAdapter;
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
				EditText searchText = (EditText) MainActivity.this
						.findViewById(R.id.searchText);

				String url = String.format(
						"http://it-ebooks-api.info/v1/search/%s", searchText
								.getText().toString());

				new RetrieveSearchTask().execute(url);
			}
		});
	}

	class RetrieveSearchTask extends AsyncTask<String, Void, JSONArray> {

		protected JSONArray doInBackground(String... urls) {
			try {
				JSONObject result = new JSONObject(JSONManager.execute(urls[0]));
				JSONArray jsonArray = result.getJSONArray("Books");

				return jsonArray;
			} catch (Exception e) {
				return null;
			}
		}

		protected void onPostExecute(JSONArray jsonArray) {
			GridView resultTextView = (GridView) MainActivity.this
					.findViewById(R.id.resultGridView);

			if (jsonArray != null) {
				resultTextView.setAdapter(new SearchAdapter(jsonArray,
						MainActivity.this));
			} else {
				Toast.makeText(MainActivity.this, "Nada encontrado",
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
