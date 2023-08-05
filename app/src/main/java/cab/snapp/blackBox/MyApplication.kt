package cab.snapp.blackBox

import android.app.Application
import cab.snapp.blackBox.crashHandler.CrashHandler
import cab.snapp.blackBox.dagger.ApplicationComponent
import cab.snapp.blackBox.dagger.DaggerApplicationComponent
import cab.snapp.blackBox.poaro.SnappLogger
import cab.snapp.blackBox.poaro.f
import javax.inject.Inject


class MyApplication : Application() {

    @Inject
    lateinit var crashHandler: CrashHandler

    @Inject
    lateinit var logger: SnappLogger

    companion object {
        lateinit var component: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.factory().create(this.filesDir){ throwable ->
            logger.f("Crash", throwable.stackTraceToString())
        }
        component.inject(this)

        crashHandler.startWatching()
    }
}
