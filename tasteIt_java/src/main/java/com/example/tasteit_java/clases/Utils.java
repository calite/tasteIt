package com.example.tasteit_java.clases;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final int SELECT_PICTURE = 101;
    private static final int TAKE_PICTURE = 202;

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

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    //validate Email
    public static boolean isEmail(String email) {

        Pattern pat = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$");
        Matcher mat = pat.matcher(email);

        return mat.matches();
    }
    //selector de fotos
    public static void selectImageFromMedia(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(activity, Intent.createChooser(intent, "Select a Picture"), SELECT_PICTURE, null);
    }

    public static void takePicture(Activity activity) {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(activity, camera_intent, TAKE_PICTURE, null);
    }

    public static void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data, Uri filePath, ImageView ivRecipePhoto) {
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        //creamos imagen
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getApplicationContext().getContentResolver(), data.getData());
                        //almacenamos el path
                        filePath = data.getData();
                        //cambiamos imagen del perfil
                        ivRecipePhoto.setImageBitmap(bitmap);
                        //subimos la imagen a firebase
                        //uploadImage(); DESCOMENTAR!
                        //el upload se debe hacer en el save
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(activity.getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //fin selector de fotos

    //buscador de tags
    public static ArrayList<String> searchTags(ArrayList<String> listaPalabras, ArrayList<String> listaDiccionario) {

        ArrayList<String> tagsEncontrados = new ArrayList<>();

        for (String palabra : listaPalabras) {
            if (listaDiccionario.contains(palabra) && !tagsEncontrados.contains(palabra)) {
                tagsEncontrados.add(palabra);
            }
        }

        return tagsEncontrados;
    }

    public static String getUserToken() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return firebaseUser.getUid();
    }

}
