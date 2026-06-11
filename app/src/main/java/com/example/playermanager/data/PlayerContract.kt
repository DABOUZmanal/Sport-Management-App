package com.example.playermanager.data

import android.provider.BaseColumns

object PlayerContract {
    object SportEntry : BaseColumns {
        const val TABLE_NAME = "sports"
        const val COLUMN_NAME = "name"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_OPENING = "opening_time"
        const val COLUMN_PRICE = "price"
        const val COLUMN_IMAGE = "image_uri"
        
        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${COLUMN_NAME} TEXT," +
                    "${COLUMN_LOCATION} TEXT," +
                    "${COLUMN_DESCRIPTION} TEXT," +
                    "${COLUMN_OPENING} TEXT," +
                    "${COLUMN_PRICE} REAL," +
                    "${COLUMN_IMAGE} TEXT)"

        const val SQL_CREATE_IF_NOT_EXISTS =
            "CREATE TABLE IF NOT EXISTS ${TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${COLUMN_NAME} TEXT," +
                    "${COLUMN_LOCATION} TEXT," +
                    "${COLUMN_DESCRIPTION} TEXT," +
                    "${COLUMN_OPENING} TEXT," +
                    "${COLUMN_PRICE} REAL," +
                    "${COLUMN_IMAGE} TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${TABLE_NAME}"
    }
}
