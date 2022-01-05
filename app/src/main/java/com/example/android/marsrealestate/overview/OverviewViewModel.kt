
package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */

enum class MarsApiStatus { LOADING, ERROR, DONE }

class OverviewViewModel : ViewModel() {

     //create coroutine job and scope using the main dispatcher it will
    // still executing code in the scope of main thread or ui thread
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()
    private val _propertyObjectList = MutableLiveData<List<MarsProperty>>()

    val propertyObjectList : LiveData<List<MarsProperty>>
       get()  = _propertyObjectList

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    // Internally, we use a MutableLiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()

    // The external immutable LiveData for the navigation property
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty
    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        // using retrofit callback

//        MarsApi.retrofitService.getProperties().enqueue(object : retrofit2.Callback<List<MarsProperty>>{
//            override fun onResponse(call: Call<List<MarsProperty>>, response: Response<List<MarsProperty>>) {
//              //  _response.value = response.body()
//                _response.value = "Success: ${response.body()?.size} Mars properties retrieved"
//               // _propertyObjectList.value = response.body()
//            }
//
//            override fun onFailure(call: Call<List<MarsProperty>>, t: Throwable) {
//                _response.value = "failure ${t.message}"
//            }
//
//        })

        //using deferred instead of retrofit callback
        coroutineScope.launch {
            try {
                var getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
                _status.value = MarsApiStatus.LOADING
                var listResult = getPropertiesDeferred.await()
                if(listResult.isNotEmpty()){
                    _propertyObjectList.value = listResult
                    _status.value = MarsApiStatus.DONE
                }
            }catch (t : Throwable){
                _status.value = MarsApiStatus.ERROR
                _propertyObjectList.value = ArrayList()
            }

        }
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [MarsProperty] that was clicked on.
     */
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    fun updateFilter(filter: MarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
