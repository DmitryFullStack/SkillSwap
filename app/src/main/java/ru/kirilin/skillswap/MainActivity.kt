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
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import ru.kirilin.skillswap.data.model.RetrofitModule
import ru.kirilin.skillswap.data.model.User
import ru.kirilin.skillswap.data.model.UserId
import ru.kirilin.skillswap.ui.fragment.MainFragment
import ru.kirilin.skillswap.ui.fragment.SignInFragment
import ru.kirilin.skillswap.ui.fragment.SignInFragment.Companion.RC_SIGN_IN
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel
import ru.kirilin.skillswap.ui.viewmodel.MainViewModelFactory


class MainActivity : AppCompatActivity(), View.OnClickListener{


    private val TAG = "Oauth2Google"

    lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serverClientId = R.string.server_client_id.toString()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(Scopes.DRIVE_APPFOLDER))
            .requestServerAuthCode(serverClientId)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null){
            val user = tryToFetchUser(account)
                .also {
                    it.id = UserId(account.id.toString(), "GOOGLE")
                }
            startMainFragment(user, account.id!!)
        }else{
            startFragment(SignInFragment(mGoogleSignInClient))
        }
//        var viewModel: RegistrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
//        startFragment(RegistrationFragment(viewModel))
    }

    private fun tryToFetchUser(account: GoogleSignInAccount): User {
        return runBlocking {
            try {
                RetrofitModule.userApi.getUserById(account.id.toString())
            } catch (throwable: HttpException) {
                return@runBlocking if (throwable.code() == 404) {
                    RetrofitModule.userApi.createNewUser(createUserFromAccount(account))
                } else {
                    finish()
                }
            }
        } as User
    }

    private fun createUserFromAccount(account: GoogleSignInAccount) =
        User(
            login = account.displayName,
            name = account.displayName,
            id = UserId(accountNumber = account.id!!)
        )

    fun startMainFragment(user: User, accountId: String) {
        val viewModel = ViewModelProvider(this, MainViewModelFactory(user))
            .get(MainViewModel::class.java)
        startFragment(MainFragment(viewModel, accountId))
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment::class.java.simpleName)
            .commit()
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
            startMainFragment(user, account.id!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)
        }
    }
}