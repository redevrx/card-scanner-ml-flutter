package com.nateshmbhat.card_scanner.scanner_core.models

import android.os.Parcel
import android.os.Parcelable

//@author nateshmbhat created on 29,June,2020

data class CardScannerOptions(
    val scanExpiryDate: Boolean,
    val scanCardHolderName: Boolean,
    val initialScansToDrop: Int,
    val validCardsToScanBeforeFinishingScan: Int,
    val cardHolderNameBlackListedWords: List<String>,
    val considerPastDatesInExpiryDateScan: Boolean,
    val maxCardHolderNameLength: Int,
    val enableLuhnCheck: Boolean,
    val cardScannerTimeOut: Int,
    val enableDebugLogs: Boolean,
    val possibleCardHolderNamePositions: List<String>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        initialScansToDrop = parcel.readInt(),
        validCardsToScanBeforeFinishingScan = parcel.readInt(),
        cardHolderNameBlackListedWords = parcel.createStringArrayList() ?: emptyList(),
        considerPastDatesInExpiryDateScan = parcel.readByte() != 0.toByte(),
        maxCardHolderNameLength = parcel.readInt(),
        enableLuhnCheck = parcel.readByte() != 0.toByte(),
        cardScannerTimeOut = parcel.readInt(),
        enableDebugLogs = parcel.readByte() != 0.toByte(),
        possibleCardHolderNamePositions = parcel.createStringArrayList() ?: emptyList()
    )

    constructor(configMap: Map<String, String>) : this(
        scanExpiryDate = configMap[ParcelKeys.SCAN_EXPIRY_DATE.value]?.toBoolean() ?: true,
        scanCardHolderName = configMap[ParcelKeys.SCAN_CARD_HOLDER_NAME.value]?.toBoolean() ?: false,
        initialScansToDrop = configMap[ParcelKeys.INITIAL_SCAN_TO_DROP.value]?.toInt() ?: 1,
        validCardsToScanBeforeFinishingScan = configMap[ParcelKeys.VALID_CARD_TO_SCAN_BEFORE_FINISHING_SCAN.value]?.toInt()
            ?: 11,
        cardHolderNameBlackListedWords = configMap[ParcelKeys.CardHolderNameBlackListedWords.value]?.split(',')
            ?: emptyList(),
        considerPastDatesInExpiryDateScan = configMap[ParcelKeys.ConsiderPastDatesInExpiryDateScan.value]?.toBoolean()
            ?: false,
        maxCardHolderNameLength = configMap[ParcelKeys.MaxCardHolderNameLength.value]?.toInt() ?: 26,
        enableLuhnCheck = configMap[ParcelKeys.EnableLuhnCheck.value]?.toBoolean() ?: true,
        cardScannerTimeOut = configMap[ParcelKeys.CardScannerTimeOut.value]?.toInt() ?: 0,
        enableDebugLogs = configMap[ParcelKeys.EnableDebugLogs.value]?.toBoolean() ?: false,
        possibleCardHolderNamePositions = configMap[ParcelKeys.PossibleCardHolderNamePositions.value]?.split(',')
            ?: listOf(CardHolderNameScanPositions.BELOW_CARD_NUMBER.value)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (scanExpiryDate) 1 else 0)
        parcel.writeByte(if (scanCardHolderName) 1 else 0)
        parcel.writeInt(initialScansToDrop)
        parcel.writeInt(validCardsToScanBeforeFinishingScan)
        parcel.writeStringList(cardHolderNameBlackListedWords)
        parcel.writeByte(if (considerPastDatesInExpiryDateScan) 1 else 0)
        parcel.writeInt(maxCardHolderNameLength)
        parcel.writeByte(if (enableLuhnCheck) 1 else 0)
        parcel.writeInt(cardScannerTimeOut)
        parcel.writeByte(if (enableDebugLogs) 1 else 0)
        parcel.writeStringList(possibleCardHolderNamePositions)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardScannerOptions> {

        enum class ParcelKeys(val value: String) {
            SCAN_EXPIRY_DATE ("scanExpiryDate"),
            SCAN_CARD_HOLDER_NAME("scanCardHolderName"),
            INITIAL_SCAN_TO_DROP("initialScansToDrop"),
            VALID_CARD_TO_SCAN_BEFORE_FINISHING_SCAN("validCardsToScanBeforeFinishingScan"),
            CardHolderNameBlackListedWords("cardHolderNameBlackListedWords"),
            ConsiderPastDatesInExpiryDateScan("considerPastDatesInExpiryDateScan"),
            MaxCardHolderNameLength("maxCardHolderNameLength"),
            EnableLuhnCheck("enableLuhnCheck"),
            CardScannerTimeOut("cardScannerTimeOut"),
            EnableDebugLogs("enableDebugLogs"),
            PossibleCardHolderNamePositions("possibleCardHolderNamePositions")
        }

        override fun createFromParcel(parcel: Parcel): CardScannerOptions {
            return CardScannerOptions(parcel)
        }

        override fun newArray(size: Int): Array<CardScannerOptions?> {
            return arrayOfNulls(size)
        }
    }
}