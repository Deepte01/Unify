package com.vide.unifychatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        SignoutBtn.setOnClickListener{
            view: View -> mAuth.signOut()
            startActivity(Intent(this, PhoneAuthentication::class.java))
            Toast.makeText(this,"Successfully Signed OUt!!",Toast.LENGTH_LONG).show()
        }
    }
     override fun onStart() {
        super.onStart()
         if(mAuth.currentUser==null)
         {
             val intent=Intent(this, PhoneAuthentication::class.java)
             intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
             startActivity(intent)

         }
         else{
             Toast.makeText(this,"Already Signed In!!",Toast.LENGTH_LONG).show()
         }
    }
}
