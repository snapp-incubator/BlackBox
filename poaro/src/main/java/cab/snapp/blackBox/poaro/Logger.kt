package cab.snapp.blackBox.poaro

interface Logger {
    fun log(level: LogLevel, label: String, message: String, selectedStreams: List<String>)
}