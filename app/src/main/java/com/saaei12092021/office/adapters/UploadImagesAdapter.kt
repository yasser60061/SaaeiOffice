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
import com.saaei12092021.office.databinding.UploadImagesForAdsItemBinding
import com.saaei12092021.office.model.responseModel.UploadImagesModel

class UploadImagesAdapter(
    private val listener: OnImageItemClickListener,
    private val deleteListener: OnDeleteItemClickListener
) :
    RecyclerView.Adapter<UploadImagesAdapter.MyViewHolder>() {

    lateinit var mContext: Context

    inner class MyViewHolder(private val binding: UploadImagesForAdsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(imagesModel: UploadImagesModel) {
            binding.root.setOnClickListener {
                listener.onIvClick(imagesModel, adapterPosition)
            }
            binding.deleteImageIv.setOnClickListener {
                deleteListener.onDeleteClick(adapterPosition)
            }
            if ((imagesModel.imageFile != null) or (imagesModel.imageUrlIfUpdate != "")) {
                binding.tempImageIv.visibility = View.GONE
                binding.adsImageIv.visibility = View.VISIBLE
                binding.deleteImageIv.visibility = View.VISIBLE
                binding.estateImageNameTv.bringToFront()
                binding.deleteImageIv.bringToFront()
                if ((imagesModel.imageFile != null)) {
                    binding.adsImageIv.setImageURI(Uri.fromFile(imagesModel.imageFile))
                    binding.estateImageNameTv.text = imagesModel.imageName
                } else if (imagesModel.imageUrlIfUpdate != "") {
                    Glide.with(mContext).load(imagesModel.imageUrlIfUpdate).into(binding.adsImageIv)
                    binding.estateImageNameTv.text = ""
                }
            } else {
                binding.tempImageIv.visibility = View.VISIBLE
                binding.adsImageIv.visibility = View.GONE
                binding.deleteImageIv.visibility = View.GONE
                binding.estateImageNameTv.text = ""
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            UploadImagesForAdsItemBinding.inflate(
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
            return oldItem.imageNumber == newItem.imageNumber
        }

        override fun areContentsTheSame(
            oldItem: UploadImagesModel,
            newItem: UploadImagesModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnImageItemClickListener {
        fun onIvClick(uploadImagesModel: UploadImagesModel, currentImagePosition: Int)
    }

    interface OnDeleteItemClickListener {
        fun onDeleteClick(currentImagePosition: Int)
    }

}













