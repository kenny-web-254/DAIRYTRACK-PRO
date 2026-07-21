package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cattle")
data class CattleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val earTag: String,
    val name: String,
    val breed: String,
    val gender: String = "Female",
    val stage: String, // Lactating, Dry, Heifer, Calf, Bull
    val birthDate: String, // YYYY-MM-DD
    val weightKg: Double,
    val lactationNumber: Int = 1,
    val healthStatus: String = "Healthy", // Healthy, Under Treatment, Sick, Quarantine
    val groupPen: String = "High Yielders", // High Yielders, Low Yielders, Dry Pen, Maternity, Calf Barn
    val status: String = "Active", // Active, Sold, Deceased
    val notes: String = ""
)

@Entity(tableName = "milk_logs")
data class MilkLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cattleTag: String, // Tag or "BULK"
    val date: String, // YYYY-MM-DD
    val session: String, // Morning, Afternoon, Evening
    val quantityLiters: Double,
    val fatPercentage: Double? = null,
    val proteinPercentage: Double? = null,
    val somaticCellCount: Int? = null,
    val notes: String = ""
)

@Entity(tableName = "health_logs")
data class HealthLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cattleTag: String,
    val date: String, // YYYY-MM-DD
    val type: String, // Vaccination, Deworming, Disease/Symptom, Treatment/Medication, Vet Visit
    val diagnosisTitle: String,
    val medicationGiven: String = "",
    val withdrawalDays: Int = 0,
    val withdrawalEndDate: String? = null, // YYYY-MM-DD
    val performedBy: String = "Farm Vet",
    val status: String = "Completed", // Completed, Active Withdrawal, Follow-up Needed
    val notes: String = ""
)

@Entity(tableName = "breeding_logs")
data class BreedingLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cattleTag: String,
    val eventDate: String, // YYYY-MM-DD
    val eventType: String, // Heat Observed, Insemination (AI), Pregnancy Check (PD), Dry-off, Calved
    val sireDetails: String = "",
    val pregnancyStatus: String = "Pending", // Pending, Confirmed Positive, Confirmed Negative
    val expectedCalvingDate: String? = null, // YYYY-MM-DD
    val notes: String = ""
)

@Entity(tableName = "feed_inventory")
data class FeedInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val itemCode: String,
    val name: String,
    val category: String, // Concentrate, Forage, Silage, Mineral
    val stockQuantityKg: Double,
    val minAlertThresholdKg: Double,
    val unitCostPerKg: Double,
    val supplier: String = ""
)

@Entity(tableName = "financial_logs")
data class FinancialLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val type: String, // Income, Expense
    val category: String, // Milk Sales, Cattle Sales, Feed Purchase, Vet & Meds, Equipment, Labor
    val amount: Double,
    val description: String,
    val referenceTag: String? = null
)
