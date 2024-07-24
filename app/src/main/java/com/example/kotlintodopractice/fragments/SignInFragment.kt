package com.example.kotlintodopractice.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.kotlintodopractice.databinding.FragmentSignUpBinding


class SignInFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignInBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        var email = sharedPreferences.getString("email", null)
        var pass = sharedPreferences.getString("password", null)

        if (email != null && pass != null) {
            binding.emailEt.setText(email)
            binding.passEt.setText(pass)
        }



        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_signInFragment_to_signUpFragment)
        }


        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()

            if (email.isEmpty() && pass.isNotEmpty()) {
                binding.emailEt.requestFocus()
            } else if (pass.isEmpty() && email.isNotEmpty()) {
                binding.passEt.requestFocus()
            } else {
                if (email.isNotEmpty() && pass.isNotEmpty())
                    loginUser(email, pass)
                else
                    Toast.makeText(context,"Empty fields are not Allowed",Toast.LENGTH_SHORT).show() // Request focus on the email field by default
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val editor = sharedPreferences.edit()
                editor.putString("email", email)
                editor.putString("password", pass)
                editor.apply()
                navController.navigate(R.id.action_signInFragment_to_homeFragment)
            }
            else
                Toast.makeText(context,"Not Login.Try Again!!",Toast.LENGTH_SHORT).show()
        }
    }

    fun clearSharedPreferences() {
        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    private lateinit var sharedPreferences: SharedPreferences
    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("login_prefs", MODE_PRIVATE)
    }
}
