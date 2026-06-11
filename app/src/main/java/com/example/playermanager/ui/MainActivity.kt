package com.example.playermanager.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playermanager.R
import com.example.playermanager.data.SportDao
import com.example.playermanager.databinding.ActivityMainBinding
import com.example.playermanager.databinding.BottomSheetManageBinding
import com.example.playermanager.model.Sport
import com.example.playermanager.players.MembersListActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sportDao: SportDao
    private lateinit var adapter: SportAdapter
    private var allSports: List<Sport> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sportDao = SportDao(this)
        setupRecyclerView()
        setupListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = SportAdapter(listOf(), 
            onItemClick = { sport -> openViewScreen(sport) },
            onItemLongClick = { sport -> showManageSheet(sport) }
        )
        binding.rvSports.layoutManager = LinearLayoutManager(this)
        binding.rvSports.adapter = adapter
    }

    private var currentSearchQuery = ""
    private var currentCategory = "Tous"

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentSearchQuery = s.toString()
                applyFilters()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setupCategoryFilters()

        binding.navMembers.setOnClickListener {
            startActivity(Intent(this, MembersListActivity::class.java))
        }
    }

    private fun setupCategoryFilters() {
        val chips = listOf(
            binding.chipAll to "Tous",
            binding.chipFootball to "Football",
            binding.chipTennis to "Tennis",
            binding.chipPiscine to "Piscine"
        )

        chips.forEach { (chip, category) ->
            chip.setOnClickListener {
                // Update state
                currentCategory = category
                
                // Update UI for all chips
                chips.forEach { (c, cat) ->
                    if (cat == currentCategory) {
                        c.setBackgroundResource(R.drawable.bg_nav_active)
                        c.setTextColor(resources.getColor(R.color.primary_dark, null))
                        c.setTypeface(null, android.graphics.Typeface.BOLD)
                    } else {
                        c.setBackgroundResource(R.drawable.bg_tag)
                        c.setTextColor(resources.getColor(R.color.text_secondary, null))
                        c.setTypeface(null, android.graphics.Typeface.NORMAL)
                    }
                }

                // Apply the filters
                applyFilters()
            }
        }
    }

    private fun loadData() {
        allSports = sportDao.getAllSports()
        applyFilters()
    }

    private fun applyFilters() {
        var filteredList = allSports

        // 1. Filter by Search Query
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { 
                it.name.contains(currentSearchQuery, ignoreCase = true) ||
                it.location.contains(currentSearchQuery, ignoreCase = true)
            }
        }

        // 2. Filter by Category Chip
        if (currentCategory != "Tous") {
            filteredList = filteredList.filter {
                it.name.contains(currentCategory, ignoreCase = true) ||
                it.location.contains(currentCategory, ignoreCase = true)
            }
        }

        adapter.updateData(filteredList)
    }

    private fun showManageSheet(sport: Sport) {
        val dialog = BottomSheetDialog(this)
        val sheetBinding = BottomSheetManageBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.btnEditSheet.setOnClickListener {
            dialog.dismiss()
            openEditScreen(sport)
        }

        sheetBinding.btnDeleteSheet.setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmation(sport)
        }

        dialog.show()
    }

    private fun openViewScreen(sport: Sport) {
        val intent = Intent(this, ViewSportActivity::class.java).apply {
            putExtra("SPORT_DATA", sport)
        }
        startActivity(intent)
    }

    private fun openEditScreen(sport: Sport) {
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("SPORT_DATA", sport)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmation(sport: Sport) {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null)
        builder.setView(dialogView)
        
        val alertDialog = builder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        

        dialogView.findViewById<android.widget.Button>(R.id.btn_delete_confirm).setOnClickListener {
            sportDao.delete(sport.id)
            loadData()
            alertDialog.dismiss()
        }
        
        dialogView.findViewById<android.widget.Button>(R.id.btn_cancel_confirm).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
