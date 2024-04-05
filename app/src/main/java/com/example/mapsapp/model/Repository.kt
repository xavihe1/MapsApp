package com.example.mapsapp.model

import com.example.mapsapp.viewModel.MapsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

class Repository {
    private val myViewModel = MapsViewModel()

    //Operació SELECT
    fun getUsers(): CollectionReference {
        return myViewModel.getDatabase().collection("users")
    }

    //Operació SELECT (un element)
    fun getUser(userId: String): DocumentReference {
        return myViewModel.getDatabase().collection("users").document(userId)
    }
}