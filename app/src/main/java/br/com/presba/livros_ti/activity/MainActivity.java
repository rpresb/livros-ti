package br.com.presba.livros_ti.activity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.adapter.SearchAdapter;
import br.com.presba.livros_ti.base.ActivityBase;
import br.com.presba.livros_ti.base.JSONManager;

public class MainActivity extends ActivityBase {

    private final Integer pageSize = 10;
    private Integer count = 0;
    private Integer firstItem = 1;
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
                MainActivity.this.findViewById(R.id.loading).setVisibility(View.VISIBLE);

                if (searchTask != null) {
                    searchTask.cancel(true);
                }

                MainActivity.this.firstItem = 1;
                MainActivity.this.doSearch();
            }
        });

        this.resultGridView = (GridView) MainActivity.this.findViewById(R.id.resultGridView);

        this.resultGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == lastSearchAdapter.getCount() - 1) {
                    if (!lastSearchAdapter.getItem(position).has("ID")) {
                        TextView moreTextView = (TextView) view.findViewById(R.id.moreTextView);
                        ProgressBar moreProgressBar = (ProgressBar) view.findViewById(R.id.moreProgressBar);

                        moreTextView.setVisibility(View.GONE);
                        moreProgressBar.setVisibility(View.VISIBLE);

                        searchNextPage();
                    }
                }
            }
        });
    }

    private void searchNextPage() {
        if (searchTask == null || !searchTask.isLoading) {
            MainActivity.this.firstItem += pageSize;

            if (firstItem < count) {
                MainActivity.this.doSearch();
                Log.i("Presba", String.format("Carrregando a partir de %d",
                        MainActivity.this.firstItem));
            } else {
                Log.i("Presba", "Chegou ao fim");
            }
        }

    }

    private void doSearch() {

        EditText searchText = (EditText) findViewById(R.id.searchText);

        String searchEncoded = Uri.encode(searchText.getText().toString());

        String url = String.format(Locale.getDefault(),
                "http://it-ebooks-api.info/v1/search/%s/page/%d", searchEncoded,
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

                return result.getJSONArray("Books");
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(JSONArray jsonArray) {

            if (jsonArray != null) {

                if (isIncremental) {

                    JSONArray newItems = new JSONArray();

                    // Add all except the last item to the new items
                    for (int i = 0; i < lastSearchAdapter.items.length(); i++) {
                        try {
                            if (lastSearchAdapter.items.getJSONObject(i).has("ID")) {
                                newItems.put(lastSearchAdapter.items.getJSONObject(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = null;
                        try {
                            obj = jsonArray.getJSONObject(i);
                        } catch (JSONException ignored) {

                        }
                        if (obj != null) {
                            newItems.put(obj);
                        }
                    }

                    addLoadMoreItem(newItems);

                    lastSearchAdapter.items = newItems;
                    lastSearchAdapter.notifyDataSetChanged();

                } else {

                    lastSearchAdapter = new SearchAdapter(jsonArray, MainActivity.this);

                    addLoadMoreItem(lastSearchAdapter.items);

                    MainActivity.this.resultGridView.setAdapter(lastSearchAdapter);
                }
            } else {
                Toast.makeText(MainActivity.this, "Nada encontrado", Toast.LENGTH_SHORT).show();
            }

            MainActivity.this.findViewById(R.id.loading).setVisibility(View.GONE);
            this.isLoading = false;
        }

        private void addLoadMoreItem(JSONArray items) {
            if (MainActivity.this.count > MainActivity.this.firstItem + MainActivity.this.pageSize) {
                // Add 1 empty object in the last for control the next page
                items.put(new JSONObject());
            }
        }
    }
}
