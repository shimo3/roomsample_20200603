package com.shimo3.roomsample_20200603

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Comment (
    @PrimaryKey(autoGenerate = true) val id:Int,
    @ColumnInfo(name = "comment") val comment:String
)