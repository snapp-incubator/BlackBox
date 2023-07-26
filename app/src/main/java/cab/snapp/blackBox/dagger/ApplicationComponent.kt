package cab.snapp.blackBox.dagger

import cab.snapp.blackBox.MainActivity
import cab.snapp.blackBox.MyApplication
import dagger.BindsInstance
import dagger.Component
import java.io.File
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class])
interface ApplicationComponent {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance fileDirectory: File
        ): ApplicationComponent
    }

    fun inject(activity: MainActivity)
    fun inject(application: MyApplication)
}