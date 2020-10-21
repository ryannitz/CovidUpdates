package com.ryannitz.covidupdates.utility;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {

    public static String USER_SETTINGS = "user.json";
    public static String NB_JSON_FILENAME = "nb_json.json";

    public static boolean provinceHasJsonFile(Context ctx, String fileName){
        return new File(ctx.getFilesDir(), fileName).exists();
    }

    public static File getProvinceJsonFile(Context ctx, String fileName){
        return new File(ctx.getFilesDir(), fileName);
    }

    public static String getFileContents(Context ctx, String fileName){
        File file = new File(ctx.getFilesDir(), fileName);
        return readFile(file.getAbsolutePath(), StandardCharsets.US_ASCII);
    }

    public static String getAssetContents(Context ctx, String fileName){
        String json = null;
        try{
            InputStream is = ctx.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
        return json;
    }


    private static String readFile(String path, Charset encoding){
        try{
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }catch(IOException e){
            e.printStackTrace();
        }
        return "";
    }

    public static boolean createJsonFile(Context ctx, String fileName, String fileContents){
        File file = new File(ctx.getFilesDir(), fileName);
        boolean didWrite = false;
        try {
            didWrite = file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(ctx.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(fileContents);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return didWrite;
    }

    public static boolean doesFileExist(Context ctx, String fileName){
        File file = new File(ctx.getFilesDir(), fileName);
        return file.exists();
    }

}
