package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DeliveryStatus(val label: String, val badgeColorHex: Long) {
  MENUNGGU_UNDIAN("Menunggu Undian", 0xFF78909C),
  DIPROSES("Diproses Pengrajin", 0xFFFFB74D),
  SIAP_KIRIM("Siap Dikirim Gudang", 0xFF64B5F6),
  DALAM_PENGIRIMAN("Dalam Pengiriman", 0xFF9575CD),
  TERKIRIM("Sudah Diterima Pelanggan", 0xFF81C784)
}

@Entity(tableName = "customers")
data class CustomerEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val phone: String,
  val address: String,
  val avatarKey: String = "person_1",
  val notes: String = "",
  val joinedDate: String = ""
)

@Entity(tableName = "products")
data class ProductEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val category: String, // Kursi & Sofa, Lemari & Buffet, Meja Makan, Tempat Tidur, dsb
  val price: Long,
  val description: String,
  val woodType: String = "Kayu Jati Asli Jepara",
  val iconKey: String = "chair"
)

@Entity(tableName = "kloters")
data class KloterEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String, // misal: Kloter Mawar Jati Emas
  val installmentAmount: Long, // e.g. Rp 250.000
  val frequency: String = "Bulanan", // Bulanan, Mingguan
  val totalPeriods: Int = 10,
  val currentPeriod: Int = 1,
  val status: String = "Aktif", // Aktif, Selesai
  val createdAt: String = ""
)

@Entity(tableName = "kloter_members")
data class KloterMemberEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val kloterId: Long,
  val customerId: Long,
  val productId: Long,
  val lotteryNumber: Int,
  val isWon: Boolean = false,
  val wonPeriod: Int = 0,
  val deliveryStatus: String = DeliveryStatus.MENUNGGU_UNDIAN.name,
  val driverName: String = "Armada Harjaya (Cak Riyan)",
  val deliveryReceiptNo: String = "",
  val deliveryDate: String = "",
  val deliveryNotes: String = ""
)

@Entity(tableName = "payments")
data class PaymentEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val kloterId: Long,
  val customerId: Long,
  val periodNumber: Int,
  val amount: Long,
  val paymentDate: String,
  val receiptNumber: String,
  val notes: String = "Lunas"
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
  @PrimaryKey val id: Int = 1,
  val storeName: String = "HARJAYA MEBEL",
  val slogan: String = "Arisan Barang & Mebel Berkualitas, Amanah & Bergaransi",
  val ownerName: String = "by Cak Riyan",
  val phone: String = "0812-3456-7890",
  val address: String = "Jl. Raya Mebel & Ukir Jepara No. 88, Jawa Timur",
  val adminPin: String = "1234",
  val themeIndex: Int = 0, // 0: Jati Klasik, 1: Emerald Teak, 2: Royal Mahogany, 3: Ocean Navy
  val logoIndex: Int = 0, // 0: Kursi Jati, 1: Sofa Mewah, 2: Meja Kayu, 3: Truk Kirim
  val isMusicEnabled: Boolean = false,
  val musicVolume: Float = 0.5f,
  val isSfxEnabled: Boolean = true
)

data class KloterMemberWithDetail(
  val member: KloterMemberEntity,
  val customer: CustomerEntity?,
  val product: ProductEntity?,
  val kloter: KloterEntity?
)
