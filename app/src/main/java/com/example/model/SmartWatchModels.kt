package com.example.model

enum class WatchConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED
}

enum class WatchSensorType(val displayName: String, val unit: String) {
    PHOTOPLETHYSMOGRAPHY("PPG Optical Pulse", "530nm Green LED"),
    PULSE_OXIMETRY("Pulse Oximeter", "660nm / 940nm IR"),
    SKIN_THERMISTOR("Contact Thermistor", "±0.05°C Precision"),
    ECG_ELECTRODE("Titanium ECG Ring", "Lead-I Equivalent"),
    BIOIMPEDANCE("BIA Dual-Frequency", "50kHz / 250kHz"),
    INERTIAL_IMU("6-Axis IMU Sensor", "100Hz Accel/Gyro")
}

data class SmartWatchDevice(
    val id: String,
    val name: String,
    val brand: String,
    val model: String,
    val batteryLevel: Int = 88,
    val isCharging: Boolean = false,
    val rssi: Int = -54, // dBm
    val firmwareVersion: String = "v3.4.12-bio",
    val connectionState: WatchConnectionState = WatchConnectionState.CONNECTED,
    val isLiveStreaming: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

data class LiveVitals(
    val heartRate: Int = 74,
    val restingHeartRate: Int = 58,
    val hrvRmssd: Int = 64, // ms
    val spo2: Int = 98, // %
    val respirationRate: Int = 14, // brpm
    val skinTempCelsius: Double = 36.6,
    val skinTempDelta: Double = 0.1, // °C relative to baseline
    val stressScore: Int = 32, // 0 - 100
    val bodyBattery: Int = 84, // 0 - 100
    val bloodPressureSys: Int = 118,
    val bloodPressureDia: Int = 76,
    val vo2Max: Double = 48.5, // mL/kg/min
    val biologicalAge: Int = 24,
    val chronologicalAge: Int = 29
)

data class DailyActivity(
    val steps: Int = 8420,
    val stepGoal: Int = 10000,
    val activeCalories: Int = 540,
    val bmrCalories: Int = 1720,
    val distanceKm: Double = 6.2,
    val activeMinutes: Int = 48,
    val standHours: Int = 10,
    val standGoalHours: Int = 12,
    val floorsClimbed: Int = 14
)

data class SleepArchitecture(
    val totalSleepMinutes: Int = 462, // 7h 42m
    val deepPercent: Int = 22,
    val remPercent: Int = 26,
    val lightPercent: Int = 47,
    val awakePercent: Int = 5,
    val sleepScore: Int = 89,
    val recoveryEfficiency: Int = 92,
    val breathingDisturbanceIndex: Double = 1.4, // events/hr
    val restingPulseDuringSleep: Int = 52
)

enum class BioscienceCategory(val title: String) {
    AUTONOMIC_NERVOUS_SYSTEM("Autonomic Nervous System"),
    CIRCADIAN_BIOLOGY("Circadian Biology"),
    METABOLIC_EFFICIENCY("Metabolic & Lactate"),
    CARDIOVASCULAR("Cardiovascular Dynamics"),
    SLEEP_HOMEOSTASIS("Sleep Architecture & Rest")
}

data class BioscienceInsight(
    val id: String,
    val category: BioscienceCategory,
    val title: String,
    val primaryMetric: String,
    val statusLabel: String,
    val statusColorHex: Long, // Color int
    val biologicalMechanism: String,
    val clinicalSignificance: String,
    val scientificRecommendation: String,
    val peerReviewedReference: String
)

data class WorkoutSession(
    val sessionType: String = "Cardio Zone Training",
    val durationSeconds: Int = 0,
    val currentHeartRate: Int = 132,
    val avgHeartRate: Int = 128,
    val maxHeartRate: Int = 165,
    val caloriesBurned: Int = 185,
    val activeZone: Int = 3, // Zone 1 to 5
    val isActive: Boolean = false
)
