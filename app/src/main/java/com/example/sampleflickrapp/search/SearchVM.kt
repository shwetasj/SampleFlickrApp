package com.example.sampleflickrapp.search

import androidx.lifecycle.ViewModel
import com.example.sampleflickrapp.data.Data
import com.example.sampleflickrapp.data.Photo
import com.example.sampleflickrapp.repo.SearchRepo
import javax.inject.Inject

class SearchVM @Inject constructor(val searchRepo: SearchRepo) : ViewModel() {

    var data : Data<Photo>? = null



    var oldQuery: String  = ""

    fun search(query:String) : Data<Photo>? {
        if(data == null || oldQuery != query)
            data = searchRepo.searchPhoto(query)

        oldQuery = query
        return data
    }
}