package com.saaei12092021.office.adapters

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.saaei12092021.office.databinding.MainFeaturesListInAddAdsItemBinding
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Feature
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Option

class MainFeaturesListInAddEstateAdapter(
    private val listener: OnCheckedChangeListener,
    private val listener2: OptionOnItemClick2
) :
    RecyclerView.Adapter<MainFeaturesListInAddEstateAdapter.MyViewHolder>(),
    OptionInMainFeaturesListAdapter.OnChooseChangeListener {

    lateinit var mContext: Context
    lateinit var optionInMainFeaturesListAdapter: OptionInMainFeaturesListAdapter
   // lateinit var optionList: ArrayList<Option>

    inner class MyViewHolder(private var binding: MainFeaturesListInAddAdsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feature: Feature) {
            binding.featureNameIfTypeNumberCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    val value = binding.theValueIfTypeNumberEtItem.text.toString()
                    if (TextUtils.isEmpty(value)) {
                        binding.theValueIfTypeNumberEtItem.error = "املأ الحقل اولا"
                        binding.featureNameIfTypeNumberCb.isChecked = false
                    } else {
                        listener.mainFeatureOnItemClick(
                            feature = feature,
                            featurePosition = adapterPosition,
                            value = value,
                            isSelectedFeature = true
                        )
                    }
                } else {
                    binding.theValueIfTypeNumberEtItem.setText("")
                    listener.mainFeatureOnItemClick(
                        feature = feature,
                        featurePosition = adapterPosition,
                        value = "0",
                        isSelectedFeature = false
                    )
                }
            }
            binding.featureNameIfTypeListCb.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
//                    listener.mainFeatureOnItemClick(
//                        feature = feature,
//                        featurePosition = position,
//                        value = "0",
//                        isSelectedFeature = false,
//                    )
                    binding.optionsRv.visibility = View.VISIBLE
                    binding.numberValueLinearItem.visibility = View.GONE
                    binding.listValuesLinearItem.visibility = View.VISIBLE
//                feature.options?.forEach {
//                    it.thisFeatureId = feature.id
//                    it.thisFeaturePosition = position
//                }
                    optionInMainFeaturesListAdapter =
                        OptionInMainFeaturesListAdapter(this@MainFeaturesListInAddEstateAdapter)
                    binding.optionsRv.apply {
                        adapter = optionInMainFeaturesListAdapter
                    }
                 //   optionList = feature.options as ArrayList<Option>
                    optionInMainFeaturesListAdapter.differ.submitList(feature.options)
                } else {
//                    listener.mainFeatureOnItemClick(
//                        feature = feature,
//                        featurePosition = position,
//                        value = "0",
//                        isSelectedFeature = false,
//                    )
                    binding.optionsRv.visibility = View.GONE
                }
            }
            if (feature.type == "NUMBER") {
                binding.numberValueLinearItem.visibility = View.VISIBLE
                binding.listValuesLinearItem.visibility = View.GONE
                binding.featureNameIfTypeNumberCb.text = feature.name
                if (feature.value == "0") {
                    binding.theValueIfTypeNumberEtItem.setText("")
                    binding.featureNameIfTypeNumberCb.isChecked = false
                } else {
                    binding.theValueIfTypeNumberEtItem.setText(feature.value)
                    binding.featureNameIfTypeNumberCb.isChecked = feature.isSelected
                }

            } else if (feature.type == "LIST") {
                if ((feature.isSelected) or (binding.featureNameIfTypeListCb.isChecked)) {
                    binding.numberValueLinearItem.visibility = View.GONE
                    binding.listValuesLinearItem.visibility = View.VISIBLE
                    binding.featureNameIfTypeListCb.text = feature.name
                    binding.featureNameIfTypeListCb.isChecked = feature.isSelected
                    binding.optionsRv.visibility = View.VISIBLE
                    optionInMainFeaturesListAdapter =
                        OptionInMainFeaturesListAdapter(this@MainFeaturesListInAddEstateAdapter)
                    binding.optionsRv.apply {
                        adapter = optionInMainFeaturesListAdapter
                    }
                    optionInMainFeaturesListAdapter.differ.submitList(feature.options)
                } else {
                    binding.numberValueLinearItem.visibility = View.GONE
                    binding.listValuesLinearItem.visibility = View.VISIBLE
                    binding.featureNameIfTypeListCb.text = feature.name
                    binding.optionsRv.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            MainFeaturesListInAddAdsItemBinding.inflate(
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
        fun mainFeatureOnItemClick(
            feature: Feature,
            featurePosition: Int,
            isSelectedFeature: Boolean,
            value: String,
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
