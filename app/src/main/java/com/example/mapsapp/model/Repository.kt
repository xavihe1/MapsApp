package com.example.mapsapp.model

import com.example.mapsapp.viewModel.MapsViewModel
import com.google.firebase.firestore.CollectionReference

class Repository {
    private val myViewModel = MapsViewModel()
    fun getUsers(): CollectionReference {
        return database.collection("users")
    }
}