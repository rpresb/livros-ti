package br.com.presba.livros_ti.base;

import android.app.Activity;
import android.content.Intent;
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
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        prepareTopBar();
    }

    private void prepareTopBar() {
        View topbar = findViewById(R.id.topbar);

        if (topbar != null) {
            ImageButton menuSearch = (ImageButton) topbar.findViewById(R.id.menuSearch);
            ImageButton menuDownloaded = (ImageButton) topbar.findViewById(R.id.menuDownloaded);
            TextView topBarTitle = (TextView) topbar.findViewById(R.id.topBarTitle);

            final Activity currentActivity = this;
            if (this instanceof DownloadedActivity) {
                menuSearch.setVisibility(View.VISIBLE);
                topBarTitle.setText("Livros Gravados");

                menuSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(currentActivity, MainActivity.class);
                        startActivity(it);
                        finish();
                    }
                });

            } else if (this instanceof MainActivity) {
                menuDownloaded.setVisibility(View.VISIBLE);
                topBarTitle.setText("Buscar");

                menuDownloaded.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(currentActivity, DownloadedActivity.class);
                        startActivity(it);
                        finish();
                    }
                });
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
