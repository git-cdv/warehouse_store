package com.chkan.warehouse_store.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.chkan.warehouse_store.MainActivity
import com.chkan.warehouse_store.R
import com.chkan.warehouse_store.databinding.ActivityLoginBinding
import com.chkan.warehouse_store.utils.Constans
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity() {

    private var back_pressed : Long = 0
    private lateinit var binding: ActivityLoginBinding
    val TAG = "LoginActivity"

    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.authButton.setOnClickListener { launchSignInFlow() }
        setContentView(binding.root)

        }

    private fun launchSignInFlow() {
        // Даем пользователям возможность войти / зарегистрироваться, используя свою электронную почту или учетную запись Google.
        // Если пользователи выберите регистрацию с использованием их электронной почты, им также потребуется создать пароль.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Создайте и запустите намерение входа. Мы слушаем ответ этого действия с помощью
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), Constans.SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constans.SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finishAndRemoveTask()
            } else {
                // Sign in failed. If response is null the user canceled the sign-in flow using
                // the back button. Otherwise check response.getError().getErrorCode() and handle
                // the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    override fun onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true)
            finish()
        } else {
            Toast.makeText(
                this, "Для выхода нажмите \"назад\" еще раз",
                Toast.LENGTH_SHORT
            ).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}