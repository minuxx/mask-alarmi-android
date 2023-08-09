package com.minux.mask_alarmi.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.nio.charset.StandardCharsets


class DBHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery =
            "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT, age INTEGER)"
        db.execSQL(createTableQuery)

        // JSON 파일에서 데이터 읽어오기 및 삽입
        insertDataFromJson(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades
    }

    private fun insertDataFromJson(db: SQLiteDatabase) {
        try {
            val inputStream = context.assets.open("sample_data.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val json = String(buffer, StandardCharsets.UTF_8)

            val jsonArray = JSONArray(json)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val id = jsonObject.getInt("id")
                val name = jsonObject.getString("name")
                val age = jsonObject.getInt("age")

                val insertQuery =
                    "INSERT INTO users (id, name, age) VALUES ($id, '$name', $age)"
                db.execSQL(insertQuery)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val DATABASE_NAME = "sample.db"
        private const val DATABASE_VERSION = 1
    }
}