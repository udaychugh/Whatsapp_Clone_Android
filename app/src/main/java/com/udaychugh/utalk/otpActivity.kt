package com.udaychugh.utalk

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.util.concurrent.TimeUnit
import kotlin.math.log

class otpActivity : AppCompatActivity() {

    val verifyTv by lazy {
        findViewById<TextView>(R.id.verifyTv)
    }

    val otpEt by lazy {
        findViewById<TextView>(R.id.sentcodeEt)
    }

    val counterTv by lazy {
        findViewById<TextView>(R.id.counterTv)
    }

    val verificationBtn by lazy {
        findViewById<Button>(R.id.verificationBtn)
    }

    val resendBtn by lazy {
        findViewById<Button>(R.id.resendBtn)
    }

    lateinit var phoneNumber : String

    //for authentication
    private lateinit var auth : FirebaseAuth
    private var verificationInProcess = false
    private var storedVerificationId : String? = ""
    private lateinit var resendingToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mTimer : CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        initfunction()
        startVerify()


    }

    private fun startVerify() {
        startPhoneNumberVerification(phoneNumber)
        startCounter(60000)
        progressDialog = createDialog("Sending Verification Code", false)
        progressDialog.show()
    }

    private fun startCounter(time: Long) {
        resendBtn.isEnabled = false
        counterTv.isVisible = true
        mTimer = object : CountDownTimer(time, 1000){
            override fun onTick(timeLeft: Long) {
                counterTv.text = "Time Remaining : " + timeLeft/1000 + " seconds"
            }

            override fun onFinish() {
                resendBtn.isEnabled = true
                counterTv.isVisible = false
            }
        }.start()
    }

    private fun initfunction() {
        //init views
        verificationBtn.setOnClickListener {
            val credential = PhoneAuthProvider.getCredential(storedVerificationId!!,otpEt.text.toString())
            signinwithAuth(credential)
        }

        resendBtn.setOnClickListener {
            resendVerificationCode(phoneNumber, resendingToken)
            startCounter(60000)
            progressDialog = createDialog("Sending Verification Code again", false)
            progressDialog.show()
        }

        try {
            phoneNumber = intent.getStringExtra(PHONE_NUMBER)!!
            verifyTv.text = "Verify $phoneNumber"
        }catch (e:Exception){
            Toast.makeText(this, "Phone number not found, Try Again!!",Toast.LENGTH_SHORT).show()
            onBackPressed()
        }

        auth = Firebase.auth

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, token)
                progressDialog.dismiss()
                storedVerificationId = verificationId
                resendingToken = token
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                progressDialog.dismiss()

                val smsCode = credential.smsCode
                otpEt.text = smsCode
                //log().i("Verification completed", "Verification has been completed")
                signinwithAuth(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Invalid Phone Number",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    Snackbar.make(
                        findViewById(android.R.id.content),
                        "Quota Exceeded",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }


    }

    private fun startPhoneNumberVerification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signinwithAuth(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful){
                if(task.result?.additionalUserInfo?.isNewUser == true){
                    showSignupActivity()
                }else{
                    showHomeActivity()
                }
            }else{
                Toast.makeText(applicationContext, "Phone Number Verification Failed, Try again", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHomeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSignupActivity() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }


}

//Extension function
fun Context.createDialog(message : String, isCancelable : Boolean) : ProgressDialog{
    return ProgressDialog(this).apply {
        setCancelable(isCancelable)
        setMessage(message)
        setCanceledOnTouchOutside(false)
    }
}