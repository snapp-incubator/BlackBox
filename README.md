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

To integrate the Poaro Library into your Android project, follow these steps:

1. Add the Poaro Library dependencies to your project's `build.gradle` file:
```groovy
dependencies {
    implementation 'io.github.snapp-incubator:poaro:1.0.0'
    
    // if u want to use fileStream
    implementation 'io.github.snapp-incubator:file-stream:1.0.0'

    // if u want to use loki
    implementation 'io.github.snapp-incubator:loki:1.0.0'
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
You can find the complete implementation in the "app" module.

4. Start logging messages with different log levels:
```groovy
// Info
logger.i("App", "Application started")
// Warning
logger.w("App", "Resource not found")
// Error
logger.e("App", "An error occurred")
// Force
logger.f("App", "Crash occurred")
```

5. Close the logger when it's no longer needed:
```groovy
logger.close()
```

## Logger Configuration

The `SnappLogger` class serves as the main entry point for using the Poaro Library. It allows you to configure the Poaro by providing a list of log streams and an optional log level. The log level determines the minimum log level that should be logged.

## Log Streams

Log streams represent destinations where log messages are sent.
The library provides a built-in log streams called `FileStreamImpl` and `Loki`.
`FileStreamImpl` offers the ability to write logs to internal storage. which u can find the logs in `Device File Explorer/data/data/your-package-name/files/logger`, And if you open it after logging you can see there is your loggers files and  another file called `file_stream_cache`
which is responsible for logs that didn't go through the stream because of not meeting the flusher limitation. those logs will be send to streams immediately after reopening the application.(note that if you want to send the logs to stream as soon as possible, you can use `NoLimitFlusher` or
you can use ```groovy logger.f``` which is force level for logging)
`Loki` provides the capability of sending logs to the server.
Additionally, you can create custom log streams based on your specific requirements, such as sending logs to a database or a remote logging service. All you need to do is inheriting from LogStream.

## Log Streams

Log streams are like pathways where log messages travel. In this library, you'll find two built-in log streams: `FileStreamImpl` and `Loki`.

`FileStreamImpl` enables you to save logs to your device's internal storage. These logs are located at `Device File Explorer/data/data/your-package-name/files/logger`. When you check this location after logging,
you'll see your logger's files and an additional file named `file_stream_cache`. This file handles logs that weren't sent through the stream due to flusher limitations. These pending logs are sent to streams 
immediately upon reopening the application. (Note that if you want to send logs to the stream right away, you can use `NoLimitFlusher` or use `groovy logger.f` for force logging.)

`Loki` adds the ability to send logs to a server.

Moreover, you can create your own custom log streams to meet specific needs, such as sending logs to a database. Creating custom streams is as simple as inheriting from `LogStream`.

## Flushers

Flushers handle the flushing of log messages from a buffer to the corresponding log stream. The library offers different types of flushers to suit various needs. These include:
- `TimeFlusher`: Flushes log messages based on a specified time interval.
- `CountFlusher`: Flushes log messages once a certain count threshold is reached.
- `TimeAndCountFlusher`: Flushes log messages based on a time interval and count threshold.
- `NoLimitFlusher`: Flushes log messages immediately.

By choosing the right flusher, you can control how often logs will be sent, making sure your log management works well.
And if you want, you can create your own flusher by inheriting from Flusher.

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
logger.log(LogLevel.UserInteraction, "Tag", "UserInteraction message")
logger.log(LogLevel.Force, "Tag", "Force message")


// Close the logger when done
logger.close()
```

## Contributing

Contributions to the Poaro Library are welcome! If you encounter any issues or have suggestions for improvements, please submit an issue or pull request on the GitHub repository.

## License
    Copyright 2023 Snapp, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

## Credits
Based on [Reza Pilehvar](https://www.github.com/rezpilehvar)'s idea.
