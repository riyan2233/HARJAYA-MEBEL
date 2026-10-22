package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettingsEntity
import com.example.data.model.KloterMemberWithDetail
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotaScreen(
  settings: AppSettingsEntity?,
  shipments: List<KloterMemberWithDetail>,
  selectedNotaMemberId: Long?,
  onShareNota: (Context, KloterMemberWithDetail) -> Unit
) {
  val context = LocalContext.current
  val wonMembers = shipments.filter { it.member.isWon }

  var activeItem by remember(selectedNotaMemberId, wonMembers) {
    mutableStateOf(
      wonMembers.find { it.member.id == selectedNotaMemberId } ?: wonMembers.firstOrNull()
    )
  }
  var dropdownExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("nota_screen_lazy_column"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "Nota Resmi Arisan & Pengiriman Mebel",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Bukti sah pemenang undian arisan, data mebel, dan bukti pengantaran",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }
    }

    if (wonMembers.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Text(
            text = "Belum ada nota pemenang arisan mebel. Lakukan undian kloter di menu Kloter.",
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      // Nota Picker Dropdown
      item {
        ExposedDropdownMenuBox(
          expanded = dropdownExpanded,
          onExpandedChange = { dropdownExpanded = !dropdownExpanded }
        ) {
          OutlinedTextField(
            value = activeItem?.let { "${it.customer?.name} - ${it.product?.name} (${it.member.deliveryReceiptNo})" } ?: "Pilih Nota",
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilih Nota Pemenang Arisan") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = dropdownExpanded,
            onDismissRequest = { dropdownExpanded = false }
          ) {
            wonMembers.forEach { item ->
              DropdownMenuItem(
                text = { Text("${item.customer?.name} - ${item.product?.name} (Periode ${item.member.wonPeriod})") },
                onClick = {
                  activeItem = item
                  dropdownExpanded = false
                }
              )
            }
          }
        }
      }

      activeItem?.let { item ->
        item {
          // Authentic Teak Furniture Receipt Card
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
          ) {
            Column(modifier = Modifier.padding(20.dp)) {
              // Store Letterhead
              Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                  modifier = Modifier.size(48.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.Weekend,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(28.dp)
                    )
                  }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = settings?.storeName ?: "HARJAYA MEBEL",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                  ),
                  color = MaterialTheme.colorScheme.primary
                )
                Text(
                  text = settings?.slogan ?: "Arisan Mebel & Perabot Kayu Jati Mewah, Amanah",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.DarkGray
                )
                Text(
                  text = "${settings?.ownerName ?: "by Cak Riyan"} • WhatsApp: ${settings?.phone ?: "0812-3456-7890"}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                  text = settings?.address ?: "Jawa Timur",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.Gray,
                  textAlign = TextAlign.Center
                )
              }

              Spacer(modifier = Modifier.height(14.dp))
              HorizontalDivider(thickness = 1.5.dp, color = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.height(14.dp))

              // Nota Title & Resi
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "NOTA RESMI PEMENANG ARISAN",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                  )
                  Text(
                    text = "No. Nota: ${item.member.deliveryReceiptNo.ifBlank { "HM-" + item.member.id }}",
                    style = MaterialTheme.typography.labelSmall.copy(fontFamily = FontFamily.Monospace),
                    color = Color.DarkGray
                  )
                }
                Text(
                  text = "Tgl: ${item.member.deliveryDate.ifBlank { "Telah Terbit" }}",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.DarkGray
                )
              }

              Spacer(modifier = Modifier.height(14.dp))

              // 1. Data Pelanggan
              ReceiptSectionTitle("DATA PELANGGAN (PEMENANG)")
              ReceiptRow("Nama Penerima", item.customer?.name ?: "-")
              ReceiptRow("No. Telepon / WA", item.customer?.phone ?: "-")
              ReceiptRow("Alamat Pengantaran", item.customer?.address ?: "-")

              Spacer(modifier = Modifier.height(10.dp))

              // 2. Data Arisan
              ReceiptSectionTitle("INFORMASI KLOTER ARISAN")
              ReceiptRow("Nama Kloter", item.kloter?.name ?: "-")
              ReceiptRow("Nomor Undian", "No. ${item.member.lotteryNumber}")
              ReceiptRow("Pemenang Periode", "Periode ke-${item.member.wonPeriod} dari ${item.kloter?.totalPeriods ?: 10}")
              ReceiptRow("Iuran Arisan", "${formatRupiah(item.kloter?.installmentAmount ?: 0L)} / ${item.kloter?.frequency ?: "Bulan"}")

              Spacer(modifier = Modifier.height(10.dp))

              // 3. Rincian Mebel & Harga
              ReceiptSectionTitle("RINCIAN BARANG MEBEL")
              ReceiptRow("Nama Mebel", item.product?.name ?: "-")
              ReceiptRow("Kategori", item.product?.category ?: "-")
              ReceiptRow("Bahan / Kayu", item.product?.woodType ?: "Kayu Jati Solid")
              ReceiptRow(
                label = "NILAI HARGA BARANG",
                value = formatRupiah(item.product?.price ?: 0L),
                isBold = true,
                valueColor = MaterialTheme.colorScheme.primary
              )

              Spacer(modifier = Modifier.height(10.dp))

              // 4. Status Pengiriman
              ReceiptSectionTitle("STATUS PENGIRIMAN")
              ReceiptRow("Status Saat Ini", item.member.deliveryStatus)
              ReceiptRow("Armada / Sopir", item.member.driverName)
              ReceiptRow("Catatan", item.member.deliveryNotes.ifBlank { "Sesuai kesepakatan arisan" })

              Spacer(modifier = Modifier.height(16.dp))
              HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
              Spacer(modifier = Modifier.height(12.dp))

              // Stempel Toko & Tanda Tangan
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Official Stamp
                Box(
                  modifier = Modifier
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Verified,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "LUNAS & RESMI",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                }

                // Signature Cak Riyan
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text(
                    text = "Pengelola Arisan,",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.DarkGray
                  )
                  Spacer(modifier = Modifier.height(20.dp))
                  Text(
                    text = settings?.ownerName ?: "Cak Riyan",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black
                  )
                }
              }
            }
          }
        }

        // Action Buttons Row
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { onShareNota(context, item) },
              modifier = Modifier
                .weight(1f)
                .testTag("share_nota_whatsapp_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Bagikan Nota")
            }

            OutlinedButton(
              onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val text = "Nota Harjaya Mebel: ${item.member.deliveryReceiptNo}\nPenerima: ${item.customer?.name}\nMebel: ${item.product?.name}\nHarga: ${formatRupiah(item.product?.price ?: 0L)}\nStatus: ${item.member.deliveryStatus}"
                val clip = ClipData.newPlainText("Nota Harjaya Mebel", text)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Nota berhasil disalin ke papan klip!", Toast.LENGTH_SHORT).show()
              },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Salin")
            }
          }
        }
      }
    }
  }
}

@Composable
fun ReceiptSectionTitle(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.labelSmall.copy(
      fontWeight = FontWeight.Bold,
      letterSpacing = 0.5.sp
    ),
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(vertical = 4.dp)
  )
}

@Composable
fun ReceiptRow(
  label: String,
  value: String,
  isBold: Boolean = false,
  valueColor: Color = Color.Black
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = Color.DarkGray
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
      ),
      color = valueColor,
      textAlign = TextAlign.End
    )
  }
}
