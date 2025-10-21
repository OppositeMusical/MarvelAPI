package com.example.marvelapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.squareup.picasso.Picasso
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    companion object {
        private val BASE_URL = "https://gateway.marvel.com/v1/public/characters"
        private val PUBLIC_KEY = "e5a7bf5a057dbd9bb3d478fc4519e3ec"
        private val PRIVATE_KEY = "9236c67e8af64cea1a527644d36fbc53dce51520"
    }
    private lateinit var heroImage: ImageView
    private lateinit var heroName: TextView
    private lateinit var heroDescription: TextView
    private lateinit var btnFetch: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        heroImage = findViewById(R.id.heroImage)
        heroName = findViewById(R.id.heroName)
        heroDescription = findViewById(R.id.heroDescription)
        btnFetch = findViewById(R.id.btnFetch)

        btnFetch.setOnClickListener { fetchRandomHero() }

        // Fetch one initially
        fetchRandomHero()
    }

    private fun fetchRandomHero() {
        val ts = System.currentTimeMillis().toString()
        val hash = md5(ts + PRIVATE_KEY + PUBLIC_KEY)
        val randomOffset = Random.nextInt(0, 1000)

        val url = "$BASE_URL?limit=1&offset=$randomOffset&ts=$ts&apikey=$PUBLIC_KEY&hash=$hash"

        val client = AsyncHttpClient()
        client.get(url, object : JsonHttpResponseHandler() {

            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>?,
                response: JSONObject?
            ) {
                try {
                    val results = response
                        ?.getJSONObject("data")
                        ?.getJSONArray("results")

                    if (results != null && results.length() > 0) {
                        val hero = results.getJSONObject(0)
                        val name = hero.getString("name")
                        val description = hero.getString("description")
                        val thumbnail = hero.getJSONObject("thumbnail")
                        val imageUrl = "${thumbnail.getString("path")}/standard_fantastic.${thumbnail.getString("extension")}"

                        heroName.text = name
                        heroDescription.text = if (description.isEmpty()) "No description available." else description
                        Picasso.get().load(imageUrl).into(heroImage)
                    } else {
                        heroName.text = getString(R.string.no_hero_found)
                        heroDescription.text = ""
                        heroImage.setImageDrawable(null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    heroName.text = getString(R.string.parsing_error)
                    heroDescription.text = e.localizedMessage
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                heroName.text = getString(R.string.parsing_error)
                heroDescription.text = throwable?.localizedMessage
            }
        })
    }

    private fun md5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            val bigInt = BigInteger(1, digest)
            var hashText = bigInt.toString(16)
            while (hashText.length < 32) {
                hashText = "0$hashText"
            }
            hashText
        } catch (e: Exception) {
            ""
        }
    }
}
