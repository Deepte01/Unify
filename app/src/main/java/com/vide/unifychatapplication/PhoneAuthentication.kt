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
        lateinit var codeSent:String

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_phone_authentication)
            mAuth=FirebaseAuth.getInstance()
            Log.d("PhoneAuthentication","${mAuth}")
            createAccBtn.setOnClickListener{
                Log.d("PhoneAuthentication","verify button is clicked")
                progressBar.visibility=View.VISIBLE
                verify()
            }
            createAccountBtn.setOnClickListener{
                startActivity(Intent(this,NewUserRegisteration::class.java))
            }
        }

        override fun onStart() {
            super.onStart()
            if(mAuth.currentUser!=null)
            {
                val intent=Intent(this, ChatandContactsTab::class.java)
                //val intent=Intent(this, FetchPhoneContacts::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            else{
                Toast.makeText(this,"Please Sign In!!",Toast.LENGTH_LONG).show()
            }
        }


        private fun verificationCallBacks()
        {
            mcallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    Log.d("PhoneAuthentication","on verif completed")
                    progressBar.visibility=View.INVISIBLE
                    signIn(credential)

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.d("PhoneAuthentication","on verif failed")
                    Log.d("PhoneAuthentication","${p0.message}")
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    Log.d("PhoneAuthentication","on code sent")
                }

            }
        }

        private fun signIn(credential: PhoneAuthCredential) {
            mAuth.signInWithCredential(credential)
                .addOnCompleteListener{
                    Log.d("PhoneAuthentication","on signIn")
                    if(it.isSuccessful){

                        Toast.makeText(this,"Logged in Successfully!!",Toast.LENGTH_LONG).show()
                        startActivity(Intent(this,ChatandContactsTab::class.java))
                    }
                }
        }

        private fun verify()
        {
            verificationCallBacks()
            Log.d("PhoneAuthentication","on verify")
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