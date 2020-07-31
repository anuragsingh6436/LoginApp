package com.example.myfirstapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myfirstapp.Repository.UserRepository
import com.example.myfirstapp.Room.UserEntity
import com.example.myfirstapp.Room.UserRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModelRegister(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository
    lateinit var usersList:List<UserEntity>
    var userInfo: LiveData<List<UserEntity>>
    var checkUser = MutableLiveData<Boolean>()

    init {
        val userDao = UserRoomDatabase.getDatabase(application,viewModelScope).userDao()
        repository = UserRepository(userDao)
        userInfo = repository.userinfo
    }

    fun insert(user: UserEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }
    fun findByEmail(email:String) = viewModelScope.launch(Dispatchers.IO) {
        usersList = repository.findByEmail(email)
        if(usersList.isNotEmpty()) {
            if(usersList[0].email == email) {
                checkUser.postValue(true)
            }
            else {
                checkUser.postValue(false)
            }
        }
        else {
            checkUser.postValue(false)
        }
    }
}