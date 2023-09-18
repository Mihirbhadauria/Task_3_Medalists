package com.example.task3a_medals

import Olympian
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task3_medalists.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_screen)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val dataList: List<Array<String>> = readCSV(R.raw.medallists)

        val olympiansList = dataList.map {
            val imageResId = when(it[1].toInt()) {
                in 0..5 -> R.drawable.bronze_medal_icon
                in 6..10 -> R.drawable.silver_medal_icon
                else -> R.drawable.gold_medal_icon
            }
            Olympian(it[0], it[1].toInt(), imageResId)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = OlympianAdapter(olympiansList)
    }

    private fun readCSV(resourceId: Int): List<Array<String>> {
        val resultList: MutableList<Array<String>> = ArrayList()
        val inputStream: InputStream = resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))

        // Skip the header row of data
        reader.readLine()

        reader.forEachLine { line ->
            val row = line.split(",").toTypedArray()
            // getting necessary data which is country and total medals
            val selectedColumns = arrayOf(row[0], row[3])
            resultList.add(selectedColumns)
        }

        try {
            inputStream.close()
        } catch (e: IOException) {
            throw RuntimeException("Error closing input stream: $e")
        }

        return resultList
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.last_clicked, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_last_clicked -> {
                startActivity(Intent(this, NewActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}


class OlympianAdapter(private val olympians: List<Olympian>) : RecyclerView.Adapter<OlympianAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)

        init {
            itemView.setOnClickListener {
                val olympian = olympians[adapterPosition]
                val toastMessage = "${olympian.name} won ${olympian.medalCount} medals"
                Toast.makeText(itemView.context, toastMessage, Toast.LENGTH_SHORT).show()

                // Saving to SharedPreferences
                val sharedPref = itemView.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putString("last_clicked_name", olympian.name)
                    putInt("last_clicked_medal_count", olympian.medalCount)
                    apply()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_olympian, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val olympian = olympians[position]
        holder.nameTextView.text = "${olympian.name} - ${olympian.medalCount}"
        holder.iconImageView.setImageResource(olympian.imageResId)
    }

    override fun getItemCount() = olympians.size
}



class NewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.last_clicked_layout)

        val sharedPref = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val name = sharedPref.getString("last_clicked_name", "N/A")
        val medalCount = sharedPref.getInt("last_clicked_medal_count", -1)

        val infoTextView: TextView = findViewById(R.id.infoTextView)
        infoTextView.text = "$name won $medalCount medals"
    }
}



