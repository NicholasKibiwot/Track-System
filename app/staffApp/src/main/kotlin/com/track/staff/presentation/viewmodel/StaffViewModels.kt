package com.track.staff.presentation.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.presentation.auth.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffAuthViewModel @Inject constructor(
    auth: FirebaseAuth,
    repository: FirestoreRepository
) : AuthViewModel(auth, repository)
