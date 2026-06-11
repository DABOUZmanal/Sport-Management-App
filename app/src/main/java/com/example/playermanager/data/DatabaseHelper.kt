package com.example.playermanager.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.playermanager.data.PlayerContract.SportEntry
import com.example.playermanager.data.PlayersContract.PlayerEntry

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SportEntry.SQL_CREATE_ENTRIES)
        db.execSQL(PlayerEntry.SQL_CREATE_ENTRIES)
        seedSportsIfEmpty(db)
        seedPlayersIfEmpty(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SportEntry.SQL_CREATE_IF_NOT_EXISTS)
        db.execSQL(PlayerEntry.SQL_CREATE_ENTRIES)
        seedSportsIfEmpty(db)
        seedPlayersIfEmpty(db)
    }

    companion object {
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "SportsClub.db"

        private fun seedSportsIfEmpty(db: SQLiteDatabase) {
            val cursor = db.rawQuery("SELECT COUNT(*) FROM ${SportEntry.TABLE_NAME}", null)
            val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()
            if (count > 0) return

            val samples = listOf(
                sample("Arena Fit Gym", "Autre", "Modern gym with full equipment.", "07:00 - 21:00", 30.0),
                sample("Club Tennis Prestige", "Tennis", "Premium tennis courts.", "08:00 - 22:00", 45.0),
                sample("Olympic Pool Center", "Piscine", "Olympic-size swimming pool.", "06:00 - 20:00", 25.0),
                sample("Central Football Field", "Football", "Full-size grass field.", "09:00 - 23:00", 40.0)
            )
            samples.forEach { db.insert(SportEntry.TABLE_NAME, null, it) }
        }

        private fun sample(
            name: String,
            location: String,
            description: String,
            opening: String,
            price: Double
        ): ContentValues = ContentValues().apply {
            put(SportEntry.COLUMN_NAME, name)
            put(SportEntry.COLUMN_LOCATION, location)
            put(SportEntry.COLUMN_DESCRIPTION, description)
            put(SportEntry.COLUMN_OPENING, opening)
            put(SportEntry.COLUMN_PRICE, price)
            put(SportEntry.COLUMN_IMAGE, null as String?)
        }

        private fun seedPlayersIfEmpty(db: SQLiteDatabase) {
            val cursor = db.rawQuery("SELECT COUNT(*) FROM ${PlayerEntry.TABLE_NAME}", null)
            val count = if (cursor.moveToFirst()) cursor.getInt(0) else 0
            cursor.close()
            if (count > 0) return

            val samples = listOf(
                player("Sarah Jenkins", "+1 555 0101", "sarah@club.com", "4.5", "Tennis", "Mar 2023", "Premium"),
                player("David Chen", "+1 555 0102", "david@club.com", "3.0", "Tennis", "Jan 2024", "Standard"),
                player("James Wilson", "+1 555 0103", "james@club.com", "4.0", "Football", "Jun 2022", "Premium"),
                player("Elena Rodriguez", "+1 555 0104", "elena@club.com", "3.5", "Swimming", "Sep 2023", "Standard")
            )
            samples.forEach { db.insert(PlayerEntry.TABLE_NAME, null, it) }
        }

        private fun player(
            name: String,
            phone: String,
            email: String,
            ntrp: String,
            sport: String,
            joinDate: String,
            membership: String
        ): ContentValues = ContentValues().apply {
            put(PlayerEntry.COLUMN_FULL_NAME, name)
            put(PlayerEntry.COLUMN_PHONE, phone)
            put(PlayerEntry.COLUMN_EMAIL, email)
            put(PlayerEntry.COLUMN_NTRP_LEVEL, ntrp)
            put(PlayerEntry.COLUMN_SPORT_TYPE, sport)
            put(PlayerEntry.COLUMN_JOIN_DATE, joinDate)
            put(PlayerEntry.COLUMN_MEMBERSHIP_TYPE, membership)
            put(PlayerEntry.COLUMN_PHOTO_URI, null as String?)
        }
    }
}
