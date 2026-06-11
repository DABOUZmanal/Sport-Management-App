package com.example.playermanager.players

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.playermanager.R
import com.example.playermanager.data.PlayerDao
import com.example.playermanager.databinding.ActivityEditPlayerBinding
import com.example.playermanager.model.Player
import java.io.File
import java.util.Calendar

class EditPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPlayerBinding
    private lateinit var playerDao: PlayerDao
    private var currentPlayer: Player? = null
    private var savedPhotoPath: String? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val path = PlayerPhotoLoader.saveToInternalStorage(this, uri)
            if (path != null) {
                savedPhotoPath = path
                binding.ivPlayerPhoto.setImageURI(Uri.fromFile(File(path)))
                binding.ivPlayerPhoto.visibility = android.view.View.VISIBLE
                binding.llUploadPlaceholder.visibility = android.view.View.GONE
            } else {
                Toast.makeText(this, R.string.toast_image_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerDao = PlayerDao(this)
        binding.tvFormTitle.text = getString(R.string.title_edit_player)

        val playerId = intent.getLongExtra(MembersListActivity.EXTRA_PLAYER_ID, -1)
        if (playerId == -1L) {
            finish()
            return
        }

        currentPlayer = playerDao.getPlayerById(playerId)
        if (currentPlayer == null) {
            finish()
            return
        }

        setupSpinners()
        setupDatePickers()
        bindPlayerToForm(currentPlayer!!)
        setupListeners()
    }

    private fun setupSpinners() {
        val sports = arrayOf("Tennis", "Football", "Basketball", "Natation", "Autre")
        binding.spinnerSportType.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sports)
        )
    }

    private fun setupDatePickers() {
        binding.etJoinDate.setOnClickListener { showDatePicker(binding.etJoinDate) }
        binding.etExpiryDate.setOnClickListener { showDatePicker(binding.etExpiryDate) }
    }

    private fun showDatePicker(target: com.google.android.material.textfield.TextInputEditText) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val m = month + 1
                target.setText(String.format("%02d/%02d/%04d", day, m, year))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun bindPlayerToForm(player: Player) {
        binding.etFullName.setText(player.fullName)
        binding.spinnerSportType.setText(player.sportType, false)
        binding.etPhone.setText(player.phone)
        binding.etEmail.setText(player.email)
        binding.etCoachName.setText(player.coachName)
        binding.etJoinDate.setText(player.joinDate)
        binding.etExpiryDate.setText(player.expiryDate)
        savedPhotoPath = player.photoUri

        if (!player.photoUri.isNullOrEmpty()) {
            PlayerPhotoLoader.load(binding.ivPlayerPhoto, player.photoUri)
            binding.ivPlayerPhoto.visibility = android.view.View.VISIBLE
            binding.llUploadPlaceholder.visibility = android.view.View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnCancelPlayer.setOnClickListener { finish() }
        binding.btnSavePlayer.setOnClickListener { updatePlayer() }
        binding.cvUploadPhoto.setOnClickListener { pickImageLauncher.launch("image/*") }
    }

    private fun updatePlayer() {
        val existing = currentPlayer ?: return
        val fullName = binding.etFullName.text.toString().trim()
        val expiry = binding.etExpiryDate.text.toString().trim()
        if (fullName.isEmpty()) {
            Toast.makeText(this, R.string.toast_name_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (expiry.isEmpty()) {
            Toast.makeText(this, R.string.toast_expiry_required, Toast.LENGTH_SHORT).show()
            return
        }

        val player = Player(
            id = existing.id,
            fullName = fullName,
            phone = binding.etPhone.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            ntrpLevel = "",
            sportType = binding.spinnerSportType.text.toString().trim(),
            joinDate = binding.etJoinDate.text.toString().trim(),
            membershipType = existing.membershipType,
            photoUri = savedPhotoPath ?: existing.photoUri,
            expiryDate = expiry,
            coachName = binding.etCoachName.text.toString().trim()
        )

        val rows = playerDao.update(player)
        if (rows > 0) {
            Toast.makeText(this, R.string.toast_player_saved, Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, R.string.toast_player_error, Toast.LENGTH_SHORT).show()
        }
    }
}
