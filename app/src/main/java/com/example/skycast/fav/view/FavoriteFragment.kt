package com.example.skycast.fav.view

import SharedWeatherViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skycast.map.view.MapFragment
import com.example.skycast.R
import com.example.skycast.db.WeatherLocalDataSourceImpl
import com.example.skycast.fav.viewmodel.FavViewModel
import com.example.skycast.fav.viewmodel.FavViewModelFactory
import com.example.skycast.home.view.HomeFragment
import com.example.skycast.model.WeatherRepositoryImpl
import com.example.skycast.network.WeatherRemoteDataSourceImpl
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private lateinit var viewModel: FavViewModel
    private lateinit var adapter: FavouriteAdapter
    private lateinit var recyclerView: RecyclerView
    private val remoteDataSource = WeatherRemoteDataSourceImpl()
    private lateinit var localDataSource: WeatherLocalDataSourceImpl
    private lateinit var sharedWeatherViewModel: SharedWeatherViewModel
    private lateinit var buttonPlus: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        localDataSource = WeatherLocalDataSourceImpl(requireContext())
        sharedWeatherViewModel = ViewModelProvider(requireActivity()).get(SharedWeatherViewModel::class.java)

        val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
        val factory = FavViewModelFactory(repository, requireContext())
        viewModel = ViewModelProvider(this, factory).get(FavViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerView)
        setupRecyclerView()

        buttonPlus = view.findViewById(R.id.buttonPlus)
        buttonPlus.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.fragment_container, MapFragment())
                addToBackStack(null)
            }
        }

        setupObservers()

        return view
    }

    private fun setupRecyclerView() {
        adapter = FavouriteAdapter(
            { currentWeather ->
                viewModel.deleteCurrentWeather(currentWeather)
            },
            onImageClick = { idKey ->
                viewModel.getCurrentWeatherById(idKey)
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.currentGetWeather.collect { currentWeather ->
                        viewModel.currentGetForCastWeather.collect { forecastWeather ->
                            if (currentWeather != null && forecastWeather != null) {
                                sharedWeatherViewModel.setWeatherData(currentWeather, forecastWeather)
                                HomeFragment.isCurrentLocation = false
                                parentFragmentManager.commit {
                                    replace(R.id.fragment_container, HomeFragment())
                                    addToBackStack(null)
                                }
                            }
                        }
                    }
                }
            }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentWeatherList.collect { weatherList ->
                adapter.setWeatherList(weatherList)
            }
        }
        viewModel.fetchAllCurrentWeather()
    }
}
