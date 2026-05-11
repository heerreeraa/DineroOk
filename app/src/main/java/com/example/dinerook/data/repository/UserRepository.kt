package com.example.dinerook.data.repository

import com.example.dinerook.data.dao.UserDao
import com.example.dinerook.data.entity.User

/**
 * Repository para gestionar operaciones de usuarios
 */
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

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun emailExists(email: String): Boolean {
        return userDao.emailExists(email) > 0
    }
}

