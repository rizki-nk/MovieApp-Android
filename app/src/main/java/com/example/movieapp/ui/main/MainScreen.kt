package com.example.movieapp.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.movieapp.data.local.UserEntity
import com.example.movieapp.ui.home.HomeScreen
import com.example.movieapp.ui.home.HomeViewModel
import com.example.movieapp.ui.library.LibraryScreen
import com.example.movieapp.ui.library.LibraryViewModel
import com.example.movieapp.ui.profile.ProfileScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable

fun MainScreen(
    homeViewModel: HomeViewModel,
    libraryViewModel: LibraryViewModel,
    currentUserId: Int,
    currentUser: UserEntity?,
    username: String,
    favoriteCount: Int,
    reviewCount: Int,
    onMovieClick: (Int) -> Unit,
    onSeeAllClick: () -> Unit,
    onLogoutClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
){
    // 1. Inisialisasi PagerState untuk 3 Halaman (0 = Home, 1 = Profile, 2 = Library)
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope ()

    // 2. Mengambil state library secara reaktif dari LibraryViewModel
    val libraryUiState by libraryViewModel.getFavorites(currentUserId).collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                // 1. Tab Home (Index 0)
                NavigationBarItem(
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    icon =  { Icon(Icons.Default.Home, contentDescription = "Home" )},
                    label = { Text("Home") }
                )

                //2. Tab Profile (Index 1 - Tengah)
                NavigationBarItem(
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    icon =  { Icon(Icons.Default.Person, contentDescription = "Profile" )},
                    label = { Text("Profile") }
                )

                // 3. Tab Library (Index 2 - Kanan)
                NavigationBarItem(
                    selected = pagerState.currentPage == 2,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(2)
                        }
                    },
                    icon =  { Icon(Icons.Default.CalendarMonth, contentDescription = "Library" )},
                    label = { Text("Library") }
                )
            }
        }
    ) { innerPading ->
        // HorizontalPager dengan urutan Home (0) -> Profile (1) -> Library (2)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPading)
        ) { page ->
            when (page) {
                0 -> HomeScreen(
                    viewModel = homeViewModel,
                    username = username,
                    isLoggedIn = true,
                    onMovieClick = onMovieClick,
                    onSeeAllClick = onSeeAllClick,
                    onProfileClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                    onLibraryClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(2) }
                    }
                )
                1 -> ProfileScreen(
                    currentUser = currentUser,
                    favoriteCount = favoriteCount,
                    reviewCount = reviewCount,
                    onHomeClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    },
                    onLibraryClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(1) }
                    },
                    onLogoutConfirm = onLogoutClick,
                    onLoginClick = onLoginClick
                )
                2 -> LibraryScreen(
                    currentUserId = currentUserId,
                    uiState = libraryUiState,
                    viewModel = libraryViewModel,
                    username = username,
                    isLoggedIn = true,
                    onMovieClick = onMovieClick,
                    onDeleteClick = { favoriteMovie ->
                        libraryViewModel.removeFavorite(favoriteMovie)
                    },
                    onHomeClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(0) }
                    },
                    onProfileClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(2) }
                    }
                )
            }
        }
    }
}