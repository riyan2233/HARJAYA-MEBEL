package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.MahoganyPrimary
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TeakGoldPrimary

@Composable
fun SettingsScreen(
  settings: AppSettingsEntity?,
  isAdmin: Boolean,
  onLoginClick: () -> Unit,
  onLogoutClick: () -> Unit,
  onSaveSettings: (
    storeName: String,
    slogan: String,
    ownerName: String,
    phone: String,
    address: String,
    themeIndex: Int,
    logoIndex: Int,
    isMusic: Boolean,
    volume: Float,
    isSfx: Boolean,
    newPin: String
  ) -> Unit
) {
  val context = LocalContext.current

  var storeName by remember(settings) { mutableStateOf(settings?.storeName ?: "HARJAYA MEBEL") }
  var slogan by remember(settings) { mutableStateOf(settings?.slogan ?: "Arisan Mebel & Perabot Kayu Jati Mewah, Amanah") }
  var ownerName by remember(settings) { mutableStateOf(settings?.ownerName ?: "by Cak Riyan") }
  var phone by remember(settings) { mutableStateOf(settings?.phone ?: "0812-3456-7890") }
  var address by remember(settings) { mutableStateOf(settings?.address ?: "Jl. Raya Mebel & Ukir Jepara No. 88, Jawa Timur") }
  var selectedTheme by remember(settings) { mutableIntStateOf(settings?.themeIndex ?: 0) }
  var selectedLogo by remember(settings) { mutableIntStateOf(settings?.logoIndex ?: 0) }
  var isMusicEnabled by remember(settings) { mutableStateOf(settings?.isMusicEnabled ?: false) }
  var musicVolume by remember(settings) { mutableFloatStateOf(settings?.musicVolume ?: 0.5f) }
  var isSfxEnabled by remember(settings) { mutableStateOf(settings?.isSfxEnabled ?: true) }
  var newPin by remember(settings) { mutableStateOf(settings?.adminPin ?: "1234") }

  val themeOptions = listOf(
    "Jati Klasik Emas" to TeakGoldPrimary,
    "Emerald Teak" to EmeraldPrimary,
    "Royal Mahogany" to MahoganyPrimary,
    "Deep Navy" to NavyPrimary
  )

  val logoIcons = listOf(
    Icons.Default.Chair to "Kursi Jati",
    Icons.Default.Weekend to "Sofa Mewah",
    Icons.Default.Storefront to "Toko Mebel",
    Icons.Default.DeliveryDining to "Armada Kirim"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("settings_screen_lazy_column"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Text(
          text = "Pengaturan & Kustomisasi Toko",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Ubah nama toko, tema warna, logo, musik latar, dan PIN admin",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }
    }

    // 1. Status Admin
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isAdmin) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
          else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (isAdmin) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isAdmin) Icons.Default.LockOpen else Icons.Default.Lock,
                contentDescription = null,
                tint = if (isAdmin) Color.White else Color.DarkGray
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = if (isAdmin) "Status: Admin Aktif (Cak Riyan)" else "Status: Mode Tamu / Anggota",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = if (isAdmin) "Anda memiliki akses penuh mengubah data" else "Masuk admin untuk mengedit seluruh data",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }

          if (isAdmin) {
            OutlinedButton(
              onClick = onLogoutClick,
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Logout", fontSize = 12.sp)
            }
          } else {
            Button(
              onClick = onLoginClick,
              shape = RoundedCornerShape(10.dp)
            ) {
              Text("Login", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // 2. Kustomisasi Identitas Toko & Arisan
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Storefront, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Identitas Arisan & Toko Mebel",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = storeName,
            onValueChange = { storeName = it },
            label = { Text("Nama Toko / Arisan") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_store_name_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = slogan,
            onValueChange = { slogan = it },
            label = { Text("Slogan / Subtitle") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = ownerName,
            onValueChange = { ownerName = it },
            label = { Text("Pemilik / Brand (misal: by Cak Riyan)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Nomor HP / WhatsApp Toko") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Alamat Lengkap Showroom & Gudang") },
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    // 3. Pilihan Tema Warna
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Pilihan Tema Warna Aplikasi",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            themeOptions.forEachIndexed { index, (name, color) ->
              val isSelected = selectedTheme == index
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .clickable { selectedTheme = index }
                  .padding(4.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(
                      width = if (isSelected) 3.dp else 1.dp,
                      color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.LightGray,
                      shape = CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  if (isSelected) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = name.split(" ").first(),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }

    // 4. Pilihan Logo Toko
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Pilihan Logo Header Toko",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            logoIcons.forEachIndexed { index, (icon, label) ->
              val isSelected = selectedLogo == index
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                  .clickable { selectedLogo = index }
                  .padding(4.dp)
              ) {
                Surface(
                  shape = CircleShape,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier.size(46.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = icon,
                      contentDescription = label,
                      tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = label,
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              }
            }
          }
        }
      }
    }

    // 5. Pengaturan Musik & Audio
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Musik Latar & Efek Suara",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Music toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Musik Santai Showroom (Akustik Jati)",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
              )
              Text(
                text = "Melodi nada kayu menenangkan saat mengoperasikan aplikasi",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
            Switch(
              checked = isMusicEnabled,
              onCheckedChange = { isMusicEnabled = it },
              modifier = Modifier.testTag("music_switch")
            )
          }

          if (isMusicEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(text = "Volume Musik: ${(musicVolume * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
            }
            Slider(
              value = musicVolume,
              onValueChange = { musicVolume = it },
              valueRange = 0f..1f,
              modifier = Modifier.fillMaxWidth()
            )
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(10.dp))

          // Sound effects toggle
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Efek Suara Kocokan & Kemenangan",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
              )
              Text(
                text = "Suara drum-roll kocok arisan dan lonceng pesta pemenang",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
            Switch(
              checked = isSfxEnabled,
              onCheckedChange = { isSfxEnabled = it }
            )
          }
        }
      }
    }

    // 6. Ganti PIN Admin
    if (isAdmin) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Keamanan & Ganti PIN Admin",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = newPin,
              onValueChange = { if (it.length <= 6) newPin = it },
              label = { Text("PIN Admin Baru (4-6 angka)") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              visualTransformation = PasswordVisualTransformation(),
              singleLine = true,
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // 7. Simpan Semua Pengaturan
    item {
      Button(
        onClick = {
          onSaveSettings(
            storeName,
            slogan,
            ownerName,
            phone,
            address,
            selectedTheme,
            selectedLogo,
            isMusicEnabled,
            musicVolume,
            isSfxEnabled,
            newPin
          )
          Toast.makeText(context, "Pengaturan & tema berhasil disimpan!", Toast.LENGTH_SHORT).show()
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("save_settings_button")
      ) {
        Icon(Icons.Default.Save, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Simpan Seluruh Pengaturan", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }
    }
  }
}
