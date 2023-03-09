package com.saaei12092021.office.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.FileNotFoundException
import java.io.InputStream

// add mime types and extensions you want to support here
val allSupportedDocumentsTypesToExtensions = mapOf(
    "application/msword" to ".doc",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to ".docx",
    "application/pdf" to ".pdf",
    "text/rtf" to ".rtf",
    "application/rtf" to ".rtf",
    "application/x-rtf" to ".rtf",
    "text/richtext" to ".rtf",
    "text/plain" to ".txt",
    "image/jpeg" to ".jpg",
    "image/png" to ".png",
)
private val extensionsToTypes =
    allSupportedDocumentsTypesToExtensions.entries.associate { (key, value) -> value to key }

val supportedMimeTypes: Array<String> = allSupportedDocumentsTypesToExtensions.keys.toTypedArray()

// for image and pdf type
val allSupportedImageAndPdfTypesToExtensions = mapOf(
    "application/msword" to ".doc",
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document" to ".docx",
    "application/pdf" to ".pdf",
    "text/rtf" to ".rtf",
    "application/rtf" to ".rtf",
    "application/x-rtf" to ".rtf",
    "text/richtext" to ".rtf",
    "text/plain" to ".txt",
    "image/jpeg" to ".jpg",
    "image/png" to ".png",
)
private val extensionsImageAndPdfToTypes =
    allSupportedImageAndPdfTypesToExtensions.entries.associate { (key, value) -> value to key }

val supportedMimeTypesForImageAndPdf: Array<String> =
    allSupportedImageAndPdfTypesToExtensions.keys.toTypedArray()

fun Activity.openDocumentAndPickImage(requestCode: Int, fileType: String) {
    if (fileType == "image/*") {
        val pickFromGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickFromGalleryIntent.type = fileType
        startActivityForResult(
            Intent.createChooser(pickFromGalleryIntent, "اختر صورة السجل التجاري"),
            requestCode
        )
    }
}

fun Activity.openDocumentAndPickImageOrPdf(requestCode: Int, fileType: String) {
    if (fileType == "image/*") {
        val pickFromGalleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickFromGalleryIntent.type = fileType
        startActivityForResult(
            Intent.createChooser(pickFromGalleryIntent, "اختر صورة السجل التجاري"),
            requestCode
        )
    }
    if (fileType == "application/pdf") {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = fileType
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(Intent.createChooser(intent, "اختر السجل التجاري"), requestCode)
    }
}

// i well used type = "audio/*" for used it to pick audio under test
// i well used type = "video/mp4" for used it to pick audio under test

fun Activity.openDocumentAndPickPdf(requestCode: Int) {
    val openDocumentIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "application/pdf"
        //    putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes)
    }
    startActivityForResult(openDocumentIntent, requestCode)
}

fun Activity.tryHandleOpenDocumentResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
): OpenFileResult {
    return handleOpenDocumentResult(requestCode, resultCode, data)
}

private fun Activity.handleOpenDocumentResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
): OpenFileResult {
    return if (resultCode == Activity.RESULT_OK && data != null) {
        val contentUri = data.data
        if (contentUri != null) {
            val stream =
                try {
                    application.contentResolver.openInputStream(contentUri)
                } catch (exception: FileNotFoundException) {
                    return OpenFileResult.ErrorOpeningFile
                }

            val fileName = contentResolver.queryFileName(contentUri)

            if (stream != null && fileName != null) {
                OpenFileResult.FileWasOpened(requestCode, fileName, stream)
            } else OpenFileResult.ErrorOpeningFile
        } else {
            OpenFileResult.ErrorOpeningFile
        }
    } else {
        OpenFileResult.OpenFileWasCancelled
    }
}

sealed class OpenFileResult {
    object OpenFileWasCancelled : OpenFileResult()
    data class FileWasOpened(val requestCode: Int, val fileName: String, val content: InputStream) :
        OpenFileResult()

    object ErrorOpeningFile : OpenFileResult()
    object DifferentResult : OpenFileResult()
}


fun ContentResolver.queryFileName(uri: Uri): String? {
    val cursor: Cursor = query(uri, null, null, null, null) ?: return null
    val nameIndex: Int = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    cursor.moveToFirst()
    val name: String = cursor.getString(nameIndex)
    cursor.close()
    return appendExtensionIfNeeded(name, uri)
}

private fun ContentResolver.appendExtensionIfNeeded(name: String, uri: Uri): String? {
    return if (hasKnownExtension(name)) {
        name
    } else {
        val type = getType(uri)
        if (type != null && allSupportedDocumentsTypesToExtensions.containsKey(type)) {
            return name + allSupportedDocumentsTypesToExtensions[type]
        }
        name
    }
}

private fun hasKnownExtension(filename: String): Boolean {
    val lastDotPosition = filename.indexOfLast { it == '.' }
    if (lastDotPosition == -1) {
        return false
    }
    val extension = filename.substring(lastDotPosition)
    return extensionsToTypes.containsKey(extension)
}
