package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppSettingsEntity
import com.example.data.model.DeliveryStatus
import com.example.data.model.KloterEntity
import com.example.data.model.KloterMemberWithDetail
import com.example.data.model.ProductEntity
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.formatRupiah
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
  settings: AppSettingsEntity?,
  kloters: List<KloterEntity>,
  shipments: List<KloterMemberWithDetail>,
  products: List<ProductEntity>,
  totalCustomers: Int,
  onNavigate: (AppScreen) -> Unit,
  onSelectKloter: (Long) -> Unit,
  onSelectNota: (Long) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("home_screen_lazy_column"),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 1. Hero Furniture Showroom Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(190.dp)
      ) {
        Image(
          painter = painterResource(id = R.drawable.hero_furniture),
          contentDescription = "Showroom Mebel Jepara",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        // Dark gradient overlay for readability
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                startY = 60f
              )
            )
        )
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = MaterialTheme.colorScheme.tertiary,
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = settings?.ownerName ?: "by Cak Riyan",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Mebel Asli Bergaransi",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White.copy(alpha = 0.9f)
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = settings?.storeName ?: "HARJAYA MEBEL",
            style = MaterialTheme.typography.headlineSmall.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            ),
            color = Color.White
          )
          Text(
            text = settings?.slogan ?: "Arisan Mebel & Perabot Kayu Jati Mewah, Amanah",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      }
    }

    // 2. Quick Stat Counters
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val activeKloterCount = kloters.count { it.status == "Aktif" }
        val activeDeliveryCount = shipments.count { it.member.deliveryStatus == DeliveryStatus.DALAM_PENGIRIMAN.name || it.member.deliveryStatus == DeliveryStatus.SIAP_KIRIM.name }

        StatCardItem(
          title = "Kloter Aktif",
          value = "$activeKloterCount",
          icon = Icons.Default.Group,
          modifier = Modifier.weight(1f)
        )
        StatCardItem(
          title = "Pengiriman",
          value = "$activeDeliveryCount",
          icon = Icons.Default.DeliveryDining,
          modifier = Modifier.weight(1f)
        )
        StatCardItem(
          title = "Pelanggan",
          value = "$totalCustomers",
          icon = Icons.Default.Person,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Quick Action Buttons
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Menu Cepat Arisan",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            QuickActionButton(
              title = "Kocok Arisan",
              icon = Icons.Default.Casino,
              onClick = { onNavigate(AppScreen.KLOTER) }
            )
            QuickActionButton(
              title = "Pengiriman",
              icon = Icons.Default.DeliveryDining,
              onClick = { onNavigate(AppScreen.SHIPPING) }
            )
            QuickActionButton(
              title = "Nota Resmi",
              icon = Icons.Default.Receipt,
              onClick = { onNavigate(AppScreen.NOTA) }
            )
            QuickActionButton(
              title = "Katalog Mebel",
              icon = Icons.Default.Weekend,
              onClick = { onNavigate(AppScreen.PRODUCTS) }
            )
          }
        }
      }
    }

    // 4. Kloter Arisan Highlight
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Kloter Arisan Sedang Berjalan",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        OutlinedButton(
          onClick = { onNavigate(AppScreen.KLOTER) },
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Text("Semua", fontSize = 12.sp)
        }
      }
    }

    val activeKloters = kloters.filter { it.status == "Aktif" }.take(3)
    if (activeKloters.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Text(
            text = "Belum ada kloter arisan aktif. Silakan buat kloter baru di menu Kloter.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      items(activeKloters) { kloter ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(2.dp),
          onClick = {
            onSelectKloter(kloter.id)
            onNavigate(AppScreen.KLOTER)
          }
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = kloter.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Periode ${kloter.currentPeriod}/${kloter.totalPeriods}",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Iuran: ${formatRupiah(kloter.installmentAmount)} / ${kloter.frequency}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            val progress = (kloter.currentPeriod.toFloat() / kloter.totalPeriods.toFloat()).coerceIn(0f, 1f)
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
          }
        }
      }
    }

    // 5. Pengiriman Mebel Terkini
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Status Pengiriman Mebel",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        OutlinedButton(
          onClick = { onNavigate(AppScreen.SHIPPING) },
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
        ) {
          Text("Lacak", fontSize = 12.sp)
        }
      }
    }

    val activeDeliveries = shipments.filter { it.member.isWon }.take(3)
    if (activeDeliveries.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Text(
            text = "Belum ada antrean pengiriman mebel. Lakukan undian arisan di menu Kloter.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium
          )
        }
      }
    } else {
      items(activeDeliveries) { item ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(2.dp),
          onClick = {
            onSelectNota(item.member.id)
          }
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.DeliveryDining,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = item.customer?.name ?: "Pelanggan",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = item.product?.name ?: "Barang Mebel",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "Harga: ${formatRupiah(item.product?.price ?: 0L)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
              DeliveryStatusBadge(status = item.member.deliveryStatus)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Nota >",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun StatCardItem(
  title: String,
  value: String,
  icon: ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(2.dp)
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
fun QuickActionButton(
  title: String,
  icon: ImageVector,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.padding(horizontal = 4.dp)
  ) {
    Surface(
      onClick = onClick,
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
      modifier = Modifier.size(52.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(26.dp)
        )
      }
    }
    Spacer(modifier = Modifier.height(6.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
      fontSize = 11.sp,
      maxLines = 1
    )
  }
}
