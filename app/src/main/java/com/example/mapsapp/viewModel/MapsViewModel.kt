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
import androidx.core.net.toUri
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel: ViewModel() {
    private val repository = Repository()

    //MAP
    private val _bottomSheetVisible = MutableLiveData<Boolean>()
    val bottomSheetVisible: LiveData<Boolean> = _bottomSheetVisible

    fun setBottomSheetVisible(visible: Boolean) {
        _bottomSheetVisible.value = visible
    }

    private val _filtrarColors = MutableLiveData(listOf<Float>())
    val filtrarColors = _filtrarColors


    private val _showBottomSheet = MutableLiveData<Boolean>()
    val showBottomSheet = _showBottomSheet

    private val _markers = MutableLiveData<Markers?>(null)
    val markers = _markers

    private val _dropDownText = MutableLiveData<String>()
    val dropDownText: LiveData<String> = _dropDownText

    private val _loadingMarkers = MutableLiveData(true)
    val loadingMarkers = _loadingMarkers

    private var _editingPosition = MutableLiveData<LatLng>()
    val editingPosition = _editingPosition

    private val _localitzacioSeleccionada = MutableLiveData(LatLng(0.0, 0.0))
    val localitzacioSeleccionada = _localitzacioSeleccionada

    private val _tipusMapa = MutableLiveData(MapType.NORMAL)
    val tipusMapa = _tipusMapa

    private val _llistaMarcadors = MutableLiveData(mutableListOf<Markers>())
    val llistaMarcadors = _llistaMarcadors

    private val _marcadorOn = MutableLiveData(false)
    val marcadorOn = _marcadorOn

    fun confirmMarcadorOn(guardar: Boolean) {
        _marcadorOn.value = guardar
    }

    private val _imatgeSeleccionada = MutableLiveData<Uri?>(null)
    val imatgeSeleccionada = _imatgeSeleccionada

    private val _urlImage = MutableLiveData<Uri?>(null)
    val urlImatge = _urlImage

    fun selectImage(image: Uri?) {
        _imatgeSeleccionada.value = image
    }

    fun selectImageUrl(imageUrl: Uri?) {
        _urlImage.value = imageUrl
    }

    fun cambiarLocalitzacio(posicio: LatLng) {
        _localitzacioSeleccionada.value = posicio
    }

    fun modificarEditingPosition(newValue: LatLng){
        _editingPosition.value = newValue
    }

    /*
    private var expandedMap by mutableStateOf(false)

    fun modifyExpandedMap(newValue: Boolean) {
        expandedMap = newValue
    }

    fun getExpandedMap(): Boolean {
        return expandedMap
    }
     */

    fun getMarkers() {
        var getMarkers = repository.getMarkers().whereEqualTo("userId", userId.value)
        if (!filtrarColors.value!!.isEmpty()) getMarkers = getMarkers.whereIn("markerColor", filtrarColors.value!!)

        getMarkers.addSnapshotListener(object: EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error != null){
                    Log.e("Firestore error", error.message.toString())
                    return
                }
                val tempList = mutableListOf<Markers>()
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        val newMarker = Markers(
                            dc.document.get("userId").toString(),
                            dc.document.id,
                            LatLng(dc.document.get("markerLatitude")!!.toString().toDouble(),
                                dc.document.get("markerLongitude")!!.toString().toDouble()),
                            dc.document.get("markerTitle")!!.toString(),
                            dc.document.get("markerSnippet")!!.toString(),
                            dc.document.get("markerColor")!!.toString().toFloat(),
                            dc.document.get("markerPhoto")?.toString()
                        )
                        tempList.add(newMarker)
                    }
                }
                _llistaMarcadors.value = tempList
            }
        })
    }
    fun guardarMarcadors(nouMarcador: Markers) {
        repository.editarMarcador(nouMarcador)
    }

    fun editarMarcador(editMarker: Markers) {
        repository.editarMarcador(editMarker)
    }

    fun esborrarMarcadors(marcadorEsborrat: Markers) {
        repository.deleteMarker(marcadorEsborrat)
    }

    fun esborrarImatge(url: String) {
        try {
            val storage = FirebaseStorage.getInstance().getReferenceFromUrl(url)
            storage.delete()
        } catch (e: Exception) {
            Log.i("esborrarImatge", "Exception caught trying to removing an image")
        }
    }

    fun selectMarker(marker: Markers?) {
        _markers.value = marker
    }


    private val _buscant = MutableLiveData(false)
    val buscant = _buscant

    private val _buscarText = MutableLiveData("")
    val buscarText = _buscarText

    fun onSearchTextChange(text: String) {
        _buscarText.value = text
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
                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        val newMarker = Markers(
                            dc.document.get("userId").toString(),
                            dc.document.id,
                            LatLng(dc.document.get("markerLatitude")!!.toString().toDouble(),
                                dc.document.get("markerLongitude")!!.toString().toDouble()),
                            dc.document.get("markerTitle")!!.toString(),
                            dc.document.get("markerSnippet")!!.toString(),
                            dc.document.get("markerColor")!!.toString().toFloat(),
                            dc.document.get("markerPhoto")?.toString()
                        )
                        tempList.add(newMarker)
                    }
                }
                _llistaMarcadors.value = tempList
            }
    }

    fun modifyDropDownText(newText: String) {
        _dropDownText.value = newText
    }
    fun modifyShowBottomSheet() {
        _showBottomSheet.value = true
    }

    fun hideBottomSheet() {
        _showBottomSheet.value = false
        selectImage(null)
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
    fun uploadImage(imageUri: Uri?, nomFitxer: String, deleteUrl: String?) {
        val storage = FirebaseStorage.getInstance().getReference("images/$nomFitxer")
        storage.putFile(imageUri?: "null".toUri())
            .addOnSuccessListener {
                Log.i("IMAGE UPLOAD", "Image uploaded successfully")
                storage.downloadUrl.addOnSuccessListener {
                    println("Image saved in Storage: $it")
                    selectImageUrl(it)
                    if (deleteUrl != null && deleteUrl != "null") esborrarImatge(deleteUrl)
                    confirmMarcadorOn(true)
                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image upload failed")
                selectImageUrl(deleteUrl?.toUri())
                confirmMarcadorOn(true)
            }
    }


    private val _permissionGranted = MutableLiveData(false)
    val permissionGranted = _permissionGranted

    private val _permissionDenied = MutableLiveData(false)
    val permissionDenied = _permissionDenied

    private val _showPermissionRationale = MutableLiveData(false)
    val showPermissionRationale = _showPermissionRationale

    fun setPermissionGranted(granted: Boolean) {
        this.permissionGranted.value = granted
    }

    fun setPermissionRationale(should: Boolean) {
        _showPermissionRationale.value = should
    }

    fun setPermissionDenied(denied: Boolean) {
        _permissionDenied.value = denied
    }
}