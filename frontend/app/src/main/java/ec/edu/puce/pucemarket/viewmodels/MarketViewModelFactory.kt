package ec.edu.puce.pucemarket.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ec.edu.puce.pucemarket.services.ApiService

class MarketViewModelFactory(private val api: ApiService) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>): T = MarketViewModel(api) as T
}
