package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.*
import com.example.data.remote.GeminiClient
import com.example.data.repository.DairyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class AiChatMessage(
    val sender: String, // "USER" or "SPECIALIST"
    val message: String,
    val timestamp: String = SimpleDateFormat("HH:mm", Locale.US).format(Date())
)

class DairyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    val repository = DairyRepository(db)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val todayDate: String
        get() = dateFormat.format(Date())

    // Search and Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _stageFilter = MutableStateFlow("ALL") // ALL, Lactating, Dry, Heifer, Calf, Bull
    val stageFilter: StateFlow<String> = _stageFilter.asStateFlow()

    // Cattle Flows
    val allCattle: StateFlow<List<CattleEntity>> = repository.allCattle
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredCattle: StateFlow<List<CattleEntity>> = combine(
        allCattle,
        searchQuery,
        stageFilter
    ) { cattleList, query, stage ->
        cattleList.filter { cattle ->
            val matchesQuery = query.isBlank() ||
                    cattle.earTag.contains(query, ignoreCase = true) ||
                    cattle.name.contains(query, ignoreCase = true) ||
                    cattle.breed.contains(query, ignoreCase = true)
            val matchesStage = stage == "ALL" || cattle.stage.equals(stage, ignoreCase = true)
            matchesQuery && matchesStage
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeCattleCount: StateFlow<Int> = repository.activeCattleCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lactatingCattleCount: StateFlow<Int> = repository.lactatingCattleCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Milk Flows
    val allMilkLogs: StateFlow<List<MilkLogEntity>> = repository.allMilkLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayMilkTotal: StateFlow<Double> = repository.getTotalMilkForDate(todayDate)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Health Flows
    val allHealthLogs: StateFlow<List<HealthLogEntity>> = repository.allHealthLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeWithdrawalLogs: StateFlow<List<HealthLogEntity>> = repository.activeWithdrawalLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Breeding Flows
    val allBreedingLogs: StateFlow<List<BreedingLogEntity>> = repository.allBreedingLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pregnantCattleLogs: StateFlow<List<BreedingLogEntity>> = repository.pregnantCattleLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Feed Flows
    val allFeedInventory: StateFlow<List<FeedInventoryEntity>> = repository.allFeedInventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockFeed: StateFlow<List<FeedInventoryEntity>> = repository.lowStockFeed
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Flows
    val allFinancialLogs: StateFlow<List<FinancialLogEntity>> = repository.allFinancialLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalIncome: StateFlow<Double> = repository.totalIncome
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalExpense: StateFlow<Double> = repository.totalExpense
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val netProfit: StateFlow<Double> = combine(totalIncome, totalExpense) { inc, exp ->
        inc - exp
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Selected Cattle for Detail View
    private val _selectedCattle = MutableStateFlow<CattleEntity?>(null)
    val selectedCattle: StateFlow<CattleEntity?> = _selectedCattle.asStateFlow()

    // AI Specialist State
    private val _aiMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                sender = "SPECIALIST",
                message = "Hello! I am DairyAI Specialist. I can help analyze your herd's performance, diagnose production drops, optimize feed rations, or check milk withdrawal periods. How can I assist you today?"
            )
        )
    )
    val aiMessages: StateFlow<List<AiChatMessage>> = _aiMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // Filter & Search Handlers
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStageFilter(stage: String) {
        _stageFilter.value = stage
    }

    fun selectCattle(cattle: CattleEntity?) {
        _selectedCattle.value = cattle
    }

    // Actions - Cattle
    fun addOrUpdateCattle(
        id: Long = 0,
        earTag: String,
        name: String,
        breed: String,
        stage: String,
        birthDate: String,
        weightKg: Double,
        lactationNumber: Int,
        healthStatus: String,
        groupPen: String,
        notes: String
    ) {
        viewModelScope.launch {
            val cattle = CattleEntity(
                id = id,
                earTag = earTag.trim().uppercase(),
                name = name.trim(),
                breed = breed,
                stage = stage,
                birthDate = birthDate,
                weightKg = weightKg,
                lactationNumber = lactationNumber,
                healthStatus = healthStatus,
                groupPen = groupPen,
                notes = notes
            )
            repository.insertCattle(cattle)
        }
    }

    fun deleteCattle(cattle: CattleEntity) {
        viewModelScope.launch {
            repository.deleteCattle(cattle)
        }
    }

    fun deleteMilkLog(log: MilkLogEntity) {
        viewModelScope.launch {
            repository.deleteMilkLog(log)
        }
    }

    fun deleteHealthLog(log: HealthLogEntity) {
        viewModelScope.launch {
            repository.deleteHealthLog(log)
        }
    }

    fun deleteBreedingLog(log: BreedingLogEntity) {
        viewModelScope.launch {
            repository.deleteBreedingLog(log)
        }
    }

    fun deleteFeedItem(item: FeedInventoryEntity) {
        viewModelScope.launch {
            repository.deleteFeedItem(item)
        }
    }

    fun deleteFinancialLog(log: FinancialLogEntity) {
        viewModelScope.launch {
            repository.deleteFinancialLog(log)
        }
    }

    // Actions - Milk Log
    fun logMilkProduction(
        cattleTag: String,
        quantityLiters: Double,
        session: String = "Morning",
        fatPct: Double? = null,
        proteinPct: Double? = null,
        scc: Int? = null,
        notes: String = ""
    ) {
        viewModelScope.launch {
            val log = MilkLogEntity(
                cattleTag = cattleTag.trim().uppercase(),
                date = todayDate,
                session = session,
                quantityLiters = quantityLiters,
                fatPercentage = fatPct,
                proteinPercentage = proteinPct,
                somaticCellCount = scc,
                notes = notes
            )
            repository.insertMilkLog(log)
        }
    }

    // Actions - Health Log
    fun logHealthEvent(
        cattleTag: String,
        type: String,
        title: String,
        medication: String,
        withdrawalDays: Int,
        performedBy: String = "Farm Vet",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val cal = Calendar.getInstance()
            var endDateStr: String? = null
            var status = "Completed"

            if (withdrawalDays > 0) {
                cal.add(Calendar.DAY_OF_YEAR, withdrawalDays)
                endDateStr = dateFormat.format(cal.time)
                status = "Active Withdrawal"
            }

            val log = HealthLogEntity(
                cattleTag = cattleTag.trim().uppercase(),
                date = todayDate,
                type = type,
                diagnosisTitle = title,
                medicationGiven = medication,
                withdrawalDays = withdrawalDays,
                withdrawalEndDate = endDateStr,
                performedBy = performedBy,
                status = status,
                notes = notes
            )
            repository.insertHealthLog(log)

            // Update cattle health status if needed
            if (type == "Disease/Symptom" || type == "Treatment/Medication") {
                val cattle = allCattle.value.find { it.earTag.equals(cattleTag, ignoreCase = true) }
                if (cattle != null) {
                    repository.updateCattle(cattle.copy(healthStatus = "Under Treatment"))
                }
            }
        }
    }

    // Actions - Breeding
    fun logBreedingEvent(
        cattleTag: String,
        eventType: String,
        sireDetails: String,
        pregnancyStatus: String = "Pending",
        notes: String = ""
    ) {
        viewModelScope.launch {
            var expectedCalving: String? = null
            if (eventType == "Insemination (AI)" || pregnancyStatus == "Confirmed Positive") {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, 283) // Gestation period ~283 days
                expectedCalving = dateFormat.format(cal.time)
            }

            val log = BreedingLogEntity(
                cattleTag = cattleTag.trim().uppercase(),
                eventDate = todayDate,
                eventType = eventType,
                sireDetails = sireDetails,
                pregnancyStatus = pregnancyStatus,
                expectedCalvingDate = expectedCalving,
                notes = notes
            )
            repository.insertBreedingLog(log)

            // Update cattle stage if dry or calved
            val cattle = allCattle.value.find { it.earTag.equals(cattleTag, ignoreCase = true) }
            if (cattle != null) {
                if (eventType == "Dry-off") {
                    repository.updateCattle(cattle.copy(stage = "Dry"))
                } else if (eventType == "Calved") {
                    repository.updateCattle(cattle.copy(stage = "Lactating", lactationNumber = cattle.lactationNumber + 1))
                }
            }
        }
    }

    // Actions - Feed
    fun addOrUpdateFeed(
        id: Long = 0,
        itemCode: String,
        name: String,
        category: String,
        stockKg: Double,
        minAlertKg: Double,
        unitCost: Double,
        supplier: String
    ) {
        viewModelScope.launch {
            val feed = FeedInventoryEntity(
                id = id,
                itemCode = itemCode,
                name = name,
                category = category,
                stockQuantityKg = stockKg,
                minAlertThresholdKg = minAlertKg,
                unitCostPerKg = unitCost,
                supplier = supplier
            )
            repository.insertFeedItem(feed)
        }
    }

    // Actions - Financial
    fun logFinancialTransaction(
        type: String,
        category: String,
        amount: Double,
        description: String,
        refTag: String? = null
    ) {
        viewModelScope.launch {
            val log = FinancialLogEntity(
                date = todayDate,
                type = type,
                category = category,
                amount = amount,
                description = description,
                referenceTag = refTag
            )
            repository.insertFinancialLog(log)
        }
    }

    // Gemini AI Specialist Query
    fun askDairyAiSpecialist(userPrompt: String) {
        if (userPrompt.isBlank()) return

        val userMsg = AiChatMessage("USER", userPrompt)
        _aiMessages.update { it + userMsg }
        _isAiThinking.value = true

        viewModelScope.launch {
            val snapshotContext = """
                Total Active Cattle: ${activeCattleCount.value}
                Lactating Cows: ${lactatingCattleCount.value}
                Today's Milk Yield Total: ${todayMilkTotal.value} L
                Active Milk Withdrawal Alerts: ${activeWithdrawalLogs.value.size} cows
                Pregnant Cows: ${pregnantCattleLogs.value.size}
                Low Feed Stock Warnings: ${lowStockFeed.value.joinToString { it.name }}
            """.trimIndent()

            val aiResponse = GeminiClient.getDairyAdvice(
                prompt = userPrompt,
                farmContext = snapshotContext
            )

            val specialistMsg = AiChatMessage("SPECIALIST", aiResponse)
            _aiMessages.update { it + specialistMsg }
            _isAiThinking.value = false
        }
    }

    // Generate Shareable Farm Report Text
    fun generateFarmReportText(): String {
        val sb = StringBuilder()
        sb.appendLine("=== DAIRYTRACK PRO FARM REPORT ===")
        sb.appendLine("Date Generated: $todayDate")
        sb.appendLine("Active Herd Count: ${activeCattleCount.value}")
        sb.appendLine("Lactating Cows: ${lactatingCattleCount.value}")
        sb.appendLine("Today's Total Milk Yield: ${todayMilkTotal.value} Liters")
        sb.appendLine("Net Financial Position: $${String.format(Locale.US, "%.2f", netProfit.value)}")
        sb.appendLine()
        sb.appendLine("--- Active Cattle Summary ---")
        allCattle.value.take(10).forEach { cow ->
            sb.appendLine("[${cow.earTag}] ${cow.name} - ${cow.breed} (${cow.stage}, ${cow.healthStatus})")
        }
        sb.appendLine()
        sb.appendLine("--- Health & Withdrawal Alerts ---")
        if (activeWithdrawalLogs.value.isEmpty()) {
            sb.appendLine("No active milk withdrawal restrictions.")
        } else {
            activeWithdrawalLogs.value.forEach { w ->
                sb.appendLine("ALERT: ${w.cattleTag} under ${w.diagnosisTitle}. Milk discard until ${w.withdrawalEndDate}")
            }
        }
        sb.appendLine()
        sb.appendLine("Generated by DairyTrack Pro Mobile System.")
        return sb.toString()
    }

    fun shareFarmReport(context: Context) {
        val report = generateFarmReportText()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "DairyTrack Pro Farm Summary - $todayDate")
            putExtra(Intent.EXTRA_TEXT, report)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Farm Report"))
    }
}
