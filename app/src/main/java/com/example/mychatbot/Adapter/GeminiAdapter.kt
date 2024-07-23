package com.example.mychatbot.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mychatbot.Custom.TypeWriter
import com.example.mychatbot.Model.DataResponse
import org.w3c.dom.Text

class GeminiAdapter(var context: Context, var list:ArrayList<DataResponse>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val GEMINI = 1
        const val USER = 0

    }
    private inner class GeminiViewHolder(item: View):RecyclerView.ViewHolder(item){
        var text1 :TypeWriter = item.findViewById(com.example.mychatbot.R.id.response)
        val image : ImageView = item.findViewById(com.example.mychatbot.R.id.resImage)

        fun bind(position: Int){
            val data = list[position]

            if(data.imageUri != null){
                image.visibility = View.VISIBLE
                Glide.with(context).load(data.imageUri).into(image)
            }
            if(list.size-1 == position){

                text1.animateText(data.prompt)
                text1.setCharacterDelay(30)
            }else{
                text1.setText(data.prompt)
            }
        }


    }
    private inner class UserViewHolder(item:View):RecyclerView.ViewHolder(item){
        var text:TextView = item.findViewById(com.example.mychatbot.R.id.userResponse)
        var imageView:ImageView = item.findViewById(com.example.mychatbot.R.id.userImage)
        fun bind(position: Int){
            val data = list[position]
            text.text = data.prompt
            if(data.imageUri.isNotEmpty()){
                imageView.setImageURI(Uri.parse(data.imageUri))
            }else{
                imageView.visibility = View.GONE
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == GEMINI){
            return GeminiViewHolder(LayoutInflater.from(context).inflate(com.example.mychatbot.R.layout.gemini_layout,parent,false))
        }else
        {
            return UserViewHolder(LayoutInflater.from(context).inflate(com.example.mychatbot.R.layout.user_layout,parent,false))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].isUser
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(list[position].isUser === GEMINI){
            (holder as GeminiViewHolder).bind(position)
        }else{
            (holder as UserViewHolder).bind(position)
        }
    }
}