package com.example.mynotes.newnote

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mynotes.FirstFragment
import com.example.mynotes.R
import com.example.mynotes.TaskModel
import com.example.mynotes.auth.AuthenticationHelper
import com.example.mynotes.auth.LoginActivity
import com.example.mynotes.databinding.FragmentNewNoteBinding
import com.example.mynotes.firestore.FirestoreHelper
import com.example.mynotes.helper.ProgressBarHandler
import com.example.mynotes.storage.StorageHelper
import java.util.*


class NewNoteFragment : Fragment() {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewNoteViewModel by activityViewModels()
    private var imageUri: Uri? = null
    private var lastLatLng: String? = null
    private var oldTask:TaskModel? = null
    var processBarHandler :ProgressBarHandler? =null
    private lateinit var storageHelper: StorageHelper
    private lateinit var authenticationHelper: AuthenticationHelper
    private lateinit var firestoreHelper: FirestoreHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        storageHelper = StorageHelper()
        authenticationHelper = AuthenticationHelper()
        firestoreHelper = FirestoreHelper()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        processBarHandler = ProgressBarHandler(requireContext())

        binding.mapCta.setOnClickListener {
            requireActivity().supportFragmentManager.commit {
                addToBackStack("NewNoteFrag")
                add(R.id.newNoteFragContainerView, MapsFragment.newInstance())
            }
        }

        binding.newNoteImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            imageLauncher.launch(Intent.createChooser(intent, "Select Image"))
        }

        if(!viewModel.isNewTask()) {
            oldTask = viewModel.getEditTaskMode()!!
            binding.noteTitle.setText(oldTask!!.title)
            binding.noteDesc.setText(oldTask!!.description)
            oldTask!!.imageUrl?.let {
                Glide.with(requireContext())
                    .load(oldTask!!.imageUrl)
                    .apply(RequestOptions().fitCenter())
                    .placeholder(R.drawable.ic_baseline_add_a_photo_24)
                    .error(R.drawable.ic_baseline_add_a_photo_24)
                    .into(binding.newNoteImage)
            }
            oldTask!!.mapUrl?.let {
                binding.mapCta.text = oldTask!!.mapUrl
                lastLatLng = oldTask!!.mapUrl
            }
            binding.deleteNoteCta.visibility = View.VISIBLE
        } else {
            binding.deleteNoteCta.visibility = View.GONE
        }

        viewModel.getLatLng().observe(requireActivity()) {
            it?.let {
                lastLatLng = "https://www.google.com/maps/place/${it.latitude},${it.longitude}"
                binding.mapCta.text = lastLatLng
            }
        }

        binding.deleteNoteCta.setOnClickListener {
            processBarHandler?.show()
           oldTask?.imageUrl?.let {
               storageHelper.deleteImageWithUrl(it)
               firestoreHelper.deleteNotes(oldTask!!.uuid!!).addOnCompleteListener { task1->
                   if(task1.isSuccessful) {
                       Toast.makeText(requireContext(),"Note deleted Successfully",Toast.LENGTH_LONG).show()
                   } else {
                       Toast.makeText(requireContext(),"Note delete failed",Toast.LENGTH_LONG).show()
                   }
                   processBarHandler?.hide()
                   requireActivity().finish()
               }
           } ?: run {
               firestoreHelper.deleteNotes(oldTask!!.uuid!!).addOnCompleteListener {  task2 ->
                   if(task2.isSuccessful) {
                       Toast.makeText(requireContext(),"Note deleted Successfully",Toast.LENGTH_LONG).show()
                   } else {
                       Toast.makeText(requireContext(),"Note delete failed",Toast.LENGTH_LONG).show()
                   }
                   processBarHandler?.hide()
                   requireActivity().finish()
               }
           }
        }

        binding.saveNoteCta.setOnClickListener {
            if(!authenticationHelper.isUserLoggedIn()) {
                val i = Intent(requireActivity(), LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            } else if (binding.noteTitle.text.isNullOrEmpty() || binding.noteDesc.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Title or Description Cannot be empty", Toast.LENGTH_LONG).show()
            } else if(viewModel.isNewTask()) {
                processBarHandler?.show()
                val uuid = UUID.randomUUID().toString()
                imageUri?.let {
                    storageHelper.uploadNotesImage(it, uuid)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var downloadUri:String?
                            storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser()?.uid!!,uuid).downloadUrl.addOnCompleteListener { urlTask ->
                                if(urlTask.isSuccessful) {
                                    downloadUri = urlTask.result.toString()
                                    val taskModel = TaskModel(uuid,binding.noteTitle.text.toString(),binding.noteDesc.text?.toString(),lastLatLng,downloadUri,System.currentTimeMillis())
                                    firestoreHelper.addNewDocument(taskModel,uuid).addOnCompleteListener {  task1 ->
                                        if(task1.isSuccessful) {
                                            Toast.makeText(requireContext(),"Note Added Successfully",Toast.LENGTH_LONG).show()
                                            requireActivity().finish()
                                        } else {
                                            storageHelper.deleteImageWithReference(storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser()?.uid!!,uuid))
                                            Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show()
                                        }
                                        processBarHandler?.hide()
                                    }
                                } else {
                                    Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show()
                                    processBarHandler?.hide()
                                }
                            }

                        } else {
                            Toast.makeText(requireContext(),"Note Failed To add",Toast.LENGTH_LONG).show()
                            processBarHandler?.hide()
                        }
                    }
                } ?: run {
                    val taskModel = TaskModel(uuid,binding.noteTitle.text.toString(),binding.noteDesc.text?.toString(),lastLatLng,null,System.currentTimeMillis())
                    firestoreHelper.addNewDocument(taskModel,uuid).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            Toast.makeText(requireContext(), "Note Added Successfully", Toast.LENGTH_LONG).show()
                            requireActivity().finish()
                        } else {
                            Toast.makeText(requireContext(), "Note Failed to add", Toast.LENGTH_LONG).show()
                        }
                        processBarHandler?.hide()
                    }
                }
            } else {
                processBarHandler?.show()
               imageUri?.let {
                    storageHelper.uploadNotesImage(it, oldTask!!.uuid!!)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            var downloadUri: String?
                            storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser()?.uid!!,oldTask!!.uuid!!).downloadUrl.addOnCompleteListener { urlTask ->
                                if(urlTask.isSuccessful) {
                                    downloadUri = urlTask.result.toString()
                                    val taskModel = TaskModel(oldTask!!.uuid,binding.noteTitle.text.toString(),binding.noteDesc.text?.toString(),lastLatLng,downloadUri,oldTask!!.createdDate,System.currentTimeMillis())
                                    firestoreHelper.addNewDocument(taskModel,oldTask!!.uuid!!).addOnCompleteListener {  task1 ->
                                        if(task1.isSuccessful) {
                                            Toast.makeText(requireContext(),"Note Added Successfully",Toast.LENGTH_LONG).show()
                                            requireActivity().finish()
                                        } else {
                                            storageHelper.deleteImageWithReference(storageHelper.getNotesImageStorageReference(authenticationHelper.getCurrentUser()?.uid!!,oldTask!!.uuid!!))
                                            Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show()
                                        }
                                        processBarHandler?.hide()
                                    }
                                } else {
                                    Toast.makeText(requireContext(),"Note Failed to add",Toast.LENGTH_LONG).show()
                                    processBarHandler?.hide()
                                }
                            }

                        } else {
                            Toast.makeText(requireContext(),"Note Failed To add",Toast.LENGTH_LONG).show()
                            processBarHandler?.hide()
                        }
                    }
                } ?: run {
                   val taskModel = TaskModel(oldTask!!.uuid,binding.noteTitle.text.toString(),binding.noteDesc.text?.toString(),lastLatLng,oldTask!!.imageUrl,oldTask!!.createdDate,System.currentTimeMillis())
                   firestoreHelper.addNewDocument(taskModel,oldTask!!.uuid!!).addOnCompleteListener { task2 ->
                       if (task2.isSuccessful) {
                           Toast.makeText(requireContext(), "Note Added Successfully", Toast.LENGTH_LONG).show()
                           requireActivity().finish()
                       } else {
                           Toast.makeText(requireContext(), "Note Failed to add", Toast.LENGTH_LONG).show()
                       }
                       processBarHandler?.hide()
                   }
               }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private val imageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data?.data != null) {
            Glide.with(this)
                .load(result.data!!.data)
                .apply(RequestOptions().fitCenter())
                .error(R.drawable.ic_baseline_add_a_photo_24)
                .placeholder(R.drawable.ic_baseline_add_a_photo_24)
                .into(binding.newNoteImage)
            imageUri = result.data!!.data
        } else {
            Toast.makeText(requireContext(), "Error loading Image", Toast.LENGTH_LONG).show()
        }
    }


}