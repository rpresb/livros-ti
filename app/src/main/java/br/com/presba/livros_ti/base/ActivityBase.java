package br.com.presba.livros_ti.base;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.presba.livros_ti.R;
import br.com.presba.livros_ti.activity.DownloadedActivity;
import br.com.presba.livros_ti.activity.MainActivity;

public class ActivityBase extends Activity {

    protected int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenWidth = metrics.widthPixels;

        prepareTopBar();
    }

    private void prepareTopBar() {
        View topbar = (View) findViewById(R.id.topbar);

        if (topbar != null) {
            ImageButton menuSearch = (ImageButton) findViewById(R.id.menuSearch);
            ImageButton menuDownloaded = (ImageButton) findViewById(R.id.menuDownloaded);
            TextView topBarTitle = (TextView) findViewById(R.id.topBarTitle);

            if (this instanceof DownloadedActivity) {
                menuSearch.setVisibility(View.VISIBLE);
                topBarTitle.setText("Livros Gravados");
            } else if (this instanceof MainActivity) {
                menuDownloaded.setVisibility(View.VISIBLE);
                topBarTitle.setText("Buscar");
            }
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
