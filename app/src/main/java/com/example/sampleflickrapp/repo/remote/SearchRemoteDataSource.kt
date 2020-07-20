package com.example.sampleflickrapp.repo.remote

import com.example.flickr.OpenForTesting
import com.example.sampleflickrapp.data.BaseDataSource
import com.example.sampleflickrapp.data.FlickrApi
import com.example.sampleflickrapp.data.Result
import com.example.sampleflickrapp.data.SearchResult
import javax.inject.Inject

@OpenForTesting
class SearchRemoteDataSource @Inject constructor(val service: FlickrApi) : BaseDataSource() {

    val map = HashMap<String,String>()

    init {
        map["method"] = "flickr.photos.search"
        map["api_key"] = "870f72f01774cdd8f7dd711bd0cae368"
        map["format"] = "json"

    }

     fun search(perPage:Int,query: String,page:Int) : Result<SearchResult> {

        map["text"] = query
        return getResult { service.searchPhotos(perPage,page,map) }
    }

}