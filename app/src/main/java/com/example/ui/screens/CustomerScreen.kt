package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.ui.viewmodel.AppScreen

@Composable
fun CustomerScreen(
  customers: List<CustomerEntity>,
  isAdmin: Boolean,
  onSaveCustomer: (id: Long, name: String, phone: String, address: String, notes: String) -> Unit,
  onDeleteCustomer: (CustomerEntity) -> Unit,
  onViewProfile: (Long) -> Unit
) {
  val context = LocalContext.current
  var searchQuery by remember { mutableStateOf("") }
  var showAddDialog by remember { mutableStateOf(false) }
  var editingCustomer by remember { mutableStateOf<CustomerEntity?>(null) }

  val filteredCustomers = customers.filter { c ->
    val query = searchQuery.trim().lowercase()
    query.isEmpty() ||
      c.name.lowercase().contains(query) ||
      c.phone.contains(query) ||
      c.address.lowercase().contains(query)
  }

  Scaffold(
    floatingActionButton = {
      if (isAdmin) {
        FloatingActionButton(
          onClick = { showAddDialog = true },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White,
          modifier = Modifier.testTag("add_customer_fab")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Tambah Pelanggan")
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .testTag("customer_screen_lazy_column"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Column {
          Text(
            text = "Data Pelanggan & Anggota",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Daftar nomor kontak, alamat pengiriman mebel, dan profil arisan",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
          )
        }
      }

      // Search Field
      item {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Cari nama, no HP, atau alamat pelanggan...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("customer_search_input")
        )
      }

      if (filteredCustomers.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Text(
              text = "Tidak ada data pelanggan yang sesuai.",
              modifier = Modifier.padding(24.dp),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      } else {
        items(filteredCustomers) { customer ->
          CustomerCard(
            customer = customer,
            isAdmin = isAdmin,
            onCall = {
              val cleanPhone = customer.phone.replace(Regex("[^0-9]"), "")
              if (cleanPhone.isNotBlank()) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
                context.startActivity(intent)
              }
            },
            onViewProfile = { onViewProfile(customer.id) },
            onEdit = { editingCustomer = customer },
            onDelete = { onDeleteCustomer(customer) }
          )
        }
      }
    }
  }

  // Add Dialog
  if (showAddDialog) {
    CustomerFormDialog(
      customer = null,
      onDismiss = { showAddDialog = false },
      onConfirm = { name, phone, address, notes ->
        onSaveCustomer(0L, name, phone, address, notes)
        showAddDialog = false
      }
    )
  }

  // Edit Dialog
  editingCustomer?.let { customer ->
    CustomerFormDialog(
      customer = customer,
      onDismiss = { editingCustomer = null },
      onConfirm = { name, phone, address, notes ->
        onSaveCustomer(customer.id, name, phone, address, notes)
        editingCustomer = null
      }
    )
  }
}

@Composable
fun CustomerCard(
  customer: CustomerEntity,
  isAdmin: Boolean,
  onCall: () -> Unit,
  onViewProfile: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("customer_card_${customer.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = customer.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Text(
            text = customer.phone,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = Icons.Default.LocationOn,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
          modifier = Modifier
            .size(16.dp)
            .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = customer.address,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
      }

      if (customer.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Catatan: ${customer.notes}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedButton(
            onClick = onCall,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Hubungi", fontSize = 12.sp)
          }

          Button(
            onClick = onViewProfile,
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Profil Arisan", fontSize = 12.sp)
          }
        }

        if (isAdmin) {
          Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            IconButton(onClick = onEdit) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Pelanggan", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
              Icon(Icons.Default.Delete, contentDescription = "Hapus Pelanggan", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }
  }
}

@Composable
fun CustomerFormDialog(
  customer: CustomerEntity?,
  onDismiss: () -> Unit,
  onConfirm: (name: String, phone: String, address: String, notes: String) -> Unit
) {
  var name by remember { mutableStateOf(customer?.name ?: "") }
  var phone by remember { mutableStateOf(customer?.phone ?: "") }
  var address by remember { mutableStateOf(customer?.address ?: "") }
  var notes by remember { mutableStateOf(customer?.notes ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (customer == null) "Tambah Pelanggan Baru" else "Edit Data Pelanggan", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nama Lengkap") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Nomor HP / WhatsApp") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Alamat Pengantaran Mebel") },
          maxLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Catatan / Keterangan Tambahan") },
          maxLines = 2,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isNotBlank()) {
            onConfirm(name, phone, address, notes)
          }
        }
      ) {
        Text("Simpan")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
