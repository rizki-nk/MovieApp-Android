package com.example.movieapp.ui.profile


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movieapp.data.local.UserEntity
import com.example.movieapp.ui.components.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    favoriteCount: Int,
    reviewCount: Int,
    onHomeClick: () -> Unit = {},
    onLibraryClick: () -> Unit = {},
    onLogoutConfirm: () -> Unit = {},
    onLoginClick: () -> Unit = {}
){
    var showLogoutDialog by remember { mutableStateOf(false) }

 Scaffold(
     topBar = {
         TopAppBar(
             title = { Text(text = "Profile Saya", fontWeight = FontWeight.Bold)}
         )
     },
 ) { innerPadding ->
     Column(
         modifier = Modifier
             .padding(innerPadding)
             .fillMaxSize()
             .padding(16.dp),
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.SpaceBetween
     ) {
         if (currentUser != null) {
             // 1. Avatar dengan Inisial Nama
             Box(
                 modifier = Modifier
                     .size(90.dp)
                     .clip(CircleShape)
                     .background(MaterialTheme.colorScheme.primary),
                 contentAlignment = Alignment.Center
             ) {
                 Text(
                     text = currentUser.name.take(1).uppercase(),
                     style = MaterialTheme.typography.headlineLarge,
                     color = MaterialTheme.colorScheme.onPrimary,
                     fontWeight = FontWeight.Bold
                 )
             }

             Spacer(modifier = Modifier.height(16.dp))

             // Nama & Username
             Text(
                 text = currentUser.name,
                 style = MaterialTheme.typography.titleLarge,
                 fontWeight = FontWeight.Bold
             )
             Text(
                 text = "@${currentUser.username}",
                 style = MaterialTheme.typography.bodyMedium,
                 color = MaterialTheme.colorScheme.onSurfaceVariant
             )

             // 2. Kartu Ringkasan Aktivitas (Statistik)
             Card(
                 modifier = Modifier.fillMaxWidth(),
                 shape = RoundedCornerShape(16.dp),
                 colors = CardDefaults.cardColors(
                     containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                 )
             ) {
                 Row(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(16.dp),
                     horizontalArrangement = Arrangement.SpaceAround,
                     verticalAlignment = Alignment.CenterVertically
                 ){
                     // Stat Favorit
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         Icon(
                             imageVector = Icons.Default.Favorite,
                             contentDescription = null,
                             tint = MaterialTheme.colorScheme.primary,
                             modifier = Modifier.size(28.dp)
                         )
                         Spacer(modifier = Modifier.height(4.dp))
                         Text(
                             text = "$favoriteCount",
                             fontWeight = FontWeight.Bold,
                             fontSize = 18.sp
                         )
                         Text(
                             text = "Favorit",
                             style = MaterialTheme.typography.bodySmall,
                             color = MaterialTheme.colorScheme.onSurfaceVariant
                         )
                     }

                     Divider(
                         modifier = Modifier
                             .height(40.dp)
                             .width(1.dp),
                         color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                     )

                     // Stat Ulasan
                     Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         Icon(
                             imageVector = Icons.Default.Star,
                             contentDescription = "null",
                             tint = Color(0xFFFFC107),
                             modifier = Modifier.size(28.dp)
                         )
                         Spacer(modifier = Modifier.height(4.dp))
                         Text(
                             text = "$reviewCount",
                             fontWeight = FontWeight.Bold,
                             fontSize = 18.sp
                         )
                         Text(
                             text = "ulasan",
                             style = MaterialTheme.typography.bodySmall,
                             color = MaterialTheme.colorScheme.onSurfaceVariant
                         )
                     }
                 }
             }

             Spacer(modifier = Modifier.weight(1f))

             // 3. Tombol Logout
             Button(
                 onClick = { showLogoutDialog = true },
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(50.dp),
                 colors = ButtonDefaults.buttonColors(
                     containerColor = MaterialTheme.colorScheme.error
                 ),
                 shape = RoundedCornerShape(12.dp)
             ) {
                 Icon(
                     imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                     contentDescription = "Logout"
                 )
                 Spacer(modifier = Modifier.width(8.dp))
                 Text("Keluar dari Akun", fontWeight = FontWeight.Bold)
             }

             Spacer(modifier = Modifier.height(16.dp))

          } else {
             // Tampilan jika pengguna belum login
              Column(
                  modifier = Modifier.fillMaxSize(),
                  horizontalAlignment = Alignment.CenterHorizontally,
                  verticalArrangement = Arrangement.Center
              ) {
                  Icon(
                      imageVector = Icons.Default.Person,
                      contentDescription = null,
                      modifier = Modifier.size(80.dp),
                      tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(16.dp))
                  Text(
                      text = "Kamu belum masuk ke akun",
                      style = MaterialTheme.typography.titleMedium
                  )
                  Spacer(modifier = Modifier.height(16.dp))
                  Button(onClick = onLoginClick) {
                      Text("Masuk / Daftar")
                  }
              }
         }
     }
 }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi keluar")},
            text = { Text("Apakah kamu yakin ingin keluar dari akun ini?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutConfirm()
                    }
                ) {
                    Text("Keluar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = {showLogoutDialog = false}) {
                    Text("Batal")
                }
            }
        )
    }
}
