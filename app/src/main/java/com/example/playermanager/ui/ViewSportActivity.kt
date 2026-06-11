package com.example.playermanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playermanager.R
import com.example.playermanager.databinding.ActivityViewSportBinding
import com.example.playermanager.model.Sport

class ViewSportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewSportBinding
    private var currentSport: Sport? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewSportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentSport = intent.getSerializableExtra("SPORT_DATA") as? Sport

        if (currentSport == null) {
            finish()
            return
        }

        populateData()
        setupListeners()
    }

    private fun populateData() {
        val sport = currentSport!!
        
        binding.tvTitle.text = sport.name
        binding.tvCategory.text = sport.location
        binding.tvLocation.text = sport.location
        binding.tvDescription.text = if (sport.description.isNotEmpty()) sport.description else "Aucune description fournie."
        binding.tvHours.text = if (sport.openingTime.isNotEmpty()) sport.openingTime else "Non spécifié"
        binding.tvPrice.text = if (sport.price > 0) "${sport.price} € / h" else "Gratuit"

        if (!sport.imageUri.isNullOrEmpty()) {
            try {
                binding.ivCover.setImageURI(android.net.Uri.parse(sport.imageUri))
            } catch (e: Exception) {
                e.printStackTrace()
                binding.ivCover.setImageResource(R.drawable.placeholder_sport)
            }
        } else {
            binding.ivCover.setImageResource(R.drawable.placeholder_sport)
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditSport.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("SPORT_DATA", currentSport)
            }
            startActivity(intent)
            finish() // Finish view so back from edit goes to main, or don't finish so it goes back to view
        }
    }
}
