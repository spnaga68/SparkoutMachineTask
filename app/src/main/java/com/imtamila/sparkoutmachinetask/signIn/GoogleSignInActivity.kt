package com.imtamila.sparkoutmachinetask.signIn

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.imtamila.sparkoutmachinetask.R
import com.imtamila.sparkoutmachinetask.databinding.ActivityGoogleSignInBinding
import com.imtamila.sparkoutmachinetask.employees.view.MainActivity
import com.imtamila.sparkoutmachinetask.interfaces.DialogOnClickInterface
import com.imtamila.sparkoutmachinetask.utils.*

private const val REQUEST_CODE_SIGN_IN = 102

class GoogleSignInActivity : AppCompatActivity(), DialogOnClickInterface {
    private val TAG = GoogleSignInActivity::class.java.simpleName
    private lateinit var binding: ActivityGoogleSignInBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private var mDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_google_sign_in)

        if (SessionManager.getSession(this, USER_ID, "").isEmpty())
            initializeGoogleSignIn()
        else {
            moveToTabActivity()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnSignIn.setOnClickListener {
            signIn()
        }
    }

    override fun onStop() {
        super.onStop()
        mDialog?.let {
            if (it.isShowing)
                it.dismiss()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun moveToTabActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     * Function to setup GoogleSignInOptions and GoogleSignIn Client
     */
    private fun initializeGoogleSignIn() {
        val gso =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * Function to start google signInIntent
     */
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN)
    }

    /**
     * Function to signOut user account once user details get successfully after signIn
     */
    private fun signOut() {
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(
                this
            ) { Log.v(TAG, "signOut onComplete") }
    }

    /**
     *Function to handle SignIn result whether it completed successfully or not
     *
     * @param completedTask - Task<GoogleSignInAccount> object which is got from onActivityResult
     */
    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)
            updateUI(account)
        } catch (e: ApiException) {
            Log.v(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    /**
     * To update UI based on google signIn response
     *
     * @param googleSignInAccount - GoogleSignInAccount object from which user account details will be accessed.
     */
    private fun updateUI(googleSignInAccount: GoogleSignInAccount?) {
        if (googleSignInAccount != null) {
            SessionManager.saveSession(this, USER_ID, googleSignInAccount.id ?: "")
            SessionManager.saveSession(this, USER_EMAIL, googleSignInAccount.email ?: "")
            SessionManager.saveSession(this, USER_NAME, googleSignInAccount.displayName ?: "")
            signOut()
            moveToTabActivity()
        } else
            mDialog =
                CommonAlertDialog.alertDialog(
                    this,
                    this,
                    getString(R.string.google_signin_failed),
                    negativeButtonText = ""
                )
    }

    override fun onPositiveButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }

    override fun onNegativeButtonCLick(dialog: DialogInterface, alertType: Int) {
        dialog.dismiss()
    }
}