package com.example.myfarm2024.ui.configuration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.example.myfarm2024.R
import androidx.fragment.app.Fragment
import com.example.myfarm2024.database.FieldDatabaseHelper

class ConfigFragment : Fragment() {
    private lateinit var dbHelper: FieldDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_config, container, false)

        dbHelper = FieldDatabaseHelper(requireContext())

        val editTextAccountName = view.findViewById<EditText>(R.id.editTextAccountName)
        val spinnerAccountType = view.findViewById<Spinner>(R.id.spinnerAccountType)
        val buttonAddAccount = view.findViewById<Button>(R.id.buttonAddAccount)

        buttonAddAccount.setOnClickListener {
            val accountName = editTextAccountName.text.toString()
            val accountType = spinnerAccountType.selectedItem.toString()

            if (accountName.isNotEmpty()){
                dbHelper.addAccount(accountName, accountType)
                editTextAccountName.text.clear()
                spinnerAccountType.setSelection(0)
            }
        }

        return view
    }
}