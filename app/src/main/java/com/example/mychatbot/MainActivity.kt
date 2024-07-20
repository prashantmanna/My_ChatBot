package com.example.mychatbot

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.WindowInsetsAnimation
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.mychatbot.Adapter.GeminiAdapter
import com.example.mychatbot.ImageGeneration.ImageModel.ImageRequestBody
import com.example.mychatbot.ImageGeneration.ImageModel.generatedImage
import com.example.mychatbot.ImageGeneration.api.ApiService
import com.example.mychatbot.ImageGeneration.api.RetrofitClient
import com.example.mychatbot.Model.DataResponse
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URI

class MainActivity : AppCompatActivity() {
    lateinit var edt:EditText
    lateinit var btn:Button
    lateinit var img:ImageView
    lateinit var rec:RecyclerView
    lateinit var check:CheckBox
    var bitmap:Bitmap? = null
    private lateinit var imageUri : String

    var responseData = arrayListOf<DataResponse>()
    lateinit var adapter: GeminiAdapter
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            imageUri = uri.toString()
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
            img.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        edt = findViewById(R.id.editTextText)
        btn = findViewById(R.id.button)
        img = findViewById(R.id.imageView)
        rec = findViewById(R.id.recycler)
        rec.isNestedScrollingEnabled = false
        check = findViewById(R.id.check)


        img.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        adapter = GeminiAdapter(this,responseData)
        rec.adapter = adapter
        btn.setOnClickListener {

            if(edt.text != null && check.isChecked){
                val prompt = edt.text.toString()
                responseData.add(DataResponse(0,prompt,""))
                adapter.notifyDataSetChanged()


                val apiservice = RetrofitClient.instance.create(ApiService::class.java)
                val requestbody = ImageRequestBody(
                    providers = "stabilityai",
                    text = prompt,
                    resolution = "1792x1024",
                    n = "1"
                )


                apiservice.generateImage(requestbody).enqueue(object : Callback<generatedImage?> {
                    override fun onResponse(call: Call<generatedImage?>, response: Response<generatedImage?>) {
                        if (response.isSuccessful) {
                            val stabilityAi = response.body()?.stabilityai
                            val items = stabilityAi?.items
                            val imageUrl = items?.get(0)?.image_resource_url

                            if (imageUrl != null) {
                                responseData.add(DataResponse(1, "Here is image", imageUrl))
                                adapter.notifyDataSetChanged()
                            } else {
                                Log.e("MainActivity", "Image URL is null")
                                // Optionally, you can add a placeholder or error message to responseData here
                            }
                        } else {
                            Log.e("MainActivity", "Response is not successful")
                            // Handle the case where the response is not successful
                        }
                    }

                    override fun onFailure(call: Call<generatedImage?>, t: Throwable) {
                        Log.e("MainActivity", "Request failed", t)
                        // Handle the failure case
                    }
                })




            }


            else if(edt.text != null){
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    // Access your API key as a Build Configuration variable (see "Set up your API key" above)
                    apiKey = "AIzaSyA5PSzOqRDeHzF6nfzBvm8yppld6FPMeUo"
                )

                var prompt = edt.text.toString()
                edt.setText("")

                if(bitmap != null){
                    responseData.add(DataResponse(0,prompt, imageUri = imageUri))
                    adapter.notifyDataSetChanged()

                    val inputContent = content {
                        image(bitmap!!)
                        text(prompt)
                    }

                    GlobalScope.launch {
                        val response = generativeModel.generateContent(inputContent)
                        runOnUiThread{
                            responseData.add(DataResponse(1,response.text!!,""))
                            adapter.notifyDataSetChanged()
                        }

                    }

                }else{

                    responseData.add(DataResponse(0,prompt,""))
                    adapter.notifyDataSetChanged()

                    GlobalScope.launch {
                        bitmap = null
                        imageUri = ""
                        val response = generativeModel.generateContent(prompt)
                        runOnUiThread {
                            responseData.add(DataResponse(1,response.text!!,""))
                            adapter.notifyDataSetChanged()
                        }

                    }

                }
            }


        }

    }
}