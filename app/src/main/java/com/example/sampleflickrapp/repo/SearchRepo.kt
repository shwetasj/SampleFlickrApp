package com.example.sampleflickrapp.repo

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.example.flickr.OpenForTesting
import com.example.sampleflickrapp.data.Data
import com.example.sampleflickrapp.data.Photo
import com.example.sampleflickrapp.repo.remote.SearchDataSourceFactory
import com.example.sampleflickrapp.repo.remote.SearchRemoteDataSource
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@OpenForTesting
class SearchRepo @Inject constructor(private val searchRemoteDataSource: SearchRemoteDataSource) {

    fun searchPhoto(query: String): Data<Photo> {
        Log.e("SearchRepo"," $query ")
        val dataSourceFactory = SearchDataSourceFactory(searchRemoteDataSource,
            query
        )

        val networkState = Transformations.switchMap(dataSourceFactory.liveData) {
            it.networkState
        }

        return Data(
            LivePagedListBuilder(dataSourceFactory,
                SearchDataSourceFactory.pagedListConfig())
                .build(),networkState)

    }


}