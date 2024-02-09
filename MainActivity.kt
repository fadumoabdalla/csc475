package com.example.contactbook

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.MaterialTheme

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("ContactBook", Context.MODE_PRIVATE)

        setContent {
            MaterialTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ContactBookApp()
                }
            }
        }
    }

    @Composable
    fun ContactBookApp() {
        var contacts by remember { mutableStateOf(mapOf<String, String>()) }
        val nameState = remember { mutableStateOf("") }
        val phoneState = remember { mutableStateOf("") }

        // Load contacts when the app starts
        LaunchedEffect(key1 = true) {
            contacts = loadContacts()
        }

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = nameState.value,
                onValueChange = { nameState.value = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = phoneState.value,
                onValueChange = { phoneState.value = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    saveContact(nameState.value, phoneState.value)
                    nameState.value = ""
                    phoneState.value = ""
                    contacts = loadContacts() // Reload the contacts after saving
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Contact")
            }
            ContactList(contacts) { name ->
                deleteContact(name)
                contacts = loadContacts() // Reload the contacts after deletion
            }
        }
    }

    @Composable
    fun ContactList(contacts: Map<String, String>, deleteContact: (String) -> Unit) {
        LazyColumn(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 8.dp)) {
            items(contacts.keys.toList()) { name ->
                ContactItem(name = name, phone = contacts[name] ?: "", deleteContact)
            }
        }
    }

    @Composable
    fun ContactItem(name: String, phone: String, deleteContact: (String) -> Unit) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(text = "Name: $name", style = MaterialTheme.typography.subtitle1)
            Text(text = "Phone: $phone", style = MaterialTheme.typography.subtitle2)
            Button(onClick = { deleteContact(name) }) {
                Text("Delete")
            }
        }
    }

    @Composable
    @Preview(showBackground = true)
    fun DefaultPreview() {
        contactBookTheme {  }
    }

    private fun contactBookTheme(function: () -> Unit) {
        TODO("Not yet implemented")
    }

    private fun saveContact(name: String, phone: String) {
        with(sharedPreferences.edit()) {
            putString(name, phone)
            apply()
        }
    }

    private fun loadContacts(): Map<String, String> {
        return sharedPreferences.all.mapValues { it.value.toString() }
    }

    private fun deleteContact(name: String) {
        with(sharedPreferences.edit()) {
            remove(name)
            apply()
        }
    }
}


