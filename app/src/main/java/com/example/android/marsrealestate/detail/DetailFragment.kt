
package com.example.android.marsrealestate.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentDetailBinding

/**
 * This [Fragment] will show the detailed information about a selected piece of Mars real estate.
 */
class DetailFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        @Suppress("UNUSED_VARIABLE")
        val application = requireNotNull(activity).application
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val marsProperty = DetailFragmentArgs.fromBundle(arguments!!).selectedProperty
        val viewModelFactory = DetailViewModelFactory(marsProperty, application)
        val viewModel = ViewModelProvider(
            this, viewModelFactory).get(DetailViewModel::class.java)

        viewModel.propertyObject.observe(viewLifecycleOwner, Observer {property->
            val imgUri = property.imgSrcUrl.toUri().buildUpon().scheme("https").build()
            Glide.with(binding.mainPhotoImage.context)
                .load(imgUri)
                .apply(RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
                .into(binding.mainPhotoImage)

            val displayType =   application.applicationContext.getString(R.string.display_type,
                    application.applicationContext.getString(
                        when(property.isRental){
                            true -> R.string.type_rent
                            false ->R.string.type_sale
                        }
                    )
                )

           val displayPrice =  application.applicationContext.getString(when(property.isRental){
                true -> R.string.display_price_monthly_rental
                false -> R.string.display_price
            }, property.price)

            binding.propertyTypeText.text = displayType
            binding.priceValueText.text = displayPrice

        })


        return binding.root
    }
}
