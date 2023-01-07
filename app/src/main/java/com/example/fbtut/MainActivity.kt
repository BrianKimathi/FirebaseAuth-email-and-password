package com.example.fbtut

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fbtut.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { registerUser() }

        binding.btnLogin.setOnClickListener { loginUser() }

        binding.btnUpdateProfile.setOnClickListener { updateProfile() }

    }

    override fun onStart() {
        super.onStart()
        checkLoggedInState()
    }

    private fun updateProfile(){
        auth.currentUser?.let { user ->
            val username = binding.etUsername.text.toString()
            val photoUri = Uri.parse("android.resource://$packageName/${R.drawable.logo_black_square}")

            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(username)
                .setPhotoUri(photoUri)
                .build()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    user.updateProfile(profileUpdate).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                        Toast.makeText(this@MainActivity, "successfully updated profile!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "Error ${e.message} occurred.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun registerUser(){
        val email = binding.etEmailRegister.text.toString()
        val password = binding.etPasswordRegister.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "Error ${e.message} occurred!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun loginUser(){
        val email = binding.etEmailLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).await()
                    withContext(Dispatchers.Main){
                        checkLoggedInState()
                    }
                }catch (e: Exception){
                    withContext(Dispatchers.Main){
                        Toast.makeText(this@MainActivity, "Error ${e.message} occurred!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun checkLoggedInState() {
        val user = auth.currentUser
        if (user == null)
            binding.tvLoggedIn.text = "You are not Logged In!"

        binding.tvLoggedIn.text = "You are Logged In!"
        binding.etUsername.setText(user!!.displayName)
        binding.ivProfilePicture.setImageURI(user.photoUrl)
    }

}