package ru.kirilin.skillswap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.kirilin.skillswap.ui.MainFragment
import ru.kirilin.skillswap.ui.viewmodel.MainViewModel

import ru.kirilin.skillswap.ui.RegistrationFragment
import ru.kirilin.skillswap.ui.viewmodel.RegistrationViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var viewModel: RegistrationViewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        startFragment(RegistrationFragment(viewModel))
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment::class.java.simpleName)
            .commit()
    }

    fun startMainFragment() {
        var viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        startFragment(MainFragment(viewModel))
    }
}