package com.track.presentation.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.track.data.repository.FirestoreRepository
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.customer.CustomerViewModel
import com.track.presentation.admin.SuperAdminViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppAuthViewModel @Inject constructor(
    auth: FirebaseAuth,
    repository: FirestoreRepository
) : AuthViewModel(auth, repository)

@HiltViewModel
class AppCustomerViewModel @Inject constructor(
    repository: FirestoreRepository
) : CustomerViewModel(repository)

@HiltViewModel
class AppSuperAdminViewModel @Inject constructor(
    repository: FirestoreRepository
) : SuperAdminViewModel(repository)
