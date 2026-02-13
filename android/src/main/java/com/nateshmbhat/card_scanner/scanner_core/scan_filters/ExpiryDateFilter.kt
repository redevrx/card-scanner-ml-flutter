package com.nateshmbhat.card_scanner.scanner_core.scan_filters

import com.google.mlkit.vision.text.Text
import com.nateshmbhat.card_scanner.scanner_core.constants.CardScannerRegexps
import com.nateshmbhat.card_scanner.scanner_core.models.CardNumberScanResult
import com.nateshmbhat.card_scanner.scanner_core.models.CardScannerOptions
import com.nateshmbhat.card_scanner.scanner_core.models.ExpiryDateScanResult
import com.nateshmbhat.card_scanner.scanner_core.models.ScanFilter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class ExpiryDateFilter(
    visionText: Text,
    scannerOptions: CardScannerOptions,
    private val cardNumberScanResult: CardNumberScanResult
) : ScanFilter(visionText, scannerOptions) {
    private val expiryDateRegex: Regex = Regex(CardScannerRegexps.EXPIRY_DATE_REGEX, RegexOption.MULTILINE)
    val maxBlocksBelowCardNumberToSearchForExpiryDate = 4
    val expiryDateFormat = "MM/yy"

    override fun filter(): ExpiryDateScanResult? {
        if (cardNumberScanResult.cardNumber.isEmpty()) return null
        if (!scannerOptions.scanExpiryDate) return null

        val scanResults: MutableList<ExpiryDateScanResult> = mutableListOf()
        val maxTextBlockIndexToSearchExpiryDate = min(
            cardNumberScanResult.textBlockIndex + maxBlocksBelowCardNumberToSearchForExpiryDate,
            visionText.textBlocks.size - 1
        )

        for (index in cardNumberScanResult.textBlockIndex..maxTextBlockIndexToSearchExpiryDate) {
            val block = visionText.textBlocks[index]
            if (!expiryDateRegex.containsMatchIn(block.text)) continue
            for (match in expiryDateRegex.findAll(block.text)) {
                val expiryDate = match.groupValues[0].trim().replace(Regex("\\s+"), "")
                if (isValidExpiryDate(expiryDate)) {
                    scanResults.add(
                        ExpiryDateScanResult(
                            textBlockIndex = index, textBlock = block, expiryDate = expiryDate, visionText = visionText
                        )
                    )
                }
            }
            if (scanResults.size > 2) break
        }

        if (scanResults.isEmpty()) return null
        return chooseMostRecentDate(scanResults)
    }

    private fun chooseMostRecentDate(expiryDateResults: List<ExpiryDateScanResult>): ExpiryDateScanResult {
        if (expiryDateResults.size == 1) return expiryDateResults[0]

        var mostRecentDateResult = expiryDateResults[0]
        for ((_, expiryDateResult) in expiryDateResults.subList(1, expiryDateResults.size).withIndex()) {
            val currentMostRecent = parseExpiryDate(mostRecentDateResult.expiryDate)
            val newDate = parseExpiryDate(expiryDateResult.expiryDate)
            if (newDate.after(currentMostRecent)) {
                mostRecentDateResult = expiryDateResult
            }
        }
        return mostRecentDateResult
    }

    private fun isValidExpiryDate(expiryDate: String): Boolean {
        val expiryDateTime = SimpleDateFormat(expiryDateFormat, Locale.US).parse(expiryDate)
        val currentDateTime = SimpleDateFormat(expiryDateFormat, Locale.US).parse(
            SimpleDateFormat(expiryDateFormat, Locale.US).format(Date())
        )
        return if (scannerOptions.considerPastDatesInExpiryDateScan) {
            true
        } else {
            expiryDateTime?.after(currentDateTime) ?: false
        }
    }

    private fun parseExpiryDate(expiryDate: String): Date {
        return SimpleDateFormat(expiryDateFormat, Locale.US).parse(expiryDate) ?: Date()
    }
}
