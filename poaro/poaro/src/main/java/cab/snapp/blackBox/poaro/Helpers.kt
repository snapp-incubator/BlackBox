package cab.snapp.blackBox.poaro

fun Logger.e(label: String, message: String, selectedStreams: List<String> = listOf()) {
    log(LogLevel.Error, label, message, selectedStreams)
}

fun Logger.w(label: String, message: String, selectedStreams: List<String> = listOf()) {
    log(LogLevel.Warning, label, message, selectedStreams)
}

fun Logger.d(label: String, message: String, selectedStreams: List<String> = listOf()) {
    log(LogLevel.Debug, label, message, selectedStreams)
}

fun Logger.i(label: String, message: String, selectedStreams: List<String> = listOf()) {
    log(LogLevel.UserInteraction, label, message, selectedStreams)
}

fun Logger.f(label: String, message: String, selectedStreams: List<String> = listOf()) {
    log(LogLevel.Force, label, message, selectedStreams)
}