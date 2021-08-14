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

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
        binding.welcomeText.text= FirebaseAuth.getInstance().currentUser?.displayName
        return binding.root
    }

}