package com.example.tasteit

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasteit.LoginActivity.Companion.providerSession
import com.example.tasteit.LoginActivity.Companion.useremail
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    //for picasso gallery - temporal
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageGalleryAdapter: ImageGalleryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        picasso gallery
         */

        val layoutManager = GridLayoutManager(this, 2)
        recyclerView = findViewById(R.id.rv_images)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager
        imageGalleryAdapter = ImageGalleryAdapter(this, SunsetPhoto.getSunsetPhotos())

        /*
        END
         */

        Toast.makeText(this, "Hola $useremail", Toast.LENGTH_SHORT).show()

        var create = findViewById<Button>(R.id.bCreate)
        create.setOnClickListener{
            Toast.makeText(this,"create pulsado", Toast.LENGTH_SHORT).show()
        }

        var home = findViewById<Button>(R.id.bHome)
        home.setOnClickListener{
            Toast.makeText(this,"home pulsado", Toast.LENGTH_SHORT).show()
        }

        var search = findViewById<Button>(R.id.bSearch)
        search.setOnClickListener{
            Toast.makeText(this,"search pulsado", Toast.LENGTH_SHORT).show()
        }

        var book = findViewById<Button>(R.id.bMyBook)
        book.setOnClickListener{
            Toast.makeText(this,"book pulsado", Toast.LENGTH_SHORT).show()
        }

        var profile = findViewById<Button>(R.id.bProfile)
        //creamos un evento con un intent para redirigir
        profile.setOnClickListener{
            val i = Intent(this, ProfileActivity::class.java )
            startActivity(i)
        }
    }

    fun callSignOut(view: View){
        signOut()
    }
    private fun signOut(){
        useremail = ""

        if (providerSession == "Facebook") LoginManager.getInstance().logOut()

        FirebaseAuth.getInstance().signOut()
        startActivity (Intent(this, LoginActivity::class.java))
    }

    /*
    Picasso gallery
    */
    override fun onStart() {
        super.onStart()
        recyclerView.adapter = imageGalleryAdapter
    }

    private inner class ImageGalleryAdapter(val context: Context, val sunsetPhotos: Array<SunsetPhoto>)
        : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryAdapter.MyViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val photoView = inflater.inflate(R.layout.item_image, parent, false)
            return MyViewHolder(photoView)
        }

        override fun onBindViewHolder(holder: ImageGalleryAdapter.MyViewHolder, position: Int) {
            val sunsetPhoto = sunsetPhotos[position]
            val imageView = holder.photoImageView

            Picasso.get()
                .load(sunsetPhoto.url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .tag(context)
                .into(imageView)

        }

        override fun getItemCount(): Int {
            return sunsetPhotos.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            var photoImageView: ImageView = itemView.findViewById(R.id.iv_photo)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val sunsetPhoto = sunsetPhotos[position]
                    val intent = Intent(context, SunsetPhotoActivity::class.java).apply {
                        putExtra(SunsetPhotoActivity.EXTRA_SUNSET_PHOTO, sunsetPhoto)
                    }
                    startActivity(intent)
                }
            }
        }
    }
    /*
    END
    */

}


