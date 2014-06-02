package br.com.presba.livros_ti.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Locale;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.base.ActivityBase;
import br.com.presba.livros_ti.base.JSONManager;
import br.com.presba.livros_ti.model.Book;

public class DetailActivity extends ActivityBase {

    private Book book;
    private TextView authorTextView;
    private TextView publisherTextView;
    private TextView yearTextView;
    private TextView pageTextView;
    private TextView descriptionTextView;

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

                DetailActivity.this.descriptionTextView.setText(book.getDescription());
                DetailActivity.this.authorTextView.setText(book.getAuthor());
                DetailActivity.this.yearTextView.setText(book.getYear());
                DetailActivity.this.pageTextView.setText(book.getPage());
                DetailActivity.this.publisherTextView.setText(book.getPublisher());
            } else {
                Toast.makeText(DetailActivity.this, "Falha ao obter o detalhe do livro", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
