package com.example.tasteit

import android.graphics.Color
import android.os.Bundle
import androidx.palette.graphics.Palette
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso


class SunsetPhotoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_SUNSET_PHOTO = "SunsetPhotoActivity.EXTRA_SUNSET_PHOTO"
    }

    private lateinit var imageView: ImageView
    private lateinit var sunsetPhoto: SunsetPhoto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sunset_photo)

        sunsetPhoto = intent.getParcelableExtra(EXTRA_SUNSET_PHOTO)!!
        imageView = findViewById(R.id.image)
    }

    override fun onStart() {
        super.onStart()

        Picasso.get()
            .load(sunsetPhoto.url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .fit()
            .priority(Picasso.Priority.HIGH)
            .into(imageView)

        Picasso.get()
            .load(sunsetPhoto.url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .fit()
            .priority(Picasso.Priority.LOW)
            .into(imageView)

        Picasso.get()
            .load(sunsetPhoto.url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .fit()
            .priority(Picasso.Priority.NORMAL)
            .into(imageView)
    }

    fun onPalette(palette: Palette?) {
        if (null != palette) {
            val parent = imageView.parent.parent as ViewGroup
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY))
        }
    }
}
