package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.DeliveryStatus
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusProcessing
import com.example.ui.theme.StatusReady
import com.example.ui.theme.StatusShipping
import com.example.ui.viewmodel.AppScreen
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Long): String {
  val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
  format.maximumFractionDigits = 0
  return format.format(amount).replace("Rp", "Rp ")
}

@Composable
fun AppHeader(
  settings: AppSettingsEntity?,
  isAdmin: Boolean,
  onAdminClick: () -> Unit,
  onMusicToggle: () -> Unit
) {
  val storeName = settings?.storeName ?: "HARJAYA MEBEL"
  val ownerName = settings?.ownerName ?: "by Cak Riyan"
  val isMusicOn = settings?.isMusicEnabled ?: false

  val infiniteTransition = rememberInfiniteTransition(label = "music_rotation")
  val rotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation"
  )

  Surface(
    modifier = Modifier.fillMaxWidth(),
    color = MaterialTheme.colorScheme.primary,
    shadowElevation = 6.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Store Logo
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.tertiary),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = when (settings?.logoIndex ?: 0) {
            1 -> Icons.Default.Weekend
            2 -> Icons.Default.Chair
            3 -> Icons.Default.DeliveryDining
            else -> Icons.Default.Chair
          },
          contentDescription = "Logo Harjaya Mebel",
          tint = Color.White,
          modifier = Modifier.size(26.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Title & Owner
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = storeName,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          ),
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = ownerName,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
          color = MaterialTheme.colorScheme.tertiary,
          maxLines = 1
        )
      }

      // Music Button
      IconButton(
        onClick = onMusicToggle,
        modifier = Modifier.testTag("music_toggle_button")
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = if (isMusicOn) Icons.Default.MusicNote else Icons.Default.MusicOff,
            contentDescription = "Toggle Musik",
            tint = if (isMusicOn) MaterialTheme.colorScheme.tertiary else Color.White.copy(alpha = 0.7f),
            modifier = if (isMusicOn) Modifier.rotate(rotation) else Modifier
          )
        }
      }

      // Admin Login/Logout Button
      Surface(
        onClick = onAdminClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isAdmin) MaterialTheme.colorScheme.tertiary else Color.White.copy(alpha = 0.2f),
        modifier = Modifier.testTag("admin_lock_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
            contentDescription = "Status Admin",
            tint = if (isAdmin) Color.Black else Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isAdmin) "Admin" else "Masuk",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (isAdmin) Color.Black else Color.White
          )
        }
      }
    }
  }
}

@Composable
fun AppBottomNav(
  currentScreen: AppScreen,
  onScreenSelect: (AppScreen) -> Unit
) {
  val items = listOf(
    AppScreen.HOME to Icons.Default.Home,
    AppScreen.KLOTER to Icons.Default.Group,
    AppScreen.SHIPPING to Icons.Default.DeliveryDining,
    AppScreen.PRODUCTS to Icons.Default.Weekend,
    AppScreen.CUSTOMERS to Icons.Default.Person,
    AppScreen.SETTINGS to Icons.Default.Settings
  )

  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    items.forEach { (screen, icon) ->
      val selected = currentScreen == screen
      NavigationBarItem(
        selected = selected,
        onClick = { onScreenSelect(screen) },
        icon = {
          Icon(
            imageVector = icon,
            contentDescription = screen.label
          )
        },
        label = {
          Text(
            text = screen.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        },
        colors = NavigationBarItemDefaults.colors(
          selectedIconColor = MaterialTheme.colorScheme.onPrimary,
          indicatorColor = MaterialTheme.colorScheme.primary,
          selectedTextColor = MaterialTheme.colorScheme.primary,
          unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
          unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
      )
    }
  }
}

@Composable
fun DeliveryStatusBadge(status: String) {
  val deliveryStatus = try {
    DeliveryStatus.valueOf(status)
  } catch (_: Exception) {
    DeliveryStatus.MENUNGGU_UNDIAN
  }

  val (bgColor, textColor, label) = when (deliveryStatus) {
    DeliveryStatus.MENUNGGU_UNDIAN -> Triple(StatusPending.copy(alpha = 0.15f), StatusPending, "Menunggu Undian")
    DeliveryStatus.DIPROSES -> Triple(StatusProcessing.copy(alpha = 0.15f), StatusProcessing, "Diproses Pengrajin")
    DeliveryStatus.SIAP_KIRIM -> Triple(StatusReady.copy(alpha = 0.15f), StatusReady, "Siap Dikirim")
    DeliveryStatus.DALAM_PENGIRIMAN -> Triple(StatusShipping.copy(alpha = 0.15f), StatusShipping, "Dalam Pengiriman")
    DeliveryStatus.TERKIRIM -> Triple(StatusDelivered.copy(alpha = 0.15f), StatusDelivered, "Sudah Diterima")
  }

  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(12.dp))
      .background(bgColor)
      .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
      color = textColor
    )
  }
}

@Composable
fun AdminLoginDialog(
  onDismiss: () -> Unit,
  onLogin: (String) -> Boolean
) {
  var pin by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Login Admin Cak Riyan", fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column {
        Text(
          "Masukkan PIN Admin untuk mengelola arisan mebel, undian, pelanggan, dan pengaturan toko.",
          style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
          value = pin,
          onValueChange = {
            if (it.length <= 6) {
              pin = it
              isError = false
            }
          },
          label = { Text("PIN Admin (Default: 1234)") },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
          isError = isError,
          supportingText = if (isError) {
            { Text("PIN salah! Silakan coba lagi.", color = MaterialTheme.colorScheme.error) }
          } else null,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_pin_input")
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (onLogin(pin)) {
            onDismiss()
          } else {
            isError = true
          }
        },
        modifier = Modifier.testTag("admin_submit_login_button")
      ) {
        Text("Masuk")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
