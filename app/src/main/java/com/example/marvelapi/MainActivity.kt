package com.example.marvelapi

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.math.BigInteger
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var heroAdapter: HeroAdapter
    private val heroList = mutableListOf<Hero>()

    private val PUBLIC_KEY = "e5a7bf5a057dbd9bb3d478fc4519e3ec"
    private val PRIVATE_KEY = "9236c67e8af64cea1a527644d36fbc53dce51520"
    private val BASE_URL = "https://gateway.marvel.com/v1/public/characters"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerViewHeroes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        heroAdapter = HeroAdapter(heroList)
        recyclerView.adapter = heroAdapter

        val btnLoadHeroes: Button = findViewById(R.id.btnLoadHeroes)
        btnLoadHeroes.setOnClickListener { fetchMarvelHeroes() }
    }

    private fun fetchMarvelHeroes() {
        val ts = System.currentTimeMillis().toString()
        val hash = md5("$ts$PRIVATE_KEY$PUBLIC_KEY")
        val url = "$BASE_URL?ts=$ts&apikey=$PUBLIC_KEY&hash=$hash&limit=10"

        val client = AsyncHttpClient()
        client.get(url, object : AsyncHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, responseBody: ByteArray?) {
                val responseString = responseBody?.toString(Charsets.UTF_8)
                try {
                    val data = JSONObject(responseString)
                        .getJSONObject("data")
                        .getJSONArray("results")

                    heroList.clear()
                    for (i in 0 until data.length()) {
                        val item = data.getJSONObject(i)
                        val name = item.getString("name")
                        val desc = item.getString("description")
                        val thumbnail = item.getJSONObject("thumbnail")
                        val imageUrl = "${thumbnail.getString("path")}.${thumbnail.getString("extension")}"

                        heroList.add(Hero(name, desc, imageUrl))
                    }

                    heroAdapter.notifyDataSetChanged()
                } catch (e: Exception) {
                    Log.e("MarvelAPI", "Parsing error: ${e.message}")
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>?, responseBody: ByteArray?, error: Throwable?) {
                Log.e("MarvelAPI", "Request failed: $statusCode ${error?.message}")
            }
        })
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }
}
