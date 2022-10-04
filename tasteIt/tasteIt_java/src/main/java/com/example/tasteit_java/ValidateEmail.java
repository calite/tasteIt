package com.example.tasteit_java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateEmail {


        public static boolean isEmail (String email){

        Pattern pat = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$");
        Matcher mat = pat.matcher(email);

        return mat.matches();
    }

}
