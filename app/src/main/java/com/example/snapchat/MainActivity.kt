package com.example.snapchat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var emailEditText:EditText?=null
    var passwordEditText:EditText?=null
    val mAuth=FirebaseAuth.getInstance()

//     var goButton=Button(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        if (mAuth.currentUser != null) {
            logIn()
        }
    }
        fun goClicked(view: View) {
            //Check if we can log in the user
            mAuth.signInWithEmailAndPassword(
                emailEditText?.text.toString(),
                passwordEditText?.text.toString()
            )
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        logIn()
                        FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user?.uid.toString()).child("email").setValue(emailEditText?.text.toString())


                    } else {
                        //Sign Up the User
                        mAuth.createUserWithEmailAndPassword(
                            emailEditText?.text.toString(),
                            passwordEditText?.text.toString()
                        ).addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Succesfully logged in", Toast.LENGTH_SHORT)
                                    .show()
                                //Add to database
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.result?.user?.uid.toString()).child("email").setValue(emailEditText?.text.toString())
                                logIn()
                            } else {
                                Toast.makeText(this, "Login Failed,try again!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }

                    }
                }

           }



    private fun logIn() {
        //Change the activity
        val intent = Intent(this, SnapsActivity::class.java)
        startActivity(intent)


    }

}
//task.result?.user?.uid.toString()