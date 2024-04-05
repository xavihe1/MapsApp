package com.example.mapsapp.model

import com.example.mapsapp.viewModel.MapsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

class Repository {
    private val myViewModel = MapsViewModel()

    //Operació SELECT
    fun getUsers(): CollectionReference {
        return database.collection("users")
    }

    //Operació SELECT (un element)
    fun getUser(userId: String): DocumentReference {
        return database.collection("users").document(userId)
    }
}