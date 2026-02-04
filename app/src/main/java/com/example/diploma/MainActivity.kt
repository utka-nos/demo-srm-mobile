package com.example.diploma

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var db: AppDatabase
    private val itemsList = mutableListOf<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = AppDatabase.getDatabase(this)

        val inputField: EditText = findViewById(R.id.inputField)
        val submitButton: Button = findViewById(R.id.submitButton)
        val itemsListView: ListView = findViewById(R.id.itemsListView)

        val adapter = object : ArrayAdapter<Item>(this, R.layout.list_item, R.id.itemText, itemsList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
                val itemText: TextView = view.findViewById(R.id.itemText)
                
                val item = getItem(position)
                itemText.text = item?.text

                deleteButton.setOnClickListener {
                    item?.let {
                        lifecycleScope.launch {
                            db.itemDao().delete(it)
                        }
                    }
                }
                return view
            }
        }

        itemsListView.adapter = adapter

        // Подписка на данные из БД
        lifecycleScope.launch {
            db.itemDao().getAll().collect { items ->
                itemsList.clear()
                itemsList.addAll(items)
                adapter.notifyDataSetChanged()
            }
        }

        submitButton.setOnClickListener {
            val text = inputField.text.toString()
            if (text.isNotBlank()) {
                lifecycleScope.launch {
                    db.itemDao().insert(Item(text = text))
                    inputField.text.clear()
                }
            }
        }
    }
}
