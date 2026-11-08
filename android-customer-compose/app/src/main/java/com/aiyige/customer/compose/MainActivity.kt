package com.aiyige.customer.compose

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.content.Context
import android.util.Base64
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Popup
import androidx.core.view.WindowCompat
import androidx.core.content.ContextCompat
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebSettings
import com.aiyige.customer.compose.BuildConfig
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import com.aiyige.customer.compose.ui.theme.AiyigeCustomerTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.github.sceneview.Scene
import io.github.sceneview.SurfaceType
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.material.setBaseColorFactor
import io.github.sceneview.material.setBaseColorMap
import io.github.sceneview.material.setMetallicFactor
import io.github.sceneview.material.setRoughnessFactor
import io.github.sceneview.math.colorOf
import io.github.sceneview.model.renderableInstances
import io.github.sceneview.model.renderableManager
import com.google.ar.core.Config
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import cn.easyar.Engine as EasyArEngine
import cn.easyar.MotionTrackerCameraDevice as EasyArMotionTrackerCameraDevice
import com.aiyige.customer.compose.easyar.EasyArPanelPreview
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.math.Scale
import io.github.sceneview.texture.ImageTexture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Date
import java.util.Calendar
import java.util.Locale

private data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val size: String,
    val material: String,
    val price: Int,
    val quantity: Int,
    val description: String,
    val panelType: String = "",
    val imageUrl: String? = null,
    val modelUrl: String? = null
)

private data class CartLine(
    val product: Product,
    val quantity: Int,
    val selected: Boolean = true
)

private data class SavedCustomization(
    val productId: Int,
    val productName: String,
    val size: String,
    val colour: String,
    val finish: String,
    val quantity: String,
    val total: Int
)

private fun customizationAddOnValues(prices: Map<String, Int>, csv: String): Int =
    csv.split("|").filter { it.isNotBlank() }.sumOf { prices[it] ?: 0 }

internal data class ArDropRequest(val rootPoint: Offset, val token: Long)

private data class CameraPlacedPanel(
    val id: Int,
    val center: Offset,
    val scale: Float
)

private data class PlacedArPanel(
    val anchor: com.google.ar.core.Anchor,
    val assetPath: String,
    val tintHex: String,
    val finish: String,
    val textureAsset: String,
    val sizeScale: Float
)

private fun customizationSizeScale(size: String): Float = when {
    size.contains("150", ignoreCase = true) -> 1.12f
    size.contains("300", ignoreCase = true) -> 0.88f
    else -> 1.0f
}

private fun panelDimensionsMm(size: String): Pair<Float, Float>? {
    val dimensions = Regex("\\d+(?:[.,]\\d+)?")
        .findAll(size)
        .mapNotNull { it.value.replace(',', '.').toFloatOrNull() }
        .take(2)
        .toList()
    if (dimensions.size != 2 || dimensions.any { it <= 0f }) return null
    return maxOf(dimensions[0], dimensions[1]) to minOf(dimensions[0], dimensions[1])
}

private data class InventoryUiState(
    val products: List<Product>,
    val isLoading: Boolean,
    val sourceLabel: String,
    val errorMessage: String? = null
)

private data class RegisteredUser(
    val username: String,
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val dateOfBirth: String,
    val gender: String,
    val email: String,
    val mobileNumber: String,
    val streetAddress: String = "",
    val cityProvince: String = "",
    val postalCode: String = "",
    val password: String = "",
    val createdAt: String = ""
) {
    val displayName: String
        get() = listOf(firstName.trim(), middleName.trim(), lastName.trim()).filter { it.isNotBlank() }.joinToString(" ")
}

private data class QuoteHistoryEntry(
    val quoteNumber: String,
    val customerEmail: String,
    val summary: String,
    val amount: String,
    val status: String,
    val createdAt: String
)

private data class CustomerOrderUpdate(
    val invoice: String,
    val products: String,
    val amount: Int,
    val paymentStatus: String,
    val deliveryStatus: String,
    val courier: String,
    val trackingNumber: String,
    val updatedAt: String
)

private data class CustomerNotification(
    val reference: String,
    val title: String,
    val message: String,
    val itemSummary: String,
    val amount: String,
    val status: String,
    val updatedAt: String,
    val quote: QuoteHistoryEntry? = null,
    val isOrder: Boolean = false
)

private const val CUSTOMER_AUTH_PREFS = "aiyige_customer_auth"
private const val CUSTOMER_AUTH_USERS_KEY = "registered_users"

private fun registeredUserToJson(user: RegisteredUser): JSONObject {
    return JSONObject().apply {
        put("username", user.username)
        put("firstName", user.firstName)
        put("lastName", user.lastName)
        put("middleName", user.middleName)
        put("dateOfBirth", user.dateOfBirth)
        put("gender", user.gender)
        put("email", user.email)
        put("mobileNumber", user.mobileNumber)
        put("streetAddress", user.streetAddress)
        put("cityProvince", user.cityProvince)
        put("postalCode", user.postalCode)
        put("password", user.password)
        put("createdAt", user.createdAt)
    }
}

private fun registeredUserFromJson(value: JSONObject): RegisteredUser? {
    val email = value.optString("email", "").trim()
    if (email.isBlank()) return null
    val fallbackUsername = value.optString("username", "").trim().ifBlank { email.substringBefore("@").trim() }
    val rawName = value.optString("name", "").trim()
    val nameParts = rawName.split(Regex("\\s+")).filter { it.isNotBlank() }
    val fallbackFirstName = nameParts.firstOrNull().orEmpty()
    val fallbackMiddleName = if (nameParts.size > 2) nameParts.subList(1, nameParts.lastIndex).joinToString(" ") else ""
    val fallbackLastName = if (nameParts.size > 1) nameParts.lastOrNull().orEmpty() else ""

    return RegisteredUser(
        username = fallbackUsername,
        firstName = value.optString("firstName", "").ifBlank { value.optString("first_name", fallbackFirstName) },
        lastName = value.optString("lastName", "").ifBlank { value.optString("last_name", fallbackLastName) },
        middleName = value.optString("middleName", "").ifBlank { value.optString("middle_name", fallbackMiddleName) },
        dateOfBirth = value.optString("dateOfBirth", "").ifBlank { value.optString("date_of_birth", "") },
        gender = value.optString("gender", ""),
        email = email,
        mobileNumber = value.optString("mobileNumber", "")
            .ifBlank { value.optString("mobile_number", "") }
            .ifBlank { value.optString("contact", "") },
        streetAddress = value.optString("streetAddress", "")
            .ifBlank { value.optString("addressLine1", "") },
        cityProvince = value.optString("cityProvince", "")
            .ifBlank { value.optString("addressLine2", "") },
        postalCode = value.optString("postalCode", "")
            .ifBlank { value.optString("postal_code", "") },
        password = value.optString("password", ""),
        createdAt = value.optString("createdAt", "")
    )
}

private fun formatMemberSince(raw: String): String {
    val trimmed = raw.trim()
    if (trimmed.isBlank()) return "Existing account"
    return trimmed.substringBefore(" ")
}

private fun loadRegisteredUsersFromPrefs(context: Context): List<RegisteredUser> {
    val raw = context.getSharedPreferences(CUSTOMER_AUTH_PREFS, Context.MODE_PRIVATE)
        .getString(CUSTOMER_AUTH_USERS_KEY, "[]")
        .orEmpty()

    return runCatching {
        val array = JSONArray(raw)
        buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val user = registeredUserFromJson(item) ?: continue
                add(user)
            }
        }
    }.getOrElse { emptyList() }
}

private fun saveRegisteredUsersToPrefs(context: Context, users: List<RegisteredUser>) {
    val array = JSONArray()
    users.forEach { user ->
        array.put(registeredUserToJson(user))
    }
    context.getSharedPreferences(CUSTOMER_AUTH_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(CUSTOMER_AUTH_USERS_KEY, array.toString())
        .apply()
}

private fun mergeRegisteredUsers(existing: RegisteredUser, incoming: RegisteredUser): RegisteredUser {
    return existing.copy(
        username = incoming.username.ifBlank { existing.username },
        firstName = incoming.firstName.ifBlank { existing.firstName },
        lastName = incoming.lastName.ifBlank { existing.lastName },
        middleName = incoming.middleName.ifBlank { existing.middleName },
        dateOfBirth = incoming.dateOfBirth.ifBlank { existing.dateOfBirth },
        gender = incoming.gender.ifBlank { existing.gender },
        email = incoming.email.ifBlank { existing.email },
        mobileNumber = incoming.mobileNumber.ifBlank { existing.mobileNumber },
        streetAddress = incoming.streetAddress.ifBlank { existing.streetAddress },
        cityProvince = incoming.cityProvince.ifBlank { existing.cityProvince },
        postalCode = incoming.postalCode.ifBlank { existing.postalCode },
        password = incoming.password.ifBlank { existing.password },
        createdAt = existing.createdAt.ifBlank { incoming.createdAt }
    )
}

private fun displayNameFromEmail(email: String): String {
    val prefix = email.substringBefore("@").trim()
    if (prefix.isBlank()) return "Customer"
    return prefix.replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString()
    }
}

private fun compactDisplayName(value: String): String {
    val parts = value.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    if (parts.isEmpty()) return ""
    if (parts.size == 1) return parts.first()
    val first = parts.first()
    val middle = parts.getOrNull(1)?.take(1).orEmpty()
    return if (middle.isNotBlank()) "$first $middle." else first
}

private fun sanitizeUsernamePart(value: String): String {
    return value.trim()
        .lowercase(Locale.getDefault())
        .replace(Regex("[^a-z0-9]+"), ".")
        .trim('.')
}

private fun buildRegistrationUsername(
    firstName: String,
    lastName: String,
    email: String,
    registeredUsers: List<RegisteredUser>
): String {
    val first = sanitizeUsernamePart(firstName)
    val last = sanitizeUsernamePart(lastName)
    val emailBase = sanitizeUsernamePart(email.substringBefore("@"))
    val candidates = buildList {
        if (first.isNotBlank() && last.isNotBlank()) add("$first.$last")
        if (emailBase.isNotBlank()) add(emailBase)
        if (first.isNotBlank() && last.isNotBlank() && emailBase.isNotBlank()) add("$first.$last.$emailBase")
        add("customer")
    }
    val existingUsernames = registeredUsers.map { it.username.lowercase(Locale.getDefault()) }.toSet()
    return candidates.firstOrNull { it.isNotBlank() && it !in existingUsernames } ?: "customer${registeredUsers.size + 1}"
}

private fun lettersOnly(value: String, allowSpaces: Boolean = true): Boolean {
    if (value.isBlank()) return false
    return value.all { it.isLetter() || (allowSpaces && it.isWhitespace()) }
}

private fun optionalLettersOnly(value: String): Boolean {
    if (value.isBlank()) return true
    return value.all { it.isLetter() || it.isWhitespace() }
}

private fun isValidUsername(value: String): Boolean {
    val trimmed = value.trim()
    return trimmed.length in 6..20 && trimmed.all { it.isLetterOrDigit() }
}

private fun isValidEmailAddress(value: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()
}

private fun isValidPhilippineMobile(value: String): Boolean {
    return Regex("^09\\d{9}$").matches(value.trim())
}

private fun isStrongPassword(value: String): Boolean {
    val trimmed = value
    return trimmed.length >= 8 &&
        trimmed.any(Char::isUpperCase) &&
        trimmed.any(Char::isLowerCase) &&
        trimmed.any(Char::isDigit) &&
        trimmed.any { !it.isLetterOrDigit() }
}

private fun isAdultDateOfBirth(value: String): Boolean {
    return isAtLeast18YearsOld(value)
}

private fun findRegisteredUserByLogin(registeredUsers: List<RegisteredUser>, value: String): RegisteredUser? {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return null
    return registeredUsers.firstOrNull {
        it.email.equals(trimmed, ignoreCase = true) || it.username.equals(trimmed, ignoreCase = true)
    }
}

private data class RegistrationValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String>
)

private fun validateRegistrationForm(
    firstName: String,
    lastName: String,
    middleName: String,
    dateOfBirth: String,
    gender: String,
    email: String,
    mobileNumber: String,
    streetAddress: String,
    cityProvince: String,
    postalCode: String,
    password: String,
    confirmPassword: String,
    termsAccepted: Boolean,
    privacyAccepted: Boolean,
    registeredUsers: List<RegisteredUser>
): RegistrationValidationResult {
    val errors = linkedMapOf<String, String>()
    val normalizedEmail = email.trim()
    val normalizedMobile = mobileNumber.trim()

    if (!lettersOnly(firstName.trim(), allowSpaces = false) || firstName.trim().length > 50) {
        errors["firstName"] = if (firstName.isBlank()) "First name is required." else "Use letters only, max 50 characters."
    }

    if (!lettersOnly(lastName.trim(), allowSpaces = false) || lastName.trim().length > 50) {
        errors["lastName"] = if (lastName.isBlank()) "Last name is required." else "Use letters only, max 50 characters."
    }

    if (!optionalLettersOnly(middleName.trim())) {
        errors["middleName"] = "Middle name should contain letters only."
    }

    if (dateOfBirth.isBlank()) {
        errors["dateOfBirth"] = "Date of birth is required."
    } else if (!isAdultDateOfBirth(dateOfBirth)) {
        errors["dateOfBirth"] = "You must be 18 years old or above."
    }

    if (gender !in setOf("Male", "Female", "Prefer not to say")) {
        errors["gender"] = "Please select a gender."
    }

    if (normalizedEmail.isBlank()) {
        errors["email"] = "Email address is required."
    } else if (!isValidEmailAddress(normalizedEmail)) {
        errors["email"] = "Enter a valid email address."
    }

    if (normalizedMobile.isBlank()) {
        errors["mobileNumber"] = "Mobile number is required."
    } else if (!isValidPhilippineMobile(normalizedMobile)) {
        errors["mobileNumber"] = "Use a valid PH number like 09XXXXXXXXX."
    }

    if (postalCode.trim().isNotBlank() && !postalCode.trim().matches(Regex("\\d{4}"))) {
        errors["postalCode"] = "Enter a valid 4-digit postal code."
    }

    if (password.isBlank()) {
        errors["password"] = "Password is required."
    } else if (!isStrongPassword(password)) {
        errors["password"] = "Use 8+ chars with uppercase, lowercase, number, and symbol."
    }

    if (confirmPassword.isBlank()) {
        errors["confirmPassword"] = "Confirm your password."
    } else if (password != confirmPassword) {
        errors["confirmPassword"] = "Passwords do not match."
    }

    if (!termsAccepted) {
        errors["terms"] = "Accept the Terms and Conditions."
    }

    if (!privacyAccepted) {
        errors["privacy"] = "Accept the Privacy Policy."
    }

    return RegistrationValidationResult(
        isValid = errors.isEmpty(),
        errors = errors
    )
}

private fun parsePesoAmount(value: String): Int {
    return value.filter(Char::isDigit).toIntOrNull() ?: 0
}

private fun Uri.collectAuthParams(): Map<String, String> {
    val params = mutableMapOf<String, String>()
    queryParameterNames.forEach { key ->
        getQueryParameter(key)?.takeIf { it.isNotBlank() }?.let { params[key] = it }
    }
    fragment
        ?.split("&")
        ?.mapNotNull { part ->
            val pieces = part.split("=", limit = 2)
            if (pieces.size == 2 && pieces[0].isNotBlank() && pieces[1].isNotBlank()) {
                runCatching {
                    val key = java.net.URLDecoder.decode(pieces[0], Charsets.UTF_8.name())
                    val value = java.net.URLDecoder.decode(pieces[1], Charsets.UTF_8.name())
                    key to value
                }.getOrNull()
            } else {
                null
            }
        }
        ?.forEach { (key, value) ->
            if (value.isNotBlank()) params[key] = value
        }
    return params
}

private data class SupabaseSessionCache(
    val accessToken: String,
    val refreshToken: String,
    val email: String,
    val displayName: String
)

private data class PaymentRecordPayload(
    val quoteNumber: String,
    val customerName: String,
    val customerEmail: String,
    val contactNumber: String,
    val streetAddress: String,
    val cityProvince: String,
    val postalCode: String,
    val paymentMethod: String,
    val deliveryMethod: String,
    val itemName: String,
    val panels: Int,
    val unitPrice: Int,
    val subtotal: Int,
    val vat: Int,
    val totalAmount: Int,
    val summary: String,
    val roomCaptureUrl: String?,
    val notes: String,
    val status: String
)

private data class PaymentSaveOutcome(
    val savedRemotely: Boolean,
    val message: String
)

private fun paymentRequestOutcomeMessage(status: String, savedRemotely: Boolean): String {
    return when (status.trim().lowercase(Locale.US)) {
        "draft" -> if (savedRemotely) {
            "Quote draft saved to the team."
        } else {
            "Quote draft saved to the admin dashboard."
        }
        else -> if (savedRemotely) {
            "Payment request submitted for review."
        } else {
            "Payment request submitted to Sales Staff for review."
        }
    }
}

private fun paymentCloudFallbackMessage(): String {
    return "Cloud payment storage is not configured yet. Your request was saved locally on this device."
}

private fun buildPaymentRequestRecord(payload: PaymentRecordPayload): JSONObject {
    val timestamp = formatCaptureTimestamp()
    return JSONObject().apply {
        put("quote_number", payload.quoteNumber)
        put("customer_name", payload.customerName)
        put("customer_email", payload.customerEmail)
        put("customer_phone", payload.contactNumber)
        put("street_address", payload.streetAddress)
        put("city_province", payload.cityProvince)
        put("postal_code", payload.postalCode)
        put("payment_method", payload.paymentMethod)
        put("delivery_method", payload.deliveryMethod)
        put("item_name", payload.itemName)
        put("panels", payload.panels)
        put("unit_price", payload.unitPrice)
        put("subtotal", payload.subtotal)
        put("vat", payload.vat)
        put("total_amount", payload.totalAmount)
        put("summary", payload.summary)
        put("room_capture_url", payload.roomCaptureUrl ?: JSONObject.NULL)
        put("notes", payload.notes)
        put("status", payload.status)
        put("source", "Customer App")
        put("created_at", timestamp)
        put("updated_at", timestamp)
    }
}

private fun buildPaymentRequestOrderRecord(payload: PaymentRecordPayload): JSONObject {
    val timestamp = formatCaptureTimestamp()
    return JSONObject().apply {
        put("invoice", payload.quoteNumber)
        put("quoteNumber", payload.quoteNumber)
        put("customer", payload.customerName.ifBlank { "Customer" })
        put("customerEmail", payload.customerEmail)
        put("customerPhone", payload.contactNumber)
        put("streetAddress", payload.streetAddress)
        put("cityProvince", payload.cityProvince)
        put("postalCode", payload.postalCode)
        put("products", payload.summary.ifBlank { payload.itemName.ifBlank { "Payment Request" } })
        put("amount", payload.totalAmount)
        put("payment", payload.paymentMethod)
        put("deliveryMethod", payload.deliveryMethod)
        put("courier", "")
        put("trackingNumber", "")
        put("status", payload.status)
        put("method", payload.paymentMethod)
        put("createdAt", timestamp)
        put("updatedAt", timestamp)
    }
}

private fun isMissingPaymentRequestsTable(error: Throwable): Boolean {
    val message = buildString {
        append(error.message.orEmpty())
        error.cause?.message?.let {
            if (isNotBlank()) append(' ')
            append(it)
        }
    }.lowercase(Locale.US)

    val tableMissingHint = message.contains("payment_requests") ||
        message.contains("http 404") ||
        message.contains("schema cache") ||
        message.contains("42p01")

    return tableMissingHint && (
        message.contains("could not find the table") ||
            message.contains("does not exist") ||
            message.contains("schema cache") ||
            message.contains("42p01") ||
            message.contains("404")
        )
}

private fun isSupabaseAuthExpired(error: Throwable): Boolean {
    val message = buildString {
        append(error.message.orEmpty())
        error.cause?.message?.let {
            if (isNotBlank()) append(' ')
            append(it)
        }
    }.lowercase(Locale.US)

    return message.contains("jwt expired") ||
        message.contains("token expired") ||
        message.contains("expired jwt") ||
        message.contains("invalid jwt") ||
        message.contains("unauthorized") ||
        message.contains("401")
}

private const val SUPABASE_SESSION_PREFS = "aiyige_supabase_session"
private const val SUPABASE_ACCESS_TOKEN_KEY = "access_token"
private const val SUPABASE_REFRESH_TOKEN_KEY = "refresh_token"
private const val SUPABASE_EMAIL_KEY = "email"
private const val SUPABASE_DISPLAY_NAME_KEY = "display_name"
private const val LOCAL_SESSION_PREFS = "aiyige_local_session"
private const val LOCAL_SESSION_EMAIL_KEY = "email"
private const val LOCAL_SESSION_DISPLAY_NAME_KEY = "display_name"
private const val PASSWORD_RESET_REDIRECT_URI = "aiyigecustomer://auth-callback"

private fun saveSupabaseSession(context: Context, session: SupabaseSessionCache) {
    context.getSharedPreferences(SUPABASE_SESSION_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(SUPABASE_ACCESS_TOKEN_KEY, session.accessToken)
        .putString(SUPABASE_REFRESH_TOKEN_KEY, session.refreshToken)
        .putString(SUPABASE_EMAIL_KEY, session.email)
        .putString(SUPABASE_DISPLAY_NAME_KEY, session.displayName)
        .apply()
}

private fun loadSupabaseSession(context: Context): SupabaseSessionCache? {
    val prefs = context.getSharedPreferences(SUPABASE_SESSION_PREFS, Context.MODE_PRIVATE)
    val accessToken = prefs.getString(SUPABASE_ACCESS_TOKEN_KEY, "").orEmpty()
    val refreshToken = prefs.getString(SUPABASE_REFRESH_TOKEN_KEY, "").orEmpty()
    val email = prefs.getString(SUPABASE_EMAIL_KEY, "").orEmpty()
    val displayName = prefs.getString(SUPABASE_DISPLAY_NAME_KEY, "").orEmpty()
    if (accessToken.isBlank() || refreshToken.isBlank() || email.isBlank()) return null
    return SupabaseSessionCache(accessToken, refreshToken, email, displayName.ifBlank { displayNameFromEmail(email) })
}

private fun clearSupabaseSession(context: Context) {
    context.getSharedPreferences(SUPABASE_SESSION_PREFS, Context.MODE_PRIVATE)
        .edit()
        .clear()
        .apply()
}

private fun saveLocalSession(context: Context, email: String, displayName: String) {
    context.getSharedPreferences(LOCAL_SESSION_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(LOCAL_SESSION_EMAIL_KEY, email)
        .putString(LOCAL_SESSION_DISPLAY_NAME_KEY, displayName)
        .apply()
}

private fun loadLocalSession(context: Context): Pair<String, String>? {
    val prefs = context.getSharedPreferences(LOCAL_SESSION_PREFS, Context.MODE_PRIVATE)
    val email = prefs.getString(LOCAL_SESSION_EMAIL_KEY, "").orEmpty()
    val displayName = prefs.getString(LOCAL_SESSION_DISPLAY_NAME_KEY, "").orEmpty()
    if (email.isBlank()) return null
    return email to displayName.ifBlank { displayNameFromEmail(email) }
}

private fun clearLocalSession(context: Context) {
    context.getSharedPreferences(LOCAL_SESSION_PREFS, Context.MODE_PRIVATE)
        .edit()
        .clear()
        .apply()
}

private fun isSupabaseNetworkError(error: Throwable): Boolean {
    fun Throwable.allMessages(): String = buildString {
        append(message.orEmpty())
        cause?.message?.let {
            if (isNotBlank()) append(' ')
            append(it)
        }
    }.lowercase(Locale.US)

    val message = error.allMessages()
    return error is java.net.UnknownHostException ||
        error is java.net.SocketTimeoutException ||
        error is java.net.ConnectException ||
        message.contains("unable to resolve host") ||
        message.contains("no address associated with hostname") ||
        message.contains("network is unreachable") ||
        message.contains("failed to connect")
}

private fun supabaseUrl(path: String): String {
    val base = BuildConfig.SUPABASE_URL.trimEnd('/')
    val cleanPath = path.trimStart('/')
    return "$base/$cleanPath"
}

private suspend fun supabaseRequest(
    path: String,
    method: String,
    body: JSONObject? = null,
    accessToken: String? = null,
    additionalHeaders: Map<String, String> = emptyMap()
): JSONObject = withContext(Dispatchers.IO) {
    val connection = (URL(supabaseUrl(path)).openConnection() as HttpURLConnection).apply {
        requestMethod = method
        connectTimeout = 15_000
        readTimeout = 15_000
        doInput = true
        setRequestProperty("apikey", BuildConfig.SUPABASE_PUBLISHABLE_KEY)
        setRequestProperty("Authorization", "Bearer ${accessToken ?: BuildConfig.SUPABASE_PUBLISHABLE_KEY}")
        setRequestProperty("Content-Type", "application/json")
        additionalHeaders.forEach { (key, value) ->
            setRequestProperty(key, value)
        }
        if (body != null) {
            doOutput = true
            outputStream.use { output ->
                output.write(body.toString().toByteArray(Charsets.UTF_8))
            }
        }
    }

    try {
        val responseCode = connection.responseCode
        val responseText = runCatching {
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        }.getOrElse { "" }

        if (responseCode !in 200..299) {
            val errorMessage = runCatching {
                if (responseText.isNotBlank()) {
                    JSONObject(responseText).optString("msg").ifBlank {
                        JSONObject(responseText).optString("error_description").ifBlank {
                            JSONObject(responseText).optString("message")
                        }
                    }
                } else {
                    ""
                }
            }.getOrDefault("")
            throw IllegalStateException(errorMessage.ifBlank { "Supabase request failed with HTTP $responseCode" })
        }

        if (responseText.isBlank()) JSONObject() else JSONObject(responseText)
    } finally {
        connection.disconnect()
    }
}

private fun normalizeAuthVerificationType(value: String): String {
    return when (value.trim().lowercase(Locale.US)) {
        "signup", "magiclink" -> "email"
        else -> value.trim().lowercase(Locale.US)
    }
}

private fun extractSupabaseSession(
    response: JSONObject,
    fallbackEmail: String,
    fallbackDisplayName: String
): SupabaseSessionCache? {
    val sessionObject = response.optJSONObject("session") ?: response
    val accessToken = sessionObject.optString("access_token", response.optString("access_token", "")).trim()
    val refreshToken = sessionObject.optString("refresh_token", response.optString("refresh_token", "")).trim()
    if (accessToken.isBlank() || refreshToken.isBlank()) return null

    val userObject = sessionObject.optJSONObject("user") ?: response.optJSONObject("user")
    val email = userObject?.optString("email", "").orEmpty().ifBlank {
        sessionObject.optString("email", response.optString("email", fallbackEmail)).trim()
    }
    if (email.isBlank()) return null

    val displayName = userObject?.let { extractDisplayNameFromUserJson(it, email) }
        ?.trim()
        .orEmpty()
        .ifBlank { fallbackDisplayName.trim() }
        .ifBlank { displayNameFromEmail(email) }

    return SupabaseSessionCache(
        accessToken = accessToken,
        refreshToken = refreshToken,
        email = email,
        displayName = displayName
    )
}

/**
 * Supabase sends the session in the deep-link fragment after a confirmation or
 * recovery link is opened.  Persist that session before showing the recovery
 * form; otherwise the subsequent password update has no authorization token.
 */
private suspend fun extractSupabaseSessionFromRedirect(
    uri: Uri,
    fallbackEmail: String = "",
    fallbackDisplayName: String = ""
): SupabaseSessionCache? {
    val params = uri.collectAuthParams()
    val accessToken = params["access_token"].orEmpty().trim()
    val refreshToken = params["refresh_token"].orEmpty().trim()
    if (accessToken.isBlank() || refreshToken.isBlank()) return null

    // The redirect normally does not include an email address. Ask Auth for the
    // user so the cached session remains usable after the app is restarted.
    val user = runCatching {
        supabaseRequest(
            path = "auth/v1/user",
            method = "GET",
            accessToken = accessToken
        )
    }.getOrNull()
    val email = user?.optString("email", "").orEmpty().trim().ifBlank { fallbackEmail.trim() }
    if (email.isBlank()) return null

    val displayName = user?.let { extractDisplayNameFromUserJson(it, email) }
        .orEmpty()
        .ifBlank { fallbackDisplayName.trim() }
        .ifBlank { displayNameFromEmail(email) }
    return SupabaseSessionCache(accessToken, refreshToken, email, displayName)
}

private suspend fun requestSignupConfirmationEmail(
    user: RegisteredUser
): Result<SupabaseSessionCache?> = runCatching {
    withTimeout(20_000) {
    runCatching {
        supabaseRequest(
            path = "auth/v1/signup?redirect_to=${URLEncoder.encode(PASSWORD_RESET_REDIRECT_URI, Charsets.UTF_8.name())}",
            method = "POST",
            body = JSONObject().apply {
                put("email", user.email)
                put("password", user.password)
                put("data", JSONObject().apply {
                    put("username", user.username)
                    put("first_name", user.firstName)
                    put("middle_name", user.middleName)
                    put("last_name", user.lastName)
                    put("date_of_birth", user.dateOfBirth)
                    put("gender", user.gender)
                    put("mobile_number", user.mobileNumber)
                    put("display_name", user.displayName)
                })
            }
        )
    }.getOrThrow().let { response -> extractSupabaseSession(response, user.email, user.displayName) }
    }
}

private suspend fun requestPasswordRecoveryEmail(email: String): Result<Unit> = runCatching {
    withTimeout(20_000) {
        supabaseRequest(
            path = "auth/v1/recover?redirect_to=${URLEncoder.encode(PASSWORD_RESET_REDIRECT_URI, Charsets.UTF_8.name())}",
            method = "POST",
            body = JSONObject().apply {
                put("email", email)
            }
        )
        Unit
    }
}

private suspend fun verifyEmailOtp(
    email: String,
    token: String,
    type: String,
    fallbackDisplayName: String = ""
): Result<SupabaseSessionCache> = runCatching {
    withTimeout(20_000) {
        val response = supabaseRequest(
            path = "auth/v1/verify",
            method = "POST",
            body = JSONObject().apply {
                put("email", email)
                put("token", token)
                put("type", type)
            }
        )
        extractSupabaseSession(response, email, fallbackDisplayName)
            ?: throw IllegalStateException("The verification code is invalid or has expired. Request a new code.")
    }
}

private suspend fun signInWithPassword(
    email: String,
    password: String
): Result<SupabaseSessionCache> = runCatching {
    withTimeout(20_000) {
        val response = supabaseRequest(
            path = "auth/v1/token?grant_type=password",
            method = "POST",
            body = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }
        )
        extractSupabaseSession(response, email, "")
            ?: throw IllegalStateException("Unable to start a secure session. Please try again.")
    }
}

private suspend fun savePaymentRequestToSupabase(
    context: Context,
    payload: PaymentRecordPayload
): Result<PaymentSaveOutcome> = withContext(Dispatchers.IO) {
    runCatching {
    // The local backend is the source used by the Admin/Sales Staff dashboard.
    // Save there first and do not turn a successful admin save into a failed
    // customer payment just because the optional Supabase mirror is missing a
    // column, table, or valid session.
    val localSaveResult = savePaymentRequestToLocalBackend(context, payload)
    var savedRemotely = false
    var remoteError: Throwable? = null
    val remoteSession = loadSupabaseSession(context)
    if (remoteSession != null) {
        runCatching {
            supabaseRequest(
                path = "rest/v1/payment_requests?on_conflict=quote_number",
                method = "POST",
                body = buildPaymentRequestRecord(payload),
                accessToken = remoteSession.accessToken,
                additionalHeaders = mapOf(
                    "Prefer" to "resolution=merge-duplicates,return=representation"
                )
            )
        }.onSuccess {
            savedRemotely = true
        }.onFailure { error ->
            remoteError = error
            if (isSupabaseAuthExpired(error) || isSupabaseNetworkError(error)) {
                clearSupabaseSession(context)
            }
        }
    }

    if (localSaveResult.isFailure && !savedRemotely) {
        throw localSaveResult.exceptionOrNull()
            ?: remoteError
            ?: IllegalStateException("Unable to save payment request.")
    }

    PaymentSaveOutcome(
        savedRemotely = savedRemotely,
        message = if (savedRemotely) {
            "Payment request submitted for review and synced to admin."
        } else {
            paymentRequestOutcomeMessage(payload.status, savedRemotely = false)
        }
    )
    }
}

private suspend fun savePaymentRequestToLocalBackend(
    context: Context,
    payload: PaymentRecordPayload
): Result<Unit> = withContext(Dispatchers.IO) {
    runCatching {
        val state = fetchBackendState(context) ?: throw IllegalStateException("Local save is unavailable.")
        val paymentRequests = state.optJSONArray("paymentRequests") ?: JSONArray()
        val orders = state.optJSONArray("orders") ?: JSONArray()
        val record = buildPaymentRequestRecord(payload)
        val orderRecord = buildPaymentRequestOrderRecord(payload)
        val existingIndex = (0 until paymentRequests.length()).firstOrNull { index ->
            paymentRequests.optJSONObject(index)
                ?.optString("quote_number", "")
                .equals(payload.quoteNumber, ignoreCase = true)
        }
        val existingOrderIndex = (0 until orders.length()).firstOrNull { index ->
            orders.optJSONObject(index)
                ?.optString("invoice", "")
                .equals(payload.quoteNumber, ignoreCase = true) ||
                orders.optJSONObject(index)
                    ?.optString("quoteNumber", "")
                    .equals(payload.quoteNumber, ignoreCase = true)
        }

        if (existingIndex != null) {
            paymentRequests.put(existingIndex, record)
        } else {
            paymentRequests.put(record)
        }

        if (existingOrderIndex != null) {
            orders.put(existingOrderIndex, orderRecord)
        } else {
            orders.put(0, orderRecord)
        }

        state.put("paymentRequests", paymentRequests)
        state.put("orders", orders)
        saveBackendState(context, state) ?: throw IllegalStateException("Unable to save payment request locally.")
    }.map { Unit }
}

private fun extractDisplayNameFromUserJson(userJson: JSONObject, fallbackEmail: String): String {
    val metadata = userJson.optJSONObject("user_metadata")
    val candidates = listOfNotNull(
        metadata?.optString("first_name", "")?.takeIf { it.isNotBlank() },
        metadata?.optString("display_name", "")?.takeIf { it.isNotBlank() },
        metadata?.optString("full_name", "")?.takeIf { it.isNotBlank() }
    )
    if (candidates.isNotEmpty()) return candidates.joinToString(" ").trim()

    val firstName = metadata?.optString("first_name", "").orEmpty()
    val middleName = metadata?.optString("middle_name", "").orEmpty()
    val lastName = metadata?.optString("last_name", "").orEmpty()
    val joined = listOf(firstName, middleName, lastName).filter { it.isNotBlank() }.joinToString(" ").trim()
    if (joined.isNotBlank()) return joined

    return displayNameFromEmail(fallbackEmail)
}

private fun formatDateOfBirthInput(value: String): String {
    val digits = value.filter(Char::isDigit).take(8)
    return buildString(digits.length + 2) {
        digits.forEachIndexed { index, char ->
            if (index == 4 || index == 6) append('-')
            append(char)
        }
    }
}

private fun formatDateOfBirthInput(value: TextFieldValue): TextFieldValue {
    val rawText = value.text
    val cursorDigits = rawText.substring(0, value.selection.start.coerceIn(0, rawText.length))
        .count(Char::isDigit)
        .coerceAtMost(8)
    val formattedText = formatDateOfBirthInput(rawText)
    val formattedCursor = when {
        cursorDigits <= 4 -> cursorDigits
        cursorDigits <= 6 -> cursorDigits + 1
        else -> cursorDigits + 2
    }.coerceAtMost(formattedText.length)

    return TextFieldValue(
        text = formattedText,
        selection = TextRange(formattedCursor)
    )
}

private fun isAtLeast18YearsOld(value: String): Boolean {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    val date = runCatching { parser.parse(value.trim()) }.getOrNull() ?: return false
    val birth = Calendar.getInstance().apply { time = date }
    val today = Calendar.getInstance()
    var age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR)
    if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
        age -= 1
    }
    return age >= 18
}

private fun parseIsoDateMillis(value: String): Long? {
    val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { isLenient = false }
    return runCatching { parser.parse(value.trim())?.time }.getOrNull()
}

private fun formatIsoDateMillis(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return formatter.format(Date(millis))
}

private const val DEFAULT_BACKEND_BASE_URL = "http://127.0.0.1:3000"
private const val EMULATOR_BACKEND_BASE_URL = "http://10.0.2.2:3000"
private const val LOCALHOST_BACKEND_BASE_URL = "http://localhost:3000"
private const val CUSTOMER_BACKEND_PREFS = "aiyige_customer_backend"
private const val CUSTOMER_BACKEND_URL_KEY = "backend_url"
private const val CUSTOMER_LAUNCH_BACKEND_URL_EXTRA = "backend_url"

@Volatile
private var activeBackendBaseUrl: String? = null
@Volatile
private var launchBackendBaseUrl: String? = null

private fun normalizeBackendBaseUrl(value: String): String {
    val trimmed = value.trim().removeSuffix("/")
    if (trimmed.isBlank()) return ""
    return when {
        trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) -> trimmed
        else -> "http://$trimmed"
    }
}

private fun loadConfiguredBackendBaseUrl(context: Context): String {
    return normalizeBackendBaseUrl(
        context.getSharedPreferences(CUSTOMER_BACKEND_PREFS, Context.MODE_PRIVATE)
            .getString(CUSTOMER_BACKEND_URL_KEY, "")
            .orEmpty()
    )
}

private fun saveConfiguredBackendBaseUrl(context: Context, value: String) {
    context.getSharedPreferences(CUSTOMER_BACKEND_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putString(CUSTOMER_BACKEND_URL_KEY, normalizeBackendBaseUrl(value))
        .apply()
}

private fun backendBaseUrls(context: Context): List<String> {
    val configured = loadConfiguredBackendBaseUrl(context)
    return buildList {
        launchBackendBaseUrl?.let { add(it) }
        activeBackendBaseUrl?.let { add(it) }
        if (configured.isNotBlank()) add(configured)
        add(EMULATOR_BACKEND_BASE_URL)
        add(DEFAULT_BACKEND_BASE_URL)
        add(LOCALHOST_BACKEND_BASE_URL)
    }.distinct()
}

private fun currentBackendBaseUrl(context: Context): String {
    return launchBackendBaseUrl
        ?: activeBackendBaseUrl
        ?: loadConfiguredBackendBaseUrl(context).ifBlank { DEFAULT_BACKEND_BASE_URL }
}

private fun applyLaunchBackendBaseUrl(intent: android.content.Intent?) {
    val rawValue = intent?.getStringExtra(CUSTOMER_LAUNCH_BACKEND_URL_EXTRA).orEmpty()
    if (rawValue.isBlank()) return

    launchBackendBaseUrl = normalizeBackendBaseUrl(rawValue).takeIf { it.isNotBlank() }
}

private fun resolveBackendImageUrl(raw: String?, baseUrl: String): String? {
    val value = raw?.trim().orEmpty()
    if (value.isEmpty()) return null
    return when {
        value.startsWith("http://") || value.startsWith("https://") || value.startsWith("data:") || value.startsWith("blob:") -> value
        value.startsWith("/assets/") -> "$baseUrl$value"
        value.startsWith("/") -> "$baseUrl$value"
        else -> "$baseUrl/$value"
    }
}

private fun resolveInventoryModelUrl(item: JSONObject, baseUrl: String): String? {
    val explicit = item.optString("modelAsset", "").trim()
    if (explicit.isNotBlank()) return resolveBackendImageUrl(explicit, baseUrl)
    val code = item.optString("productCode", item.optString("name", "")).trim()
    val fallback = mapOf(
        "F-44" to "/3dPanels/Seamless/F - 44 Seamless.glb",
        "F-213" to "/3dPanels/Seamless/F - 213 Seamless.glb",
        "F-222" to "/3dPanels/Seamless/F - 222 Seamless.glb",
        "PG-00" to "/3dPanels/Grove/PG-00 - Grove.glb",
        "LJS-102" to "/3dPanels/UV Board/LJS - 102 UV Board.glb",
        "LJS-110" to "/3dPanels/UV Board/LJS - 110 UV Board.glb",
        "LJS-121" to "/3dPanels/UV Board/LJS - 121 UV Board.glb",
        "150-48" to "/3dPanels/5 in 1/150-48 5in1.glb",
        "PG-08" to "/3dPanels/Grove/PG-08 - Grove.glb",
        "PG-203" to "/3dPanels/Grove/PG-203 - Grove.glb",
        "150-00B" to "/3dPanels/5 in 1/150-00B 5in1.glb",
        "150-10" to "/3dPanels/5 in 1/150-10 5in1.glb",
        "150-05B" to "/3dPanels/5 in 1/150-05B 5in1.glb",
        "160-23" to "/3dPanels/4 in 1 (Fluted)/160 23 - 4in1.glb",
        "160-KB 01" to "/3dPanels/4 in 1 (Fluted)/160 KB 01 - 4in1.glb",
        "160-KB 02" to "/3dPanels/4 in 1 (Fluted)/160 KB 02 - 4in1.glb",
        "195-04" to "/3dPanels/3 in 1/195 - 04 3in1.glb",
        "195-05" to "/3dPanels/3 in 1/195 - 05 3in1.glb",
        "195-07" to "/3dPanels/3 in 1/195 - 07 3in1.glb",
        "01" to "/3dPanels/Wall Board/01 - Wall Board.glb",
        "84-25" to "/3dPanels/Wall Board/84-25 - Wall Board.glb",
        "423" to "/3dPanels/Wall Board/423 - Wall Board.glb"
    )[code] ?: return null
    return resolveBackendImageUrl(fallback, baseUrl)
}

private suspend fun cacheRemoteModel(context: Context, modelUrl: String): String? = withContext(Dispatchers.IO) {
    runCatching {
        val target = File(context.cacheDir, "aiyige-model-${modelUrl.hashCode().toUInt()}.glb")
        if (!target.exists() || target.length() == 0L) {
            URL(modelUrl).openStream().use { input -> target.outputStream().use { output -> input.copyTo(output) } }
        }
        target.absolutePath
    }.getOrNull()
}

private fun formatCaptureTimestamp(date: java.util.Date = java.util.Date()): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(date)
}

private fun buildQuoteNumber(@Suppress("UNUSED_PARAMETER") sequence: Int): String {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    // A local request counter resets when the app is reinstalled or its
    // process is recreated. Using it directly could reuse QT-YYYY-001 and
    // overwrite an older request in the backend. The timestamp suffix is
    // unique across normal customer submissions while preserving the
    // quotation number format used by the staff dashboard.
    val uniqueSuffix = (System.currentTimeMillis() % 1_000_000L)
        .toString()
        .padStart(6, '0')
    return "QT-$year-$uniqueSuffix"
}

private fun calculateQuoteTotals(unitPrice: Int, quantity: Int, discountPercent: Int): Triple<Int, Int, Int> {
    val safeQuantity = maxOf(1, quantity)
    val subtotal = unitPrice * safeQuantity
    val discount = (subtotal * (discountPercent.coerceIn(0, 100) / 100.0)).toInt()
    val taxable = maxOf(subtotal - discount, 0)
    val grandTotal = maxOf(taxable, 0)
    return Triple(subtotal, discount, grandTotal)
}

private fun calculatePanelTotals(unitPrice: Int, quantity: Int): Triple<Int, Int, Int> {
    val safeQuantity = maxOf(1, quantity)
    val subtotal = unitPrice * safeQuantity
    val vat = 0
    val grandTotal = subtotal
    return Triple(subtotal, vat, grandTotal)
}

private fun formatPeso(amount: Int): String = "₱$amount"

private fun toDataUrl(file: File, mimeType: String = "image/jpeg"): String {
    val encoded = Base64.encodeToString(file.readBytes(), Base64.NO_WRAP)
    return "data:$mimeType;base64,$encoded"
}

private suspend fun fetchBackendState(context: Context): JSONObject? = withContext(Dispatchers.IO) {
    for (baseUrl in backendBaseUrls(context)) {
        val state = runCatching {
            val connection = (URL("$baseUrl/api/state").openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("Accept", "application/json")
            }

            try {
                val responseCode = connection.responseCode
                val responseBody = if (responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                }

                if (responseCode in 200..299) JSONObject(responseBody) else null
            } finally {
                connection.disconnect()
            }
        }.getOrNull()

        if (state != null) {
            activeBackendBaseUrl = baseUrl
            return@withContext state
        }
    }

    null
}

private suspend fun saveBackendState(context: Context, state: JSONObject): JSONObject? = withContext(Dispatchers.IO) {
    for (baseUrl in backendBaseUrls(context)) {
        val response = runCatching {
            val connection = (URL("$baseUrl/api/state").openConnection() as HttpURLConnection).apply {
                requestMethod = "PUT"
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }

            try {
                connection.outputStream.use { output ->
                    output.write(state.toString().toByteArray(Charsets.UTF_8))
                }

                val responseCode = connection.responseCode
                val responseBody = if (responseCode in 200..299) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
                }

                if (responseCode in 200..299) JSONObject(responseBody) else null
            } finally {
                connection.disconnect()
            }
        }.getOrNull()

        activeBackendBaseUrl = baseUrl
        return@withContext response
    }

    null
}

private suspend fun prepareQuotation(
    context: Context,
    lines: List<CartLine>,
    requestId: String,
    customerName: String,
    customerEmail: String,
    contactNumber: String,
    streetAddress: String,
    cityProvince: String,
    postalCode: String,
    source: String,
    roomCaptureUrl: String? = null
): JSONObject = withContext(Dispatchers.IO) {
    val items = JSONArray().apply {
        lines.forEach { line ->
            put(JSONObject().apply {
                put("productId", line.product.id)
                put("panelModel", line.product.name)
                put("size", line.product.size)
                put("finish", line.product.material)
                put("quantity", line.quantity)
                put("unitPrice", line.product.price)
            })
        }
    }
    val payload = JSONObject().apply {
        put("requestId", requestId)
        put("source", source)
        put("items", items)
        put("deliveryFee", 0)
        put("roomCaptureUrl", roomCaptureUrl ?: JSONObject.NULL)
        put("customer", JSONObject().apply {
            put("name", customerName)
            put("email", customerEmail)
            put("contact", contactNumber)
            put("deliveryAddress", listOf(streetAddress, cityProvince, postalCode).filter { it.isNotBlank() }.joinToString(", "))
        })
    }
    for (baseUrl in backendBaseUrls(context)) {
        val response = runCatching {
            val connection = (URL("$baseUrl/api/quotations/prepare").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 30_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
            }
            try {
                connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                val responseCode = connection.responseCode
                val body = if (responseCode in 200..299) connection.inputStream else connection.errorStream
                val text = body?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (responseCode in 200..299) JSONObject(text)
                else throw IllegalStateException(JSONObject(text).optString("error", "Quotation could not be prepared."))
            } finally {
                connection.disconnect()
            }
        }.getOrElse { throw it }
        activeBackendBaseUrl = baseUrl
        return@withContext response
    }
    throw IllegalStateException("The quotation could not be prepared by the backend.")
}

private suspend fun createOrderWithQuotation(
    context: Context,
    lines: List<CartLine>,
    quotationNumber: String,
    customerName: String,
    customerEmail: String,
    contactNumber: String,
    streetAddress: String,
    cityProvince: String,
    postalCode: String,
    paymentMethod: String,
    courier: String,
    source: String,
    roomCaptureUrl: String? = null,
    paymentReference: String = ""
): JSONObject = withContext(Dispatchers.IO) {
    val items = JSONArray().apply {
        lines.forEach { line ->
            put(JSONObject().apply {
                put("productId", line.product.id)
                put("panelModel", line.product.name)
                put("size", line.product.size)
                put("finish", line.product.material)
                put("quantity", line.quantity)
                put("unitPrice", line.product.price)
            })
        }
    }
    val payload = JSONObject().apply {
        put("quotationNumber", quotationNumber)
        put("source", source)
        put("items", items)
        put("deliveryFee", 0)
        put("courier", courier)
        put("roomCaptureUrl", roomCaptureUrl ?: JSONObject.NULL)
        put("paymentMethod", paymentMethod)
        put("paymentReference", paymentReference)
        put("customer", JSONObject().apply {
            put("name", customerName)
            put("email", customerEmail)
            put("contact", contactNumber)
            put("deliveryAddress", listOf(streetAddress, cityProvince, postalCode).filter { it.isNotBlank() }.joinToString(", "))
        })
    }
    for (baseUrl in backendBaseUrls(context)) {
        val response = runCatching {
            val connection = (URL("$baseUrl/api/orders").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 10_000
                readTimeout = 30_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
            }
            try {
                connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                val responseCode = connection.responseCode
                val body = if (responseCode in 200..299) connection.inputStream else connection.errorStream
                val text = body?.bufferedReader()?.use { it.readText() }.orEmpty()
                if (responseCode in 200..299) JSONObject(text) else throw IllegalStateException(JSONObject(text).optString("error", "Order could not be created."))
            } finally {
                connection.disconnect()
            }
        }.getOrElse { throw it }
        activeBackendBaseUrl = baseUrl
        return@withContext response
    }
    throw IllegalStateException("The order could not be submitted to the backend.")
}

private suspend fun saveRoomCaptureToBackend(
    context: Context,
    selectedProduct: Product,
    customerName: String,
    streetAddress: String = "",
    cityProvince: String = "",
    postalCode: String = "",
    contactNumber: String = "",
    emailAddress: String = "",
    finish: String,
    size: String,
    quantity: String,
    notes: String,
    file: File,
    discountPercent: Int = 0
): String? {
    val dataUrl = runCatching { toDataUrl(file) }.getOrNull() ?: return null
    val captureId = System.currentTimeMillis()
    val state = fetchBackendState(context) ?: return null
    val roomCaptures = state.optJSONArray("roomCaptures") ?: JSONArray()
    val quantityValue = maxOf(1, quantity.toIntOrNull() ?: 1)

    val captureRecord = JSONObject().apply {
        put("id", captureId)
        put("customerName", customerName)
        put("streetAddress", streetAddress)
        put("cityProvince", cityProvince)
        put("postalCode", postalCode)
        put("productId", selectedProduct.id)
        put("productName", selectedProduct.name)
        put("finish", finish)
        put("size", size.ifBlank { selectedProduct.size })
        put("quantity", quantityValue)
        put("notes", notes)
        put("capturedAt", formatCaptureTimestamp())
        put("status", "Captured")
        put("discountPercent", discountPercent)
        put("discountAmount", 0)
        put("imageData", dataUrl)
    }
    roomCaptures.put(captureRecord)
    state.put("roomCaptures", roomCaptures)

    val response = saveBackendState(context, state) ?: return null
    val responseState = response.optJSONObject("state") ?: state
    val savedCaptures = responseState.optJSONArray("roomCaptures") ?: return null

    for (index in 0 until savedCaptures.length()) {
        val record = savedCaptures.optJSONObject(index) ?: continue
        if (record.optLong("id", -1L) == captureId) {
            return resolveBackendImageUrl(
                record.optString("imageAsset", record.optString("imageUrl", dataUrl)),
                currentBackendBaseUrl(context)
            )
        }
    }

    return resolveBackendImageUrl(dataUrl, currentBackendBaseUrl(context))
}

private suspend fun saveCustomerRegistrationToBackend(context: Context, user: RegisteredUser): Boolean = withContext(Dispatchers.IO) {
    runCatching {
        val state = fetchBackendState(context) ?: return@withContext false
        val customers = state.optJSONArray("customers") ?: JSONArray()
        val now = formatCaptureTimestamp()
        val existingIndex = (0 until customers.length()).firstOrNull { index ->
            val record = customers.optJSONObject(index) ?: return@firstOrNull false
            record.optString("email", "").equals(user.email, ignoreCase = true)
        }

        val customerRecord = JSONObject().apply {
            put("username", user.username)
            put("name", user.displayName)
            put("firstName", user.firstName)
            put("middleName", user.middleName)
            put("lastName", user.lastName)
            put("gender", user.gender)
            put("dateOfBirth", user.dateOfBirth)
            put("contact", user.mobileNumber)
            put("mobileNumber", user.mobileNumber)
            put("streetAddress", user.streetAddress)
            put("cityProvince", user.cityProvince)
            put("postalCode", user.postalCode)
            put("address", listOf(user.streetAddress, user.cityProvince, user.postalCode).filter { it.isNotBlank() }.joinToString(", "))
            put("quote", "Mobile signup")
            put("notes", "Registered from customer app")
            put("email", user.email)
            put("source", "Customer App")
            put("createdAt", user.createdAt.ifBlank { now })
            put("updatedAt", now)
        }

        if (existingIndex != null) {
            customers.put(existingIndex, customerRecord)
        } else {
            customers.put(customerRecord)
        }
        state.put("customers", customers)
        saveBackendState(context, state) != null
    }.getOrDefault(false)
}

private suspend fun loadRegisteredUsersFromBackend(context: Context): List<RegisteredUser> = withContext(Dispatchers.IO) {
    runCatching {
        val state = fetchBackendState(context) ?: return@withContext emptyList()
        val customers = state.optJSONArray("customers") ?: return@withContext emptyList()
        buildList {
            for (index in 0 until customers.length()) {
                val record = customers.optJSONObject(index) ?: continue
                val user = registeredUserFromJson(record) ?: continue
                add(user)
            }
        }
    }.getOrDefault(emptyList())
}

private val sampleProducts = listOf(
    Product(1, "Seamless Ceiling Panel", "UPVC Ceiling Panel", "2.4m x 1.2m", "UPVC", 650, 18, "Clean and modern ceiling finish for residential or commercial interiors.", "Seamless"),
    Product(2, "Groove Ceiling Panel", "UPVC Ceiling Panel", "2.4m x 1.2m", "UPVC", 680, 14, "Textured ceiling finish with a more defined panel line.", "Groove"),
    Product(3, "WPC Fluted Wall Cladding", "WPC Wall Cladding", "2.9m x 0.2m", "WPC", 980, 9, "Premium fluted panel for feature walls and business-grade interiors.", "Fluted"),
    Product(4, "Outdoor Deck Panel", "WPC Outdoor", "3.0m x 0.15m", "WPC", 1120, 4, "Durable panel option for exterior applications.", "Outdoor"),
    Product(5, "Wall Board Accent Panel", "Other Products", "2.4m x 1.2m", "Composite", 850, 12, "Versatile accent board for renovation and design upgrades.", "Other")
)

private enum class Tab { Home, Catalog, Cart, Ar, Customization, Settings }

class MainActivity : ComponentActivity() {
    private val recoveryRedirectEvents = MutableSharedFlow<Uri>(replay = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        applyLaunchBackendBaseUrl(intent)
        intent.data?.let { recoveryRedirectEvents.tryEmit(it) }
        setContent {
            AiyigeCustomerTheme {
                CustomerApp(recoveryRedirectEvents = recoveryRedirectEvents)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        applyLaunchBackendBaseUrl(intent)
        intent.data?.let { recoveryRedirectEvents.tryEmit(it) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomerApp(recoveryRedirectEvents: SharedFlow<Uri> = MutableSharedFlow()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var allowDrawerOpen by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed,
        confirmStateChange = { target ->
            if (target == DrawerValue.Open) {
                val allowed = allowDrawerOpen
                allowDrawerOpen = false
                allowed
            } else {
                true
            }
        }
    )
    var syncTick by rememberSaveable { mutableIntStateOf(0) }
    val inventoryUiState by produceState(
        key1 = syncTick,
        initialValue = InventoryUiState(
            products = sampleProducts,
            isLoading = true,
            sourceLabel = "Connecting to backend..."
        )
    ) {
        value = loadInventory(context)
    }

    val products = remember(inventoryUiState.products, inventoryUiState.errorMessage) {
        when {
            inventoryUiState.products.isNotEmpty() -> inventoryUiState.products
            inventoryUiState.errorMessage != null -> sampleProducts
            else -> emptyList()
        }
    }

    var currentTab by rememberSaveable { mutableStateOf(Tab.Home) }
    var recentlyViewedIds by rememberSaveable { mutableStateOf<List<Int>>(emptyList()) }
    var settingsPage by rememberSaveable { mutableStateOf(SettingsPage.Home) }
    var settingsLastAuthPage by rememberSaveable { mutableStateOf(SettingsPage.Login) }
    var settingsReturnTab by rememberSaveable { mutableStateOf(Tab.Home) }
    var pendingPostLoginTab by rememberSaveable { mutableStateOf("") }
    var selectedProductId by rememberSaveable { mutableStateOf<Int?>(null) }
    var cart by remember { mutableStateOf<List<CartLine>>(emptyList()) }
    var isSignedIn by rememberSaveable { mutableStateOf(false) }
    var isDemoAccount by rememberSaveable { mutableStateOf(false) }
    var signInName by rememberSaveable { mutableStateOf("Guest User") }
    var signInEmail by rememberSaveable { mutableStateOf("") }
    var requestCount by rememberSaveable { mutableIntStateOf(0) }
    var backendUrlDraft by rememberSaveable { mutableStateOf(loadConfiguredBackendBaseUrl(context)) }
    val registeredUsers = remember { mutableStateListOf<RegisteredUser>() }
    var search by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    var quoteName by rememberSaveable { mutableStateOf("") }
    var quoteEmail by rememberSaveable { mutableStateOf("") }
    var quotePhone by rememberSaveable { mutableStateOf("") }
    var quoteQty by rememberSaveable { mutableStateOf("1") }
    var quoteNotes by rememberSaveable { mutableStateOf("") }
    var lastQuoteSummary by rememberSaveable { mutableStateOf<String?>(null) }
    var latestQuoteNumber by rememberSaveable { mutableStateOf("") }
    var latestQuotationItemsJson by rememberSaveable { mutableStateOf("") }
    var latestQuotationIssuedAt by rememberSaveable { mutableStateOf("") }
    var latestQuotationBusinessJson by rememberSaveable { mutableStateOf("") }
    var latestQuoteItemName by rememberSaveable { mutableStateOf("") }
    var latestQuoteUnitPrice by rememberSaveable { mutableStateOf("₱0") }
    var latestQuoteSubtotal by rememberSaveable { mutableStateOf("₱0") }
    var latestQuoteVat by rememberSaveable { mutableStateOf("₱0") }
    var latestQuotePanels by rememberSaveable { mutableStateOf("0") }
    var latestQuoteAmount by rememberSaveable { mutableStateOf("₱0") }
    var paymentStatusMessage by rememberSaveable { mutableStateOf("") }
    var selectedPaymentMethod by rememberSaveable { mutableStateOf("") }
    var selectedDeliveryMethod by rememberSaveable { mutableStateOf("") }
    var configuredPaymentMethods by remember { mutableStateOf(listOf("Cash", "GCash", "Bank Transfer")) }
    var latestRoomCaptureUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var latestRoomCaptureLabel by rememberSaveable { mutableStateOf("No room capture saved yet.") }
    var selectedQuantity by rememberSaveable { mutableIntStateOf(1) }
    var paymentScreenOpen by rememberSaveable { mutableStateOf(false) }
    var isPreparingQuotation by rememberSaveable { mutableStateOf(false) }
    var addressScreenOpen by rememberSaveable { mutableStateOf(false) }
    var addressStreet by rememberSaveable { mutableStateOf("") }
    var addressCityProvince by rememberSaveable { mutableStateOf("") }
    var addressPostalCode by rememberSaveable { mutableStateOf("") }
    var pendingCheckoutLines by remember { mutableStateOf<List<CartLine>?>(null) }
    var pendingCheckoutReturnTab by rememberSaveable { mutableStateOf(Tab.Cart) }
    // The exact quantities that entered the current checkout. Keep this
    // separate from the cart selection so finishing checkout removes only
    // what was purchased and leaves any remainder in the cart.
    var activeCheckoutLines by remember { mutableStateOf<List<CartLine>>(emptyList()) }
    var paymentSubmitted by rememberSaveable { mutableStateOf(false) }
    var isPaymentSubmitting by rememberSaveable { mutableStateOf(false) }
    var paymentReturnTab by rememberSaveable { mutableStateOf(Tab.Settings) }
    var recentQuoteHistory by remember { mutableStateOf<List<QuoteHistoryEntry>>(emptyList()) }
    var customerOrderUpdates by remember { mutableStateOf<List<CustomerOrderUpdate>>(emptyList()) }
    var customizationFinish by rememberSaveable(selectedProductId) { mutableStateOf("Matte") }
    var customizationSize by rememberSaveable(selectedProductId) { mutableStateOf("2.4m x 1.2m") }
    var customizationColour by rememberSaveable(selectedProductId) { mutableStateOf("Oak") }
    var customizationPattern by rememberSaveable(selectedProductId) { mutableStateOf("Horizontal") }
    // Keep the customer's chosen preview mode while switching between panels.
    var customizationIs3d by rememberSaveable { mutableStateOf(false) }
    // Keep the quantity while the same product is selected. Using selectedQuantity
    // as a rememberSaveable key recreated this value as "1" after every +/- click.
    var customizationQuantity by rememberSaveable(selectedProductId) { mutableStateOf("1") }
    var customizationNotes by rememberSaveable(selectedProductId) { mutableStateOf("") }
    var customizationAddOnsCsv by rememberSaveable(selectedProductId) { mutableStateOf("") }
    var savedCustomizations by remember { mutableStateOf<List<SavedCustomization>>(emptyList()) }
    var showSavedPanels by rememberSaveable { mutableStateOf(false) }
    var showNotificationPanel by rememberSaveable { mutableStateOf(false) }
    var showAutomaticEstimate by rememberSaveable { mutableStateOf(false) }
    var orderTrackingEntry by remember { mutableStateOf<CustomerOrderUpdate?>(null) }
    var selectedSavedPanelIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var pendingSignupUser by remember { mutableStateOf<RegisteredUser?>(null) }
    var pendingRecoveryEmail by rememberSaveable { mutableStateOf("") }
    var pendingOtpType by rememberSaveable { mutableStateOf("") }
    var authNotice by rememberSaveable { mutableStateOf("") }
    var isAuthEmailRequestInFlight by rememberSaveable { mutableStateOf(false) }
    val customerReferenceName = quoteName.ifBlank { signInName }
    val compactSignedInName = compactDisplayName(signInName).ifBlank { signInName.substringBefore(" ").ifBlank { signInName } }
    val deliveryMethodOptions = remember { listOf("J&T", "Lalamove") }

    val excludedCatalogCategories = remember {
        setOf(
            "upvc ceiling lines",
            "wall cladding accessories",
            "wall outdoor accessories",
            "ceiling baffle accessories",
            "new products",
            "tubular",
            "ceiling",
            "wpc products"
        )
    }
    val categories = remember(products) {
        listOf("All") + products
            .map { it.category.trim() }
            .filter { it.isNotBlank() && it.lowercase(Locale.ROOT) !in excludedCatalogCategories }
            .distinct()
    }
    val filteredProducts = remember(search, category, products) {
        products.filter {
            (search.isBlank() || it.name.contains(search, ignoreCase = true) || it.material.contains(search, ignoreCase = true)) &&
                it.category.trim().lowercase(Locale.ROOT) !in excludedCatalogCategories &&
                (category == "All" || it.category.equals(category, ignoreCase = true))
        }
    }
    val selectedProduct = products.firstOrNull { it.id == selectedProductId }
    val cartItemCount = cart.sumOf { it.quantity }
    fun addToCart(product: Product, quantity: Int = 1): Boolean {
        if (product.quantity <= 0) return false
        val safeQuantity = quantity.coerceIn(1, product.quantity)
        val existing = cart.firstOrNull { it.product.id == product.id }
        cart = if (existing == null) {
            cart + CartLine(product, safeQuantity)
        } else {
            cart.map { line ->
                if (line.product.id == product.id) line.copy(quantity = (line.quantity + safeQuantity).coerceAtMost(product.quantity)) else line
            }
        }
        return true
    }
    fun updateCartQuantity(productId: Int, quantity: Int) {
        cart = cart.mapNotNull { line ->
            if (line.product.id != productId) line
            else if (quantity <= 0) null
            else line.copy(quantity = quantity.coerceAtMost(line.product.quantity))
        }
    }
    fun toggleCartSelection(productId: Int) {
        cart = cart.map { line ->
            if (line.product.id == productId) line.copy(selected = !line.selected) else line
        }
    }
    fun setAllCartSelection(selected: Boolean) {
        cart = cart.map { it.copy(selected = selected) }
    }
    val selectedAddOns = customizationAddOnsCsv.split("|").filter { it.isNotBlank() }
    LaunchedEffect(products, selectedProductId) {
        if (products.isEmpty()) return@LaunchedEffect
        val currentId = selectedProductId
        val needsDefaultSelection = currentId == null || products.none { it.id == currentId }
        if (needsDefaultSelection) {
            selectedProductId = products.first().id
        }
    }

    LaunchedEffect(products) {
        if (recentlyViewedIds.isEmpty() && products.isNotEmpty()) {
            recentlyViewedIds = products.take(4).map { it.id }
        }
    }
    val currentRegisteredUser = registeredUsers.firstOrNull { it.email.equals(signInEmail, ignoreCase = true) }
    val paymentMethodOptions = configuredPaymentMethods
    LaunchedEffect(paymentScreenOpen, isSignedIn, signInEmail) {
        if (!paymentScreenOpen || !isSignedIn || signInEmail.isBlank()) return@LaunchedEffect
        val backendUser = loadRegisteredUsersFromBackend(context)
            .firstOrNull { it.email.equals(signInEmail, ignoreCase = true) }
            ?: return@LaunchedEffect
        val existingIndex = registeredUsers.indexOfFirst { it.email.equals(signInEmail, ignoreCase = true) }
        if (existingIndex >= 0) {
            registeredUsers[existingIndex] = mergeRegisteredUsers(registeredUsers[existingIndex], backendUser)
        } else {
            registeredUsers.add(backendUser)
        }
        saveRegisteredUsersToPrefs(context, registeredUsers.toList())
    }
    LaunchedEffect(isSignedIn, signInEmail, signInName, currentTab, syncTick) {
        customerOrderUpdates = if (isSignedIn && signInEmail.isNotBlank()) {
            loadCustomerOrderUpdates(context, signInEmail, signInName)
        } else {
            emptyList()
        }
    }

    LaunchedEffect(isSignedIn, signInEmail, currentRegisteredUser?.displayName) {
        if (!isSignedIn) return@LaunchedEffect
        val preferredName = currentRegisteredUser?.displayName?.trim().orEmpty()
        if (preferredName.isNotBlank() && preferredName != signInName) {
            signInName = preferredName
        }
    }
    LaunchedEffect(
        addressScreenOpen,
        currentRegisteredUser?.email,
        currentRegisteredUser?.streetAddress,
        currentRegisteredUser?.cityProvince,
        currentRegisteredUser?.postalCode
    ) {
        if (addressScreenOpen && pendingCheckoutLines == null) {
            addressStreet = currentRegisteredUser?.streetAddress.orEmpty()
            addressCityProvince = currentRegisteredUser?.cityProvince.orEmpty()
            addressPostalCode = currentRegisteredUser?.postalCode.orEmpty()
        }
    }
    fun upsertRegisteredUser(nextUser: RegisteredUser) {
        val normalizedUser = nextUser.copy(
            username = nextUser.username.trim(),
            email = nextUser.email.trim(),
            firstName = nextUser.firstName.trim(),
            lastName = nextUser.lastName.trim(),
            middleName = nextUser.middleName.trim(),
            dateOfBirth = nextUser.dateOfBirth.trim(),
            mobileNumber = nextUser.mobileNumber.trim(),
            password = nextUser.password
        )
        val existingIndex = registeredUsers.indexOfFirst { it.email.equals(normalizedUser.email, ignoreCase = true) }
        if (existingIndex >= 0) {
            registeredUsers[existingIndex] = normalizedUser
        } else {
            registeredUsers.add(normalizedUser)
        }
        saveRegisteredUsersToPrefs(context, registeredUsers)
    }
    fun applyAuthenticatedSession(
        displayName: String,
        email: String,
        session: SupabaseSessionCache? = null
    ) {
        isSignedIn = true
        isDemoAccount = false
        signInName = displayName.ifBlank { displayNameFromEmail(email) }
        signInEmail = email
        // Keep a lightweight local profile so the customer stays signed in
        // across app launches, including when the backend is temporarily offline.
        saveLocalSession(context, email, signInName)
        session?.let {
            saveSupabaseSession(
                context,
                it.copy(
                    email = email,
                    displayName = signInName
                )
            )
        }
        val nextEmail = email.trim()
        if (nextEmail.isNotBlank()) {
            val existingUser = registeredUsers.firstOrNull { it.email.equals(nextEmail, ignoreCase = true) }
            val authUser = (existingUser ?: RegisteredUser(
                username = signInName.substringBefore(" ").lowercase(Locale.getDefault()).ifBlank { nextEmail.substringBefore("@") },
                firstName = signInName,
                lastName = "",
                middleName = "",
                dateOfBirth = "",
                gender = "",
                email = nextEmail,
                mobileNumber = "",
                password = ""
            )).copy(
                firstName = existingUser?.firstName?.ifBlank { signInName } ?: signInName,
                email = nextEmail,
                password = ""
            )
            upsertRegisteredUser(authUser)
            signInName = authUser.displayName.ifBlank { signInName }
        }
    }
    LaunchedEffect(recoveryRedirectEvents) {
        recoveryRedirectEvents.collect { uri ->
            val authParams = uri.collectAuthParams()
            val authType = authParams["type"].orEmpty()
            val redirectEmail = authParams["email"].orEmpty().ifBlank {
                pendingSignupUser?.email.orEmpty().ifBlank { pendingRecoveryEmail }
            }
            val normalizedAuthType = normalizeAuthVerificationType(authType).ifBlank { "recovery" }

            val callbackSession = extractSupabaseSessionFromRedirect(
                uri = uri,
                fallbackEmail = redirectEmail,
                fallbackDisplayName = pendingSignupUser?.displayName.orEmpty()
            )
            if (callbackSession != null) {
                saveSupabaseSession(context, callbackSession)
            }

            currentTab = Tab.Settings
            if (normalizedAuthType == "recovery") {
                if (callbackSession == null) {
                    settingsPage = SettingsPage.ForgotPassword
                    settingsLastAuthPage = SettingsPage.ForgotPassword
                    authNotice = "This reset link is missing or has expired. Request a new password reset link."
                } else {
                    pendingRecoveryEmail = callbackSession.email
                    settingsPage = SettingsPage.RecoverPassword
                    settingsLastAuthPage = SettingsPage.RecoverPassword
                    authNotice = "Choose a new password to finish resetting your account."
                }
            } else {
                pendingSignupUser?.takeIf { it.email.equals(redirectEmail, ignoreCase = true) }?.let { confirmedUser ->
                    upsertRegisteredUser(confirmedUser)
                    signInName = confirmedUser.displayName.ifBlank { displayNameFromEmail(confirmedUser.email) }
                }
                pendingSignupUser = null
                settingsPage = SettingsPage.Login
                settingsLastAuthPage = SettingsPage.Login
                authNotice = "Your email has been confirmed. You can sign in now."
            }
        }
    }

    val settingsAuthPageOpen = currentTab == Tab.Settings && !isSignedIn && settingsPage in setOf(
        SettingsPage.Login,
        SettingsPage.Register,
        SettingsPage.ForgotPassword,
        SettingsPage.VerifyEmail,
        SettingsPage.RecoverPassword
    )
    fun openSettings(page: SettingsPage = if (isSignedIn) SettingsPage.Home else SettingsPage.Login, returnTab: Tab = currentTab) {
        settingsReturnTab = returnTab
        currentTab = Tab.Settings
        settingsPage = page
        if (page != SettingsPage.Home) {
            settingsLastAuthPage = page
        }
    }

    fun openLoginForAr(returnTab: Tab = currentTab, selectedProductForAr: Int? = null) {
        selectedProductForAr?.let { selectedProductId = it }
        pendingPostLoginTab = Tab.Ar.name
        settingsReturnTab = returnTab
        currentTab = Tab.Settings
        settingsPage = SettingsPage.Login
        settingsLastAuthPage = SettingsPage.Login
    }

    fun openArOrLogin(returnTab: Tab = currentTab, selectedProductForAr: Int? = null) {
        if (isSignedIn) {
            selectedProductForAr?.let { selectedProductId = it }
            pendingPostLoginTab = ""
            currentTab = Tab.Ar
        } else {
            openLoginForAr(returnTab = returnTab, selectedProductForAr = selectedProductForAr)
        }
    }

    fun authBackLabel(): String {
        return when (settingsPage) {
            SettingsPage.Register,
            SettingsPage.ForgotPassword,
            SettingsPage.VerifyEmail,
            SettingsPage.RecoverPassword -> "Back to log in"
            SettingsPage.Login -> when (settingsReturnTab) {
                Tab.Home -> "Back to Home"
                Tab.Catalog -> "Back to Catalog"
                Tab.Cart -> "Back to Cart"
                Tab.Ar -> "Back to AR"
                Tab.Customization -> "Back to Customize"
                Tab.Settings -> "Back"
            }
            SettingsPage.Home,
            SettingsPage.ProfileDetails,
            SettingsPage.DeliveryAddress,
            SettingsPage.Password,
            SettingsPage.Notifications,
            SettingsPage.Quotations,
            SettingsPage.HelpSupport,
            SettingsPage.TermsPrivacy,
            SettingsPage.AboutApp -> "Back"
        }
    }

    fun returnFromSettingsAuth() {
        if (isSignedIn) {
            currentTab = Tab.Settings
            settingsPage = SettingsPage.Home
            settingsLastAuthPage = SettingsPage.Home
        } else if (
            settingsPage == SettingsPage.RecoverPassword ||
                settingsPage == SettingsPage.ForgotPassword ||
                settingsPage == SettingsPage.VerifyEmail ||
                settingsPage == SettingsPage.Register
        ) {
            settingsPage = SettingsPage.Login
            settingsLastAuthPage = SettingsPage.Login
        } else {
            currentTab = settingsReturnTab
            settingsPage = settingsLastAuthPage
        }
    }

    fun openPayment(returnTab: Tab = currentTab) {
        paymentReturnTab = returnTab
        paymentSubmitted = false
        selectedPaymentMethod = ""
        selectedDeliveryMethod = ""
        paymentScreenOpen = true
    }

    fun closePayment() {
        paymentScreenOpen = false
        currentTab = paymentReturnTab
    }

    fun startCartCheckout(lines: List<CartLine>, returnTab: Tab = currentTab) {
        activeCheckoutLines = lines
        val subtotal = lines.sumOf { it.product.price * it.quantity }
        val vat = 0
        latestQuoteNumber = ""
        latestQuotationItemsJson = ""
        latestQuotationIssuedAt = ""
        latestQuotationBusinessJson = ""
        latestQuoteItemName = lines.joinToString(", ") { "${it.product.name} x${it.quantity}" }
        latestQuoteUnitPrice = if (lines.size == 1) formatPeso(lines.first().product.price) else "Multiple items"
        latestQuoteSubtotal = formatPeso(subtotal)
        latestQuoteVat = formatPeso(vat)
        latestQuotePanels = lines.sumOf { it.quantity }.toString()
        latestQuoteAmount = formatPeso(subtotal + vat)
        lastQuoteSummary = latestQuoteItemName
        paymentSubmitted = false
        // Catalog/cart checkout and AR checkout prepare the official quote
        // before the customer chooses delivery or payment.
        paymentStatusMessage = ""
        showAutomaticEstimate = false
        paymentReturnTab = returnTab
        openPayment(returnTab = returnTab)
        isPreparingQuotation = true
        val requestId = "${signInEmail.lowercase()}-${System.currentTimeMillis()}-${requestCount + 1}"
        scope.launch {
            try {
                val result = runCatching {
                    withTimeout(30_000) {
                        prepareQuotation(
                            context = context,
                            lines = lines,
                            requestId = requestId,
                            customerName = signInName,
                            customerEmail = signInEmail,
                            contactNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                            streetAddress = currentRegisteredUser?.streetAddress.orEmpty(),
                            cityProvince = currentRegisteredUser?.cityProvince.orEmpty(),
                            postalCode = currentRegisteredUser?.postalCode.orEmpty(),
                            source = if (latestRoomCaptureUrl != null) "AR" else "CATALOG",
                            roomCaptureUrl = latestRoomCaptureUrl
                        )
                    }
                }
                result.onSuccess { response ->
                    val quotation = response.optJSONObject("quotation")
                    latestQuoteNumber = quotation?.optString("quotation_no").orEmpty()
                    latestQuotationItemsJson = quotation?.optJSONArray("items_snapshot")?.toString().orEmpty()
                    latestQuotationIssuedAt = quotation?.optString("created_at").orEmpty()
                    latestQuotationBusinessJson = quotation?.optJSONObject("business")?.toString().orEmpty()
                    val total = quotation?.optDouble("total", parsePesoAmount(latestQuoteAmount).toDouble())
                        ?: parsePesoAmount(latestQuoteAmount).toDouble()
                    latestQuoteAmount = formatPeso(total.toInt())
                    latestQuoteSubtotal = formatPeso(quotation?.optDouble("subtotal", subtotal.toDouble())?.toInt() ?: subtotal)
                    latestQuoteVat = formatPeso(quotation?.optDouble("vat", 0.0)?.toInt() ?: 0)
                    lastQuoteSummary = latestQuoteItemName
                    requestCount += 1
                    recentQuoteHistory = listOf(
                        QuoteHistoryEntry(
                            quoteNumber = latestQuoteNumber,
                            customerEmail = signInEmail,
                            summary = latestQuoteItemName,
                            amount = latestQuoteAmount,
                            status = "Quotation ready",
                            createdAt = quotation?.optString("created_at").orEmpty().ifBlank { formatCaptureTimestamp() }
                        )
                    ) + recentQuoteHistory
                }.onFailure { error ->
                    paymentScreenOpen = false
                    paymentStatusMessage = error.message ?: "Unable to prepare your quotation. Please try again."
                    Toast.makeText(context, paymentStatusMessage, Toast.LENGTH_LONG).show()
                }
            } finally {
                isPreparingQuotation = false
            }
        }
    }

    fun createQuotationFromCustomization(returnTab: Tab) {
        val product = selectedProduct ?: return
        val quantityValue = customizationQuantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
        val addOnPrices = mapOf("Fixings Kit" to 18, "Pro Adhesive" to 12, "Edge Trim" to 24)
        val sizeAdjustment = if (customizationSize.contains("300")) 12 else 0
        val addOnTotal = selectedAddOns.sumOf { addOnPrices[it] ?: 0 }
        val subtotal = (product.price + addOnTotal + sizeAdjustment) * quantityValue
        val vat = 0
        val grandTotal = subtotal
        quoteQty = quantityValue.toString()
        quoteNotes = buildList {
            add("Finish: $customizationFinish")
            add("Colour: $customizationColour")
            add("View: ${if (customizationIs3d) "3D" else "2D"}")
            add("Size: ${customizationSize.ifBlank { product.size }}")
            if (selectedAddOns.isNotEmpty()) add("Add-ons: ${selectedAddOns.joinToString(", ")}")
            if (latestRoomCaptureUrl != null) add("Room capture: attached")
            if (customizationNotes.isNotBlank()) add(customizationNotes)
        }.joinToString("\n")
        latestQuoteNumber = buildQuoteNumber((requestCount + 1).coerceAtLeast(1))
        latestQuoteItemName = "${product.name} x$quantityValue"
        latestQuoteUnitPrice = "₱${product.price}"
        latestQuoteSubtotal = "₱$subtotal"
        latestQuoteVat = "₱$vat"
        latestQuotePanels = quantityValue.toString()
        latestQuoteAmount = "₱$grandTotal"
        lastQuoteSummary = latestQuoteItemName
        paymentStatusMessage = ""
        requestCount += 1
        recentQuoteHistory = listOf(
            QuoteHistoryEntry(
                quoteNumber = latestQuoteNumber,
                customerEmail = signInEmail.ifBlank { customerReferenceName },
                summary = latestQuoteItemName,
                amount = latestQuoteAmount,
                status = "Estimate",
                createdAt = formatCaptureTimestamp()
            )
        ) + recentQuoteHistory
        currentTab = returnTab
        showAutomaticEstimate = true
    }

    LaunchedEffect(isSignedIn, currentTab, settingsPage, pendingCheckoutLines) {
        if (!isSignedIn || pendingCheckoutLines == null || currentTab != Tab.Settings || settingsPage != SettingsPage.Home) return@LaunchedEffect
        val lines = pendingCheckoutLines ?: return@LaunchedEffect
        pendingCheckoutLines = null
        if (currentRegisteredUser?.streetAddress.isNullOrBlank() ||
            currentRegisteredUser?.cityProvince.isNullOrBlank() ||
            currentRegisteredUser?.postalCode.isNullOrBlank()
        ) {
            addressStreet = currentRegisteredUser?.streetAddress.orEmpty()
            addressCityProvince = currentRegisteredUser?.cityProvince.orEmpty()
            addressPostalCode = currentRegisteredUser?.postalCode.orEmpty()
            addressScreenOpen = true
        } else {
            startCartCheckout(lines, returnTab = pendingCheckoutReturnTab)
        }
    }

    BackHandler(enabled = paymentScreenOpen || addressScreenOpen || orderTrackingEntry != null || currentTab == Tab.Settings) {
        when {
            paymentScreenOpen -> closePayment()
            orderTrackingEntry != null -> orderTrackingEntry = null
            addressScreenOpen -> {
                addressScreenOpen = false
                pendingCheckoutLines = null
            }
            currentTab == Tab.Settings && !isSignedIn && settingsPage != SettingsPage.Login -> {
                if (settingsPage == SettingsPage.Register ||
                    settingsPage == SettingsPage.ForgotPassword ||
                    settingsPage == SettingsPage.VerifyEmail ||
                    settingsPage == SettingsPage.RecoverPassword
                ) {
                    settingsPage = SettingsPage.Login
                    settingsLastAuthPage = SettingsPage.Login
                } else {
                    currentTab = settingsReturnTab
                    settingsPage = settingsLastAuthPage
                }
            }
            currentTab == Tab.Settings && isSignedIn && settingsPage == SettingsPage.Home -> {
                currentTab = settingsReturnTab
            }
            currentTab == Tab.Settings -> {
                currentTab = settingsReturnTab
            }
        }
    }

    // The drawer is a modal surface, so the system back button should dismiss
    // it before navigating away from the current screen.
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    LaunchedEffect(selectedProduct?.id) {
        customizationSize = "2400×200mm"
        selectedQuantity = 1
        customizationQuantity = "1"
        customizationFinish = "Matte"
        customizationColour = "Oak"
        customizationPattern = "Horizontal"
        customizationNotes = selectedProduct?.name?.let { "Base panel: $it" }.orEmpty()
        customizationAddOnsCsv = ""
    }

    LaunchedEffect(products) {
        if (selectedProductId != null && products.none { it.id == selectedProductId }) {
            selectedProductId = null
        }
    }

    LaunchedEffect(Unit) {
        val backendState = fetchBackendState(context)
        backendState?.optJSONObject("settings")
            ?.optJSONArray("paymentModes")
            ?.let { modes ->
                val backendMethods = buildList {
                    for (index in 0 until modes.length()) {
                        val method = modes.optString(index).trim()
                        if (method.isNotBlank()) add(method)
                    }
                }
                if (backendMethods.isNotEmpty()) {
                    configuredPaymentMethods = backendMethods
                    if (selectedPaymentMethod !in backendMethods) selectedPaymentMethod = ""
                }
            }

        val remoteSession = loadSupabaseSession(context)
        val localSession = loadLocalSession(context)
        val session = remoteSession
        if (session != null) {
            isSignedIn = true
            signInEmail = session.email
            signInName = session.displayName
            settingsPage = SettingsPage.Home
            settingsLastAuthPage = SettingsPage.Home
        } else {
            localSession?.let { (email, displayName) ->
                isSignedIn = true
                signInEmail = email
                signInName = displayName
                settingsPage = SettingsPage.Home
                settingsLastAuthPage = SettingsPage.Home
            }
        }

        val storedUsers = loadRegisteredUsersFromPrefs(context)
        if (storedUsers.isNotEmpty()) {
            registeredUsers.clear()
            registeredUsers.addAll(storedUsers)
        }

        val backendUsers = loadRegisteredUsersFromBackend(context)
        if (backendUsers.isNotEmpty()) {
            val mergedUsers = buildList {
                addAll(registeredUsers)
                backendUsers.forEach { backendUser ->
                    val existingIndex = indexOfFirst { it.email.equals(backendUser.email, ignoreCase = true) }
                    if (existingIndex >= 0) {
                        set(existingIndex, mergeRegisteredUsers(get(existingIndex), backendUser))
                    } else {
                        add(backendUser)
                    }
                }
            }
            registeredUsers.clear()
            registeredUsers.addAll(mergedUsers)
            saveRegisteredUsersToPrefs(context, mergedUsers)
        }

        runCatching {
            val state = backendState ?: return@runCatching
            val paymentRequests = state.optJSONArray("paymentRequests") ?: JSONArray()
            val savedQuotations = state.optJSONArray("savedQuotations") ?: JSONArray()
            val generatedQuotations = state.optJSONArray("quotations") ?: JSONArray()
            val customerEmailNeedle = signInEmail.trim().lowercase(Locale.US)
            val customerNameNeedle = signInName.trim().lowercase(Locale.US)
            val historyByNumber = linkedMapOf<String, QuoteHistoryEntry>()
            var latestPersistedQuote: JSONObject? = null
            var latestPersistedCreatedAt = ""

            fun belongsToCustomer(email: String, name: String): Boolean {
                val normalizedEmail = email.trim().lowercase(Locale.US)
                val normalizedName = name.trim().lowercase(Locale.US)
                return (customerEmailNeedle.isNotBlank() && normalizedEmail == customerEmailNeedle) ||
                    (customerNameNeedle.isNotBlank() && normalizedName == customerNameNeedle)
            }

            for (index in 0 until generatedQuotations.length()) {
                val record = generatedQuotations.optJSONObject(index) ?: continue
                val customer = record.optJSONObject("customer") ?: JSONObject()
                val quoteNumber = record.optString("quotation_no", "").trim()
                val customerEmail = customer.optString("email", "")
                val customerName = customer.optString("name", "")
                if (quoteNumber.isBlank() || !belongsToCustomer(customerEmail, customerName)) continue
                val items = record.optJSONArray("items_snapshot") ?: JSONArray()
                val summary = buildList {
                    for (itemIndex in 0 until items.length()) {
                        val item = items.optJSONObject(itemIndex) ?: continue
                        add("${item.optString("panelModel", "Panel")} x${item.optInt("quantity", 1)}")
                    }
                }.joinToString(", ")
                val entry = QuoteHistoryEntry(quoteNumber, customerEmail, summary, formatPeso(record.optDouble("total", 0.0).toInt()), "Quotation ready", record.optString("created_at", ""))
                historyByNumber[quoteNumber] = entry
                if (entry.createdAt >= latestPersistedCreatedAt) {
                    latestPersistedCreatedAt = entry.createdAt
                    latestPersistedQuote = record
                }
            }

            for (index in 0 until paymentRequests.length()) {
                val record = paymentRequests.optJSONObject(index) ?: continue
                val quoteNumber = record.optString("quote_number", record.optString("quoteNumber", "")).trim()
                val customerEmail = record.optString("customer_email", record.optString("customerEmail", ""))
                val customerName = record.optString("customer_name", record.optString("customerName", ""))
                if (quoteNumber.isBlank() || !belongsToCustomer(customerEmail, customerName)) continue
                val entry = QuoteHistoryEntry(
                    quoteNumber = quoteNumber,
                    customerEmail = customerEmail,
                    summary = record.optString("summary", "").ifBlank { record.optString("item_name", "") },
                    amount = record.optString("total_amount", "").ifBlank { record.optString("amount", "") },
                    status = when (record.optString("status", "").trim()) {
                        "Draft" -> "Estimate"
                        else -> record.optString("status", "Pending Review")
                    },
                    createdAt = record.optString("created_at", record.optString("createdAt", ""))
                )
                historyByNumber[quoteNumber] = entry
                if (entry.createdAt >= latestPersistedCreatedAt) {
                    latestPersistedCreatedAt = entry.createdAt
                    latestPersistedQuote = record
                }
            }

            for (index in 0 until savedQuotations.length()) {
                val record = savedQuotations.optJSONObject(index) ?: continue
                val meta = record.optJSONObject("meta") ?: JSONObject()
                val quoteNumber = record.optString("number", meta.optString("quotationNumber", "")).trim()
                val customerEmail = meta.optString("emailAddress", "")
                val customerName = meta.optString("customerName", record.optString("customerName", ""))
                if (quoteNumber.isBlank() || !belongsToCustomer(customerEmail, customerName)) continue
                val items = record.optJSONArray("items") ?: JSONArray()
                val summary = buildList {
                    for (itemIndex in 0 until items.length()) {
                        val item = items.optJSONObject(itemIndex) ?: continue
                        val name = item.optString("name", "Panel")
                        val quantity = item.optInt("qty", 1)
                        add("$name x$quantity")
                    }
                }.joinToString(", ")
                val totals = record.optJSONObject("totals") ?: JSONObject()
                val createdAt = record.optString("savedAt", meta.optString("dateIssued", ""))
                // A saved quotation is the canonical source for its items and
                // total. A matching payment request may contain an older
                // calculated amount, so merge it without losing its status.
                val existingEntry = historyByNumber[quoteNumber]
                historyByNumber[quoteNumber] = QuoteHistoryEntry(
                    quoteNumber = quoteNumber,
                    customerEmail = customerEmail.ifBlank { existingEntry?.customerEmail.orEmpty() },
                    summary = summary.ifBlank { existingEntry?.summary.orEmpty() },
                    amount = totals.optString("grandTotal", "").ifBlank { existingEntry?.amount.orEmpty() },
                    status = existingEntry?.status?.takeIf { it.isNotBlank() }
                        ?: record.optString("workflowStatus", meta.optString("workflowStatus", "Official quotation ready"))
                            .ifBlank { "Official quotation ready" },
                    createdAt = maxOf(createdAt, existingEntry?.createdAt.orEmpty())
                )
                if (createdAt >= latestPersistedCreatedAt) {
                    latestPersistedCreatedAt = createdAt
                    latestPersistedQuote = record
                }
            }

            val orders = state.optJSONArray("orders") ?: JSONArray()
            for (index in 0 until orders.length()) {
                val order = orders.optJSONObject(index) ?: continue
                val quoteNumber = order.optString("quoteNumber", order.optString("quote_number", "")).trim()
                if (quoteNumber.isBlank()) continue
                val existing = historyByNumber[quoteNumber] ?: continue
                val paymentStatus = order.optString("paymentStatus", order.optString("payment_status", "")).trim()
                val deliveryStatus = order.optString("deliveryStatus", order.optString("delivery_status", "")).trim()
                val normalizedPaymentStatus = paymentStatus.lowercase(Locale.US)
                val customerStatus = when {
                    normalizedPaymentStatus in setOf("paid", "completed", "settled") ->
                        deliveryStatus.ifBlank { "Paid" }
                    normalizedPaymentStatus == "partially paid" -> "Payment partially received"
                    else -> "Awaiting payment"
                }
                historyByNumber[quoteNumber] = existing.copy(status = customerStatus)
            }

            val history = historyByNumber.values.sortedByDescending { it.createdAt }
            recentQuoteHistory = history

            // Restore the latest saved quote into the payment screen after an
            // app restart. Previously the home screen could show an order
            // update while the menu still said “No Pay” because only payment
            // requests were loaded into the quote state.
            latestPersistedQuote?.let { record ->
                val meta = record.optJSONObject("meta") ?: JSONObject()
                val items = record.optJSONArray("items") ?: JSONArray()
                val firstItem = items.optJSONObject(0) ?: JSONObject()
                val totals = record.optJSONObject("totals") ?: JSONObject()
                val quoteNumber = record.optString("number", meta.optString("quotationNumber", "")).trim()
                historyByNumber[quoteNumber]?.status?.takeIf { it.isNotBlank() }?.let {
                    paymentStatusMessage = it
                }
                if (latestQuoteNumber.isBlank() && quoteNumber.isNotBlank()) {
                    latestQuoteNumber = quoteNumber
                    latestQuoteItemName = buildList {
                        for (itemIndex in 0 until items.length()) {
                            val item = items.optJSONObject(itemIndex) ?: continue
                            add("${item.optString("name", "Panel")} x${item.optInt("qty", 1)}")
                        }
                    }.joinToString(", ")
                    latestQuoteUnitPrice = formatPeso(firstItem.optDouble("price", 0.0).toInt())
                    latestQuoteSubtotal = formatPeso(totals.optDouble("subtotal", 0.0).toInt())
                    latestQuoteVat = formatPeso(totals.optDouble("vat", 0.0).toInt())
                    latestQuotePanels = (0 until items.length()).sumOf { items.optJSONObject(it)?.optInt("qty", 1) ?: 1 }.toString()
                    latestQuoteAmount = formatPeso(totals.optDouble("grandTotal", 0.0).toInt())
                    lastQuoteSummary = latestQuoteItemName
                }
            }
        }
    }

    LaunchedEffect(settingsAuthPageOpen) {
        if (settingsAuthPageOpen) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        // Disable the built-in two-way gesture completely. The hamburger
        // button is the only way to open the drawer on this device.
        gesturesEnabled = false,
        scrimColor = Color.Transparent,
        drawerContent = {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.32f))
                        .clickable { scope.launch { drawerState.close() } }
                )
                ModalDrawerSheet(
                    modifier = Modifier
                        .width(320.dp)
                        .fillMaxHeight()
                        .pointerInput(Unit) {
                            var horizontalDistance = 0f
                            detectDragGestures(
                                onDragStart = { horizontalDistance = 0f },
                                onDrag = { change, dragAmount ->
                                    horizontalDistance += dragAmount.x
                                    change.consume()
                                },
                                onDragEnd = {
                                    if (horizontalDistance < -60f) {
                                        scope.launch { drawerState.close() }
                                    }
                                }
                            )
                        }
                ) {
                    MenuScreen(
                    modifier = Modifier.fillMaxSize(),
                    isLoading = inventoryUiState.isLoading || isPreparingQuotation,
                    isSignedIn = isSignedIn,
                    signInName = signInName,
                    catalogCount = products.size,
                    inStockCount = products.count { it.quantity > 5 },
                    requestCount = requestCount,
                    latestQuoteSummary = lastQuoteSummary,
                    latestQuoteNumber = latestQuoteNumber,
                    latestQuoteItemName = latestQuoteItemName,
                    latestQuoteAmount = latestQuoteAmount,
                    paymentStatusMessage = paymentStatusMessage,
                    recentQuoteHistory = recentQuoteHistory,
                    customerOrderUpdates = customerOrderUpdates,
                    onOpenSettings = {
                        scope.launch {
                            drawerState.close()
                            openSettings(page = SettingsPage.Home, returnTab = currentTab)
                        }
                    },
                    onOpenPayment = {
                        scope.launch {
                            drawerState.close()
                            openPayment(returnTab = currentTab)
                        }
                    },
                    onOpenOrderUpdates = {
                        scope.launch {
                            drawerState.close()
                            showNotificationPanel = true
                        }
                    },
                    onTrackOrder = { order ->
                        scope.launch {
                            drawerState.close()
                            orderTrackingEntry = order
                        }
                    },
                    onOpenCatalog = {
                        scope.launch {
                            drawerState.close()
                            currentTab = Tab.Catalog
                        }
                    },
                    onOpenHome = {
                        scope.launch {
                            drawerState.close()
                            currentTab = Tab.Home
                        }
                    }
                    )
                }
            }
        }
    ) {
    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFF7EB)),
        topBar = {
            if (!settingsAuthPageOpen && !paymentScreenOpen && !addressScreenOpen) {
                TopAppBar(
                    title = {
                        if (paymentScreenOpen) {
                            Column {
                                Text("Aiyige", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Payment", fontSize = 12.sp, color = Color(0xFF667085))
                            }
                        } else if (addressScreenOpen) {
                            Column {
                                Text("Aiyige", fontWeight = FontWeight.ExtraBold, maxLines = 1)
                                Text("Delivery address", fontSize = 12.sp, color = Color(0xFF667085))
                            }
                        } else if (showSavedPanels) {
                            Column {
                                Text("Aiyige", fontWeight = FontWeight.ExtraBold, maxLines = 1)
                                Text("Saved panels", fontSize = 12.sp, color = Color(0xFF667085))
                            }
                        } else {
                            Column {
                                Text("Aiyige", fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    if (currentTab == Tab.Settings) "Settings" else "Customer",
                                    fontSize = 12.sp,
                                    color = Color(0xFF667085)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        if (showSavedPanels) {
                            IconButton(onClick = {
                                selectedSavedPanelIds = emptySet()
                                showSavedPanels = false
                            }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back to customize")
                            }
                        } else if (!paymentScreenOpen) {
                            IconButton(onClick = {
                                scope.launch {
                                    if (drawerState.isOpen) {
                                        drawerState.close()
                                    } else {
                                        allowDrawerOpen = true
                                        drawerState.open()
                                    }
                                }
                            }) {
                                Icon(Icons.Outlined.Menu, contentDescription = "Open menu")
                            }
                        }
                    },
                    actions = {
                        if (paymentScreenOpen) {
                            Button(
                                enabled = !isPaymentSubmitting,
                                onClick = {
                                openSettings(page = if (isSignedIn) SettingsPage.Home else SettingsPage.Login, returnTab = currentTab)
                                },
                                shape = RoundedCornerShape(999.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE8D1), contentColor = Color(0xFF8F3D08)),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                            ) { Text(if (isSignedIn) compactSignedInName.ifBlank { "Account" } else "Login") }
                        } else if (addressScreenOpen) {
                            TextButton(onClick = {
                                addressScreenOpen = false
                                pendingCheckoutLines = null
                            }) {
                                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Back")
                            }
                        } else {
                            BadgedBox(
                                badge = {
                                    if (customerOrderUpdates.isNotEmpty() || recentQuoteHistory.isNotEmpty()) {
                                        Badge { Text("!", fontSize = 9.sp) }
                                    }
                                }
                            ) {
                                IconButton(onClick = { showNotificationPanel = true }) {
                                    Icon(Icons.Outlined.NotificationsNone, contentDescription = "Open notifications")
                                }
                            }
                            BadgedBox(
                                badge = {
                                    if (cartItemCount > 0) {
                                        Badge { Text(cartItemCount.coerceAtMost(99).toString(), fontSize = 9.sp) }
                                    }
                                }
                            ) {
                                IconButton(onClick = { currentTab = Tab.Cart }) {
                                    Icon(Icons.Outlined.ShoppingCart, contentDescription = "Open cart")
                                }
                            }
                            TextButton(onClick = {
                                openSettings(page = if (isSignedIn) SettingsPage.Home else SettingsPage.Login, returnTab = currentTab)
                            }) {
                                Icon(Icons.Outlined.AccountCircle, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text(if (isSignedIn) compactSignedInName.ifBlank { "Account" } else "Login")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFFFF7EB),
                        titleContentColor = Color(0xFF121826)
                    ),
                )
            }
        },
        bottomBar = {
            if (!settingsAuthPageOpen && !paymentScreenOpen && !addressScreenOpen) {
                    NavigationBar(
                        containerColor = Color(0xFFFFFCF8),
                        tonalElevation = 8.dp
                    ) {
                        BottomItems.entries.forEach { item ->
                                    NavigationBarItem(
                                        selected = currentTab == item.tab,
                                        onClick = {
                                            if (item.tab == Tab.Settings) {
                                                openSettings(page = SettingsPage.Home, returnTab = currentTab)
                                            } else if (item.tab == Tab.Ar) {
                                                openArOrLogin(returnTab = currentTab, selectedProductForAr = selectedProductId)
                                            } else {
                                                currentTab = item.tab
                                            }
                                        },
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(item.label, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF2B2038),
                                    selectedTextColor = Color(0xFF2B2038),
                                    indicatorColor = Color(0xFFE8DDF8),
                                    unselectedIconColor = Color(0xFF615D66),
                                    unselectedTextColor = Color(0xFF615D66)
                                )
                            )
                        }
                    }
                }
            }
    ) { padding ->
        if (paymentScreenOpen) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                PaymentScreen(
                    modifier = Modifier.fillMaxSize(),
                    isLoading = inventoryUiState.isLoading || isPreparingQuotation,
                    isSubmitting = isPaymentSubmitting,
                    paymentSubmitted = paymentSubmitted,
                    isSignedIn = isSignedIn,
                    signInName = signInName,
                    signInEmail = signInEmail,
                    latestQuoteSummary = lastQuoteSummary,
                    latestQuoteItemName = latestQuoteItemName,
                    latestQuoteUnitPrice = latestQuoteUnitPrice,
                    latestQuoteSubtotal = latestQuoteSubtotal,
                    latestQuoteVat = latestQuoteVat,
                    latestQuotePanels = latestQuotePanels,
                    latestQuoteAmount = latestQuoteAmount,
                    latestQuoteNumber = latestQuoteNumber,
                    quotationItemsJson = latestQuotationItemsJson,
                    quotationIssuedAt = latestQuotationIssuedAt,
                    quotationBusinessJson = latestQuotationBusinessJson,
                    paymentStatusMessage = paymentStatusMessage,
                    customerStreetAddress = currentRegisteredUser?.streetAddress.orEmpty(),
                    customerCityProvince = currentRegisteredUser?.cityProvince.orEmpty(),
                    customerPostalCode = currentRegisteredUser?.postalCode.orEmpty(),
                    customerPhone = currentRegisteredUser?.mobileNumber.orEmpty(),
                    selectedPaymentMethod = selectedPaymentMethod,
                    selectedDeliveryMethod = selectedDeliveryMethod,
                    deliveryMethodOptions = deliveryMethodOptions,
                    paymentMethodOptions = paymentMethodOptions,
                    onPaymentMethodChange = { nextMethod -> selectedPaymentMethod = nextMethod },
                    onDeliveryMethodChange = { nextMethod -> selectedDeliveryMethod = nextMethod },
                    onPayNow = {
                        if (!isPaymentSubmitting) {
                            isPaymentSubmitting = true
                            scope.launch {
                                try {
                                    if (signInEmail.isBlank() || latestQuoteNumber.isBlank()) {
                                        paymentStatusMessage = "Sign in and save a quote first."
                                        return@launch
                                    }
                                    if (currentRegisteredUser?.streetAddress.isNullOrBlank() ||
                                        currentRegisteredUser?.cityProvince.isNullOrBlank() ||
                                        currentRegisteredUser?.postalCode.orEmpty().matches(Regex("\\d{4}")).not()
                                    ) {
                                        paymentStatusMessage = "Add a valid delivery address before submitting payment."
                                        return@launch
                                    }
                                    val result = runCatching {
                                        withTimeout(30_000) {
                                            createOrderWithQuotation(
                                                context = context,
                                                lines = activeCheckoutLines,
                                                quotationNumber = latestQuoteNumber,
                                                customerName = signInName,
                                                customerEmail = signInEmail,
                                                contactNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                                                streetAddress = currentRegisteredUser?.streetAddress.orEmpty(),
                                                cityProvince = currentRegisteredUser?.cityProvince.orEmpty(),
                                                postalCode = currentRegisteredUser?.postalCode.orEmpty(),
                                                paymentMethod = selectedPaymentMethod,
                                                courier = selectedDeliveryMethod,
                                                source = if (latestRoomCaptureUrl != null || showAutomaticEstimate) "AR" else "CATALOG",
                                                roomCaptureUrl = latestRoomCaptureUrl
                                            )
                                        }
                                    }
                                    result.fold(
                                        onSuccess = { outcome ->
                                            val quotation = outcome?.optJSONObject("quotation")
                                            val totals = quotation?.optDouble("total", parsePesoAmount(latestQuoteAmount).toDouble()) ?: parsePesoAmount(latestQuoteAmount).toDouble()
                                            val submittedQuoteNumber = latestQuoteNumber
                                            latestQuoteNumber = quotation?.optString("quotation_no", latestQuoteNumber).orEmpty()
                                            latestQuoteAmount = formatPeso(totals.toInt())
                                            recentQuoteHistory = recentQuoteHistory.map { entry ->
                                                if (entry.quoteNumber == submittedQuoteNumber) entry.copy(
                                                    quoteNumber = latestQuoteNumber,
                                                    amount = latestQuoteAmount,
                                                    status = "Awaiting payment"
                                                ) else entry
                                            }
                                            paymentSubmitted = true
                                            paymentStatusMessage = "Order submitted. Payment is pending confirmation."
                                        },
                                        onFailure = { error ->
                                            paymentSubmitted = false
                                            paymentStatusMessage = error.message
                                                ?.takeIf { it.isNotBlank() }
                                                ?: "The payment request could not be submitted. Please try again."
                                        }
                                    )
                                } finally {
                                    isPaymentSubmitting = false
                                }
                            }
                        }
                    },
                    onFinish = {
                        cart = cart.mapNotNull { line ->
                            val purchasedQuantity = activeCheckoutLines
                                .firstOrNull { it.product.id == line.product.id }
                                ?.quantity
                                ?: 0
                            val remainingQuantity = line.quantity - purchasedQuantity
                            when {
                                remainingQuantity > 0 -> line.copy(quantity = remainingQuantity, selected = false)
                                purchasedQuantity > 0 -> null
                                else -> line
                            }
                        }
                        activeCheckoutLines = emptyList()
                        paymentScreenOpen = false
                        paymentSubmitted = false
                        paymentStatusMessage = ""
                        currentTab = Tab.Home
                    },
                    onBack = { closePayment() }
                )
            }
        } else if (orderTrackingEntry != null) {
            OrderTrackingScreen(
                modifier = Modifier.padding(padding).fillMaxSize(),
                order = orderTrackingEntry!!,
                onBack = { orderTrackingEntry = null },
            )
        } else if (addressScreenOpen) {
            AddressFormScreen(
                modifier = Modifier.padding(padding).fillMaxSize(),
                fullName = signInName,
                mobileNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                streetAddress = addressStreet,
                cityProvince = addressCityProvince,
                postalCode = addressPostalCode,
                onStreetAddressChange = { addressStreet = it },
                onCityProvinceChange = { addressCityProvince = it },
                onPostalCodeChange = { addressPostalCode = it },
                onBack = {
                    addressScreenOpen = false
                    pendingCheckoutLines = null
                },
                onCancel = {
                    addressStreet = currentRegisteredUser?.streetAddress.orEmpty()
                    addressCityProvince = currentRegisteredUser?.cityProvince.orEmpty()
                    addressPostalCode = currentRegisteredUser?.postalCode.orEmpty()
                },
                onSave = {
                    val existing = currentRegisteredUser ?: RegisteredUser(
                        username = signInEmail.substringBefore("@").ifBlank { "customer" },
                        firstName = signInName,
                        lastName = "",
                        middleName = "",
                        dateOfBirth = "",
                        gender = "",
                        email = signInEmail,
                        mobileNumber = ""
                    )
                    val updated = existing.copy(
                        streetAddress = addressStreet.trim(),
                        cityProvince = addressCityProvince.trim(),
                        postalCode = addressPostalCode.trim()
                    )
                    upsertRegisteredUser(updated)
                    scope.launch { saveCustomerRegistrationToBackend(context, updated) }
                    pendingCheckoutLines?.let {
                        addressScreenOpen = false
                        startCartCheckout(it, returnTab = pendingCheckoutReturnTab)
                        pendingCheckoutLines = null
                    }
                }
            )
        } else if (currentTab == Tab.Settings) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .background(Color(0xFFFFF7EB))
                    .padding(
                        start = if (settingsAuthPageOpen) 0.dp else 16.dp,
                        top = if (settingsAuthPageOpen) 0.dp else 8.dp,
                        end = if (settingsAuthPageOpen) 0.dp else 16.dp,
                        bottom = if (settingsAuthPageOpen) 0.dp else 16.dp
                    )
                    .fillMaxSize()
            ) {
                SettingsScreen(
                    modifier = Modifier.fillMaxSize(),
                    isLoading = inventoryUiState.isLoading,
                    isSignedIn = isSignedIn,
                    signInName = signInName,
                    signInEmail = signInEmail,
                    signInUsername = currentRegisteredUser?.username.orEmpty(),
                    requestCount = requestCount,
                    latestQuoteSummary = lastQuoteSummary,
                    latestQuoteItemName = latestQuoteItemName,
                    latestQuoteUnitPrice = latestQuoteUnitPrice,
                    latestQuoteSubtotal = latestQuoteSubtotal,
                    latestQuoteVat = latestQuoteVat,
                    latestQuotePanels = latestQuotePanels,
                    latestQuoteAmount = latestQuoteAmount,
                    latestQuoteNumber = latestQuoteNumber,
                    paymentStatusMessage = paymentStatusMessage,
                    memberSince = formatMemberSince(currentRegisteredUser?.createdAt.orEmpty()),
                    backendBaseUrl = backendUrlDraft,
                    profileUsername = currentRegisteredUser?.username.orEmpty(),
                    profileFirstName = currentRegisteredUser?.firstName.orEmpty(),
                    profileMiddleName = currentRegisteredUser?.middleName.orEmpty(),
                    profileLastName = currentRegisteredUser?.lastName.orEmpty(),
                    profileDateOfBirth = currentRegisteredUser?.dateOfBirth.orEmpty(),
                    profileGender = currentRegisteredUser?.gender.orEmpty(),
                    profileMobileNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                    profileStreetAddress = currentRegisteredUser?.streetAddress.orEmpty(),
                    profileCityProvince = currentRegisteredUser?.cityProvince.orEmpty(),
                    profilePostalCode = currentRegisteredUser?.postalCode.orEmpty(),
                    recentQuoteHistory = recentQuoteHistory,
                    registeredUsers = registeredUsers,
                    authNotice = authNotice,
                    isAuthEmailRequestInFlight = isAuthEmailRequestInFlight,
                    verificationEmail = if (pendingOtpType == "signup") pendingSignupUser?.email.orEmpty() else pendingRecoveryEmail,
                    verificationType = pendingOtpType,
                    onSaveProfile = { nextUsername, nextFirstName, nextMiddleName, nextLastName, nextDateOfBirth, nextMobile, nextStreetAddress, nextCityProvince, nextPostalCode ->
                        val email = signInEmail.trim()
                        if (email.isNotBlank()) {
                            val existingUser = registeredUsers.firstOrNull { it.email.equals(email, ignoreCase = true) }
                            val sourceUser = existingUser ?: RegisteredUser(
                                username = signInName.substringBefore(" ").lowercase(Locale.getDefault()).ifBlank { email.substringBefore("@") },
                                firstName = nextFirstName.trim().ifBlank { displayNameFromEmail(email) },
                                lastName = "",
                                middleName = "",
                                dateOfBirth = nextDateOfBirth.trim(),
                                gender = currentRegisteredUser?.gender.orEmpty(),
                                email = email,
                                mobileNumber = nextMobile.trim(),
                                password = currentRegisteredUser?.password.orEmpty(),
                                createdAt = formatCaptureTimestamp()
                            )
                            val updatedUser = sourceUser.copy(
                                username = nextUsername.trim().ifBlank { sourceUser.username },
                                firstName = nextFirstName.trim().ifBlank { sourceUser.firstName },
                                middleName = nextMiddleName.trim(),
                                lastName = nextLastName.trim().ifBlank { sourceUser.lastName },
                                dateOfBirth = nextDateOfBirth.trim().ifBlank { sourceUser.dateOfBirth },
                                mobileNumber = nextMobile.ifBlank { sourceUser.mobileNumber },
                                streetAddress = nextStreetAddress.trim(),
                                cityProvince = nextCityProvince.trim(),
                                postalCode = nextPostalCode.trim(),
                                createdAt = sourceUser.createdAt.ifBlank { formatCaptureTimestamp() }
                            )

                            upsertRegisteredUser(updatedUser)
                            scope.launch {
                                saveCustomerRegistrationToBackend(context, updatedUser)
                            }
                            loadSupabaseSession(context)?.let { session ->
                                saveSupabaseSession(
                                    context,
                                    session.copy(
                                        email = email,
                                        displayName = updatedUser.displayName.ifBlank { displayNameFromEmail(email) }
                                    )
                                )
                            }
                            signInName = updatedUser.displayName.ifBlank { displayNameFromEmail(email) }
                        }
                    },
                    onOpenAddress = {
                        addressStreet = currentRegisteredUser?.streetAddress.orEmpty()
                        addressCityProvince = currentRegisteredUser?.cityProvince.orEmpty()
                        addressPostalCode = currentRegisteredUser?.postalCode.orEmpty()
                        addressScreenOpen = true
                    },
                    onBackendBaseUrlChange = { backendUrlDraft = it },
                    onSaveBackendBaseUrl = { nextUrl -> 
                        saveConfiguredBackendBaseUrl(context, nextUrl)
                        backendUrlDraft = loadConfiguredBackendBaseUrl(context)
                        syncTick += 1
                    },
                    onOpenCatalog = { currentTab = Tab.Catalog },
                    onChangePassword = { nextPassword ->
                        val email = signInEmail.trim()
                        val passwordOwnerEmail = email.ifBlank { pendingRecoveryEmail }
                        if (passwordOwnerEmail.isNotBlank()) {
                            val existingUser = registeredUsers.firstOrNull { it.email.equals(passwordOwnerEmail, ignoreCase = true) }
                            val baseUser = existingUser ?: RegisteredUser(
                                username = signInName.substringBefore(" ").lowercase(Locale.getDefault()).ifBlank { passwordOwnerEmail.substringBefore("@") },
                                firstName = signInName.ifBlank { displayNameFromEmail(passwordOwnerEmail) },
                                lastName = "",
                                middleName = "",
                                dateOfBirth = currentRegisteredUser?.dateOfBirth.orEmpty(),
                                gender = currentRegisteredUser?.gender.orEmpty(),
                                email = passwordOwnerEmail,
                                mobileNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                                password = currentRegisteredUser?.password.orEmpty(),
                                createdAt = currentRegisteredUser?.createdAt.orEmpty().ifBlank { formatCaptureTimestamp() }
                            )
                            val updatedUser = baseUser.copy(password = "")
                            scope.launch {
                                runCatching {
                                    val session = loadSupabaseSession(context)
                                        ?: throw IllegalStateException("Sign in again to change your password.")
                                    supabaseRequest(
                                        path = "auth/v1/user",
                                        method = "PUT",
                                        body = JSONObject().apply {
                                            put("password", nextPassword)
                                        },
                                        accessToken = session.accessToken
                                    )
                                    upsertRegisteredUser(updatedUser)
                                }.onSuccess {
                                    if (settingsPage == SettingsPage.RecoverPassword) {
                                        pendingRecoveryEmail = ""
                                        settingsPage = SettingsPage.Login
                                        settingsLastAuthPage = SettingsPage.Login
                                        authNotice = "Password updated. You can now sign in with your new password."
                                    } else {
                                        authNotice = "Password updated."
                                    }
                                }.onFailure { error ->
                                    Toast.makeText(
                                        context,
                                        error.message?.takeIf { it.isNotBlank() }
                                            ?: "Password update could not be synced right now.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    },
                    onRegisterUser = registerUser@{ user ->
                        if (isAuthEmailRequestInFlight) return@registerUser
                        currentTab = Tab.Settings
                        settingsPage = SettingsPage.Register
                        settingsLastAuthPage = SettingsPage.Register
                        authNotice = "Sending a confirmation link to ${user.email}..."
                        isAuthEmailRequestInFlight = true
                        scope.launch {
                            try {
                                val result = requestSignupConfirmationEmail(user)
                                result.onSuccess { session ->
                                    if (session != null) {
                                        pendingSignupUser = null
                                        val confirmedUser = user.copy(password = "")
                                        upsertRegisteredUser(confirmedUser)
                                        // Keep the admin dashboard's shared customer
                                        // records in sync with accounts created in the
                                        // mobile app. Previously signup only updated
                                        // this device's local preferences.
                                        scope.launch {
                                            saveCustomerRegistrationToBackend(context, confirmedUser)
                                        }
                                        applyAuthenticatedSession(
                                            displayName = session.displayName,
                                            email = session.email,
                                            session = session
                                        )
                                        currentTab = Tab.Settings
                                        settingsPage = SettingsPage.Home
                                        settingsLastAuthPage = SettingsPage.Home
                                        authNotice = "Account created and confirmed. You are now signed in."
                                    } else {
                                        isSignedIn = false
                                        signInName = "Guest User"
                                        signInEmail = ""
                                        pendingSignupUser = user
                                        currentTab = Tab.Settings
                                        pendingOtpType = "signup"
                                        settingsPage = SettingsPage.VerifyEmail
                                        settingsLastAuthPage = SettingsPage.VerifyEmail
                                        authNotice = "Enter the verification code sent to ${user.email}."
                                    }
                                }.onFailure { error ->
                                    pendingSignupUser = null
                                    authNotice = error.message?.takeIf { it.isNotBlank() } ?: "Could not send the verification email."
                                }
                            } finally {
                                isAuthEmailRequestInFlight = false
                            }
                        }
                    },
                    onRequestPasswordRecovery = recoverPassword@{ email ->
                        if (isAuthEmailRequestInFlight) return@recoverPassword
                        scope.launch {
                            val trimmedEmail = email.trim()
                            if (trimmedEmail.isBlank()) {
                                authNotice = "Please enter your email address."
                                return@launch
                            }
                            currentTab = Tab.Settings
                            settingsPage = SettingsPage.ForgotPassword
                            settingsLastAuthPage = SettingsPage.ForgotPassword
                            pendingRecoveryEmail = trimmedEmail
                            authNotice = "Sending a password reset link to $trimmedEmail..."
                            isAuthEmailRequestInFlight = true
                            try {
                                val result = requestPasswordRecoveryEmail(trimmedEmail)
                                result.onSuccess {
                                    currentTab = Tab.Settings
                                    pendingOtpType = "recovery"
                                    settingsPage = SettingsPage.VerifyEmail
                                    settingsLastAuthPage = SettingsPage.VerifyEmail
                                    authNotice = "Enter the reset code sent to $trimmedEmail."
                                }.onFailure { error ->
                                    authNotice = error.message?.takeIf { it.isNotBlank() } ?: "Could not send the recovery email."
                                }
                            } finally {
                                isAuthEmailRequestInFlight = false
                            }
                        }
                    },
                    onVerifyEmailOtp = { code ->
                        val email = if (pendingOtpType == "signup") pendingSignupUser?.email.orEmpty() else pendingRecoveryEmail
                        if (email.isBlank() || pendingOtpType !in setOf("signup", "recovery")) {
                            authNotice = "Request a new verification code and try again."
                        } else {
                            isAuthEmailRequestInFlight = true
                            scope.launch {
                                try {
                                    verifyEmailOtp(email, code, pendingOtpType, pendingSignupUser?.displayName.orEmpty())
                                        .onSuccess { session ->
                                            saveSupabaseSession(context, session)
                                            if (pendingOtpType == "signup") {
                                                val user = pendingSignupUser
                                                if (user == null) {
                                                    authNotice = "Your code was verified. Please sign in to continue."
                                                    settingsPage = SettingsPage.Login
                                                    settingsLastAuthPage = SettingsPage.Login
                                                } else {
                                                    val confirmedUser = user.copy(password = "")
                                                    upsertRegisteredUser(confirmedUser)
                                                    // Persist the confirmed mobile account
                                                    // to the same backend read by Admin.
                                                    scope.launch {
                                                        saveCustomerRegistrationToBackend(context, confirmedUser)
                                                    }
                                                    applyAuthenticatedSession(user.displayName, session.email, session)
                                                    pendingSignupUser = null
                                                    pendingOtpType = ""
                                                    settingsPage = SettingsPage.Home
                                                    settingsLastAuthPage = SettingsPage.Home
                                                    authNotice = "Your email is verified. Welcome to Aiyige."
                                                }
                                            } else {
                                                pendingOtpType = ""
                                                pendingRecoveryEmail = session.email
                                                settingsPage = SettingsPage.RecoverPassword
                                                settingsLastAuthPage = SettingsPage.RecoverPassword
                                                authNotice = "Code verified. Choose a new password."
                                            }
                                        }.onFailure { error ->
                                            authNotice = error.message?.takeIf { it.isNotBlank() } ?: "That code could not be verified."
                                        }
                                } finally {
                                    isAuthEmailRequestInFlight = false
                                }
                            }
                        }
                    },
                    page = settingsPage,
                    onPageChange = { next ->
                        settingsPage = next
                        if (next != SettingsPage.Home) {
                            settingsLastAuthPage = next
                        }
                    },
                    onSignIn = { email, password ->
                        if (!isAuthEmailRequestInFlight) {
                            isAuthEmailRequestInFlight = true
                            authNotice = ""
                            scope.launch {
                                try {
                                signInWithPassword(email, password).onSuccess { session ->
                                    val existingUser = registeredUsers.firstOrNull {
                                        it.email.equals(session.email, ignoreCase = true)
                                    }
                                    if (existingUser == null) {
                                        upsertRegisteredUser(
                                            RegisteredUser(
                                                username = session.email.substringBefore("@"),
                                                firstName = session.displayName,
                                                lastName = "",
                                                middleName = "",
                                                dateOfBirth = "",
                                                gender = "",
                                                email = session.email,
                                                mobileNumber = "",
                                                password = "",
                                                createdAt = formatCaptureTimestamp()
                                            )
                                        )
                                    }
                                    applyAuthenticatedSession(
                                        displayName = session.displayName,
                                        email = session.email,
                                        session = session
                                    )
                                    val shouldOpenArAfterLogin = pendingPostLoginTab == Tab.Ar.name
                                    pendingPostLoginTab = ""
                                    if (shouldOpenArAfterLogin) {
                                        currentTab = Tab.Ar
                                    } else {
                                        currentTab = Tab.Settings
                                        settingsPage = SettingsPage.Home
                                        settingsLastAuthPage = SettingsPage.Home
                                    }
                                }.onFailure {
                                    authNotice = "Could not sign in. Check your email and password, then try again."
                                }
                                } finally {
                                    isAuthEmailRequestInFlight = false
                                }
                            }
                        }
                    },
                    onUseDemoAccount = {
                        val demoEmail = "demo@aiyige.local"
                        val demoUser = RegisteredUser(
                            username = "demo-customer",
                            firstName = "Demo",
                            lastName = "Customer",
                            middleName = "",
                            dateOfBirth = "01/01/1990",
                            gender = "Rather not to say",
                            email = demoEmail,
                            mobileNumber = "09170000000",
                            streetAddress = "Demo Test Address",
                            cityProvince = "Quezon City, Metro Manila",
                            postalCode = "1100",
                            password = "",
                            createdAt = formatCaptureTimestamp()
                        )
                        registeredUsers.removeAll { it.email.equals(demoEmail, ignoreCase = true) }
                        registeredUsers.add(demoUser)
                        isDemoAccount = true
                        isSignedIn = true
                        signInName = demoUser.displayName
                        signInEmail = demoEmail
                        requestCount = 1
                        latestQuoteNumber = "QT-2026-001"
                        latestQuoteItemName = "F-44 x1"
                        latestQuoteUnitPrice = "₱70"
                        latestQuoteSubtotal = "₱70"
                        latestQuoteVat = "₱8"
                        latestQuotePanels = "1"
                        latestQuoteAmount = "₱78"
                        lastQuoteSummary = "F-44 x1"
                        selectedPaymentMethod = "Cash"
                        paymentSubmitted = true
                        paymentStatusMessage = "Paid"
                        recentQuoteHistory = listOf(
                            QuoteHistoryEntry(
                                quoteNumber = "QT-2026-001",
                                customerEmail = demoEmail,
                                summary = "F-44 x1",
                                amount = "₱78",
                                status = "Paid",
                                createdAt = formatCaptureTimestamp()
                            )
                        )
                        authNotice = "Demo customer mode is active. Quotes and orders use the demo account."
                        pendingPostLoginTab = ""
                        currentTab = Tab.Settings
                        settingsPage = SettingsPage.Home
                        settingsLastAuthPage = SettingsPage.Home
                    },
                    onSignOut = {
                        clearSupabaseSession(context)
                        clearLocalSession(context)
                        registeredUsers.removeAll { it.email.equals("demo@aiyige.local", ignoreCase = true) }
                        isSignedIn = false
                        isDemoAccount = false
                        signInName = "Guest User"
                        signInEmail = ""
                        pendingPostLoginTab = ""
                        settingsReturnTab = Tab.Home
                        settingsPage = SettingsPage.Login
                        settingsLastAuthPage = SettingsPage.Login
                        currentTab = Tab.Settings
                    }
                )
            }
        } else if (currentTab == Tab.Ar) {
            if (!isSignedIn) {
                LaunchedEffect(Unit) {
                    openLoginForAr(returnTab = Tab.Ar, selectedProductForAr = selectedProductId)
                }
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                ) {
                    RefinedArScreen(
                        modifier = Modifier.fillMaxSize(),
                        selectedProduct = selectedProduct,
                        panelOptions = products.filter { it.modelUrl != null },
                        isSignedIn = isSignedIn,
                        finish = customizationFinish,
                        tintHex = customizationColourHex(customizationColour),
                        textureAsset = customizationTextureAsset(customizationColour),
                        pattern = customizationPattern,
                        size = customizationSize,
                        quantity = customizationQuantity,
                        notes = customizationNotes,
                        customerName = customerReferenceName,
                        streetAddress = currentRegisteredUser?.streetAddress.orEmpty(),
                        cityProvince = currentRegisteredUser?.cityProvince.orEmpty(),
                        postalCode = currentRegisteredUser?.postalCode.orEmpty(),
                        contactNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                        emailAddress = currentRegisteredUser?.email.orEmpty(),
                        latestRoomCaptureUrl = latestRoomCaptureUrl,
                        latestRoomCaptureLabel = latestRoomCaptureLabel,
                        onOpenCatalog = { currentTab = Tab.Catalog },
                        onRequireSignIn = {
                            openLoginForAr(returnTab = Tab.Ar, selectedProductForAr = selectedProductId)
                        },
                        onRoomCaptureSaved = { imageUrl, label ->
                            latestRoomCaptureUrl = imageUrl
                            latestRoomCaptureLabel = label
                        },
                        onQuantityChange = { nextQuantity ->
                            val safeQuantity = maxOf(1, nextQuantity)
                            selectedQuantity = safeQuantity
                            customizationQuantity = safeQuantity.toString()
                        },
                        onPanelSelected = { selectedProductId = it },
                        onBackToCatalog = { currentTab = Tab.Catalog },
                        onSaveToQuote = {
                            if (selectedProduct != null) {
                                val quantityValue = customizationQuantity.ifBlank { "1" }
                                val numericQuantity = quantityValue.toIntOrNull()?.coerceAtLeast(1) ?: 1
                                val addOnTotal = selectedAddOns.fold(0) { total, addOn ->
                                    total + when (addOn) {
                                        "Fixings Kit" -> 18
                                        "Pro Adhesive" -> 12
                                        "Edge Trim" -> 24
                                        else -> 0
                                    }
                                }
                                val sizeAdjustment = if (customizationSize.contains("300")) 12 else 0
                                val subtotal = (selectedProduct.price + addOnTotal + sizeAdjustment) * numericQuantity
                                val vat = 0
                                val grandTotal = subtotal
                                quoteQty = quantityValue
                                quoteNotes = buildList {
                                    add("Finish: $customizationFinish")
                                    add("Colour: $customizationColour")
                                    add("View: ${if (customizationIs3d) "3D" else "2D"}")
                                    add("Size: ${customizationSize.ifBlank { selectedProduct.size }}")
                                    if (selectedAddOns.isNotEmpty()) add("Add-ons: ${selectedAddOns.joinToString(", ")}")
                                    if (latestRoomCaptureUrl != null) add("Room capture: attached")
                                    if (customizationNotes.isNotBlank()) add(customizationNotes)
                                }.joinToString("\n")
                                latestQuoteNumber = buildQuoteNumber((requestCount + 1).coerceAtLeast(1))
                                latestQuoteItemName = selectedProduct.name
                                latestQuoteUnitPrice = "₱${selectedProduct.price}"
                            latestQuoteSubtotal = "₱${subtotal}"
                            latestQuoteVat = "₱${vat}"
                                latestQuotePanels = numericQuantity.toString()
                            latestQuoteAmount = "₱${grandTotal}"
                                lastQuoteSummary = "${selectedProduct.name} x$numericQuantity"
                                paymentStatusMessage = ""
                                requestCount += 1
                                recentQuoteHistory = listOf(
                                    QuoteHistoryEntry(
                                        quoteNumber = latestQuoteNumber,
                                        customerEmail = signInEmail.ifBlank { customerReferenceName },
                                        summary = lastQuoteSummary.orEmpty(),
                                        amount = latestQuoteAmount,
                                        status = "Draft",
                                        createdAt = formatCaptureTimestamp()
                                    )
                                ) + recentQuoteHistory
                                if (isSignedIn && signInEmail.isNotBlank()) {
                                    scope.launch {
                                        val result = savePaymentRequestToSupabase(
                                            context = context,
                                            payload = PaymentRecordPayload(
                                                quoteNumber = latestQuoteNumber,
                                                customerName = customerReferenceName,
                                                customerEmail = signInEmail,
                                                contactNumber = currentRegisteredUser?.mobileNumber.orEmpty(),
                                                streetAddress = currentRegisteredUser?.streetAddress.orEmpty(),
                                                cityProvince = currentRegisteredUser?.cityProvince.orEmpty(),
                                                postalCode = currentRegisteredUser?.postalCode.orEmpty(),
                                                paymentMethod = selectedPaymentMethod,
                                                deliveryMethod = selectedDeliveryMethod,
                                                itemName = selectedProduct.name,
                                                panels = numericQuantity,
                                                unitPrice = selectedProduct.price,
                                                subtotal = subtotal,
                                                vat = vat,
                                                totalAmount = grandTotal,
                                                summary = lastQuoteSummary.orEmpty(),
                                                roomCaptureUrl = latestRoomCaptureUrl,
                                                notes = quoteNotes,
                                                status = "Draft"
                                            )
                                        )
                                        paymentStatusMessage = result.fold(
                                            onSuccess = { it.message },
                                            onFailure = { it.message ?: "Saved to this device." }
                                        )
                                    }
                                } else {
                                    paymentStatusMessage = "Quote draft saved on this device. Sign in again to sync it online."
                                }
                                currentTab = Tab.Ar
                                showAutomaticEstimate = true
                                Toast.makeText(context, "Quotation request sent. The seller will prepare the quotation.", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = if (currentTab == Tab.Customization) 0.dp else 16.dp)
                    .then(
                        if (currentTab == Tab.Customization || currentTab == Tab.Catalog || currentTab == Tab.Cart) {
                            Modifier
                        } else {
                            Modifier.verticalScroll(rememberScrollState())
                        }
                    ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Compose only the currently selected tab. Keeping the outgoing tab
                // alive during AnimatedContent caused Customize and Home to overlap
                // briefly, which could restore the same saveable/scroll state twice
                // and crash when navigating directly from Customize to Home.
                if (showSavedPanels) {
                    SavedPanelsScreen(
                        savedPanels = savedCustomizations,
                        products = products,
                        selectedIds = selectedSavedPanelIds,
                        onToggleSelection = { id ->
                            selectedSavedPanelIds = if (id in selectedSavedPanelIds) {
                                selectedSavedPanelIds - id
                            } else {
                                selectedSavedPanelIds + id
                            }
                        },
                        onSelectAll = {
                            selectedSavedPanelIds = if (selectedSavedPanelIds.size == savedCustomizations.size) {
                                emptySet()
                            } else {
                                savedCustomizations.map { it.productId }.toSet()
                            }
                        },
                        onDelete = {
                            savedCustomizations = savedCustomizations.filterNot { it.productId in selectedSavedPanelIds }
                            selectedSavedPanelIds = emptySet()
                        },
                        onUpdate = {
                            savedCustomizations.firstOrNull { it.productId in selectedSavedPanelIds }?.let { saved ->
                                selectedProductId = saved.productId
                                customizationSize = saved.size
                                customizationColour = saved.colour
                                customizationFinish = saved.finish
                                customizationQuantity = saved.quantity
                                selectedSavedPanelIds = emptySet()
                                showSavedPanels = false
                                currentTab = Tab.Customization
                            }
                        }
                    )
                } else when (currentTab) {
                    Tab.Home -> HomeOverviewScreen(
                        products = products,
                        isInventoryLoading = inventoryUiState.isLoading,
                        isSignedIn = isSignedIn,
                        signInName = signInName,
                        catalogCount = products.size,
                        inStockCount = products.count { it.quantity > 5 },
                        requestCount = requestCount,
                        lastQuoteSummary = lastQuoteSummary,
                        recentlyViewedIds = recentlyViewedIds,
                        onOpenCatalog = { currentTab = Tab.Catalog },
                        onOpenAr = { openArOrLogin(returnTab = Tab.Home, selectedProductForAr = selectedProductId) },
                        onAddToCart = { product -> addToCart(product, 1) },
                        onProceedToAr = { productId ->
                            selectedProductId = productId
                            openArOrLogin(returnTab = Tab.Home, selectedProductForAr = productId)
                        },
                        onOpenQuote = {
                            openSettings(page = if (isSignedIn) SettingsPage.Home else SettingsPage.Login, returnTab = Tab.Home)
                        },
                        onPreviewProduct = { productId ->
                            recentlyViewedIds = (listOf(productId) + recentlyViewedIds.filterNot { it == productId }).take(8)
                            selectedProductId = productId
                        }
                    )

                    Tab.Catalog -> CatalogScreen(
                        isInventoryLoading = inventoryUiState.isLoading,
                        categories = categories,
                        search = search,
                        onSearchChange = { search = it },
                        category = category,
                        onCategoryChange = { category = it },
                        products = filteredProducts,
                        selectedProduct = selectedProduct,
                        onSelect = {
                            selectedProductId = it
                            selectedQuantity = 1
                            customizationQuantity = "1"
                        },
                        onProceedToAr = {
                            openArOrLogin(returnTab = Tab.Catalog, selectedProductForAr = selectedProductId)
                        },
                        onAddToCart = { product, quantity -> addToCart(product, quantity) }
                    )
                    Tab.Cart -> CartScreen(
                        cart = cart,
                        onQuantityChange = { productId, quantity -> updateCartQuantity(productId, quantity) },
                        onToggleSelection = { productId -> toggleCartSelection(productId) },
                        onSelectAll = { selected -> setAllCartSelection(selected) },
                        onContinueShopping = { currentTab = Tab.Catalog },
                        onCheckout = { lines ->
                            pendingCheckoutLines = lines
                            pendingCheckoutReturnTab = Tab.Cart
                            if (!isSignedIn) {
                                openSettings(page = SettingsPage.Login, returnTab = Tab.Cart)
                            } else if (currentRegisteredUser?.streetAddress.isNullOrBlank() || currentRegisteredUser?.cityProvince.isNullOrBlank() || currentRegisteredUser?.postalCode.isNullOrBlank()) {
                                addressStreet = currentRegisteredUser?.streetAddress.orEmpty()
                                addressCityProvince = currentRegisteredUser?.cityProvince.orEmpty()
                                addressPostalCode = currentRegisteredUser?.postalCode.orEmpty()
                                addressScreenOpen = true
                            } else {
                                pendingCheckoutLines = null
                                startCartCheckout(lines, returnTab = Tab.Cart)
                            }
                        }
                    )
                    Tab.Customization -> CustomizationScreen(
                        selectedProduct = selectedProduct,
                        inventoryProducts = products,
                        inventoryCategories = categories.filter { it != "All" },
                        inventoryCategory = category,
                        onInventoryCategoryChange = { category = it },
                        onSelectInventoryProduct = { productId ->
                            selectedProductId = productId
                            selectedQuantity = 1
                            customizationQuantity = "1"
                        },
                        finish = customizationFinish,
                        size = customizationSize,
                        colour = customizationColour,
                        pattern = customizationPattern,
                        is3d = customizationIs3d,
                        quantity = customizationQuantity,
                        notes = customizationNotes,
                        addOns = customizationAddOnsCsv.split("|").filter { it.isNotBlank() },
                        onFinishChange = { customizationFinish = it },
                        onSizeChange = { customizationSize = it },
                        onColourChange = { customizationColour = it },
                        on3dChange = { customizationIs3d = it },
                        onQuantityChange = { next ->
                            val safeQuantity = next.toIntOrNull()?.coerceAtLeast(1) ?: 1
                            customizationQuantity = safeQuantity.toString()
                            selectedQuantity = safeQuantity
                        },
                        onNotesChange = { customizationNotes = it },
                        onAddOnToggle = { addOn ->
                            val current = customizationAddOnsCsv.split("|").filter { it.isNotBlank() }.toMutableSet()
                            if (!current.add(addOn)) current.remove(addOn)
                            customizationAddOnsCsv = current.joinToString("|")
                        },
                        onGoToCatalog = { currentTab = Tab.Catalog },
                        onPreviewInAr = { currentTab = Tab.Ar },
                        onOpenSavedPanels = { showSavedPanels = true },
                        onSaveCustomization = {
                            val addOnPrices = mapOf("Fixings Kit" to 18, "Pro Adhesive" to 12, "Edge Trim" to 24)
                            val fixingPrice = if (customizationSize.contains("300")) 12 else 0
                            val total = selectedProduct?.price?.plus(fixingPrice)?.plus(
                                customizationAddOnValues(addOnPrices, customizationAddOnsCsv)
                            ) ?: 0
                            selectedProduct?.let { product ->
                                savedCustomizations = (
                                    savedCustomizations.filterNot { it.productId == product.id } +
                                        SavedCustomization(
                                            productId = product.id,
                                            productName = product.name,
                                            size = customizationSize,
                                            colour = customizationColour,
                                            finish = customizationFinish,
                                            quantity = customizationQuantity,
                                            total = total
                                        )
                                )
                            }
                            Toast.makeText(context, "Customized panel saved.", Toast.LENGTH_SHORT).show()
                        },
                        onCreateQuotation = { createQuotationFromCustomization(Tab.Customization) }
                    )

                    Tab.Ar -> Unit

                    Tab.Settings -> Unit
                }
                }
            }
        }
        if (drawerState.isOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.32f))
                    .clickable {
                        scope.launch { drawerState.close() }
                    }
            )
        }
    }
    }

    val notificationItems = remember(customerOrderUpdates, recentQuoteHistory) {
        val ordersByQuote = customerOrderUpdates
            .filter { it.invoice.isNotBlank() }
            .associateBy { it.invoice }
        buildList {
            customerOrderUpdates.distinctBy { it.invoice }.take(3).forEach { order ->
                val quote = recentQuoteHistory.firstOrNull { it.quoteNumber == order.invoice }
                val message = if (quote != null) {
                    "Quotation ${quote.quoteNumber} is ready to view"
                } else {
                    order.deliveryStatus.ifBlank { "Your order is being processed" }
                }
                add(
                    CustomerNotification(
                        reference = order.invoice,
                        title = if (order.deliveryStatus.equals("Delivered", true)) "Order delivered" else "Order update",
                        message = message,
                        itemSummary = order.products.ifBlank { quote?.summary ?: "Panel order" },
                        amount = formatPeso(order.amount),
                        status = order.deliveryStatus.ifBlank { "Processing" },
                        updatedAt = order.updatedAt,
                        quote = quote,
                        isOrder = true
                    )
                )
            }
            recentQuoteHistory
                .filter { it.quoteNumber !in ordersByQuote }
                .distinctBy { it.quoteNumber }
                .take(3)
                .forEach { quote ->
                    val (title, message) = when {
                        quote.status.equals("Draft", ignoreCase = true) -> "Quotation request" to "Waiting for the seller's official quotation"
                        quote.status.equals("Paid", ignoreCase = true) -> "Payment received" to "Your payment has been recorded"
                        quote.status.contains("Ready", ignoreCase = true) -> "Quotation ready" to "Your automatic quotation is ready to view"
                        else -> "Quotation update" to quote.status
                    }
                    add(
                        CustomerNotification(
                            reference = quote.quoteNumber,
                            title = title,
                            message = message,
                            itemSummary = quote.summary,
                            amount = quote.amount.trim().let { value ->
                                if (value.startsWith("₱")) value else "₱$value"
                            },
                            status = quote.status,
                            updatedAt = quote.createdAt,
                            quote = quote
                        )
                    )
                }
        }
    }

    if (showNotificationPanel) {
        Dialog(
            onDismissRequest = { showNotificationPanel = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF6)),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Notifications", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
                            Text("Your orders and quotation updates", fontSize = 12.sp, color = Color(0xFF667085))
                        }
                        IconButton(onClick = { showNotificationPanel = false }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Close notifications", tint = Color(0xFF667085))
                        }
                    }
                    if (notificationItems.isEmpty()) {
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                            Text("You do not have any updates yet.", modifier = Modifier.padding(18.dp), color = Color(0xFF667085))
                        }
                    } else {
                        Column(
                            modifier = Modifier.heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            notificationItems.forEach { item ->
                                val statusColor = when {
                                    item.status.contains("Delivered", true) || item.status.contains("Paid", true) -> Color(0xFF18794E)
                                    item.status.contains("Official", true) -> Color(0xFF2563EB)
                                    item.status.contains("Shipped", true) || item.status.contains("Preparing", true) -> Color(0xFF8A4A0A)
                                    else -> Color(0xFFF06416)
                                }
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(18.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEDE3D8))
                                ) {
                                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Box(Modifier.size(34.dp).background(Color(0xFFFFE8D1), CircleShape), contentAlignment = Alignment.Center) {
                                                Icon(if (item.isOrder) Icons.Outlined.ShoppingBag else Icons.Outlined.ReceiptLong, contentDescription = null, tint = Color(0xFFF06416), modifier = Modifier.size(18.dp))
                                            }
                                            Text(item.title, modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = Color(0xFF121826), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        Text(
                                            item.status,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = statusColor,
                                            fontSize = 10.sp,
                                            lineHeight = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(item.message, color = Color(0xFF344054), fontSize = 13.sp, lineHeight = 18.sp)
                                        Text(item.itemSummary, color = Color(0xFF667085), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(item.reference, color = Color(0xFF98A2B3), fontSize = 11.sp)
                                            Text(item.amount, color = Color(0xFFF06416), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Text(item.updatedAt.ifBlank { "Just now" }, color = Color(0xFF98A2B3), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAutomaticEstimate) {
        AlertDialog(
            onDismissRequest = { showAutomaticEstimate = false },
            confirmButton = {
                TextButton(onClick = {
                    showAutomaticEstimate = false
                    selectedProduct?.let { startCartCheckout(listOf(CartLine(it, customizationQuantity.toIntOrNull()?.coerceAtLeast(1) ?: 1))) }
                }) { Text("Yes, continue to buy") }
            },
            dismissButton = {
                TextButton(onClick = { showAutomaticEstimate = false }) { Text("No, adjust placement") }
            },
            title = { Text("Automatic estimate") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("This is an automatic estimate based on your customization.")
                    Text("Do you want to continue and buy these panels?", color = Color(0xFF667085), fontSize = 12.sp)
                    Divider()
                    Text("Request ${latestQuoteNumber.ifBlank { "Pending" }}", fontWeight = FontWeight.Bold)
                    Text(latestQuoteItemName.ifBlank { "Customized panel" })
                    Text("Subtotal: ${latestQuoteSubtotal.ifBlank { "₱0" }}")
                    Text("VAT: ${latestQuoteVat.ifBlank { "₱0" }}")
                    Text("Estimated total: ${latestQuoteAmount.ifBlank { "₱0" }}", fontWeight = FontWeight.Bold, color = Color(0xFFF06416))
                }
            }
        )
    }

    if (false && showSavedPanels) {
        Dialog(onDismissRequest = { showSavedPanels = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBF7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xFFFFE7D4), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.PendingActions, contentDescription = null, tint = Color(0xFFF06416))
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Saved panels", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
                            Text(
                                if (savedCustomizations.isEmpty()) "Your personal panel library" else "${savedCustomizations.size} customized panel${if (savedCustomizations.size == 1) "" else "s"}",
                                fontSize = 12.sp,
                                color = Color(0xFF667085)
                            )
                        }
                    }

                    if (savedCustomizations.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF5EFE8), RoundedCornerShape(18.dp))
                                .padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("No saved panels yet", fontWeight = FontWeight.Bold, color = Color(0xFF121826))
                            Text("Customize a panel and tap Save Panel to keep it here.", fontSize = 12.sp, color = Color(0xFF667085), textAlign = TextAlign.Center)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            savedCustomizations.forEach { saved ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFEDE4DB), RoundedCornerShape(16.dp))
                                        .clickable {
                                            selectedProductId = saved.productId
                                            customizationSize = saved.size
                                            customizationColour = saved.colour
                                            customizationFinish = saved.finish
                                            customizationQuantity = saved.quantity
                                            showSavedPanels = false
                                            currentTab = Tab.Customization
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .background(
                                                customizationColourHex(saved.colour).let { hex ->
                                                    Color(android.graphics.Color.parseColor("#$hex"))
                                                },
                                                RoundedCornerShape(12.dp)
                                            )
                                            .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    )
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(saved.productName, fontWeight = FontWeight.Bold, color = Color(0xFF121826), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${saved.colour} · ${saved.size}", color = Color(0xFF667085), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text("${saved.finish} finish · Qty ${saved.quantity}", color = Color(0xFF98A2B3), fontSize = 10.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(formatPeso(saved.total), fontWeight = FontWeight.ExtraBold, color = Color(0xFFF06416), fontSize = 13.sp)
                                        Text("Open", color = Color(0xFF667085), fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }

                    TextButton(
                        onClick = { showSavedPanels = false },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF06416))
                    ) { Text("Close", fontWeight = FontWeight.Bold) }
                }
            }
        }
        val actionLoadingMessage = when {
            isPaymentSubmitting -> "Submitting your order..."
            isPreparingQuotation -> "Preparing your quotation..."
            isAuthEmailRequestInFlight -> "Sending account confirmation..."
            else -> null
        }
        if (actionLoadingMessage != null) {
            CustomerActionLoadingOverlay(message = actionLoadingMessage)
        }
    }
}

@Composable
private fun SavedPanelsScreen(
    savedPanels: List<SavedCustomization>,
    products: List<Product>,
    selectedIds: Set<Int>,
    onToggleSelection: (Int) -> Unit,
    onSelectAll: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: () -> Unit
) {
    val allSelected = savedPanels.isNotEmpty() && selectedIds.size == savedPanels.size
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF7))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Saved panels", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
                Text("Manage your customized panel designs", fontSize = 13.sp, color = Color(0xFF667085))
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFE7D4), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.PendingActions, contentDescription = null, tint = Color(0xFFF06416))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5EFE8), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${savedPanels.size} saved design${if (savedPanels.size == 1) "" else "s"}", color = Color(0xFF667085), fontSize = 12.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onSelectAll, enabled = savedPanels.isNotEmpty()) {
                Text(if (allSelected) "Clear all" else "Select all", color = Color(0xFFF06416), fontWeight = FontWeight.Bold)
            }
        }

        if (savedPanels.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.PendingActions, contentDescription = null, tint = Color(0xFFD0B7A0), modifier = Modifier.size(52.dp))
                Text("No saved panels yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121826))
                Text("Save a customized panel to manage it here.", color = Color(0xFF667085), fontSize = 13.sp)
            }
        } else {
            savedPanels.forEach { saved ->
                val isSelected = saved.productId in selectedIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) Color(0xFFFFF0E5) else Color.White)
                        .border(1.dp, if (isSelected) Color(0xFFF77F2C) else Color(0xFFEDE4DB), RoundedCornerShape(18.dp))
                        .clickable { onToggleSelection(saved.productId) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(if (isSelected) Color(0xFFF77F2C) else Color.Transparent)
                            .border(1.5.dp, if (isSelected) Color(0xFFF77F2C) else Color(0xFF344054), RoundedCornerShape(3.dp))
                            .clickable { onToggleSelection(saved.productId) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Text("✓", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(13.dp))
                            .border(1.dp, Color.Black.copy(alpha = 0.08f), RoundedCornerShape(13.dp))
                    ) {
                        val savedProduct = products.firstOrNull { it.id == saved.productId }
                        if (savedProduct?.imageUrl?.isNotBlank() == true) {
                            var imageFailed by remember(saved.productId, savedProduct.imageUrl) { mutableStateOf(false) }
                            if (!imageFailed) {
                                AsyncImage(
                                    model = savedProduct.imageUrl,
                                    contentDescription = "Saved ${saved.productName} panel",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit,
                                    onError = { imageFailed = true }
                                )
                            } else {
                                PanelVisualizer(
                                    product = savedProduct,
                                    colour = saved.colour,
                                    finish = saved.finish,
                                    is3d = false,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else if (savedProduct != null) {
                            PanelVisualizer(
                                product = savedProduct,
                                colour = saved.colour,
                                finish = saved.finish,
                                is3d = false,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color(android.graphics.Color.parseColor("#${customizationColourHex(saved.colour)}")))
                            )
                        }
                    }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(saved.productName, fontWeight = FontWeight.Bold, color = Color(0xFF121826), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${saved.colour} · ${saved.size}", color = Color(0xFF667085), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${saved.finish} finish · Qty ${saved.quantity}", color = Color(0xFF98A2B3), fontSize = 10.sp)
                    }
                    Text(formatPeso(saved.total), color = Color(0xFFF06416), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }

        if (selectedIds.isNotEmpty()) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFD64545)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD64545))
                ) { Text("Delete") }
                Button(
                    onClick = onUpdate,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C))
                ) { Text("Update") }
            }
        }
    }
}

private suspend fun loadCustomerOrderUpdates(
    context: Context,
    email: String,
    customerName: String
): List<CustomerOrderUpdate> = withContext(Dispatchers.IO) {
    val state = fetchBackendState(context) ?: return@withContext emptyList()
    val orders = state.optJSONArray("orders") ?: return@withContext emptyList()
    val normalizedEmail = email.trim().lowercase(Locale.US)
    val normalizedName = customerName.trim().lowercase(Locale.US)
    buildList {
        for (index in 0 until orders.length()) {
            val order = orders.optJSONObject(index) ?: continue
            val orderEmail = order.optString("customerEmail", order.optString("customer_email", "")).trim().lowercase(Locale.US)
            val orderName = order.optString("customer", order.optString("customerName", "")).trim().lowercase(Locale.US)
            if ((normalizedEmail.isNotBlank() && orderEmail == normalizedEmail) ||
                (orderEmail.isBlank() && normalizedName.isNotBlank() && orderName == normalizedName)
            ) {
                add(
                    CustomerOrderUpdate(
                        invoice = order.optString("quoteNumber", order.optString("quote_number", order.optString("invoice", "Order"))),
                        products = order.optString("products", "Order items"),
                        amount = order.optInt("amount", order.optInt("totalAmount", 0)),
                        paymentStatus = order.optString("paymentStatus", order.optString("payment_status", "Payment pending")),
                        deliveryStatus = order.optString("deliveryStatus", "Pending confirmation"),
                        courier = order.optString("courier", ""),
                        trackingNumber = order.optString("trackingNumber", ""),
                        updatedAt = order.optString("updatedAt", order.optString("createdAt", ""))
                    )
                )
            }
        }
    }.sortedByDescending { it.updatedAt }
}

private suspend fun loadInventory(context: Context): InventoryUiState = withContext(Dispatchers.IO) {
    try {
        val state = fetchBackendState(context) ?: throw IllegalStateException("Backend inventory could not be reached.")
        val inventoryArray = state.optJSONArray("inventory")
        val products = buildList {
            if (inventoryArray != null) {
                for (index in 0 until inventoryArray.length()) {
                    val item = inventoryArray.optJSONObject(index) ?: continue
                    add(
                        Product(
                            id = item.optInt("id", index + 1),
                            name = item.optString("name", "Panel Item"),
                            category = item.optString("categoryGroup", "").trim().ifBlank { "Uncategorized" },
                            size = item.optString("size", "N/A"),
                            material = item.optString("material", "N/A"),
                            price = item.optDouble("price", item.optDouble("pricePerMeter", 0.0)).toInt(),
                            quantity = item.optInt("quantity", 0),
                            description = item.optString("description", "Premium panel option for customer requests."),
                            panelType = item.optString("productType", item.optString("categoryName", "")).trim(),
                            imageUrl = resolveBackendImageUrl(
                                item.optString("imageAsset", item.optString("imageUrl", item.optString("imageData", ""))),
                                baseUrl = currentBackendBaseUrl(context)
                            ),
                            modelUrl = resolveInventoryModelUrl(item, currentBackendBaseUrl(context))
                        )
                    )
                }
            }
        }

        if (products.isEmpty()) {
            InventoryUiState(
                products = emptyList(),
                isLoading = false,
                sourceLabel = "Live inventory",
                errorMessage = "No inventory records are available yet."
            )
        } else {
            InventoryUiState(
                products = products,
                isLoading = false,
                sourceLabel = "Live inventory"
            )
        }
    } catch (error: Exception) {
        Log.w("AiyigeCustomer", "Using demo catalog because backend inventory could not load.", error)
        InventoryUiState(
            products = sampleProducts,
            isLoading = false,
            sourceLabel = "Demo catalog",
            errorMessage = "Could not reach the backend inventory. Showing sample products for now."
        )
    }
}

private enum class BottomItems(val tab: Tab, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Home(Tab.Home, "Home", Icons.Outlined.Home),
    Catalog(Tab.Catalog, "Catalog", Icons.Outlined.ShoppingBag),
    Ar(Tab.Ar, "AR", Icons.Outlined.CameraAlt),
    Customization(Tab.Customization, "Customize", Icons.Outlined.Build),
    Settings(Tab.Settings, "Settings", Icons.Outlined.PendingActions)
}

private enum class MenuSection {
    Payment,
    Profile
}

private enum class SettingsPage {
    Home,
    Login,
    ProfileDetails,
    DeliveryAddress,
    Password,
    Notifications,
    Quotations,
    HelpSupport,
    TermsPrivacy,
    AboutApp,
    Register,
    ForgotPassword,
    VerifyEmail,
    RecoverPassword
}

@Composable
private fun StatsRow(requestCount: Int, latestQuoteAmount: String, paymentStatusMessage: String) {
    val isPaid = paymentStatusMessage.equals("Paid", ignoreCase = true)
    val isWaitingForSeller = paymentStatusMessage.contains("seller quotation", ignoreCase = true)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        StatCard(
            when {
                isPaid -> "Paid"
                isWaitingForSeller -> "Quotation request"
                else -> "Pending Payment"
            },
            latestQuoteAmount.ifBlank { "₱0" },
            when {
                isPaid -> "Completed"
                isWaitingForSeller -> "Awaiting seller"
                else -> "Due"
            },
            Modifier.weight(1f)
        )
        StatCard("Quotes", requestCount.toString(), "Saved", Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(title: String, value: String, note: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(title, color = Color(0xFF667085), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
            Spacer(Modifier.height(1.dp))
            Text(note, color = Color(0xFF667085), fontSize = 11.sp)
        }
    }
}

@Composable
private fun HomeScreen(selectedProduct: Product?, onSelectAsQuote: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Featured Product", color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                if (selectedProduct == null) {
                    Text("No catalog yet.", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
                    Text("Featured item shows here.", color = Color(0xFF667085))
                } else {
                if (selectedProduct.imageUrl != null) {
                        AsyncImage(
                            model = selectedProduct.imageUrl,
                            contentDescription = selectedProduct.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF4EFE7), RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFFF77F2C), Color(0xFFFFD29C))
                                    ),
                                    RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Panel Preview", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(selectedProduct.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
                    Text(selectedProduct.description, color = Color(0xFF667085))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        InfoChip("Price", "₱${selectedProduct.price}")
                        InfoChip("Stock", selectedProduct.quantity.toString())
                        InfoChip("Category", selectedProduct.category)
                    }
                    Button(
                        onClick = onSelectAsQuote,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C))
                    ) { Text("Use for Quote") }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            QuickCard("Support", "Material and size help.", Modifier.weight(1f))
            QuickCard("Quick Quote", "Send a request fast.", Modifier.weight(1f))
        }
    }
}

@Composable
private fun RowScope.InfoChip(label: String, value: String, dark: Boolean = false) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (dark) Color.Black.copy(alpha = 0.32f) else Color(0xFFFDF7F0)
        ),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.weight(1f)
    ) {
        Column(Modifier.padding(horizontal = 10.dp, vertical = 11.dp)) {
            Text(label, color = if (dark) Color.White.copy(alpha = 0.72f) else Color(0xFF667085), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(value, color = if (dark) Color.White else Color(0xFF121826), fontSize = 13.sp, lineHeight = 17.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun RowScope.SettingsInfoChip(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFF2D7BC)),
        modifier = modifier
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Text(label, color = Color(0xFF667085), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(
                value,
                color = Color(0xFF121826),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickCard(title: String, desc: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Color(0xFF667085), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(desc, fontWeight = FontWeight.SemiBold, color = Color(0xFF121826), lineHeight = 20.sp)
        }
    }
}

@Composable
private fun CustomerActionLoadingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x660E1726))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.padding(28.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFF2D7BC)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(26.dp),
                    color = Color(0xFFF57419),
                    strokeWidth = 3.dp
                )
                Text(message, color = Color(0xFF121826), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun LoadingSkeletonBlock(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp)
) {
    val shimmerTransition = rememberInfiniteTransition(label = "skeletonShimmer")
    val shimmerProgress by shimmerTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skeletonProgress"
    )
    val brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFE7DDD1),
            Color(0xFFF8F4EF),
            Color(0xFFE7DDD1)
        ),
        start = Offset(x = 80f * shimmerProgress, y = 0f),
        end = Offset(x = 420f + 80f * shimmerProgress, y = 140f)
    )

    Box(
        modifier = modifier
            .background(brush, shape)
    )
}

@Composable
private fun LoadingSkeletonText(
    modifier: Modifier = Modifier,
    widthFraction: Float = 1f,
    height: androidx.compose.ui.unit.Dp = 14.dp
) {
    LoadingSkeletonBlock(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(height)
    )
}

@Composable
private fun HomeOverviewSkeleton() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LoadingSkeletonText(widthFraction = 0.22f, height = 11.dp)
                LoadingSkeletonText(widthFraction = 0.78f, height = 28.dp)
                LoadingSkeletonText(widthFraction = 0.62f, height = 16.dp)
                LoadingSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(270.dp),
                    shape = RoundedCornerShape(24.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        }

        SectionHeader("Featured panels")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(2) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        LoadingSkeletonText(widthFraction = 0.72f, height = 14.dp)
                        LoadingSkeletonText(widthFraction = 0.42f, height = 12.dp)
                        LoadingSkeletonText(widthFraction = 0.56f, height = 12.dp)
                    }
                }
            }
        }

        SectionHeader("Categories")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            repeat(3) {
                LoadingSkeletonBlock(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
private fun MenuSkeletonScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF7EB), Color(0xFFF4EFE7))
                )
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFF8F1E8))
                        ),
                        RoundedCornerShape(30.dp)
                    )
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LoadingSkeletonText(widthFraction = 0.22f, height = 11.dp)
                LoadingSkeletonText(widthFraction = 0.56f, height = 28.dp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    LoadingSkeletonBlock(
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                    LoadingSkeletonBlock(
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        }

        repeat(3) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LoadingSkeletonText(widthFraction = 0.32f, height = 16.dp)
                    LoadingSkeletonText(widthFraction = 0.9f, height = 14.dp)
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                    LoadingSkeletonText(widthFraction = 0.62f, height = 12.dp)
                }
            }
        }
    }
}

@Composable
private fun PaymentSkeletonScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        LoadingSkeletonText(widthFraction = 0.36f, height = 24.dp)
                        LoadingSkeletonText(widthFraction = 0.64f, height = 14.dp)
                    }
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .width(72.dp)
                            .height(28.dp),
                        shape = RoundedCornerShape(999.dp)
                    )
                }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LoadingSkeletonText(widthFraction = 0.34f, height = 14.dp)
                        repeat(4) {
                            LoadingSkeletonText(widthFraction = 0.92f, height = 13.dp)
                        }
                        LoadingSkeletonText(widthFraction = 0.78f, height = 12.dp)
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LoadingSkeletonText(widthFraction = 0.24f, height = 13.dp)
                        LoadingSkeletonText(widthFraction = 0.74f, height = 13.dp)
                        LoadingSkeletonText(widthFraction = 0.42f, height = 12.dp)
                    }
                }

                LoadingSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp),
                    shape = RoundedCornerShape(999.dp)
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LoadingSkeletonText(widthFraction = 0.30f, height = 14.dp)
                        repeat(6) {
                            LoadingSkeletonText(widthFraction = if (it % 2 == 0) 0.86f else 0.60f, height = 13.dp)
                        }
                    }
                }

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoadingSkeletonText(widthFraction = 0.24f, height = 13.dp)
                        LoadingSkeletonText(widthFraction = 0.76f, height = 13.dp)
                    }
                }

                LoadingSkeletonText(widthFraction = 0.44f, height = 13.dp)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        shape = RoundedCornerShape(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSkeletonScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                LoadingSkeletonText(widthFraction = 0.16f, height = 11.dp)
                LoadingSkeletonText(widthFraction = 0.54f, height = 24.dp)

                LoadingSkeletonBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp)
                )

                LoadingSkeletonBlock(
                    modifier = Modifier
                        .width(88.dp)
                        .height(26.dp),
                    shape = RoundedCornerShape(999.dp)
                )

                Divider(color = Color(0x15121826), thickness = 1.dp)

                Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LoadingSkeletonText(widthFraction = 0.44f, height = 12.dp)
                        LoadingSkeletonText(widthFraction = 0.88f, height = 16.dp)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LoadingSkeletonText(widthFraction = 0.40f, height = 12.dp)
                        LoadingSkeletonText(widthFraction = 0.72f, height = 16.dp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LoadingSkeletonText(widthFraction = 0.44f, height = 12.dp)
                        LoadingSkeletonText(widthFraction = 0.32f, height = 16.dp)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        LoadingSkeletonText(widthFraction = 0.30f, height = 12.dp)
                        LoadingSkeletonText(widthFraction = 0.68f, height = 16.dp)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    LoadingSkeletonBlock(
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                    LoadingSkeletonBlock(
                        modifier = Modifier.weight(1f).height(42.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LoadingSkeletonText(widthFraction = 0.28f, height = 18.dp)
                LoadingSkeletonText(widthFraction = 0.40f, height = 13.dp)
                repeat(3) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LoadingSkeletonText(widthFraction = 0.52f, height = 14.dp)
                                    LoadingSkeletonText(widthFraction = 0.30f, height = 11.dp)
                                }
                                LoadingSkeletonBlock(
                                    modifier = Modifier
                                        .width(66.dp)
                                        .height(24.dp),
                                    shape = RoundedCornerShape(999.dp)
                                )
                            }
                            LoadingSkeletonText(widthFraction = 0.86f, height = 12.dp)
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LoadingSkeletonText(widthFraction = 0.24f, height = 11.dp)
                                LoadingSkeletonText(widthFraction = 0.34f, height = 13.dp)
                            }
                        }
                    }
                }
            }
        }

        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LoadingSkeletonText(widthFraction = 0.26f, height = 18.dp)
                repeat(3) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    LoadingSkeletonText(widthFraction = 0.52f, height = 13.dp)
                                    LoadingSkeletonText(widthFraction = 0.28f, height = 11.dp)
                                }
                                LoadingSkeletonBlock(
                                    modifier = Modifier
                                        .width(68.dp)
                                        .height(24.dp),
                                    shape = RoundedCornerShape(999.dp)
                                )
                            }
                            LoadingSkeletonText(widthFraction = 0.80f, height = 12.dp)
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                LoadingSkeletonText(widthFraction = 0.18f, height = 11.dp)
                                LoadingSkeletonText(widthFraction = 0.24f, height = 13.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthPageSkeletonScreen(page: SettingsPage, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LoadingSkeletonBlock(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                        LoadingSkeletonText(widthFraction = 0.38f, height = 22.dp)
                        LoadingSkeletonText(widthFraction = 0.28f, height = 12.dp)
                    }
                }

                when (page) {
                    SettingsPage.Login -> {
                        LoadingSkeletonText(widthFraction = 0.56f, height = 13.dp)
                        repeat(2) {
                            LoadingSkeletonBlock(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(18.dp)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LoadingSkeletonText(widthFraction = 0.34f, height = 12.dp)
                        }
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            LoadingSkeletonText(widthFraction = 0.54f, height = 12.dp)
                        }
                    }

                    SettingsPage.ForgotPassword -> {
                        LoadingSkeletonText(widthFraction = 0.82f, height = 14.dp)
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        LoadingSkeletonText(widthFraction = 0.68f, height = 12.dp)
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            LoadingSkeletonText(widthFraction = 0.42f, height = 12.dp)
                        }
                    }

                    SettingsPage.VerifyEmail -> {
                        LoadingSkeletonText(widthFraction = 0.72f, height = 14.dp)
                        LoadingSkeletonBlock(
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        LoadingSkeletonBlock(
                            modifier = Modifier.fillMaxWidth().height(46.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                    }

                    SettingsPage.RecoverPassword -> {
                        LoadingSkeletonText(widthFraction = 0.88f, height = 14.dp)
                        LoadingSkeletonText(widthFraction = 0.42f, height = 13.dp)
                        repeat(2) {
                            LoadingSkeletonBlock(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(18.dp)
                            )
                        }
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            LoadingSkeletonText(widthFraction = 0.44f, height = 12.dp)
                        }
                    }

                    SettingsPage.Register -> {
                        LoadingSkeletonText(widthFraction = 0.66f, height = 13.dp)
                        repeat(7) {
                            LoadingSkeletonBlock(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(
                                        when (it) {
                                            4, 5 -> 24.dp
                                            else -> 54.dp
                                        }
                                    ),
                                shape = RoundedCornerShape(18.dp)
                            )
                        }
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(18.dp)
                        )
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            LoadingSkeletonText(widthFraction = 0.60f, height = 12.dp)
                        }
                    }

                    SettingsPage.Home,
                    SettingsPage.ProfileDetails,
                    SettingsPage.DeliveryAddress,
                    SettingsPage.Password,
                    SettingsPage.Notifications,
                    SettingsPage.Quotations,
                    SettingsPage.HelpSupport,
                    SettingsPage.TermsPrivacy,
                    SettingsPage.AboutApp -> Unit
                }
            }
        }
    }
}

@Composable
private fun HomeOverviewScreen(
    products: List<Product>,
    isInventoryLoading: Boolean,
    isSignedIn: Boolean,
    signInName: String,
    catalogCount: Int,
    inStockCount: Int,
    requestCount: Int,
    lastQuoteSummary: String?,
    recentlyViewedIds: List<Int>,
    onOpenCatalog: () -> Unit,
    onOpenAr: () -> Unit,
    onAddToCart: (Product) -> Unit,
    onProceedToAr: (Int) -> Unit,
    onOpenQuote: () -> Unit,
    onPreviewProduct: (Int) -> Unit
) {
    val context = LocalContext.current
    var homeDetailsProductId by rememberSaveable { mutableStateOf<Int?>(null) }
    var homePreviewMode by rememberSaveable { mutableStateOf("2D") }
    if (isInventoryLoading) {
        HomeOverviewSkeleton()
        return
    }

    // Start with the same inventory order used by Catalog so featured prices
    // always match the products customers see when they browse.
    var selectedCategory by rememberSaveable { mutableStateOf("New Arrivals") }
    val carouselState = rememberLazyListState()
    val density = LocalDensity.current
    val carouselProgress by remember(carouselState, products) {
        derivedStateOf {
            if (products.isEmpty()) {
                0f
            } else {
                val itemWidthPx = with(density) { 280.dp.roundToPx() + 12.dp.roundToPx() }
                val offset = carouselState.firstVisibleItemIndex + (carouselState.firstVisibleItemScrollOffset.toFloat() / itemWidthPx.coerceAtLeast(1))
                offset.coerceIn(0f, products.lastIndex.toFloat())
            }
        }
    }
    val featuredPanels = remember(products, selectedCategory) {
        val filtered = when (selectedCategory) {
            "Wall Panels" -> products.filter { product ->
                listOf(product.name, product.category, product.description).any {
                    it.contains("wall", ignoreCase = true)
                }
            }
            "Ceiling Panels" -> products.filter { product ->
                listOf(product.name, product.category, product.description).any {
                    it.contains("ceiling", ignoreCase = true)
                }
            }
            else -> products
        }
        (if (filtered.isEmpty()) products else filtered).take(8)
    }
    val recentlyViewed = remember(products, recentlyViewedIds) {
        recentlyViewedIds.mapNotNull { id -> products.firstOrNull { product -> product.id == id } }.take(8)
    }
    val popularPanels = remember(products) {
        products.sortedWith(
            compareByDescending<Product> { it.quantity > 0 }
                .thenByDescending { it.quantity }
                .thenBy { it.name }
        ).take(8)
    }
    val openHomePanel: (Int) -> Unit = { productId ->
        homeDetailsProductId = productId
        onPreviewProduct(productId)
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RefinedHeroBanner(
            isSignedIn = isSignedIn,
            signInName = signInName,
            onOpenCatalog = onOpenCatalog,
            onOpenAr = onOpenAr,
            onOpenQuote = onOpenQuote,
            heroProduct = featuredPanels.firstOrNull(),
            onPreviewProduct = openHomePanel,
            parallaxProgress = carouselProgress
        )

        SectionHeader("Featured panels")
        FeaturedPanelCarousel(
            products = featuredPanels,
            listState = carouselState,
            onPreviewProduct = openHomePanel
        )

        SectionHeader("Recently viewed")
        FeaturedPanelCarousel(
            products = recentlyViewed,
            listState = rememberLazyListState(),
            onPreviewProduct = openHomePanel,
            emptyTitle = "No recently viewed panels",
            emptyDescription = "Browse a panel to build your viewing history."
        )

        SectionHeader("Popular designs")
        FeaturedPanelCarousel(
            products = popularPanels,
            listState = rememberLazyListState(),
            onPreviewProduct = openHomePanel,
            emptyTitle = "No popular designs yet",
            emptyDescription = "Popular panels will appear here as inventory grows."
        )

    }

    val homeDetailsProduct = products.firstOrNull { it.id == homeDetailsProductId }
    if (homeDetailsProduct != null) {
        Dialog(
            onDismissRequest = { homeDetailsProductId = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.32f))
                        .clickable { homeDetailsProductId = null }
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(Color.White)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                        .clickable { },
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Box(Modifier.width(54.dp).height(5.dp).background(Color(0xFFD8C7B0), RoundedCornerShape(999.dp)))
                    }
                    Text("Panel Details", color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "Review this panel here, then open AR without leaving Home.",
                        color = Color(0xFF667085),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                    CatalogPreview(
                        product = homeDetailsProduct,
                        mode = homePreviewMode,
                        onModeChange = { homePreviewMode = it }
                    )
                    SelectedPanelSummaryCard(
                        product = homeDetailsProduct,
                        onAddToCart = {
                            if (homeDetailsProduct.quantity > 0) {
                                onAddToCart(homeDetailsProduct)
                                Toast.makeText(context, "${homeDetailsProduct.name} added to cart", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "${homeDetailsProduct.name} is out of stock", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onProceedToAr = {
                            homeDetailsProductId = null
                            onProceedToAr(homeDetailsProduct.id)
                        }
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroBanner(
    isSignedIn: Boolean,
    signInName: String,
    onOpenCatalog: () -> Unit,
    onOpenAr: () -> Unit,
    onOpenQuote: () -> Unit,
    heroProduct: Product?,
    onPreviewProduct: (Int) -> Unit,
    parallaxProgress: Float
) {
    var heroLoaded by rememberSaveable { mutableStateOf(false) }
    val heroAlpha by animateFloatAsState(targetValue = if (heroLoaded) 1f else 0f, label = "heroAlpha")
    val heroOffsetY by animateFloatAsState(targetValue = if (heroLoaded) 0f else 10f, label = "heroOffsetY")
    val heroParallax by animateFloatAsState(
        targetValue = -parallaxProgress * 18f,
        label = "heroParallax"
    )
    val accentParallax by animateFloatAsState(
        targetValue = parallaxProgress * 8f,
        label = "accentParallax"
    )

    LaunchedEffect(Unit) {
        heroLoaded = true
    }

    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFB87A41), Color(0xFFF6E4C9))
                    )
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(18.dp)
                    .width(96.dp)
                    .height(96.dp)
                    .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(48.dp))
                    .graphicsLayer { translationX = accentParallax }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(26.dp)
                    .width(64.dp)
                    .height(64.dp)
                    .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(32.dp))
                    .graphicsLayer { translationX = -accentParallax }
            )

            heroProduct?.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = heroProduct.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(330.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(330.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x11FFFFFF),
                                Color(0xAA121826),
                                Color(0xE6121826)
                            )
                        )
                )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .graphicsLayer {
                        alpha = heroAlpha
                        translationY = heroOffsetY
                        translationX = heroParallax * 0.35f
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Aiyige", color = Color(0xFFF8F1E8), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    "Visualize before you buy",
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    if (isSignedIn) "Welcome back, $signInName." else "Try AR, then quote in minutes.",
                    color = Color(0xFFF4EFE7),
                    lineHeight = 22.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text("Featured", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            heroProduct?.category ?: "Featured",
                            color = Color(0xFFF8F1E8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            heroProduct?.let { onPreviewProduct(it.id) } ?: onOpenAr()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) { Text("Try AR Now", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    Button(
                        onClick = onOpenCatalog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.92f),
                            contentColor = Color(0xFF121826)
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) { Text("Browse", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                    heroProduct?.let { product ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
                            modifier = Modifier
                                .width(68.dp)
                                .height(58.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                .padding(horizontal = 7.dp, vertical = 7.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Price", color = Color(0xFFF8F1E8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("₱${product.price}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            heroProduct?.let { product ->
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .graphicsLayer { translationX = accentParallax * 0.45f }
                ) {
                    Column(Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Price", color = Color(0xFFF8F1E8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("₱${product.price}", color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RefinedHeroBanner(
    isSignedIn: Boolean,
    signInName: String,
    onOpenCatalog: () -> Unit,
    onOpenAr: () -> Unit,
    onOpenQuote: () -> Unit,
    heroProduct: Product?,
    onPreviewProduct: (Int) -> Unit,
    parallaxProgress: Float
) {
    var heroLoaded by rememberSaveable { mutableStateOf(false) }
    val heroAlpha by animateFloatAsState(targetValue = if (heroLoaded) 1f else 0f, label = "refinedHeroAlpha")
    val heroOffsetY by animateFloatAsState(targetValue = if (heroLoaded) 0f else 10f, label = "refinedHeroOffsetY")
    val accentParallax by animateFloatAsState(
        targetValue = parallaxProgress * 8f,
        label = "refinedAccentParallax"
    )

    LaunchedEffect(Unit) {
        heroLoaded = true
    }

    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(330.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF151A26), Color(0xFF2A2430), Color(0xFF473225))
                    )
                )
        ) {
            heroProduct?.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = heroProduct.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0x06000000),
                                Color(0x55000000),
                                Color(0xD9121826)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(18.dp)
                    .graphicsLayer {
                        alpha = heroAlpha
                        translationY = heroOffsetY
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Visualize before you buy",
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    if (isSignedIn) "Welcome back, $signInName." else "Try AR, then quote in minutes.",
                    color = Color(0xFFF4EFE7),
                    lineHeight = 22.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Featured", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            heroProduct?.category ?: "Featured panel",
                            color = Color(0xFFF8F1E8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            heroProduct?.let { onPreviewProduct(it.id) } ?: onOpenAr()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                        modifier = Modifier.weight(1.15f),
                        contentPadding = PaddingValues(vertical = 9.dp)
                    ) {
                        Text(
                            "Try AR",
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Clip,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onOpenCatalog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.92f),
                            contentColor = Color(0xFF121826)
                        ),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 9.dp)
                    ) { Text("Browse", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    heroProduct?.let { product ->
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF121826)),
                            modifier = Modifier
                                .width(68.dp)
                                .height(58.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 7.dp, vertical = 7.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Price", color = Color(0xFFF8F1E8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("₱${product.price}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        title,
        color = Color(0xFF121826),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 16.sp
    )
}

@Composable
private fun FeaturedPanelCarousel(
    products: List<Product>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onPreviewProduct: (Int) -> Unit,
    emptyTitle: String = "No featured panels",
    emptyDescription: String = "Products will appear here when inventory loads."
) {
    if (products.isEmpty()) {
        QuickCard(emptyTitle, emptyDescription, Modifier.fillMaxWidth())
        return
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(end = 8.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    ) {
        items(products, key = { it.id }) { product ->
            Box(modifier = Modifier.width(258.dp)) {
                FeaturedPanelCard(
                    product = product,
                    onClick = { onPreviewProduct(product.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeaturedPanelCard(product: Product, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF4EFE7))
            ) {
                if (product.imageUrl != null) {
                    AsyncImage(
                        model = customizationInventoryPreviewAsset(product.name) ?: product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        // Product previews are artwork for this fixed card area. Crop the
                        // source to the card bounds so new inventory does not leave empty
                        // margins around the panel image.
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFF77F2C), Color(0xFFFFC27A))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Panel", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.82f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(product.category, color = Color(0xFF121826), fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color(0xFF121826).copy(alpha = 0.82f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (product.quantity <= 0) "Out of stock" else if (product.quantity <= 5) "Low stock" else "In stock",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(product.name, color = Color(0xFF121826), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text("₱${product.price}", color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold)
                Text("${product.quantity} available", color = Color(0xFF667085), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun AddressFormScreen(
    modifier: Modifier = Modifier,
    fullName: String,
    mobileNumber: String,
    streetAddress: String,
    cityProvince: String,
    postalCode: String,
    onStreetAddressChange: (String) -> Unit,
    onCityProvinceChange: (String) -> Unit,
    onPostalCodeChange: (String) -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    val canContinue = streetAddress.isNotBlank() && cityProvince.isNotBlank() && postalCode.matches(Regex("\\d{4}"))
    val hasSavedAddress = streetAddress.isNotBlank() && cityProvince.isNotBlank() && postalCode.isNotBlank()
    var showAddAddressForm by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier
            .background(Color(0xFFFFFBF6))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text("AR Panels", color = Color(0xFF898681), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }
            Column { Text("Delivery address", color = Color(0xFF202124), fontSize = 17.sp, fontWeight = FontWeight.Bold); Text("Where to deliver panels", color = Color(0xFF777777), fontSize = 12.sp) }
        }
        if (hasSavedAddress) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE3DFDA)), shape = RoundedCornerShape(14.dp)) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Default", color = Color(0xFF8A4A0A), fontSize = 11.sp, modifier = Modifier.border(1.dp, Color(0xFFF57419), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 3.dp))
                    Text(fullName, color = Color(0xFFB7B7B7), fontWeight = FontWeight.Bold)
                    Text(streetAddress, color = Color(0xFF999999))
                    Text(cityProvince, color = Color(0xFF999999))
                    Text(mobileNumber, color = Color(0xFF999999))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedButton(onClick = { }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) { Text("Edit") }; OutlinedButton(onClick = { }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)) { Text("Set default") } }
                }
            }
        }
        OutlinedButton(onClick = { showAddAddressForm = true }, modifier = Modifier.fillMaxWidth().height(54.dp), border = BorderStroke(1.dp, Color(0xFFE3DFDA)), shape = RoundedCornerShape(14.dp)) { Text("＋  Add new address", color = Color(0xFFF57419), fontWeight = FontWeight.Bold) }
        if (showAddAddressForm) {
            Text("ADD ADDRESS", color = Color(0xFFB0ADA8), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(fullName, { }, modifier = Modifier.fillMaxWidth(), label = { Text("Full name") }, placeholder = { Text("Enter full name") }, readOnly = true, singleLine = true, shape = RoundedCornerShape(10.dp))
            OutlinedTextField(streetAddress, onStreetAddressChange, modifier = Modifier.fillMaxWidth(), label = { Text("Street address") }, placeholder = { Text("House no., street, barangay") }, singleLine = true, shape = RoundedCornerShape(10.dp))
            OutlinedTextField(cityProvince, onCityProvinceChange, modifier = Modifier.fillMaxWidth(), label = { Text("City / Municipality") }, placeholder = { Text("e.g. Rosario, Cavite") }, singleLine = true, shape = RoundedCornerShape(10.dp))
            OutlinedTextField(postalCode, { onPostalCodeChange(it.filter(Char::isDigit).take(4)) }, modifier = Modifier.fillMaxWidth(), label = { Text("ZIP code") }, placeholder = { Text("4 digits") }, singleLine = true, shape = RoundedCornerShape(10.dp))
            OutlinedTextField(mobileNumber, { }, modifier = Modifier.fillMaxWidth(), label = { Text("Mobile number") }, placeholder = { Text("09XX XXX XXXX") }, readOnly = true, singleLine = true, shape = RoundedCornerShape(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { onCancel(); showAddAddressForm = false }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(10.dp)) { Text("Cancel") }
                Button(onClick = { onSave(); showAddAddressForm = false }, enabled = canContinue, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419))) { Text("Save address", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun CartScreen(
    cart: List<CartLine>,
    onQuantityChange: (Int, Int) -> Unit,
    onToggleSelection: (Int) -> Unit,
    onSelectAll: (Boolean) -> Unit,
    onContinueShopping: () -> Unit,
    onCheckout: (List<CartLine>) -> Unit
) {
    val allSelected = cart.isNotEmpty() && cart.all { it.selected }
    val selectedLines = cart.filter { it.selected }
    var itemPendingRemoval by remember { mutableStateOf<CartLine?>(null) }
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFFFFBF6)).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Your Cart", fontWeight = FontWeight.ExtraBold, fontSize = 26.sp, color = Color(0xFF121826))
        if (cart.isEmpty()) {
            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Your cart is empty", fontWeight = FontWeight.Bold, color = Color(0xFF121826))
                    Text("Browse the catalog and add panels here before checkout.", color = Color(0xFF667085), textAlign = TextAlign.Center)
                    Button(onClick = onContinueShopping, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C))) { Text("Browse catalog") }
                }
            }
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = allSelected, onCheckedChange = onSelectAll)
                Text("Select all", color = Color(0xFF344054), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                Text("${selectedLines.size} selected", color = Color(0xFF667085), fontSize = 12.sp)
            }
            cart.forEach { line ->
                val cartPreview = customizationInventoryPreviewAsset(line.product.name) ?: line.product.imageUrl
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = line.selected,
                                onCheckedChange = { onToggleSelection(line.product.id) },
                                modifier = Modifier.size(28.dp)
                            )
                            if (!cartPreview.isNullOrBlank()) {
                                AsyncImage(
                                    model = cartPreview,
                                    contentDescription = line.product.name,
                                    modifier = Modifier.size(72.dp).clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(72.dp).background(Color(0xFFF4EFE7), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) { Text("Panel", color = Color(0xFF8D4E15), fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(line.product.name, fontWeight = FontWeight.Bold, color = Color(0xFF121826))
                                Text(formatPeso(line.product.price), color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            if (line.quantity <= 1) {
                                                itemPendingRemoval = line
                                            } else {
                                                onQuantityChange(line.product.id, line.quantity - 1)
                                            }
                                        },
                                        modifier = Modifier.size(width = 34.dp, height = 30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        shape = CircleShape
                                    ) { Text("−", fontSize = 14.sp) }
                                    Text("${line.quantity}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    OutlinedButton(
                                        onClick = { onQuantityChange(line.product.id, line.quantity + 1) },
                                        enabled = line.quantity < line.product.quantity,
                                        modifier = Modifier.size(width = 34.dp, height = 30.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        shape = CircleShape
                                    ) { Text("+", fontSize = 14.sp) }
                                }
                            }
                            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text(formatPeso(line.product.price * line.quantity), fontWeight = FontWeight.Bold, color = Color(0xFF121826))
                                IconButton(
                                    onClick = { itemPendingRemoval = line },
                                    modifier = Modifier.size(32.dp).background(Color(0xFFFFECEC), RoundedCornerShape(9.dp))
                                ) {
                                    Icon(
                                        Icons.Outlined.DeleteOutline,
                                        contentDescription = "Remove ${line.product.name}",
                                        tint = Color(0xFFB42318),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Button(onClick = { onCheckout(selectedLines) }, enabled = selectedLines.isNotEmpty(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)), modifier = Modifier.fillMaxWidth()) {
                Text("Proceed to checkout")
            }
        }
    }

    itemPendingRemoval?.let { line ->
        AlertDialog(
            onDismissRequest = { itemPendingRemoval = null },
            title = { Text("Remove item?") },
            text = { Text("Are you sure you want to remove ${line.product.name} from your cart?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onQuantityChange(line.product.id, 0)
                        itemPendingRemoval = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFB42318))
                ) { Text("Remove", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { itemPendingRemoval = null }) { Text("Keep item") }
            }
        )
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFFF77F2C) else Color.White.copy(alpha = 0.92f),
            contentColor = if (selected) Color.White else Color(0xFF121826)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, lineHeight = 13.sp, textAlign = TextAlign.Center)
    }
}

@Composable
private fun StepCard(step: String, title: String, desc: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(step, color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(title, color = Color(0xFF121826), fontWeight = FontWeight.ExtraBold)
            Text(desc, color = Color(0xFF667085), fontSize = 12.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreen(
    isInventoryLoading: Boolean,
    categories: List<String>,
    search: String,
    onSearchChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    products: List<Product>,
    selectedProduct: Product?,
    initialDetailsProductId: Int? = null,
    onInitialDetailsShown: () -> Unit = {},
    onSelect: (Int) -> Unit,
    onProceedToAr: () -> Unit,
    onAddToCart: (Product, Int) -> Unit
) {
    val context = LocalContext.current
    var showPanelDetails by rememberSaveable { mutableStateOf(false) }
    var previewMode by rememberSaveable { mutableStateOf("2D") }
    var selectedPanelType by rememberSaveable { mutableStateOf("All") }
    var isPanelClosing by remember { mutableStateOf(false) }
    var isHandleDragging by remember { mutableStateOf(false) }
    var handleDragDistance by remember { mutableStateOf(0f) }
    var cartToastMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var isSearchOpen by rememberSaveable { mutableStateOf(search.isNotBlank()) }
    var showFilters by rememberSaveable { mutableStateOf(false) }
    val searchFocusRequester = remember { FocusRequester() }
    val animatedHandleOffset by animateFloatAsState(
        targetValue = if (isHandleDragging) handleDragDistance else 0f,
        // Follow the finger immediately while dragging. Animating every pointer
        // event makes the handle continuously chase its target and look jittery.
        // Keep the eased motion only for the release/snap-back state.
        animationSpec = if (isHandleDragging) snap() else tween(180),
        label = "panel handle offset"
    )
    val sheetScope = rememberCoroutineScope()
    val panelTypeOptions = remember(products) {
        val preferredOrder = listOf("Seamless", "Groove")
        val availableTypes = products.map { it.panelType.trim() }.filter { it.isNotBlank() }.distinct()
        buildList {
            add("All")
            preferredOrder.filter { it in availableTypes }.forEach { add(it) }
        }
    }

    LaunchedEffect(panelTypeOptions) {
        if (selectedPanelType != "All" && selectedPanelType !in panelTypeOptions) {
            selectedPanelType = "All"
        }
    }

    LaunchedEffect(selectedProduct?.id) {
        previewMode = "2D"
    }

    LaunchedEffect(initialDetailsProductId, selectedProduct?.id) {
        if (initialDetailsProductId != null && initialDetailsProductId == selectedProduct?.id) {
            showPanelDetails = true
            onInitialDetailsShown()
        }
    }

    LaunchedEffect(isPanelClosing) {
        if (isPanelClosing) {
            delay(280)
            showPanelDetails = false
            isPanelClosing = false
        }
    }

    LaunchedEffect(cartToastMessage) {
        if (cartToastMessage != null) {
            delay(1800)
            cartToastMessage = null
        }
    }

    LaunchedEffect(isSearchOpen) {
        if (isSearchOpen) searchFocusRequester.requestFocus()
    }

    LaunchedEffect(search) {
        if (search.isNotBlank()) isSearchOpen = true
    }

    val displayProducts = remember(products, selectedPanelType) {
        products.filter { product ->
            selectedPanelType == "All" || product.panelType.equals(selectedPanelType, ignoreCase = true)
        }
    }

    if (isInventoryLoading) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LoadingSkeletonText(widthFraction = 0.46f, height = 26.dp)
            LoadingSkeletonText(widthFraction = 0.72f, height = 16.dp)
            LoadingSkeletonBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                repeat(4) {
                    LoadingSkeletonBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
            repeat(3) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        LoadingSkeletonBlock(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = RoundedCornerShape(20.dp)
                        )
                        LoadingSkeletonText(widthFraction = 0.28f, height = 12.dp)
                        LoadingSkeletonText(widthFraction = 0.76f, height = 18.dp)
                        LoadingSkeletonText(widthFraction = 0.92f, height = 14.dp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            LoadingSkeletonBlock(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(16.dp)
                            )
                            LoadingSkeletonBlock(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFBF6), RoundedCornerShape(28.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        // Catalog owns its scroll container so it does not compete with the
        // app shell's scroll state during fast swipes.
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Catalog",
                modifier = Modifier.weight(1f, fill = false),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
                color = Color(0xFF121826),
                letterSpacing = (-0.4).sp
            )

            AnimatedVisibility(
                visible = isSearchOpen,
                enter = expandHorizontally(expandFrom = Alignment.End) + fadeIn(),
                exit = shrinkHorizontally(shrinkTowards = Alignment.End) + fadeOut()
            ) {
                OutlinedTextField(
                    value = search,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .width(174.dp)
                        .focusRequester(searchFocusRequester),
                    placeholder = { Text("Search", maxLines = 1) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    trailingIcon = {
                        if (search.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            }

            IconButton(
                onClick = { isSearchOpen = !isSearchOpen },
                modifier = Modifier.size(42.dp)
            ) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = if (isSearchOpen) "Close search" else "Search catalog",
                    tint = Color(0xFF121826)
                )
            }
            IconButton(
                onClick = { showFilters = !showFilters },
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        if (showFilters) Color(0xFFFFE5D3) else Color.Transparent,
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Outlined.Tune,
                    contentDescription = "Filter catalog",
                    tint = if (showFilters) Color(0xFFF06416) else Color(0xFF121826)
                )
            }
        }

        AnimatedVisibility(visible = showFilters) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(category).coerceAtLeast(0),
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    divider = {}
                ) {
                    categories.forEach { item ->
                        val selected = item == category
                        androidx.compose.material3.Tab(
                            selected = selected,
                            onClick = { onCategoryChange(item) },
                            text = {
                                Text(
                                    if (item == "All") "All Categories" else item,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }

        if (panelTypeOptions.size > 1) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Panel type",
                    color = Color(0xFF667085),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(end = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(panelTypeOptions, key = { it }) { type ->
                        val selected = selectedPanelType == type
                        Button(
                            onClick = { selectedPanelType = type },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFFF77F2C) else Color.White.copy(alpha = 0.82f),
                                contentColor = if (selected) Color.White else Color(0xFF121826)
                            ),
                            shape = RoundedCornerShape(999.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                        ) {
                            Text(
                                if (type == "All") "All types" else type,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
            }
        }

        if (displayProducts.isEmpty()) {
            EmptyState()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                displayProducts.forEach { product ->
                    val isSelected = selectedProduct?.id == product.id
                    CatalogCard(
                        product = product,
                        selected = isSelected,
                        onClick = {
                            onSelect(product.id)
                            showPanelDetails = true
                        },
                        onAddToCart = {
                            if (product.quantity > 0) {
                                onAddToCart(product, 1)
                                cartToastMessage = "${product.name} added to cart"
                                Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "${product.name} is out of stock", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
        }
    }

    if ((showPanelDetails || isPanelClosing) && selectedProduct != null) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Box(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.32f))
                )
                AnimatedVisibility(
                    visible = !isPanelClosing,
                    modifier = Modifier.align(Alignment.BottomCenter),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { translationY = animatedHandleOffset }
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                    .background(Color(0xFFFFFFFF))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragStart = {
                                    isHandleDragging = true
                                    handleDragDistance = 0f
                                },
                                onVerticalDrag = { change, dragAmount ->
                                    change.consume()
                                    handleDragDistance = (handleDragDistance + dragAmount).coerceAtLeast(0f)
                                },
                                onDragEnd = {
                                    if (handleDragDistance > 40f) {
                                        isHandleDragging = false
                                        isPanelClosing = true
                                    } else {
                                        isHandleDragging = false
                                        handleDragDistance = 0f
                                    }
                                },
                                onDragCancel = {
                                    isHandleDragging = false
                                    handleDragDistance = 0f
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .width(54.dp)
                            .height(5.dp)
                            .background(Color(0xFFD8C7B0), RoundedCornerShape(999.dp))
                    )
                }
                Text("Panel Details", color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    "Review the panel here, then open AR to place as many as needed.",
                    color = Color(0xFF667085),
                    fontSize = 12.sp
                )
                CatalogPreview(
                    product = selectedProduct,
                    mode = previewMode,
                    onModeChange = { previewMode = it }
                )
                SelectedPanelSummaryCard(
                    product = selectedProduct,
                    onAddToCart = {
                        if (selectedProduct.quantity > 0) {
                            onAddToCart(selectedProduct, 1)
                            cartToastMessage = "${selectedProduct.name} added to cart"
                            Toast.makeText(context, "${selectedProduct.name} added to cart", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "${selectedProduct.name} is out of stock", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onProceedToAr = {
                        showPanelDetails = false
                        onProceedToAr()
                    }
                )
                Spacer(Modifier.height(18.dp))
            }
                }
            }
        }
    }

    if (cartToastMessage != null) {
        Popup(alignment = Alignment.BottomCenter) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(999.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18794E))
                ) {
                    Row(
                        Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✓", color = Color.White, fontWeight = FontWeight.ExtraBold)
                        Text(
                            cartToastMessage.orEmpty(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogCard(product: Product, selected: Boolean, onClick: () -> Unit, onAddToCart: () -> Unit) {
    val context = LocalContext.current
    val imageRequest = remember(product.imageUrl) {
        ImageRequest.Builder(context)
            .data(product.imageUrl)
            .size(640)
            .crossfade(false)
            .build()
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        border = if (selected) BorderStroke(2.dp, Color(0xFFF77F2C)) else null,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 5.dp else 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (product.imageUrl != null) {
                CatalogPanelImage(
                    product = product,
                    imageRequest = imageRequest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                )
            } else if (product.modelUrl != null) {
                PanelSceneViewer(
                    modelUrl = product.modelUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Text(
                    "Interactive 3D preview · drag to rotate, pinch to zoom",
                    color = Color(0xFF667085),
                    fontSize = 10.sp
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFF77F2C), Color(0xFFFFB14A))
                            ),
                            RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Panel Image", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                if (product.quantity <= 0) "Out of Stock" else if (product.quantity <= 5) "Low Stock" else "In Stock",
                color = if (product.quantity <= 0) Color(0xFFB42318) else if (product.quantity <= 5) Color(0xFFA16207) else Color(0xFF12825B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            if (selected) {
                Text(
                    "Selected",
                    color = Color(0xFFF77F2C),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(product.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
            Text(product.material.uppercase(), color = Color(0xFF667085), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            Text(product.description, color = Color(0xFF667085), maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InfoChip("Price", formatPeso(product.price))
                InfoChip("Size", product.size)
            }
            Button(
                onClick = onAddToCart,
                enabled = product.quantity > 0,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (product.quantity > 0) "Add to cart" else "Out of stock")
            }
        }
    }
}

@Composable
private fun CatalogPanelImage(
    product: Product,
    imageRequest: ImageRequest,
    modifier: Modifier = Modifier
) {
    val grayEdgePanelNames = setOf(
        "LJS-102",
        "LJS-110",
        "LJS-121",
        "150-48",
        "PG-08",
        "PG-203",
        "150-00B",
        "160-23",
        "160-KB 02",
        "160-KB-02",
        "195-04",
        "195-07",
        "01",
        "Wall-01"
    )
    val hasPrintedEdge = product.name.trim() in grayEdgePanelNames ||
        product.imageUrl?.let { imageUrl ->
            grayEdgePanelNames.any { panelName -> imageUrl.contains(panelName, ignoreCase = true) }
        } == true ||
        product.imageUrl?.contains("/Wall-", ignoreCase = true) == true ||
        product.modelUrl?.contains("Wall Board", ignoreCase = true) == true
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF4EFE7), RoundedCornerShape(20.dp))
    ) {
        AsyncImage(
            model = customizationInventoryPreviewAsset(product.name) ?: imageRequest,
            contentDescription = product.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center
        )
    }
}

@Composable
private fun CatalogPreview(
    product: Product,
    mode: String,
    onModeChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4EFE7), RoundedCornerShape(14.dp))
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("2D", "3D").forEach { option ->
                TextButton(
                    onClick = { onModeChange(option) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (mode == option) Color.White else Color.Transparent,
                        contentColor = if (mode == option) Color(0xFFF77F2C) else Color(0xFF667085)
                    ),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text(option, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF4EFE7)),
            contentAlignment = Alignment.Center
        ) {
            if (mode == "3D" && product.modelUrl != null) {
                PanelSceneViewer(
                    modelUrl = product.modelUrl,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = "2D preview of ${product?.name ?: "panel"}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Text("Preview unavailable", color = Color(0xFF667085), fontSize = 12.sp)
            }
            if (mode == "3D" && product.modelUrl == null) {
                Text("3D model unavailable", color = Color(0xFF667085), fontSize = 12.sp)
            }
        }
        Text(
            if (mode == "3D") "Drag to rotate · pinch to zoom" else "2D product image",
            color = Color(0xFF667085),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun PanelSceneViewer(
    modelUrl: String,
    modifier: Modifier = Modifier,
    tintHex: String? = null,
    textureAsset: String? = null,
    pattern: String = "Horizontal",
    finish: String = "Matte",
    size: String = "2400 × 200mm",
    showModified: Boolean = true,
    transparentBackground: Boolean = false,
    showControls: Boolean = true,
    allowRotation: Boolean = true
) {
    val context = LocalContext.current
    // SceneView 3.3 uses the app's assets for model loading. The matching
    // GLBs are copied from the server's 3dPanels folder into assets/models.
    val assetPath = remember(modelUrl) {
        "models/" + modelUrl.substringAfter("/3dPanels/")
    }
    // Some exported panel groups use a different Blender forward axis. Turn
    // those groups to face the front camera instead of displaying edge-on.
    val modelBaseRotationY = remember(assetPath) {
        when {
            assetPath.contains("Wall Board", ignoreCase = true) -> 90.0f
            else -> 0.0f
        }
    }
    val isUvBoard = assetPath.contains("UV Board", ignoreCase = true)
    val sizeScale = when {
        size.contains("150", ignoreCase = true) -> 1.12f
        size.contains("300", ignoreCase = true) -> 0.88f
        else -> 1.0f
    }
    val finishRoughness = when (finish.lowercase()) {
        "gloss" -> 0.18f
        "satin" -> 0.42f
        else -> 0.78f
    }
    var modelScale by remember(modelUrl) { mutableStateOf(1.35f) }
    var modelRotation by remember(modelUrl) { mutableStateOf(0.0f) }
    val cameraManipulator = rememberCameraManipulator(
        // Start in front of the panel. The previous x-offset placed the
        // camera beside a flat GLB, making it appear as a thin edge.
        // Users can still orbit/zoom from this front-facing starting view.
        orbitHomePosition = if (isUvBoard) {
            // UV Board files were exported with their face oriented toward
            // the X side, so this is the customer-facing view for that group.
            Position(x = 2.6f, y = 0.35f, z = 3.2f)
        } else {
            Position(x = 0.0f, y = 0.35f, z = 4.0f)
        },
        targetPosition = Position()
    )

    Box(
        modifier = modifier
            .background(
                if (transparentBackground) Color.Transparent else Color(0xFF100D0B),
                RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
    ) {
        Scene(
            modifier = Modifier.fillMaxSize(),
            surfaceType = SurfaceType.TextureSurface,
            isOpaque = false,
            cameraManipulator = cameraManipulator
        ) {
            val appearanceTexture = textureAsset
                ?.takeIf { it.isNotBlank() }
                ?.let { asset ->
                    remember(engine, asset) {
                        runCatching {
                            ImageTexture.Builder()
                                .bitmap(context.assets, asset)
                                .build(engine)
                        }.getOrNull()
                    }
                }
            DisposableEffect(appearanceTexture, engine) {
                onDispose {
                    appearanceTexture?.let { texture ->
                        runCatching { engine.destroyTexture(texture) }
                    }
                }
            }
            Node(
                scale = Scale(modelScale * sizeScale),
                // Lower the model slightly so its visible bounds sit in the
                // center of the black preview rather than near the top.
                position = Position(y = -0.22f),
                rotation = Rotation(y = modelBaseRotationY + modelRotation)
            ) {
                rememberModelInstance(modelLoader, assetFileLocation = assetPath)?.let { instance ->
                    val modelNode = ModelNode(
                        modelInstance = instance,
                        scaleToUnits = 1.0f,
                        centerOrigin = Position(),
                        autoAnimate = false
                    )
                    tintHex?.let { hex ->
                        val tint: Int? = runCatching<Int> {
                            android.graphics.Color.parseColor("#${hex.removePrefix("#")}")
                        }.getOrNull()
                        if (tint != null || appearanceTexture != null) {
                            instance.renderableInstances.forEach { renderableInstance ->
                                val primitiveCount = instance.renderableManager.getPrimitiveCount(renderableInstance)
                                repeat(primitiveCount) { primitiveIndex ->
                                    instance.renderableManager
                                        .getMaterialInstanceAt(renderableInstance, primitiveIndex)
                                        .apply {
                                            appearanceTexture?.let { texture -> setBaseColorMap(texture) }
                                            tint?.let { setBaseColorFactor(colorOf(it)) }
                                            setRoughnessFactor(finishRoughness)
                                            setMetallicFactor(if (finish.equals("Gloss", ignoreCase = true)) 0.12f else 0.0f)
                                        }
                                }
                            }
                        }
                    }
                    modelNode
                }
            }
        }

        // Give the preview one clear gesture: horizontal drag rotates the
        // model. Vertical movement is deliberately left unconsumed so the
        // parent customization sheet can scroll instead of moving both at
        // once. This overlay also prevents SceneView's own orbit handler from
        // competing with the screen scroll gesture.
        if (allowRotation) Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(modelUrl) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        modelRotation = (modelRotation + dragAmount * 0.65f).mod(360.0f)
                    }
                }
        )

        if (showControls) Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFFF77F2C), CircleShape)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                "3D PREVIEW",
                color = Color(0xFFD9D0CA),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
        }
        if (showControls) Text(
            "3D PREVIEW",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            color = Color(0xFF6D625B),
            fontSize = 8.sp,
            letterSpacing = 0.8.sp
        )
        if (showControls) Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(12.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                onClick = { modelScale = (modelScale * 1.2f).coerceAtMost(4.0f) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(42.dp)
            ) { Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121826)) }
            TextButton(
                onClick = { modelScale = (modelScale / 1.2f).coerceAtLeast(0.35f) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(42.dp)
            ) { Text("−", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF121826)) }
            TextButton(
                onClick = { modelRotation = (modelRotation + 45.0f) % 360.0f },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(42.dp)
            ) { Text("↻", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF77F2C)) }
        }
    }
}

/** Camera-only AR fallback for phones that are not ARCore-certified. */
@Composable
private fun CameraOverlayPanelPreview(
    modelUrl: String,
    cameraController: LifecycleCameraController,
    imageUrl: String? = null,
    modifier: Modifier = Modifier,
    tintHex: String? = null,
    textureAsset: String? = null,
    finish: String = "Matte",
    size: String = "2400 × 200mm",
    dropRequest: ArDropRequest? = null,
    onDropHandled: () -> Unit = {},
    onStatusChanged: (String) -> Unit = {}
) {
    val placedPanels = remember(modelUrl) { mutableStateListOf<CameraPlacedPanel>() }
    var selectedPanelId by remember(modelUrl) { mutableStateOf<Int?>(null) }
    var readyPanelScale by remember(modelUrl) { mutableStateOf(1f) }
    var nextPanelId by remember(modelUrl) { mutableIntStateOf(1) }
    var draggingPanelId by remember { mutableStateOf<Int?>(null) }
    var previewSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    var previewOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    var wallWidthInput by rememberSaveable { mutableStateOf("3.0") }
    var wallHeightInput by rememberSaveable { mutableStateOf("2.4") }
    var draftWallWidth by rememberSaveable { mutableStateOf("3.0") }
    var draftWallHeight by rememberSaveable { mutableStateOf("2.4") }
    var showWallDimensions by rememberSaveable { mutableStateOf(false) }
    val wallWidthMm = wallWidthInput.toFloatOrNull()?.takeIf { it in 0.5f..30f }?.times(1000f) ?: 3000f
    val wallHeightMm = wallHeightInput.toFloatOrNull()?.takeIf { it in 0.5f..12f }?.times(1000f) ?: 2400f
    val panelDimensions = panelDimensionsMm(size) ?: (2400f to 200f)
    val previewPixelsPerMm = if (previewSize.width > 0 && previewSize.height > 0) {
        minOf(previewSize.width / wallWidthMm, previewSize.height / wallHeightMm)
    } else 0f
    val panelWidthPx = if (previewPixelsPerMm > 0f) {
        (panelDimensions.second * previewPixelsPerMm).coerceIn(48f, previewSize.width * 0.9f)
    } else 90f
    val panelHeightPx = if (previewPixelsPerMm > 0f) {
        (panelDimensions.first * previewPixelsPerMm).coerceIn(96f, previewSize.height * 0.86f)
    } else 280f
    val density = LocalDensity.current

    fun clampedPanelCenter(point: Offset, scale: Float = 1f): Offset {
        val halfWidth = panelWidthPx * scale / 2f
        val halfHeight = panelHeightPx * scale / 2f
        return Offset(
            (point.x - previewSize.width / 2f).coerceIn(
                -previewSize.width / 2f + halfWidth,
                previewSize.width / 2f - halfWidth
            ),
            (point.y - previewSize.height / 2f).coerceIn(
                -previewSize.height / 2f + halfHeight,
                previewSize.height / 2f - halfHeight
            )
        )
    }

    fun addPanelAt(point: Offset, scale: Float = readyPanelScale): Int {
        val id = nextPanelId++
        val safeScale = scale.coerceIn(0.25f, 4f)
        placedPanels += CameraPlacedPanel(id, clampedPanelCenter(point, safeScale), safeScale)
        selectedPanelId = id
        return id
    }

    LaunchedEffect(dropRequest?.token, previewOriginInRoot) {
        val request = dropRequest ?: return@LaunchedEffect
        if (previewSize == androidx.compose.ui.unit.IntSize.Zero) return@LaunchedEffect
        val localPoint = request.rootPoint - previewOriginInRoot
        addPanelAt(localPoint)
        onStatusChanged("Panel placed. Select it and use + or − to resize.")
        onDropHandled()
    }

    Box(
        modifier = modifier
            .background(Color.Black)
            .onGloballyPositioned {
                previewSize = it.size
                previewOriginInRoot = it.positionInRoot()
            }
    ) {
        CameraPreview(cameraController = cameraController, modifier = Modifier.fillMaxSize())
        placedPanels.forEach { panel ->
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(
                        width = with(density) { (panelWidthPx * panel.scale).toDp() },
                        height = with(density) { (panelHeightPx * panel.scale).toDp() }
                    )
                    .graphicsLayer {
                        translationX = panel.center.x
                        translationY = panel.center.y
                    }
                    .border(
                        width = if (panel.id == selectedPanelId) 2.dp else 0.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(2.dp)
                    )
            ) {
                imageUrl?.let { previewUrl ->
                    AsyncImage(
                        model = previewUrl,
                        contentDescription = "Placed panel overlay",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds,
                        alpha = 0.88f
                    )
                }
            }
        }
        // Tapping a clear area places the ready panel; dragging an existing
        // panel moves it, and dragging on a clear area places while dragging.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(modelUrl, previewSize, panelWidthPx, panelHeightPx) {
                    detectDragGestures(
                        onDragStart = { start ->
                            val existing = placedPanels.lastOrNull { panel ->
                                kotlin.math.abs(start.x - (previewSize.width / 2f + panel.center.x)) <= panelWidthPx * panel.scale / 2f &&
                                    kotlin.math.abs(start.y - (previewSize.height / 2f + panel.center.y)) <= panelHeightPx * panel.scale / 2f
                            }
                            draggingPanelId = existing?.id ?: addPanelAt(start)
                            selectedPanelId = draggingPanelId
                        },
                        onDrag = { change, amount ->
                            change.consume()
                            val index = placedPanels.indexOfFirst { it.id == draggingPanelId }
                            if (index >= 0) {
                                placedPanels[index] = placedPanels[index].copy(
                                    center = clampedPanelCenter(change.position, placedPanels[index].scale)
                                )
                            }
                        },
                        onDragEnd = {
                            draggingPanelId = null
                            onStatusChanged("Panel positioned. Select it and use + or − to resize.")
                        },
                        onDragCancel = { draggingPanelId = null }
                    )
                }
                .pointerInput(modelUrl, previewSize, panelWidthPx, panelHeightPx) {
                    detectTapGestures(
                        onTap = { tapPosition ->
                            val existing = placedPanels.lastOrNull { panel ->
                                kotlin.math.abs(tapPosition.x - (previewSize.width / 2f + panel.center.x)) <= panelWidthPx * panel.scale / 2f &&
                                    kotlin.math.abs(tapPosition.y - (previewSize.height / 2f + panel.center.y)) <= panelHeightPx * panel.scale / 2f
                            }
                            if (existing != null) {
                                selectedPanelId = existing.id
                                onStatusChanged("Panel selected. Use + or − to resize it, or drag to move it.")
                            } else if (previewSize != androidx.compose.ui.unit.IntSize.Zero) {
                                addPanelAt(tapPosition)
                                onStatusChanged("Panel ${placedPanels.size} placed. Tap another clear area to add one.")
                            }
                        }
                    )
                }
        )
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 76.dp, end = 12.dp)
                .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(12.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(
                onClick = {
                    val index = placedPanels.indexOfFirst { it.id == selectedPanelId }
                    if (index >= 0) placedPanels[index] = placedPanels[index].copy(scale = (placedPanels[index].scale * 1.15f).coerceAtMost(4f))
                    else readyPanelScale = (readyPanelScale * 1.15f).coerceAtMost(4f)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) { Text("+", fontSize = 16.sp, color = Color(0xFF121826)) }
            TextButton(
                onClick = {
                    val index = placedPanels.indexOfFirst { it.id == selectedPanelId }
                    if (index >= 0) placedPanels[index] = placedPanels[index].copy(scale = (placedPanels[index].scale / 1.15f).coerceAtLeast(0.25f))
                    else readyPanelScale = (readyPanelScale / 1.15f).coerceAtLeast(0.25f)
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) { Text("−", fontSize = 16.sp, color = Color(0xFF121826)) }
            TextButton(
                onClick = {
                    draftWallWidth = wallWidthInput
                    draftWallHeight = wallHeightInput
                    showWallDimensions = true
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
            ) { Text("Wall size", fontSize = 11.sp, color = Color(0xFF121826)) }
            if (selectedPanelId != null) {
                TextButton(
                    onClick = {
                        placedPanels.removeAll { it.id == selectedPanelId }
                        selectedPanelId = null
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) { Text("Delete", fontSize = 10.sp, color = Color(0xFFB42318)) }
            }
            TextButton(
                onClick = {
                    placedPanels.clear()
                    selectedPanelId = null
                },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(54.dp)
            ) { Text("Reset", fontSize = 11.sp, color = Color(0xFF121826)) }
        }
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 18.dp),
            shape = RoundedCornerShape(999.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.65f))
        ) {
            Text(
                "${placedPanels.size} placed · drag to move · + / − resizes",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                color = Color.White,
                fontSize = 12.sp
            )
        }
        if (showWallDimensions) {
            AlertDialog(
                onDismissRequest = { showWallDimensions = false },
                title = { Text("Measure the wall") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Enter the real wall dimensions to scale this panel preview.",
                            color = Color(0xFF667085)
                        )
                        OutlinedTextField(
                            value = draftWallWidth,
                            onValueChange = { value -> draftWallWidth = value.filter { it.isDigit() || it == '.' }.take(5) },
                            label = { Text("Wall width (m)") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = draftWallHeight,
                            onValueChange = { value -> draftWallHeight = value.filter { it.isDigit() || it == '.' }.take(5) },
                            label = { Text("Wall height (m)") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val width = draftWallWidth.toFloatOrNull()
                        val height = draftWallHeight.toFloatOrNull()
                        if (width != null && width in 0.5f..30f && height != null && height in 0.5f..12f) {
                            wallWidthInput = width.toString()
                            wallHeightInput = height.toString()
                            onStatusChanged("Panel preview scaled for a ${width} m × ${height} m wall.")
                            showWallDimensions = false
                        }
                    }) { Text("Apply") }
                },
                dismissButton = {
                    TextButton(onClick = { showWallDimensions = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
private fun PanelModelViewer(
    modelUrl: String,
    modifier: Modifier = Modifier,
    tintHex: String? = null
) {
    // Keep the server URL as the document base and encode the filename for
    // WebView. GLB paths contain spaces and parentheses in several folders.
    val encodedModelUrl = modelUrl
        .replace("%", "%25")
        .replace(" ", "%20")
        .replace("(", "%28")
        .replace(")", "%29")
    // Use a normal HTTP document for the viewer. Android WebView can reject
    // module scripts/WebGL when the document is loaded with loadData().
    val viewerBaseUrl = modelUrl.substringBefore("/3dPanels")
        .ifBlank { modelUrl.substringBeforeLast("/") }
        .trimEnd('/')
    val viewerPageUrl = "$viewerBaseUrl/model-viewer?model=" +
        java.net.URLEncoder.encode(modelUrl, "UTF-8")
    val html = remember(modelUrl, tintHex) {
        val safeTint = tintHex?.trim()?.removePrefix("#")?.takeIf { it.matches(Regex("[0-9a-fA-F]{6}")) }
        val tintScript = safeTint?.let {
            """
            const tint = "#$it";
            function applyTint() {
              const value = tint.substring(1);
              const color = [0, 2, 4].map(index => parseInt(value.substring(index, index + 2), 16) / 255);
              if (viewer.model && viewer.model.materials) {
                viewer.model.materials.forEach(material => {
                  material.pbrMetallicRoughness.setBaseColorFactor([...color, 1]);
                });
              }
            }
            viewer.addEventListener("load", applyTint);
            """.trimIndent()
        } ?: ""
        """
        <!doctype html>
        <html><head><meta name="viewport" content="width=device-width, initial-scale=1" />
        <style>
          html,body { margin:0; width:100%; height:100%; overflow:hidden; background:#F4EFE7; }
          canvas { width:100%; height:100%; display:block; touch-action:none; }
        </style></head>
        <body><canvas id="panel"></canvas>
        <script>
          const canvas = document.getElementById("panel");
          const ctx = canvas.getContext("2d");
          let angle = -0.18, zoom = 1, drag = false, lastX = 0, lastY = 0;
          function resize() { canvas.width = innerWidth * devicePixelRatio; canvas.height = innerHeight * devicePixelRatio; draw(); }
          function draw() {
            const w = canvas.width, h = canvas.height, d = devicePixelRatio;
            ctx.clearRect(0, 0, w, h); ctx.save(); ctx.scale(d, d);
            const cw = innerWidth, ch = innerHeight, pw = Math.min(cw * .72, 270) * zoom;
            const ph = pw * .32, x = cw / 2, y = ch / 2 + 4;
            const skew = Math.sin(angle) * pw * .16, depth = Math.max(5, pw * .035);
            const front = [[x-pw/2+skew,y-ph/2],[x+pw/2+skew,y-ph/2],[x+pw/2-skew,y+ph/2],[x-pw/2-skew,y+ph/2]];
            ctx.fillStyle = "#7A4A2A";
            ctx.beginPath(); ctx.moveTo(front[0][0],front[0][1]); front.slice(1).forEach(p=>ctx.lineTo(p[0],p[1])); ctx.closePath(); ctx.fill();
            ctx.fillStyle = "#9A6A3F";
            ctx.beginPath(); ctx.moveTo(front[0][0],front[0][1]); ctx.lineTo(front[1][0],front[1][1]); ctx.lineTo(front[1][0]-depth,front[1][1]-depth); ctx.lineTo(front[0][0]-depth,front[0][1]-depth); ctx.closePath(); ctx.fill();
            ctx.fillStyle = "#633919";
            ctx.beginPath(); ctx.moveTo(front[1][0],front[1][1]); ctx.lineTo(front[2][0],front[2][1]); ctx.lineTo(front[2][0]-depth,front[2][1]-depth); ctx.lineTo(front[1][0]-depth,front[1][1]-depth); ctx.closePath(); ctx.fill();
            ctx.strokeStyle = "rgba(35,20,12,.5)"; ctx.lineWidth = Math.max(1, 1.3*d);
            for (let i=1;i<8;i++) { const t=i/8, tx=front[0][0]*(1-t)+front[1][0]*t, ty=front[0][1]*(1-t)+front[1][1]*t; const bx=front[3][0]*(1-t)+front[2][0]*t, by=front[3][1]*(1-t)+front[2][1]*t; ctx.beginPath(); ctx.moveTo(tx,ty); ctx.lineTo(bx,by); ctx.stroke(); }
            ctx.restore();
          }
          addEventListener("resize", resize);
          canvas.addEventListener("pointerdown", e => { drag=true; lastX=e.clientX; lastY=e.clientY; canvas.setPointerCapture(e.pointerId); });
          canvas.addEventListener("pointermove", e => { if(!drag)return; angle += (e.clientX-lastX)*.008; zoom=Math.max(.65,Math.min(1.5,zoom+(lastY-e.clientY)*.003)); lastX=e.clientX; lastY=e.clientY; draw(); });
          canvas.addEventListener("pointerup", () => drag=false);
          canvas.addEventListener("wheel", e => { e.preventDefault(); zoom=Math.max(.65,Math.min(1.5,zoom-e.deltaY*.001)); draw(); }, {passive:false});
          resize();
        </script></body></html>
        """.trimIndent()
    }
    Box(modifier = modifier.clip(RoundedCornerShape(20.dp))) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    setBackgroundColor(android.graphics.Color.rgb(244, 239, 231))
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.allowFileAccess = false
                    settings.allowContentAccess = false
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.loadWithOverviewMode = true
                    settings.useWideViewPort = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, request: android.webkit.WebResourceRequest): Boolean = false
                        override fun onReceivedError(view: WebView, request: android.webkit.WebResourceRequest, error: android.webkit.WebResourceError) {
                            Log.e("Aiyige3D", "WebView error ${error.errorCode}: ${error.description} url=${request.url}")
                        }
                    }
                    webChromeClient = object : android.webkit.WebChromeClient() {
                        override fun onConsoleMessage(message: android.webkit.ConsoleMessage): Boolean {
                            Log.e("Aiyige3D", "JS ${message.messageLevel()}: ${message.message()} @${message.sourceId()}:${message.lineNumber()}")
                            return true
                        }
                    }
                    tag = modelUrl
                    Log.d("Aiyige3D", "Loading viewer $viewerPageUrl model=$modelUrl")
                    loadUrl(viewerPageUrl)
                }
            },
            update = { webView ->
                if (webView.tag != modelUrl) {
                    webView.tag = modelUrl
                    webView.loadUrl(viewerPageUrl)
                }
            }
        )
    }
}

@Composable
private fun Panel3DFallback(tintHex: String?, modifier: Modifier = Modifier) {
    val panelColor = when (tintHex?.removePrefix("#")?.uppercase()) {
        "7A3E25" -> Color(0xFF7A3E25)
        "737879" -> Color(0xFF737879)
        "D8D4C8" -> Color(0xFFD8D4C8)
        "242323" -> Color(0xFF242323)
        else -> Color(0xFF9A6A3F)
    }
    Box(modifier = modifier.background(Brush.linearGradient(listOf(Color(0xFFF4EFE7), Color(0xFFE7DED2))))) {
        Canvas(Modifier.fillMaxSize()) {
            val left = size.width * .13f
            val right = size.width * .88f
            val top = size.height * .19f
            val bottom = size.height * .82f
            val depth = size.width * .035f
            val topFace = androidx.compose.ui.graphics.Path().apply {
                moveTo(left, top)
                lineTo(right, top)
                lineTo(right - depth, top - depth)
                lineTo(left - depth, top - depth)
                close()
            }
            val sideFace = androidx.compose.ui.graphics.Path().apply {
                moveTo(right, top)
                lineTo(right, bottom)
                lineTo(right - depth, bottom - depth)
                lineTo(right - depth, top - depth)
                close()
            }
            drawPath(topFace, Color(0xFFB27A4A))
            drawPath(sideFace, Color(0xFF633919))
            drawRoundRect(panelColor, topLeft = Offset(left, top), size = androidx.compose.ui.geometry.Size(right - left, bottom - top), cornerRadius = androidx.compose.ui.geometry.CornerRadius(18f, 18f))
            for (index in 0..7) {
                val x = left + (right - left) * (index + 0.5f) / 8f
                drawLine(Color.Black.copy(alpha = .28f), Offset(x, top + 8f), Offset(x - size.width * .04f, bottom - 8f), strokeWidth = 5f)
                drawLine(Color.White.copy(alpha = .17f), Offset(x + 5f, top + 8f), Offset(x - size.width * .04f + 5f, bottom - 8f), strokeWidth = 2f)
            }
            drawLine(Color.White.copy(alpha = .35f), Offset(left + 8f, top + 12f), Offset(right - 8f, top + 12f), strokeWidth = 3f)
            drawLine(Color.Black.copy(alpha = .35f), Offset(left + 8f, bottom - 10f), Offset(right - 8f, bottom - 10f), strokeWidth = 4f)
        }
        Text("3D PREVIEW", color = Color.White.copy(alpha = .85f), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopEnd).padding(12.dp))
    }
}

@Composable
private fun SelectedPanelSummaryCard(
    product: Product,
    onAddToCart: () -> Unit,
    onProceedToAr: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Panel Details", color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(product.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
            Text(product.description, color = Color(0xFF667085), fontSize = 14.sp, lineHeight = 21.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                InfoChip("Unit Price", formatPeso(product.price))
                InfoChip("Size", product.size)
                InfoChip("Stock", product.quantity.toString())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onAddToCart,
                    enabled = product.quantity > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                    modifier = Modifier.weight(1f)
                ) { Text("Add to cart", fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                OutlinedButton(onClick = onProceedToAr, modifier = Modifier.weight(1f)) { Text("Try in AR", fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun QuantityStepper(
    quantity: Int,
    onQuantityChange: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = { onQuantityChange(maxOf(1, quantity - 1)) }
        ) {
            Text("-")
        }
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Panels needed", color = Color(0xFF667085), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                Text(quantity.toString(), color = Color(0xFF121826), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        OutlinedButton(
            onClick = { onQuantityChange(quantity + 1) }
        ) {
            Text("+")
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF667085), fontSize = 12.sp)
        Text(value, color = Color(0xFF121826), fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun ArScreen() {
    val context = LocalContext.current
    var hasCameraPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("AR Camera", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
            Text(
                "Camera is ready. AR coming soon.",
                color = Color(0xFF667085),
                lineHeight = 22.sp
            )

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF121826))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    if (hasCameraPermission) {
                        CameraPreview(
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xAA121826)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "AR coming soon",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                            Text(
                                "Preview is on for scanning.",
                                color = Color(0xFFFFD29C),
                                lineHeight = 20.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Camera permission needed", color = Color.White, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C))
                            ) {
                                Text("Enable Camera")
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Markerless AR placement for the currently selected panel. */
@Composable
private fun ArPanelPreview(
    modelUrl: String,
    modifier: Modifier = Modifier,
    tintHex: String? = null,
    finish: String = "Matte",
    textureAsset: String? = null,
    size: String = "2400 × 200mm",
    dropRequest: ArDropRequest? = null,
    onDropHandled: () -> Unit = {},
    onPanelPlaced: () -> Unit = {},
    onStatusChanged: (String) -> Unit,
    onSessionFailed: (Exception) -> Unit
) {
    val context = LocalContext.current
    // Keep placements independent from the currently selected carousel item.
    // Selecting another panel must not remove or replace panels already placed.
    val placedPanels = remember { mutableStateListOf<PlacedArPanel>() }
    val latestFrame = remember { java.util.concurrent.atomic.AtomicReference<com.google.ar.core.Frame?>(null) }
    val lastTrackingMessage = remember { java.util.concurrent.atomic.AtomicReference<String?>(null) }
    var sceneOriginInRoot by remember { mutableStateOf(Offset.Zero) }
    var assetPath by remember(modelUrl) {
        mutableStateOf(
            if (modelUrl.contains("/3dPanels/")) "models/" + modelUrl.substringAfter("/3dPanels/") else ""
        )
    }

    LaunchedEffect(modelUrl) {
        if (!modelUrl.contains("/3dPanels/")) {
            onStatusChanged("Loading the panel's 3D model...")
            assetPath = cacheRemoteModel(context, modelUrl).orEmpty()
            if (assetPath.isBlank()) onStatusChanged("This panel's 3D model could not be loaded.")
        }
    }

    fun placeAt(x: Float, y: Float): Boolean {
        if (assetPath.isBlank()) {
            onStatusChanged("The panel model is still loading. Please try again in a moment.")
            return false
        }
        val frame = latestFrame.get()
        val hit = frame
            ?.hitTest(x, y)
            ?.firstOrNull { result ->
                val plane = result.trackable as? Plane
                plane != null &&
                    plane.trackingState == TrackingState.TRACKING &&
                    // A ceiling is a downward-facing horizontal plane. The
                    // previous filter only accepted floors, so ceiling
                    // placement could never succeed.
                    (plane.type == Plane.Type.VERTICAL ||
                        plane.type == Plane.Type.HORIZONTAL_UPWARD_FACING ||
                        plane.type == Plane.Type.HORIZONTAL_DOWNWARD_FACING)
            }
        if (hit == null) {
            onStatusChanged(
                when {
                    frame == null -> "Scanning the room... keep the camera moving slowly."
                    frame.camera.trackingState != TrackingState.TRACKING ->
                        "Tracking is limited. Point at a textured wall and move slowly."
                    else -> "No detected surface at that point. Aim at a wall or ceiling and try again."
                }
            )
            return false
        }
        return runCatching { hit.createAnchor() }.getOrNull()?.let { placedAnchor ->
            placedPanels += PlacedArPanel(
                anchor = placedAnchor,
                assetPath = assetPath,
                tintHex = tintHex.orEmpty(),
                finish = finish,
                textureAsset = textureAsset.orEmpty(),
                sizeScale = customizationSizeScale(size)
            )
            onStatusChanged("Panel ${placedPanels.size} placed. Hold the ready panel to add another.")
            onPanelPlaced()
            true
        } ?: run {
            onStatusChanged("The detected surface was lost. Move the camera slowly and try again.")
            false
        }
    }

    LaunchedEffect(dropRequest?.token, sceneOriginInRoot) {
        val request = dropRequest ?: return@LaunchedEffect
        if (sceneOriginInRoot == Offset.Zero) return@LaunchedEffect
        val placed = placeAt(request.rootPoint.x - sceneOriginInRoot.x, request.rootPoint.y - sceneOriginInRoot.y)
        if (placed) onDropHandled()
    }

    Box(modifier = modifier.background(Color.Black).onGloballyPositioned { coordinates ->
        sceneOriginInRoot = coordinates.positionInRoot()
    }) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            planeRenderer = true,
            sessionConfiguration = { _, config ->
                config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            },
            onSessionUpdated = { _, frame ->
                latestFrame.set(frame)
                val message = when {
                    frame.camera.trackingState != TrackingState.TRACKING ->
                        "Tracking the room... point at a textured wall and move slowly."
                    frame.getUpdatedTrackables(Plane::class.java).isNotEmpty() ->
                        "Surface detected. Tap a wall or ceiling to place the panel."
                    else -> "Scanning the room... move slowly to detect walls and ceilings."
                }
                if (lastTrackingMessage.getAndSet(message) != message) onStatusChanged(message)
            },
            onSessionFailed = { exception ->
                onSessionFailed(exception)
            },
            onTouchEvent = { event, _ ->
                if (event.actionMasked != MotionEvent.ACTION_UP) {
                    false
                } else {
                    placeAt(event.x, event.y)
                }
            }
        ) {
            placedPanels.forEach { placedPanel ->
                val panelModel = rememberModelInstance(
                    modelLoader,
                    assetFileLocation = placedPanel.assetPath
                )
                AnchorNode(anchor = placedPanel.anchor) {
                    panelModel?.let { instance ->
                        val tint = placedPanel.tintHex.takeIf { it.isNotBlank() }?.let {
                            runCatching { android.graphics.Color.parseColor("#${it.removePrefix("#")}") }.getOrNull()
                        }
                        val appearanceTexture = placedPanel.textureAsset
                            .takeIf { it.isNotBlank() }
                            ?.let { asset ->
                                runCatching {
                                    ImageTexture.Builder()
                                        .bitmap(context.assets, asset)
                                        .build(engine)
                                }.getOrNull()
                            }
                        if (tint != null || appearanceTexture != null) {
                            instance.renderableInstances.forEach { renderableInstance ->
                                val primitiveCount = instance.renderableManager.getPrimitiveCount(renderableInstance)
                                repeat(primitiveCount) { primitiveIndex ->
                                    instance.renderableManager
                                        .getMaterialInstanceAt(renderableInstance, primitiveIndex)
                                        .apply {
                                            appearanceTexture?.let { texture -> setBaseColorMap(texture) }
                                            tint?.let { setBaseColorFactor(colorOf(it)) }
                                            setRoughnessFactor(
                                                when (placedPanel.finish.lowercase(Locale.ROOT)) {
                                                    "gloss" -> 0.18f
                                                    "satin" -> 0.42f
                                                    else -> 0.78f
                                                }
                                            )
                                            setMetallicFactor(if (placedPanel.finish.equals("Gloss", ignoreCase = true)) 0.12f else 0.0f)
                                        }
                                }
                            }
                        }
                        Node(scale = Scale(placedPanel.sizeScale)) {
                            ModelNode(modelInstance = instance, scaleToUnits = 1.0f)
                        }
                    }
                }
            }
        }

        if (placedPanels.isEmpty()) {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(999.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.55f))
            ) {
                Text(
                    "Move slowly, then hold the ready panel and release it on a detected wall or ceiling",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 18.dp),
                shape = RoundedCornerShape(999.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.55f))
            ) {
                Text(
                    "${placedPanels.size} panel${if (placedPanels.size == 1) "" else "s"} placed · Hold the ready panel to add another",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun RefinedArScreen(
    modifier: Modifier = Modifier,
    selectedProduct: Product?,
    panelOptions: List<Product>,
    isSignedIn: Boolean,
    finish: String,
    tintHex: String,
    textureAsset: String,
    pattern: String,
    size: String,
    quantity: String,
    notes: String,
    customerName: String,
    streetAddress: String,
    cityProvince: String,
    postalCode: String,
    contactNumber: String,
    emailAddress: String,
    latestRoomCaptureUrl: String?,
    latestRoomCaptureLabel: String,
    onOpenCatalog: () -> Unit,
    onRequireSignIn: () -> Unit,
    onRoomCaptureSaved: (String?, String) -> Unit,
    onQuantityChange: (Int) -> Unit,
    onPanelSelected: (Int) -> Unit,
    onBackToCatalog: () -> Unit,
    onSaveToQuote: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val selectedQuantity = quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val summaryTotals = selectedProduct?.let { calculatePanelTotals(it.price, selectedQuantity) }
    val hasSelectedPanel = selectedProduct != null
    val readyPanel = panelOptions.firstOrNull { it.id == selectedProduct?.id } ?: panelOptions.firstOrNull()
    val hasCapturedRoom = latestRoomCaptureUrl != null
    var hasCameraPermission by rememberSaveable {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var captureState by rememberSaveable {
        mutableStateOf("Camera preview is ready. Capture the room to attach it to the quote.")
    }
    var isCapturing by rememberSaveable { mutableStateOf(false) }
    var showArMode by rememberSaveable { mutableStateOf(false) }
    var arCameraReleased by remember { mutableStateOf(false) }
    var arInstallRequested by rememberSaveable { mutableStateOf(false) }
    var useCameraArFallback by rememberSaveable { mutableStateOf(false) }
    var useEasyArMode by rememberSaveable { mutableStateOf(false) }
    var flashEnabled by rememberSaveable { mutableStateOf(false) }
    var zoomPreset by rememberSaveable { mutableStateOf(1f) }
    var showLatestCaptureFullscreen by rememberSaveable { mutableStateOf(false) }
    var panelSelectorOrigin by remember { mutableStateOf(Offset.Zero) }
    var panelDragOffset by remember { mutableStateOf(Offset.Zero) }
    var dropRequest by remember { mutableStateOf<ArDropRequest?>(null) }
    var carouselDirection by rememberSaveable { mutableIntStateOf(1) }
    val shutterPulseTransition = rememberInfiniteTransition(label = "shutterPulse")
    val shutterPulse by shutterPulseTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(850),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shutterPulseScale"
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) captureState = "Camera permission granted. You can capture the room now."
        else captureState = "Camera permission is required to capture a room reference."
    }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    fun applyFlashMode(enabled: Boolean) {
        val flashMode = if (enabled) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        runCatching {
            cameraController.javaClass
                .getMethod("setImageCaptureFlashMode", Int::class.javaPrimitiveType)
                .invoke(cameraController, flashMode)
        }
    }

    fun applyZoom(ratio: Float) {
        zoomPreset = ratio
        runCatching { cameraController.setZoomRatio(ratio) }
    }

    fun openArMode() {
        if (selectedProduct?.modelUrl == null) {
            captureState = "Select a panel with a 3D model before opening AR."
            return
        }
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        val activity = context as? Activity
        if (activity == null) {
            captureState = "AR is unavailable in this screen."
            return
        }
        runCatching {
            val availability = ArCoreApk.getInstance().checkAvailability(context)
            if (!availability.isSupported) {
                val licenseKey = BuildConfig.EASYAR_LICENSE_KEY
                if (licenseKey.isBlank()) {
                    useEasyArMode = false
                    useCameraArFallback = true
                    captureState = "ARCore is unavailable and EasyAR is not configured. Add easyar.licenseKey to local.properties to enable room scanning."
                    return@runCatching
                }
                if (!EasyArEngine.initialize(activity, licenseKey)) {
                    useEasyArMode = false
                    useCameraArFallback = true
                    captureState = "EasyAR could not initialize: ${EasyArEngine.errorMessage()}"
                    return@runCatching
                }
                if (!EasyArMotionTrackerCameraDevice.isAvailable()) {
                    useEasyArMode = false
                    useCameraArFallback = true
                    captureState = "EasyAR motion tracking is unavailable on this device."
                    return@runCatching
                }
                useEasyArMode = true
                useCameraArFallback = false
                showArMode = false
                captureState = "EasyAR is starting. Move slowly to scan walls and ceilings."
                return@runCatching
            }
            when (ArCoreApk.getInstance().requestInstall(activity, true)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    useEasyArMode = false
                    arInstallRequested = true
                    useCameraArFallback = true
                    captureState = "Please finish installing Google Play Services for AR. Scanning will start automatically afterward."
                }
                ArCoreApk.InstallStatus.INSTALLED -> {
                    useEasyArMode = false
                    arInstallRequested = false
                    useCameraArFallback = false
                    showArMode = true
                    captureState = "Move slowly, then tap a detected wall or ceiling to add a panel."
                }
            }
        }.onFailure { error ->
            arInstallRequested = false
            useEasyArMode = false
            useCameraArFallback = true
            captureState = "AR could not start safely: ${error.message ?: "ARCore is unavailable."}"
            showArMode = false
        }
    }

    DisposableEffect(lifecycleOwner, arInstallRequested, hasCameraPermission, selectedProduct?.modelUrl) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && arInstallRequested) {
                arInstallRequested = false
                openArMode()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(lifecycleOwner, showArMode, useEasyArMode) {
        // ARCore and CameraX must not compete for the camera. Release the
        // CameraX use cases before creating an ARCore session.
        if (showArMode || useEasyArMode) {
            cameraController.unbind()
            arCameraReleased = true
        } else {
            arCameraReleased = false
            cameraController.bindToLifecycle(lifecycleOwner)
            applyFlashMode(flashEnabled)
            applyZoom(zoomPreset)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // AR is the default experience for this screen. Start scanning as soon
    // as permission and a 3D panel are available; users no longer need to
    // press a separate Try AR button.
    LaunchedEffect(hasCameraPermission, selectedProduct?.modelUrl) {
        if (hasCameraPermission && selectedProduct?.modelUrl != null && !showArMode) {
            openArMode()
        }
    }

    LaunchedEffect(flashEnabled) {
        applyFlashMode(flashEnabled)
    }

    LaunchedEffect(zoomPreset) {
        applyZoom(zoomPreset)
    }

    fun captureRoom() {
        if (!isSignedIn) {
            onRequireSignIn()
            return
        }
        if (selectedProduct == null) {
            captureState = "Select a panel before capturing the room."
            return
        }
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        if (isCapturing) return

        isCapturing = true
        captureState = "Saving room capture..."

        val captureFile = File(context.cacheDir, "room-capture-${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(captureFile).build()
        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    scope.launch {
                        // Keep the camera result usable even when the backend is
                        // offline. The previous flow only exposed a capture after
                        // upload succeeded, making a successful local photo look
                        // like it had not been captured.
                        val localCaptureUrl = Uri.fromFile(captureFile).toString()
                        val savedUrl = try {
                            saveRoomCaptureToBackend(
                                context = context,
                                selectedProduct = selectedProduct,
                                customerName = customerName,
                                streetAddress = streetAddress,
                                cityProvince = cityProvince,
                                postalCode = postalCode,
                                contactNumber = contactNumber,
                                emailAddress = emailAddress,
                                finish = finish,
                                size = size,
                                quantity = quantity,
                                notes = notes
                                    .ifBlank { "Room capture attached from customer AR preview." }
                                    .plus("\nSaved for quotation reference."),
                                file = captureFile,
                                discountPercent = 0
                            )
                        } catch (error: Exception) {
                            Log.w("AiyigeCustomer", "Unable to save room capture.", error)
                            null
                        }

                        if (savedUrl != null) {
                            onRoomCaptureSaved(savedUrl, "Saved at ${formatCaptureTimestamp()}")
                            captureState = "Room captured. You can proceed or retake."
                            captureFile.delete()
                        } else {
                            onRoomCaptureSaved(localCaptureUrl, "Captured locally at ${formatCaptureTimestamp()}")
                            captureState = "Room captured locally. Backend upload is unavailable."
                        }
                        isCapturing = false
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    scope.launch {
                        captureState = "Capture failed: ${exception.message ?: "Unknown camera error."}"
                        isCapturing = false
                    }
                }
            }
        )
    }

    Box(modifier = modifier) {
        if (hasCameraPermission) {
            if (showArMode && arCameraReleased && selectedProduct?.modelUrl != null) {
                ArPanelPreview(
                    modelUrl = selectedProduct.modelUrl,
                    modifier = Modifier.fillMaxSize(),
                    tintHex = tintHex,
                    textureAsset = textureAsset,
                    finish = finish,
                    size = size,
                    dropRequest = dropRequest,
                    onDropHandled = { dropRequest = null },
                    onPanelPlaced = {
                        captureState = "${selectedProduct?.name ?: "Panel"} placed with the selected customization."
                    },
                    onStatusChanged = { captureState = it },
                    onSessionFailed = { exception ->
                        showArMode = false
                        captureState = "AR could not start: ${exception.message ?: "This device may not support ARCore."}"
                    }
                )
            } else if (useEasyArMode && arCameraReleased && selectedProduct != null) {
                EasyArPanelPreview(
                    imageUrl = selectedProduct.imageUrl
                        ?: customizationInventoryPreviewAsset(selectedProduct.name),
                    size = size,
                    dropRequest = dropRequest,
                    onDropHandled = { dropRequest = null },
                    onStatusChanged = { captureState = it },
                    modifier = Modifier.fillMaxSize()
                )
            } else if (useCameraArFallback && selectedProduct?.modelUrl != null) {
                CameraOverlayPanelPreview(
                    modelUrl = selectedProduct.modelUrl,
                    cameraController = cameraController,
                    imageUrl = selectedProduct.imageUrl
                        ?: customizationInventoryPreviewAsset(selectedProduct.name),
                    modifier = Modifier.fillMaxSize(),
                    tintHex = tintHex,
                    textureAsset = textureAsset,
                    finish = finish,
                    size = size,
                    dropRequest = dropRequest,
                    onDropHandled = { dropRequest = null },
                    onStatusChanged = { captureState = it }
                )
            } else {
                CameraPreview(
                    cameraController = cameraController,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isCapturing) 0.82f else 0.9f)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0x55000000), Color.Transparent, Color(0xDD121826))
                        )
                    )
            )

            Card(
                shape = RoundedCornerShape(999.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.22f)),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 14.dp)
            ) {
                Column(
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "AR Camera",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp
                    )
                    Text(
                        when {
                            showArMode -> "Markerless AR placement"
                            useCameraArFallback -> "Camera AR preview"
                            else -> "Camera preview"
                        },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (selectedProduct != null) {
                        Text(
                            "${selectedProduct.name} · $size · $finish · $pattern",
                            color = Color.White.copy(alpha = 0.86f),
                            fontSize = 9.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBackToCatalog,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.35f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Back", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { flashEnabled = !flashEnabled },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.35f),
                    contentColor = Color.White
                ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (flashEnabled) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (flashEnabled) "Flash on" else "Flash off", fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
                if (latestRoomCaptureUrl != null) {
                    AsyncImage(
                        model = latestRoomCaptureUrl,
                        contentDescription = "Latest capture thumbnail",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .clickable { showLatestCaptureFullscreen = true }
                    )
                }
            }

            // The ready panel is intentionally separate from the shutter. A
            // customer selects a panel, holds it, drags it onto the detected
            // room surface, and releases it to place one instance. The same
            // item remains ready so several panels can be placed.
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                readyPanel?.let { panel ->
                    val activeIndex = panelOptions.indexOfFirst { it.id == panel.id }.coerceAtLeast(0)
                    val leftPanel = panelOptions.getOrNull((activeIndex - 1 + panelOptions.size) % panelOptions.size)
                    val rightPanel = panelOptions.getOrNull((activeIndex + 1) % panelOptions.size)

                    fun selectCarouselPanel(item: Product, direction: Int) {
                        carouselDirection = direction
                        onPanelSelected(item.id)
                    }

                    // This rectangular card is the actual ready panel. It is
                    // the drag source; the circular control below remains the
                    // camera capture button.
                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                val position = coordinates.positionInRoot()
                                panelSelectorOrigin = position + Offset(coordinates.size.width / 2f, coordinates.size.height / 2f)
                            }
                            .pointerInput(panel.id, showArMode, useEasyArMode, useCameraArFallback) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { panelDragOffset = Offset.Zero },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        panelDragOffset += dragAmount
                                    },
                                    onDragEnd = {
                                        if ((showArMode || useEasyArMode || useCameraArFallback) && panelSelectorOrigin != Offset.Zero) {
                                            dropRequest = ArDropRequest(panelSelectorOrigin + panelDragOffset, System.nanoTime())
                                        }
                                        panelDragOffset = Offset.Zero
                                    },
                                    onDragCancel = { panelDragOffset = Offset.Zero }
                                )
                            }
                            .clickable {
                                captureState = if (showArMode || useEasyArMode || useCameraArFallback) {
                                    "Panel selected. Tap a detected wall or ceiling, or hold and drag this card to place it."
                                } else {
                                    "AR is still starting. Please aim the camera at a wall or ceiling."
                                }
                            }
                            .width(118.dp)
                            .height(64.dp)
                            .background(Color.Black.copy(alpha = 0.62f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(12.dp))
                            .padding(7.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (panel.imageUrl != null) {
                                AsyncImage(
                                    model = panel.imageUrl,
                                    contentDescription = "Ready ${panel.name} panel",
                                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) { Text("3D", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            Text("READY", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        if (useEasyArMode) "Hold READY and drag to place. Tap a placed panel, then pinch to resize."
                        else "Hold the panel and drag to place",
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 9.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        @Composable
                        fun panelBubble(item: Product?, selected: Boolean, onSelect: () -> Unit) {
                            if (item == null) return
                            Box(
                                modifier = Modifier
                                    .size(if (selected) 86.dp else 48.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) Color(0xFFF77F2C) else Color.Black.copy(alpha = 0.62f), CircleShape)
                                    .border(if (selected) 3.dp else 1.dp, Color.White.copy(alpha = if (selected) 1f else 0.6f), CircleShape)
                                    .clickable { onSelect() },
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.imageUrl != null) {
                                    AsyncImage(
                                        model = item.imageUrl,
                                        contentDescription = item.name,
                                        modifier = Modifier
                                            .size(if (selected) 58.dp else 38.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("3D", color = Color.White, fontSize = if (selected) 13.sp else 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        leftPanel?.let { item ->
                            panelBubble(item, selected = false) { selectCarouselPanel(item, -1) }
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(
                            modifier = Modifier
                                .pointerInput(panel.id, showArMode) {
                                    detectDragGestures(
                                        onDragStart = { panelDragOffset = Offset.Zero },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            panelDragOffset += dragAmount
                                        },
                                        onDragEnd = {
                                            val horizontalSwipe = kotlin.math.abs(panelDragOffset.x) > 35f &&
                                                kotlin.math.abs(panelDragOffset.x) > kotlin.math.abs(panelDragOffset.y)
                                            if (horizontalSwipe && panelOptions.size > 1) {
                                                val direction = if (panelDragOffset.x < 0f) 1 else -1
                                                val nextIndex = (activeIndex + direction + panelOptions.size) % panelOptions.size
                                                selectCarouselPanel(panelOptions[nextIndex], direction)
                                            }
                                            panelDragOffset = Offset.Zero
                                        },
                                        onDragCancel = { panelDragOffset = Offset.Zero }
                                    )
                                }
                                .clickable { captureRoom() }
                                .size(86.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.28f), CircleShape)
                                .border(3.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = panel.id,
                                transitionSpec = {
                                    (slideInHorizontally { fullWidth -> carouselDirection * fullWidth } + fadeIn()) togetherWith
                                        (slideOutHorizontally { fullWidth -> -carouselDirection * fullWidth } + fadeOut())
                                },
                                label = "panelCarouselSwipe"
                            ) { targetId ->
                                val animatedPanel = panelOptions.firstOrNull { it.id == targetId } ?: panel
                                if (animatedPanel.imageUrl != null) {
                                    AsyncImage(
                                        model = animatedPanel.imageUrl,
                                        contentDescription = animatedPanel.name,
                                        modifier = Modifier.size(58.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("3D", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        rightPanel?.let { item ->
                            panelBubble(item, selected = false) { selectCarouselPanel(item, 1) }
                        }
                    }
                    Text(
                        "Tap or swipe to browse panels",
                        color = Color.White.copy(alpha = 0.82f),
                        fontSize = 9.sp
                    )
                }

            }

            // Keep the AR camera completely clear of the old bottom summary,
            // payment text, and shutter overlay.
            if (false) Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    // Keep the camera unobstructed: the summary is now a
                    // transparent overlay instead of a large white panel.
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (hasSelectedPanel && summaryTotals != null) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Panels placed", color = Color.White.copy(alpha = 0.78f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text(selectedQuantity.toString(), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedButton(
                                        onClick = { onQuantityChange(maxOf(1, selectedQuantity - 1)) },
                                        shape = RoundedCornerShape(999.dp),
                                        modifier = Modifier.size(42.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) { Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                                    OutlinedButton(
                                        onClick = { onQuantityChange(selectedQuantity + 1) },
                                        shape = RoundedCornerShape(999.dp),
                                        modifier = Modifier.size(42.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) { Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    InfoChip("Subtotal", formatPeso(summaryTotals.first), dark = true)
                                    InfoChip("Total", formatPeso(summaryTotals.third), dark = true)
                            }
                        } else {
                            Text(
                                "Select a panel first to unlock capture and payment.",
                                color = Color.White.copy(alpha = 0.78f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            if (hasCapturedRoom) {
                                "Room capture saved. Use Proceed to Payment when you are ready."
                            } else {
                                "Capture a room photo to unlock payment."
                            },
                            color = Color.White.copy(alpha = 0.78f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            captureState,
                            color = if (captureState.startsWith("Capture failed")) Color(0xFFFFB4A2) else Color.White.copy(alpha = 0.78f),
                            fontSize = 11.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasCapturedRoom) {
                        OutlinedButton(
                            onClick = {
                                onRoomCaptureSaved(null, "Room capture cleared.")
                                captureState = "Room capture cleared. Capture a new reference when ready."
                            },
                            enabled = !isCapturing,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.38f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Retake room photo",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text("Retake", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (hasSelectedPanel) {
                                    onSaveToQuote()
                                } else {
                                    onOpenCatalog()
                                }
                            },
                            enabled = !isCapturing,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF77F2C),
                                disabledContainerColor = Color(0xFFE7D5C2),
                                disabledContentColor = Color(0xFF9A7B5D)
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                if (hasSelectedPanel) "Proceed to Payment" else "Proceed to Catalog",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { captureRoom() },
                                enabled = hasCameraPermission && !isCapturing && hasSelectedPanel && isSignedIn,
                                shape = CircleShape,
                                contentPadding = PaddingValues(0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Transparent,
                                    disabledContentColor = Color.White.copy(alpha = 0.35f)
                                ),
                                modifier = Modifier
                                    .size(72.dp)
                                    .padding(0.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(58.dp)
                                        .background(Color.Black.copy(alpha = 0.30f), CircleShape)
                                        .border(
                                            width = 3.dp,
                                            color = Color(0xFFF77F2C),
                                            shape = CircleShape
                                        )
                                        .graphicsLayer {
                                            scaleX = shutterPulse
                                            scaleY = shutterPulse
                                        }
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Camera permission needed", color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C))
                ) {
                    Text("Enable Camera")
                }
            }
        }
    }

    if (showLatestCaptureFullscreen && latestRoomCaptureUrl != null) {
        Dialog(onDismissRequest = { showLatestCaptureFullscreen = false }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.92f))
                    .clickable { showLatestCaptureFullscreen = false },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = latestRoomCaptureUrl,
                    contentDescription = "Fullscreen latest room capture",
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .height(520.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(22.dp)),
                    contentScale = ContentScale.Crop
                )
                Button(
                    onClick = { showLatestCaptureFullscreen = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.14f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(18.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun CustomizationScreen(
    selectedProduct: Product?,
    inventoryProducts: List<Product>,
    inventoryCategories: List<String>,
    inventoryCategory: String,
    onInventoryCategoryChange: (String) -> Unit,
    onSelectInventoryProduct: (Int) -> Unit,
    finish: String,
    size: String,
    colour: String,
    pattern: String,
    is3d: Boolean,
    quantity: String,
    notes: String,
    addOns: List<String>,
    onFinishChange: (String) -> Unit,
    onSizeChange: (String) -> Unit,
    onColourChange: (String) -> Unit,
    on3dChange: (Boolean) -> Unit,
    onQuantityChange: (String) -> Unit,
    onNotesChange: (String) -> Unit,
    onAddOnToggle: (String) -> Unit,
    onGoToCatalog: () -> Unit,
    onPreviewInAr: () -> Unit,
    onOpenSavedPanels: () -> Unit,
    onSaveCustomization: () -> Unit,
    onCreateQuotation: () -> Unit
) {
    // The preview is always the customized material; the stock/modified
    // switch was removed from the UI.
    // Start with the actual panel image at normal brightness. The old default
    // applied a tint and several dark overlays, which made every new panel look
    // shaded before the customer changed anything.
    val showModifiedPreview = false
    if (selectedProduct == null) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Customization", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF121826))
                    Text("Choose a panel from Catalog to start customizing it.", color = Color(0xFF667085), lineHeight = 22.sp)
                    Button(
                        onClick = onGoToCatalog,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Go to Catalog") }
                }
            }
        }
        return
    }

    val quantityValue = quantity.toIntOrNull()?.coerceAtLeast(1) ?: 1
    val panelSizes = listOf("2400 × 150mm", "2400 × 200mm", "2400 × 300mm")
    val fixingPrice = if (size.contains("300")) 12 else 0
    val addOnPrices = mapOf("Fixings Kit" to 18, "Pro Adhesive" to 12, "Edge Trim" to 24)
    val total = selectedProduct.price + fixingPrice + addOns.fold(0) { sum, addOn -> sum + (addOnPrices[addOn] ?: 0) }
    val dark = Color(0xFF0D0D0C)
    val panelForCategory = inventoryProducts.filter { inventoryCategory == "All" || it.category.equals(inventoryCategory, ignoreCase = true) }

    Column(
        modifier = Modifier.fillMaxSize().background(dark).verticalScroll(rememberScrollState()).padding(bottom = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("PREVIEW", color = Color(0xFFD8B16E), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Box(Modifier.fillMaxWidth().height(300.dp)) {
            if (is3d && selectedProduct.modelUrl != null) {
                PanelSceneViewer(
                    modelUrl = selectedProduct.modelUrl,
                    modifier = Modifier.fillMaxSize(),
                    tintHex = if (showModifiedPreview) customizationColourHex(colour) else null,
                    // Keep the 3D material on SceneView's stable GLB path.
                    // Applying the bitmap texture here can crash the renderer
                    // while the Scene is being created; the colour tint below
                    // still updates the selected panel appearance.
                    textureAsset = null,
                    pattern = pattern,
                    finish = finish,
                    size = size,
                    showModified = showModifiedPreview
                )
            } else {
                PanelVisualizer(
                    product = selectedProduct,
                    colour = colour,
                    finish = finish,
                    pattern = pattern,
                    is3d = is3d,
                    showModified = showModifiedPreview,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onGoToCatalog, modifier = Modifier.size(34.dp).background(Color.Black.copy(.55f), CircleShape)) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = Color.White) }
                Text("PANEL STUDIO", color = Color(0xFFE8D7C4), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.padding(top = 9.dp))
                IconButton(onClick = onOpenSavedPanels, modifier = Modifier.size(34.dp).background(Color.Black.copy(.55f), CircleShape)) { Icon(Icons.Outlined.PendingActions, "Saved panels", tint = Color.White, modifier = Modifier.size(17.dp)) }
            }
            Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
                Text("UPVC COLLECTION", color = Color(0xFFD4AD6C), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text("Wall Panel\nConfigurator", color = Color.White, fontSize = 29.sp, lineHeight = 31.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("2D" to !is3d, "3D" to is3d).forEach { (label, active) ->
                        TextButton(onClick = { on3dChange(label == "3D") }, shape = RoundedCornerShape(20.dp), colors = ButtonDefaults.textButtonColors(containerColor = if (active) Color(0xFFD8B16E) else Color(0xFF292725), contentColor = if (active) Color.Black else Color.White), contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp)) { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }

        Column(Modifier.padding(horizontal = 18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            if (panelForCategory.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) { items(panelForCategory, key = { it.id }) { product ->
                    val active = product.id == selectedProduct.id
                    TextButton(onClick = { onSelectInventoryProduct(product.id) }, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.textButtonColors(containerColor = if (active) Color(0xFF2C2823) else Color(0xFF171615), contentColor = if (active) Color(0xFFE4C486) else Color(0xFF8D8780)), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)) { Text(product.name, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 10.sp) }
                } }
            }
            Text("COLOUR", color = Color(0xFFD8B16E), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("Mahogany" to Color(0xFF39241B), "Ash Grey" to Color(0xFFB7ADA0), "Chalk White" to Color(0xFFD98B3C), "Onyx" to Color(0xFF151515)).forEach { (name, swatch) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(48.dp).clickable {
                            onColourChange(if (colour == name) "" else name)
                        }
                    ) {
                        Box(
                            Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(swatch)
                                .border(
                                    if (colour == name) 2.dp else 1.dp,
                                    if (colour == name) Color(0xFFD8B16E) else Color(0xFF3B3732),
                                    RoundedCornerShape(11.dp)
                                )
                        )
                        Text(name, color = Color(0xFF9F978E), fontSize = 7.sp, modifier = Modifier.padding(top = 4.dp), maxLines = 1)
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("SIZE", color = Color(0xFFD8B16E), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp); Text(size, color = Color(0xFFD8B16E), fontSize = 10.sp) }
            panelSizes.forEach { option ->
                val active = size == option
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF171615))
                        .border(1.dp, if (active) Color(0xFFD8B16E) else Color(0xFF302D29), RoundedCornerShape(12.dp))
                        .clickable { onSizeChange(if (active) "" else option) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .size(12.dp)
                                .border(1.dp, if (active) Color(0xFFD8B16E) else Color(0xFF5B554E), CircleShape)
                                .background(if (active) Color(0xFFD8B16E) else Color.Transparent, CircleShape)
                        )
                        Column {
                            Text(option, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(if (option.contains("200")) "Standard profile" else if (option.contains("150")) "Slim profile" else "Wide profile", color = Color(0xFF77716A), fontSize = 8.sp)
                        }
                    }
                    Text("₱${if (option.contains("200")) 84 else if (option.contains("150")) 72 else 96}", color = if (active) Color(0xFFD8B16E) else Color(0xFF77716A), fontSize = 12.sp)
                }
            }
            OutlinedTextField(
                value = size,
                onValueChange = onSizeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Custom dimensions") },
                placeholder = { Text("Example: 2.9m x 20cm x 12mm") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD8B16E),
                    unfocusedBorderColor = Color(0xFF5B554E),
                    focusedLabelColor = Color(0xFFD8B16E),
                    unfocusedLabelColor = Color(0xFF9F978E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD8B16E)
                )
            )
            OutlinedTextField(
                value = quantity,
                onValueChange = { value -> onQuantityChange(value.filter { it.isDigit() }.take(4)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Quantity") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD8B16E),
                    unfocusedBorderColor = Color(0xFF5B554E),
                    focusedLabelColor = Color(0xFFD8B16E),
                    unfocusedLabelColor = Color(0xFF9F978E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD8B16E)
                )
            )
            Text("ADD-ONS", color = Color(0xFFD8B16E), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            listOf("Fixings Kit" to 18, "Pro Adhesive" to 12, "Edge Trim" to 24).forEach { (name, price) ->
                val active = name in addOns
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(if (active) Color(0xFF241F1A) else Color(0xFF171615)).border(1.dp, if (active) Color(0xFFD8B16E) else Color(0xFF302D29), RoundedCornerShape(10.dp)).clickable { onAddOnToggle(name) }.padding(horizontal = 13.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(15.dp).background(if (active) Color(0xFFD8B16E) else Color.Transparent, RoundedCornerShape(4.dp)).border(1.dp, if (active) Color(0xFFD8B16E) else Color(0xFF5B554E), RoundedCornerShape(4.dp))); Text(name, color = Color.White, fontSize = 11.sp) }
                    Text("+₱$price", color = if (active) Color(0xFFD8B16E) else Color(0xFF9F978E), fontSize = 10.sp)
                }
            }
            OutlinedTextField(
                value = notes,
                onValueChange = onNotesChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Customization notes") },
                placeholder = { Text("Describe any finish, layout, or installation request") },
                minLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD8B16E),
                    unfocusedBorderColor = Color(0xFF5B554E),
                    focusedLabelColor = Color(0xFFD8B16E),
                    unfocusedLabelColor = Color(0xFF9F978E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD8B16E)
                )
            )
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF171615)), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(horizontal = 14.dp, vertical = 12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("BREAKDOWN", color = Color(0xFF77716A), fontSize = 9.sp, fontWeight = FontWeight.Bold); Text("₱${total}", color = Color(0xFFD8B16E), fontSize = 13.sp, fontWeight = FontWeight.Bold) } }
            Button(onClick = onPreviewInAr, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0A25D), contentColor = Color.Black), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(13.dp)) { Text("TRY AR", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) }
            OutlinedButton(onClick = onSaveCustomization, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(13.dp), border = BorderStroke(1.dp, Color(0xFFD8B16E)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD8B16E))) { Text("SAVE PANEL", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) }
            Button(onClick = onCreateQuotation, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(13.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF06416), contentColor = Color.White)) { Text("REQUEST QUOTATION", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp) }
        }
    }
}

@Composable
private fun PanelVisualizer(
    product: Product?,
    colour: String,
    finish: String = "Matte",
    pattern: String = "Horizontal",
    is3d: Boolean,
    showModified: Boolean = false,
    modifier: Modifier = Modifier
) {
    val wood = when (colour) { "Mahogany" -> Color(0xFF6F3724); "Ash Grey" -> Color(0xFF737879); "Chalk White" -> Color(0xFFD8D4C8); "Onyx" -> Color(0xFF242323); else -> Color(0xFF9A6A3F) }
    val sheen = when (finish.lowercase()) {
        "gloss" -> 0.28f
        "satin" -> 0.14f
        else -> 0.04f
    }
    Box(modifier.clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))) {
        // A softly lit studio background keeps the 2D preview from reading like a flat swatch.
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF273033), Color(0xFF111313)))))
        Canvas(Modifier.fillMaxSize()) {
            // Keep the material sample at a believable object scale instead of stretching it edge-to-edge.
            val panelWidth = size.width * .68f
            val left = (size.width - panelWidth) / 2f
            val top = 48.dp.toPx()
            val right = left + panelWidth
            val bottom = size.height - 18.dp.toPx()
            val radius = 18.dp.toPx()
            drawRoundRect(
                color = Color.Black.copy(alpha = .52f),
                topLeft = Offset(left + 7.dp.toPx(), top + 10.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
            )
            drawRoundRect(
                brush = Brush.verticalGradient(listOf(wood.copy(alpha = .98f), wood, wood.copy(alpha = .72f))),
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
            )
            drawRoundRect(
                color = Color.White.copy(alpha = .18f),
                topLeft = Offset(left + 1.dp.toPx(), top + 1.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(right - left - 2.dp.toPx(), 2.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius, radius)
            )
        }
        Box(
            Modifier
                .fillMaxWidth(.68f)
                .fillMaxHeight()
                .padding(top = 48.dp, bottom = 18.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(18.dp))
        ) {
            // Use the selected catalog image when available so every panel has its own 2D preview.
            // The procedural material remains underneath as a graceful fallback for products without an image.
            // Use the selected product's own image so changing panels changes
            // the visible preview as well as the selected product state.
            val previewSource = customizationInventoryPreviewAsset(product?.name)
                ?: customizationCatalogPreviewAsset(product?.name)
                ?: product?.imageUrl
            previewSource?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "2D preview of ${product?.name ?: "panel"}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            if (showModified) {
                // Keep the selected colour and pattern inside the panel bounds.
                // The neutral texture assets are tinted here so every swatch
                // changes the visible material, not just the surrounding scene.
                Box(Modifier.fillMaxSize().background(wood.copy(alpha = 0.46f)))
                Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.White.copy(alpha = sheen + .04f), Color.Transparent, Color.Black.copy(alpha = sheen + .08f)))))
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.White.copy(alpha = .17f), Color.Transparent, Color.Black.copy(alpha = .28f)))))
            }
        }
        Text(product?.name ?: "Panel preview", color = Color.White.copy(alpha = .78f), fontSize = 9.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 5.dp))
        Text("2D MATERIAL PREVIEW", color = Color.White.copy(alpha = .72f), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 18.dp))
        if (is3d) Text("3D VIEW", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.TopEnd).padding(top = 66.dp, end = 16.dp))
    }
}

@Composable
private fun PatternOverlay(pattern: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val lineColor = Color.Black.copy(alpha = 0.18f)
        val highlight = Color.White.copy(alpha = 0.08f)
        val step = 30.dp.toPx()
        when (pattern) {
            "Vertical" -> {
                var x = 12.dp.toPx()
                while (x < size.width) {
                    drawLine(Color.Black.copy(alpha = .24f), Offset(x, 0f), Offset(x, size.height), strokeWidth = 5.dp.toPx())
                    drawLine(lineColor, Offset(x + 4.dp.toPx(), 0f), Offset(x + 4.dp.toPx(), size.height), strokeWidth = 2.dp.toPx())
                    drawLine(highlight, Offset(x + 7.dp.toPx(), 0f), Offset(x + 7.dp.toPx(), size.height), strokeWidth = 1.dp.toPx())
                    x += step
                }
            }
            "Grid" -> {
                var x = 0f
                while (x < size.width) { drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 2.dp.toPx()); drawLine(highlight, Offset(x + 2.dp.toPx(), 0f), Offset(x + 2.dp.toPx(), size.height), strokeWidth = 1.dp.toPx()); x += step }
                var y = 0f
                while (y < size.height) { drawLine(lineColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 2.dp.toPx()); drawLine(highlight, Offset(0f, y + 2.dp.toPx()), Offset(size.width, y + 2.dp.toPx()), strokeWidth = 1.dp.toPx()); y += step }
            }
            "Chevron" -> {
                var y = -step
                while (y < size.height + step) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(0f, y)
                        lineTo(size.width * .25f, y + step * .45f)
                        lineTo(size.width * .5f, y)
                        lineTo(size.width * .75f, y + step * .45f)
                        lineTo(size.width, y)
                    }
                    drawPath(path, Color.Black.copy(alpha = .24f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 5.dp.toPx()))
                    drawPath(path, highlight, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx()))
                    y += step
                }
            }
            else -> {
                var y = 0f
                while (y < size.height) { drawLine(Color.Black.copy(alpha = .22f), Offset(0f, y), Offset(size.width, y), strokeWidth = 5.dp.toPx()); drawLine(highlight, Offset(0f, y - 3.dp.toPx()), Offset(size.width, y - 3.dp.toPx()), strokeWidth = 1.5.dp.toPx()); y += step }
            }
        }
    }
}

private fun customizationColourHex(colour: String): String = when (colour) {
    "Mahogany" -> "7A3E25"
    "Ash Grey" -> "737879"
    "Chalk White" -> "D8D4C8"
    "Onyx" -> "242323"
    "Oak" -> "9A6A3F"
    else -> "9A6A3F"
}

private fun customizationTextureAsset(colour: String): String = when (colour) {
    "Mahogany" -> "textures/customization/mahogany.jpg"
    "Ash Grey" -> "textures/customization/ash-grey.jpg"
    "Chalk White" -> "textures/customization/chalk-white.jpg"
    "Onyx" -> "textures/customization/onyx.jpg"
    else -> "textures/customization/oak.jpg"
}

private fun customizationCatalogPreviewAsset(productName: String?): String? {
    val code = productName?.trim().orEmpty()
    val supportedCodes = setOf(
        "F-44", "F-213", "F-222",
        "PG-00", "PG-08", "PG-203",
        "LJS-102", "LJS-110", "LJS-121",
        "150-00B", "150-05B", "150-10", "150-48",
        "160-23", "160-KB-01", "160-KB-02",
        "195-04", "195-05", "195-07", "01", "84-25", "423"
    )
    return code.takeIf { it in supportedCodes }
        ?.let { "file:///android_asset/catalog/catalog-$it.jpg" }
}

private fun customizationInventoryPreviewAsset(productName: String?): String? {
    val asset = when (productName?.trim()) {
        "01" -> "inventory-crop-01.jpg"
        "84-25" -> "inventory-crop-84-25.jpg"
        "423" -> "inventory-crop-423.jpg"
        "F-44" -> "inventory-crop-F-44.jpg"
        "F-213" -> "inventory-crop-F-213.jpg"
        "F-222" -> "inventory-crop-F-222.jpg"
        "PG-00" -> "inventory-crop-PG-00.jpg"
        "PG-08" -> "inventory-crop-PG-08.jpg"
        "PG-203" -> "inventory-crop-PG-203.jpg"
        "LJS-102" -> "inventory-crop-LJS-102.jpg"
        "LJS-110" -> "inventory-crop-LJS-110.jpg"
        "LJS-121" -> "inventory-crop-LJS-121.jpg"
        "150-48" -> "inventory-crop-150-48.jpg"
        "150-00B" -> "inventory-crop-150-00B.jpg"
        "150-10" -> "inventory-crop-150-10.jpg"
        "150-05B" -> "inventory-crop-150-05B.jpg"
        "160-23" -> "inventory-crop-160-23.jpg"
        "160-KB 01" -> "inventory-crop-160-KB-01.jpg"
        "160-KB 02" -> "inventory-crop-160-KB-02.jpg"
        "195-04" -> "inventory-crop-195-04.jpg"
        "195-05" -> "inventory-crop-195-05.jpg"
        "195-07" -> "inventory-crop-195-07.jpg"
        else -> null
    }
    return asset?.let { "file:///android_asset/inventory/$it" }
}

@Composable
private fun PriceLine(label: String, value: String, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = if (bold) 12.sp else 11.sp, fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Normal, color = Color(0xFF48413D))
        Text(value, fontSize = if (bold) 15.sp else 11.sp, fontWeight = if (bold) FontWeight.ExtraBold else FontWeight.Normal, color = Color(0xFF28231F))
    }
}

@Composable
private fun CustomizationChoiceCard(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF121826))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(options) { option ->
                    val active = option == selected
                    TextButton(
                        onClick = { onSelect(option) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (active) Color(0xFFE9DDFB) else Color(0xFFF7F3EE),
                            contentColor = if (active) Color(0xFF4A315F) else Color(0xFF667085)
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) { Text(option, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium) }
                }
            }
        }
    }
}

@Composable
private fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    CameraPreview(cameraController = cameraController, modifier = modifier)
}

@Composable
private fun CameraPreview(
    cameraController: LifecycleCameraController,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(lifecycleOwner) {
        cameraController.bindToLifecycle(lifecycleOwner)
    }

    AndroidView(
        factory = { context ->
            PreviewView(context).apply {
                this.controller = cameraController
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = modifier,
        update = { previewView: PreviewView ->
            previewView.controller = cameraController
        }
    )
}

@Composable
private fun PaymentStepHeader(step: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf("Quotation", "Courier", "Payment", "Summary").forEachIndexed { index, label ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(if (index < step) Color(0xFFF57419) else Color(0xFFE7E7E7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(if (index < step - 1) "✓" else "${index + 1}", color = if (index < step) Color.White else Color(0xFF777777), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text(label, color = if (index == step - 1) Color(0xFFF57419) else Color(0xFF777777), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

private enum class CheckoutStep {
    QUOTATION,
    COURIER,
    PAYMENT,
    ORDER_REVIEW
}

@Composable
private fun QuotationStep(
    modifier: Modifier,
    latestQuoteNumber: String,
    quotationItemsJson: String,
    quotationIssuedAt: String,
    quotationBusinessJson: String,
    customerName: String,
    customerEmail: String,
    customerPhone: String,
    customerAddress: String,
    latestQuoteSubtotal: String,
    latestQuoteVat: String,
    latestQuoteAmount: String,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val business = remember(quotationBusinessJson) {
        runCatching { JSONObject(quotationBusinessJson) }.getOrDefault(JSONObject())
    }
    val savedBusinessName = business.optString("name").trim()
    val businessName = if (savedBusinessName.isBlank() || savedBusinessName.equals("AR Panels", ignoreCase = true)) {
        "Aiyige Hardware & Construction"
    } else {
        savedBusinessName
    }
    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFFFFBF6)).verticalScroll(rememberScrollState()).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth().background(Color(0xFFFFF7EB)).padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text(businessName, color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Official customer quotation", color = Color(0xFF4A5568), fontSize = 11.sp) }
            OutlinedButton(onClick = onBack, modifier = Modifier.height(38.dp), shape = RoundedCornerShape(999.dp), border = BorderStroke(1.dp, Color(0xFFE0B98F)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF202124)), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)) { Text("No, go back", fontSize = 12.sp) }
        }
        Text("Step 1 of 4 — Review quotation", color = Color(0xFF555555), fontSize = 12.sp)
        PaymentStepHeader(1)
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Image(painter = painterResource(id = R.drawable.client_logo_circle), contentDescription = "Aiyige logo", modifier = Modifier.size(48.dp))
                    Column(Modifier.weight(1f)) {
                        Text(businessName.uppercase(Locale.getDefault()), color = Color(0xFF202124), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Customer quotation", color = Color(0xFF667085), fontSize = 12.sp)
                    }
                }
                listOf(business.optString("address"), business.optString("contact")).filter { it.isNotBlank() }.forEach { detail ->
                    Text(detail, color = Color(0xFF667085), fontSize = 11.sp)
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE7E9ED)))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("QUOTATION NO.", color = Color(0xFF667085), fontSize = 10.sp, fontWeight = FontWeight.Bold); Text(latestQuoteNumber.ifBlank { "Preparing…" }, color = Color(0xFF202124), fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    Column(horizontalAlignment = Alignment.End) { Text("DATE ISSUED", color = Color(0xFF667085), fontSize = 10.sp, fontWeight = FontWeight.Bold); Text(quotationIssuedAt.substringBefore("T").ifBlank { "--" }, color = Color(0xFF202124), fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
                }
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("CUSTOMER", color = Color(0xFFF57419), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(customerName.ifBlank { "Customer" }, color = Color(0xFF202124), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Contact: ${customerPhone.ifBlank { "--" }}", color = Color(0xFF475467), fontSize = 12.sp)
                    Text("Email: ${customerEmail.ifBlank { "--" }}", color = Color(0xFF475467), fontSize = 12.sp)
                    Text("Address: ${customerAddress.ifBlank { "--" }}", color = Color(0xFF475467), fontSize = 12.sp)
                }
            }
        }
        QuotationItemsCard(itemsJson = quotationItemsJson)
        PaymentReviewCard("TOTALS", listOf(
            "Subtotal" to latestQuoteSubtotal,
            "VAT" to latestQuoteVat,
            "Grand total" to latestQuoteAmount,
            "External delivery" to "Booked and paid by you separately"
        ))
        Text("Confirm this quotation to choose J&T or Lalamove, then select a payment method and review your order before submitting.", color = Color(0xFF667085), fontSize = 13.sp, lineHeight = 19.sp)
        Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(999.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419))) { Text("Confirm quotation", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun QuotationItemsCard(itemsJson: String) {
    val items = remember(itemsJson) {
        runCatching {
            val array = JSONArray(itemsJson)
            (0 until array.length()).mapNotNull { index -> array.optJSONObject(index) }
        }.getOrDefault(emptyList())
    }
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("QUOTED ITEMS", color = Color(0xFFF57419), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            items.forEach { item ->
                Column(Modifier.fillMaxWidth().background(Color(0xFFF8F9FB), RoundedCornerShape(10.dp)).padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(item.optString("panelModel", "Panel"), modifier = Modifier.weight(1f), color = Color(0xFF202124), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(formatPeso(item.optDouble("lineTotal", 0.0).toInt()), color = Color(0xFF202124), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(listOf(item.optString("size"), item.optString("finish")).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "Product" }, color = Color(0xFF667085), fontSize = 11.sp)
                    Text("${item.optInt("quantity", 1)} × ${formatPeso(item.optDouble("unitPrice", 0.0).toInt())} per unit", color = Color(0xFF475467), fontSize = 11.sp)
                }
            }
            if (items.isEmpty()) Text("Quotation items are not available yet.", color = Color(0xFF667085), fontSize = 12.sp)
        }
    }
}

@Composable
private fun CourierSelectionStep(
    modifier: Modifier,
    quotationNumber: String,
    selectedCourier: String,
    courierOptions: List<String>,
    isSubmitting: Boolean,
    onCourierChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFFFFBF6))
            .verticalScroll(rememberScrollState()).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth().background(Color(0xFFFFF7EB)).padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("Aiyige", color = Color(0xFF202124), fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("Delivery", color = Color(0xFF4A5568), fontSize = 11.sp) }
            OutlinedButton(onClick = onBack, enabled = !isSubmitting, modifier = Modifier.height(38.dp), shape = RoundedCornerShape(999.dp), border = BorderStroke(1.dp, Color(0xFFE0B98F)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF202124)), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)) { Text("Back", fontSize = 12.sp) }
        }
        Text("Step 2 of 4 — Choose a courier", color = Color(0xFF555555), fontSize = 12.sp)
        PaymentStepHeader(2)
        Text("QUOTATION $quotationNumber", color = Color(0xFFF57419), fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("Choose who will deliver your order", color = Color(0xFF202124), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        courierOptions.forEach { courier ->
            val selected = courier.equals(selectedCourier, ignoreCase = true)
            OutlinedButton(
                onClick = { if (!isSubmitting) onCourierChange(courier) },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (selected) Color(0xFFF57419) else Color(0xFFE0D8D0)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) Color(0xFFFFF3E8) else Color.White, contentColor = Color(0xFF202124))
            ) { Text(if (selected) "✓  $courier" else courier, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) }
        }
        Text(if (selectedCourier.isBlank()) "Select J&T or Lalamove to continue. You will book and pay the courier directly outside the app." else "You selected $selectedCourier. Book and pay this courier directly outside the app.", color = Color(0xFF667085), fontSize = 13.sp, lineHeight = 19.sp)
        Button(
            onClick = onNext,
            enabled = selectedCourier in courierOptions && !isSubmitting,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419), disabledContainerColor = Color(0xFFD8D4D0))
        ) { Text("Continue to payment", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PaymentDetailsStep(
    modifier: Modifier,
    isSignedIn: Boolean,
    signInName: String,
    signInEmail: String,
    customerStreetAddress: String,
    customerCityProvince: String,
    customerPostalCode: String,
    latestQuoteNumber: String,
    latestQuoteAmount: String,
    selectedPaymentMethod: String,
    paymentMethodOptions: List<String>,
    isSubmitting: Boolean,
    onPaymentMethodChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val address = listOf(customerStreetAddress, customerCityProvince, customerPostalCode)
        .filter { it.isNotBlank() }
        .joinToString(", ")
    val ready = isSignedIn && latestQuoteNumber.isNotBlank() &&
        customerStreetAddress.isNotBlank() && customerCityProvince.isNotBlank() &&
        customerPostalCode.matches(Regex("\\d{4}")) &&
        selectedPaymentMethod in paymentMethodOptions && !isSubmitting

    Column(
        modifier = modifier.fillMaxSize().background(Color(0xFFFFFBF6))
            .verticalScroll(rememberScrollState()).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth().background(Color(0xFFFFF7EB)).padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Aiyige", color = Color(0xFF202124), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Payment", color = Color(0xFF4A5568), fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onBack, enabled = !isSubmitting, modifier = Modifier.height(38.dp), shape = RoundedCornerShape(999.dp), border = BorderStroke(1.dp, Color(0xFFE0B98F)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF202124)), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)) { Text("Back", fontSize = 12.sp) }
                Text(if (isSignedIn) compactDisplayName(signInName).ifBlank { "Account" } else "Sign in", color = Color(0xFF9E4300), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.background(Color(0xFFFFE8D1), RoundedCornerShape(999.dp)).padding(horizontal = 12.dp, vertical = 10.dp))
            }
        }
        Text("Step 3 of 4 — Payment method", color = Color(0xFF555555), fontSize = 12.sp)
        PaymentStepHeader(3)

        PaymentReviewCard("ACCOUNT", listOf(
            "Name" to (signInName.ifBlank { "Sign in to continue" }),
            "Email" to signInEmail
        ))
        PaymentReviewCard("DELIVERY ADDRESS", listOf(
            "Address" to address.ifBlank { "Add a delivery address" }
        ))

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("PAYMENT METHOD", color = Color(0xFFF57419), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentMethodOptions.chunked(2).forEach { rowMethods ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowMethods.forEach { method ->
                        val selected = method.equals(selectedPaymentMethod, ignoreCase = true)
                        OutlinedButton(
                            onClick = { if (!isSubmitting) onPaymentMethodChange(method) },
                            enabled = !isSubmitting,
                            modifier = Modifier.weight(1f).height(72.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (selected) Color(0xFFF57419) else Color(0xFFE0D8D0)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (selected) Color(0xFFFFF3E8) else Color.White,
                                contentColor = Color(0xFF202124)
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(if (selected) "●" else "○", color = if (selected) Color(0xFFF57419) else Color(0xFF777777), fontSize = 16.sp)
                                Text(method, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                    if (rowMethods.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                }
                val paymentInstructions = when (selectedPaymentMethod.trim().lowercase()) {
                    "" -> "Select a payment method to view payment instructions."
                    "gcash" -> "After submitting your order, contact Aiyige for verified GCash payment details. Use your quotation number as the payment reference."
                    "bank transfer", "bank", "banktransfer" -> "After submitting your order, contact Aiyige for verified bank details. Use your quotation number as the payment reference."
                    "cash" -> "Pay at the store. Your order will enter preparation after staff records the full payment."
                    else -> "Follow the payment instructions provided by Aiyige. Your order enters preparation after full payment is confirmed."
                }
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F0))) {
                    Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("HOW TO PAY", color = Color(0xFF9E4300), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(paymentInstructions, color = Color(0xFF555555), fontSize = 12.sp, lineHeight = 18.sp)
                    }
                }
            }
        }
        PaymentReviewCard("AMOUNT DUE", listOf("Quotation" to latestQuoteNumber, "Total to pay" to latestQuoteAmount))
        Text("No payment is charged in the app yet. Your order will stay out of preparation until staff confirms full payment.", color = Color(0xFF667085), fontSize = 12.sp, lineHeight = 17.sp)

        if (latestQuoteNumber.isNotBlank()) {
            Text(latestQuoteNumber, color = Color(0xFFFFB15C), fontSize = 12.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.background(Color(0xFF3A1D00), RoundedCornerShape(999.dp)).padding(horizontal = 12.dp, vertical = 7.dp))
        }
        Button(
            onClick = onNext,
            enabled = ready,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419), disabledContainerColor = Color(0xFFD8D4D0))
        ) { Text(if (!isSignedIn) "Sign in to continue" else "Next: review order", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PaymentSubmittedStep(
    modifier: Modifier,
    latestQuoteNumber: String,
    latestQuoteAmount: String,
    selectedPaymentMethod: String,
    selectedDeliveryMethod: String,
    onFinish: () -> Unit
) {
    Column(modifier.fillMaxSize().background(Color(0xFFFFFBF6)).verticalScroll(rememberScrollState()).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Aiyige", modifier = Modifier.align(Alignment.Start), color = Color(0xFF202124), fontSize = 25.sp, fontWeight = FontWeight.Bold)
        Text("Order submitted", modifier = Modifier.align(Alignment.Start), color = Color(0xFF555555), fontSize = 12.sp)
        Box(Modifier.size(66.dp).background(Color(0xFFEAF8F0), CircleShape).border(2.dp, Color(0xFF75B843), CircleShape), contentAlignment = Alignment.Center) { Text("✓", color = Color(0xFF4D7C0F), fontSize = 30.sp, fontWeight = FontWeight.Bold) }
        Text("Order submitted", color = Color(0xFF202124), fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("Your order is saved. No payment was charged in the app. Complete payment using the instructions from Aiyige; preparation starts after staff confirms the full amount.", color = Color(0xFF555555), fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 19.sp)
        Text("▣  ${latestQuoteNumber.ifBlank { "ORDER" }}", color = Color(0xFFF57419), fontWeight = FontWeight.Bold, modifier = Modifier.border(1.dp, Color(0xFFF57419), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 6.dp))
        PaymentReviewCard("PAYMENT SUMMARY", listOf("Amount due" to latestQuoteAmount, "Method" to selectedPaymentMethod, "Status" to "Awaiting payment"))
        PaymentReviewCard("ORDER STATUS", listOf("Quotation" to latestQuoteNumber, "Order" to "Submitted", "Courier" to selectedDeliveryMethod, "Delivery" to "Booked by you outside the app"))
        Button(onClick = onFinish, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419))) { Text("Track My Order", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PaymentReviewStep(
    modifier: Modifier,
    signInName: String,
    signInEmail: String,
    customerPhone: String,
    deliveryAddress: String,
    latestQuoteNumber: String,
    quotationItemsJson: String,
    latestQuoteSubtotal: String,
    latestQuoteVat: String,
    latestQuoteAmount: String,
    selectedPaymentMethod: String,
    selectedDeliveryMethod: String,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier.fillMaxSize().background(Color(0xFFFFFBF6)).verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth().background(Color(0xFFFFF7EB)).padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column { Text("Aiyige", color = Color(0xFF202124), fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("Review", color = Color(0xFF4A5568), fontSize = 11.sp) }
            OutlinedButton(onClick = onBack, modifier = Modifier.height(38.dp), shape = RoundedCornerShape(999.dp), border = BorderStroke(1.dp, Color(0xFFE0B98F)), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF202124)), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)) { Text("Back", fontSize = 12.sp) }
        }
        Text("Step 4 of 4 — Final order summary", color = Color(0xFF555555), fontSize = 12.sp)
        PaymentStepHeader(4)
        Text("ORDER $latestQuoteNumber", color = Color(0xFF9E4300), fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth().background(Color(0xFFFFE8D1), RoundedCornerShape(12.dp)).padding(14.dp))
        PaymentReviewCard("CUSTOMER", listOf("Name" to signInName, "Contact" to customerPhone, "Email" to signInEmail))
        PaymentReviewCard("DELIVERY", listOf("Address" to deliveryAddress, "Courier" to selectedDeliveryMethod.ifBlank { "Choose a courier" }, "Delivery fee" to "Not included; arrange with courier"))
        PaymentReviewCard("PAYMENT", listOf("Method" to selectedPaymentMethod, "Status" to "Awaiting payment", "Amount due" to latestQuoteAmount))
        QuotationItemsCard(itemsJson = quotationItemsJson)
        PaymentReviewCard("TOTALS", listOf("Subtotal" to latestQuoteSubtotal, "VAT" to latestQuoteVat, "Amount due" to latestQuoteAmount))
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(999.dp)) { Text("Confirm and submit order", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Composable
private fun PaymentReviewCard(title: String, rows: List<Pair<String, String>>) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(title, color = Color(0xFFF57419), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            rows.forEach { (label, value) ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, color = Color(0xFF555555), fontSize = 12.sp)
                    Text(value.ifBlank { "--" }, color = Color(0xFF202124), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun PaymentScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isSubmitting: Boolean,
    paymentSubmitted: Boolean,
    isSignedIn: Boolean,
    signInName: String,
    signInEmail: String,
    latestQuoteSummary: String?,
    latestQuoteItemName: String,
    latestQuoteUnitPrice: String,
    latestQuoteSubtotal: String,
    latestQuoteVat: String,
    latestQuotePanels: String,
    latestQuoteAmount: String,
    latestQuoteNumber: String,
    quotationItemsJson: String,
    quotationIssuedAt: String,
    quotationBusinessJson: String,
    paymentStatusMessage: String,
    customerStreetAddress: String,
    customerCityProvince: String,
    customerPostalCode: String,
    customerPhone: String,
    selectedPaymentMethod: String,
    selectedDeliveryMethod: String,
    deliveryMethodOptions: List<String>,
    paymentMethodOptions: List<String>,
    onPaymentMethodChange: (String) -> Unit,
    onDeliveryMethodChange: (String) -> Unit,
    onPayNow: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    if (isLoading) {
        PaymentSkeletonScreen(modifier = modifier)
        return
    }

    var checkoutStep by rememberSaveable { mutableStateOf(CheckoutStep.QUOTATION) }
    LaunchedEffect(paymentSubmitted, paymentStatusMessage, latestQuoteNumber) {
        if (!paymentSubmitted && paymentStatusMessage.isBlank()) {
            checkoutStep = CheckoutStep.QUOTATION
        }
    }

    if (paymentSubmitted) {
        PaymentSubmittedStep(
            modifier = modifier,
            latestQuoteNumber = latestQuoteNumber,
            latestQuoteAmount = latestQuoteAmount,
            selectedPaymentMethod = selectedPaymentMethod,
            selectedDeliveryMethod = selectedDeliveryMethod,
            onFinish = onFinish,
        )
        return
    }

    if (checkoutStep == CheckoutStep.QUOTATION) {
        QuotationStep(
            modifier = modifier,
            latestQuoteNumber = latestQuoteNumber,
            quotationItemsJson = quotationItemsJson,
            quotationIssuedAt = quotationIssuedAt,
            quotationBusinessJson = quotationBusinessJson,
            customerName = signInName,
            customerEmail = signInEmail,
            customerPhone = customerPhone,
            customerAddress = listOf(customerStreetAddress, customerCityProvince, customerPostalCode).filter { it.isNotBlank() }.joinToString(", "),
            latestQuoteSubtotal = latestQuoteSubtotal,
            latestQuoteVat = latestQuoteVat,
            latestQuoteAmount = latestQuoteAmount,
            onConfirm = { checkoutStep = CheckoutStep.COURIER },
            onBack = onBack
        )
        return
    }

    if (checkoutStep == CheckoutStep.COURIER) {
        CourierSelectionStep(
            modifier = modifier,
            quotationNumber = latestQuoteNumber,
            selectedCourier = selectedDeliveryMethod,
            courierOptions = deliveryMethodOptions,
            isSubmitting = isSubmitting,
            onCourierChange = onDeliveryMethodChange,
            onNext = { checkoutStep = CheckoutStep.PAYMENT },
            onBack = { checkoutStep = CheckoutStep.QUOTATION }
        )
        return
    }

    if (checkoutStep == CheckoutStep.ORDER_REVIEW) {
        PaymentReviewStep(
            modifier = modifier,
            signInName = signInName,
            signInEmail = signInEmail,
            customerPhone = customerPhone,
            deliveryAddress = listOf(customerStreetAddress, customerCityProvince, customerPostalCode)
                .filter { it.isNotBlank() }
                .joinToString(", "),
            latestQuoteNumber = latestQuoteNumber,
            quotationItemsJson = quotationItemsJson,
            latestQuoteSubtotal = latestQuoteSubtotal,
            latestQuoteVat = latestQuoteVat,
            latestQuoteAmount = latestQuoteAmount,
            selectedPaymentMethod = selectedPaymentMethod,
            selectedDeliveryMethod = selectedDeliveryMethod,
            onNext = { onPayNow() },
            onBack = { checkoutStep = CheckoutStep.PAYMENT }
        )
        return
    }

    if (checkoutStep == CheckoutStep.PAYMENT) {
        PaymentDetailsStep(
            modifier = modifier,
            isSignedIn = isSignedIn,
            signInName = signInName,
            signInEmail = signInEmail,
            customerStreetAddress = customerStreetAddress,
            customerCityProvince = customerCityProvince,
            customerPostalCode = customerPostalCode,
            latestQuoteNumber = latestQuoteNumber,
            latestQuoteAmount = latestQuoteAmount,
            selectedPaymentMethod = selectedPaymentMethod,
            paymentMethodOptions = paymentMethodOptions,
            isSubmitting = isSubmitting,
            onPaymentMethodChange = onPaymentMethodChange,
            onNext = { checkoutStep = CheckoutStep.ORDER_REVIEW },
            onBack = { checkoutStep = CheckoutStep.COURIER }
        )
        return
    }

    Box(modifier = modifier.fillMaxSize()) {
        val displayNameShort = compactDisplayName(signInName).ifBlank { signInName.substringBefore(" ").ifBlank { signInName } }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 14.dp)
                .padding(bottom = 112.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "Payment",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 25.sp,
                                color = Color(0xFF241B16)
                            )
                            Text("Step 3 of 4 — Payment method", color = Color(0xFF555555), fontSize = 12.sp)
                            PaymentStepHeader(3)
                        }
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFFFFF), RoundedCornerShape(999.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                if (isSignedIn) displayNameShort.ifBlank { "Ready" } else "Sign in",
                                color = if (isSignedIn) Color(0xFFB94D00) else Color(0xFF7A4B12),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                            .padding(17.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Account", color = Color(0xFF3A2D25), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                if (isSignedIn) signInName.ifBlank { "Signed in" } else "Sign in to submit this request.",
                                color = Color(0xFF667085),
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                            if (signInEmail.isNotBlank()) {
                                Text(signInEmail, color = Color(0xFF77675D), fontSize = 13.sp)
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                            .padding(17.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("Delivery address", color = Color(0xFF3A2D25), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                listOf(customerStreetAddress, customerCityProvince, customerPostalCode)
                                    .filter { it.isNotBlank() }
                                    .joinToString(", ")
                                    .ifBlank { "Add a delivery address before payment." },
                                color = if (customerStreetAddress.isBlank() || customerCityProvince.isBlank() || customerPostalCode.isBlank()) Color(0xFFB42318) else Color(0xFF594A41),
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                            .padding(17.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Payment method", color = Color(0xFF3A2D25), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                paymentMethodOptions.forEach { method ->
                                    val selected = method.equals(selectedPaymentMethod, ignoreCase = true)
                                    OutlinedButton(
                                        onClick = { if (!isSubmitting) onPaymentMethodChange(method) },
                                        enabled = !isSubmitting,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (selected) Color(0xFFC65300) else Color.White,
                                            contentColor = if (selected) Color.White else Color(0xFF594A41)
                                        ),
                                        border = BorderStroke(
                                            width = 1.dp,
                                            color = if (selected) Color(0xFFC65300) else Color(0xFFE8C9A8)
                                        )
                                    ) {
                                        Text(
                                            method,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                            .padding(17.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Delivery arrangement", color = Color(0xFF3A2D25), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            deliveryMethodOptions.forEach { method ->
                                val selected = method.equals(selectedDeliveryMethod, ignoreCase = true)
                                OutlinedButton(
                                    onClick = { if (!isSubmitting) onDeliveryMethodChange(method) },
                                    enabled = !isSubmitting,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (selected) Color(0xFFFFF1E3) else Color.White,
                                        contentColor = if (selected) Color(0xFF6B3411) else Color(0xFF77675D)
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (selected) Color(0xFFE8C9A8) else Color(0xFFE8C9A8)
                                    )
                                ) {
                                    Text(method, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    if (latestQuoteNumber.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFFE8D1), RoundedCornerShape(999.dp))
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                latestQuoteNumber,
                                color = Color(0xFF9E4300),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Order summary", color = Color(0xFF3A2D25), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            PaymentDetailRow("Item", latestQuoteItemName.ifBlank { latestQuoteSummary.orEmpty() })
                            PaymentDetailRow("Unit price", latestQuoteUnitPrice)
                            PaymentDetailRow("Panels", latestQuotePanels)
                            PaymentDetailRow("Subtotal", latestQuoteSubtotal)
                            PaymentDetailRow("VAT", latestQuoteVat)
                            Divider(color = Color(0x1A121826))
                            PaymentDetailRow("Quote total", latestQuoteAmount, emphasize = true)
                        }
                    }

                    if (latestQuoteSummary != null || latestQuoteItemName.isNotBlank()) {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text("Overview", color = Color(0xFF121826), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    latestQuoteSummary ?: "Ready to review.",
                                    color = Color(0xFF667085),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    if (paymentStatusMessage.isNotBlank()) {
                        val isFallbackStatus =
                            !paymentSubmitted &&
                            paymentStatusMessage.contains("expired", ignoreCase = true) ||
                                (!paymentSubmitted && paymentStatusMessage.contains("saved locally", ignoreCase = true)) ||
                                (!paymentSubmitted && paymentStatusMessage.contains("saved on this device", ignoreCase = true))
                        val statusColor = when {
                            paymentSubmitted -> Color(0xFF18794E)
                            isFallbackStatus -> Color(0xFF7A4B12)
                            else -> Color(0xFFB42318)
                        }
                        val statusBackground = when {
                            paymentSubmitted -> Color(0xFFEAF8F0)
                            isFallbackStatus -> Color(0xFFFFF7EE)
                            else -> Color(0xFFFFEEEE)
                        }

                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = statusBackground)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    if (paymentSubmitted) "Payment submitted" else "Payment status",
                                    color = statusColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(paymentStatusMessage, color = statusColor, fontSize = 13.sp, lineHeight = 20.sp)
                            }
                        }
                    }

                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { checkoutStep = CheckoutStep.ORDER_REVIEW },
                enabled = isSignedIn && latestQuoteNumber.isNotBlank() && customerStreetAddress.isNotBlank() && customerCityProvince.isNotBlank() && customerPostalCode.matches(Regex("\\d{4}")) && !isSubmitting && !paymentSubmitted,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC65300)),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text(
                    when {
                        isSubmitting -> "Submitting..."
                        paymentSubmitted -> "Submitted ✓"
                        !isSignedIn -> "Sign in to submit"
                        !customerStreetAddress.isNotBlank() || !customerCityProvince.isNotBlank() || !customerPostalCode.matches(Regex("\\d{4}")) -> "Add delivery address"
                        latestQuoteAmount.isNotBlank() -> "Next — review order"
                        else -> "Next — review order"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            OutlinedButton(
                onClick = { if (!isSubmitting) onBack() },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.dp, Color(0xFFE8C9A8)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF34261F))
            ) { Text("Back", fontSize = 15.sp) }
        }

    }
}

@Composable
private fun PaymentFlowStep(step: String, label: String, active: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(
                    if (active) Color(0xFFF77F2C) else Color(0xFFF4EFE7),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                step,
                color = if (active) Color.White else Color(0xFF8B6B4E),
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Text(
            label,
            modifier = Modifier.weight(1f),
            color = if (active) Color(0xFF121826) else Color(0xFF667085),
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun PaymentDetailRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            modifier = Modifier.width(68.dp),
            color = if (emphasize) Color(0xFF121826) else Color(0xFF667085),
            fontSize = if (emphasize) 13.sp else 12.sp,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            lineHeight = 17.sp
        )
        Text(
            value.ifBlank { "--" },
            modifier = Modifier.weight(1f),
            color = if (emphasize) Color(0xFFF77F2C) else Color(0xFF121826),
            fontSize = if (emphasize) 15.sp else 13.sp,
            fontWeight = if (emphasize) FontWeight.ExtraBold else FontWeight.Bold,
            textAlign = TextAlign.End,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun SettingsStatBlock(label: String, value: String, accent: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F0E7))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                label,
                color = if (accent) Color(0xFFC97A2B) else Color(0xFFB59A7A),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                value.ifBlank { "--" },
                color = if (accent) Color(0xFFC97A2B) else Color(0xFF121826),
                fontSize = if (accent) 16.sp else 15.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            title,
            color = Color(0xFF8D4E15),
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
        if (!subtitle.isNullOrBlank()) {
            Text(
                subtitle,
                color = Color(0xFF667085),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun SettingsMenuRow(
    title: String,
    description: String,
    icon: ImageVector,
    iconTint: Color = Color(0xFFF77F2C),
    iconBackground: Color = Color(0xFFFFE4C6),
    chevronTint: Color = Color(0xFFC7B299),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .heightIn(min = 72.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(iconBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint)
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                title,
                color = Color(0xFF121826),
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                description,
                color = Color(0xFF667085),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = chevronTint
        )
    }
}

@Composable
private fun CustomerInformationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            modifier = Modifier.weight(0.42f),
            color = Color(0xFF667085),
            fontSize = 13.sp
        )
        Text(
            value.ifBlank { "Not provided" },
            modifier = Modifier.weight(0.58f),
            color = Color(0xFF121826),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AuthBrandHeader(
    subtitle: String,
    compact: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.client_logo_circle),
            contentDescription = "Aiyige logo",
            modifier = Modifier.size(if (compact) 56.dp else 72.dp).clip(RoundedCornerShape(if (compact) 16.dp else 20.dp)),
            contentScale = ContentScale.Crop
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Aiyige", color = Color(0xFF202124), fontSize = if (compact) 20.sp else 25.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = Color(0xFF9E4300), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isSignedIn: Boolean,
    signInName: String,
    signInEmail: String,
    signInUsername: String,
    requestCount: Int,
    latestQuoteSummary: String?,
    latestQuoteItemName: String,
    latestQuoteUnitPrice: String,
    latestQuoteSubtotal: String,
    latestQuoteVat: String,
    latestQuotePanels: String,
    latestQuoteAmount: String,
    latestQuoteNumber: String,
    paymentStatusMessage: String,
    memberSince: String,
    backendBaseUrl: String,
    profileUsername: String,
    profileFirstName: String,
    profileMiddleName: String,
    profileLastName: String,
    profileDateOfBirth: String,
    profileGender: String,
    profileMobileNumber: String,
    profileStreetAddress: String,
    profileCityProvince: String,
    profilePostalCode: String,
    recentQuoteHistory: List<QuoteHistoryEntry>,
    registeredUsers: List<RegisteredUser>,
    authNotice: String,
    isAuthEmailRequestInFlight: Boolean,
    verificationEmail: String,
    verificationType: String,
    onSaveProfile: (String, String, String, String, String, String, String, String, String) -> Unit,
    onChangePassword: (String) -> Unit,
    onRegisterUser: (RegisteredUser) -> Unit,
    onRequestPasswordRecovery: (String) -> Unit,
    onVerifyEmailOtp: (String) -> Unit,
    onOpenAddress: () -> Unit,
    page: SettingsPage,
    onPageChange: (SettingsPage) -> Unit,
    onSignIn: (String, String) -> Unit,
    onUseDemoAccount: () -> Unit,
    onSignOut: () -> Unit,
    onBackendBaseUrlChange: (String) -> Unit,
    onSaveBackendBaseUrl: (String) -> Unit,
    onOpenCatalog: () -> Unit
) {
    val scrollState = rememberScrollState()
    var loginIdentifier by rememberSaveable { mutableStateOf("") }
    var loginPassword by rememberSaveable { mutableStateOf("") }
    var registerFirstName by rememberSaveable { mutableStateOf("") }
    var registerMiddleName by rememberSaveable { mutableStateOf("") }
    var registerLastName by rememberSaveable { mutableStateOf("") }
    var registerDateOfBirth by rememberSaveable { mutableStateOf("") }
    var registerGender by rememberSaveable { mutableStateOf("") }
    var registerMobileNumber by rememberSaveable { mutableStateOf("") }
    var registerStreetAddress by rememberSaveable { mutableStateOf("") }
    var registerCityProvince by rememberSaveable { mutableStateOf("") }
    var registerProvince by rememberSaveable { mutableStateOf("") }
    var registerPostalCode by rememberSaveable { mutableStateOf("") }
    var registerEmail by rememberSaveable { mutableStateOf("") }
    var registerPassword by rememberSaveable { mutableStateOf("") }
    var registerConfirmPassword by rememberSaveable { mutableStateOf("") }
    var acceptTerms by rememberSaveable { mutableStateOf(false) }
    var acceptPrivacy by rememberSaveable { mutableStateOf(false) }
    var registerSubmitted by rememberSaveable { mutableStateOf(false) }
    var registerStep by rememberSaveable { mutableIntStateOf(1) }
    var registerReviewPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var loginSubmitted by rememberSaveable { mutableStateOf(false) }
    var loginFeedback by rememberSaveable { mutableStateOf("") }
    var loginPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var registerGenderMenuExpanded by rememberSaveable { mutableStateOf(false) }
    var registerPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var registerConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var profilePasswordVisible by rememberSaveable { mutableStateOf(false) }
    var profileCurrentPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var profileConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var recoveryPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var recoveryConfirmPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var showProfileEditor by rememberSaveable { mutableStateOf(false) }
    var showSecurityEditor by rememberSaveable { mutableStateOf(false) }
    var showNotificationsEditor by rememberSaveable { mutableStateOf(false) }
    var showQuotesHistory by rememberSaveable { mutableStateOf(false) }
    var showHelpDialog by rememberSaveable { mutableStateOf(false) }
    var showTermsDialog by rememberSaveable { mutableStateOf(false) }
    var showAboutDialog by rememberSaveable { mutableStateOf(false) }
    var showEmailDialog by rememberSaveable { mutableStateOf(false) }
    var showPaymentPreferencesDialog by rememberSaveable { mutableStateOf(false) }
    var showRecentlyViewedDialog by rememberSaveable { mutableStateOf(false) }
    var showVersionDialog by rememberSaveable { mutableStateOf(false) }
    var supportTopic by rememberSaveable { mutableStateOf("Help center") }
    var quotationAlertsEnabled by rememberSaveable { mutableStateOf(true) }
    var panelAlertsEnabled by rememberSaveable { mutableStateOf(true) }
    val profileSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val securitySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val notificationsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val quotesSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var profileFirstDraft by rememberSaveable { mutableStateOf("") }
    var profileMiddleDraft by rememberSaveable { mutableStateOf("") }
    var profileLastDraft by rememberSaveable { mutableStateOf("") }
    var profileDobDraft by rememberSaveable { mutableStateOf("") }
    var profileGenderDraft by rememberSaveable { mutableStateOf("") }
    var profileMobileDraft by rememberSaveable { mutableStateOf("") }
    var profileStreetDraft by rememberSaveable { mutableStateOf("") }
    var profileCityDraft by rememberSaveable { mutableStateOf("") }
    var profilePostalDraft by rememberSaveable { mutableStateOf("") }
    var profileUsernameDraft by rememberSaveable { mutableStateOf("") }
    var currentPasswordDraft by rememberSaveable { mutableStateOf("") }
    var passwordDraft by rememberSaveable { mutableStateOf("") }
    var confirmPasswordDraft by rememberSaveable { mutableStateOf("") }
    var recoveryPasswordDraft by rememberSaveable { mutableStateOf("") }
    var recoveryPasswordConfirmDraft by rememberSaveable { mutableStateOf("") }
    var backendUrlDraft by rememberSaveable(backendBaseUrl) { mutableStateOf(backendBaseUrl) }
    var verificationCode by rememberSaveable(verificationEmail, verificationType) { mutableStateOf("") }
    val settingsAvatarLabel = remember(signInName) {
        signInName
            .trim()
            .split(Regex("\\s+"))
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { part -> part.take(1).uppercase(Locale.getDefault()) }
            .ifBlank { "CU" }
    }

    LaunchedEffect(backendBaseUrl) {
        if (backendUrlDraft != backendBaseUrl) {
            backendUrlDraft = backendBaseUrl
        }
    }

    LaunchedEffect(
        profileUsername,
        profileFirstName,
        profileMiddleName,
        profileLastName,
        profileDateOfBirth,
        profileGender,
        profileMobileNumber,
        profileStreetAddress,
        profileCityProvince,
        profilePostalCode
    ) {
        profileUsernameDraft = profileUsername
        profileFirstDraft = profileFirstName
        profileMiddleDraft = profileMiddleName
        profileLastDraft = profileLastName
        profileDobDraft = profileDateOfBirth
        profileGenderDraft = profileGender
        profileMobileDraft = profileMobileNumber
        profileStreetDraft = profileStreetAddress
        profileCityDraft = profileCityProvince
        profilePostalDraft = profilePostalCode
    }

    LaunchedEffect(page, isSignedIn) {
        scrollState.scrollTo(0)
    }

    val registrationValidation = remember(
        registerFirstName,
        registerMiddleName,
        registerLastName,
        registerDateOfBirth,
        registerGender,
        registerEmail,
        registerMobileNumber,
        registerStreetAddress,
        registerCityProvince,
        registerPostalCode,
        registerPassword,
        registerConfirmPassword,
        acceptTerms,
        acceptPrivacy,
        registeredUsers
    ) {
        validateRegistrationForm(
            firstName = registerFirstName,
            lastName = registerLastName,
            middleName = registerMiddleName,
            dateOfBirth = registerDateOfBirth,
            gender = registerGender,
            email = registerEmail,
            mobileNumber = registerMobileNumber,
            streetAddress = registerStreetAddress,
            cityProvince = registerCityProvince,
            postalCode = registerPostalCode,
            password = registerPassword,
            confirmPassword = registerConfirmPassword,
            termsAccepted = acceptTerms,
            privacyAccepted = acceptPrivacy,
            registeredUsers = registeredUsers
        )
    }

    val loginErrorVisible = loginSubmitted && loginFeedback.isNotBlank()
    val showRegisterErrors = registerSubmitted
    val authFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Color.White,
        focusedContainerColor = Color.White,
        unfocusedBorderColor = Color(0xFFE7DDD1),
        focusedBorderColor = Color(0xFFF77F2C),
        cursorColor = Color(0xFFF77F2C)
    )

    if (page == SettingsPage.ForgotPassword && !isSignedIn) {
        Column(
            modifier = modifier.fillMaxSize().verticalScroll(scrollState).background(Color.White).padding(horizontal = 34.dp, vertical = 38.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Forgot password?", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF202124))
            Text("Enter your email address and we’ll send you a reset code.", color = Color(0xFF777777), fontSize = 14.sp, lineHeight = 22.sp)
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(loginIdentifier, { loginIdentifier = it }, modifier = Modifier.fillMaxWidth().height(52.dp), label = { Text("Email address") }, placeholder = { Text("name@email.com") }, singleLine = true, shape = RoundedCornerShape(10.dp), colors = authFieldColors)
                    if (authNotice.isNotBlank()) Text(authNotice, color = Color(0xFF777777), fontSize = 13.sp)
                    Button(onClick = { onRequestPasswordRecovery(loginIdentifier.trim()) }, enabled = !isAuthEmailRequestInFlight, modifier = Modifier.fillMaxWidth().height(47.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(10.dp)) { Text("Send reset code", color = Color.White, fontWeight = FontWeight.Bold) }
                    TextButton(onClick = { onPageChange(SettingsPage.Login) }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Back to sign in", color = Color(0xFFF57419), fontWeight = FontWeight.SemiBold) }
                }
            }
        }
        return
    }

    if (page == SettingsPage.VerifyEmail && !isSignedIn) {
        Column(modifier = modifier.fillMaxSize().imePadding().verticalScroll(scrollState).background(Color.White).padding(horizontal = 34.dp, vertical = 38.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Verify your email", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF202124))
            Text(authNotice.ifBlank { "Enter the verification code sent to your email." }, color = Color(0xFF777777), fontSize = 14.sp, lineHeight = 22.sp)
            OutlinedTextField(verificationCode, { verificationCode = it.filter(Char::isDigit).take(8) }, modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp), label = { Text("Verification code") }, singleLine = true, shape = RoundedCornerShape(10.dp), colors = authFieldColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Button(onClick = { onVerifyEmailOtp(verificationCode) }, enabled = verificationCode.length in 6..8 && !isAuthEmailRequestInFlight, modifier = Modifier.fillMaxWidth().height(47.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(10.dp)) { Text("Verify code", color = Color.White, fontWeight = FontWeight.Bold) }
            TextButton(onClick = { onPageChange(SettingsPage.Login) }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Back to sign in", color = Color(0xFFF57419), fontWeight = FontWeight.SemiBold) }
        }
        return
    }

    if (page == SettingsPage.RecoverPassword && !isSignedIn) {
        Column(modifier = modifier.fillMaxSize().verticalScroll(scrollState).background(Color.White).padding(horizontal = 34.dp, vertical = 38.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Create a new password", fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF202124))
            Text("Choose a secure password for your account.", color = Color(0xFF777777), fontSize = 14.sp, lineHeight = 22.sp)
            OutlinedTextField(recoveryPasswordDraft, { recoveryPasswordDraft = it }, modifier = Modifier.fillMaxWidth().height(52.dp), label = { Text("New password") }, singleLine = true, visualTransformation = if (recoveryPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { recoveryPasswordVisible = !recoveryPasswordVisible }) { Icon(if (recoveryPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (recoveryPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF777777)) } }, shape = RoundedCornerShape(10.dp), colors = authFieldColors)
            OutlinedTextField(recoveryPasswordConfirmDraft, { recoveryPasswordConfirmDraft = it }, modifier = Modifier.fillMaxWidth().height(52.dp), label = { Text("Confirm password") }, singleLine = true, visualTransformation = if (recoveryConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { recoveryConfirmPasswordVisible = !recoveryConfirmPasswordVisible }) { Icon(if (recoveryConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (recoveryConfirmPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF777777)) } }, shape = RoundedCornerShape(10.dp), colors = authFieldColors)
            Button(onClick = { if (recoveryPasswordDraft.isNotBlank() && recoveryPasswordDraft == recoveryPasswordConfirmDraft) onChangePassword(recoveryPasswordDraft) }, enabled = recoveryPasswordDraft.isNotBlank() && recoveryPasswordDraft == recoveryPasswordConfirmDraft && !isAuthEmailRequestInFlight, modifier = Modifier.fillMaxWidth().height(47.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(10.dp)) { Text("Update password", color = Color.White, fontWeight = FontWeight.Bold) }
        }
        return
    }

    // Login is also the initial settings state for signed-out customers. Keep
    // it inside SettingsScreen so opening the Settings tab never produces an
    // empty content area while authentication is required.
    if (page == SettingsPage.Login && !isSignedIn) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF7EB), Color(0xFFF4EFE7))))
                .padding(horizontal = 22.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AuthBrandHeader("Customer sign in")
            Text("Welcome back", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
            Text("Sign in to view your panels, quotations, and orders.", color = Color(0xFF667085), fontSize = 13.sp, lineHeight = 19.sp)
            Text("Email address", color = Color(0xFF222222), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = loginIdentifier,
                onValueChange = { loginIdentifier = it },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                placeholder = { Text("name@email.com", color = Color(0xFF999999)) },
                singleLine = true,
                trailingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = Color(0xFF777777)) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFD8D2CB), focusedBorderColor = Color(0xFFF57419), unfocusedTextColor = Color(0xFF222222), focusedTextColor = Color(0xFF222222), unfocusedPlaceholderColor = Color(0xFF888888), focusedPlaceholderColor = Color(0xFF888888), cursorColor = Color(0xFFF57419)),
                shape = RoundedCornerShape(10.dp)
            )
            Text("Password", color = Color(0xFF222222), fontSize = 13.sp, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = loginPassword,
                onValueChange = { loginPassword = it },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                placeholder = { Text("••••••••••", color = Color(0xFF888888)) },
                singleLine = true,
                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                        Icon(
                            imageVector = if (loginPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (loginPasswordVisible) "Hide password" else "Show password",
                            tint = Color(0xFF777777),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = Color(0xFFD8D2CB), focusedBorderColor = Color(0xFFF57419), unfocusedTextColor = Color(0xFF222222), focusedTextColor = Color(0xFF222222), unfocusedPlaceholderColor = Color(0xFF888888), focusedPlaceholderColor = Color(0xFF888888), cursorColor = Color(0xFFF57419)),
                shape = RoundedCornerShape(10.dp)
            )
            if (loginErrorVisible) Text(loginFeedback, color = Color(0xFFC62828), fontSize = 13.sp)
            if (authNotice.isNotBlank()) Text(authNotice, color = Color(0xFF4F7C26), fontSize = 13.sp)
            TextButton(onClick = { onPageChange(SettingsPage.ForgotPassword) }, modifier = Modifier.align(Alignment.End)) { Text("Forgot password?", color = Color(0xFFF57419), fontWeight = FontWeight.SemiBold) }
            Button(onClick = { loginSubmitted = true; onSignIn(loginIdentifier.trim(), loginPassword) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(10.dp)) { Text("Sign in", color = Color.White, fontWeight = FontWeight.Bold) }
            OutlinedButton(
                onClick = onUseDemoAccount,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                border = BorderStroke(1.dp, Color(0xFFF57419)),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFF57419))
            ) { Text("Continue as Demo Customer", fontWeight = FontWeight.Bold) }
            Text("or continue with", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.Black, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(46.dp),
                border = BorderStroke(1.dp, Color(0xFFB8B8B8)),
                shape = RoundedCornerShape(10.dp)
            ) { Text("◉  Sign in with Google", color = Color.Black, fontWeight = FontWeight.SemiBold) }
            TextButton(onClick = { registerStep = 1; onPageChange(SettingsPage.Register) }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Don't have an account? ", color = Color.Black); Text("Create one", color = Color(0xFFF57419), fontWeight = FontWeight.Bold) }
        }
        return
    }

    if (page == SettingsPage.Register && !isSignedIn) {
        val registrationScope = rememberCoroutineScope()
        val firstNameBringIntoView = remember { BringIntoViewRequester() }
        val lastNameBringIntoView = remember { BringIntoViewRequester() }
        val middleNameBringIntoView = remember { BringIntoViewRequester() }
        val genderBringIntoView = remember { BringIntoViewRequester() }
        val emailBringIntoView = remember { BringIntoViewRequester() }
        val mobileBringIntoView = remember { BringIntoViewRequester() }
        val passwordBringIntoView = remember { BringIntoViewRequester() }
        val confirmPasswordBringIntoView = remember { BringIntoViewRequester() }
        val streetBringIntoView = remember { BringIntoViewRequester() }
        val cityBringIntoView = remember { BringIntoViewRequester() }
        val provinceBringIntoView = remember { BringIntoViewRequester() }
        val postalBringIntoView = remember { BringIntoViewRequester() }
        fun keyboardAware(requester: BringIntoViewRequester): Modifier = Modifier
            .bringIntoViewRequester(requester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    registrationScope.launch {
                        delay(150)
                        requester.bringIntoView()
                    }
                }
            }
        if (registerStep == 2) {
            val contactFieldColors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF3F3F3), focusedContainerColor = Color(0xFFF3F3F3),
                unfocusedBorderColor = Color(0xFFD7D7D7), focusedBorderColor = Color(0xFFF57419),
                unfocusedTextColor = Color(0xFF222222), focusedTextColor = Color(0xFF222222), cursorColor = Color(0xFFF57419)
            )
            Column(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF7EB), Color(0xFFF4EFE7)))).imePadding().verticalScroll(scrollState).padding(horizontal = 22.dp, vertical = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AuthBrandHeader("Create your customer account", compact = true)
                Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { registerStep = 1 }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }; Column { Text("Create account", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Step 2 of 3 — Contact info", color = Color(0xFF777777), fontSize = 12.sp) } }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) { listOf("✓" to "Personal", "2" to "Contact", "3" to "Review").forEachIndexed { i, item -> Column(horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(34.dp).background(if (i < 2) Color(0xFFF57419) else Color.Black, CircleShape), contentAlignment = Alignment.Center) { Text(item.first, color = Color.White, fontWeight = FontWeight.Bold) }; Text(item.second, color = if (i == 1) Color(0xFFF57419) else Color(0xFF777777), fontSize = 12.sp, fontWeight = FontWeight.Bold) } } }
                Text("Contact details", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(registerMobileNumber, { registerMobileNumber = it.filter(Char::isDigit).take(11) }, label = { Text("Mobile number *") }, placeholder = { Text("09XX XXX XXXX") }, singleLine = true, modifier = keyboardAware(mobileBringIntoView).fillMaxWidth(), colors = contactFieldColors, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                OutlinedTextField(registerStreetAddress, { registerStreetAddress = it.replaceFirstChar { char -> char.uppercase() } }, label = { Text("Complete address *") }, placeholder = { Text("House no., street, barangay") }, singleLine = true, modifier = keyboardAware(streetBringIntoView).fillMaxWidth(), colors = contactFieldColors, shape = RoundedCornerShape(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = registerCityProvince,
                        onValueChange = { registerCityProvince = it.replaceFirstChar { char -> char.uppercase() } },
                        placeholder = { Text("City / Municipality") },
                        singleLine = true,
                        modifier = keyboardAware(cityBringIntoView).weight(1f).height(64.dp),
                        colors = contactFieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )
                    OutlinedTextField(
                        value = registerProvince,
                        onValueChange = { registerProvince = it.replaceFirstChar { char -> char.uppercase() } },
                        placeholder = { Text("Province") },
                        singleLine = true,
                        modifier = keyboardAware(provinceBringIntoView).weight(1f).height(64.dp),
                        colors = contactFieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                OutlinedTextField(registerPostalCode, { registerPostalCode = it.filter(Char::isDigit).take(4) }, label = { Text("ZIP code *") }, placeholder = { Text("4 digits") }, singleLine = true, modifier = keyboardAware(postalBringIntoView).width(195.dp), colors = contactFieldColors, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Button(onClick = { registerSubmitted = true; if (registrationValidation.errors["mobileNumber"] == null && registrationValidation.errors["streetAddress"] == null && registrationValidation.errors["postalCode"] == null) { registerCityProvince = listOf(registerCityProvince, registerProvince).filter { it.isNotBlank() }.joinToString(", "); registerStep = 3 } }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(10.dp)) { Text("Next — review details", color = Color.White, fontWeight = FontWeight.Bold) }
                TextButton(onClick = { registerStep = 1 }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Back", color = Color(0xFF999999)) }
            }
            return
        }
        if (registerStep == 3) {
            Column(modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF7EB), Color(0xFFF4EFE7)))).imePadding().verticalScroll(scrollState).padding(horizontal = 22.dp, vertical = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AuthBrandHeader("Review your account", compact = true)
                Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { registerStep = 2 }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }; Column { Text("Create account", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Step 3 of 3 — Review details", color = Color(0xFF777777), fontSize = 12.sp) } }
                Text("Review and submit", color = Color(0xFF202124), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("${registerFirstName.trim()} ${registerLastName.trim()}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(registerEmail, color = Color(0xFF777777))
                listOf("PERSONAL" to "Full name|${registerFirstName.trim()} ${registerLastName.trim()}\\nDate of birth|${registerDateOfBirth}\\nGender|${registerGender}", "CONTACT" to "Mobile|${registerMobileNumber}\\nCity|${registerCityProvince}\\nZIP|${registerPostalCode}").forEach { section -> Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)), shape = RoundedCornerShape(12.dp)) { Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text(section.first, color = Color(0xFFF57419), fontWeight = FontWeight.Bold, fontSize = 12.sp); section.second.split("\\n").forEach { row -> val pair = row.split("|", limit = 2); Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(pair[0], color = Color(0xFF777777), fontSize = 12.sp); Text(pair.getOrElse(1) { "" }, fontWeight = FontWeight.Bold, fontSize = 12.sp) } } } } }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F3)), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("ACCOUNT", color = Color(0xFFF57419), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Email", color = Color(0xFF777777), fontSize = 12.sp); Text(registerEmail, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Password", color = Color(0xFF777777), fontSize = 12.sp); Row(verticalAlignment = Alignment.CenterVertically) { Text(if (registerReviewPasswordVisible) registerPassword else "••••••••", fontWeight = FontWeight.Bold, fontSize = 12.sp); IconButton(onClick = { registerReviewPasswordVisible = !registerReviewPasswordVisible }, modifier = Modifier.size(28.dp)) { Icon(if (registerReviewPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (registerReviewPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF777777), modifier = Modifier.size(18.dp)) } } }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("Terms agreed", color = Color(0xFF777777), fontSize = 12.sp); Text("Yes", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    }
                }
                Button(onClick = { onRegisterUser(RegisteredUser(buildRegistrationUsername(registerFirstName, registerLastName, registerEmail, registeredUsers), registerFirstName.trim(), registerLastName.trim(), registerMiddleName.trim(), registerDateOfBirth.trim(), registerGender.trim(), registerEmail.trim(), registerMobileNumber.trim(), registerStreetAddress.trim(), registerCityProvince.trim(), registerPostalCode.trim(), registerPassword, formatCaptureTimestamp())) }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(10.dp)) { Text("Create account", color = Color.White, fontWeight = FontWeight.Bold) }
                TextButton(onClick = { registerStep = 2 }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Back", color = Color(0xFF999999)) }
            }
            return
        }
        val registerFieldColors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF3F3F3),
            focusedContainerColor = Color(0xFFF3F3F3),
            unfocusedBorderColor = Color(0xFFD7D7D7),
            focusedBorderColor = Color(0xFFF57419),
            unfocusedTextColor = Color(0xFF222222),
            focusedTextColor = Color(0xFF222222),
            unfocusedPlaceholderColor = Color(0xFF999999),
            focusedPlaceholderColor = Color(0xFF999999),
            cursorColor = Color(0xFFF57419)
        )
        Column(
            modifier = modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFFFF7EB), Color(0xFFF4EFE7)))).imePadding().verticalScroll(scrollState).padding(horizontal = 22.dp, vertical = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AuthBrandHeader("Create your customer account", compact = true)
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onPageChange(SettingsPage.Login) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }
                Column { Text("Create account", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Fill in your details to get started", color = Color(0xFF777777), fontSize = 12.sp) }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                listOf("1" to "Personal", "2" to "Contact", "3" to "Account").forEachIndexed { index, item ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(34.dp).background(if (index == 0) Color(0xFFF57419) else Color.Black, CircleShape), contentAlignment = Alignment.Center) { Text(item.first, color = Color.White, fontWeight = FontWeight.Bold) }
                        Text(item.second, color = if (index == 0) Color(0xFFF57419) else Color(0xFF777777), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("Personal information", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(registerFirstName, { registerFirstName = it.replaceFirstChar { char -> char.uppercase() } }, label = { Text("First name *") }, singleLine = true, modifier = keyboardAware(firstNameBringIntoView).weight(1f), colors = registerFieldColors, shape = RoundedCornerShape(10.dp))
                OutlinedTextField(registerLastName, { registerLastName = it.replaceFirstChar { char -> char.uppercase() } }, label = { Text("Last name *") }, singleLine = true, modifier = keyboardAware(lastNameBringIntoView).weight(1f), colors = registerFieldColors, shape = RoundedCornerShape(10.dp))
            }
            OutlinedTextField(registerMiddleName, { registerMiddleName = it.replaceFirstChar { char -> char.uppercase() } }, label = { Text("Middle name (optional)") }, placeholder = { Text("Enter middle name") }, singleLine = true, modifier = keyboardAware(middleNameBringIntoView).fillMaxWidth(), colors = registerFieldColors, shape = RoundedCornerShape(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DateOfBirthPickerField(registerDateOfBirth, { registerDateOfBirth = it }, modifier = Modifier.weight(1f), label = "Date of birth *", placeholder = "MM/DD/YYYY")
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = registerGender,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Gender *") },
                        placeholder = { Text("Select") },
                        singleLine = true,
                        modifier = keyboardAware(genderBringIntoView)
                            .fillMaxWidth(),
                        trailingIcon = { Text("⌄", color = Color(0xFF777777), fontSize = 18.sp) },
                        colors = registerFieldColors,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { registerGenderMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = registerGenderMenuExpanded,
                        onDismissRequest = { registerGenderMenuExpanded = false }
                    ) {
                        listOf("Male", "Female", "Rather not to say").forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    registerGender = option
                                    registerGenderMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            Text("Account credentials", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 14.dp))
            OutlinedTextField(registerEmail, { registerEmail = it }, label = { Text("Email address *") }, placeholder = { Text("name@email.com") }, singleLine = true, modifier = keyboardAware(emailBringIntoView).fillMaxWidth(), colors = registerFieldColors, shape = RoundedCornerShape(10.dp))
            OutlinedTextField(registerMobileNumber, { registerMobileNumber = it.filter(Char::isDigit).take(11) }, label = { Text("Mobile number *") }, placeholder = { Text("09XXXXXXXXX") }, singleLine = true, modifier = keyboardAware(mobileBringIntoView).fillMaxWidth(), colors = registerFieldColors, shape = RoundedCornerShape(10.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            val passwordStrength = when {
                registerPassword.isBlank() -> 0
                isStrongPassword(registerPassword) -> 3
                registerPassword.length >= 6 && registerPassword.any(Char::isUpperCase) && registerPassword.any(Char::isDigit) -> 2
                else -> 1
            }
            val passwordStrengthColor = when (passwordStrength) {
                3 -> Color(0xFF4D9B27)
                2 -> Color(0xFFE5B422)
                1 -> Color(0xFFE04B3F)
                else -> Color(0xFFD7D7D7)
            }
            OutlinedTextField(registerPassword, { registerPassword = it }, label = { Text("Password *") }, singleLine = true, modifier = keyboardAware(passwordBringIntoView).fillMaxWidth(), visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) { Icon(if (registerPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (registerPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF999999)) } }, colors = registerFieldColors, shape = RoundedCornerShape(10.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFFE1E1E1))
                ) {
                    if (passwordStrength > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(passwordStrength / 3f)
                                .fillMaxHeight()
                                .background(passwordStrengthColor)
                        )
                    }
                    Text(
                        when (passwordStrength) {
                            3 -> "Strong password"
                            2 -> "Medium password"
                            1 -> "Weak password"
                            else -> "Strong — add a special character to max strength"
                        },
                        color = if (passwordStrength == 3) Color.White else Color(0xFF555555),
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().align(Alignment.Center)
                    )
                }
            }
            OutlinedTextField(registerConfirmPassword, { registerConfirmPassword = it }, label = { Text("Confirm password *") }, singleLine = true, modifier = keyboardAware(confirmPasswordBringIntoView).fillMaxWidth(), visualTransformation = if (registerConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { registerConfirmPasswordVisible = !registerConfirmPasswordVisible }) { Icon(if (registerConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (registerConfirmPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF999999)) } }, colors = registerFieldColors, shape = RoundedCornerShape(10.dp))
            if (registerSubmitted && registrationValidation.errors.isNotEmpty()) Text(registrationValidation.errors.values.first(), color = Color(0xFFC62828), fontSize = 12.sp)
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(checked = acceptTerms && acceptPrivacy, onCheckedChange = { acceptTerms = it; acceptPrivacy = it })
                Text("I agree to the Terms and Conditions and Privacy Policy", color = Color(0xFF777777), fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
            }
            Button(
                onClick = {
                    registerSubmitted = true
                    if (registrationValidation.errors["firstName"] == null &&
                        registrationValidation.errors["lastName"] == null &&
                        registrationValidation.errors["dateOfBirth"] == null &&
                        registrationValidation.errors["gender"] == null &&
                        registrationValidation.errors["email"] == null &&
                        registrationValidation.errors["password"] == null &&
                        registrationValidation.errors["confirmPassword"] == null &&
                        registrationValidation.errors["terms"] == null &&
                        registrationValidation.errors["privacy"] == null
                    ) registerStep = 2
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)),
                shape = RoundedCornerShape(10.dp)
            ) { Text("Next — contact info", color = Color.White, fontWeight = FontWeight.Bold) }
            TextButton(onClick = { onPageChange(SettingsPage.Login) }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Already have an account? ", color = Color(0xFF777777)); Text("Sign in", color = Color(0xFFF57419), fontWeight = FontWeight.Bold) }
        }
        return
    }

    if (page == SettingsPage.Home || page == SettingsPage.Login) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPageChange(SettingsPage.ProfileDetails) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color(0xFFF57419), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(settingsAvatarLabel, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(signInName.ifBlank { "Your account" }, color = Color(0xFF202124), fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        Text(signInEmail.ifBlank { "your@email.com" }, color = Color(0xFFAAAAAA), fontSize = 13.sp)
                        Text(
                            "✓ Verified account",
                            color = Color(0xFF4F7C26),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .background(Color(0xFFEAF7D8), RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsSectionHeader("ACCOUNT")
                    SettingsMenuRow("Profile details", "Name, birthday, mobile", Icons.Outlined.AccountCircle, Color(0xFFF57419), Color(0xFFFFF0E5)) { onPageChange(SettingsPage.ProfileDetails) }
                    Divider(color = Color(0xFFF1EEE9))
                    SettingsMenuRow("Delivery address", "Add or update your delivery address", Icons.Outlined.Home, Color(0xFFF57419), Color(0xFFFFF0E5)) { onOpenAddress() }
                    Divider(color = Color(0xFFF1EEE9))
                    SettingsMenuRow("Password", "Change your password", Icons.Outlined.Lock, Color(0xFFF57419), Color(0xFFFFF0E5)) { onPageChange(SettingsPage.Password) }
                    Divider(color = Color(0xFFF1EEE9))
                    SettingsMenuRow("Notifications", "Quotation and panel alerts", Icons.Outlined.NotificationsNone, Color(0xFF2563EB), Color(0xFFEAF2FF)) { onPageChange(SettingsPage.Notifications) }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsSectionHeader("MY ACTIVITY")
                    SettingsMenuRow("My quotations", "View saved quotations", Icons.Outlined.ReceiptLong, Color(0xFF5B8F2B), Color(0xFFEAF6DF)) { onPageChange(SettingsPage.Quotations) }
                    Divider(color = Color(0xFFF1EEE9))
                    SettingsMenuRow("Saved panels", "Your panel wishlist", Icons.Outlined.BookmarkBorder, Color(0xFF5B8F2B), Color(0xFFEAF6DF), onClick = onOpenCatalog)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    SettingsSectionHeader("SUPPORT")
                    SettingsMenuRow("Help and support", "FAQs and contact", Icons.Outlined.HelpOutline, Color(0xFF777777), Color(0xFFF1EFEA)) { onPageChange(SettingsPage.HelpSupport) }
                    Divider(color = Color(0xFFF1EEE9))
                    SettingsMenuRow("Terms and privacy", "Policies and agreements", Icons.Outlined.Lock, Color(0xFF777777), Color(0xFFF1EFEA)) { onPageChange(SettingsPage.TermsPrivacy) }
                    Divider(color = Color(0xFFF1EEE9))
                    SettingsMenuRow("About the app", "Version and proponents", Icons.Outlined.Info, Color(0xFF777777), Color(0xFFF1EFEA)) { onPageChange(SettingsPage.AboutApp) }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                SettingsMenuRow(
                    title = "Sign out",
                    description = "End your session",
                    icon = Icons.Outlined.AccountCircle,
                    iconTint = Color(0xFFE55353),
                    iconBackground = Color(0xFFFFECEC),
                    chevronTint = Color(0xFFE55353),
                    onClick = onSignOut
                )
            }
        }

        // The detail screens must be siblings of the Home screen. They were
        // previously nested inside the Home condition, which made every one
        // of them render nothing when selected.
    }

        if (page == SettingsPage.ProfileDetails) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F2EC))
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 34.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text("AR Panels", color = Color(0xFF898681), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onPageChange(SettingsPage.Home) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE))
                    }
                    Column {
                        Text("Profile details", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Update your info", color = Color(0xFF777777), fontSize = 12.sp)
                    }
                }
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier.size(56.dp).background(Color(0xFFF57419), CircleShape),
                            contentAlignment = Alignment.Center
                        ) { Text(settingsAvatarLabel, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold) }
                        Text("Change photo", color = Color(0xFFF57419), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                val profileFieldColors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF7F6F5),
                    focusedContainerColor = Color(0xFFF7F6F5),
                    unfocusedBorderColor = Color(0xFFD9D7D5),
                    focusedBorderColor = Color(0xFFF57419),
                    cursorColor = Color(0xFFF57419)
                )
                OutlinedTextField(profileFirstDraft, { profileFirstDraft = it }, label = { Text("First name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = profileFieldColors)
                OutlinedTextField(profileLastDraft, { profileLastDraft = it }, label = { Text("Last name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = profileFieldColors)
                OutlinedTextField(profileMiddleDraft, { profileMiddleDraft = it }, label = { Text("Middle name") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = profileFieldColors)
                OutlinedTextField(profileDobDraft, { profileDobDraft = it }, label = { Text("Date of birth") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = profileFieldColors)
                OutlinedTextField(profileGenderDraft, { profileGenderDraft = it }, label = { Text("Gender") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = profileFieldColors)
                OutlinedTextField(profileMobileDraft, { profileMobileDraft = it }, label = { Text("Mobile number") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = profileFieldColors)
                Button(
                    onClick = {
                        onSaveProfile(profileUsernameDraft, profileFirstDraft, profileMiddleDraft, profileLastDraft, profileDobDraft, profileMobileDraft, profileStreetDraft, profileCityDraft, profilePostalDraft)
                        onPageChange(SettingsPage.Home)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Save changes", color = Color.White, fontWeight = FontWeight.Bold) }
            }
            return
        }
        if (page == SettingsPage.Password) {
            Column(
                modifier = modifier.fillMaxSize().background(Color.White).padding(horizontal = 22.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onPageChange(SettingsPage.Home) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }
                    Column { Text("Change password", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Keep your account secure", color = Color(0xFF777777), fontSize = 12.sp) }
                }
                OutlinedTextField(currentPasswordDraft, { currentPasswordDraft = it }, label = { Text("Current password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), visualTransformation = if (profileCurrentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { profileCurrentPasswordVisible = !profileCurrentPasswordVisible }) { Icon(if (profileCurrentPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (profileCurrentPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF777777)) } })
                OutlinedTextField(
                    passwordDraft,
                    { passwordDraft = it },
                    label = { Text("New password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (profilePasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { profilePasswordVisible = !profilePasswordVisible }) {
                            Icon(
                                imageVector = if (profilePasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (profilePasswordVisible) "Hide password" else "Show password",
                                tint = Color(0xFF777777),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
                OutlinedTextField(confirmPasswordDraft, { confirmPasswordDraft = it }, label = { Text("Confirm new password") }, singleLine = true, modifier = Modifier.fillMaxWidth(), visualTransformation = if (profileConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), trailingIcon = { IconButton(onClick = { profileConfirmPasswordVisible = !profileConfirmPasswordVisible }) { Icon(if (profileConfirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = if (profileConfirmPasswordVisible) "Hide password" else "Show password", tint = Color(0xFF777777)) } })
                Text("Must be at least 8 characters with uppercase, lowercase, number, and special character.", color = Color(0xFF999999), fontSize = 12.sp)
                Button(onClick = {
                    if (passwordDraft.isNotBlank() && passwordDraft == confirmPasswordDraft) onChangePassword(passwordDraft)
                    currentPasswordDraft = ""
                    passwordDraft = ""
                    confirmPasswordDraft = ""
                    onPageChange(SettingsPage.Home)
                }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57419)), shape = RoundedCornerShape(12.dp)) { Text("Update password", color = Color.White, fontWeight = FontWeight.Bold) }
                OutlinedButton(onClick = { onPageChange(SettingsPage.Login) }, modifier = Modifier.fillMaxWidth()) { Text("Forgot password?") }
            }
            return
        }
        if (page == SettingsPage.Notifications) {
            Column(modifier = modifier.fillMaxSize().background(Color.White).padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onPageChange(SettingsPage.Home) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }
                    Column { Text("Notifications", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Manage your alerts", color = Color(0xFF777777), fontSize = 12.sp) }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Quotation alerts"); Switch(quotationAlertsEnabled, { quotationAlertsEnabled = it }) }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) { Text("Panel and catalog alerts"); Switch(panelAlertsEnabled, { panelAlertsEnabled = it }) }
            }
            return
        }
        if (page == SettingsPage.Quotations) {
            Column(modifier = modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(22.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onPageChange(SettingsPage.Home) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }
                    Column { Text("Quotation history", color = Color(0xFF202124), fontSize = 16.sp, fontWeight = FontWeight.Bold); Text("Quotations are reviewed during checkout", color = Color(0xFF777777), fontSize = 12.sp) }
                }
                if (recentQuoteHistory.isEmpty()) Text("No quotations yet.") else recentQuoteHistory.forEach { quote ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF9F7))) {
                        Column(Modifier.padding(14.dp)) { Text(quote.quoteNumber, fontWeight = FontWeight.Bold); Text(quote.summary); Text("${quote.amount} • ${quote.status}", color = Color(0xFFF57419), fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
            return
        }
        if (page == SettingsPage.HelpSupport || page == SettingsPage.TermsPrivacy || page == SettingsPage.AboutApp) {
            val detailTitle = when (page) {
                SettingsPage.HelpSupport -> "Help and support"
                SettingsPage.TermsPrivacy -> "Terms and privacy"
                else -> "About the app"
            }
            val detailSubtitle = when (page) {
                SettingsPage.HelpSupport -> "FAQs and contact"
                SettingsPage.TermsPrivacy -> "Policies and agreements"
                else -> "Version and proponents"
            }
            Column(
                modifier = modifier.fillMaxSize().background(Color(0xFFFFFBF6)).verticalScroll(rememberScrollState()).padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onPageChange(SettingsPage.Home) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFFB9B6AE)) }
                    Column { Text(detailTitle, color = Color(0xFF202124), fontSize = 17.sp, fontWeight = FontWeight.Bold); Text(detailSubtitle, color = Color(0xFF777777), fontSize = 12.sp) }
                }
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        when (page) {
                            SettingsPage.HelpSupport -> {
                                Text("How can we help?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text("Find answers about quotations, accounts, panels, orders, and delivery.", color = Color(0xFF667085))
                                OutlinedButton(onClick = { supportTopic = "Frequently asked questions" }, modifier = Modifier.fillMaxWidth()) { Text("FAQs") }
                                OutlinedButton(onClick = { supportTopic = "Contact support" }, modifier = Modifier.fillMaxWidth()) { Text("Contact support") }
                                if (supportTopic != "Help center") Text(supportTopic, color = Color(0xFFF57419), fontWeight = FontWeight.SemiBold)
                            }
                            SettingsPage.TermsPrivacy -> {
                                Text("Terms and privacy", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text("This app stores account details, quotations, and profile updates so your customer account stays synchronized.", color = Color(0xFF667085))
                                Text("Please use the app responsibly and keep your account information secure.", color = Color(0xFF667085))
                            }
                            else -> {
                                Text("Aiyige Customer", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text("Browse panels, preview designs with AR, request quotations, and manage your customer account.", color = Color(0xFF667085))
                                Text("Version 1.0", color = Color(0xFF667085))
                            }
                        }
                    }
                }
            }
            return
        }
        if (showProfileEditor) {
            AlertDialog(
                onDismissRequest = { showProfileEditor = false },
                confirmButton = {
                    TextButton(onClick = {
                        onSaveProfile(
                            profileUsernameDraft, profileFirstDraft, profileMiddleDraft, profileLastDraft,
                            profileDobDraft, profileMobileDraft, profileStreetDraft, profileCityDraft, profilePostalDraft
                        )
                        showProfileEditor = false
                    }) { Text("Save") }
                },
                dismissButton = { TextButton(onClick = { showProfileEditor = false }) { Text("Cancel") } },
                title = { Text("Profile details") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(profileFirstDraft, { profileFirstDraft = it }, label = { Text("First name") }, singleLine = true)
                        OutlinedTextField(profileMiddleDraft, { profileMiddleDraft = it }, label = { Text("Middle name") }, singleLine = true)
                        OutlinedTextField(profileLastDraft, { profileLastDraft = it }, label = { Text("Last name") }, singleLine = true)
                        OutlinedTextField(profileDobDraft, { profileDobDraft = it }, label = { Text("Date of birth") }, singleLine = true)
                        OutlinedTextField(profileStreetDraft, { profileStreetDraft = it }, label = { Text("Street address") }, singleLine = true)
                        OutlinedTextField(profileCityDraft, { profileCityDraft = it }, label = { Text("City / Province") }, singleLine = true)
                        OutlinedTextField(profilePostalDraft, { profilePostalDraft = it }, label = { Text("ZIP code") }, singleLine = true)
                        OutlinedTextField(profileMobileDraft, { profileMobileDraft = it }, label = { Text("Mobile number") }, singleLine = true)
                    }
                }
            )
        }
        if (showSecurityEditor) {
            AlertDialog(
                onDismissRequest = { showSecurityEditor = false },
                confirmButton = {
                    TextButton(onClick = {
                        if (passwordDraft.isNotBlank()) onChangePassword(passwordDraft)
                        passwordDraft = ""
                        showSecurityEditor = false
                    }) { Text("Save password") }
                },
                dismissButton = { TextButton(onClick = { showSecurityEditor = false }) { Text("Cancel") } },
                title = { Text("Password") },
                text = { OutlinedTextField(passwordDraft, { passwordDraft = it }, label = { Text("New password") }, singleLine = true) }
            )
        }
        if (showNotificationsEditor) {
            AlertDialog(
                onDismissRequest = { showNotificationsEditor = false },
                confirmButton = { TextButton(onClick = { showNotificationsEditor = false }) { Text("Done") } },
                title = { Text("Notifications") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Quotation alerts")
                                Text("Status changes and approvals", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(checked = quotationAlertsEnabled, onCheckedChange = { quotationAlertsEnabled = it })
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("Panel and catalog alerts")
                                Text("New arrivals and promotions", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(checked = panelAlertsEnabled, onCheckedChange = { panelAlertsEnabled = it })
                        }
                    }
                }
            )
        }
        if (showQuotesHistory) {
            AlertDialog(
                onDismissRequest = { showQuotesHistory = false },
                confirmButton = { TextButton(onClick = { showQuotesHistory = false }) { Text("Close") } },
                title = { Text("My quotations") },
                text = {
                    if (recentQuoteHistory.isEmpty()) {
                        Text("No quotations yet.")
                    } else {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            recentQuoteHistory.take(10).forEach { quote ->
                                Column {
                                    Text(quote.quoteNumber.ifBlank { "Quotation" }, fontWeight = FontWeight.SemiBold)
                                    Text(quote.summary.ifBlank { "Saved quotation" }, style = MaterialTheme.typography.bodySmall)
                                    Text("${quote.amount} • ${quote.status}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            )
        }
        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                confirmButton = { TextButton(onClick = { showHelpDialog = false }) { Text("Close") } },
                title = { Text(supportTopic) },
                text = { Text("Browse help articles, contact details, and app guidance for quotations, accounts, and orders.") }
            )
        }
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                confirmButton = { TextButton(onClick = { showTermsDialog = false }) { Text("Close") } },
                title = { Text("Terms and privacy") },
                text = { Text("This app stores account details, quotations, and profile updates so the mobile app stays synced with the backend.") }
            )
        }
        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                confirmButton = { TextButton(onClick = { showAboutDialog = false }) { Text("Close") } },
                title = { Text("About the app") },
                text = { Text("Aiyige Customer is the mobile app for browsing panels, viewing quotes, and managing your account.") }
            )
        }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateOfBirthPickerField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Date of birth",
    placeholder: String = "YYYY-MM-DD",
    supportingText: String? = null
) {
    var openPicker by rememberSaveable(value) { mutableStateOf(false) }
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = parseIsoDateMillis(value))

    if (openPicker) {
        DatePickerDialog(
            onDismissRequest = { openPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            onValueChange(formatIsoDateMillis(millis))
                        }
                        openPicker = false
                    }
                ) {
                    Text("Set")
                }
            },
            dismissButton = {
                TextButton(onClick = { openPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }

    OutlinedTextField(
        value = value,
        onValueChange = { },
        modifier = modifier
            .fillMaxWidth()
            .clickable { openPicker = true },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = Color(0xFFE7DDD1),
            focusedBorderColor = Color(0xFFF77F2C),
            cursorColor = Color(0xFFF77F2C)
        ),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = null) },
        trailingIcon = {
            IconButton(onClick = { openPicker = true }) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null)
            }
        },
        readOnly = true,
        singleLine = true,
        supportingText = supportingText?.let { text ->
            { Text(text, color = Color(0xFF667085), fontSize = 11.sp) }
        }
    )
}

@Composable
private fun MenuScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isSignedIn: Boolean,
    signInName: String,
    catalogCount: Int,
    inStockCount: Int,
    requestCount: Int,
    latestQuoteSummary: String?,
    latestQuoteNumber: String,
    latestQuoteItemName: String,
    latestQuoteAmount: String,
    paymentStatusMessage: String,
    recentQuoteHistory: List<QuoteHistoryEntry>,
    customerOrderUpdates: List<CustomerOrderUpdate>,
    onOpenSettings: () -> Unit,
    onOpenPayment: () -> Unit,
    onOpenOrderUpdates: () -> Unit,
    onTrackOrder: (CustomerOrderUpdate) -> Unit,
    onOpenCatalog: () -> Unit,
    onOpenHome: () -> Unit
) {
    if (isLoading) {
        MenuSkeletonScreen(modifier = modifier)
        return
    }

    var menuLoaded by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        menuLoaded = true
    }
    val menuOpenProgress by animateFloatAsState(
        targetValue = if (menuLoaded) 1f else 0f,
        label = "menuOpenProgress"
    )

    // recentQuoteHistory is already scoped to the signed-in customer when it
    // is loaded from the backend. Comparing an email with the display name
    // here incorrectly hid valid quotes from the mobile menu.
    val filteredRecentQuotes = remember(recentQuoteHistory) {
        recentQuoteHistory.take(10)
    }
    var showCompletedOrders by rememberSaveable { mutableStateOf(false) }
    val visibleOrders = remember(customerOrderUpdates, showCompletedOrders) {
        customerOrderUpdates.filter { order ->
            val completed = order.deliveryStatus.equals("Delivered", true) ||
                order.deliveryStatus.equals("Completed", true)
            completed == showCompletedOrders
        }.take(10)
    }

    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFFFF7EB), Color(0xFFF4EFE7))
                )
            )
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = menuOpenProgress
                    translationY = (1f - menuOpenProgress) * 18f
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFFFFF), Color(0xFFF8F1E8))
                        ),
                        RoundedCornerShape(30.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Welcome back", color = Color(0xFF667085), fontSize = 13.sp)
                    Text(signInName.ifBlank { "Aiyige" }, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = Color(0xFF121826))
                    StatsRow(
                        requestCount = recentQuoteHistory.size,
                        latestQuoteAmount = latestQuoteAmount,
                        paymentStatusMessage = paymentStatusMessage
                    )
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("My Orders", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFFF77F2C))
                TextButton(onClick = onOpenOrderUpdates) { Text("See all →", color = Color(0xFF667085), fontSize = 12.sp) }
            }
            Row(
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF4EFE7), RoundedCornerShape(12.dp)).padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Button(
                    onClick = { showCompletedOrders = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (!showCompletedOrders) Color(0xFFF77F2C) else Color.Transparent),
                    elevation = null
                ) { Text("Active", color = if (!showCompletedOrders) Color.White else Color(0xFF667085), fontSize = 12.sp) }
                Button(
                    onClick = { showCompletedOrders = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (showCompletedOrders) Color(0xFFF77F2C) else Color.Transparent),
                    elevation = null
                ) { Text("Completed", color = if (showCompletedOrders) Color.White else Color(0xFF667085), fontSize = 12.sp) }
            }
            if (visibleOrders.isEmpty()) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0)), shape = RoundedCornerShape(20.dp)) {
                    Text(if (showCompletedOrders) "No completed orders yet." else "No active orders yet.", modifier = Modifier.padding(18.dp), color = Color(0xFF667085), fontSize = 13.sp)
                }
            } else {
                visibleOrders.forEach { order ->
                    MenuOrderCard(order = order, onTrackOrder = { onTrackOrder(order) })
                }
            }
        }

        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Quotation history", fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color(0xFFF77F2C))
                if (filteredRecentQuotes.isEmpty()) {
                    Text("No quotes yet.", color = Color(0xFF667085), fontSize = 13.sp)
                } else {
                    filteredRecentQuotes.forEach { quote ->
                        val displayStatus = quote.status.ifBlank { "Quotation ready" }
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(quote.quoteNumber.ifBlank { "Quote" }, color = Color(0xFF121826), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    displayStatus,
                                    modifier = Modifier.fillMaxWidth(),
                                    color = Color(0xFFF77F2C),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp,
                                    textAlign = TextAlign.End,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(quote.summary.ifBlank { "Saved quote." }, color = Color(0xFF667085), fontSize = 13.sp)
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text(quote.createdAt.ifBlank { "Recently" }, color = Color(0xFF667085), fontSize = 12.sp)
                                    Text(quote.amount.ifBlank { "--" }, color = Color(0xFF121826), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
        }
    }

private fun displayDeliveryStatus(status: String): String = when (status.trim().lowercase()) {
    "shipped" -> "Handed to courier"
    "" -> "Pending confirmation"
    else -> status
}

@Composable
private fun OrderTrackingScreen(
    modifier: Modifier = Modifier,
    order: CustomerOrderUpdate,
    onBack: () -> Unit
) {
    val completed = order.deliveryStatus.equals("Delivered", true) || order.deliveryStatus.equals("Completed", true)
    val paymentPending = !order.paymentStatus.trim().lowercase().let { it in listOf("paid", "completed", "settled") }
    val statusColor = if (completed) Color(0xFF18794E) else Color(0xFFF77F2C)
    Column(
        modifier = modifier.background(Color(0xFFFFF7EB)).verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color(0xFF121826))
            }
            Text("Track order", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF121826))
        }
        Text("Delivery progress", color = Color(0xFFF77F2C), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(order.invoice, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF121826))
                    Text(if (paymentPending) "Awaiting payment" else displayDeliveryStatus(order.deliveryStatus), color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(order.products.ifBlank { "Panel order" }, color = Color(0xFF667085))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    val completedStages = when {
                        paymentPending -> 0
                        completed -> 4
                        order.deliveryStatus.equals("Shipped", true) -> 3
                        else -> 2
                    }
                    repeat(4) { index ->
                        Box(Modifier.weight(1f).height(5.dp).background(if (index < completedStages) Color(0xFFF77F2C) else Color(0xFFE4DED6), RoundedCornerShape(99.dp)))
                    }
                }
                Text(
                    when {
                        paymentPending -> "Your order will enter preparation after payment has been received and confirmed."
                        completed -> "Your order has been delivered."
                        order.deliveryStatus.equals("Shipped", true) -> "Your order has been handed to ${order.courier.ifBlank { "the selected courier" }}."
                        order.deliveryStatus.equals("Preparing", true) -> "Your order is being prepared for the selected courier."
                        else -> "Your order is being prepared."
                    },
                    color = Color(0xFF344054),
                    fontSize = 13.sp
                )
            }
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0)), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Delivery details", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF121826))
                TrackingDetailRow("Courier", order.courier.ifBlank { "Not selected" })
                TrackingDetailRow("Tracking reference", order.trackingNumber.ifBlank { "Not provided yet" })
                TrackingDetailRow("Last updated", order.updatedAt.ifBlank { "Recently" })
                TrackingDetailRow("Order total", formatPeso(order.amount))
            }
        }
    }
}

@Composable
private fun TrackingDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color(0xFF667085), fontSize = 12.sp)
        Text(value, color = Color(0xFF121826), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Composable
private fun MenuOrderCard(
    order: CustomerOrderUpdate,
    onTrackOrder: () -> Unit
) {
    val isCompleted = order.deliveryStatus.equals("Delivered", true) || order.deliveryStatus.equals("Completed", true)
    val statusColor = if (isCompleted) Color(0xFF18794E) else Color(0xFFF77F2C)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDF7F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(order.invoice, color = Color(0xFF121826), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Text(displayDeliveryStatus(order.deliveryStatus), color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text(order.products.ifBlank { "Panel order" }, color = Color(0xFF667085), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(4) { index ->
                    Box(Modifier.weight(1f).height(4.dp).background(if (index < if (isCompleted) 4 else 2) Color(0xFFF77F2C) else Color(0xFFE4DED6), RoundedCornerShape(99.dp)))
                }
            }
            if (order.courier.isNotBlank() || order.trackingNumber.isNotBlank()) {
                Text(listOf(order.courier, order.trackingNumber).filter { it.isNotBlank() }.joinToString(" · "), color = Color(0xFF667085), fontSize = 11.sp)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(order.updatedAt.ifBlank { "Recently updated" }, color = Color(0xFF667085), fontSize = 11.sp)
                Text(formatPeso(order.amount), color = Color(0xFF121826), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onTrackOrder, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE1C2)), shape = RoundedCornerShape(11.dp), contentPadding = PaddingValues(vertical = 9.dp)) {
                    Text("Track Order", color = Color(0xFFB95708), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun MenuPrimaryRow(
    title: String,
    subtitle: String,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF121826))
                Text(subtitle, color = Color(0xFF667085), fontSize = 12.sp)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF77F2C)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(buttonLabel, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, color = Color(0xFF667085), fontSize = 13.sp)
        Text(value, color = Color(0xFF121826), fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyState() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF))) {
        Column(
            Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No matches", fontWeight = FontWeight.Bold)
            Text("Try another search.", color = Color(0xFF667085))
        }
    }
}






