package com.chkan.warehouse_store

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.chkan.warehouse_store.databinding.FragmentMainBinding
import com.chkan.warehouse_store.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth


class MainFragment : Fragment() {

    companion object {
        const val TAG = "MainFragment"
    }

    // Get a reference to the ViewModel scoped to this Fragment
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
    }
    /**
     * Наблюдает за состоянием аутентификации и соответствующим образом изменяет пользовательский интерфейс.
     * Если есть вошедший в систему пользователь: (1) показать кнопку выхода и (2) отобразить его имя.
     * Если нет зарегистрированного пользователя: показать страницу авторизации
     */
    private fun observeAuthenticationState() {

        //authenticationState - это LiveData которая дает статусы авторизации и здесь мы на них реагируем
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                binding.welcomeText.text= FirebaseAuth.getInstance().currentUser?.displayName

                }
                else -> {
                    findNavController().navigate(R.id.loginFragment)
                }
            }
        })
    }

}