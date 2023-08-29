package cab.snapp.blackBox.poaro


data class Log(
    val timestamp: Long,
    val level: LogLevel,
    val label: String,
    val message: String
)