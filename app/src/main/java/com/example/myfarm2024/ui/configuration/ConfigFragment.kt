package com.example.myfarm2024.ui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.myfarm2024.R
import androidx.fragment.app.Fragment
import com.example.myfarm2024.database.FieldDatabaseHelper

class ConfigFragment : Fragment() {

    private lateinit var databaseHelper: FieldDatabaseHelper
    private lateinit var buttonShowAddAccountDialog: Button
    private lateinit var buttonShowSubcategoryDialog: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_config, container, false)

        databaseHelper = FieldDatabaseHelper(requireContext())
        buttonShowAddAccountDialog = view.findViewById(R.id.buttonShowAddAccountDialog)
        buttonShowSubcategoryDialog = view.findViewById(R.id.buttonShowAddSubcategoryDialog)

        buttonShowAddAccountDialog.setOnClickListener {
            showAddAccountDialog()
        }

        buttonShowSubcategoryDialog.setOnClickListener {
            showAddSubcategoryDialog()
        }

        return view
    }

    private fun showAddAccountDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_account, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Dodaj nowe konto")
            .setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val editTextAccountName = dialogView.findViewById<EditText>(R.id.editTextAccountName)
        val editTextInitialBalance = dialogView.findViewById<EditText>(R.id.editTextInitialBalance)
        val spinnerAccountType = dialogView.findViewById<Spinner>(R.id.spinnerAccountType)
        val buttonAddNewAccount = dialogView.findViewById<Button>(R.id.buttonAddNewAccount)

        // Ustaw adapter dla Spinnera z listą typów kont
        val accountTypes = arrayOf("Konto gotówkowe", "Konto bankowe") // Możesz rozszerzyć listę według potrzeb
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccountType.adapter = adapter

        buttonAddNewAccount.setOnClickListener {
            val accountName = editTextAccountName.text.toString().trim()
            val accountType = spinnerAccountType.selectedItem.toString()
            val initialBalance = editTextInitialBalance.text.toString().toDoubleOrNull() ?: 0.0

            if (accountName.isNotEmpty() && accountType.isNotEmpty()) {
                databaseHelper.addAccount(accountName, accountType, initialBalance)
                alertDialog.dismiss()
                Toast.makeText(requireContext(), "Dodano nowe konto", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Podaj nazwę i typ konta", Toast.LENGTH_SHORT).show()
            }
        }

        alertDialog.show()
    }

    private fun showAddSubcategoryDialog(){
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_subcategory, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Dodaj nową podkategorię")
            .setCancelable(true)

        val alertDialog = dialogBuilder.create()

        val spinnerCategory = dialogView.findViewById<Spinner>(R.id.spinnerCategory)
        val editTextNewSubcategory = dialogView.findViewById<EditText>(R.id.editTextNewSubcategory)
        val buttonAddNewSubcategory = dialogView.findViewById<Button>(R.id.buttonAddNewSubcategory)

        loadCategories(spinnerCategory)

        buttonAddNewSubcategory.setOnClickListener {
            val newSubcategory = editTextNewSubcategory.text.toString().trim()
            val category = spinnerCategory.selectedItem?.toString() ?: ""

            if (newSubcategory.isNotEmpty() && category.isNotEmpty()){
                databaseHelper.addExpenseSubcategory(category, newSubcategory)
                alertDialog.dismiss()
                Toast.makeText(requireContext(), "Dodano nową podkategorię", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Podaj nazwę nowej podkategorii i wybirz kategorię główną", Toast.LENGTH_LONG).show()
            }
        }

        alertDialog.show()
    }

    private fun loadCategories(spinner: Spinner){
        val categories = databaseHelper.getAllExpenseCategories().map {it.first}.distinct()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}