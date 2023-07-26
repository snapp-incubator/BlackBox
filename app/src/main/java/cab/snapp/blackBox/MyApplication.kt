package cab.snapp.blackBox

import android.app.Application

import cab.snapp.blackBox.crashHandler.CrashHandler
import cab.snapp.blackBox.dagger.ApplicationComponent
import cab.snapp.blackBox.dagger.DaggerApplicationComponent
import javax.inject.Inject


class MyApplication : Application() {

    @Inject
    lateinit var crashHandler: CrashHandler


    companion object {
        lateinit var component: ApplicationComponent
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerApplicationComponent.factory().create(this.filesDir)
        component.inject(this)

        crashHandler.startWatching()
    }
}
