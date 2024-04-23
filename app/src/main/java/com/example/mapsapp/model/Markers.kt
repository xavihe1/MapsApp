package com.example.mapsapp.model

import android.graphics.Bitmap

data class Markers(
    var owner:String?,
    var markerId:String?,
    var latitude:Double,
    var longitude:Double,
    var title:String,
    var snippet:String,
    var photo: Bitmap?,
    var photoReference:String?
){
    constructor():this(null,null,0.0,0.0,"","",null,null)

    fun modificarTitle(newTitle: String) {
        title = newTitle
    }

    fun modificarSnippet(newSnippet: String) {
        snippet = newSnippet
    }

    fun modificarPhoto(newPhoto: Bitmap) {
        photo = newPhoto
    }

    fun modificarPhotoReference(newReference: String) {
        photoReference = newReference
    }
}
