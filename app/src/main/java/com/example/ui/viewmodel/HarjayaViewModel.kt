package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.HarjayaDatabase
import com.example.data.model.AppSettingsEntity
import com.example.data.model.CustomerEntity
import com.example.data.model.DeliveryStatus
import com.example.data.model.KloterEntity
import com.example.data.model.KloterMemberEntity
import com.example.data.model.KloterMemberWithDetail
import com.example.data.model.ProductEntity
import com.example.data.repository.HarjayaRepository
import com.example.sound.ArisanSoundEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen(val label: String) {
  HOME("Beranda"),
  KLOTER("Kloter Arisan"),
  SHIPPING("Pengiriman"),
  PRODUCTS("Mebel & Barang"),
  CUSTOMERS("Pelanggan"),
  NOTA("Nota Arisan"),
  PROFILE("Profil Anggota"),
  SETTINGS("Pengaturan Toko")
}

class HarjayaViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: HarjayaRepository
  val soundEngine = ArisanSoundEngine()

  init {
    val db = HarjayaDatabase.getDatabase(application)
    repository = HarjayaRepository(db)
    viewModelScope.launch {
      repository.seedInitialDataIfEmpty()
    }
  }

  val customers: StateFlow<List<CustomerEntity>> = repository.allCustomers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val products: StateFlow<List<ProductEntity>> = repository.allProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val kloters: StateFlow<List<KloterEntity>> = repository.allKloters
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val members: StateFlow<List<KloterMemberEntity>> = repository.allMembers
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val shipments: StateFlow<List<KloterMemberWithDetail>> = repository.shipmentsWithDetail
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val settings: StateFlow<AppSettingsEntity?> = repository.appSettings
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // Navigation & UI States
  private val _currentScreen = MutableStateFlow(AppScreen.HOME)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  private val _selectedKloterId = MutableStateFlow<Long?>(null)
  val selectedKloterId: StateFlow<Long?> = _selectedKloterId.asStateFlow()

  private val _selectedCustomerId = MutableStateFlow<Long?>(null)
  val selectedCustomerId: StateFlow<Long?> = _selectedCustomerId.asStateFlow()

  private val _selectedNotaMemberId = MutableStateFlow<Long?>(null)
  val selectedNotaMemberId: StateFlow<Long?> = _selectedNotaMemberId.asStateFlow()

  // Admin Security
  private val _isAdmin = MutableStateFlow(false)
  val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

  private val _showAdminLoginDialog = MutableStateFlow(false)
  val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

  // Lottery / Kocokan Arisan State
  private val _showLotteryDialog = MutableStateFlow(false)
  val showLotteryDialog: StateFlow<Boolean> = _showLotteryDialog.asStateFlow()

  private val _isRolling = MutableStateFlow(false)
  val isRolling: StateFlow<Boolean> = _isRolling.asStateFlow()

  private val _rollingCandidate = MutableStateFlow<KloterMemberWithDetail?>(null)
  val rollingCandidate: StateFlow<KloterMemberWithDetail?> = _rollingCandidate.asStateFlow()

  private val _wonResult = MutableStateFlow<KloterMemberWithDetail?>(null)
  val wonResult: StateFlow<KloterMemberWithDetail?> = _wonResult.asStateFlow()

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun selectKloter(kloterId: Long?) {
    _selectedKloterId.value = kloterId
  }

  fun selectCustomer(customerId: Long?) {
    _selectedCustomerId.value = customerId
  }

  fun selectNotaMember(memberId: Long?) {
    _selectedNotaMemberId.value = memberId
    if (memberId != null) {
      _currentScreen.value = AppScreen.NOTA
    }
  }

  fun openAdminLoginDialog() {
    _showAdminLoginDialog.value = true
  }

  fun closeAdminLoginDialog() {
    _showAdminLoginDialog.value = false
  }

  fun loginAdmin(pin: String): Boolean {
    val currentPin = settings.value?.adminPin ?: "1234"
    return if (pin == currentPin) {
      _isAdmin.value = true
      _showAdminLoginDialog.value = false
      true
    } else {
      false
    }
  }

  fun logoutAdmin() {
    _isAdmin.value = false
  }

  fun toggleMusic() {
    val current = settings.value ?: return
    val newMusicState = !current.isMusicEnabled
    viewModelScope.launch {
      repository.updateSettings(current.copy(isMusicEnabled = newMusicState))
    }
    if (newMusicState) {
      soundEngine.setVolume(current.musicVolume)
      soundEngine.startBackgroundMusic(viewModelScope)
    } else {
      soundEngine.stopBackgroundMusic()
    }
  }

  fun updateSettings(
    storeName: String,
    slogan: String,
    ownerName: String,
    phone: String,
    address: String,
    themeIndex: Int,
    logoIndex: Int,
    isMusicEnabled: Boolean,
    musicVolume: Float,
    isSfxEnabled: Boolean,
    newPin: String
  ) {
    val current = settings.value ?: AppSettingsEntity()
    val updated = current.copy(
      storeName = storeName.ifBlank { "HARJAYA MEBEL" },
      slogan = slogan.ifBlank { "Arisan Mebel & Perabot Kayu Jati Mewah" },
      ownerName = ownerName.ifBlank { "by Cak Riyan" },
      phone = phone,
      address = address,
      themeIndex = themeIndex,
      logoIndex = logoIndex,
      isMusicEnabled = isMusicEnabled,
      musicVolume = musicVolume,
      isSfxEnabled = isSfxEnabled,
      adminPin = newPin.ifBlank { current.adminPin }
    )
    viewModelScope.launch {
      repository.updateSettings(updated)
    }

    soundEngine.setVolume(musicVolume)
    if (isMusicEnabled) {
      soundEngine.startBackgroundMusic(viewModelScope)
    } else {
      soundEngine.stopBackgroundMusic()
    }
  }

  // Lottery / Kocokan Arisan
  fun openLottery(kloterId: Long) {
    _selectedKloterId.value = kloterId
    _wonResult.value = null
    _rollingCandidate.value = null
    _isRolling.value = false
    _showLotteryDialog.value = true
  }

  fun closeLottery() {
    _showLotteryDialog.value = false
    _isRolling.value = false
  }

  fun startLotteryDraw(eligibleCandidates: List<KloterMemberWithDetail>, kloter: KloterEntity) {
    if (eligibleCandidates.isEmpty()) return
    _isRolling.value = true
    _wonResult.value = null

    viewModelScope.launch {
      val sfxOn = settings.value?.isSfxEnabled ?: true
      // Spin animation loop
      val totalTicks = 25
      var delayMs = 60L
      for (i in 0 until totalTicks) {
        val randomPick = eligibleCandidates.random()
        _rollingCandidate.value = randomPick
        if (sfxOn) {
          soundEngine.playLotteryTickSound(viewModelScope)
        }
        delay(delayMs)
        if (i > 15) {
          delayMs += 35L // Slow down near the end
        }
      }

      // Final winner pick
      val winner = eligibleCandidates.random()
      _rollingCandidate.value = winner
      _wonResult.value = winner
      _isRolling.value = false

      if (sfxOn) {
        soundEngine.playWinnerCelebration(viewModelScope)
      }

      // Update database: mark member as won and set delivery status to DIPROSES
      val currentTimestamp = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
      val receiptNum = "HM-NOTA-${System.currentTimeMillis() % 1000000}"

      val updatedMember = winner.member.copy(
        isWon = true,
        wonPeriod = kloter.currentPeriod,
        deliveryStatus = DeliveryStatus.DIPROSES.name,
        deliveryReceiptNo = receiptNum,
        deliveryDate = currentTimestamp,
        deliveryNotes = "Pemenang Periode ${kloter.currentPeriod}. Mebel disiapkan pengrajin untuk pengiriman."
      )
      repository.updateMember(updatedMember)

      // Advance kloter period if not exceeded
      if (kloter.currentPeriod < kloter.totalPeriods) {
        repository.updateKloter(kloter.copy(currentPeriod = kloter.currentPeriod + 1))
      } else {
        repository.updateKloter(kloter.copy(status = "Selesai"))
      }
    }
  }

  // Shipping Status Update
  fun updateShippingStatus(
    memberId: Long,
    newStatus: DeliveryStatus,
    driverName: String,
    deliveryDate: String,
    deliveryNotes: String
  ) {
    viewModelScope.launch {
      val currentMember = repository.allMembers.stateIn(viewModelScope).value.find { it.id == memberId } ?: return@launch
      val updated = currentMember.copy(
        deliveryStatus = newStatus.name,
        driverName = driverName,
        deliveryDate = deliveryDate,
        deliveryNotes = deliveryNotes
      )
      repository.updateMember(updated)
    }
  }

  // Customer Actions
  fun saveCustomer(id: Long, name: String, phone: String, address: String, notes: String) {
    viewModelScope.launch {
      val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
      if (id == 0L) {
        repository.insertCustomer(
          CustomerEntity(
            name = name,
            phone = phone,
            address = address,
            notes = notes,
            joinedDate = today
          )
        )
      } else {
        val existing = repository.allCustomers.stateIn(viewModelScope).value.find { it.id == id }
        if (existing != null) {
          repository.updateCustomer(
            existing.copy(
              name = name,
              phone = phone,
              address = address,
              notes = notes
            )
          )
        }
      }
    }
  }

  fun deleteCustomer(customer: CustomerEntity) {
    viewModelScope.launch {
      repository.deleteCustomer(customer)
    }
  }

  // Product Actions
  fun saveProduct(
    id: Long,
    name: String,
    category: String,
    price: Long,
    description: String,
    woodType: String
  ) {
    viewModelScope.launch {
      if (id == 0L) {
        repository.insertProduct(
          ProductEntity(
            name = name,
            category = category,
            price = price,
            description = description,
            woodType = woodType
          )
        )
      } else {
        val existing = repository.allProducts.stateIn(viewModelScope).value.find { it.id == id }
        if (existing != null) {
          repository.updateProduct(
            existing.copy(
              name = name,
              category = category,
              price = price,
              description = description,
              woodType = woodType
            )
          )
        }
      }
    }
  }

  fun deleteProduct(product: ProductEntity) {
    viewModelScope.launch {
      repository.deleteProduct(product)
    }
  }

  // Kloter Actions
  fun createKloter(
    name: String,
    installmentAmount: Long,
    frequency: String,
    totalPeriods: Int
  ) {
    viewModelScope.launch {
      val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
      repository.insertKloter(
        KloterEntity(
          name = name,
          installmentAmount = installmentAmount,
          frequency = frequency,
          totalPeriods = totalPeriods,
          currentPeriod = 1,
          status = "Aktif",
          createdAt = today
        )
      )
    }
  }

  fun addMemberToKloter(
    kloterId: Long,
    customerId: Long,
    productId: Long,
    lotteryNumber: Int
  ) {
    viewModelScope.launch {
      repository.insertMember(
        KloterMemberEntity(
          kloterId = kloterId,
          customerId = customerId,
          productId = productId,
          lotteryNumber = lotteryNumber,
          isWon = false,
          wonPeriod = 0,
          deliveryStatus = DeliveryStatus.MENUNGGU_UNDIAN.name,
          driverName = "Armada Harjaya Mebel (Cak Riyan)",
          deliveryReceiptNo = "",
          deliveryDate = "",
          deliveryNotes = "Menunggu jadwal undian kloter."
        )
      )
    }
  }

  fun deleteMember(member: KloterMemberEntity) {
    viewModelScope.launch {
      repository.deleteMember(member)
    }
  }

  fun deleteKloter(kloter: KloterEntity) {
    viewModelScope.launch {
      repository.deleteKloter(kloter)
    }
  }

  // Share Nota / Invoice
  fun shareNotaText(context: Context, detail: KloterMemberWithDetail) {
    val setting = settings.value ?: AppSettingsEntity()
    val nf = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val priceFormatted = nf.format(detail.product?.price ?: 0L).replace("Rp", "Rp ")

    val invoiceText = """
      ====================================
      🏛️ ${setting.storeName.uppercase()} 🏛️
      ${setting.slogan}
      ${setting.ownerName}
      Alamat: ${setting.address}
      WhatsApp: ${setting.phone}
      ====================================
      📄 NOTA RESMI ARISAN & PENGIRIMAN MEBEL
      Nomor Nota : ${detail.member.deliveryReceiptNo.ifBlank { "HM-" + detail.member.id }}
      Tanggal    : ${detail.member.deliveryDate.ifBlank { "Telah Terbit" }}
      
      👤 DATA PELANGGAN (PEMENANG)
      Nama       : ${detail.customer?.name ?: "Pelanggan"}
      No. HP     : ${detail.customer?.phone ?: "-"}
      Alamat     : ${detail.customer?.address ?: "-"}
      
      🛋️ RINCIAN BARANG / MEBEL
      Nama Mebel : ${detail.product?.name ?: "-"}
      Kategori   : ${detail.product?.category ?: "-"}
      Bahan Kayu : ${detail.product?.woodType ?: "Jati Jepara"}
      Nilai Mebel: $priceFormatted
      
      🎲 INFORMASI ARISAN
      Grup/Kloter: ${detail.kloter?.name ?: "-"}
      No. Undian : No. ${detail.member.lotteryNumber}
      Pemenang   : Periode ke-${detail.member.wonPeriod}
      
      🚚 STATUS PENGIRIMAN
      Status     : ${detail.member.deliveryStatus}
      Armada/Sopir: ${detail.member.driverName}
      Catatan    : ${detail.member.deliveryNotes}
      ====================================
      Terima kasih telah mempercayakan perabot mebel Anda kepada ${setting.storeName} (${setting.ownerName})!
    """.trimIndent()

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, invoiceText)
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Bagikan Nota Harjaya Mebel")
    context.startActivity(shareIntent)
  }
}
