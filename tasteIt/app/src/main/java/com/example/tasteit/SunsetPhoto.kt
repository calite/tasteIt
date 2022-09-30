package com.example.tasteit

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SunsetPhoto(val url: String) : Parcelable {

    companion object {
        fun getSunsetPhotos(): Array<SunsetPhoto> {
            return arrayOf(
                SunsetPhoto("https://goo.gl/32YN2B"),
                SunsetPhoto("https://goo.gl/Wqz4Ev"),
                SunsetPhoto("https://goo.gl/U7XXdF"),
                SunsetPhoto("https://goo.gl/ghVPFq"),
                SunsetPhoto("https://goo.gl/qEaCWe"),
                SunsetPhoto("https://goo.gl/vutGmM"),
                SunsetPhoto("https://goo.gl/32YN2B"),
                SunsetPhoto("https://goo.gl/Wqz4Ev"),
                SunsetPhoto("https://goo.gl/U7XXdF"),
                SunsetPhoto("https://goo.gl/ghVPFq"),
                SunsetPhoto("https://goo.gl/qEaCWe"),
                SunsetPhoto("https://goo.gl/vutGmM"),
                SunsetPhoto("https://goo.gl/32YN2B"),
                SunsetPhoto("https://goo.gl/Wqz4Ev"),
                SunsetPhoto("https://goo.gl/U7XXdF"),
                SunsetPhoto("https://goo.gl/ghVPFq"),
                SunsetPhoto("https://goo.gl/qEaCWe"),
                SunsetPhoto("https://goo.gl/vutGmM"),
                SunsetPhoto("https://goo.gl/32YN2B"),
                SunsetPhoto("https://goo.gl/Wqz4Ev"),
                SunsetPhoto("https://goo.gl/U7XXdF"),
                SunsetPhoto("https://goo.gl/ghVPFq"),
                SunsetPhoto("https://goo.gl/qEaCWe"),
                SunsetPhoto("https://goo.gl/vutGmM")
            )
        }
    }
}