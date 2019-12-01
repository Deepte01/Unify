package com.vide.unifychatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.activity_phone_authentication.*
import java.util.concurrent.TimeUnit

class PhoneAuthentication : AppCompatActivity() {

    lateinit var  mcallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var  mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_authentication)
        mAuth=FirebaseAuth.getInstance()
        verifyBtn.setOnClickListener{
            progressBar.visibility=View.VISIBLE
            verify()
        }
    }

    private fun verificationCallBacks()
    {
        mcallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressBar.visibility=View.INVISIBLE
                signIn(credential)

            }

            override fun onVerificationFailed(p0: FirebaseException) {
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
            }

        }
    }

    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Logged in Successfully!!",Toast.LENGTH_LONG).show()
                    startActivity(Intent(this,MainActivity::class.java))
                }
            }
    }

    private fun verify()
    {
        verificationCallBacks()
        val phno =phnoTxt.text.toString()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phno,
            60,
            TimeUnit.SECONDS,
            this,
            mcallbacks
        )

    }

}
