package com.example.tasteit


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    companion object{
        lateinit var useremail: String
        lateinit var providerSession: String
    }

    private var email by Delegates.notNull<String>()
    private var password by Delegates.notNull<String>()
    private var confirmPassword by Delegates.notNull<String>()
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var lyTerms: LinearLayout

    private lateinit var mAuth: FirebaseAuth

    private var RESULT_CODE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //OCULTAMOS TERMINOS Y CONFIRMACION PASS
        lyTerms = findViewById(R.id.lyTerms)
        lyTerms.visibility = View.INVISIBLE
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etConfirmPassword.visibility = View.INVISIBLE

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        mAuth = FirebaseAuth.getInstance()

        manageButtonLogin()
        etEmail.doOnTextChanged { text, start, before, count ->  manageButtonLogin() }
        etPassword.doOnTextChanged { text, start, before, count ->  manageButtonLogin() }

    }

    public override fun onStart() {
        super.onStart()

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null)  goHome(currentUser.email.toString(), currentUser.providerId)

    }


    override fun onBackPressed() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }


    private fun manageButtonLogin(){
        var bLogin = findViewById<TextView>(R.id.bLogin)
        email = etEmail.text.toString()
        password = etPassword.text.toString()
        confirmPassword = etConfirmPassword.text.toString()

        if (TextUtils.isEmpty(password) || !ValidateEmail.isEmail(email)){
            bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.gray))
            bLogin.isEnabled = false
        }
        else{
            bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            bLogin.isEnabled = true
        }
    }

    fun login(view: View) {
        loginUser()
    }
    private fun loginUser(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()
        var bLogin = findViewById<TextView>(R.id.bLogin)

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){ task ->
                if (task.isSuccessful)  goHome(email, "email")
                else{
                    if (lyTerms.visibility == View.INVISIBLE && etConfirmPassword.visibility == View.INVISIBLE) {
                        lyTerms.visibility = View.VISIBLE
                        etConfirmPassword.visibility = View.VISIBLE
                        bLogin.text = "register now!"
                    }
                    else{
                        var cbAcept = findViewById<CheckBox>(R.id.cbAcept)
                        if (cbAcept.isChecked) {
                            if(confirmPassword()) {
                                bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                                register()
                            }
                            else {
                                bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                                Toast.makeText(baseContext, "passwords are not equals.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else {
                            bLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.orange))
                            Toast.makeText(baseContext, "CHECK the terms policy.", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }

    }

    private fun goHome(email: String, provider: String){
        useremail = email
        providerSession = provider

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun register(){
        email = etEmail.text.toString()
        password = etPassword.text.toString()

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){

                    var dateRegister = SimpleDateFormat("dd/MM/yyyy").format(Date())
                    var dbRegister = FirebaseFirestore.getInstance()
                    var username = email.subSequence(0,email.indexOf("@"))
                    var photo = "ic_default_profile"

                    dbRegister.collection("users").document(email).set(hashMapOf(
                        "user" to email,
                        "dateRegister" to dateRegister,
                        "username" to username,
                        "photo" to photo
                    ))

                    goHome(email, "email")
                }
                else Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
    }

    fun goTerms(v: View){
        val intent = Intent(this, TermsActivity::class.java)
        startActivity(intent)
    }

    fun forgotPassword(view: View) {
        resetPassword()
    }
    private fun resetPassword(){
        var e = etEmail.text.toString()
        if (!TextUtils.isEmpty(e)){
            mAuth.sendPasswordResetEmail(e)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) Toast.makeText(this, "Email sent to $e", Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, "no user was found with that email", Toast.LENGTH_SHORT).show()
                }
        }
        else Toast.makeText(this, "please, put your email", Toast.LENGTH_SHORT).show()
    }

    private fun confirmPassword() : Boolean {
        password = etPassword.text.toString()
        confirmPassword = etConfirmPassword.text.toString()

        return password == confirmPassword
    }

    fun callSingInGoogle(view: View) {
        signInGoogle()
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        var googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        startActivityForResult(googleSignInClient.signInIntent, RESULT_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RESULT_CODE_SIGN_IN) {

            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                val account = task.getResult(ApiException::class.java)!!

                if (account != null){
                    email = account.email!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    mAuth.signInWithCredential(credential).addOnCompleteListener{
                        if (it.isSuccessful) goHome(email, "Google")
                        else showError("Google")

                    }
                }


            } catch (e: ApiException) {
                showError("Google")
            }
        }

    }

    /*
    DOESNT WORK YET!
    */

    fun callSingInFacebook(view: View) {
        signInFacebook()
    }

    private fun signInFacebook(){
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                result.let{
                    val token = it.accessToken
                    val credential = FacebookAuthProvider.getCredential(token.token)
                    mAuth.signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful){
                            email = it.result.user?.email.toString()
                            goHome(email, "Facebook")
                        }
                        else showError("Facebook")
                    }
                }
            }

            override fun onCancel() { }
            override fun onError(error: FacebookException) { showError("Facebook") }
        })

    }

    fun showError(provider: String) {
        Toast.makeText(this, "Error with the connection to $provider", Toast.LENGTH_SHORT).show()
    }

}