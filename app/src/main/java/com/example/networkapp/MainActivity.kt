package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import android.content.Context
import android.content.SharedPreferences

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)
        sharedPreferences = getPreferences(Context.MODE_PRIVATE)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            val comic = numberEditText.text.toString().trim()
            if (comic.isNotEmpty()) {
                downloadComic(comic)
            } else {
                Toast.makeText(this, "Please enter a comic number", Toast.LENGTH_SHORT).show()
            }
        }

        loadComic()
    }

    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        val jsonObjectRequest = JsonObjectRequest(url,
            { response ->
                showComic(response)
                saveComic(response)
            },
            { error ->
                Toast.makeText(this, "Failed loading comic: ${error.message}", Toast.LENGTH_SHORT).show()
            })
        requestQueue.add(jsonObjectRequest)
    }

    private fun showComic(comicObject: JSONObject) {
        try {
            val title = comicObject.optString("title", "No title")
            val description = comicObject.optString("alt", "No description")
            val imageUrl = comicObject.optString("img", "")

            titleTextView.text = title
            descriptionTextView.text = description

            if (imageUrl.isNotEmpty()) {
                Picasso.get().load(imageUrl).into(comicImageView)
            } else {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error displaying comic: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveComic(comicObject: JSONObject) {
        with(sharedPreferences.edit()) {
            putString("title", comicObject.getString("title"))
            putString("description", comicObject.getString("alt"))
            putString("image", comicObject.getString("img"))
            apply()
        }

    }
    
    private fun loadComic() {
        val title = sharedPreferences.getString("title", "")
        val description = sharedPreferences.getString("description", "")
        val imageURL = sharedPreferences.getString("image", "")

        titleTextView.text = title
        descriptionTextView.text = description

        if (!imageURL.isNullOrEmpty()) {
            Picasso.get().load(imageURL).into(comicImageView)
        }
    }

}