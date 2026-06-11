package com.example.playermanager.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.playermanager.R
import com.example.playermanager.data.SportDao
import com.example.playermanager.databinding.ActivityDetailBinding
import com.example.playermanager.model.Sport

class DetailActivity : AppCompatActivity() {

    // Binding for the Detail Activity layout
    private lateinit var binding: ActivityDetailBinding
    private lateinit var sportDao: SportDao
    private var currentSport: Sport? = null

    private var selectedImageUri: android.net.Uri? = null

    private val pickImageLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.GetContent()) { uri: android.net.Uri? ->
        if (uri != null) {
            val savedUri = copyImageToInternalStorage(uri)
            if (savedUri != null) {
                selectedImageUri = savedUri
                binding.ivCoverPhoto.setImageURI(savedUri)
                binding.ivCoverPhoto.visibility = android.view.View.VISIBLE
                binding.llUploadPlaceholder.visibility = android.view.View.GONE
            } else {
                Toast.makeText(this, "Erreur lors de la copie de l'image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyImageToInternalStorage(uri: android.net.Uri): android.net.Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "sport_img_${System.currentTimeMillis()}.jpg"
            val file = java.io.File(filesDir, fileName)
            val outputStream = java.io.FileOutputStream(file)
            
            inputStream.copyTo(outputStream)
            
            inputStream.close()
            outputStream.close()
            
            android.net.Uri.fromFile(file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sportDao = SportDao(this)

        // Setup Category Dropdown to match filters
        val categories = arrayOf("Football", "Tennis", "Piscine", "Basketball", "Autre")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)

        // Check if editing
        currentSport = intent.getSerializableExtra("SPORT_DATA") as? Sport
        if (currentSport != null) {
            setupEditMode(currentSport!!)
        }

        setupListeners()
    }

    private fun setupEditMode(sport: Sport) {
        binding.tvFormTitle.text = "Modifier Sport"
        binding.etName.setText(sport.name)
        binding.actvCategory.setText(sport.location, false)
        binding.etDescription.setText(sport.description)
        binding.etOpening.setText(sport.openingTime)
        binding.etPrice.setText(sport.price.toString())
        
        sport.imageUri?.let { uriString ->
            if (uriString.isNotEmpty()) {
                try {
                    val uri = android.net.Uri.parse(uriString)
                    selectedImageUri = uri
                    binding.ivCoverPhoto.setImageURI(uri)
                    binding.ivCoverPhoto.visibility = android.view.View.VISIBLE
                    binding.llUploadPlaceholder.visibility = android.view.View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Fallback to placeholder if URI permission is lost
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnSaveSport.setOnClickListener {
            saveSport()
        }

        binding.btnCancelSport.setOnClickListener {
            finish()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        binding.cvUploadPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun saveSport() {
        val name = binding.etName.text.toString()
        val location = binding.actvCategory.text.toString()
        val description = binding.etDescription.text.toString()
        val opening = binding.etOpening.text.toString()
        val price = binding.etPrice.text.toString().toDoubleOrNull() ?: 0.0

        if (name.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un nom", Toast.LENGTH_SHORT).show()
            return
        }

        val sport = Sport(
            id = currentSport?.id ?: -1,
            name = name,
            location = location,
            description = description,
            openingTime = opening,
            price = price,
            imageUri = selectedImageUri?.toString() ?: currentSport?.imageUri
        )

        val result = if (currentSport == null) {
            sportDao.insert(sport)
        } else {
            sportDao.update(sport).toLong()
        }

        if (result != -1L) {
            Toast.makeText(this, "Sport enregistré avec succès", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
        }
    }
}
