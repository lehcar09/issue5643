package com.example.testproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.firebase.ui.auth.AuthMethodPickerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var txtUser: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtUser = findViewById(R.id.txt_user)

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser == null) {
            createSignInIntent()
        } else {
            currentUser.let {
                var providerId = ""
                var uid = ""

                for (profile in it.providerData) {
                    // Id of the provider (ex: google.com)
                    providerId = profile.providerId

                    // UID specific to the provider
                    uid = profile.uid
                }
                txtUser.text = uid
                Log.d(TAG,"User $uid via $providerId")
            }
        }
    }

    // [START auth_fui_create_launcher]
    // See: https://developer.android.com/training/basics/intents/result
    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }
    // [END auth_fui_create_launcher]

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
//            AuthUI.IdpConfig.PhoneBuilder().build(),
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.TwitterBuilder().build(),
        )
        // [END auth_fui_create_intent]

        val customLayout = AuthMethodPickerLayout.Builder(R.layout.custom_layout)
            .setGoogleButtonId(R.id.signInButton)
            .setEmailButtonId(R.id.customButton)
            .build()

        val signInIntent: Intent = AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.mipmap.ic_launcher)
//            .setLastMail(UsersManager.getInstance().getLastEmail())
//            .setIdpIndex(UsersManager.getInstance().getIdpIndexInt())
            .setIsSmartLockEnabled(false)
//            .setTosAndPrivacyPolicyUrls(getSelectedTosUrl(),
//                getSelectedPrivacyPolicyUrl())
            .setAuthMethodPickerLayout(customLayout)
            .build()
        signInLauncher.launch(signInIntent)
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.d(TAG, "User: $user")
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            Log.d(TAG, "Error: ${response?.error?.errorCode}")
        }
    }
    // [END auth_fui_result]

    companion object {

        private const val TAG = "Issue5643"

    }
}