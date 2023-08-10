package com.minux.mask_alarmi.ui.main.map

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.minux.mask_alarmi.R
import com.minux.mask_alarmi.domain.model.RemainState
import com.minux.mask_alarmi.domain.model.Store

class StoreBottomDialog(
    private val store: Store,
    private val onDismiss: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = View.inflate(context, R.layout.dialog_bottom_store, null)
        dialog.setContentView(view)
        dialog.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.let {
                it.setBackgroundColor(Color.TRANSPARENT)
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        initViews(view)
        return dialog
    }

    private fun initViews(view: View) {
        view.findViewById<TextView>(R.id.tv_store_name).text = store.name
        view.findViewById<TextView>(R.id.tv_store_address).text = store.address
        view.findViewById<ImageView>(R.id.iv_remain_state).setImageResource(when(store.remainState) {
            RemainState.EMPTY ->  R.drawable.ic_empty_big
            RemainState.FEW ->R.drawable.ic_few_big
            RemainState.SOME ->  R.drawable.ic_some_big
            RemainState.PLENTY -> R.drawable.ic_plenty_big
        })
        store.stockAt?.let {
            view.findViewById<TextView>(R.id.tv_stock_at).text = "${it.substring(5, 7)}.${it.substring(8, 10)}.${it.substring(11, 16)}"
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss()
    }
}