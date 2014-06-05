package br.com.presba.livros_ti.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.adapter.DownloadedAdapter;
import br.com.presba.livros_ti.base.ActivityBase;
import br.com.presba.livros_ti.model.Book;
import br.com.presba.livros_ti.util.DBAdapter;

public class DownloadedActivity extends ActivityBase {

    private GridView resultGridView;
    private DBAdapter db = new DBAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);

        resultGridView = (GridView) DownloadedActivity.this.findViewById(R.id.resultGridView);

        loadDownloadedBooks();

        resultGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book;
                try {
                    db.open();
                    book = db.getBook((Long) view.getTag());
                } finally {
                    db.close();
                }

                if (book != null) {
                    Intent it = new Intent(DownloadedActivity.this, DetailActivity.class);

                    it.putExtra("BookID", book.getBookID());
                    it.putExtra("Title", book.getTitle());
                    it.putExtra("SubTitle", book.getSubTitle());
                    it.putExtra("Description", book.getDescription());
                    it.putExtra("Image", book.getImage());
                    it.putExtra("ISBN", book.getIsbn());

                    startActivity(it);
                }
            }
        });
    }

    private void loadDownloadedBooks() {

        try {
            db.open();
            DownloadedAdapter downloadedAdapter = new DownloadedAdapter(this, db.getBooks(), 0);
            resultGridView.setAdapter(downloadedAdapter);
        } finally {
            db.close();
        }

    }
}
