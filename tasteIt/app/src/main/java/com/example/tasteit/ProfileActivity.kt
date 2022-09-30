package com.example.tasteit

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.view.*

class ProfileActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)



    }



    /*
    TEMPORAL
    */
    fun callSignOut(view: View){
        signOut()
    }
    private fun signOut(){
        LoginActivity.useremail = ""

        FirebaseAuth.getInstance().signOut()
        startActivity (Intent(this, LoginActivity::class.java))
    }

    fun callNonClicable(view: View) {
        nonClicable()
    }

    private fun nonClicable() {
        Toast.makeText(this, "that cant be changed!",Toast.LENGTH_SHORT).show()
    }
}