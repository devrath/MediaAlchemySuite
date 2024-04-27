package com.istudio.media3.di

import com.istudio.media3.main.MainViewModel
import com.istudio.media3.main.selection.SelectionScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { MainViewModel() }
    viewModel { SelectionScreenViewModel() }
    // single { MyRepository(get()) }
}