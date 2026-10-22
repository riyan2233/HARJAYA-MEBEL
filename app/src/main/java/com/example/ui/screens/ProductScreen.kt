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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chair
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.components.formatRupiah

@Composable
fun ProductScreen(
  products: List<ProductEntity>,
  isAdmin: Boolean,
  onSaveProduct: (id: Long, name: String, category: String, price: Long, desc: String, woodType: String) -> Unit,
  onDeleteProduct: (ProductEntity) -> Unit
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Semua") }
  var showAddProductDialog by remember { mutableStateOf(false) }
  var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

  val categories = listOf("Semua", "Kursi & Sofa", "Meja Makan", "Lemari & Buffet", "Tempat Tidur")

  val filteredProducts = products.filter { product ->
    val matchesCategory = selectedCategory == "Semua" || product.category.contains(selectedCategory)
    val query = searchQuery.trim().lowercase()
    val matchesQuery = query.isEmpty() ||
      product.name.lowercase().contains(query) ||
      product.woodType.lowercase().contains(query) ||
      product.description.lowercase().contains(query)
    matchesCategory && matchesQuery
  }

  Scaffold(
    floatingActionButton = {
      if (isAdmin) {
        FloatingActionButton(
          onClick = { showAddProductDialog = true },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White,
          modifier = Modifier.testTag("add_product_fab")
        ) {
          Icon(Icons.Default.Add, contentDescription = "Tambah Mebel")
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .testTag("product_screen_lazy_column"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Column {
          Text(
            text = "Katalog Mebel & Barang Arisan",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Koleksi perabot kayu jati Jepara pilihan untuk anggota arisan",
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
          placeholder = { Text("Cari kursi tamu, lemari ukir, meja jati...") },
          leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("product_search_input")
        )
      }

      // Category Chips
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          items(categories) { category ->
            FilterChip(
              selected = selectedCategory == category,
              onClick = { selectedCategory = category },
              label = { Text(category) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              )
            )
          }
        }
      }

      if (filteredProducts.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Text(
              text = "Tidak ada produk mebel yang cocok dengan pencarian Anda.",
              modifier = Modifier.padding(24.dp),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      } else {
        items(filteredProducts) { product ->
          ProductCard(
            product = product,
            isAdmin = isAdmin,
            onEdit = { editingProduct = product },
            onDelete = { onDeleteProduct(product) }
          )
        }
      }
    }
  }

  // Add Product Dialog
  if (showAddProductDialog) {
    ProductFormDialog(
      product = null,
      onDismiss = { showAddProductDialog = false },
      onConfirm = { name, cat, price, desc, wood ->
        onSaveProduct(0L, name, cat, price, desc, wood)
        showAddProductDialog = false
      }
    )
  }

  // Edit Product Dialog
  editingProduct?.let { product ->
    ProductFormDialog(
      product = product,
      onDismiss = { editingProduct = null },
      onConfirm = { name, cat, price, desc, wood ->
        onSaveProduct(product.id, name, cat, price, desc, wood)
        editingProduct = null
      }
    )
  }
}

@Composable
fun ProductCard(
  product: ProductEntity,
  isAdmin: Boolean,
  onEdit: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("product_card_${product.id}"),
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
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (product.iconKey) {
              "sofa" -> Icons.Default.Weekend
              else -> Icons.Default.Chair
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(26.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = product.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ) {
            Text(
              text = product.category,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = product.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "Harga Mebel",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
          )
          Text(
            text = formatRupiah(product.price),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = product.woodType,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary
          )
        }

        if (isAdmin) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onEdit) {
              Icon(Icons.Default.Edit, contentDescription = "Edit Mebel", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
              Icon(Icons.Default.Delete, contentDescription = "Hapus Mebel", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProductFormDialog(
  product: ProductEntity?,
  onDismiss: () -> Unit,
  onConfirm: (name: String, category: String, price: Long, desc: String, wood: String) -> Unit
) {
  var name by remember { mutableStateOf(product?.name ?: "") }
  var category by remember { mutableStateOf(product?.category ?: "Kursi & Sofa") }
  var priceStr by remember { mutableStateOf(product?.price?.toString() ?: "3500000") }
  var woodType by remember { mutableStateOf(product?.woodType ?: "Kayu Jati Solid Jepara") }
  var desc by remember { mutableStateOf(product?.description ?: "") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(if (product == null) "Tambah Mebel Baru" else "Edit Data Mebel", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nama Mebel") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = category,
          onValueChange = { category = it },
          label = { Text("Kategori (Kursi & Sofa, Meja Makan, Lemari, dll)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = priceStr,
          onValueChange = { priceStr = it },
          label = { Text("Harga Barang (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = woodType,
          onValueChange = { woodType = it },
          label = { Text("Jenis Kayu / Bahan") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("Deskripsi / Spesifikasi Lengkap") },
          maxLines = 3,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val price = priceStr.toLongOrNull() ?: 0L
          if (name.isNotBlank()) {
            onConfirm(name, category, price, desc, woodType)
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
