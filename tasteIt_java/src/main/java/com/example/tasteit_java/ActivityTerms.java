package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.tasteit_java.R;

public class ActivityTerms extends AppCompatActivity {
    TextView tvterms1;
    TextView tvterms2;
    TextView tvterms3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        tvterms1 = findViewById(R.id.tvterms1);
        tvterms2 = findViewById(R.id.tvterms2);
        tvterms3 = findViewById(R.id.tvterms3);
        tvterms1.setText(R.string.terms1);
        tvterms2.setText(R.string.terms2);
        tvterms3.setText(R.string.terms3);
    }
}