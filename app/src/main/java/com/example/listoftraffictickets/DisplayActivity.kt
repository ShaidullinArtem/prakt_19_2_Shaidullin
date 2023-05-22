package com.example.listoftraffictickets

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import android.view.inputmethod.InputMethodManager

class DisplayActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var deleteButton: Button
    private lateinit var numberEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        listView = findViewById(R.id.list_view)
        deleteButton = findViewById(R.id.delete)
        numberEditText = findViewById(R.id.number_edit_text)
        typeEditText = findViewById(R.id.type_edit_text)

        val saveButton: Button = findViewById(R.id.add)
        saveButton.setOnClickListener {
            if (numberEditText.text.toString().isEmpty() || typeEditText.text.toString()
                    .isEmpty()
            ) {
                val i = Toast.makeText(this, "Введите номер и тип билета.",
                    Toast.LENGTH_SHORT)
                i.setGravity(Gravity.TOP, 0, 160)
                i.show()
                return@setOnClickListener
            }

            val number = numberEditText.text.toString()
            val type = typeEditText.text.toString()

            val tickets = SharedPreferencesHelper.loadTickets(this)
            if (tickets.any { it.number == number }) {
                val i = Toast.makeText(
                    this,
                    "Билет с номером $number уже существует.",
                    Toast.LENGTH_SHORT
                )
                i.setGravity(Gravity.TOP, 0, 160)
                i.show()
            } else {
                val ticket = Ticket(number, type)
                val updatedTickets = tickets.toMutableList()
                updatedTickets.add(ticket)
                SharedPreferencesHelper.saveTickets(this, updatedTickets)

                val i = Toast.makeText(this, "Билет № $number добавлен.",
                    Toast.LENGTH_SHORT)
                i.setGravity(Gravity.TOP, 0, 160)
                i.show()

                numberEditText.text = null
                typeEditText.text = null

                updateList()

                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(saveButton.windowToken, 0)
            }
        }

        adapter = createTicketAdapter()
        listView.adapter = adapter

        deleteButton.setOnClickListener {
            SharedPreferencesHelper.clearTickets(this)
            adapter.clear()
            adapter.notifyDataSetChanged()
            updateList()
        }

        val imageViewBackToMain: ImageView = findViewById(R.id.imageViewBackToMain)
        imageViewBackToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createTicketAdapter(): ArrayAdapter<String> {
        val tickets = SharedPreferencesHelper.loadTickets(this)
        val ticketData = tickets.map { "${it.number} - ${it.type}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ticketData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun updateList() {
        val tickets = SharedPreferencesHelper.loadTickets(this)
        val ticketData = tickets.map { "${it.number} - ${it.type}" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, ticketData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        listView.adapter = adapter
    }
}