package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DeliveryStatus
import com.example.data.model.KloterMemberWithDetail
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.formatRupiah
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ShippingScreen(
  shipments: List<KloterMemberWithDetail>,
  isAdmin: Boolean,
  onUpdateStatus: (memberId: Long, status: DeliveryStatus, driver: String, date: String, notes: String) -> Unit,
  onSelectNota: (Long) -> Unit
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf<DeliveryStatus?>(null) }
  var editingItem by remember { mutableStateOf<KloterMemberWithDetail?>(null) }

  val filteredShipments = shipments.filter { item ->
    val matchesFilter = selectedStatusFilter == null || item.member.deliveryStatus == selectedStatusFilter!!.name
    val query = searchQuery.trim().lowercase()
    val matchesQuery = query.isEmpty() ||
      (item.customer?.name?.lowercase()?.contains(query) == true) ||
      (item.product?.name?.lowercase()?.contains(query) == true) ||
      (item.kloter?.name?.lowercase()?.contains(query) == true) ||
      item.member.deliveryReceiptNo.lowercase().contains(query) ||
      item.member.driverName.lowercase().contains(query)
    matchesFilter && matchesQuery
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("shipping_screen_lazy_column"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Column {
        Text(
          text = "Pengiriman Barang & Mebel",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
          text = "Lacak armada ekspedisi Cak Riyan, kloter, nama barang, harga & status",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Cari penerima, mebel, resi, sopir...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("shipping_search_input")
      )
    }

    // Filter Chips
    item {
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
      ) {
        item {
          FilterChip(
            selected = selectedStatusFilter == null,
            onClick = { selectedStatusFilter = null },
            label = { Text("Semua (${shipments.size})") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = Color.White
            )
          )
        }
        items(DeliveryStatus.values()) { status ->
          val count = shipments.count { it.member.deliveryStatus == status.name }
          FilterChip(
            selected = selectedStatusFilter == status,
            onClick = { selectedStatusFilter = status },
            label = { Text("${status.label} ($count)") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    if (filteredShipments.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Text(
            text = "Tidak ada data pengiriman mebel dengan filter saat ini.",
            modifier = Modifier.padding(24.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      items(filteredShipments) { item ->
        ShippingCard(
          item = item,
          isAdmin = isAdmin,
          onEditClick = { editingItem = item },
          onViewNota = { onSelectNota(item.member.id) },
          onContactClick = {
            val phone = item.customer?.phone?.replace(Regex("[^0-9]"), "") ?: ""
            if (phone.isNotBlank()) {
              val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
              context.startActivity(intent)
            }
          }
        )
      }
    }
  }

  // Edit Delivery Status Dialog
  editingItem?.let { item ->
    EditShippingDialog(
      item = item,
      onDismiss = { editingItem = null },
      onConfirm = { status, driver, date, notes ->
        onUpdateStatus(item.member.id, status, driver, date, notes)
        editingItem = null
      }
    )
  }
}

@Composable
fun ShippingCard(
  item: KloterMemberWithDetail,
  isAdmin: Boolean,
  onEditClick: () -> Unit,
  onViewNota: () -> Unit,
  onContactClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("shipping_card_${item.member.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top Row: Resi & Status Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = if (item.member.deliveryReceiptNo.isNotBlank()) item.member.deliveryReceiptNo else "RESI: BELUM TERBIT",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "Kloter: ${item.kloter?.name ?: "Arisan Mebel"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
        DeliveryStatusBadge(status = item.member.deliveryStatus)
      }

      Spacer(modifier = Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(12.dp))

      // Customer & Item Specs
      Row(modifier = Modifier.fillMaxWidth()) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocalShipping,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.customer?.name ?: "Penerima",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = item.customer?.address ?: "Alamat belum diatur",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Furniture Details & Price
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(8.dp)) {
              Text(
                text = item.product?.name ?: "Barang Mebel",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = "Harga Barang: ${formatRupiah(item.product?.price ?: 0L)}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Bahan: ${item.product?.woodType ?: "Kayu Jati Asli"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          // Driver & Timeline
          Text(
            text = "Armada/Sopir: ${item.member.driverName}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
          )
          if (item.member.deliveryDate.isNotBlank()) {
            Text(
              text = "Tanggal Pengiriman: ${item.member.deliveryDate}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
          }
          if (item.member.deliveryNotes.isNotBlank()) {
            Text(
              text = "Catatan: ${item.member.deliveryNotes}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Bottom Action Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = onContactClick,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Hubungi", fontSize = 12.sp)
        }

        OutlinedButton(
          onClick = onViewNota,
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Nota", fontSize = 12.sp)
        }

        if (isAdmin) {
          Button(
            onClick = onEditClick,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Update Status", fontSize = 12.sp)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShippingDialog(
  item: KloterMemberWithDetail,
  onDismiss: () -> Unit,
  onConfirm: (DeliveryStatus, String, String, String) -> Unit
) {
  var selectedStatus by remember {
    mutableStateOf(
      try {
        DeliveryStatus.valueOf(item.member.deliveryStatus)
      } catch (_: Exception) {
        DeliveryStatus.DIPROSES
      }
    )
  }
  var driverName by remember { mutableStateOf(item.member.driverName) }
  var deliveryDate by remember {
    mutableStateOf(
      if (item.member.deliveryDate.isNotBlank()) item.member.deliveryDate
      else SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    )
  }
  var notes by remember { mutableStateOf(item.member.deliveryNotes) }
  var statusMenuExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Update Status Pengiriman", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Penerima: ${item.customer?.name} - ${item.product?.name}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Bold
        )

        // Status Dropdown
        ExposedDropdownMenuBox(
          expanded = statusMenuExpanded,
          onExpandedChange = { statusMenuExpanded = !statusMenuExpanded }
        ) {
          OutlinedTextField(
            value = selectedStatus.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Status Pengiriman") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = statusMenuExpanded,
            onDismissRequest = { statusMenuExpanded = false }
          ) {
            DeliveryStatus.values().forEach { status ->
              DropdownMenuItem(
                text = { Text(status.label) },
                onClick = {
                  selectedStatus = status
                  statusMenuExpanded = false
                }
              )
            }
          }
        }

        OutlinedTextField(
          value = driverName,
          onValueChange = { driverName = it },
          label = { Text("Nama Sopir / Armada") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = deliveryDate,
          onValueChange = { deliveryDate = it },
          label = { Text("Tanggal Pengiriman") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Catatan Pengiriman") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirm(selectedStatus, driverName, deliveryDate, notes)
        }
      ) {
        Text("Simpan Status")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
