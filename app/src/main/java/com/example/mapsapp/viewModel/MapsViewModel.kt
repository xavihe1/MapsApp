package com.example.mapsapp.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.model.Repository
import com.example.mapsapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private val _userId = MutableLiveData<String>()
    val userId = _userId

    private val _loggedUser = MutableLiveData<String>()
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
                Log.w("UserRepository", "Listen Failed.", error)
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



    var uri: Uri? = null
    fun uploadImage(imageUri: Uri) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("IMAGE UPLOAD", "Image upload successfully")
                storage.downloadUrl.addOnSuccessListener {
                    Log.i("IMAGEN", it.toString())
                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image upload failed")
            }
    }
}