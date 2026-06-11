package com.example.playermanager.data

import android.provider.BaseColumns

object PlayersContract {
    object PlayerEntry : BaseColumns {
        const val TABLE_NAME = "players"
        const val COLUMN_FULL_NAME = "full_name"
        const val COLUMN_PHONE = "phone"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_NTRP_LEVEL = "ntrp_level"
        const val COLUMN_SPORT_TYPE = "sport_type"
        const val COLUMN_JOIN_DATE = "join_date"
        const val COLUMN_MEMBERSHIP_TYPE = "membership_type"
        const val COLUMN_PHOTO_URI = "photo_uri"

        const val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_FULL_NAME TEXT," +
                    "$COLUMN_PHONE TEXT," +
                    "$COLUMN_EMAIL TEXT," +
                    "$COLUMN_NTRP_LEVEL TEXT," +
                    "$COLUMN_SPORT_TYPE TEXT," +
                    "$COLUMN_JOIN_DATE TEXT," +
                    "$COLUMN_MEMBERSHIP_TYPE TEXT," +
                    "$COLUMN_PHOTO_URI TEXT)"

        const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}
