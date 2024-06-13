package com.example.mediavault.ui.model.HomeViewModel

import android.app.Application
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

//to read metadata from uris like video title name , etc
interface MetaDataReader {
    fun getFileNameFromURI(uri: Uri) : String?
    // can return null if file is not of video / music type
    fun getRealPathFromURI(uri: Uri) : String?
}

class MetaDataReaderImpl(
    private val app: Application
):MetaDataReader {
    override fun getFileNameFromURI(uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            cursor = app.contentResolver.query(
                uri,
                arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), // Use the correct column for file path
                null,
                null,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (index != -1) {
                    return cursor.getString(index)
                }
            }
        } catch (e: Exception) {
            Log.e("debugg", "Error getting real name from URI", e)
        } finally {
            cursor?.close()
        }
        return null
    }

    override fun getRealPathFromURI(uri: Uri): String? {
        var cursor: Cursor? = null
        var column = "_data"
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":").toTypedArray()
        val type = split[0]
        val contentUri = when (type) {
            "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> null
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])
        try {
            cursor = app.contentResolver.query(
                contentUri!!,
                arrayOf(column), // Use the correct column for file path
                selection,
                selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(column)
                if (index != -1) {
                    return cursor.getString(index)
                }
            }
        } catch (e: Exception) {
            Log.e("debugg", "Error getting real path from URI", e)
        } finally {
            cursor?.close()
        }
        return null
    }

}