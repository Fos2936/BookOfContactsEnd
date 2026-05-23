package com.example.bookofcontacts

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookofcontacts.ui.theme.BookOfContactsTheme
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookOfContactsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ContactScreen()
                }
            }
        }
    }
}

fun callPhoneNumber(context: Context, phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri())
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }

    exceptionTry(
        intent = intent,
        context = context,
        errorMessage = "Не удалось открыть контакты",
    )
}

fun exceptionTry(
    intent: Intent,
    context: Context,
    errorMessage: String = "Не удалось открыть приложение",
    onFail: (() -> Unit)? = null
) {
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        if (onFail != null) {
            onFail()
        } else {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }
}

fun sendEmail(context: Context, address: String, subject: String) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        putExtra(Intent.EXTRA_SUBJECT, subject)
    }

    exceptionTry(
        context = context,
        intent = emailIntent,
        errorMessage = "Не удалось отправить сообщение"
    )
}

fun showOfficeOnMap(context: Context, latitude: Double, longitude: Double, label: String) {
    val geoUri = "geo:0,0?q=$latitude,$longitude($label)".toUri()
    val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)

    exceptionTry(
        context = context,
        intent = mapIntent,
        onFail = {
            val webUrl = "https://google.com"
            val browserIntent = Intent(Intent.ACTION_VIEW, webUrl.toUri())

            exceptionTry(
                context = context,
                intent = browserIntent,
                errorMessage = "Не удалось открыть карту"
            )
        }
    )
}

fun shareContact(context: Context, contactInfo: String) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, contactInfo)
    }

    val chooser = Intent.createChooser(sendIntent, "Поделиться через...")

    exceptionTry(
        context = context,
        intent = chooser,
        errorMessage = "Не удалось поделиться контактом"
    )
}

@Preview(showBackground = true)
@Composable
fun ContactScreen() {
    val context = LocalContext.current

    // Данные из задания
    val phone = stringResource(R.string.number_phone)
    val email = stringResource(R.string.email)
    val contact = stringResource(R.string.Contact)
    val contactData = "$contact, $phone, $email"
    val ring = stringResource(R.string.Ring)
    val contactBook = stringResource(R.string.contactBook)
    val emailWrite = stringResource(R.string.EmailWrite)
    val showOffice = stringResource(R.string.ShowOffice)
    val shareContact = stringResource(R.string.ShareContact)
    val appeal = stringResource(R.string.appeal)
    val locationPoint = stringResource(R.string.location_point)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = contactBook,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Кнопка: Позвонить
        ContactButton(
            label = ring,
            icon = Icons.Default.Phone,
            onClick = {
                callPhoneNumber(context, phone)
            }
        )

        // Кнопка: Написать email
        ContactButton(
            label = emailWrite,
            icon = Icons.Default.Email,
            onClick = { sendEmail(context, email, appeal) }
        )

        // Кнопка: Офис на карте
        ContactButton(
            label = showOffice,
            icon = Icons.Default.LocationOn,
            onClick = {
                showOfficeOnMap(
                    context,
                    60.0237,
                    30.2289,
                    locationPoint
                )
            }
        )

        // Кнопка: Поделиться
        ContactButton(
            label = shareContact,
            icon = Icons.Default.Share,
            onClick = { shareContact(context, contactData) }
        )
    }
}

@Composable
fun ContactButton(label: String, icon: ImageVector, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(Modifier.size(12.dp))
        Text(text = label, fontSize = 16.sp)
    }
}