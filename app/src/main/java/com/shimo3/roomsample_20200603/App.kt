package com.shimo3.roomsample_20200603

import android.app.Application
import androidx.room.Room

class App: Application() {
    private lateinit var db: AppDatabase

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(applicationContext,
                                    AppDatabase::class.java,
                                "internal-db").build()

    }

    fun getDb(): AppDatabase {
        return db
    }
}