package com.example.snapchat

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*


class CreateSnap : AppCompatActivity() {


    var createSnapImageView: ImageView? = null;
    var messageEditText: EditText? = null;
    val imageName= UUID.randomUUID().toString()+".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        createSnapImageView = findViewById(R.id.createSnapImageView)
        messageEditText = findViewById(R.id.messageEditText)
    }

    //1.HERE WE HAVE CODED TO UPLOAD IMAGE 2. WITH FIREBASE INTEGRATION
    //1.
    fun getPhoto() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    fun chooseImageClicked(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            getPhoto()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                createSnapImageView?.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }

    //2.
        fun nextClicked(view: View) {
            createSnapImageView?.setDrawingCacheEnabled(true)
            createSnapImageView?.buildDrawingCache()
            val bitmap = createSnapImageView?.getDrawingCache()
            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

        val uploadTask: UploadTask = FirebaseStorage.getInstance().reference.child("images").child(imageName).putBytes(data)
        uploadTask.addOnFailureListener(OnFailureListener {
            Toast.makeText(applicationContext,"We could upload the image :(",Toast.LENGTH_SHORT).show()
        }).addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
            //taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
            Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()

            // if i want url of this image
            var url: String? = null
            val downloadUri = taskSnapshot.metadata?.reference?.downloadUrl
            downloadUri?.addOnCompleteListener(OnCompleteListener<Uri> { task ->
                if (task.isSuccessful) {
                    // Task completed successfully
                    val result = task.result
                    url = downloadUri.result.toString()
                    Log.i("Url", url!!)

                } else {
                    // Task failed with an exception
                    val exception = task.exception
                }
                val intent = Intent(this, ChooseUserActivity::class.java)
                intent.putExtra("imageURL", url)
                intent.putExtra("imageName", imageName)
                intent.putExtra("message", messageEditText?.text.toString())
                startActivity(intent)
            })

        })
                }

            }







//.addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
//    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//    val uploadSessionUri=taskSnapshot.uploadSessionUri
//    Log.i("URL", uploadSessionUri.toString())
//})


//
//var url: String = ""
//var task = it.storage.downloadUrl
//task.addOnSuccessListener {
//    url = task.getResult().toString()
//    Log.i("URL   ",url)