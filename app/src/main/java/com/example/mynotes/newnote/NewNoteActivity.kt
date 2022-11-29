package com.example.mynotes.newnote

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.mynotes.R
import com.example.mynotes.TaskModel
import com.google.android.gms.tasks.Task

class NewNoteActivity : AppCompatActivity() {

    private val viewModel:NewNoteViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_note)

        var taskModel: TaskModel? = null
        var isNewTask = true

        isNewTask = intent.getBooleanExtra("IsNewTask",true)

        if(!isNewTask) {
            taskModel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("TaskModel",TaskModel::class.java)
            } else {
                intent.getSerializableExtra("TaskModel") as? TaskModel
            }
            viewModel.updateTaskModel(taskModel!!)
            viewModel.updateIsNewTask(false)
        } else {
            viewModel.updateIsNewTask(true)
        }

    }
}