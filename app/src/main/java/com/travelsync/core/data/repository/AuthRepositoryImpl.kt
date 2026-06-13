package com.travelsync.core.data.repository

import com.travelsync.core.data.local.dao.AppSessionDao
import com.travelsync.core.data.local.dao.UserDao
import com.travelsync.core.data.local.entity.AppSessionEntity
import com.travelsync.core.data.local.entity.UserEntity
import com.travelsync.core.data.local.mapper.toDomain
import com.travelsync.core.domain.model.TravelStats
import com.travelsync.core.domain.model.User
import com.travelsync.core.domain.repository.AuthRepository
import com.travelsync.core.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val appSessionDao: AppSessionDao
) : AuthRepository {

    override val currentUser: Flow<User?> =
        appSessionDao.observeSession().flatMapLatest { session ->
            val userId = session?.currentUserId
            if (userId == null) {
                flowOf(null)
            } else {
                userDao.observeUser(userId).mapLatest { entity ->
                    entity?.toDomain(TravelStats(totalTrips = userDao.countActiveTrips(entity.id)))
                }
            }
        }

    override suspend fun isLoggedIn(): Boolean =
        appSessionDao.getSession()?.currentUserId != null

    override suspend fun register(
        username: String,
        password: String,
        displayName: String,
        email: String?
    ): Result<User> = runCatching {
        val normalizedUsername = username.trim().lowercase()
        require(normalizedUsername.length >= 3) { "Username must be at least 3 characters" }
        require(password.length >= 6) { "Password must be at least 6 characters" }
        require(displayName.isNotBlank()) { "Display name is required" }

        if (userDao.getUserByUsername(normalizedUsername) != null) {
            throw IllegalArgumentException("Username already taken")
        }

        val hash = PasswordHasher.hash(password)
        val now = System.currentTimeMillis()
        val userId = UUID.randomUUID().toString()
        val entity = UserEntity(
            id = userId,
            username = normalizedUsername,
            passwordHash = hash.hash,
            passwordSalt = hash.salt,
            displayName = displayName.trim(),
            email = email?.trim()?.ifEmpty { null },
            bio = null,
            homeCurrency = "USD",
            createdAt = now,
            updatedAt = now,
            lastLoginAt = now
        )
        userDao.insertUser(entity)
        appSessionDao.saveSession(AppSessionEntity(currentUserId = userId))
        entity.toDomain()
    }

    override suspend fun login(username: String, password: String): Result<User> = runCatching {
        val entity = userDao.getUserByUsername(username.trim().lowercase())
            ?: throw IllegalArgumentException("Invalid username or password")
        if (!PasswordHasher.verify(password, entity.passwordSalt, entity.passwordHash)) {
            throw IllegalArgumentException("Invalid username or password")
        }
        val now = System.currentTimeMillis()
        userDao.updateUser(entity.copy(lastLoginAt = now, updatedAt = now))
        appSessionDao.saveSession(AppSessionEntity(currentUserId = entity.id))
        entity.copy(lastLoginAt = now).toDomain()
    }

    override suspend fun logout(): Result<Unit> = runCatching {
        appSessionDao.clearSession()
    }

    override suspend fun updateProfile(
        displayName: String,
        email: String?,
        bio: String?,
        homeCurrency: String
    ): Result<User> = runCatching {
        val session = appSessionDao.getSession()?.currentUserId
            ?: throw IllegalStateException("Not logged in")
        val existing = userDao.getUserById(session)
            ?: throw IllegalStateException("User not found")
        val now = System.currentTimeMillis()
        val updated = existing.copy(
            displayName = displayName.trim(),
            email = email?.trim()?.ifEmpty { null },
            bio = bio?.trim()?.ifEmpty { null },
            homeCurrency = homeCurrency.ifBlank { "USD" },
            updatedAt = now
        )
        userDao.updateUser(updated)
        updated.toDomain(TravelStats(totalTrips = userDao.countActiveTrips(updated.id)))
    }
}
