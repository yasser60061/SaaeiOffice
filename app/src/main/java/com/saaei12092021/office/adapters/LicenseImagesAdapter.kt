package com.saaei12092021.office.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.saaei12092021.office.databinding.LicenseImagesItemBinding
import com.saaei12092021.office.databinding.UploadImagesForAdsItemBinding
import com.saaei12092021.office.model.responseModel.UploadImagesModel

class LicenseImagesAdapter(
    private val listener: LicenseImagesAdapter.OnImageLicenseItemClickListener,
    private val deleteListener: LicenseImagesAdapter.OnDeleteLicenseItemClickListener
) :
    RecyclerView.Adapter<LicenseImagesAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: LicenseImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uploadImagesModel: UploadImagesModel) {
            // we use the same xml file and item name because it is similar shape
            if ((uploadImagesModel.imageFile != null) or (uploadImagesModel.imageUrlIfUpdate != "")) {
                binding.tempImageIv.visibility = View.GONE
                binding.adsImageIv.visibility = View.VISIBLE
                binding.deleteImageIv.visibility = View.VISIBLE
                binding.estateImageNameTv.bringToFront()
                binding.deleteImageIv.bringToFront()
                if ((uploadImagesModel.imageFile != null)) {
                    binding.adsImageIv.setImageURI(Uri.fromFile(uploadImagesModel.imageFile))
                    binding.estateImageNameTv.text = ""
                } else if (uploadImagesModel.imageUrlIfUpdate != "") {
                    Glide.with(mContext).load(uploadImagesModel.imageUrlIfUpdate)
                        .into(binding.adsImageIv)
                    binding.estateImageNameTv.text = ""
                }
            } else {
                binding.tempImageIv.visibility = View.VISIBLE
                binding.adsImageIv.visibility = View.GONE
                binding.deleteImageIv.visibility = View.GONE
                binding.estateImageNameTv.text = "إضافة صورة"
            }

            binding.root.setOnClickListener {
                listener.onIvLicenseClick(uploadImagesModel, adapterPosition)
            }

            binding.deleteImageIv.setOnClickListener {
                deleteListener.onDeleteLicenseClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            LicenseImagesItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        mContext = parent.context
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val uploadImagesModel = differ.currentList[position]
        holder.bind(uploadImagesModel)
    }

    val differCallback = object : DiffUtil.ItemCallback<UploadImagesModel>() {
        override fun areItemsTheSame(
            oldItem: UploadImagesModel,
            newItem: UploadImagesModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: UploadImagesModel,
            newItem: UploadImagesModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnImageLicenseItemClickListener {
        fun onIvLicenseClick(uploadImagesModel: UploadImagesModel, currentImagePosition: Int)
    }

    interface OnDeleteLicenseItemClickListener {
        fun onDeleteLicenseClick(currentImagePosition: Int)
    }

}













