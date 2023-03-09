package com.example.tasteit_java;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivityLogin extends AppCompatActivity {

    private String email;
    private String password;
    private String confirmPassword;
    private String userName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private ConstraintLayout lyTerms;
    private Button bShowPass;
    private Button bShowPass2;

    private FirebaseAuth mAuth;

    private BdConnection app;

    Logger log = Logger.getLogger(ActivityMain.class.getName());

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        app = new BdConnection();

        getSupportActionBar().hide();

        //OCULTAMOS TERMINOS Y CONFIRMACION PASS
        lyTerms = findViewById(R.id.lyTerms);
        lyTerms.setVisibility(View.INVISIBLE);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etConfirmPassword.setVisibility(View.INVISIBLE);
        bShowPass = findViewById(R.id.bShowPass);
        bShowPass2 = findViewById(R.id.bShowPass2);
        bShowPass2.setVisibility(View.INVISIBLE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        mAuth = FirebaseAuth.getInstance();

        manageButtonLogin();

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                manageButtonLogin();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                manageButtonLogin();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        bShowPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etConfirmPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null){

            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        log.log(Level.INFO,currentUser.getUid()+" "+currentUser.getEmail());
                        goHome();

                    }else {
                        log.log(Level.INFO,"Logged user doesn't exist anymore");
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void manageButtonLogin() {
        TextView bLogin = findViewById(R.id.bLogin);
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();
        //HAY QUE COMPROBAR LA CLASE VALIDATE EMAIL CON SU METODO
        if (password.length() < 6 || !Utils.isEmail(email)) {
            bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray));
            bLogin.setEnabled(false);
        } else {
            bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.green));
            bLogin.setEnabled(true);
        }
    }

    public void login(View view) {
        loginUser();
    }

    private void loginUser() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();
        TextView bLogin = findViewById(R.id.bLogin);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){waitingForConection(); goHome();}
                        else {
                            if (lyTerms.getVisibility() == View.INVISIBLE && etConfirmPassword.getVisibility() == View.INVISIBLE) {
                                lyTerms.setVisibility(View.VISIBLE);
                                etConfirmPassword.setVisibility(View.VISIBLE);
                                bShowPass2.setVisibility(View.VISIBLE);
                                bLogin.setText("Register now!");
                            } else {
                                CheckBox cbAcept = findViewById(R.id.cbAcept);
                                if (cbAcept.isChecked()) {
                                    if (confirmPassword()) {
                                        bLogin.setBackgroundColor(ContextCompat.getColor(ActivityLogin.this, R.color.green));
                                        //
                                        //Alert para el username
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
                                        builder.setTitle("Choose your username");
                                        final EditText etUsername = new EditText(ActivityLogin.this);
                                        etUsername.setInputType(InputType.TYPE_CLASS_TEXT);
                                        builder.setView(etUsername);
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                userName = etUsername.getText().toString();
                                                if(!userName.equals("")){
                                                    dialog.dismiss();
                                                    waitingForConection();
                                                    register(); }else{
                                                    Toast.makeText(ActivityLogin.this, "You must choose a valid username", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        builder.show();
                                        //
                                    } else {
                                        bLogin.setBackgroundColor(ContextCompat.getColor(ActivityLogin.this, R.color.orange));
                                        Toast.makeText(ActivityLogin.this, "passwords are not equals.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    bLogin.setBackgroundColor(ContextCompat.getColor(ActivityLogin.this, R.color.orange));
                                    Toast.makeText(ActivityLogin.this, "CHECK the terms policy.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });

    }

    private void waitingForConection(){

        View view = View.inflate(ActivityLogin.this, R.layout.item_waiting_for_login, null);

        //creamos el alert dialog
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
        builder.setView(view);

        //por ultimos creamos y mostramos el dialogo
        builder.create().show();



    }

    private void goHome() {
        Intent i = new Intent(this, ActivityMain.class);
        //NEO4J
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();
        User usuario = app.login(uid); //Intentamos el login
        i.putExtra("user", usuario);
        log.log(Level.INFO,uid+" "+firebaseUser.getEmail());
        //FIN NEO
        startActivity(i);
    }

    private void register() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //NEO4J
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = firebaseUser.getUid();
                    app.register(userName, uid);
                    //FIN NEO
                    //pantalla de carga
                    waitingForConection();
                    goHome();
                } else
                    Toast.makeText(ActivityLogin.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void goTerms(View view) {

        Intent i = new Intent(this, ActivityTerms.class);
        startActivity(i);

    }

    public void forgotPassword(View view) {
        resetPassword();
    }

    private void resetPassword() {
        String e = etEmail.getText().toString();
        if (!TextUtils.isEmpty(e)) {
            mAuth.sendPasswordResetEmail(e).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        Toast.makeText(ActivityLogin.this, "Email sent to $e", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ActivityLogin.this, "no user was found with that email", Toast.LENGTH_SHORT).show();
                }
            });
        } else Toast.makeText(this, "please, put your email", Toast.LENGTH_SHORT).show();
    }

    private boolean confirmPassword() {

        password = etPassword.getText().toString();
        confirmPassword = etConfirmPassword.getText().toString();

        return password.equals(confirmPassword);
    }


    //POR REPARAR
    /*
    public void callSingInGoogle(View view) {
        signInGoogle();
    }

    private void signInGoogle() {
        GoogleSignInOptions.Builder gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignIn googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();

        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_SIGN_IN);
    }

    private void onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_SIGN_IN) {

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                val account = task.getResult(ApiException:: class.java)!!

                if (account != null) {
                    email = account.email !!
                            val credential = GoogleAuthProvider.getCredential(account.idToken, null);
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) goHome();
                        else showError("Google")

                    }
                }


            } catch (e:ApiException){
                showError("Google")
            }
        }

    }

    private void showError(String provider) {
        Toast.makeText(this, "Error with the connection to $provider", Toast.LENGTH_SHORT).show();
    }
    */

}
