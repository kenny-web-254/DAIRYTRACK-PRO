package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CattleDao {
    @Query("SELECT * FROM cattle ORDER BY id DESC")
    fun getAllCattle(): Flow<List<CattleEntity>>

    @Query("SELECT * FROM cattle WHERE status = 'Active' ORDER BY id DESC")
    fun getActiveCattle(): Flow<List<CattleEntity>>

    @Query("SELECT * FROM cattle WHERE earTag = :tag LIMIT 1")
    suspend fun getCattleByTag(tag: String): CattleEntity?

    @Query("SELECT COUNT(*) FROM cattle WHERE status = 'Active'")
    fun getActiveCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM cattle WHERE stage = 'Lactating' AND status = 'Active'")
    fun getLactatingCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCattle(cattle: CattleEntity): Long

    @Update
    suspend fun updateCattle(cattle: CattleEntity)

    @Delete
    suspend fun deleteCattle(cattle: CattleEntity)
}

@Dao
interface MilkLogDao {
    @Query("SELECT * FROM milk_logs ORDER BY date DESC, id DESC")
    fun getAllMilkLogs(): Flow<List<MilkLogEntity>>

    @Query("SELECT * FROM milk_logs WHERE cattleTag = :tag ORDER BY date DESC, id DESC")
    fun getMilkLogsForCattle(tag: String): Flow<List<MilkLogEntity>>

    @Query("SELECT * FROM milk_logs WHERE date = :date")
    fun getMilkLogsForDate(date: String): Flow<List<MilkLogEntity>>

    @Query("SELECT SUM(quantityLiters) FROM milk_logs WHERE date = :date")
    fun getTotalMilkForDate(date: String): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilkLog(log: MilkLogEntity): Long

    @Delete
    suspend fun deleteMilkLog(log: MilkLogEntity)
}

@Dao
interface HealthLogDao {
    @Query("SELECT * FROM health_logs ORDER BY date DESC, id DESC")
    fun getAllHealthLogs(): Flow<List<HealthLogEntity>>

    @Query("SELECT * FROM health_logs WHERE cattleTag = :tag ORDER BY date DESC")
    fun getHealthLogsForCattle(tag: String): Flow<List<HealthLogEntity>>

    @Query("SELECT * FROM health_logs WHERE status = 'Active Withdrawal' OR withdrawalDays > 0")
    fun getActiveWithdrawalLogs(): Flow<List<HealthLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthLog(log: HealthLogEntity): Long

    @Delete
    suspend fun deleteHealthLog(log: HealthLogEntity)
}

@Dao
interface BreedingLogDao {
    @Query("SELECT * FROM breeding_logs ORDER BY eventDate DESC, id DESC")
    fun getAllBreedingLogs(): Flow<List<BreedingLogEntity>>

    @Query("SELECT * FROM breeding_logs WHERE cattleTag = :tag ORDER BY eventDate DESC")
    fun getBreedingLogsForCattle(tag: String): Flow<List<BreedingLogEntity>>

    @Query("SELECT * FROM breeding_logs WHERE pregnancyStatus = 'Confirmed Positive'")
    fun getPregnantCattleLogs(): Flow<List<BreedingLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreedingLog(log: BreedingLogEntity): Long

    @Delete
    suspend fun deleteBreedingLog(log: BreedingLogEntity)
}

@Dao
interface FeedDao {
    @Query("SELECT * FROM feed_inventory ORDER BY name ASC")
    fun getAllFeedInventory(): Flow<List<FeedInventoryEntity>>

    @Query("SELECT * FROM feed_inventory WHERE stockQuantityKg <= minAlertThresholdKg")
    fun getLowStockFeed(): Flow<List<FeedInventoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedItem(item: FeedInventoryEntity): Long

    @Update
    suspend fun updateFeedItem(item: FeedInventoryEntity)

    @Delete
    suspend fun deleteFeedItem(item: FeedInventoryEntity)
}

@Dao
interface FinancialDao {
    @Query("SELECT * FROM financial_logs ORDER BY date DESC, id DESC")
    fun getAllFinancialLogs(): Flow<List<FinancialLogEntity>>

    @Query("SELECT SUM(amount) FROM financial_logs WHERE type = 'Income'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM financial_logs WHERE type = 'Expense'")
    fun getTotalExpense(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFinancialLog(log: FinancialLogEntity): Long

    @Delete
    suspend fun deleteFinancialLog(log: FinancialLogEntity)
}
