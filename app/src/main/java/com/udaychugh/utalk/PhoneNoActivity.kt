package com.udaychugh.utalk

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.hbb20.CountryCodePicker

const val PHONE_NUMBER = "phone"

class PhoneNoActivity : AppCompatActivity() {

    val phonenoET : EditText by lazy {
        findViewById<EditText>(R.id.phoneNumberEt)
    }

    val button : Button by lazy {
        findViewById<Button>(R.id.nextBtn)
    }

    lateinit var countryCode : String
    lateinit var phoneNumber : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_no)

        phonenoET.addTextChangedListener { value ->
            button.isEnabled = !(value.isNullOrEmpty() || value.length < 10)
        }

        button.setOnClickListener {
            checkNumber()
        }
    }

    private fun checkNumber() {
        countryCode = findViewById<CountryCodePicker>(R.id.ccp).selectedCountryCodeWithPlus
        phoneNumber = countryCode + phonenoET.text.toString()

        //validations

        startActivity(Intent(this, otpActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
        finish()
    }
}