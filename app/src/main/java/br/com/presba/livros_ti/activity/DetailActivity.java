package br.com.presba.livros_ti.activity;

import android.os.Bundle;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.base.ActivityBase;

public class DetailActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
    }
}
