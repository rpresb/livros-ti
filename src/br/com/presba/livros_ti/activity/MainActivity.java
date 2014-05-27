package br.com.presba.livros_ti.activity;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.adapter.SearchAdapter;
import br.com.presba.livros_ti.base.ActivityBase;
import br.com.presba.livros_ti.base.JSONManager;

public class MainActivity extends ActivityBase {

	private Integer count = 0;
	private Integer firstItem = 1;
	private Integer pageSize = 10;
	private GridView resultGridView;
	private SearchAdapter lastSearchAdapter;
	private RetrieveSearchTask searchTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button searchButton = (Button) this.findViewById(R.id.searchButton);
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchTask != null) {
					searchTask.cancel(true);
				}

				MainActivity.this.firstItem = 1;
				MainActivity.this.doSearch();
			}
		});

		this.resultGridView = (GridView) MainActivity.this.findViewById(R.id.resultGridView);

		this.resultGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Log.i("Presba", String.format("%d", scrollState));

				if (scrollState == 0) {
					if (searchTask == null || !searchTask.isLoading) {
						MainActivity.this.firstItem += pageSize;

						if (firstItem < count) {
							MainActivity.this.doSearch();
							Log.i("Presba", String.format("Carrregando a partir de %d",
									MainActivity.this.firstItem));
						} else {
							Log.i("Presba", "Chegou a fim");
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
				Log.i("Presba", String.format("Primeiro: %d, Qtd Visivel: %d, Qtd Total: %d",
						firstVisibleItem, visibleItemCount, totalItemCount));

			}
		});
	}

	private void doSearch() {

		EditText searchText = (EditText) findViewById(R.id.searchText);

		String url = String.format(Locale.getDefault(),
				"http://it-ebooks-api.info/v1/search/%s/page/%d", searchText.getText().toString(),
				firstItem);

		searchTask = new RetrieveSearchTask();

		if (firstItem > 1) {
			searchTask.isIncremental = true;
		}
		searchTask.execute(url);
	}

	class RetrieveSearchTask extends AsyncTask<String, Void, JSONArray> {

		public boolean isIncremental = false;
		public boolean isLoading = false;

		protected JSONArray doInBackground(String... urls) {
			try {
				this.isLoading = true;

				Log.i("Presba", String.format("Url: %s", urls[0]));

				JSONObject result = new JSONObject(JSONManager.execute(urls[0]));

				MainActivity.this.count = Integer.parseInt(result.getString("Total"));

				JSONArray jsonArray = result.getJSONArray("Books");

				return jsonArray;
			} catch (Exception e) {
				return null;
			}
		}

		protected void onPostExecute(JSONArray jsonArray) {

			if (jsonArray != null) {

				if (isIncremental) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = null;
						try {
							obj = jsonArray.getJSONObject(i);
						} catch (JSONException e) {

						}
						if (obj != null) {
							lastSearchAdapter.items.put(obj);
						}
					}

					lastSearchAdapter.notifyDataSetChanged();

				} else {

					lastSearchAdapter = new SearchAdapter(jsonArray, MainActivity.this);
					MainActivity.this.resultGridView.setAdapter(lastSearchAdapter);
				}
			} else {
				Toast.makeText(MainActivity.this, "Nada encontrado", Toast.LENGTH_SHORT).show();
			}

			this.isLoading = false;
		}
	}
}
