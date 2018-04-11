package com.yord.v.wheretogo.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import com.yord.v.wheretogo.R

open class DeleteDialogFragment: DialogFragment() {

    interface OptionDialogListener {
        fun onPositive()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity !is OptionDialogListener){
            throw ClassCastException(activity.toString() + " must implement YesNoListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val act = activity as OptionDialogListener
        return AlertDialog.Builder(
                activity)
                .setTitle(getString(R.string.delete_dialog_title))
                .setIcon(android.R.drawable.ic_delete)
                .setMessage(getString(R.string.delete_dialog_text) + arguments.get("spot"))
                .setPositiveButton(getString(R.string.possitive), { _, _ ->
                    act.onPositive()
                })
                .setNegativeButton(getString(R.string.negative), { dialog, _ ->
                    dialog.dismiss()
                })
                .create()
    }

}