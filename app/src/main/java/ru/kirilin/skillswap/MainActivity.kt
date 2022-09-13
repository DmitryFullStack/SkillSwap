package ru.kirilin.skillswap

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.User
import ru.kirilin.skillswap.data.model.UserId
import ru.kirilin.skillswap.ui.MainFragment
import ru.kirilin.skillswap.ui.RegistrationFragment
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel
import ru.kirilin.skillswap.ui.viewmodel.MainViewModelFactory
import ru.kirilin.skillswap.ui.viewmodel.RegistrationViewModel


class MainActivity : AppCompatActivity(), View.OnClickListener{

    private val RC_SIGN_IN = 9001
    private val TAG = "Oauth2Google"

    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null){
            val user = runBlocking { try {
                    RetrofitModule.userApi.getUserById(account.id.toString())
                } catch (throwable: HttpException){
                    return@runBlocking if(throwable.code() == 404){
                        RetrofitModule.userApi.createNewUser(createUserFromAccount(account))
                    }else{
                        finish()
                    }
                }} as User
            startMainFragment(user)
        }else{
            val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
            signInButton.setSize(SignInButton.SIZE_STANDARD)
            signInButton.setOnClickListener(this)
        }
//        var viewModel: RegistrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
//        startFragment(RegistrationFragment(viewModel))
    }

    private fun createUserFromAccount(account: GoogleSignInAccount) =
        User(
            login = account.displayName,
            name = account.displayName,
            id = UserId(accountNumber = account.id!!)
        )

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment::class.java.simpleName)
            .commit()
    }

    fun startMainFragment(user: User) {
        val viewModel = ViewModelProvider(this, MainViewModelFactory(user))
            .get(MainViewModel::class.java)
        startFragment(MainFragment(viewModel))
    }

    override fun onClick(view: View?) {
        signIn()
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val user = runBlocking { RetrofitModule.userApi.createNewUser(createUserFromAccount(account))}
            // Signed in successfully, show authenticated UI.
            startMainFragment(user)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
    }
}