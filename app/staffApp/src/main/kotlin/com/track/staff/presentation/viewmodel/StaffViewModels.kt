package com.track.staff.presentation.viewmodel

import com.google.firebase.auth.FirebaseAuth
import com.track.data.TrackingStateHolder
import com.track.data.repository.FirestoreRepository
import com.track.presentation.admin.SuperAdminViewModel
import com.track.presentation.auth.AuthViewModel
import com.track.presentation.driver.DriverViewModel
import com.track.presentation.staff.StaffViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StaffAuthViewModel @Inject constructor(
    auth: FirebaseAuth,
    repository: FirestoreRepository
) : AuthViewModel(auth, repository)

@HiltViewModel
class AppStaffViewModel @Inject constructor(
    repository: FirestoreRepository
) : StaffViewModel(repository)

@HiltViewModel
class AppDriverViewModel @Inject constructor(
    repository: FirestoreRepository,
    trackingStateHolder: TrackingStateHolder
) : DriverViewModel(repository, trackingStateHolder)

@HiltViewModel
class AppSuperAdminViewModel @Inject constructor(
    repository: FirestoreRepository
) : SuperAdminViewModel(repository)
