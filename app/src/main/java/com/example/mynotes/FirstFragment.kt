package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynotes.databinding.FragmentFirstBinding
import com.example.mynotes.firestore.FirestoreHelper
import com.example.mynotes.newnote.NewNoteActivity
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot


class FirstFragment : Fragment(),OnNoteClick {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!
    private lateinit var tasksAdapter:TasksAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksAdapter = TasksAdapter(requireActivity(),this)
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = tasksAdapter

        fetchData()


        binding.fab.setOnClickListener {
            val intent = Intent(requireActivity(),NewNoteActivity::class.java)
            intent.putExtra("IsNewTask",true)
            startActivity(intent)
        }
    }

    private fun fetchData() {
        FirestoreHelper().getAllNotes().addSnapshotListener { value, error ->
            if(value != null) {
                tasksAdapter.submitList(value.toObjects(TaskModel::class.java))
            } else {
                Toast.makeText(requireContext(),"Error Fetching Documents",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNoteClicked(taskModel: TaskModel) {
        val intent = Intent(requireActivity(),NewNoteActivity::class.java)
        intent.putExtra("TaskModel",taskModel)
        intent.putExtra("IsNewTask",false)
        startActivity(intent)
    }
}

interface OnNoteClick {
    fun onNoteClicked(taskModel: TaskModel)
}