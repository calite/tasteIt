package com.example.tasteit_java.clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //base64
    public static String encodeTobase64(Bitmap image){
        System.gc();  //For memory efficiency
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = null;
        imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    //validate Email
    public static boolean isEmail(String email) {

        Pattern pat = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$");
        Matcher mat = pat.matcher(email);

        return mat.matches();
    }

}
