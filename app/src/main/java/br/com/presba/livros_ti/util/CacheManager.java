package br.com.presba.livros_ti.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CacheManager {

    private final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    private LruCache<String, Bitmap> mMemoryCache = null;
    private File myDir;

    public CacheManager() {
        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/livros_ti/covers");
        myDir.mkdirs();


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

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public void saveBitmapToSD(Long bookId, Bitmap bitmap) {
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

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public Bitmap getBitmapFromSD(Long bookId) {
        String fname = bookId + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            return bitmap;
        }

        return null;
    }

    public void cachePrepare() {
        List<String> fileToDelete = new ArrayList<String>();

        for (File file : myDir.listFiles()) {
            Date lastModified = new Date(file.lastModified());
            long millis = new Date().getTime() - lastModified.getTime();

            if (millis > DAY_IN_MILLIS) {
                fileToDelete.add(file.getAbsolutePath());
            }
        }

        for (String filePath : fileToDelete) {
            Log.d("Presba", "Deleting cache file: " + filePath);
            new File(filePath).delete();
        }
    }

}
