package com.leonid.myshoppinglist.presentation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PathEffect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
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

        setupLongClickListener()
        setupSimpleClickListener()
        setupSwipeListener(rvShopList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val shopItem = shopListAdapter.shopList[viewHolder.adapterPosition]
                    viewModel.deleteShopItem(shopItem)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView

                    val background = ColorDrawable(Color.RED)
                    val cornerRadius = 16f
                    background.setBounds(
                        itemView.right + dX.toInt(), itemView.top,
                        itemView.right, itemView.bottom
                    )

                    val paint = Paint().apply {
                        isAntiAlias = true
                        color = Color.RED
                        pathEffect = PathEffect()
                    }

                    val rect = RectF(
                        itemView.right + dX, itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat()
                    )
                    c.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

                    val icon = ContextCompat.getDrawable(viewHolder.itemView.context, android.R.drawable.ic_menu_delete)
                    icon?.let {
                        val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                        val iconLeft = itemView.right - it.intrinsicWidth - iconMargin
                        val iconRight = itemView.right - iconMargin
                        val iconTop = itemView.top + (itemView.height - it.intrinsicHeight) / 2
                        val iconBottom = iconTop + it.intrinsicHeight
                        it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        it.draw(c)
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupSimpleClickListener() {
        shopListAdapter.onShopItemClickListener = object : ShopListAdapter.OnShopItemClickListener {
            override fun onShopItemClick(shopItem: ShopItem) {
                Log.d(TAG, shopItem.toString())
            }
        }
    }

    private fun setupLongClickListener() {
        shopListAdapter.onShopItemLongClickListener =
            object : ShopListAdapter.OnShopItemLongClickListener {
                override fun onShopItemLongClick(shopItem: ShopItem) {
                    viewModel.changeEnableState(shopItem)
                }
            }
    }
}