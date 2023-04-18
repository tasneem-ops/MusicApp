package com.example.musicapp.database


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import kotlin.time.Duration

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {
        // below is a sqlite query, where column names
        // along with their data types is given
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY ON CONFLICT IGNORE, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_URI + " TEXT, " + COLUMN_DURATION + " TEXT, " + COLUMN_ALBUM + " TEXT " + ")")

        // we are calling sqlite
        // method for executing our query
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    // This method is for adding data in our database
    fun addRecord(title : String, uri: String, duration: String, album : String, id : Long){

        // below we are creating
        // a content values variable
        val values = ContentValues()

        // we are inserting our values
        // in the form of key-value pair
        values.put(COLUMN_TITLE, title)
        values.put(COLUMN_ALBUM, album)
        values.put(COLUMN_DURATION, duration)
        values.put(COLUMN_URI, uri)
        values.put(COLUMN_ID, id)

        // here we are creating a
        // writable variable of
        // our database as we want to
        // insert value in our database
        val db = this.writableDatabase

        // all values are inserted into database
        db.insert(TABLE_NAME, null, values)

        // at last we are
        // closing our database
        db.close()
    }

    // below method is to get
    // all data from our database
    fun getRecords(): Cursor? {

        // here we are creating a readable
        // variable of our database
        // as we want to read value from it
        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null)

    }

    fun getSearchOutput(text : String): Cursor? {

        val db = this.readableDatabase

        // below code returns a cursor to
        // read data from the database
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_TITLE +" LIKE '%$text%'", null)

    }

    companion object{
        private val DATABASE_NAME = "Music"

        private val DATABASE_VERSION = 1

        val TABLE_NAME = "music_table"

        val COLUMN_ID = "id"

        val COLUMN_TITLE = "title"

        val COLUMN_DURATION = "duartion"

        val COLUMN_ALBUM = "album"
        val COLUMN_URI = "uri"

        val COLUMN_ORDER = "order"
    }
}
