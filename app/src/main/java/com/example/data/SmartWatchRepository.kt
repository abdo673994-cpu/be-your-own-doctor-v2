package com.example.data

import com.example.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.*
import kotlin.random.Random

class SmartWatchRepository(
    private val externalScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {

    private val availableDevicesList = mutableListOf(
        SmartWatchDevice(
            id = "watch_01",
            name = "Galaxy Watch Ultra",
            brand = "Samsung",
            model = "SM-L705F BioActive",
            batteryLevel = 88,
            rssi = -52,
            firmwareVersion = "v4.1.0-BioScience",
            connectionState = WatchConnectionState.CONNECTED
        ),
        SmartWatchDevice(
            id = "watch_02",
            name = "Pixel Watch 3",
            brand = "Google",
            model = "PW3-LTE 45mm",
            batteryLevel = 92,
            rssi = -61,
            firmwareVersion = "v3.2.14-Vitals",
            connectionState = WatchConnectionState.DISCONNECTED
        ),
        SmartWatchDevice(
            id = "watch_03",
            name = "Apple Watch Ultra 2",
            brand = "Apple",
            model = "A2986 Biomarker",
            batteryLevel = 74,
            rssi = -68,
            firmwareVersion = "watchOS 11.2",
            connectionState = WatchConnectionState.DISCONNECTED
        ),
        SmartWatchDevice(
            id = "watch_04",
            name = "Garmin Forerunner 965",
            brand = "Garmin",
            model = "Elevate V5 Sensor",
            batteryLevel = 96,
            rssi = -49,
            firmwareVersion = "v19.18-HRV",
            connectionState = WatchConnectionState.DISCONNECTED
        ),
        SmartWatchDevice(
            id = "watch_05",
            name = "Whoop 4.0 Biostrap",
            brand = "Whoop",
            model = "WP40-Recovery",
            batteryLevel = 65,
            rssi = -73,
            firmwareVersion = "v41.2.0",
            connectionState = WatchConnectionState.DISCONNECTED
        )
    )

    private val _devices = MutableStateFlow<List<SmartWatchDevice>>(availableDevicesList)
    val devices: StateFlow<List<SmartWatchDevice>> = _devices.asStateFlow()

    private val _connectedDevice = MutableStateFlow<SmartWatchDevice?>(availableDevicesList.first())
    val connectedDevice: StateFlow<SmartWatchDevice?> = _connectedDevice.asStateFlow()

    private val _liveVitals = MutableStateFlow(LiveVitals())
    val liveVitals: StateFlow<LiveVitals> = _liveVitals.asStateFlow()

    private val _dailyActivity = MutableStateFlow(DailyActivity())
    val dailyActivity: StateFlow<DailyActivity> = _dailyActivity.asStateFlow()

    private val _sleepArchitecture = MutableStateFlow(SleepArchitecture())
    val sleepArchitecture: StateFlow<SleepArchitecture> = _sleepArchitecture.asStateFlow()

    private val _workoutSession = MutableStateFlow(WorkoutSession())
    val workoutSession: StateFlow<WorkoutSession> = _workoutSession.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _sensorDiagnostics = MutableStateFlow(
        WatchSensorType.values().associateWith { true }
    )
    val sensorDiagnostics: StateFlow<Map<WatchSensorType, Boolean>> = _sensorDiagnostics.asStateFlow()

    // Realtime ECG Waveform data stream for oscilloscope
    private val _ecgWaveform = MutableStateFlow<List<Float>>(generateBaselineEcg())
    val ecgWaveform: StateFlow<List<Float>> = _ecgWaveform.asStateFlow()

    private var telemetryJob: Job? = null
    private var workoutJob: Job? = null
    private var ecgJob: Job? = null

    init {
        startTelemetrySimulation()
        startEcgWaveformStream()
    }

    private fun startTelemetrySimulation() {
        telemetryJob?.cancel()
        telemetryJob = externalScope.launch {
            var phase = 0.0
            while (isActive) {
                delay(1000L)
                val isConnected = _connectedDevice.value?.connectionState == WatchConnectionState.CONNECTED
                if (!isConnected) continue

                // Simulate physiological sinus respiratory rhythm
                phase += 0.2
                val sinusArrhythmia = (sin(phase) * 3.5).toInt()
                val currentBaseHr = if (_workoutSession.value.isActive) {
                    _workoutSession.value.currentHeartRate
                } else {
                    72
                }
                val jitter = Random.nextInt(-2, 3)
                val newHr = (currentBaseHr + sinusArrhythmia + jitter).coerceIn(45, 195)
                val newHrv = (64 + (cos(phase) * 8).toInt() + Random.nextInt(-3, 4)).coerceIn(25, 115)
                val newSpo2 = if (Random.nextFloat() > 0.85f) {
                    (98 + Random.nextInt(-1, 2)).coerceIn(95, 100)
                } else {
                    _liveVitals.value.spo2
                }

                _liveVitals.update { vitals ->
                    vitals.copy(
                        heartRate = newHr,
                        hrvRmssd = newHrv,
                        spo2 = newSpo2,
                        respirationRate = (14 + (sin(phase * 0.5) * 1.5).toInt()).coerceIn(11, 22),
                        stressScore = (32 + (cos(phase * 0.3) * 6).toInt()).coerceIn(12, 85),
                        skinTempCelsius = 36.6 + (sin(phase * 0.1) * 0.15),
                        bodyBattery = (vitals.bodyBattery - if (Random.nextInt(100) > 96) 1 else 0).coerceIn(10, 100)
                    )
                }

                // Increment steps subtly if live streaming
                if (Random.nextBoolean()) {
                    _dailyActivity.update { act ->
                        val addSteps = if (_workoutSession.value.isActive) Random.nextInt(15, 25) else Random.nextInt(1, 4)
                        act.copy(
                            steps = act.steps + addSteps,
                            distanceKm = (act.steps + addSteps) * 0.00078,
                            activeCalories = act.activeCalories + (addSteps * 0.04).toInt()
                        )
                    }
                }
            }
        }
    }

    private fun startEcgWaveformStream() {
        ecgJob?.cancel()
        ecgJob = externalScope.launch {
            var step = 0
            while (isActive) {
                delay(40L) // 25 fps smooth ECG scrolling
                step = (step + 1) % 100
                _ecgWaveform.update { oldList ->
                    val nextSample = calculateEcgSample(step)
                    val newList = oldList.drop(1) + nextSample
                    newList
                }
            }
        }
    }

    private fun calculateEcgSample(index: Int): Float {
        // P-Q-R-S-T wave model normalized around 0.5
        val normIndex = index % 30
        return when (normIndex) {
            4 -> 0.58f  // P wave
            5 -> 0.62f
            6 -> 0.54f
            11 -> 0.44f // Q wave
            12 -> 0.95f // R peak
            13 -> 0.18f // S dip
            14 -> 0.48f
            19 -> 0.68f // T wave
            20 -> 0.72f
            21 -> 0.65f
            else -> 0.50f + (Random.nextFloat() * 0.03f - 0.015f) // baseline noise
        }
    }

    private fun generateBaselineEcg(): List<Float> {
        return List(60) { i -> calculateEcgSample(i) }
    }

    fun startScanning() {
        externalScope.launch {
            _isScanning.value = true
            delay(2400L)
            _isScanning.value = false
        }
    }

    fun connectDevice(deviceId: String) {
        externalScope.launch {
            val target = _devices.value.find { it.id == deviceId } ?: return@launch
            // Set connecting
            _devices.update { list ->
                list.map {
                    if (it.id == deviceId) it.copy(connectionState = WatchConnectionState.CONNECTING)
                    else it.copy(connectionState = WatchConnectionState.DISCONNECTED)
                }
            }
            delay(1200L)
            // Set connected
            _devices.update { list ->
                list.map {
                    if (it.id == deviceId) it.copy(connectionState = WatchConnectionState.CONNECTED, lastSyncTimestamp = System.currentTimeMillis())
                    else it.copy(connectionState = WatchConnectionState.DISCONNECTED)
                }
            }
            _connectedDevice.value = _devices.value.find { it.id == deviceId }
        }
    }

    fun disconnectDevice() {
        _devices.update { list ->
            list.map { it.copy(connectionState = WatchConnectionState.DISCONNECTED) }
        }
        _connectedDevice.value = null
    }

    fun toggleSensorDiagnostic(sensor: WatchSensorType) {
        _sensorDiagnostics.update { current ->
            val updated = current.toMutableMap()
            updated[sensor] = !(updated[sensor] ?: true)
            updated
        }
    }

    fun syncNow() {
        externalScope.launch {
            _connectedDevice.update { it?.copy(lastSyncTimestamp = System.currentTimeMillis()) }
            // generate fresh synced metric state
            _liveVitals.update {
                it.copy(
                    restingHeartRate = 58,
                    vo2Max = 48.5,
                    hrvRmssd = (62..68).random(),
                    spo2 = (98..99).random()
                )
            }
        }
    }

    fun toggleWorkoutSession(type: String = "Cardio Zone Training") {
        val current = _workoutSession.value
        if (current.isActive) {
            workoutJob?.cancel()
            _workoutSession.value = current.copy(isActive = false)
        } else {
            _workoutSession.value = WorkoutSession(
                sessionType = type,
                durationSeconds = 0,
                currentHeartRate = 125,
                avgHeartRate = 125,
                maxHeartRate = 135,
                caloriesBurned = 0,
                activeZone = 2,
                isActive = true
            )
            workoutJob?.cancel()
            workoutJob = externalScope.launch {
                while (isActive) {
                    delay(1000L)
                    _workoutSession.update { session ->
                        val newDur = session.durationSeconds + 1
                        val hrTarget = when {
                            newDur < 30 -> 118 // Warm-up
                            newDur < 90 -> 138 // Aerobic Zone 2/3
                            newDur < 150 -> 156 // Threshold Zone 4
                            else -> 142
                        }
                        val liveHr = (hrTarget + Random.nextInt(-3, 4)).coerceIn(90, 185)
                        val zone = when {
                            liveHr < 115 -> 1
                            liveHr < 133 -> 2
                            liveHr < 152 -> 3
                            liveHr < 171 -> 4
                            else -> 5
                        }
                        session.copy(
                            durationSeconds = newDur,
                            currentHeartRate = liveHr,
                            avgHeartRate = ((session.avgHeartRate * newDur + liveHr) / (newDur + 1)),
                            maxHeartRate = max(session.maxHeartRate, liveHr),
                            caloriesBurned = session.caloriesBurned + if (newDur % 4 == 0) 1 else 0,
                            activeZone = zone
                        )
                    }
                }
            }
        }
    }

    fun getBioscienceInsights(): List<BioscienceInsight> {
        return listOf(
            BioscienceInsight(
                id = "ans_vagal",
                category = BioscienceCategory.AUTONOMIC_NERVOUS_SYSTEM,
                title = "Vagal Nerve & Parasympathetic Tone",
                primaryMetric = "HRV rMSSD: 64 ms (+14% above age baseline)",
                statusLabel = "High Vagal Tone (Optimal)",
                statusColorHex = 0xFF00E676,
                biologicalMechanism = "Heart Rate Variability root-mean-square of successive differences (rMSSD) quantifies the beat-to-beat acetylcholine release from the Tenth Cranial Nerve (Vagus), modulating sinoatrial node firing speed.",
                clinicalSignificance = "Elevated resting vagal tone is clinically associated with superior allostatic resilience, reduced systemic inflammation, rapid cardiac recovery, and lowered all-cause cardiovascular mortality.",
                scientificRecommendation = "Maintain deep diaphragmatic breathing with extended exhalations (4-7-8 protocol) to reinforce parasympathetic dominance before nocturnal rest.",
                peerReviewedReference = "Circulation / American Heart Association: 'Heart Rate Variability Standards of Measurement & Clinical Use'"
            ),
            BioscienceInsight(
                id = "circadian_phase",
                category = BioscienceCategory.CIRCADIAN_BIOLOGY,
                title = "Circadian Endocrine & Phase Alignment",
                primaryMetric = "DLMO Window: 21:45 | Chronotype: Intermediate Lion",
                statusLabel = "Synchronized Rhythm",
                statusColorHex = 0xFF7C4DFF,
                biologicalMechanism = "Light-stimulated melanopsin retinal ganglion cells transmit via the retinohypothalamic tract to the Suprachiasmatic Nucleus (SCN), timing pineal Dim Light Melatonin Onset (DLMO) and hypothalamic-pituitary-adrenal cortisol cycles.",
                clinicalSignificance = "Current biometric core temperature drop (-0.3°C after 20:00) validates healthy pineal melatonin activation and autonomic thermoregulatory heat dissipation via distal extremities.",
                scientificRecommendation = "Limit 480nm spectrum blue wavelength photons 90 minutes prior to sleep. Aim for 10,000 lux natural photon exposure within 30 min of waking.",
                peerReviewedReference = "Cell Metabolism / Journal of Pineal Research: 'Circadian Photoreception and Systemic Chronobiology'"
            ),
            BioscienceInsight(
                id = "mitochondrial_lactate",
                category = BioscienceCategory.METABOLIC_EFFICIENCY,
                title = "Substrate Oxidation & Lactate Clearance",
                primaryMetric = "FatMax Zone: 114–132 BPM | BMR: 1,720 kcal",
                statusLabel = "High Metabolic Flexibility",
                statusColorHex = 0xFF00F2FE,
                biologicalMechanism = "Zone 2 aerobic exercise stimulates mitochondrial biogenesis via PGC-1alpha upregulation, optimizing beta-oxidation of free fatty acids while keeping blood lactate below the 2.0 mmol/L Aerobic Threshold (AeT).",
                clinicalSignificance = "High mitochondrial density preserves intracellular glycogen stores, improves cellular insulin sensitivity, and increases metabolic clearing of glycolytic hydrogen ions.",
                scientificRecommendation = "Execute 150–180 minutes of polarized Zone 2 endurance work weekly to maximize mitochondrial cristae density and fatty-acid transport enzyme levels.",
                peerReviewedReference = "Sports Medicine / Nature Metabolism: 'Mitochondrial Adaptations to Exercise & Energy Homeostasis'"
            ),
            BioscienceInsight(
                id = "cardio_vo2",
                category = BioscienceCategory.CARDIOVASCULAR,
                title = "Cardiorespiratory Fitness & VO2 Max",
                primaryMetric = "VO2 Max: 48.5 mL/kg/min | Bio Age: 24 (Chron: 29)",
                statusLabel = "Superior Tier (Top 15%)",
                statusColorHex = 0xFF4FACFE,
                biologicalMechanism = "Maximal oxygen uptake (VO2 Max) defines the systemic rate limit of oxygen convective delivery (Fick equation: Cardiac Output × Arterio-Venous Oxygen Difference) and peripheral microvascular capillary perfusion.",
                clinicalSignificance = "A VO2 Max of 48.5 mL/kg/min yields a 5-year biological age advantage. Each 1-MET (3.5 mL/kg/min) increase is correlated with a 13% reduction in cardiovascular morbidity.",
                scientificRecommendation = "Incorporate 1 weekly 4x4-minute Norwegian HIIT session at 90-95% HRmax to stimulate left-ventricular end-diastolic eccentric remodeling and stroke volume.",
                peerReviewedReference = "Journal of the American College of Cardiology: 'Cardiorespiratory Fitness as an Independent Vital Sign'"
            ),
            BioscienceInsight(
                id = "sleep_architecture",
                category = BioscienceCategory.SLEEP_HOMEOSTASIS,
                title = "Slow Wave & REM Neuro-Regeneration",
                primaryMetric = "Deep (SWS): 22% (1h 41m) | REM: 26% (2h 00m)",
                statusLabel = "Optimal Restorative State",
                statusColorHex = 0xFFFF2A85,
                biologicalMechanism = "Slow-Wave Sleep (delta 0.5–4Hz EEG oscillations) triggers pulsatile somatotroph secretion of Human Growth Hormone (HGH) and activates the brain's glymphatic fluid convective flux, flushing beta-amyloid and hyperphosphorylated tau.",
                clinicalSignificance = "Combined 48% Deep + REM surpasses normal adult clinical baselines (35-40%), indicating high physiological restorative efficiency and muscular sarcomere repair.",
                scientificRecommendation = "Maintain bedroom ambient temperature between 18.0°C–19.5°C to facilitate normal nocturnal core body cooling.",
                peerReviewedReference = "Science / The Lancet Neurology: 'Glymphatic System Clearance During Natural Slow-Wave Sleep'"
            )
        )
    }
}
