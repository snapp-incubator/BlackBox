package cab.snapp.blackBox.poaro

enum class LogLevel(val priority: Int) {
    Debug(1),
    Error(2),
    Warning(3),
    UserInteraction(4),
    Force(5)
}