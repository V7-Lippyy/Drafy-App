// PDFGenerator.kt
package com.example.drafy.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.drafy.data.local.entity.Note
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

object PDFGenerator {
    private val TITLE_FONT = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD)
    private val HEADER_FONT = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
    private val NORMAL_FONT = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)

    fun createNotePDF(context: Context, notes: List<Note>) {
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale.getDefault())
        val fileName = "Catatan_${dateFormatter.format(Date())}.pdf"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        val document = Document(PageSize.A4)
        PdfWriter.getInstance(document, FileOutputStream(filePath))
        document.open()

        // Title
        val title = Paragraph("Catatan Drafy", TITLE_FONT)
        title.alignment = Element.ALIGN_CENTER
        title.spacingAfter = 20f
        document.add(title)

        // Date
        val dateParagraph = Paragraph("Tanggal: ${SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())}", NORMAL_FONT)
        dateParagraph.alignment = Element.ALIGN_RIGHT
        dateParagraph.spacingAfter = 20f
        document.add(dateParagraph)

        // Notes Content
        for (note in notes) {
            // Note title
            val noteTitle = Paragraph(note.title, HEADER_FONT)
            noteTitle.spacingAfter = 10f
            document.add(noteTitle)

            // Last edited
            val editTime = Paragraph(
                "Terakhir diubah: ${note.lastEditTime.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))}",
                NORMAL_FONT
            )
            editTime.spacingAfter = 10f
            document.add(editTime)

            // Note content
            val content = Paragraph(note.content, NORMAL_FONT)
            content.spacingAfter = 20f
            document.add(content)

            document.add(Paragraph("\n"))
        }

        document.close()

        // Share the PDF
        sharePDF(context, filePath)
    }

    private fun sharePDF(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle exception if no PDF reader is available
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/pdf"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(Intent.createChooser(
                shareIntent, "Bagikan PDF menggunakan"
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}