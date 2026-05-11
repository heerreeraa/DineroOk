package com.example.dinerook.data.dao

import androidx.room.*
import com.example.dinerook.data.entity.User

/**
 * DAO para operaciones de usuarios
 */
@Dao
interface UserDao {

    /**
     * Inserta un nuevo usuario
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User)

    /**
     * Obtiene un usuario por email
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    /**
     * Verifica credenciales de login
     */
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    /**
     * Verifica si existe un email
     */
    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    suspend fun emailExists(email: String): Int
}

