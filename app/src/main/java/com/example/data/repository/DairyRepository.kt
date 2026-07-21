package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class DairyRepository(
    private val db: AppDatabase
) {
    val allCattle: Flow<List<CattleEntity>> = db.cattleDao().getAllCattle()
    val activeCattle: Flow<List<CattleEntity>> = db.cattleDao().getActiveCattle()
    val activeCattleCount: Flow<Int> = db.cattleDao().getActiveCount()
    val lactatingCattleCount: Flow<Int> = db.cattleDao().getLactatingCount()

    val allMilkLogs: Flow<List<MilkLogEntity>> = db.milkLogDao().getAllMilkLogs()
    val allHealthLogs: Flow<List<HealthLogEntity>> = db.healthLogDao().getAllHealthLogs()
    val activeWithdrawalLogs: Flow<List<HealthLogEntity>> = db.healthLogDao().getActiveWithdrawalLogs()

    val allBreedingLogs: Flow<List<BreedingLogEntity>> = db.breedingLogDao().getAllBreedingLogs()
    val pregnantCattleLogs: Flow<List<BreedingLogEntity>> = db.breedingLogDao().getPregnantCattleLogs()

    val allFeedInventory: Flow<List<FeedInventoryEntity>> = db.feedDao().getAllFeedInventory()
    val lowStockFeed: Flow<List<FeedInventoryEntity>> = db.feedDao().getLowStockFeed()

    val allFinancialLogs: Flow<List<FinancialLogEntity>> = db.financialDao().getAllFinancialLogs()
    val totalIncome: Flow<Double?> = db.financialDao().getTotalIncome()
    val totalExpense: Flow<Double?> = db.financialDao().getTotalExpense()

    fun getMilkLogsForDate(date: String): Flow<List<MilkLogEntity>> = db.milkLogDao().getMilkLogsForDate(date)
    fun getTotalMilkForDate(date: String): Flow<Double?> = db.milkLogDao().getTotalMilkForDate(date)
    fun getMilkLogsForCattle(tag: String): Flow<List<MilkLogEntity>> = db.milkLogDao().getMilkLogsForCattle(tag)
    fun getHealthLogsForCattle(tag: String): Flow<List<HealthLogEntity>> = db.healthLogDao().getHealthLogsForCattle(tag)
    fun getBreedingLogsForCattle(tag: String): Flow<List<BreedingLogEntity>> = db.breedingLogDao().getBreedingLogsForCattle(tag)

    suspend fun insertCattle(cattle: CattleEntity): Long = db.cattleDao().insertCattle(cattle)
    suspend fun updateCattle(cattle: CattleEntity) = db.cattleDao().updateCattle(cattle)
    suspend fun deleteCattle(cattle: CattleEntity) = db.cattleDao().deleteCattle(cattle)

    suspend fun insertMilkLog(log: MilkLogEntity): Long = db.milkLogDao().insertMilkLog(log)
    suspend fun deleteMilkLog(log: MilkLogEntity) = db.milkLogDao().deleteMilkLog(log)

    suspend fun insertHealthLog(log: HealthLogEntity): Long = db.healthLogDao().insertHealthLog(log)
    suspend fun deleteHealthLog(log: HealthLogEntity) = db.healthLogDao().deleteHealthLog(log)

    suspend fun insertBreedingLog(log: BreedingLogEntity): Long = db.breedingLogDao().insertBreedingLog(log)
    suspend fun deleteBreedingLog(log: BreedingLogEntity) = db.breedingLogDao().deleteBreedingLog(log)

    suspend fun insertFeedItem(item: FeedInventoryEntity): Long = db.feedDao().insertFeedItem(item)
    suspend fun updateFeedItem(item: FeedInventoryEntity) = db.feedDao().updateFeedItem(item)
    suspend fun deleteFeedItem(item: FeedInventoryEntity) = db.feedDao().deleteFeedItem(item)

    suspend fun insertFinancialLog(log: FinancialLogEntity): Long = db.financialDao().insertFinancialLog(log)
    suspend fun deleteFinancialLog(log: FinancialLogEntity) = db.financialDao().deleteFinancialLog(log)
}
