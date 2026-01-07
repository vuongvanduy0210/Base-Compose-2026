package com.duyvv.basecompose.data.repository

import com.duyvv.basecompose.data.local.dao.TransactionDao
import com.duyvv.basecompose.domain.repository.TransactionRepository

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
) : TransactionRepository {

}