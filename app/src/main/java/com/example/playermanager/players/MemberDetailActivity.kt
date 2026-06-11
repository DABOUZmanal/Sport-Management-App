package com.example.playermanager.players

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playermanager.R
import com.example.playermanager.databinding.ActivityMemberDetailBinding
import com.example.playermanager.model.Player

class MemberDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMemberDetailBinding
    private var currentPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMemberDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        currentPlayer = intent.getSerializableExtra(MembersListActivity.EXTRA_PLAYER_DATA) as? Player
        if (currentPlayer == null) {
            finish()
            return
        }

        populateData()
        setupListeners()
    }

    private fun populateData() {
        val player = currentPlayer!!

        binding.tvMemberName.text = player.fullName
        binding.tvMembershipLabel.text = membershipLabel(player.membershipType)
        binding.tvDetailFullName.text = player.fullName
        binding.tvDetailPhone.text = player.phone.ifEmpty { "—" }
        binding.tvDetailEmail.text = player.email.ifEmpty { "—" }
        binding.tvDetailNtrp.text = formatNtrpDisplay(player.ntrpLevel)

        val isPremium = player.membershipType.equals("Premium", ignoreCase = true)
        binding.badgePremium.visibility = if (isPremium) android.view.View.VISIBLE else android.view.View.GONE

        PlayerPhotoLoader.load(binding.ivProfile, player.photoUri)
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnEditMember.setOnClickListener {
            startActivity(Intent(this, EditPlayerActivity::class.java).apply {
                putExtra(MembersListActivity.EXTRA_PLAYER_ID, currentPlayer!!.id)
            })
        }
    }

    private fun membershipLabel(type: String): String {
        return if (type.equals("Premium", ignoreCase = true)) {
            getString(R.string.membership_premium)
        } else {
            getString(R.string.membership_standard)
        }
    }

    private fun formatNtrpDisplay(level: String): String {
        return if (level.contains("NTRP", ignoreCase = true)) level else "NTRP $level"
    }
}
