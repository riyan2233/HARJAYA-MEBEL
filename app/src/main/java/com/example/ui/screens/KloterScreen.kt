package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerEntity
import com.example.data.model.KloterEntity
import com.example.data.model.KloterMemberEntity
import com.example.data.model.KloterMemberWithDetail
import com.example.data.model.ProductEntity
import com.example.ui.components.DeliveryStatusBadge
import com.example.ui.components.formatRupiah

@Composable
fun KloterScreen(
  kloters: List<KloterEntity>,
  allMembersWithDetail: List<KloterMemberWithDetail>,
  customers: List<CustomerEntity>,
  products: List<ProductEntity>,
  selectedKloterId: Long?,
  isAdmin: Boolean,
  onSelectKloter: (Long?) -> Unit,
  onCreateKloter: (name: String, amount: Long, freq: String, periods: Int) -> Unit,
  onAddMember: (kloterId: Long, customerId: Long, productId: Long, lotteryNo: Int) -> Unit,
  onDeleteMember: (KloterMemberEntity) -> Unit,
  onDeleteKloter: (KloterEntity) -> Unit,
  onStartLottery: (eligibleCandidates: List<KloterMemberWithDetail>, kloter: KloterEntity) -> Unit,
  onSelectNota: (Long) -> Unit,
  isRolling: Boolean,
  rollingCandidate: KloterMemberWithDetail?,
  wonResult: KloterMemberWithDetail?,
  showLotteryDialog: Boolean,
  onCloseLotteryDialog: () -> Unit
) {
  var showAddKloterDialog by remember { mutableStateOf(false) }
  var kloterToAddMemberTo by remember { mutableStateOf<KloterEntity?>(null) }
  var activeLotteryKloter by remember { mutableStateOf<KloterEntity?>(null) }

  Scaffold(
    floatingActionButton = {
      if (isAdmin) {
        FloatingActionButton(
          onClick = { showAddKloterDialog = true },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = Color.White,
          modifier = Modifier.testTag("add_kloter_fab")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Kloter")
        }
      }
    }
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(padding)
        .testTag("kloter_screen_lazy_column"),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "Grup Kloter Arisan Mebel",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = "Kelola anggota, iuran, dan kocok pemenang periode",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
          }
        }
      }

      if (kloters.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Text(
              text = "Belum ada kloter arisan mebel. Klik tombol + untuk menambahkan kloter baru.",
              modifier = Modifier.padding(24.dp),
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      } else {
        items(kloters) { kloter ->
          val membersInKloter = allMembersWithDetail.filter { it.member.kloterId == kloter.id }
          val isExpanded = selectedKloterId == kloter.id

          KloterCard(
            kloter = kloter,
            members = membersInKloter,
            isExpanded = isExpanded,
            isAdmin = isAdmin,
            onToggleExpand = {
              if (isExpanded) onSelectKloter(null) else onSelectKloter(kloter.id)
            },
            onAddMember = { kloterToAddMemberTo = kloter },
            onStartDraw = {
              val eligible = membersInKloter.filter { !it.member.isWon }
              activeLotteryKloter = kloter
              onStartLottery(eligible, kloter)
            },
            onDeleteMember = onDeleteMember,
            onDeleteKloter = { onDeleteKloter(kloter) },
            onSelectNota = onSelectNota
          )
        }
      }
    }
  }

  // Dialog: Add Kloter
  if (showAddKloterDialog) {
    AddKloterDialog(
      onDismiss = { showAddKloterDialog = false },
      onConfirm = { name, amount, freq, periods ->
        onCreateKloter(name, amount, freq, periods)
        showAddKloterDialog = false
      }
    )
  }

  // Dialog: Add Member to Kloter
  kloterToAddMemberTo?.let { kloter ->
    val existingMembers = allMembersWithDetail.filter { it.member.kloterId == kloter.id }
    AddMemberDialog(
      kloter = kloter,
      existingCount = existingMembers.size,
      customers = customers,
      products = products,
      onDismiss = { kloterToAddMemberTo = null },
      onConfirm = { customerId, productId, lotteryNo ->
        onAddMember(kloter.id, customerId, productId, lotteryNo)
        kloterToAddMemberTo = null
      }
    )
  }

  // Modal: Kocok Arisan (Lottery Draw Animation)
  if (showLotteryDialog && activeLotteryKloter != null) {
    LotteryDrawModal(
      kloter = activeLotteryKloter!!,
      isRolling = isRolling,
      rollingCandidate = rollingCandidate,
      wonResult = wonResult,
      onClose = {
        activeLotteryKloter = null
        onCloseLotteryDialog()
      },
      onViewNota = { memberId ->
        activeLotteryKloter = null
        onCloseLotteryDialog()
        onSelectNota(memberId)
      }
    )
  }
}

@Composable
fun KloterCard(
  kloter: KloterEntity,
  members: List<KloterMemberWithDetail>,
  isExpanded: Boolean,
  isAdmin: Boolean,
  onToggleExpand: () -> Unit,
  onAddMember: () -> Unit,
  onStartDraw: () -> Unit,
  onDeleteMember: (KloterMemberEntity) -> Unit,
  onDeleteKloter: () -> Unit,
  onSelectNota: (Long) -> Unit
) {
  val wonCount = members.count { it.member.isWon }
  val eligibleCount = members.count { !it.member.isWon }
  val progress = (kloter.currentPeriod.toFloat() / kloter.totalPeriods.toFloat()).coerceIn(0f, 1f)

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("kloter_card_${kloter.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(3.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = kloter.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Iuran: ${formatRupiah(kloter.installmentAmount)} / ${kloter.frequency}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
          )
        }
        Surface(
          color = if (kloter.status == "Aktif") MaterialTheme.colorScheme.primaryContainer else Color.LightGray.copy(alpha = 0.3f),
          shape = RoundedCornerShape(10.dp)
        ) {
          Text(
            text = kloter.status,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (kloter.status == "Aktif") MaterialTheme.colorScheme.onPrimaryContainer else Color.DarkGray,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Progress Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Periode ke-${kloter.currentPeriod} dari ${kloter.totalPeriods}",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
        )
        Text(
          text = "${members.size} Anggota (${wonCount} Sudah Dapat)",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Actions Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onStartDraw,
          enabled = kloter.status == "Aktif" && eligibleCount > 0,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("kocok_arisan_button_${kloter.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Casino,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Kocok Periode ${kloter.currentPeriod}",
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        OutlinedButton(
          onClick = onToggleExpand,
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(text = if (isExpanded) "Tutup" else "Anggota (${members.size})", fontSize = 12.sp)
          Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      // Expanded Section: Member list
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ) {
          HorizontalDivider()
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Daftar Anggota & Barang Mebel",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            if (isAdmin) {
              OutlinedButton(
                onClick = onAddMember,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
              ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Tambah", fontSize = 11.sp)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          if (members.isEmpty()) {
            Text(
              text = "Belum ada anggota di kloter ini.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
              modifier = Modifier.padding(vertical = 8.dp)
            )
          } else {
            members.forEach { item ->
              MemberItemRow(
                item = item,
                isAdmin = isAdmin,
                onDelete = { onDeleteMember(item.member) },
                onSelectNota = { onSelectNota(item.member.id) }
              )
              Spacer(modifier = Modifier.height(8.dp))
            }
          }

          if (isAdmin) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
              onClick = onDeleteKloter,
              colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.align(Alignment.End)
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Hapus Kloter", fontSize = 12.sp)
            }
          }
        }
      }
    }
  }
}

@Composable
fun MemberItemRow(
  item: KloterMemberWithDetail,
  isAdmin: Boolean,
  onDelete: () -> Unit,
  onSelectNota: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Lottery Number Badge
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(
            if (item.member.isWon) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "#${item.member.lotteryNumber}",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = if (item.member.isWon) Color.Black else MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.width(10.dp))

      // Member Info & Furniture Pick
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = item.customer?.name ?: "Anggota",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (item.member.isWon) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = "Dapat Periode ${item.member.wonPeriod}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                fontSize = 10.sp
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(2.dp))
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

      // Delivery Status / Nota
      Column(horizontalAlignment = Alignment.End) {
        DeliveryStatusBadge(status = item.member.deliveryStatus)
        if (item.member.isWon) {
          Spacer(modifier = Modifier.height(4.dp))
          Surface(
            onClick = onSelectNota,
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
              Spacer(modifier = Modifier.width(2.dp))
              Text("Nota", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      if (isAdmin && !item.member.isWon) {
        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Hapus Anggota",
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

@Composable
fun LotteryDrawModal(
  kloter: KloterEntity,
  isRolling: Boolean,
  rollingCandidate: KloterMemberWithDetail?,
  wonResult: KloterMemberWithDetail?,
  onClose: () -> Unit,
  onViewNota: (Long) -> Unit
) {
  val scale by animateFloatAsState(targetValue = if (wonResult != null) 1.05f else 1f, label = "scale")

  AlertDialog(
    onDismissRequest = { if (!isRolling) onClose() },
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        Icon(
          imageVector = Icons.Default.Casino,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Kocokan Arisan Harjaya",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleMedium
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "${kloter.name} - Periode ${kloter.currentPeriod}",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Center Rolling/Winner Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
              if (wonResult != null) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
              else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            )
            .border(
              width = 2.dp,
              color = if (wonResult != null) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
              shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          if (isRolling) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
                strokeWidth = 3.dp
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = rollingCandidate?.customer?.name ?: "Mengocok nama...",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
              )
              Text(
                text = rollingCandidate?.product?.name ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
              )
            }
          } else if (wonResult != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(38.dp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "SELAMAT KEPADA:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
              )
              Text(
                text = wonResult.customer?.name ?: "Pemenang",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = wonResult.product?.name ?: "Mebel",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
              )
              Text(
                text = "Nilai Mebel: ${formatRupiah(wonResult.product?.price ?: 0L)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
              )
            }
          } else {
            Text(
              text = "Menunggu kocokan dimulai...",
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }

        if (wonResult != null) {
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "Barang mebel otomatis masuk ke antrean pengiriman & nota pemenang telah dibuat!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
          )
        }
      }
    },
    confirmButton = {
      if (wonResult != null) {
        Button(
          onClick = { onViewNota(wonResult.member.id) },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Lihat Nota")
        }
      }
    },
    dismissButton = {
      if (!isRolling) {
        OutlinedButton(onClick = onClose) {
          Text(if (wonResult != null) "Tutup" else "Batal")
        }
      }
    }
  )
}

@Composable
fun AddKloterDialog(
  onDismiss: () -> Unit,
  onConfirm: (name: String, amount: Long, freq: String, periods: Int) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("350000") }
  var freq by remember { mutableStateOf("Bulanan") }
  var periodsStr by remember { mutableStateOf("10") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Buat Kloter Arisan Baru", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nama Kloter (misal: Kloter Jati Mahoni)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it },
          label = { Text("Iuran per Periode (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = freq,
          onValueChange = { freq = it },
          label = { Text("Frekuensi (Bulanan / Mingguan)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
          value = periodsStr,
          onValueChange = { periodsStr = it },
          label = { Text("Total Periode (misal: 10)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val amount = amountStr.toLongOrNull() ?: 350000L
          val periods = periodsStr.toIntOrNull() ?: 10
          if (name.isNotBlank()) {
            onConfirm(name, amount, freq, periods)
          }
        }
      ) {
        Text("Simpan Kloter")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
  kloter: KloterEntity,
  existingCount: Int,
  customers: List<CustomerEntity>,
  products: List<ProductEntity>,
  onDismiss: () -> Unit,
  onConfirm: (customerId: Long, productId: Long, lotteryNo: Int) -> Unit
) {
  var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }
  var selectedProduct by remember { mutableStateOf(products.firstOrNull()) }
  var lotteryNoStr by remember { mutableStateOf("${existingCount + 1}") }

  var customerExpanded by remember { mutableStateOf(false) }
  var productExpanded by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Tambah Anggota ke ${kloter.name}", fontWeight = FontWeight.Bold) },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Customer Selector
        ExposedDropdownMenuBox(
          expanded = customerExpanded,
          onExpandedChange = { customerExpanded = !customerExpanded }
        ) {
          OutlinedTextField(
            value = selectedCustomer?.name ?: "Pilih Pelanggan",
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilih Pelanggan") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = customerExpanded,
            onDismissRequest = { customerExpanded = false }
          ) {
            customers.forEach { customer ->
              DropdownMenuItem(
                text = { Text("${customer.name} (${customer.phone})") },
                onClick = {
                  selectedCustomer = customer
                  customerExpanded = false
                }
              )
            }
          }
        }

        // Product (Mebel) Selector
        ExposedDropdownMenuBox(
          expanded = productExpanded,
          onExpandedChange = { productExpanded = !productExpanded }
        ) {
          OutlinedTextField(
            value = selectedProduct?.let { "${it.name} - ${formatRupiah(it.price)}" } ?: "Pilih Mebel",
            onValueChange = {},
            readOnly = true,
            label = { Text("Pilihan Barang Mebel") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = productExpanded) },
            modifier = Modifier
              .menuAnchor()
              .fillMaxWidth()
          )
          ExposedDropdownMenu(
            expanded = productExpanded,
            onDismissRequest = { productExpanded = false }
          ) {
            products.forEach { product ->
              DropdownMenuItem(
                text = { Text("${product.name} (${formatRupiah(product.price)})") },
                onClick = {
                  selectedProduct = product
                  productExpanded = false
                }
              )
            }
          }
        }

        // Lottery Number
        OutlinedTextField(
          value = lotteryNoStr,
          onValueChange = { lotteryNoStr = it },
          label = { Text("Nomor Undian") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val customerId = selectedCustomer?.id ?: return@Button
          val productId = selectedProduct?.id ?: return@Button
          val lotteryNo = lotteryNoStr.toIntOrNull() ?: (existingCount + 1)
          onConfirm(customerId, productId, lotteryNo)
        }
      ) {
        Text("Daftarkan Anggota")
      }
    },
    dismissButton = {
      OutlinedButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}
