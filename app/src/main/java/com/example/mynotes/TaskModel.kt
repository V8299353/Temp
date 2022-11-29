package com.example.mynotes

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class TaskModel(

    var uuid:String? = null,
    var title:String? = null,
    var description: String? = null,
    var mapUrl:String? = null,
    var imageUrl:String? = null,
    var createdDate: Long? = null,
    var updatedDate:Long? = null

) : Serializable