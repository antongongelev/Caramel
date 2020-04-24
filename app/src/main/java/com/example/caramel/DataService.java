package com.example.caramel;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataService {

    //Parse arrayList to json
    public static void savePositions(SharedPreferences.Editor editor, ArrayList<Position> positions) {
        Gson gson = new Gson();
        String positionsGson = gson.toJson(positions);
        //losing imageName
        editor.putString("positions", positionsGson);
    }

    //Parse json to arrayList
    public static ArrayList<Position> loadPositions(SharedPreferences sharedPreferences) {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("positions", null);
        Type type = new TypeToken<ArrayList<Position>>() {
        }.getType();
        Object positions = gson.fromJson(json, type);
        return positions == null ? new ArrayList<Position>() : (ArrayList<Position>) positions;
    }

    //Save photo
    public static String saveToInternalStorage(Bitmap bitmapImage, Context context) {
        File directory = getDirectory(context);///data/user/0/com.example.caramel/app_imageDir
        String fileName = System.currentTimeMillis() + ".jpg";
        File filePath = new File(directory, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //Load photo
    public static Bitmap loadImageFromStorage(String fileName, Context context) {
        if (fileName != null) {
            try {
                String path = getDirectory(context).getAbsolutePath();
                File file = new File(path, fileName);
                return BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static File getDirectory(Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        return cw.getDir("imageDir", Context.MODE_PRIVATE);
    }
}
