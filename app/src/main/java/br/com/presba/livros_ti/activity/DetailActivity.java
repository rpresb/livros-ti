package br.com.presba.livros_ti.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.base.ActivityBase;
import br.com.presba.livros_ti.base.JSONManager;
import br.com.presba.livros_ti.model.Book;
import br.com.presba.livros_ti.util.CacheManager;

public class DetailActivity extends ActivityBase {

    private Book book;
    private TextView authorTextView;
    private TextView publisherTextView;
    private TextView yearTextView;
    private TextView pageTextView;
    private TextView descriptionTextView;
    private Button downloadButton;
    private Button openButton;
    private ProgressDialog mProgressDialog;
    private ImageView logoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Bundle bundle = getIntent().getExtras();

        book = new Book(bundle.getLong("BookID"),
                bundle.getString("Title"),
                bundle.getString("SubTitle"),
                bundle.getString("Description"),
                bundle.getString("Image"),
                bundle.getString("ISBN"));

        TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
        titleTextView.setText(book.getTitle());

        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        descriptionTextView.setText(book.getDescription());

        TextView isbnTextView = (TextView) findViewById(R.id.isbnTextView);
        isbnTextView.setText(book.getIsbn());

        authorTextView = (TextView) findViewById(R.id.authorTextView);
        authorTextView.setText(getResources().getText(R.string.Loading));

        publisherTextView = (TextView) findViewById(R.id.publisherTextView);
        publisherTextView.setText(getResources().getText(R.string.Loading));

        yearTextView = (TextView) findViewById(R.id.yearTextView);
        yearTextView.setText(getResources().getText(R.string.Loading));

        pageTextView = (TextView) findViewById(R.id.pageTextView);
        pageTextView.setText(getResources().getText(R.string.Loading));

        downloadButton = (Button) findViewById(R.id.downloadButton);
        downloadButton.setVisibility(View.GONE);

        openButton = (Button) findViewById(R.id.openButton);
        openButton.setVisibility(View.GONE);

        logoImageView = (ImageView) findViewById(R.id.logoImageView);

        downloadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.startDownload();
            }
        });

        openButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.openBook();
            }
        });

        String url = String.format(Locale.getDefault(),
                "http://it-ebooks-api.info/v1/book/%d", book.getBookID());
        new RetrieveBookTask().execute(url);

    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    private File getBookFile() {
        String root = Environment.getExternalStorageDirectory().toString();
        return new File(root + "/livros_ti/books/" + book.getBookID() + ".pdf");
    }

    private void openBook() {
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.setDataAndType(Uri.fromFile(getBookFile()), "application/pdf");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    private void startDownload() {
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // execute this when the downloader must be fired
        final DownloadTask downloadTask = new DownloadTask(this, getBook().getBookID());
        downloadTask.execute(getBook().getDownload());

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
    }

    private void showDetail() {
        descriptionTextView.setText(getBook().getDescription());
        authorTextView.setText(getBook().getAuthor());
        yearTextView.setText(getBook().getYear());
        pageTextView.setText(getBook().getPage());
        publisherTextView.setText(getBook().getPublisher());

        openButton.setVisibility(View.GONE);
        downloadButton.setVisibility(View.GONE);

        if (getBookFile().exists()) {
            openButton.setVisibility(View.VISIBLE);
        } else {
            downloadButton.setVisibility(View.VISIBLE);
        }

        Bitmap cachedBmp = new CacheManager().getBitmapFromSD(getBook().getBookID());
        logoImageView.setImageBitmap(cachedBmp);
    }

    class RetrieveBookTask extends AsyncTask<String, Void, Book> {

        protected Book doInBackground(String... urls) {
            try {
                Log.i("Presba", String.format("book detail url: %s", urls[0]));

                JSONObject result = new JSONObject(JSONManager.execute(urls[0]));

                return new Book(result);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Book book) {

            if (book != null) {
                DetailActivity.this.setBook(book);
                DetailActivity.this.showDetail();
            } else {
                Toast.makeText(DetailActivity.this, "Falha ao obter o detalhe do livro", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        public long bookId;
        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context, long bookId) {
            this.context = context;
            this.bookId = bookId;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Referer", String.format("http://it-ebooks.info/book/%d", this.bookId));
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                DetailActivity.this.mProgressDialog.setMax(fileLength / 1024);

                // download the file
                input = connection.getInputStream();

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/livros_ti/books");
                myDir.mkdirs();

                output = new FileOutputStream(myDir.getAbsoluteFile() + "/" + this.bookId + ".pdf");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) total / 1024);
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            DetailActivity.this.mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            DetailActivity.this.mProgressDialog.setIndeterminate(false);
            DetailActivity.this.mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            DetailActivity.this.mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                DetailActivity.this.showDetail();
            }
        }
    }
}
