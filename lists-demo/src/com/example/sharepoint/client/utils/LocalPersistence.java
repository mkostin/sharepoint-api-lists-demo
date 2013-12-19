package com.example.sharepoint.client.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.example.sharepoint.client.ListsDemoApplication;

/**
 * Serializes/deserializes an object to/from a private local file.
 */
public class LocalPersistence {

    /**
     * Serializes an object to a private local file.
     *
     * @param context Application context.
     * @param object Object to serialize.
     * @param filename Filename to save to object to.
     *
     * @throws IOException
     */
    public static void writeObjectToFile(Context context, Object object, String filename) throws IOException {

        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(object);
            fileOut.getFD().sync();

        } catch (IOException ex) {
            Utility.showAlertDialog(ex.toString(), ListsDemoApplication.getAppContext());
        } finally {
            if (objectOut != null) {
                objectOut.close();
            }
        }
    }

    /**
     * Deserializes an object from a private local file.
     *
     * @param context Application context.
     * @param filename Filename to load to object from.
     *
     * @return Deserialized object.
     *
     * @throws IOException
     */
    public static Object readObjectFromFile(Context context, String filename) throws IOException {
        ObjectInputStream objectIn = null;
        Object object = null;

        try {
            FileInputStream fileIn = context.getApplicationContext().openFileInput(filename);
            objectIn = new ObjectInputStream(fileIn);
            object = objectIn.readObject();
        } catch (Exception ex) {
        	Utility.showAlertDialog(ex.toString(), ListsDemoApplication.getAppContext());
        } finally {
            if (objectIn != null) {
                objectIn.close();
            }
        }

        return object;
    }
}
