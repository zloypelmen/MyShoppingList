package com.leonid.myshoppinglist.presentation.main_screen

import androidx.lifecycle.ViewModel
import com.leonid.myshoppinglist.data.ShopListRepositoryImpl
import com.leonid.myshoppinglist.domain.DeleteShopItemUseCase
import com.leonid.myshoppinglist.domain.EditShopItemUseCase
import com.leonid.myshoppinglist.domain.GetShopListUseCase
import com.leonid.myshoppinglist.domain.ShopItem

class MainViewModel : ViewModel() {

    private val repository = ShopListRepositoryImpl

    private val getShopListUseCase = GetShopListUseCase(repository)
    private val deleteShopItemUseCase = DeleteShopItemUseCase(repository)
    private val editShopItemUseCase = EditShopItemUseCase(repository)

    val shopList = getShopListUseCase.getShopList()

    fun deleteShopItem(shopItem: ShopItem) {
        deleteShopItemUseCase.deleteShopItem(shopItem)
    }

    fun changeEnableState(shopItem: ShopItem) {
        val newItem = shopItem.copy(enabled = !shopItem.enabled)
        editShopItemUseCase.editShopItem(newItem)
    }
}