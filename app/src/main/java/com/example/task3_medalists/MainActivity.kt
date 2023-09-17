package com.example.task3a_medals

import Olympian
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
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
                in 1..5 -> R.drawable.bronze_medal_icon
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

        // Skip the header row
        reader.readLine()

        reader.forEachLine { line ->
            val row = line.split(",").toTypedArray()
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
}


class OlympianAdapter(private val olympians: List<Olympian>) : RecyclerView.Adapter<OlympianAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
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


