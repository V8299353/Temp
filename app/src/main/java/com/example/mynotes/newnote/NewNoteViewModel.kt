package com.example.mynotes.newnote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mynotes.TaskModel
import com.google.android.gms.maps.model.LatLng

class NewNoteViewModel : ViewModel() {

    private var latLng = MutableLiveData<LatLng?>()

    private var editTaskModel:TaskModel? = null
    private var isNewTask:Boolean = true


    fun setLatLng(lg: LatLng?) {
        latLng.postValue(lg)
    }

    fun getLatLng():LiveData<LatLng?> {
        return latLng
    }

    fun updateTaskModel(taskModel: TaskModel) {
        editTaskModel = taskModel
    }

    fun getEditTaskMode(): TaskModel? {
        return  editTaskModel
    }

    fun updateIsNewTask(isNewTask:Boolean){
        this.isNewTask = isNewTask
    }

    fun isNewTask() : Boolean {
        return  isNewTask
    }
}