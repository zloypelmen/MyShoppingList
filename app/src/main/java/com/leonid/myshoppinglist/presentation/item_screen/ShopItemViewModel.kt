package com.leonid.myshoppinglist.presentation.item_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.leonid.myshoppinglist.data.ShopListRepositoryImpl
import com.leonid.myshoppinglist.domain.AddShopItemUseCase
import com.leonid.myshoppinglist.domain.EditShopItemUseCase
import com.leonid.myshoppinglist.domain.GetShopItemUseCase
import com.leonid.myshoppinglist.domain.ShopItem

class ShopItemViewModel : ViewModel() {

    private val _errorInputName = MutableLiveData<Boolean>()
    private val _errorInputCount = MutableLiveData<Boolean>()
    private val _shopItem = MutableLiveData<ShopItem>()
    private val _shouldCloseScreen = MutableLiveData<Unit>()

    val errorInputName: LiveData<Boolean>
        get() = _errorInputName

    val errorInputCount: LiveData<Boolean>
        get() = _errorInputCount

    val shopItem: LiveData<ShopItem>
        get() = _shopItem

    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    private val repository = ShopListRepositoryImpl

    private val getShopItemUseCase = GetShopItemUseCase(repository)
    private val addShopItemUseCase = AddShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    fun getShopItem(shopItemId: Int) {
        val item = getShopItemUseCase.getShopItem(shopItemId)
        _shopItem.value = item
    }

    fun addShopItem(inputName: String?, inputCount: String?) {
        val shopItemName = parseName(inputName)
        val shopItemCount = parseCount(inputCount)
        val isFieldValid = validateInput(shopItemName, shopItemCount)
        if (isFieldValid) {
            val shopItem = ShopItem(shopItemName, shopItemCount, true)
            addShopItemUseCase.addShopItem(shopItem)
            setShouldCloseScreen()
        }
    }

    fun editShopItem(inputName: String?, inputCount: String?) {
        val shopItemName = parseName(inputName)
        val shopItemCount = parseCount(inputCount)
        val isFieldValid = validateInput(shopItemName, shopItemCount)
        if (isFieldValid) {
            _shopItem.value?.let {
                val item = it.copy(name = shopItemName, count = shopItemCount)
                editShopItemUseCase.editShopItem(item)
                setShouldCloseScreen()
            }
        }
    }

    private fun parseName(inputName: String?): String {
        return inputName?.trim() ?: ""
    }

    private fun parseCount(inputCount: String?): Int {
        return try {
            inputCount?.trim()?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun validateInput(name: String, count: Int): Boolean {
        var result = true
        if (name.isBlank()) {
            _errorInputName.value = true
            result = false
        }
        if (count <= 0) {
            _errorInputCount.value = true
            result = false
        }
        return result
    }

    private fun resetErrorInputName() {
        _errorInputName.value = false
    }

    private fun resetErrorInputCount() {
        _errorInputCount.value = false
    }

    private fun setShouldCloseScreen() {
        _shouldCloseScreen.value = Unit
    }
}