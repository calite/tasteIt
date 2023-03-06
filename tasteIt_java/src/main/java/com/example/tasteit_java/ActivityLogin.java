package com.example.tasteit_java;

import android.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.example.tasteit_java.clases.ValidateEmail;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ActivityLogin extends AppCompatActivity {

    private String email;
    private String password;
    private String confirmPassword;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button bShowPass;
    private Button bShowPass2;
    private ConstraintLayout lyTerms;

    private FirebaseAuth mAuth;

    //google sign in
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        /*
        GoogleSignInOptions gso =  new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this,null)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        */

        //onclick eyes
        bShowPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
        bShowPass2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etConfirmPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        manageButtonLogin();

        //listener para comprobar pass y email
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

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) goHome();

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
        if (password.length() < 6 || !ValidateEmail.isEmail(email)) {
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
                        if (task.isSuccessful()) {
                            waitingForLogin();
                            goHome();
                        } else {
                            if (lyTerms.getVisibility() == View.INVISIBLE && etConfirmPassword.getVisibility() == View.INVISIBLE) {
                                lyTerms.setVisibility(View.VISIBLE);
                                etConfirmPassword.setVisibility(View.VISIBLE);
                                bShowPass2.setVisibility(View.VISIBLE);
                                bLogin.setText("register now!");
                            } else {
                                CheckBox cbAcept = findViewById(R.id.cbAcept);
                                if (cbAcept.isChecked()) {
                                    if (confirmPassword()) {
                                        bLogin.setBackgroundColor(ContextCompat.getColor(ActivityLogin.this, R.color.green));
                                        register();
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

    private void goHome() {

        Intent i = new Intent(this, ActivityMain.class);
        /*
        //NEO4J
        String uri = "neo4j+s://dc95b24b.databases.neo4j.io"; //URL conexion Neo4j
        String user = "neo4j";
        String pass = "sBQ6Fj2oXaFltjizpmTDhyEO9GDiqGM1rG-zelf17kg"; //PDTE CIFRAR

        try (BdConnection app = new BdConnection(uri, user, pass)) { //Instanciamos la conexion
            User usuario = app.login(email, password); //Intentamos el login
            i.putExtra("user", usuario);
        }
        //NEO4J
        */
        startActivity(i);
    }

    private void waitingForLogin() {

        View loading = View.inflate(ActivityLogin.this, R.layout.item_waiting_for_login, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);
        builder.setView(loading);
        builder.create().show();

    }

    private void register() {
        email = etEmail.getText().toString();
        password = etPassword.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    /*
                    //temporal - se usara SQLite
                    //NO FURULA
                    SimpleDateFormat dateRegister = new SimpleDateFormat("dd/MM/yyyy");
                    FirebaseFirestore dbRegister = FirebaseFirestore.getInstance();
                    String username = email.substring(0, email.indexOf("@"));
                    int photo = R.drawable.ic_default_profile;

                    HashMap<String, Object> user = new HashMap<>();
                    user.put("user", email);
                    user.put("dateRegister", dateRegister);
                    user.put("username", username);
                    user.put("photo", photo);

                    dbRegister.collection("users").document(email).set(user);
                    */
                    waitingForLogin();
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

    /*
    //POR REPARAR
    public void callSingInGoogle(View view) {
        signInGoogle();
    }

    private void signInGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.signOut();
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            goHome();
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }

    private void showError(String provider) {
        Toast.makeText(this, "Error with the connection to $provider", Toast.LENGTH_SHORT).show();
    }
    */
}