package com.leonid.myshoppinglist.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.leonid.myshoppinglist.R
import com.leonid.myshoppinglist.domain.ShopItem

class MainActivity : AppCompatActivity() {

    private lateinit var shopListAdapter: ShopListAdapter
    private lateinit var viewModel: MainViewModel
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setupRecyclerView()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.shopList.observe(this) {
            shopListAdapter.shopList = it
        }
    }

    private fun setupRecyclerView() {
        val rvShopList: RecyclerView = findViewById(R.id.rv_shop_list)

        shopListAdapter = ShopListAdapter()

        with(rvShopList){
            adapter = shopListAdapter
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_ENABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
            recycledViewPool.setMaxRecycledViews(
                ShopListAdapter.VIEW_TYPE_DISABLED,
                ShopListAdapter.MAX_POOL_SIZE
            )
        }
        shopListAdapter.onShopItemLongClickListener = object : ShopListAdapter.OnShopItemLongClickListener{
            override fun onShopItemLongClick(shopItem: ShopItem) {
                viewModel.changeEnableState(shopItem)
            }
        }

        shopListAdapter.onShopItemClickListener = object : ShopListAdapter.OnShopItemClickListener{
            override fun onShopItemClick(shopItem: ShopItem) {
                Log.d(TAG, shopItem.toString())
            }
        }
    }
}