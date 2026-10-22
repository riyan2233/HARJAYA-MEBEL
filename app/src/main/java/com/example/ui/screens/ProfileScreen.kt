package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.DeliveryStatus
import com.example.data.model.KloterMemberWithDetail
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.formatRupiah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  customers: List<CustomerEntity>,
  shipments: List<KloterMemberWithDetail>,
  selectedCustomerId: Long?,
  onSelectCustomer: (Long) -> Unit,
  onSelectNota: (Long) -> Unit
) {
  var activeCustomer by remember(selectedCustomerId, customers) {
    mutableStateOf(
      customers.find { it.id == selectedCustomerId } ?: customers.firstOrNull()
    )
  }
  var dropdownExpanded by remember { mutableStateOf(false) }

  val customerMemberships = shipments.filter { it.customer?.id == activeCustomer?.id }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("profile_screen_lazy_column"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "Profil Anggota Arisan",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Lihat status keikutsertaan kloter, barang mebel, dan progres pengiriman",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }
    }

    // Customer Selector Dropdown
    item {
      ExposedDropdownMenuBox(
        expanded = dropdownExpanded,
        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
      ) {
        OutlinedTextField(
          value = activeCustomer?.let { "${it.name} (${it.phone})" } ?: "Pilih Anggota",
          onValueChange = {},
          readOnly = true,
          label = { Text("Pilih Anggota / Pelanggan") },
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
          modifier = Modifier
            .menuAnchor()
            .fillMaxWidth()
            .testTag("customer_profile_selector")
        )
        ExposedDropdownMenu(
          expanded = dropdownExpanded,
          onDismissRequest = { dropdownExpanded = false }
        ) {
          customers.forEach { customer ->
            DropdownMenuItem(
              text = { Text("${customer.name} - ${customer.phone}") },
              onClick = {
                activeCustomer = customer
                onSelectCustomer(customer.id)
                dropdownExpanded = false
              }
            )
          }
        }
      }
    }

    activeCustomer?.let { customer ->
      // Profile Hero Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(3.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(32.dp)
                )
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = customer.name,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                  text = customer.phone,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Bergabung: ${customer.joinedDate.ifBlank { "Tahun 2025" }}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Top) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = customer.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
              )
            }
          }
        }
      }

      // Statistics Row
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val wonCount = customerMemberships.count { it.member.isWon }
          val totalKloter = customerMemberships.size

          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "$totalKloter",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Kloter Diikuti",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }

          Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "$wonCount",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.tertiary
              )
              Text(
                text = "Undian Menang",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }
        }
      }

      // Memberships & Furniture Details
      item {
        Text(
          text = "Riwayat Arisan & Pengiriman Mebel",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }

      if (customerMemberships.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Text(
              text = "Anggota ini belum terdaftar di kloter manapun. Tambahkan anggota melalui menu Kloter.",
              modifier = Modifier.padding(20.dp),
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      } else {
        items(customerMemberships) { item ->
          MemberProfileDetailCard(
            item = item,
            onSelectNota = { onSelectNota(item.member.id) }
          )
        }
      }
    }
  }
}

@Composable
fun MemberProfileDetailCard(
  item: KloterMemberWithDetail,
  onSelectNota: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Kloter info & status
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.kloter?.name ?: "Kloter Arisan",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = "Nomor Undian: #${item.member.lotteryNumber}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        }
        DeliveryStatusBadge(status = item.member.deliveryStatus)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Furniture Selected
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Weekend,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.product?.name ?: "Barang Mebel",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Harga: ${formatRupiah(item.product?.price ?: 0L)} • ${item.product?.woodType ?: "Jati Jepara"}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Delivery Stepper Timeline
      Text(
        text = "Progres Pengiriman Mebel",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
      Spacer(modifier = Modifier.height(8.dp))

      val currentStatus = try {
        DeliveryStatus.valueOf(item.member.deliveryStatus)
      } catch (_: Exception) {
        DeliveryStatus.MENUNGGU_UNDIAN
      }

      val steps = listOf(
        "1. Undian Menang" to (item.member.isWon),
        "2. Finishing Pengrajin" to (currentStatus.ordinal >= DeliveryStatus.DIPROSES.ordinal),
        "3. Siap di Gudang" to (currentStatus.ordinal >= DeliveryStatus.SIAP_KIRIM.ordinal),
        "4. Armada Pengiriman" to (currentStatus.ordinal >= DeliveryStatus.DALAM_PENGIRIMAN.ordinal),
        "5. Diterima di Rumah" to (currentStatus.ordinal >= DeliveryStatus.TERKIRIM.ordinal)
      )

      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        steps.forEach { (label, isCompleted) ->
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
              contentDescription = null,
              tint = if (isCompleted) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = label,
              style = MaterialTheme.typography.labelSmall,
              color = if (isCompleted) MaterialTheme.colorScheme.onSurface else Color.Gray.copy(alpha = 0.5f),
              fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }

      if (item.member.isWon) {
        Spacer(modifier = Modifier.height(14.dp))
        Button(
          onClick = onSelectNota,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Buka Nota Resmi Arisan")
        }
      }
    }
  }
}
