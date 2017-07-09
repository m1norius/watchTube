package com.minorius.watchertube.dbtube;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by minorius on 06.07.2017.
 */

public class MyIMG {

    private static final String replaceablePreString = "https://i.ytimg.com/vi/";
    private static final String replaceablePostString = "/mqdefault";

    public String saveToInternalStorage(Bitmap bitmapImage, Context context, String nameOfFile){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, nameOfFile);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static String getParseNameFromUrl(String url){
        return url.replaceAll(replaceablePreString, "").replaceAll(replaceablePostString, "");
    }

    public boolean isImageLoaded(Context context, String nameOfFile){
        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        return new File(directory, nameOfFile).exists();
    }

}
