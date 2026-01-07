package com.duyvv.basecompose.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transaction",
    indices = [Index(value = ["categoryId", "typeId", "timeStamp", "timeAdded"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Int,
    val typeId: Int,
    val amount: Double,
    val description: String,
    val timeStamp: Long,
    val timeAdded: Long
)