package br.com.presba.livros_ti.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import br.com.presba.livros_ti.R;

public class SearchAdapter extends BaseAdapter {
	private static final String TAG = "Presba";
	private LayoutInflater inflater;
	public JSONArray items;
	private ViewHolder holder = null;
	private Context context;
	private LruCache<String, Bitmap> mMemoryCache = null;

	public SearchAdapter(JSONArray items, Context context) {

		this.context = context;
		this.items = items;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 8;

		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return bitmap.getByteCount() / 1024;
			}
		};

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
			return Long.valueOf(getItem(position).getLong("ID"));
		} catch (JSONException e) {
			return -1;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		holder = null;

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
			default:
				break;
			}
		}

		try {

			holder.retrieveImageTask = new RetrieveImageTask(getItem(position).getLong("ID"),
					holder);
			holder.coverImageView.setVisibility(View.GONE);
			holder.coverProgressBar.setVisibility(View.VISIBLE);
			holder.coverTextView.setText(getItem(position).getString("Title"));

			loadImage(getItem(position).getString("Image"), holder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private void loadImage(String url, ViewHolder holder) {
		// Bitmap cachedBmp = getBitmapFromMemCache(url);
		Bitmap cachedBmp = getBitmapFromSD(holder.retrieveImageTask.bookId);
		if (cachedBmp != null) {
			holder.coverImageView.setImageBitmap(cachedBmp);

			holder.coverImageView.setVisibility(View.VISIBLE);
			holder.coverProgressBar.setVisibility(View.GONE);
		} else {
			holder.retrieveImageTask.execute(url);
			Log.i(TAG, String.format("Loading image: %s", url));
		}
	}

	private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (getBitmapFromMemCache(key) == null) {
			mMemoryCache.put(key, bitmap);
		}
	}

	private void saveBitmapToSD(Long bookId, Bitmap bitmap) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/livros_ti/covers");
		myDir.mkdirs();

		String fname = bookId + ".jpg";
		File file = new File(myDir, fname);
		if (file.exists())
			file.delete();

		try {
			FileOutputStream out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Bitmap getBitmapFromMemCache(String key) {
		return mMemoryCache.get(key);
	}

	private Bitmap getBitmapFromSD(Long bookId) {
		String root = Environment.getExternalStorageDirectory().toString();
		File myDir = new File(root + "/livros_ti/covers");
		myDir.mkdirs();

		String fname = bookId + ".jpg";
		File file = new File(myDir, fname);
		if (file.exists()) {

			Log.i(TAG, "Image already exists on cache. BookId: " + bookId);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

			return bitmap;
		}

		return null;
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

				saveBitmapToSD(bookId, bmp);

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
