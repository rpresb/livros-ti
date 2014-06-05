package br.com.presba.livros_ti.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.presba.livros_ti.R;

public class DownloadedAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public DownloadedAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.inflater.inflate(R.layout.include_search, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView coverImageView = (ImageView) view.findViewById(R.id.coverImageView);
        TextView coverTextView = (TextView) view.findViewById(R.id.coverTextView);

        view.findViewById(R.id.coverProgressBar).setVisibility(View.GONE);
        view.findViewById(R.id.item_search).setVisibility(View.VISIBLE);
        view.findViewById(R.id.item_more).setVisibility(View.GONE);

        coverTextView.setText(cursor.getString(cursor.getColumnIndex("title")));
        coverImageView.setImageBitmap(byteArrayToBitmap(cursor.getBlob(cursor.getColumnIndex("image"))));

        view.setTag(cursor.getLong(cursor.getColumnIndex("bookid")));
    }

    private Bitmap byteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

}
