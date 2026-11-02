package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SmartWatchRepository
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: SmartWatchRepository = SmartWatchRepository()
) : ViewModel() {

    val devices: StateFlow<List<SmartWatchDevice>> = repository.devices
    val connectedDevice: StateFlow<SmartWatchDevice?> = repository.connectedDevice
    val liveVitals: StateFlow<LiveVitals> = repository.liveVitals
    val dailyActivity: StateFlow<DailyActivity> = repository.dailyActivity
    val sleepArchitecture: StateFlow<SleepArchitecture> = repository.sleepArchitecture
    val workoutSession: StateFlow<WorkoutSession> = repository.workoutSession
    val isScanning: StateFlow<Boolean> = repository.isScanning
    val sensorDiagnostics: StateFlow<Map<WatchSensorType, Boolean>> = repository.sensorDiagnostics
    val ecgWaveform: StateFlow<List<Float>> = repository.ecgWaveform

    private val _currentNavIndex = MutableStateFlow(0)
    val currentNavIndex: StateFlow<Int> = _currentNavIndex.asStateFlow()

    private val _selectedInsightCategory = MutableStateFlow<BioscienceCategory?>(null)
    val selectedInsightCategory: StateFlow<BioscienceCategory?> = _selectedInsightCategory.asStateFlow()

    private val _showHealthReport = MutableStateFlow(false)
    val showHealthReport: StateFlow<Boolean> = _showHealthReport.asStateFlow()

    val allInsights: List<BioscienceInsight> = repository.getBioscienceInsights()

    val filteredInsights: StateFlow<List<BioscienceInsight>> = _selectedInsightCategory.map { cat ->
        if (cat == null) allInsights else allInsights.filter { it.category == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), allInsights)

    fun setNavIndex(index: Int) {
        _currentNavIndex.value = index
    }

    fun selectCategoryFilter(category: BioscienceCategory?) {
        _selectedInsightCategory.value = category
    }

    fun scanDevices() {
        repository.startScanning()
    }

    fun connectDevice(deviceId: String) {
        repository.connectDevice(deviceId)
    }

    fun disconnectDevice() {
        repository.disconnectDevice()
    }

    fun toggleSensor(sensor: WatchSensorType) {
        repository.toggleSensorDiagnostic(sensor)
    }

    fun syncNow() {
        repository.syncNow()
    }

    fun toggleWorkout(type: String = "Cardio Zone Training") {
        repository.toggleWorkoutSession(type)
    }

    fun setHealthReportVisible(visible: Boolean) {
        _showHealthReport.value = visible
    }
}
