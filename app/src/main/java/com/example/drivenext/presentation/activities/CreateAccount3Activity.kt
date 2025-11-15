package com.example.drivenext.presentation.activities

import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.example.drivenext.R
import com.example.drivenext.domain.model.User
import com.example.drivenext.utils.SessionManager
import com.example.drivenext.utils.SupabaseHelper
import kotlinx.coroutines.launch

class CreateAccount3Activity : AppCompatActivity() {
    private lateinit var backImage: ImageView
    private lateinit var licenseInputLayout: TextInputLayout
    private lateinit var licenseEditText: TextInputEditText
    private lateinit var dateInputLayout: TextInputLayout
    private lateinit var dateEditText: TextInputEditText
    private lateinit var uploadLicenseButton: ImageView
    private lateinit var uploadPassButton: ImageView
    private lateinit var profileButton: ImageView
    private lateinit var continueButton: Button

    private var licensePhotoUri: Uri? = null
    private var passPhotoUri: Uri? = null
    private var profilePhotoUri: Uri? = null
    private var currentImageButton: ImageView? = null
    private lateinit var sessionManager: SessionManager

    // Данные из предыдущих активностей
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var surname: String
    private lateinit var name: String
    private lateinit var patronymic: String
    private lateinit var dob: String
    private lateinit var sex: String

    private val getImageFromGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data

            when {
                // Фото из камеры
                data?.extras?.containsKey("data") == true -> {
                    val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Для Android 13+ (API 33)
                        data.extras?.getParcelable("data", Bitmap::class.java)
                    } else {
                        // Для старых версий Android
                        @Suppress("DEPRECATION")
                        data.extras?.getParcelable("data")
                    }

                    if (imageBitmap != null) {
                        currentImageButton?.setImageBitmap(imageBitmap)
                        when (currentImageButton) {
                            uploadLicenseButton -> licensePhotoUri = Uri.EMPTY
                            uploadPassButton -> passPhotoUri = Uri.EMPTY
                            profileButton -> profilePhotoUri = Uri.EMPTY
                        }
                    }
                }
                // Фото из галереи
                data?.data != null -> {
                    val imageUri = data.data
                    currentImageButton?.setImageURI(imageUri)
                    when (currentImageButton) {
                        uploadLicenseButton -> licensePhotoUri = imageUri
                        uploadPassButton -> passPhotoUri = imageUri
                        profileButton -> profilePhotoUri = imageUri
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account3)

        sessionManager = SessionManager(this)

        // Получаем данные из предыдущих активностей
        getDataFromIntent()

        backImage = findViewById(R.id.imageViewArrowLeft)
        licenseInputLayout = findViewById(R.id.licenseInputLayout)
        licenseEditText = findViewById(R.id.licenseEditText)
        dateInputLayout = findViewById(R.id.DateInputLayout)
        dateEditText = findViewById(R.id.dateEditText)
        uploadLicenseButton = findViewById(R.id.imageViewLicenseUpload)
        uploadPassButton = findViewById(R.id.imageViewPassportUpload)
        profileButton = findViewById(R.id.imageViewProfile)
        continueButton = findViewById(R.id.buttonContinueLast)

        backImage.setOnClickListener { finish() }

        licenseEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        licenseEditText.filters = arrayOf(InputFilter.LengthFilter(10))
        licenseEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                licenseInputLayout.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dateEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        dateEditText.filters = arrayOf(InputFilter.LengthFilter(10))
        dateEditText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isDeleting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                val digits = s.toString().replace("/", "")
                if (digits == current) return

                if (!isDeleting) {
                    val sb = StringBuilder()
                    for (i in digits.indices) {
                        sb.append(digits[i])
                        if ((i == 1 || i == 3) && i != digits.lastIndex) sb.append("/")
                    }
                    current = sb.toString().replace("/", "")
                    dateEditText.removeTextChangedListener(this)
                    dateEditText.setText(sb)
                    dateEditText.setSelection(sb.length)
                    dateEditText.addTextChangedListener(this)
                } else {
                    current = digits
                }
                dateInputLayout.error = null
            }
        })

        val photoClickListener = { imageButton: ImageView ->
            currentImageButton = imageButton
            openImagePicker()
        }

        uploadLicenseButton.setOnClickListener { photoClickListener(uploadLicenseButton) }
        uploadPassButton.setOnClickListener { photoClickListener(uploadPassButton) }
        profileButton.setOnClickListener { photoClickListener(profileButton) }

        continueButton.setOnClickListener {
            if (validateForm()) {
                registerUser()
            }
        }
    }

    private fun getDataFromIntent() {
        email = intent.getStringExtra("EMAIL") ?: ""
        password = intent.getStringExtra("PASSWORD") ?: ""
        surname = intent.getStringExtra("SURNAME") ?: ""
        name = intent.getStringExtra("NAME") ?: ""
        patronymic = intent.getStringExtra("PATRONYMIC") ?: ""
        dob = intent.getStringExtra("DOB") ?: ""
        sex = intent.getStringExtra("SEX") ?: ""

        // Проверяем, что все обязательные данные получены
        if (email.isEmpty() || password.isEmpty() || surname.isEmpty() || name.isEmpty() || dob.isEmpty() || sex.isEmpty()) {
            Toast.makeText(this, "Ошибка: не все данные получены", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openImagePicker() {
        val options = arrayOf<CharSequence>("Камера", "Галерея")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Выберите источник изображения")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        getImageFromGallery.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageFromGallery.launch(intent)
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val license = licenseEditText.text.toString().trim()
        val date = dateEditText.text.toString().trim()

        licenseInputLayout.error = null
        dateInputLayout.error = null

        if (license.isEmpty()) {
            licenseInputLayout.error = "Пожалуйста, заполните все обязательные поля."
            isValid = false
        } else if (license.length != 10) {
            licenseInputLayout.error = "Номер должен содержать ровно 10 цифр"
            isValid = false
        }

        if (date.isEmpty()) {
            dateInputLayout.error = "Пожалуйста, заполните все обязательные поля."
            isValid = false
        } else if (!isValidDate(date)) {
            dateInputLayout.error = "Введите корректную дату выдачи."
            isValid = false
        }

        if (licensePhotoUri == null) {
            Toast.makeText(this, "Пожалуйста, загрузите все необходимые фото.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (passPhotoUri == null) {
            Toast.makeText(this, "Пожалуйста, загрузите все необходимые фото.", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun isValidDate(dateStr: String): Boolean {
        val parts = dateStr.split("/")
        if (parts.size != 3) return false

        val day = parts[0].toIntOrNull() ?: return false
        val month = parts[1].toIntOrNull() ?: return false
        val year = parts[2].toIntOrNull() ?: return false

        if (day !in 1..31) return false
        if (month !in 1..12) return false
        if (year !in 1900..2025) return false

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }

    private fun registerUser() {
        val license = licenseEditText.text.toString().trim()
        val licenseDate = dateEditText.text.toString().trim()

        val user = User.createForRegistration(
            email = email,
            password = password,
            surname = surname,
            name = name,
            patronymic = if (patronymic.isEmpty()) null else patronymic,
            dob = dob,
            sex = sex,
            licenseNumber = license,
            licenseDate = licenseDate
        )

        lifecycleScope.launch {
            continueButton.isEnabled = false
            continueButton.text = "Регистрация..."

            val isSuccess = SupabaseHelper.registerUser(user, this@CreateAccount3Activity)

            if (isSuccess) {
                val registeredUser = SupabaseHelper.signInUser(email, password, this@CreateAccount3Activity)
                if (registeredUser != null) {
                    sessionManager.createLoginSession(
                        userId = registeredUser.id,
                        email = registeredUser.email,
                        name = registeredUser.name,
                        surname = registeredUser.surname,
                        registrationDate = registeredUser.registrationDate
                    )
                    val intent = Intent(this@CreateAccount3Activity, SuccessActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    continueButton.isEnabled = true
                    continueButton.text = "Продолжить"
                }
            } else {
                continueButton.isEnabled = true
                continueButton.text = "Продолжить"
            }
        }
    }
}