package com.example.kotlintodopractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        init(view)

        binding.textViewSignIn.setOnClickListener {
            navController.navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        binding.nextBtn.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            val verifyPass = binding.verifyPassEt.text.toString()
            val nickname = binding.nicknameEt.text.toString()

            if(email.isEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                Toast.makeText(context, "Fill E-mail Field First!!", Toast.LENGTH_SHORT).show()
            }
            else if(pass.isEmpty() && email.isNotEmpty() && verifyPass.isNotEmpty()) {
                Toast.makeText(context, "Fill Password Field First!!", Toast.LENGTH_SHORT).show()
            }
            else if(verifyPass.isEmpty() && pass.isNotEmpty() && email.isNotEmpty()) {
                Toast.makeText(context, "Enter Password Again!!", Toast.LENGTH_SHORT).show()
            }
            else if(verifyPass != pass) {
                Toast.makeText(context, "Passwords Does Not Match!!", Toast.LENGTH_SHORT).show()
            }
            else {
                if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                    if (pass == verifyPass) {

                        registerUser(email, pass, nickname)

                    } else {
                        Toast.makeText(context, "Password not verified", Toast.LENGTH_SHORT).show()
                    }

                } else
                    Toast.makeText(context, "Empty fields are not allowed", Toast.LENGTH_SHORT)
                        .show()
            }
        }

    }

    private fun registerUser(email: String, pass: String, nickname: String) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val database = FirebaseDatabase.getInstance().reference
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null) {
                    val userRef = database.child("users").child(userId)
                    userRef.child("score").setValue(0)
                    userRef.child("overall_scores").setValue(0)
                    userRef.child("nickname").setValue(nickname)
                    Toast.makeText(context, "Registered", Toast.LENGTH_SHORT).show()
                }
            }
            else
                Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        mAuth = FirebaseAuth.getInstance()
    }
}