package com.example.mapsapp.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.mapsapp.model.Markers
import com.example.mapsapp.model.Repository
import com.example.mapsapp.model.User
import com.example.mapsapp.model.UserPrefs
import com.example.mapsapp.navigation.Routes
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsViewModel: ViewModel() {
    private val repository = Repository()


    //MAP
    private val _bottomSheetVisible = MutableLiveData<Boolean>()
    val bottomSheetVisible: LiveData<Boolean> = _bottomSheetVisible

    fun setBottomSheetVisible(visible: Boolean) {
        _bottomSheetVisible.value = visible
    }

    fun addMarker(name: String, description: String, position: LatLng) {
        val marker = MarkerOptions().position(position).title(name).snippet(description)
        googleMap?.addMarker(marker)
    }

    private val _showBottomSheet = MutableLiveData<Boolean>()
    val showBottomSheet = _showBottomSheet

    private val _markers = MutableLiveData<MutableList<Markers>>()
    val markers: LiveData<MutableList<Markers>> = _markers

    private val _dropDownText = MutableLiveData<String>()
    val dropDownText: LiveData<String> = _dropDownText

    private val _loadingMarkers = MutableLiveData(true)
    val loadingMarkers = _loadingMarkers

    private var _editingPosition = MutableLiveData<LatLng>()
    val editingPosition = _editingPosition

    fun modificarEditingPosition(newValue: LatLng){
        _editingPosition.value = newValue
    }

    private var expandedMap by mutableStateOf(false)

    fun modifyExpandedMap(newValue: Boolean) {
        expandedMap = newValue
    }

    fun getExpandedMap(): Boolean {
        return expandedMap
    }

    fun getMarkers() {

    }

    private var position = LatLng(41.4534265, 2.1837151)
    fun changePosition(newPosition: LatLng) {
        position = newPosition
    }

    fun getAllMarkers() {
        //modifyLoadingMarkers(false)
        repository.getMarkers()
            .whereEqualTo("owner", _loggedUser.value)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<Markers>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(Markers::class.java)
                        newMarker.markerId = dc.document.id
                        newMarker.latitude =
                            dc.document.get("positionLatitude").toString().toDouble()
                        newMarker.longitude =
                            dc.document.get("positionLongitude").toString().toDouble()
                        newMarker.photoReference = dc.document.get("linkImage").toString()
                        tempList.add(newMarker)
                        Log.d("Success",("Adios :( $newMarker"))
                    }

                }
                _markers.value = tempList
            }
    }

    fun modifyDropDownText(newText: String) {
        _dropDownText.value = newText
    }
    fun modifyShowBottomSheet(newBoolean: Boolean) {
        _showBottomSheet.value = newBoolean
    }




    //CAMERA
    private val _cameraPermissionGranted = MutableLiveData(false)
    val cameraPermissionGranted = _cameraPermissionGranted

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied


    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }

    private fun takePhoto(context: Context,
                          controller: LifecycleCameraController, onPhotoTaken: (Bitmap) -> Unit) {
        controller.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onPhotoTaken(image.toBitmap())
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("Camera", "Error taken photo", exception)
                }
            }
        )
    }



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


    //Operació INSERT
    fun addUser(user: User) {
        repository.addUser(user)
    }


    //Operació UPDATE
    fun editUser(editedUser: User) {
        repository.editUser(editedUser)
    }


    //Operació DELETE
    fun deleteUser(userId: String) {
        repository.deleteUser(userId)
    }




    //AUTHENTICATION
    private val auth = FirebaseAuth.getInstance()

    private val _stayLogged = MutableLiveData<Boolean>()
    val stayLogged = _stayLogged

    private val _emailState = MutableLiveData<String>()
    val emailState: LiveData<String> = _emailState

    private val _passwordState = MutableLiveData<String>()
    val passwordState: LiveData<String> = _passwordState

    fun modificarEmailState(value: String) {
        _emailState.value = value
    }

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

    fun logout(context: Context, navController: NavController) {
        val userPrefs = UserPrefs(context)
        if (_stayLogged.value == true){
            CoroutineScope(Dispatchers.IO).launch {
                println("JEJE ESTOY EN TRUE")
                userPrefs.deleteUserPass()
            }
        } else {
            modificarEmailState("")
            CoroutineScope(Dispatchers.IO).launch {
                userPrefs.deleteUserData()
            }
        }
        auth.signOut()
        _goToNext.value = false
        _passwordState.value = ""

        modifyProcessing(true)
        navController.navigate(Routes.Pantalla2.route)
    }


    fun userLogged(): Boolean {
        return auth.currentUser != null
    }




    //FILTRAR DADES FIRESTORE
    fun selectFunctionsFirestore() {
        repository.getUsers()
            .whereGreaterThan("", "")
            .whereEqualTo("", "")
            .whereGreaterThanOrEqualTo("", "")
            .whereLessThan("", "")
            .whereLessThanOrEqualTo("", "")
            .whereNotEqualTo("", "")
    }




    //FIREBASE STORAGE
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