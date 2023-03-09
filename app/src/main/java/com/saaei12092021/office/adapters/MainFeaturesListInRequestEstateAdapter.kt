package com.saaei12092021.office.adapters

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.MainFeaturesListInRequestEstateItemBinding
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Feature
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Option

class MainFeaturesListInRequestEstateAdapter(
    private val listener: OnCheckedChangeListener,
    private val listener2: OptionOnItemClick2
) :
    RecyclerView.Adapter<MainFeaturesListInRequestEstateAdapter.MyViewHolder>(),
    OptionsListInRequestEstateAdapter.OnChooseChangeListener {

    lateinit var mContext: Context
    lateinit var optionInMainFeaturesListAdapter: OptionsListInRequestEstateAdapter

    inner class MyViewHolder(private val binding: MainFeaturesListInRequestEstateItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            binding.featureNameIfTypeNumberCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (TextUtils.isEmpty(binding.fromNumberTv.text.toString())) {
                        binding.fromNumberTv.error = "املأ الحقل اولا"
                        binding.featureNameIfTypeNumberCb.isChecked = false
                    } else if (TextUtils.isEmpty(binding.toNumberTv.text.toString())) {
                        binding.toNumberTv.error = "امل الحقل اولا"
                        binding.featureNameIfTypeNumberCb.isChecked = false
                    } else {
                        val fromNumber = binding.fromNumberTv.text.toString()
                        val toNumber = binding.toNumberTv.text.toString()
                        listener.specificationOnItemClick(
                            feature,
                            position,
                            fromNumber,
                            toNumber,
                            true
                        )
                    }
                } else {
                    binding.fromNumberTv.setText("")
                    binding.toNumberTv.setText("")
                    listener.specificationOnItemClick(feature, position, "", "", false)
                }
            }
            binding.featureNameIfTypeListCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.optionsRv.visibility = View.VISIBLE
                    binding.numberValueLinearItem.visibility = View.GONE
                    binding.listValuesLinearItem.visibility = View.VISIBLE
                    optionInMainFeaturesListAdapter =
                        OptionsListInRequestEstateAdapter(this@MainFeaturesListInRequestEstateAdapter)
                    binding.optionsRv.apply {
                        adapter = optionInMainFeaturesListAdapter
                    }
                    optionInMainFeaturesListAdapter.differ.submitList(feature.options)
                } else {
                    binding.optionsRv.visibility = View.GONE
                }
            }
            if (feature.type == "NUMBER") {
                binding.numberValueLinearItem.visibility = View.VISIBLE
                binding.listValuesLinearItem.visibility = View.GONE
                binding.fromNumberTv.setText(feature.from)
                binding.toNumberTv.setText(feature.to)
                binding.featureNameIfTypeNumberCb.text = feature.name
                binding.featureNameIfTypeNumberCb.isChecked = feature.isSelected
            } else if (feature.type == "LIST") {
                binding.listValuesLinearItem.visibility = View.VISIBLE
                binding.numberValueLinearItem.visibility = View.GONE
                if ((feature.isSelected) or (binding.featureNameIfTypeListCb.isChecked)) {
                    binding.featureNameIfTypeListCb.text = feature.name
                    binding.featureNameIfTypeListCb.isChecked = true
                    binding.optionsRv.visibility = View.VISIBLE
                    optionInMainFeaturesListAdapter =
                        OptionsListInRequestEstateAdapter(this@MainFeaturesListInRequestEstateAdapter)
                    binding.optionsRv.apply {
                        adapter = optionInMainFeaturesListAdapter
                    }
                    optionInMainFeaturesListAdapter.differ.submitList(feature.options)
                } else {
                    binding.numberValueLinearItem.visibility = View.INVISIBLE
                    binding.listValuesLinearItem.visibility = View.VISIBLE
                    binding.featureNameIfTypeListCb.text = feature.name
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            MainFeaturesListInRequestEstateItemBinding.inflate(
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
        val feature = differ.currentList[position]
        holder.bind(feature)
    }

    val differCallback = object : DiffUtil.ItemCallback<Feature>() {
        override fun areItemsTheSame(oldItem: Feature, newItem: Feature): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Feature, newItem: Feature): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnCheckedChangeListener {
        fun specificationOnItemClick(
            feature: Feature,
            position: Int,
            fromNumber: String,
            toNumber: String,
            isSelected: Boolean
        )
    }

    interface OptionOnItemClick2 {
        fun optionOnItemClick2(
            thisFeaturePosition: Int,
            thisFeatureId: Int,
            optionPosition: Int,
            optionId: Int,
            isSelectedOption: Boolean,
        )
    }

    override fun optionOnItemClick(
        thisFeaturePosition: Int,
        thisFeatureId: Int,
        optionPosition: Int,
        optionId: Int,
        isSelectedOption: Boolean
    ) {
        listener2.optionOnItemClick2(
            thisFeaturePosition = thisFeaturePosition,
            thisFeatureId = thisFeatureId,
            optionPosition = optionPosition,
            optionId = optionId,
            isSelectedOption = isSelectedOption
        )
    }
}
