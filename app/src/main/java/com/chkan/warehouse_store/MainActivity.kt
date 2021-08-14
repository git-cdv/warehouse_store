package com.chkan.warehouse_store

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.chkan.warehouse_store.login.LoginActivity
import com.chkan.warehouse_store.login.LoginViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private var back_pressed : Long = 0
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeAuthenticationState()
        setContentView(R.layout.activity_main)

        //устанавливаем BottomMenu с привязкой к Navigation
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        navController = host.navController

        setupBottomNavMenu(navController)

        //устанавливаем на весь экран
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

    }

    /**
     * Наблюдает за состоянием аутентификации и соответствующим образом изменяет пользовательский интерфейс.
     * Если нет зарегистрированного пользователя: показать страницу авторизации
     */
    private fun observeAuthenticationState() {

        //authenticationState - это LiveData которая дает статусы авторизации и здесь мы на них реагируем
        viewModel.authenticationState.observe(this, Observer { authenticationState ->

            if (authenticationState == LoginViewModel.AuthenticationState.UNAUTHENTICATED){
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }

        })
    }


    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
    }
    //переопределяем нажатие кнопки назад - чтобы выходило а не переходило по стеку
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