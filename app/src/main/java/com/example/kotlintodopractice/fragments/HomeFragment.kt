package com.example.kotlintodopractice.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.databinding.FragmentHomeBinding
import com.example.kotlintodopractice.utils.adapter.TaskAdapter
import com.example.kotlintodopractice.utils.model.ToDoData
import com.example.kotlintodopractice.utils.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import androidx.navigation.NavController
import com.example.kotlintodopractice.MainActivity
import com.example.kotlintodopractice.utils.adapter.TaskTypesAdapter
import com.example.kotlintodopractice.utils.moddel.ToDoType
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.UUID


class HomeFragment : Fragment(), ToDoDialogFragment.OnDialogNextBtnClickListener,
    TaskAdapter.TaskAdapterInterface {

    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private var frag: ToDoDialogFragment? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var authId: String
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var toDoItemList: MutableList<ToDoData>
    private lateinit var navController: NavController

    private val toDoTypes = listOf(
        ToDoType(R.drawable.baseline_work_24), ToDoType(R.drawable.baseline_menu_book_24), ToDoType(R.drawable.baseline_fitness_center_24),
        ToDoType(R.drawable.baseline_color_lens_24), ToDoType(R.drawable.baseline_cruelty_free_24), ToDoType(R.drawable.baseline_diversity_1_24),
        ToDoType(R.drawable.baseline_beach_access_24), ToDoType(R.drawable.baseline_diversity_2_24), ToDoType((R.drawable.baseline_recycling_24)))


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)

        //get data from firebase
        getTaskFromFirebase()

        binding.addTaskBtn.setOnClickListener {

            if (frag != null)
                childFragmentManager.beginTransaction().remove(frag!!).commit()
            frag = ToDoDialogFragment()
            frag!!.setListener(this)

            frag!!.show(
                childFragmentManager,
                ToDoDialogFragment.TAG
            )
        }


        binding.logOutBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder
                .setTitle("Log out?")
                .setPositiveButton("Yes") { dialog, which ->
                    auth.signOut()
                    auth.currentUser?.let { user ->
                        user.delete()
                            .addOnCompleteListener { task ->
                                if (!task.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Failed to sign out",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } ?: run {
                        (activity as MainActivity).supportFragmentManager.findFragmentByTag("SignInFragment")
                            ?.let { fragment ->
                                if (fragment is SignInFragment) {
                                    fragment.clearSharedPreferences()
                                }
                            }
                        navController.navigate(R.id.action_homeFragment_to_signInFragment)
                    }
                }
                .setNegativeButton("No") { dialog, which ->
                }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        binding.goToLeaderboard.setOnClickListener {
            navController.navigate(R.id.action_homeFragment_to_leaderboardFragment)
        }
    }


    private fun getTaskFromFirebase() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoItemList.clear()
                for (taskSnapshot in snapshot.children) {
                    val taskData = taskSnapshot.value as HashMap<String, Any>
                    val taskId = taskSnapshot.key!! as? String
                    val todoTask = taskData["task"] as? String
                    val imageId = taskData["imageId"] as? String

                    if (taskId!= null && todoTask!= null && imageId!= null) {
                        val intImageId = imageId.toInt() // Convert imageId from string to int
                        toDoItemList.add(ToDoData(taskId, todoTask, intImageId))
                    }
                    else
                    {
                        Log.d(TAG, "There is a null parameter")
                    }
                }
                taskAdapter.notifyDataSetChanged() // Notify the adapter that the data has changed
            }

            override fun onCancelled(databaseError: DatabaseError) {
                if (isResumed) {
                    Log.e(TAG, "Failed to load tasks: " + databaseError.toString())
                    Toast.makeText(requireActivity(), "Failed to load tasks", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }


    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()

        auth = FirebaseAuth.getInstance()
        authId = auth.currentUser!!.uid
        database = Firebase.database.reference.child("users").child(authId).child("Tasks")

        binding.mainRecyclerView.setHasFixedSize(true)
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(context)
        toDoItemList = mutableListOf()
        taskAdapter = TaskAdapter(toDoItemList)
        taskAdapter.setListener(this)
        binding.mainRecyclerView.adapter = taskAdapter
    }


    override fun saveTask(todoTask: String, todoEdit: TextInputEditText) {
        val newToDo = ToDoData(UUID.randomUUID().toString(), todoTask, R.drawable.baseline_menu_24)
        val taskMap = hashMapOf<String, Any>(
            "taskId" to newToDo.taskId,
            "task" to newToDo.task,
            "imageId" to newToDo.imageId.toString() // Convert imageId to string
        )
        database.push().setValue(taskMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Task Added Successfully", Toast.LENGTH_SHORT).show()
                    todoEdit.text = null
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        frag!!.dismiss()
    }


    override fun updateTask(toDoData: ToDoData, todoEdit: TextInputEditText) {
        database.child(toDoData.taskId).child("task").setValue(todoEdit.text.toString())
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
                frag!!.dismiss()
            }
    }


    override fun onDeleteItemClicked(toDoData: ToDoData, position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage("Delete task?")
            .setPositiveButton("Yes") { dialog, which ->
                database.child(toDoData.taskId).removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("No") { dialog, which ->
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun updateScore() {
        val userRef = Firebase.database.reference.child("users").child(authId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    val newScore = user.score + 10
                    userRef.child("score").setValue(newScore)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Toast.makeText(
                    context,
                    "Failed to retrieve user data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    override fun onEditItemClicked(toDoData: ToDoData, position: Int) {
        if (frag != null)
            childFragmentManager.beginTransaction().remove(frag!!).commit()

        frag = ToDoDialogFragment.newInstance(toDoData.taskId, toDoData.task)
        frag!!.setListener(this)
        frag!!.show(
            childFragmentManager,
            ToDoDialogFragment.TAG
        )
    }


    override fun onMenuItemClicked(toDoData: ToDoData, position: Int, taskMenu: ImageView) {
        val dialog = BottomSheetDialog(requireContext())
        val dialogView = LayoutInflater.from(context).inflate(R.layout.task_type_grid, null, false)
        val gridView = dialogView.findViewById<GridView>(R.id.gridView)
        val gridAdapter = TaskTypesAdapter(requireContext(), toDoTypes)
        gridView.adapter = gridAdapter

        dialog.setContentView(dialogView)
        dialog.show()

        gridView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, typePosition, id ->

            val toDoType = toDoTypes[typePosition]
            database.child(toDoData.taskId).child("imageId").setValue(toDoType.imageResId.toString())
            taskAdapter.notifyDataSetChanged()
        }
    }


    override fun onDoneItemClicked(toDoData: ToDoData, position: Int) {
        database.child(toDoData.taskId).removeValue()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Well done! 10 POINTS!", Toast.LENGTH_SHORT).show()
                    updateScore()
                } else {
                    Toast.makeText(context, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }
}