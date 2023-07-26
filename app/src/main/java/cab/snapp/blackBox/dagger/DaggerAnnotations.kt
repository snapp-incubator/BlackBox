package cab.snapp.blackBox.dagger

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class CountLimitFlusher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeAndCountLimitFlusher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class LimitLessFlusher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class TimeLimitFlusher