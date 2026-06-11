package com.example.playermanager.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns
import com.example.playermanager.data.PlayerContract.SportEntry
import com.example.playermanager.model.Sport

class SportDao(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun insert(sport: Sport): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(SportEntry.COLUMN_NAME, sport.name)
            put(SportEntry.COLUMN_LOCATION, sport.location)
            put(SportEntry.COLUMN_DESCRIPTION, sport.description)
            put(SportEntry.COLUMN_OPENING, sport.openingTime)
            put(SportEntry.COLUMN_PRICE, sport.price)
            put(SportEntry.COLUMN_IMAGE, sport.imageUri)
        }
        return db.insert(SportEntry.TABLE_NAME, null, values)
    }

    fun getAllSports(): List<Sport> {
        val sports = mutableListOf<Sport>()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            SportEntry.COLUMN_NAME,
            SportEntry.COLUMN_LOCATION,
            SportEntry.COLUMN_DESCRIPTION,
            SportEntry.COLUMN_OPENING,
            SportEntry.COLUMN_PRICE,
            SportEntry.COLUMN_IMAGE
        )

        val cursor: Cursor = db.query(
            SportEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            "${SportEntry.COLUMN_NAME} ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val name = getString(getColumnIndexOrThrow(SportEntry.COLUMN_NAME))
                val location = getString(getColumnIndexOrThrow(SportEntry.COLUMN_LOCATION))
                val description = getString(getColumnIndexOrThrow(SportEntry.COLUMN_DESCRIPTION))
                val opening = getString(getColumnIndexOrThrow(SportEntry.COLUMN_OPENING))
                val price = getDouble(getColumnIndexOrThrow(SportEntry.COLUMN_PRICE))
                val image = getString(getColumnIndexOrThrow(SportEntry.COLUMN_IMAGE))
                sports.add(Sport(id, name, location, description, opening, price, image))
            }
        }
        cursor.close()
        return sports
    }

    fun update(sport: Sport): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(SportEntry.COLUMN_NAME, sport.name)
            put(SportEntry.COLUMN_LOCATION, sport.location)
            put(SportEntry.COLUMN_DESCRIPTION, sport.description)
            put(SportEntry.COLUMN_OPENING, sport.openingTime)
            put(SportEntry.COLUMN_PRICE, sport.price)
            put(SportEntry.COLUMN_IMAGE, sport.imageUri)
        }
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(sport.id.toString())
        return db.update(SportEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    fun delete(id: Long): Int {
        val db = dbHelper.writableDatabase
        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())
        return db.delete(SportEntry.TABLE_NAME, selection, selectionArgs)
    }
}
