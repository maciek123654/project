package com.example.myfarm2024.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.ImageButton
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.myfarm2024.R
import com.example.myfarm2024.database.FieldDatabaseHelper
import java.text.SimpleDateFormat
import java.util.*

class ExpenseFragment : Fragment() {

    private lateinit var databaseHelper: FieldDatabaseHelper
    private lateinit var editTextAmount: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerSubcategory: Spinner
    private lateinit var spinnerAccount: Spinner
    private lateinit var buttonAddExpense: Button
    private lateinit var editTextNewSubcategory: EditText

    private var accountsList: List<Pair<Int, String>> = listOf()
    private var selectedAccountId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_expense, container, false)

        databaseHelper = FieldDatabaseHelper(requireContext())
        editTextAmount = view.findViewById(R.id.editTextAmount)
        spinnerCategory = view.findViewById(R.id.spinnerCategory)
        spinnerSubcategory = view.findViewById(R.id.spinnerSubcategory)
        spinnerAccount = view.findViewById(R.id.spinnerAccount)
        buttonAddExpense = view.findViewById(R.id.buttonAddExpense)

        loadCategories()
        loadAccounts()

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                loadSubcategories(spinnerCategory.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        spinnerAccount.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedAccountId = accountsList[position].first
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        buttonAddExpense.setOnClickListener {
            addExpense()
        }

        return view
    }

    private fun loadCategories() {
        val categories = databaseHelper.getAllExpenseCategories().map { it.first }.distinct()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun loadSubcategories(category: String) {
        val subcategories = databaseHelper.getAllExpenseCategories()
            .filter { it.first == category }
            .map { it.second ?: "Brak podkategorii" }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subcategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSubcategory.adapter = adapter
    }

    private fun loadAccounts() {
        accountsList = databaseHelper.getAllAccounts()
        val accountNames = accountsList.map { it.second }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, accountNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerAccount.adapter = adapter

        if (accountsList.isNotEmpty()) {
            selectedAccountId = accountsList[0].first
        }
    }

    private fun addExpense() {
        val amount = editTextAmount.text.toString().toDoubleOrNull()
        val category = spinnerCategory.selectedItem.toString()
        val subcategory = spinnerSubcategory.selectedItem.toString()

        if (amount != null && category.isNotEmpty() && selectedAccountId != -1) {
            databaseHelper.addExpense(amount, category, subcategory, System.currentTimeMillis().toString(), selectedAccountId)
            Toast.makeText(requireContext(), "Dodano wydatek", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Podaj kwotę, wybierz kategorię i konto", Toast.LENGTH_SHORT).show()
        }
    }


}