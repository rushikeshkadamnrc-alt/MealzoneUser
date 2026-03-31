package com.example.mealzone

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mealzone.databinding.ActivitySignUpBinding
import com.example.mealzone.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("180050433602-icvue7aj73stn8tl900mtgapiepie6s3.apps.googleusercontent.com").requestEmail()
            .build()
        auth = Firebase.auth
        database = Firebase.database.reference

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.createAccountButton.setOnClickListener {
            username = binding.userName.text.toString()
            email = binding.emailAddress.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isBlank() || username.isBlank()) {
                Toast.makeText(this, "Please Fill the Details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }
        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.googleButton.setOnClickListener {
            Log.d("CLICK", "Google button clicked")
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account = task.result
                    if (account != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        auth.signInWithCredential(credential).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Sign-in Successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Sign-in Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Sign-in Failed", Toast.LENGTH_SHORT).show()
                }
            }

            }
    private fun createAccount(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()

                    saveUserData()

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()

                } else {

                    // 🔥 real error show karo
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()

                    Log.d("Account", "createAccount: Failure", task.exception)
                }
            }
    }

            private fun saveUserData() {
                username = binding.userName.text.toString()
                email = binding.emailAddress.text.toString().trim()
                password = binding.password.text.toString().trim()

                val user = UserModel(username, email, "")
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                database.child("user").child(userId).setValue(user)
            }
        }
