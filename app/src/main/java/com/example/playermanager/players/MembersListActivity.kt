package com.example.playermanager.players

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playermanager.R
import com.example.playermanager.data.PlayerDao
import com.example.playermanager.databinding.ActivityMembersListBinding
import com.example.playermanager.databinding.BottomSheetManageMemberBinding
import com.example.playermanager.model.Player
import com.example.playermanager.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class MembersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMembersListBinding
    private lateinit var playerDao: PlayerDao
    private lateinit var adapter: PlayerAdapter
    private var allPlayers: List<Player> = emptyList()
    private var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerDao = PlayerDao(this)
        setupRecyclerView()
        setupListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        adapter = PlayerAdapter(
            emptyList(),
            onItemClick = { player -> openDetailScreen(player) },
            onItemLongClick = { player -> showManageSheet(player) }
        )
        binding.rvMembers.layoutManager = LinearLayoutManager(this)
        binding.rvMembers.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddMember.setOnClickListener {
            startActivity(Intent(this, AddPlayerActivity::class.java))
        }

        binding.etSearchMembers.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s?.toString() ?: ""
                applyFilters()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadData() {
        allPlayers = playerDao.getAllPlayers()
        updateDashboard(allPlayers)
        applyFilters()
    }

    private fun updateDashboard(players: List<Player>) {
        binding.tvStatPlayers.text = players.size.toString()
        binding.tvStatActive.text = playerDao.countActiveSubscriptions(players).toString()
        binding.tvStatSports.text = playerDao.countDistinctSports(players).toString()
    }

    private fun applyFilters() {
        var filtered = allPlayers
        if (searchQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                        it.sportType.contains(searchQuery, ignoreCase = true) ||
                        it.coachName.contains(searchQuery, ignoreCase = true)
            }
        }
        adapter.updateData(filtered)
    }

    private fun showManageSheet(player: Player) {
        val dialog = BottomSheetDialog(this)
        val sheetBinding = BottomSheetManageMemberBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.btnEditSheet.setOnClickListener {
            dialog.dismiss()
            openEditScreen(player)
        }

        sheetBinding.btnDeleteSheet.setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmation(player)
        }

        dialog.show()
    }

    private fun openDetailScreen(player: Player) {
        startActivity(Intent(this, MemberDetailActivity::class.java).apply {
            putExtra(EXTRA_PLAYER_DATA, player)
        })
    }

    private fun openEditScreen(player: Player) {
        startActivity(Intent(this, EditPlayerActivity::class.java).apply {
            putExtra(EXTRA_PLAYER_ID, player.id)
        })
    }

    private fun showDeleteConfirmation(player: Player) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_member, null)
        dialogView.findViewById<android.widget.TextView>(R.id.tv_delete_msg).text =
            getString(R.string.delete_member_msg, player.fullName)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<android.widget.Button>(R.id.btn_delete_confirm).setOnClickListener {
            playerDao.delete(player.id)
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

    companion object {
        const val EXTRA_PLAYER_DATA = "PLAYER_DATA"
        const val EXTRA_PLAYER_ID = "PLAYER_ID"
    }
}
