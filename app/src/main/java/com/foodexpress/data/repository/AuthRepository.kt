package com.foodexpress.data.repository

import com.foodexpress.core.model.User
import com.foodexpress.core.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Boolean
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String, role: UserRole): Result<User>
    suspend fun logout()
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Fetch user document from Firestore
                firestore.collection("users").document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject(User::class.java)?.copy(uid = firebaseUser.uid)
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Login failed")
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
                ?.copy(uid = uid)
                ?: throw Exception("User data not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Registration failed")
            val user = User(
                uid = uid,
                name = name,
                email = email,
                role = role
            )
            firestore.collection("users").document(uid).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }
}
