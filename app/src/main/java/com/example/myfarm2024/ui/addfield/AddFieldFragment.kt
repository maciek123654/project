package com.example.myfarm2024.ui.addfield

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.myfarm2024.R
import com.example.myfarm2024.database.FieldDatabaseHelper
import com.example.myfarm2024.database.FieldData

class AddFieldFragment : Fragment() {

    private lateinit var editTextParcelId: EditText
    private lateinit var editTextParcelNumber: EditText
    private lateinit var editTextSurfaceArea: EditText
    private lateinit var spinnerLandClass: Spinner
    private lateinit var buttonAddField: Button
    private lateinit var editTextProvince: EditText
    private lateinit var editTextCounty: EditText
    private lateinit var editTextCommune: EditText
    private lateinit var editTextTown: EditText

    private lateinit var databaseHelper: FieldDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_add_field, container, false)

        // Inicjalizacja helpera bazy danych
        databaseHelper = FieldDatabaseHelper(requireContext())

        // Inicjalizacja widoków
        editTextParcelId = rootView.findViewById(R.id.editTextFieldId) // parcelId
        editTextParcelNumber = rootView.findViewById(R.id.editTextFieldNumber) // parcelNumber
        editTextSurfaceArea = rootView.findViewById(R.id.editTextFieldArea) // surfaceArea
        editTextProvince = rootView.findViewById(R.id.editTextProvince) // province
        editTextCounty = rootView.findViewById(R.id.editTextCounty) // county
        editTextCommune = rootView.findViewById(R.id.editTextCommune) //commune
        editTextTown = rootView.findViewById(R.id.editTextTown) // town
        spinnerLandClass = rootView.findViewById(R.id.spinnerLandClass)
        buttonAddField = rootView.findViewById(R.id.buttonSubmit)

        // Ustawienie nasłuchiwacza na przycisku
        buttonAddField.setOnClickListener {
            addField()
        }

        return rootView
    }

    private fun addField() {
        val parcelId = editTextParcelId.text.toString()
        val parcelNumber = editTextParcelNumber.text.toString()
        val surfaceAreaText = editTextSurfaceArea.text.toString()
        val surfaceArea = surfaceAreaText.toDoubleOrNull() ?: 0.0 // Ustawienie domyślnej wartości na 0.0 w przypadku błędnych danych

        val province = editTextProvince.text.toString()
        val county = editTextCounty.text.toString()
        val commune = editTextCommune.text.toString()
        val town = editTextTown.text.toString()
        val landClass = spinnerLandClass.selectedItem.toString()

        // Zapis do bazy danych
        val fieldData = FieldData(parcelId, parcelNumber, surfaceArea, province, county, commune, town, landClass)
        databaseHelper.addField(fieldData)

        // Wyczyszczenie formularza po dodaniu pola
        clearForm()
    }


    private fun clearForm() {
        editTextParcelId.text.clear()
        editTextParcelNumber.text.clear()
        editTextSurfaceArea.text.clear()
        editTextProvince.text.clear()
        editTextCounty.text.toString()
        editTextCommune.text.clear()
        editTextTown.text.clear()
        spinnerLandClass.setSelection(0) // Ustawienie pierwszej opcji jako domyślną
    }
}