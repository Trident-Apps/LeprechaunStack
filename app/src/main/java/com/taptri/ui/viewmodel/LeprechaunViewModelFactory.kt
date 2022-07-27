package com.taptri.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.taptri.model.database.UrlDataBase

class LeprechaunViewModelFactory(private val app: Application, private val db: UrlDataBase) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LeprechaunViewModel(app, db) as T
    }

}