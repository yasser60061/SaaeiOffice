package com.saaei12092021.office.ui.activities.packagesActivity

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import com.saaei12092021.office.R
import com.saaei12092021.office.databinding.BottomSheetForMoreInPackagesBinding

class BottomSheetForMoreInPackagesList(mContext: Context?, theTitle : String, theContent : String , tag : String) {
    var bottomSheetDialog: BottomSheetDialog = BottomSheetDialog(mContext!!)
    var binding: BottomSheetForMoreInPackagesBinding

    init {
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_for_more_in_packages)
         bottomSheetDialog.setCanceledOnTouchOutside(true)
        binding =
            BottomSheetForMoreInPackagesBinding.inflate(
                LayoutInflater.from(mContext),
                null,
                false
            )
        bottomSheetDialog.setContentView(binding.root)
        if (tag == "TERMS")
            binding.packageTitleTv.visibility = View.GONE

        // ---- for design and style only ---- //
//        val frameLayout =
//            bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.transition_current_scene)
//        if (frameLayout != null) {
//            val bottomSheetBehavior = BottomSheetBehavior.from<View>(frameLayout)
//            bottomSheetBehavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
//            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }

        bottomSheetDialog.show()

        binding.bottomSheetCloseRl.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        binding.descriptionTv.text = theContent
        binding.packageNameTv.text = " $theTitle "

    }

}