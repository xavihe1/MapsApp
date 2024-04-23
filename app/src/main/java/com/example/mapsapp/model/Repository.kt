package com.example.mapsapp.model

import com.example.mapsapp.viewModel.MapsViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Repository {
    private val myViewModel = MapsViewModel()
    private val database = FirebaseFirestore.getInstance()

    //Operació INSERT
    fun addUser(user: User) {
        database.collection("users")
            .add(
                hashMapOf(
                    "userName" to user.userName,
                    "age" to user.age,
                    "profilePicture" to user.profilePicture
                )
            )
    }

    //Operació UPDATE
    fun editUser(editedUser: User) {
        database.collection("users").document(editedUser.userId!!).set(
            hashMapOf(
                "userName" to editedUser.userName,
                "age" to editedUser.age,
                "profilePicture" to editedUser.profilePicture
            )
        )
    }

    //Operació DELETE
    fun deleteUser(userId: String) {
        database.collection("users").document(userId).delete()
    }


    //Operació SELECT
    fun getUsers(): CollectionReference {
        return database.collection("users")
    }

    //Operació SELECT (un element)
    fun getUser(userId: String): DocumentReference {
        return database.collection("users").document(userId)
    }

    fun getMarkers(): CollectionReference {
        return database.collection("markers")
    }
}