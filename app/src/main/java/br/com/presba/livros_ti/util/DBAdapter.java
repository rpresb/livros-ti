package br.com.presba.livros_ti.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import br.com.presba.livros_ti.model.Book;

public class DBAdapter {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns = new String[]{"bookid", "title", "subtitle", "description",
            "isbn", "author", "year", "page", "publisher", "image"};

    public DBAdapter(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (database.isOpen()) {
            database.close();
        }
    }

    public Book saveBook(Book book) {
        Bitmap imageBmp = new CacheManager().getBitmapFromSD(book.getBookID());

        ContentValues values = new ContentValues();
        values.put("bookid", book.getBookID());
        values.put("title", book.getTitle());
        values.put("subtitle", book.getSubTitle());
        values.put("description", book.getDescription());
        values.put("isbn", book.getIsbn());
        values.put("author", book.getAuthor());
        values.put("year", book.getYear());
        values.put("page", book.getPage());
        values.put("publisher", book.getPublisher());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.PNG, 100, baos);

        byte[] photo = baos.toByteArray();
        values.put("image", photo);

        long insertId = database.insert("book", null, values);
        Cursor cursor = database.query("book", allColumns, "_id = " + insertId, null, null, null, null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursorToBook(cursor);
        }

        return null;
    }

    public Cursor getBooks() {
        return database.rawQuery("select * from book", null);
    }

    public Book getBook(Long bookId) {
        Cursor cursor = database.query("book", allColumns, "bookid = " + bookId, null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursorToBook(cursor);
        }

        return null;
    }

    private Book cursorToBook(Cursor cursor) {
        byte[] blob = cursor.getBlob(cursor.getColumnIndex("image"));
        Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
        Book book = new Book(cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), null, cursor.getString(4));
        book.setAuthor(cursor.getString(5));
        book.setYear(cursor.getString(6));
        book.setPage(cursor.getString(7));
        book.setPublisher(cursor.getString(8));
        book.setImageBitmap(bmp);
        return book;
    }
}
