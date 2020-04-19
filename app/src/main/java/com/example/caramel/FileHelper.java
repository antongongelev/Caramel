package com.example.caramel;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHelper {

    public static final String FILENAME = "listinfo.dat";

    public static void writeData(ArrayList<String> items, Context context) throws IOException {

        FileOutputStream fileOutputStream = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(items);
        objectOutputStream.close();
    }

    public static ArrayList<String> readData(Context context) {

        ArrayList<String> itemsList = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(FILENAME);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            itemsList = (ArrayList<String>) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            itemsList = new ArrayList<>();
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return itemsList;
    }

}