package com.example.playermanager.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.example.playermanager.data.PlayersContract.PlayerEntry
import com.example.playermanager.model.Player
import com.example.playermanager.players.SubscriptionHelper

class PlayerDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    init {
        migratePlayerColumns()
    }

    /** Adds new columns without touching DatabaseHelper or PlayersContract. */
    private fun migratePlayerColumns() {
        val db = dbHelper.writableDatabase
        try {
            db.execSQL("ALTER TABLE ${PlayerEntry.TABLE_NAME} ADD COLUMN $COL_EXPIRY_DATE TEXT")
        } catch (_: Exception) {
        }
        try {
            db.execSQL("ALTER TABLE ${PlayerEntry.TABLE_NAME} ADD COLUMN $COL_COACH_NAME TEXT")
        } catch (_: Exception) {
        }
    }

    fun insert(player: Player): Long {
        val db = dbHelper.writableDatabase
        val values = contentValuesFrom(player)
        return db.insert(PlayerEntry.TABLE_NAME, null, values)
    }

    fun getAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            PlayerEntry.TABLE_NAME,
            projection(),
            null,
            null,
            null,
            null,
            "${PlayerEntry.COLUMN_FULL_NAME} ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                players.add(cursorToPlayer(this))
            }
        }
        cursor.close()
        return players
    }

    fun getPlayerById(id: Long): Player? {
        val db = dbHelper.readableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            PlayerEntry.TABLE_NAME,
            projection(),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var player: Player? = null
        if (cursor.moveToFirst()) {
            player = cursorToPlayer(cursor)
        }
        cursor.close()
        return player
    }

    fun update(player: Player): Int {
        val db = dbHelper.writableDatabase
        val values = contentValuesFrom(player)
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(player.id.toString())
        return db.update(PlayerEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.delete(PlayerEntry.TABLE_NAME, selection, selectionArgs)
    }

    fun countActiveSubscriptions(players: List<Player>): Int =
        players.count { SubscriptionHelper.isSubscriptionActive(it.expiryDate) }

    fun countDistinctSports(players: List<Player>): Int =
        players.map { it.sportType.trim() }.filter { it.isNotEmpty() }.distinct().size

    private fun projection(): Array<String> = arrayOf(
        BaseColumns._ID,
        PlayerEntry.COLUMN_FULL_NAME,
        PlayerEntry.COLUMN_PHONE,
        PlayerEntry.COLUMN_EMAIL,
        PlayerEntry.COLUMN_NTRP_LEVEL,
        PlayerEntry.COLUMN_SPORT_TYPE,
        PlayerEntry.COLUMN_JOIN_DATE,
        PlayerEntry.COLUMN_MEMBERSHIP_TYPE,
        PlayerEntry.COLUMN_PHOTO_URI,
        COL_EXPIRY_DATE,
        COL_COACH_NAME
    )

    private fun contentValuesFrom(player: Player): ContentValues =
        ContentValues().apply {
            put(PlayerEntry.COLUMN_FULL_NAME, player.fullName)
            put(PlayerEntry.COLUMN_PHONE, player.phone)
            put(PlayerEntry.COLUMN_EMAIL, player.email)
            put(PlayerEntry.COLUMN_NTRP_LEVEL, player.ntrpLevel)
            put(PlayerEntry.COLUMN_SPORT_TYPE, player.sportType)
            put(PlayerEntry.COLUMN_JOIN_DATE, player.joinDate)
            put(PlayerEntry.COLUMN_MEMBERSHIP_TYPE, player.membershipType)
            put(PlayerEntry.COLUMN_PHOTO_URI, player.photoUri)
            put(COL_EXPIRY_DATE, player.expiryDate)
            put(COL_COACH_NAME, player.coachName)
        }

    private fun cursorToPlayer(cursor: Cursor): Player {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val fullName = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_FULL_NAME))
        val phone = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PHONE)) ?: ""
        val email = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_EMAIL)) ?: ""
        val ntrpLevel = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_NTRP_LEVEL)) ?: ""
        val sportType = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_SPORT_TYPE)) ?: ""
        val joinDate = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_JOIN_DATE)) ?: ""
        val membershipType =
            cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_MEMBERSHIP_TYPE)) ?: ""
        val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(PlayerEntry.COLUMN_PHOTO_URI))
        val expiryDate = cursor.getStringOrEmpty(COL_EXPIRY_DATE)
        val coachName = cursor.getStringOrEmpty(COL_COACH_NAME)
        return Player(
            id, fullName, phone, email, ntrpLevel, sportType, joinDate,
            membershipType, photoUri, expiryDate, coachName
        )
    }

    private fun Cursor.getStringOrEmpty(column: String): String {
        val index = getColumnIndex(column)
        if (index < 0) return ""
        return getString(index) ?: ""
    }

    companion object {
        const val COL_EXPIRY_DATE = "expiry_date"
        const val COL_COACH_NAME = "coach_name"
    }
}
