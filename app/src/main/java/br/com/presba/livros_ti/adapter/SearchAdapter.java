package br.com.presba.livros_ti.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.util.CacheManager;

public class SearchAdapter extends BaseAdapter {
    private static final String TAG = "Presba";
    public JSONArray items;
    private LayoutInflater inflater;
    private Context context;
    private CacheManager cacheManager = new CacheManager();

    public SearchAdapter(JSONArray items, Context context) {

        this.context = context;
        this.items = items;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return this.items.length();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            return this.items.getJSONObject(position);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        try {
            return getItem(position).getLong("ID");
        } catch (JSONException e) {
            return -1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = this.inflater.inflate(R.layout.include_search, null);

            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.coverImageView = (ImageView) convertView.findViewById(R.id.coverImageView);
            holder.coverTextView = (TextView) convertView.findViewById(R.id.coverTextView);
            holder.coverProgressBar = (ProgressBar) convertView.findViewById(R.id.coverProgressBar);
        } else {
            holder = (ViewHolder) convertView.getTag();

            switch (holder.retrieveImageTask.getStatus()) {
                case PENDING:
                case RUNNING:
                    holder.retrieveImageTask.cancel(true);
                    break;
                case FINISHED:
                    break;
            }
        }

        try {

            if (!getItem(position).has("ID")) {
                convertView.findViewById(R.id.item_search).setVisibility(View.GONE);
                convertView.findViewById(R.id.item_more).setVisibility(View.VISIBLE);

                convertView.findViewById(R.id.moreTextView).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.moreProgressBar).setVisibility(View.GONE);
            } else {
                convertView.findViewById(R.id.item_search).setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.item_more).setVisibility(View.GONE);

                holder.retrieveImageTask = new RetrieveImageTask(getItem(position).getLong("ID"),
                        holder);
                holder.coverImageView.setVisibility(View.GONE);
                holder.coverProgressBar.setVisibility(View.VISIBLE);
                holder.coverTextView.setText(getItem(position).getString("Title"));

                loadImage(getItem(position).getString("Image"), holder);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private void loadImage(String url, ViewHolder holder) {
        // Bitmap cachedBmp = getBitmapFromMemCache(url);
        Bitmap cachedBmp = cacheManager.getBitmapFromSD(holder.retrieveImageTask.bookId);
        if (cachedBmp != null) {
            holder.coverImageView.setImageBitmap(cachedBmp);

            holder.coverImageView.setVisibility(View.VISIBLE);
            holder.coverProgressBar.setVisibility(View.GONE);
        } else {
            holder.retrieveImageTask.execute(url);
            Log.i(TAG, String.format("Loading image: %s", url));
        }
    }


    static class ViewHolder {

        ImageView coverImageView;
        TextView coverTextView;
        ProgressBar coverProgressBar;
        RetrieveImageTask retrieveImageTask;
    }

    class RetrieveImageTask extends AsyncTask<String, Void, Bitmap> {

        private ViewHolder holder;
        private Long bookId;

        public RetrieveImageTask(Long bookId, ViewHolder holder) {
            this.holder = holder;
            this.bookId = bookId;
        }

        protected Bitmap doInBackground(String... urls) {
            try {
                URL uri = new URL(urls[0]);
                Bitmap bmp = BitmapFactory.decodeStream(uri.openConnection().getInputStream());

                // addBitmapToMemoryCache(urls[0], bmp);

                if (this.isCancelled()) {
                    return null;
                }

                cacheManager.saveBitmapToSD(bookId, bmp);

                return bmp;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i(TAG, "Image download cancelled for bookId: " + bookId);
        }

        protected void onPostExecute(Bitmap bmp) {
            if (bmp != null) {
                holder.coverImageView.setImageBitmap(bmp);
            } else {
                holder.coverImageView.setImageDrawable(context.getResources().getDrawable(
                        R.drawable.ic_launcher));
            }

            holder.coverImageView.setVisibility(View.VISIBLE);
            holder.coverProgressBar.setVisibility(View.GONE);
        }
    }

}
