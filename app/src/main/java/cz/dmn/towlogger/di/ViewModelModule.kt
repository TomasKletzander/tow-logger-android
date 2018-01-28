package cz.dmn.towlogger.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cz.dmn.towlogger.ui.pickpilot.PickPilotViewModel
import cz.dmn.towlogger.ui.utils.ViewModelFactory
import cz.dmn.towlogger.ui.utils.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PickPilotViewModel::class)
    abstract fun bindViewModel(model: PickPilotViewModel): ViewModel

    @Binds
    abstract fun bindFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}