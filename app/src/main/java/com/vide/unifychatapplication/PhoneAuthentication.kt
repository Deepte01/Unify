    package com.vide.unifychatapplication

    import android.Manifest
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.os.Build
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.util.Log
    import android.view.View
    import android.widget.Toast
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.google.firebase.FirebaseException
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.auth.PhoneAuthCredential
    import com.google.firebase.auth.PhoneAuthProvider
    import com.google.firebase.database.DataSnapshot
    import com.google.firebase.database.DatabaseError
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ValueEventListener
    import kotlinx.android.synthetic.main.activity_phone_authentication.*
    import java.util.concurrent.TimeUnit
    /*
    This function is to provide direct login to the app.
     */

    class PhoneAuthentication : AppCompatActivity() {

        lateinit var  mcallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
        lateinit var  mAuth: FirebaseAuth
        var listOfUserPhos=ArrayList<String>()
        lateinit var codeSent:String
        init {
            // assign the list of phone numbers in this block since  it is a asynchronous process.
            getUsers()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_phone_authentication)
            /*
            * First check if the user is giving access to their phone contact list
            * This is useful while fetching the contacts in the contacts fragment.
            * */
            if(Build.VERSION.SDK_INT>=23){

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                    //Permission not granted
                    Log.d("PhoneAuthentication","Permission not granted")
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_CONTACTS)) {
                        //Seeking Permission
                        Log.d("PhoneAuthentication"," Seeking Permission ")
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            1)

                    } else {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            1)
                    }
                } else {
                    //Permission already granted
                    Log.d("PhoneAuthentication","Permission already granted")
                    mAuth=FirebaseAuth.getInstance()
                    Log.d("PhoneAuthentication","${mAuth}")
                    createAccBtn.setOnClickListener{
                        //verify button is clicked
                        Log.d("PhoneAuthentication","verify button is clicked")
                        if(phnoTxt.text.toString().length<10 || phnoTxt.text.equals(""))
                        {
                            Toast.makeText(this,"Please enter a valid phone number",Toast.LENGTH_SHORT).show()

                        }
                        else
                        {
                            if(listOfUserPhos.contains(phnoTxt.text.toString()))
                            {
                                //verify the user only if he is a member of this app
                                progressBar.visibility = View.VISIBLE
                                verify()

                            }
                            else
                            {
                                Toast.makeText(this,"Please create an account",Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                    createAccountBtn.setOnClickListener{
                        startActivity(Intent(this,NewUserRegisteration::class.java))
                    }
                }
            }

        }

        //check if the user is already signed in
        //if already signedin, directly navigate to naext page
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
        //verify the phone number by receiving the code in text message
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

        //autodetect the message and signin
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

        //send a message to the phone
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
        /*
        get the list of all the phone numbers which are already registered with this app
        and this list will be used to check if the input phone number provided by the user is already registered.
         */
        fun getUsers()
        {
            //to check if the input putnumber already exits to signin
            var dbRef = FirebaseDatabase.getInstance().getReference("Users")
            Log.d("PhoneAuthentication","in side get users}")


            dbRef.addValueEventListener(object : ValueEventListener {

                override fun onCancelled(p0: DatabaseError) {

                    //Log.d("DB","DB Error $p0")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                chatList.clear()
                    for (data in dataSnapshot.children) {
                        listOfUserPhos.add(data.child("phno").value.toString())
                    }
                }
            })
        }
    }