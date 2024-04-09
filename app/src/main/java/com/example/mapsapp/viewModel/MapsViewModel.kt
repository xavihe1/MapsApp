package com.example.mapsapp.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.model.Repository
import com.example.mapsapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MapsViewModel: ViewModel() {
    private val repository = Repository()

    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied



    //FIREBASE CLOUD FIRESTORE
    private val _userList = MutableLiveData<List<User>>()
    val userList = _userList

    private val _actualUser = MutableLiveData<String>()
    val actualUser = _actualUser

    private val _userName = MutableLiveData<String>()
    val userName = _userName

    private val _age = MutableLiveData<Int>()
    val age = _age


    //AUTHENTICATION
    private var _goToNext = MutableLiveData(false)
    val goToNext = _goToNext

    private val _userId = MutableLiveData<Int>()
    val userId = _userId

    private val _loggedUser = MutableLiveData(false)
    val loggedUser = _loggedUser

    private val _showProcessingBar = MutableLiveData<Boolean>(false)
    val showProgressBar = _showProcessingBar

    fun modifyProcessing(show: Boolean) {
        showProgressBar.value = show
    }


    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }

    private val database = FirebaseFirestore.getInstance()

    fun getDatabase(): FirebaseFirestore {
        return database
    }

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

    fun getUsers() {
        repository.getUsers().addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firestore error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<User>()

            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newUser = dc.document.toObject(User::class.java)
                    newUser.userId = dc.document.id
                    tempList.add(newUser)
                }
            }
            _userList.value = tempList
        }
    }

    //Operació SELECT
    fun getUser(userId: String) {
        repository.getUser(userId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w("UserRepository", "Listen failed.", error)
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val user = value.toObject(User::class.java)
                if (user != null) {
                    user.userId = userId
                }
                _actualUser.value = user
                _userName.value = _actualUser.value!!.userName
                _age.value = _actualUser.value!!.age.toString()
            } else {
                Log.d("UserRepository", "Current data: null")
            }
        }
    }


    private val auth = FirebaseAuth.getInstance()

    fun register(username: String, password: String) {
        auth.createUserWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user: ${task.result}")
                }
                modifyProcessing(false)
            }
    }

    fun login(username: String?, password: String?) {
        auth.signInWithEmailAndPassword(username!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in ¡: ${task.result}")
                }
                modifyProcessing(false)
            }
    }

    fun logout() {
        auth.signOut()
    }

    fun selectFunctionsFirestore() {
        repository.getUsers()
            .whereGreaterThan("", "")
            .whereEqualTo("", "")
            .whereGreaterThanOrEqualTo("", "")
            .whereLessThan("", "")
            .whereLessThanOrEqualTo("", "")
            .whereNotEqualTo("", "")
    }
}