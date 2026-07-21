package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Database(
    entities = [
        CattleEntity::class,
        MilkLogEntity::class,
        HealthLogEntity::class,
        BreedingLogEntity::class,
        FeedInventoryEntity::class,
        FinancialLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cattleDao(): CattleDao
    abstract fun milkLogDao(): MilkLogDao
    abstract fun healthLogDao(): HealthLogDao
    abstract fun breedingLogDao(): BreedingLogDao
    abstract fun feedDao(): FeedDao
    abstract fun financialDao(): FinancialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dairy_track_pro_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val cal = Calendar.getInstance()
                val today = dateFormat.format(cal.time)

                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yesterday = dateFormat.format(cal.time)

                cal.add(Calendar.DAY_OF_YEAR, -1)
                val dayBeforeYesterday = dateFormat.format(cal.time)

                // 1. Initial Cattle
                val cattleList = listOf(
                    CattleEntity(
                        earTag = "COW-101",
                        name = "Daisy",
                        breed = "Holstein Friesian",
                        gender = "Female",
                        stage = "Lactating",
                        birthDate = "2021-03-15",
                        weightKg = 620.0,
                        lactationNumber = 3,
                        healthStatus = "Healthy",
                        groupPen = "High Yielders",
                        status = "Active",
                        notes = "Top producer, calm demeanor."
                    ),
                    CattleEntity(
                        earTag = "COW-102",
                        name = "Bessie",
                        breed = "Jersey",
                        gender = "Female",
                        stage = "Lactating",
                        birthDate = "2020-08-20",
                        weightKg = 480.0,
                        lactationNumber = 4,
                        healthStatus = "Under Treatment",
                        groupPen = "High Yielders",
                        status = "Active",
                        notes = "High butterfat content (4.8%). Currently on foot rot treatment."
                    ),
                    CattleEntity(
                        earTag = "COW-103",
                        name = "Clover",
                        breed = "Guernsey",
                        gender = "Female",
                        stage = "Lactating",
                        birthDate = "2022-01-10",
                        weightKg = 510.0,
                        lactationNumber = 2,
                        healthStatus = "Healthy",
                        groupPen = "High Yielders",
                        status = "Active",
                        notes = "Golden milk producer."
                    ),
                    CattleEntity(
                        earTag = "COW-104",
                        name = "Bella",
                        breed = "Holstein Friesian",
                        gender = "Female",
                        stage = "Dry",
                        birthDate = "2019-11-05",
                        weightKg = 650.0,
                        lactationNumber = 5,
                        healthStatus = "Healthy",
                        groupPen = "Dry Pen",
                        status = "Active",
                        notes = "Due for calving in 3 weeks."
                    ),
                    CattleEntity(
                        earTag = "COW-105",
                        name = "Molly",
                        breed = "Ayrshire",
                        gender = "Female",
                        stage = "Heifer",
                        birthDate = "2023-06-12",
                        weightKg = 380.0,
                        lactationNumber = 0,
                        healthStatus = "Healthy",
                        groupPen = "Heifer Pen",
                        status = "Active",
                        notes = "Ready for first artificial insemination."
                    ),
                    CattleEntity(
                        earTag = "BULL-01",
                        name = "Thunder",
                        breed = "Sahiwal",
                        gender = "Male",
                        stage = "Bull",
                        birthDate = "2020-02-14",
                        weightKg = 820.0,
                        lactationNumber = 0,
                        healthStatus = "Healthy",
                        groupPen = "Bull Stall",
                        status = "Active",
                        notes = "Breeding bull, strong genetics."
                    )
                )
                cattleList.forEach { database.cattleDao().insertCattle(it) }

                // 2. Initial Milk Logs
                val milkLogs = listOf(
                    MilkLogEntity(cattleTag = "COW-101", date = today, session = "Morning", quantityLiters = 18.5, fatPercentage = 3.9, proteinPercentage = 3.3, somaticCellCount = 120000),
                    MilkLogEntity(cattleTag = "COW-101", date = today, session = "Afternoon", quantityLiters = 14.2, fatPercentage = 4.0, proteinPercentage = 3.4),
                    MilkLogEntity(cattleTag = "COW-102", date = today, session = "Morning", quantityLiters = 12.0, fatPercentage = 4.8, proteinPercentage = 3.8),
                    MilkLogEntity(cattleTag = "COW-102", date = today, session = "Afternoon", quantityLiters = 10.5, fatPercentage = 4.9, proteinPercentage = 3.9),
                    MilkLogEntity(cattleTag = "COW-103", date = today, session = "Morning", quantityLiters = 15.0, fatPercentage = 4.2, proteinPercentage = 3.5),

                    MilkLogEntity(cattleTag = "COW-101", date = yesterday, session = "Morning", quantityLiters = 18.0, fatPercentage = 3.8, proteinPercentage = 3.3),
                    MilkLogEntity(cattleTag = "COW-101", date = yesterday, session = "Afternoon", quantityLiters = 14.5, fatPercentage = 3.9, proteinPercentage = 3.4),
                    MilkLogEntity(cattleTag = "COW-102", date = yesterday, session = "Morning", quantityLiters = 12.5, fatPercentage = 4.7, proteinPercentage = 3.7),
                    MilkLogEntity(cattleTag = "COW-103", date = yesterday, session = "Morning", quantityLiters = 14.8, fatPercentage = 4.1, proteinPercentage = 3.5),

                    MilkLogEntity(cattleTag = "BULK", date = today, session = "Morning", quantityLiters = 45.5, fatPercentage = 4.2, notes = "Bulk tank morning collection")
                )
                milkLogs.forEach { database.milkLogDao().insertMilkLog(it) }

                // 3. Initial Health Logs
                val healthLogs = listOf(
                    HealthLogEntity(
                        cattleTag = "COW-102",
                        date = today,
                        type = "Treatment/Medication",
                        diagnosisTitle = "Mild Foot Rot Treatment",
                        medicationGiven = "Oxytetracycline Spray & Cleansing",
                        withdrawalDays = 3,
                        withdrawalEndDate = "2026-07-24",
                        performedBy = "Dr. James (Vet)",
                        status = "Active Withdrawal",
                        notes = "Discard milk for 3 days."
                    ),
                    HealthLogEntity(
                        cattleTag = "COW-101",
                        date = dayBeforeYesterday,
                        type = "Vaccination",
                        diagnosisTitle = "FMD Annual Booster",
                        medicationGiven = "Fotvax FMD Vaccine",
                        withdrawalDays = 0,
                        performedBy = "Farm Admin",
                        status = "Completed",
                        notes = "Routine annual vaccination."
                    )
                )
                healthLogs.forEach { database.healthLogDao().insertHealthLog(it) }

                // 4. Initial Breeding Logs
                val breedingLogs = listOf(
                    BreedingLogEntity(
                        cattleTag = "COW-104",
                        eventDate = "2025-11-10",
                        eventType = "Insemination (AI)",
                        sireDetails = "Holstein Sire #USA-9942",
                        pregnancyStatus = "Confirmed Positive",
                        expectedCalvingDate = "2026-08-20",
                        notes = "PD confirmed positive by ultrasound. Currently dry."
                    ),
                    BreedingLogEntity(
                        cattleTag = "COW-105",
                        eventDate = yesterday,
                        eventType = "Heat Observed",
                        sireDetails = "Pending AI",
                        pregnancyStatus = "Pending",
                        notes = "Standing heat observed at 06:00 AM."
                    )
                )
                breedingLogs.forEach { database.breedingLogDao().insertBreedingLog(it) }

                // 5. Initial Feed Inventory
                val feeds = listOf(
                    FeedInventoryEntity(
                        itemCode = "F-01",
                        name = "Dairy Meal 18% Protein",
                        category = "Concentrate",
                        stockQuantityKg = 450.0,
                        minAlertThresholdKg = 200.0,
                        unitCostPerKg = 0.45,
                        supplier = "AgroFeed Supplies"
                    ),
                    FeedInventoryEntity(
                        itemCode = "F-02",
                        name = "Alfalfa Hay Bales",
                        category = "Forage",
                        stockQuantityKg = 120.0,
                        minAlertThresholdKg = 150.0, // Low stock trigger!
                        unitCostPerKg = 0.25,
                        supplier = "GreenValley Farms"
                    ),
                    FeedInventoryEntity(
                        itemCode = "F-03",
                        name = "Maize Silage",
                        category = "Silage",
                        stockQuantityKg = 1200.0,
                        minAlertThresholdKg = 300.0,
                        unitCostPerKg = 0.15,
                        supplier = "Own Production"
                    ),
                    FeedInventoryEntity(
                        itemCode = "F-04",
                        name = "High-Calcium Mineral Lick",
                        category = "Mineral",
                        stockQuantityKg = 45.0,
                        minAlertThresholdKg = 20.0,
                        unitCostPerKg = 1.20,
                        supplier = "AgroVet Direct"
                    )
                )
                feeds.forEach { database.feedDao().insertFeedItem(it) }

                // 6. Initial Financial Logs
                val financials = listOf(
                    FinancialLogEntity(
                        date = today,
                        type = "Income",
                        category = "Milk Sales",
                        amount = 185.0,
                        description = "Morning bulk milk collection (260L @ $0.71/L)"
                    ),
                    FinancialLogEntity(
                        date = yesterday,
                        type = "Expense",
                        category = "Feed Purchase",
                        amount = 135.0,
                        description = "3 bags Dairy Meal 18%"
                    ),
                    FinancialLogEntity(
                        date = dayBeforeYesterday,
                        type = "Expense",
                        category = "Vet & Meds",
                        amount = 45.0,
                        description = "Vet visit consultation & foot rot medication"
                    )
                )
                financials.forEach { database.financialDao().insertFinancialLog(it) }
            }
        }
    }
}
