# BlackBox

## Introduction
BlackBox is a comprehensive project that encompasses various tools, one of which is called "Poaro."
Poaro is specifically designed as a logging solution for Android applications that provides flexible and efficient logging capabilities.
It offers various features to simplify the process of logging messages with different log levels, sending logs to multiple destinations, and managing log files effectively.
It allows you to log messages with different log levels, send logs to multiple streams, and includes advanced features like buffering, flushing, file management, and file cleanup.


## Features

- **Flexible logging capabilities with different log levels**
- **Efficient buffering and flushing mechanisms for log management**
- **File management system for handling log files**
- **Automatic file cleanup with a file deletion handler**
- **Multiple log streams for sending logs to different destinations: Enables logging to multiple streams simultaneously, allowing developers to direct log messages to different destinations such as files, network endpoints, or custom implementations.**
- **Cache buffer for efficient log message storage**
- **Customizable log stream implementations**
- **Support for various flushers based on time, count, or both**
- **Flexible Log Stream Limitation:** You can limit the number of log streams based on your requirements.
- **LogLevel Control:** Set the minimum log level to control which log messages are recorded.
- **Multiple Log Streams:** Support for different log streams, allowing separate handling and configuration for each stream. Each log stream can have its own dedicated flusher.
- **Various Stream Types:** Choose from different stream types such as `stdStream`, `lokiStream`, or `fileStream` to suit your logging needs.
- **File Types in Internal Storage: The `_cache` file is a temporary storage location within the internal storage where log messages are temporarily stored before being processed and flushed to the appropriate log streams, ensuring efficient and reliable logging operations.**
- **File Types in Internal Storage: The `logger` file represents the persistent storage location within the internal storage where log messages are permanently stored, allowing for easy retrieval and analysis of logged information over time.**
- **Easy-to-use Poaro configuration**
- **Well-documented codebase and example usage**

## Interface Diagram
![](document/interface_diagram.png)

## Getting Started

To use the Poaro Library in your Android project, follow these steps:

1. Add the Poaro Library dependency to your project's `build.gradle` file:
```groovy
dependencies {
    implementation 'cab.snapp.blackBox:poaro:1.0.0'
}
```

2. Import the necessary classes or modules for logging and configuration:
```groovy
import cab.snapp.blackBox.poaro.Logger
import cab.snapp.blackBox.poaro.LogStream
import cab.snapp.blackBox.poaro.FileLogStream
import cab.snapp.blackBox.poaro.LogLevel
```

3. Create log streams and configure the logger:
```groovy
LogStream fileStream = new FileLogStream("logs.log")
List<LogStream> logStreams = [fileStream]
Logger logger = new Logger(logStreams, LogLevel.Debug)
```

4. Start logging messages with different log levels:
```groovy
logger.log(LogLevel.Info, "App", "Application started")
logger.log(LogLevel.Warning, "App", "Resource not found")
logger.log(LogLevel.Error, "App", "An error occurred")
```

5. Close the logger when it's no longer needed:
```groovy
logger.close()
```

## Logger Configuration

The `SnappLogger` class serves as the main entry point for using the Poaro Library. It allows you to configure the Poaro by providing a list of log streams and an optional log level. The log level determines the minimum log level that should be logged.

## Log Streams

Log streams represent destinations where log messages are sent. The library provides a built-in log stream called `FileStream`, which writes log messages to a file. Additionally, you can create custom log streams based on your specific requirements, such as sending logs to a database or a remote logging service.

## Flushers

Flushers handle the flushing of log messages from a buffer to the corresponding log stream. The library offers different types of flushers to suit various needs. These include:
- `TimeFlusher`: Flushes log messages based on a specified time interval.
- `CountFlusher`: Flushes log messages once a certain count threshold is reached.
- `TimeAndCountFlusher`: Flushes log messages based on a time interval and count threshold.

By using the appropriate flusher, you can control the frequency and efficiency of log flushing, ensuring optimal log management.

## File Manager

The `FileManager` interface provides operations for managing log files, such as writing logs, reading logs, initializing files, and clearing files. The library includes a default implementation, `FileManagerImpl`, which handles log file management in a specified directory. This allows for easy file handling and simplifies log file management tasks.

## File Deletion Handler

The file deletion handler, implemented by the `FileDeletionHandler` interface and the `FileDeletionHandlerImpl` class, automates the process of deleting older log files. It runs a cleanup job periodically and removes files that exceed a specified duration. With the file deletion handler, you can keep your log files organized and prevent unnecessary storage usage.

## Cache Buffer

The cache buffer, implemented by the `CacheBufferImp` class, provides an efficient mechanism for buffering log messages. It stores log messages until they are flushed to the log stream, allowing for optimized log management. The cache buffer integrates with the file manager and utilizes a time formatter for effective caching and recovery of log messages.

## Example Usage

```kotlin
// Create log streams
val logStreams = listOf(FileStreamImpl(), AnotherLogStream())

// Create a SnappLogger instance
val logger = SnappLogger(logStreams)

// Log messages with different log levels
logger.log(LogLevel.Debug, "Tag", "Debug message")
logger.log(LogLevel.Warning, "Tag", "Warning message")
logger.log(LogLevel.Error, "Tag", "Error message")

// Close the logger when done
logger.close()
```

## Contributing

Contributions to the Poaro Library are welcome! If you encounter any issues or have suggestions for improvements, please submit an issue or pull request on the GitHub repository.

## Credits
Based on @rezpilehvar 's idea.
