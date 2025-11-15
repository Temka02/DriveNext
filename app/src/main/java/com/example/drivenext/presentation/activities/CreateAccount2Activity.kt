package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.ContextCompat
import com.example.drivenext.R

class CreateAccount2Activity : AppCompatActivity() {
    private lateinit var backImage: ImageView
    private lateinit var surnameInputLayout: TextInputLayout
    private lateinit var nameInputLayout: TextInputLayout
    private lateinit var patronymicInputLayout: TextInputLayout
    private lateinit var dobInputLayout: TextInputLayout
    private lateinit var surnameEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var patronymicEditText: TextInputEditText
    private lateinit var dobEditText: TextInputEditText
    private lateinit var radioGroupSex: RadioGroup
    private lateinit var continueButton: Button

    private var email: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account2)

        email = intent.getStringExtra("EMAIL") ?: ""
        password = intent.getStringExtra("PASSWORD") ?: ""

        // Инициализация
        backImage = findViewById(R.id.imageViewArrowLeft)
        surnameInputLayout = findViewById(R.id.surnameInputLayout)
        nameInputLayout = findViewById(R.id.nameInputLayout)
        patronymicInputLayout = findViewById(R.id.patronymicInputLayout)
        dobInputLayout = findViewById(R.id.BirthDateInputLayout)
        surnameEditText = findViewById(R.id.surnameEditText)
        nameEditText = findViewById(R.id.nameEditText)
        patronymicEditText = findViewById(R.id.patronymicEditText)
        dobEditText = findViewById(R.id.BirthDateEditText)
        radioGroupSex = findViewById(R.id.radioGroupSex)
        continueButton = findViewById(R.id.button8)

        backImage.setOnClickListener {
            startActivity(Intent(this, CreateAccount1Activity::class.java))
            finish()
        }

        surnameEditText.setOnFocusChangeListener { _, _ -> surnameInputLayout.error = null }
        nameEditText.setOnFocusChangeListener { _, _ -> nameInputLayout.error = null }
        patronymicEditText.setOnFocusChangeListener { _, _ -> patronymicInputLayout.error = null }
        dobEditText.setOnFocusChangeListener { _, _ -> dobInputLayout.error = null }

        val letterFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[a-zA-Zа-яА-ЯёЁ]+"))) source else ""
        }

        fun configureNameField(editText: TextInputEditText) {
            editText.inputType = android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS or
                    android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            editText.filters = arrayOf(letterFilter)
        }

        configureNameField(surnameEditText)
        configureNameField(nameEditText)
        configureNameField(patronymicEditText)

        radioGroupSex.setOnCheckedChangeListener { _, checkedId ->
            for (i in 0 until radioGroupSex.childCount) {
                val rb = radioGroupSex.getChildAt(i) as com.google.android.material.radiobutton.MaterialRadioButton
                val isChecked = rb.id == checkedId
                val colorRes = if (isChecked) R.color.primary else R.color.form_gray
                val color = ContextCompat.getColor(this, colorRes)
                rb.setTextColor(color)
                rb.buttonTintList = android.content.res.ColorStateList.valueOf(color)
            }
        }
        for (i in 0 until radioGroupSex.childCount) {
            val rb = radioGroupSex.getChildAt(i) as com.google.android.material.radiobutton.MaterialRadioButton
            val isChecked = rb.isChecked
            val colorRes = if (isChecked) R.color.primary else R.color.form_gray
            val color = ContextCompat.getColor(this, colorRes)
            rb.setTextColor(color)
            rb.buttonTintList = android.content.res.ColorStateList.valueOf(color)
        }

        dobEditText.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        dobEditText.filters = arrayOf(InputFilter.LengthFilter(10))


        dobEditText.addTextChangedListener(object : TextWatcher {
            private var current = ""
            private var isSelfUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isSelfUpdating) return

                val input = s?.toString() ?: ""
                val cleanInput = input.replace("/", "")

                if (cleanInput.length > 8) {
                    isSelfUpdating = true
                    s?.replace(0, s.length, current)
                    isSelfUpdating = false
                    return
                }

                if (cleanInput == current) return

                val formatted = StringBuilder()
                for (i in cleanInput.indices) {
                    if (i == 2 || i == 4) formatted.append("/")
                    formatted.append(cleanInput[i])
                }

                current = cleanInput
                isSelfUpdating = true
                dobEditText.setText(formatted)
                dobEditText.setSelection(formatted.length)
                isSelfUpdating = false

                dobInputLayout.error = null
            }
        })

        continueButton.setOnClickListener {
            if (validateForm()) {
                val surname = surnameEditText.text.toString().trim()
                val name = nameEditText.text.toString().trim()
                val patronymic = patronymicEditText.text.toString().trim()
                val dob = dobEditText.text.toString().trim()
                val selectedSexId = radioGroupSex.checkedRadioButtonId
                val sex = when (selectedSexId) {
                    R.id.radioMan -> "Мужской"
                    R.id.radioWoman -> "Женский"
                    else -> ""
                }

                val intent = Intent(this, CreateAccount3Activity::class.java)
                // Передаем все данные
                intent.putExtra("EMAIL", email)
                intent.putExtra("PASSWORD", password)
                intent.putExtra("SURNAME", surname)
                intent.putExtra("NAME", name)
                intent.putExtra("PATRONYMIC", patronymic)
                intent.putExtra("DOB", dob)
                intent.putExtra("SEX", sex)
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val surname = surnameEditText.text.toString().trim()
        val name = nameEditText.text.toString().trim()
        val dob = dobEditText.text.toString().trim()
        val selectedSexId = radioGroupSex.checkedRadioButtonId

        surnameInputLayout.error = null
        nameInputLayout.error = null
        patronymicInputLayout.error = null
        dobInputLayout.error = null

        if (surname.isEmpty()) {
            surnameInputLayout.error = "Поле обязательно для заполнения."
            isValid = false
        }

        if (name.isEmpty()) {
            nameInputLayout.error = "Поле обязательно для заполнения."
            isValid = false
        }

        if (dob.isEmpty()) {
            dobInputLayout.error = "Поле обязательно для заполнения."
            isValid = false
        } else if (!isValidDate(dob)) {
            dobInputLayout.error = "Введите корректную дату в формате DD/MM/YYYY"
            isValid = false
        }

        if (selectedSexId == -1) {
            Toast.makeText(this, "Выберите пол", Toast.LENGTH_SHORT).show()
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
}