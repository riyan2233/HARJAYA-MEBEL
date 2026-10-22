package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.AppBottomNav
import com.example.ui.components.AppHeader
import com.example.ui.screens.CustomerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KloterScreen
import com.example.ui.screens.NotaScreen
import com.example.ui.screens.ProductScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShippingScreen
import com.example.ui.theme.HarjayaMebelTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HarjayaViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: HarjayaViewModel = viewModel()
      val settings by viewModel.settings.collectAsState()

      HarjayaMebelTheme(themeIndex = settings?.themeIndex ?: 0) {
        HarjayaAppContent(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun HarjayaAppContent(viewModel: HarjayaViewModel) {
  val settings by viewModel.settings.collectAsState()
  val currentScreen by viewModel.currentScreen.collectAsState()
  val isAdmin by viewModel.isAdmin.collectAsState()
  val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsState()

  val customers by viewModel.customers.collectAsState()
  val products by viewModel.products.collectAsState()
  val kloters by viewModel.kloters.collectAsState()
  val shipments by viewModel.shipments.collectAsState()

  val selectedKloterId by viewModel.selectedKloterId.collectAsState()
  val selectedCustomerId by viewModel.selectedCustomerId.collectAsState()
  val selectedNotaMemberId by viewModel.selectedNotaMemberId.collectAsState()

  val isRolling by viewModel.isRolling.collectAsState()
  val rollingCandidate by viewModel.rollingCandidate.collectAsState()
  val wonResult by viewModel.wonResult.collectAsState()
  val showLotteryDialog by viewModel.showLotteryDialog.collectAsState()

  // Handle back button: if not on Home screen, navigate back to Home
  BackHandler(enabled = currentScreen != AppScreen.HOME) {
    viewModel.navigateTo(AppScreen.HOME)
  }

  Scaffold(
    topBar = {
      AppHeader(
        settings = settings,
        isAdmin = isAdmin,
        onAdminClick = {
          if (isAdmin) {
            viewModel.logoutAdmin()
          } else {
            viewModel.openAdminLoginDialog()
          }
        },
        onMusicToggle = { viewModel.toggleMusic() }
      )
    },
    bottomBar = {
      AppBottomNav(
        currentScreen = currentScreen,
        onScreenSelect = { viewModel.navigateTo(it) }
      )
    },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (currentScreen) {
        AppScreen.HOME -> HomeScreen(
          settings = settings,
          kloters = kloters,
          shipments = shipments,
          products = products,
          totalCustomers = customers.size,
          onNavigate = { viewModel.navigateTo(it) },
          onSelectKloter = {
            viewModel.selectKloter(it)
            viewModel.navigateTo(AppScreen.KLOTER)
          },
          onSelectNota = {
            viewModel.selectNotaMember(it)
          }
        )

        AppScreen.KLOTER -> KloterScreen(
          kloters = kloters,
          allMembersWithDetail = shipments,
          customers = customers,
          products = products,
          selectedKloterId = selectedKloterId,
          isAdmin = isAdmin,
          onSelectKloter = { viewModel.selectKloter(it) },
          onCreateKloter = { name, amount, freq, periods ->
            viewModel.createKloter(name, amount, freq, periods)
          },
          onAddMember = { kloterId, customerId, productId, lotteryNo ->
            viewModel.addMemberToKloter(kloterId, customerId, productId, lotteryNo)
          },
          onDeleteMember = { viewModel.deleteMember(it) },
          onDeleteKloter = { viewModel.deleteKloter(it) },
          onStartLottery = { eligible, kloter ->
            viewModel.openLottery(kloter.id)
            viewModel.startLotteryDraw(eligible, kloter)
          },
          onSelectNota = { viewModel.selectNotaMember(it) },
          isRolling = isRolling,
          rollingCandidate = rollingCandidate,
          wonResult = wonResult,
          showLotteryDialog = showLotteryDialog,
          onCloseLotteryDialog = { viewModel.closeLottery() }
        )

        AppScreen.SHIPPING -> ShippingScreen(
          shipments = shipments,
          isAdmin = isAdmin,
          onUpdateStatus = { memberId, status, driver, date, notes ->
            viewModel.updateShippingStatus(memberId, status, driver, date, notes)
          },
          onSelectNota = { viewModel.selectNotaMember(it) }
        )

        AppScreen.PRODUCTS -> ProductScreen(
          products = products,
          isAdmin = isAdmin,
          onSaveProduct = { id, name, cat, price, desc, wood ->
            viewModel.saveProduct(id, name, cat, price, desc, wood)
          },
          onDeleteProduct = { viewModel.deleteProduct(it) }
        )

        AppScreen.CUSTOMERS -> CustomerScreen(
          customers = customers,
          isAdmin = isAdmin,
          onSaveCustomer = { id, name, phone, address, notes ->
            viewModel.saveCustomer(id, name, phone, address, notes)
          },
          onDeleteCustomer = { viewModel.deleteCustomer(it) },
          onViewProfile = { customerId ->
            viewModel.selectCustomer(customerId)
            viewModel.navigateTo(AppScreen.PROFILE)
          }
        )

        AppScreen.PROFILE -> ProfileScreen(
          customers = customers,
          shipments = shipments,
          selectedCustomerId = selectedCustomerId,
          onSelectCustomer = { viewModel.selectCustomer(it) },
          onSelectNota = { viewModel.selectNotaMember(it) }
        )

        AppScreen.NOTA -> NotaScreen(
          settings = settings,
          shipments = shipments,
          selectedNotaMemberId = selectedNotaMemberId,
          onShareNota = { ctx, detail ->
            viewModel.shareNotaText(ctx, detail)
          }
        )

        AppScreen.SETTINGS -> SettingsScreen(
          settings = settings,
          isAdmin = isAdmin,
          onLoginClick = { viewModel.openAdminLoginDialog() },
          onLogoutClick = { viewModel.logoutAdmin() },
          onSaveSettings = { name, slogan, owner, phone, addr, theme, logo, isMusic, vol, isSfx, pin ->
            viewModel.updateSettings(name, slogan, owner, phone, addr, theme, logo, isMusic, vol, isSfx, pin)
          }
        )
      }
    }
  }

  // Admin Login Modal Dialog
  if (showAdminLoginDialog) {
    AdminLoginDialog(
      onDismiss = { viewModel.closeAdminLoginDialog() },
      onLogin = { pin -> viewModel.loginAdmin(pin) }
    )
  }
}
