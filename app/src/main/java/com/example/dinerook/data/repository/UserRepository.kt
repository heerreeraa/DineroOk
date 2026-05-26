package com.example.dinerook.data.repository

import com.example.dinerook.data.dao.UserDao
import com.example.dinerook.data.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(email: String, password: String): Boolean {
        return try {
            if (userDao.emailExists(email) > 0) {
                false
            } else {
                userDao.insertUser(User(email, password))
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun login(email: String, password: String): User? =
        userDao.login(email, password)

    suspend fun emailExists(email: String): Boolean =
        userDao.emailExists(email) > 0
}
