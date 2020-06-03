package com.shimo3.roomsample_20200603

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Comment::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun CommentDao(): CommentDao
}