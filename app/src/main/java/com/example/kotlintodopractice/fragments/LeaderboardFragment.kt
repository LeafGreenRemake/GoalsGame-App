package com.example.kotlintodopractice.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.FragmentLeaderboardBinding
import com.example.kotlintodopractice.databinding.FragmentSignInBinding
import com.example.kotlintodopractice.utils.adapter.LeaderboardAdapter
import com.example.kotlintodopractice.utils.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LeaderboardFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        // Create a reference to the Firebase Realtime Database
        database = FirebaseDatabase.getInstance().reference

        // Retrieve the top 50 users from the Firebase Realtime Database
        database.child("users").orderByChild("score").limitToLast(50).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { users.add(it) }
                }

                // Sort the users by score in descending order
                users.sortByDescending { it.score }

                // Create a RecyclerView and set its adapter to the LeaderboardAdapter
                val recyclerView = view.findViewById<RecyclerView>(R.id.leaderboardRecyclerView)
                recyclerView.adapter = LeaderboardAdapter(users)

                // Use a LinearLayoutManager to display the items in a vertical list
                recyclerView.layoutManager = LinearLayoutManager(view.context) // or use the context passed to the init function
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })

        binding.exitButton.setOnClickListener{
            navController.navigate(R.id.action_leaderboardFragment_to_homeFragment)
        }
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        sharedPreferences = requireActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

}