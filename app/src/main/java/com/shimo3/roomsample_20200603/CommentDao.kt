package com.shimo3.roomsample_20200603

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CommentDao {
    @Query("SELECT * FROM comment;")
    fun getAll(): List<Comment>

    @Query("SELECT * FROM comment WHERE id=:id")
    fun getById(id: Int): Comment

    @Insert
    fun insert(comment: Comment)

    @Delete
    fun delete(comment: Comment)
}