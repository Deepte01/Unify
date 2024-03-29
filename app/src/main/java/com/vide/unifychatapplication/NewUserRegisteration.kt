package com.vide.unifychatapplication

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_new_user_registeration.*
import java.util.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_new_user_registeration.createAccBtn
import kotlinx.android.synthetic.main.activity_new_user_registeration.phnoTxt
import kotlinx.android.synthetic.main.activity_new_user_registeration.progressBar
import kotlinx.android.synthetic.main.activity_phone_authentication.*
import java.util.concurrent.TimeUnit


/*
This class is used for creating a new user for the first time
 */
class NewUserRegisteration : AppCompatActivity() {

    private var selectedImageURI:Uri?=null
    private val IMAGE_FETCH_CODE:Int=100
    private var database= FirebaseDatabase.getInstance()
    var myRef = database.getReference("Users")
    lateinit var  mcallbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var  mAuth: FirebaseAuth

   // myRef.child("Users").child("name").setValue("Deepika bewakuff hai")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_user_registeration)
        mAuth=FirebaseAuth.getInstance()

       // The image upload functionality is not working
        selectPhotoBtn.setOnClickListener{
            // select photo from the gallery
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,IMAGE_FETCH_CODE)
        }
       //verify the user's phone number
        verifyBtn.setOnClickListener{
            createUserProgressBar.visibility=View.VISIBLE
            verify()
        }
        createAccBtn.setOnClickListener{
            if(usernameTxt.text!=null && phnoTxt.text!=null && phnoTxt.text.length>10 && phnoTxt.text.length<13 )
            {
                //first check the format of the phone number entered by the user and convert it into required format
                var temp:String
                if(phnoTxt.text.startsWith("+1"))
                {
                    temp =phnoTxt.text.replace("""[-, ,(,)]""".toRegex(), "")
                }
                else if(phnoTxt.text.startsWith("1"))
                {
                    temp ="+"+phnoTxt.text.replace("""[-, ,(,)]""".toRegex(), "")
                }
                else if(phnoTxt.text.startsWith("+91"))
                {
                    temp =phnoTxt.text.replace("""[-, ,(,)]""".toRegex(), "")
                }
                else
                {
                    temp="+1"+phnoTxt.text.replace("""[-, ,(,)]""".toRegex(), "")
                }
              //  uploadImagetoFirestore()

                try{
                    var currentUser= mAuth!!.currentUser
                    // add the new user informatiom to the database
                    myRef.child(currentUser!!.uid).child("username").setValue(usernameTxt.text.toString())
                    myRef.child(currentUser!!.uid).child("phno").setValue(temp)
                    myRef.child(currentUser!!.uid).child("userId").setValue(currentUser!!.uid)
                    // navigate into the next activity
                    startActivity(Intent(this,ChatandContactsTab::class.java))
                }
                catch (ex:Exception){
                    //display the message incase of an error
                    Log.d("NewUserRegisteration","${ex.message}")
                }

            }
            else
            {
                Toast.makeText(this,"Please enter valid details",Toast.LENGTH_SHORT).show()
            }
        }
    }


    // select an image from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100 && resultCode==Activity.RESULT_OK && data!=null)
        {
            // proceed and check what is the image secleted
            selectedImageURI=data.data
            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedImageURI)
            val bitmapDrawable = BitmapDrawable(bitmap)
            selectPhotoBtn.text=""
            selectPhotoBtn.setBackgroundDrawable(bitmapDrawable)
        }
    }
    //upload the image to Firestore and retrieve the uri path of that image
    private fun uploadImagetoFirestore()
    {
        if (selectedImageURI==null)return
        val fileName = UUID.randomUUID().toString()
        val ref= FirebaseStorage.getInstance("gs://unifychatapplication.appspot.com").getReference("/images/$fileName")
        ref.putFile(selectedImageURI!!)
            .addOnSuccessListener {
                Log.d("Register","successfully uploaded the image: ${it.metadata?.path}")
            }
            .addOnCanceledListener {
                Log.d("Register","failed to upload")
            }
    }
    //verifying the phone
    private fun verificationCallBacks()
    {
        mcallbacks = object: PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progressBar.visibility= View.INVISIBLE
                signIn(credential)

            }

            override fun onVerificationFailed(p0: FirebaseException) {
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                // this method automatically sends the secret code as a message
                super.onCodeSent(p0, p1)
            }

        }
    }

    //auto authenticate the phone
    /*
    This method validates the message received in the message to login
     */
    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener{
                if(it.isSuccessful){

                    Toast.makeText(this,"Logged in Successfully!!", Toast.LENGTH_LONG).show()
                    createAccBtn.visibility=View.VISIBLE
                  //  startActivity(Intent(this,FetchPhoneContacts::class.java))
                }
            }
    }

    //get message to the phone
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
        ) // it is used to send the message to the phone

    }

}
