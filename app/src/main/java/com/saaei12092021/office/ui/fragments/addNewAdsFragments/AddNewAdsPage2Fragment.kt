package com.saaei12092021.office.ui.fragments.addNewAdsFragments

import android.app.Activity
import android.app.appsearch.AppSearchResult.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.saaei12092021.office.adapters.MainFeaturesListInAddEstateAdapter
import java.util.ArrayList
import com.saaei12092021.office.adapters.SubFeaturesFromListAdapter
import com.saaei12092021.office.adapters.UploadImagesAdapter
import com.saaei12092021.office.databinding.FragmentAddNewAdsPage2Binding
import com.saaei12092021.office.model.responseModel.UploadImagesModel
import com.saaei12092021.office.model.requestModel.addNewAdsRequest.PropertiesItem
import com.saaei12092021.office.model.responseModel.mainFeaturesResponse.Feature
import com.saaei12092021.office.ui.MyViewModel
import com.saaei12092021.office.ui.activities.homeActivity.HomeActivity
import com.saaei12092021.office.util.Constant
import com.saaei12092021.office.util.GeneralFunctions
import com.saaei12092021.office.util.Resource
import com.saaei12092021.office.util.showCustomToast
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class AddNewAdsPage2Fragment : Fragment(),
    SubFeaturesFromListAdapter.OnCheckedChangeListener,
    MainFeaturesListInAddEstateAdapter.OnCheckedChangeListener,
    MainFeaturesListInAddEstateAdapter.OptionOnItemClick2,
    UploadImagesAdapter.OnImageItemClickListener,
    UploadImagesAdapter.OnDeleteItemClickListener {

    private var _binding: FragmentAddNewAdsPage2Binding? = null
    val binding get() = _binding!!
    lateinit var parentFrag: MainAddNewAdsFragment
    lateinit var viewModel: MyViewModel
    lateinit var subFeaturesFromListAdapter: SubFeaturesFromListAdapter
    lateinit var mainFeaturesListInAddEstateAdapter: MainFeaturesListInAddEstateAdapter
    lateinit var allFeaturesList: ArrayList<Feature>
    lateinit var mainFeaturesList: ArrayList<Feature>
    lateinit var subFeaturesList: ArrayList<Feature>
    private var resultUri: Uri? = Uri.EMPTY
    private var imagePickerType = ""
    var currentImagePosition: Int = 0
    lateinit var uploadImagesAdapter: UploadImagesAdapter
    var isSelectAnyImage = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewAdsPage2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFrag = this@AddNewAdsPage2Fragment.parentFragment as MainAddNewAdsFragment
        parentFrag.binding.titleLinear.visibility = View.GONE
        viewModel = (activity as HomeActivity).viewModel

        allFeaturesList = ArrayList()
        subFeaturesList = ArrayList()
        mainFeaturesList = ArrayList()

        initialImageListToStartPick()

        if (parentFrag.pickedImagesList.isNullOrEmpty()) {
            for (i in 1..10) {
                parentFrag.pickedImagesList.add(
                    UploadImagesModel(
                        imageNumber = i,
                        imageName = "",
                        imageFile = null,
                        imageUrlIfUpdate = ""
                    )
                )
            }
            if (!parentFrag.imgs.isNullOrEmpty()) {
                parentFrag.imgs.forEachIndexed { indexed, ImageLinkItem ->
                    parentFrag.pickedImagesList[indexed].imageUrlIfUpdate = ImageLinkItem
                }
            }
            parentFrag.imgs.clear()

        }

        //   uploadImagesAdapter.differ.submitList(parentFrag.pickedImagesList)

        displayMainFeatures()
        displaySubFeatures()
        displayDataIfExist()

        binding.addEstateThreeDIv.setOnClickListener {
            pickThreeDImage()
            imagePickerType = "threeDImage"
        }

        binding.displayMainFeatureTv.setOnClickListener {
            binding.displayMainFeatureTv.visibility = View.INVISIBLE
            binding.hideMainFeatureTv.visibility = View.VISIBLE
            binding.mainFeaturesRv.visibility = View.VISIBLE
            parentFrag.binding.titleLinear.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }
        binding.hideMainFeatureTv.setOnClickListener {
            binding.hideMainFeatureTv.visibility = View.INVISIBLE
            binding.displayMainFeatureTv.visibility = View.VISIBLE
            binding.mainFeaturesRv.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }

        binding.displaySubFeatureTv.setOnClickListener {
            binding.displaySubFeatureTv.visibility = View.INVISIBLE
            binding.hideSubFeatureTv.visibility = View.VISIBLE
            binding.subFeaturesRv.visibility = View.VISIBLE
            parentFrag.binding.titleLinear.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }
        binding.hideSubFeatureTv.setOnClickListener {
            binding.hideSubFeatureTv.visibility = View.INVISIBLE
            binding.displaySubFeatureTv.visibility = View.VISIBLE
            binding.subFeaturesRv.visibility = View.GONE
            GeneralFunctions.hideKeyboard(requireActivity())
        }

        binding.previousBtn.setOnClickListener {
            parentFrag.displayPage1()
        }

        binding.nextBtn.setOnClickListener {
            if (mainFeaturesList.isNullOrEmpty()
               // or subFeaturesList.isNullOrEmpty()
            ) {
                Toast(requireActivity()).showCustomToast("انتظر تحميل البيانات", requireActivity())
            } else {
                parentFrag.pickedImagesList.forEach {
                    if ((it.imageFile != null) or (it.imageUrlIfUpdate != ""))
                        isSelectAnyImage = true
                }
                parentFrag.features.clear()
                parentFrag.featuresName.clear()
                parentFrag.properties.clear()
                parentFrag.propertiesName?.clear()
                subFeaturesList.forEach {
                    if (it.isSelected) {
                        parentFrag.features.add(it.id)
                        parentFrag.featuresName.add(it.name_ar)
                    }
                }
                mainFeaturesList.forEach {
                    if (it.isSelected) {
                        parentFrag.properties.add(
                            PropertiesItem(
                                it.id.toString(),
                                it.value.toString()
                            )
                        )
                        parentFrag.propertiesName?.add(it.name)
                    }
                }

                parentFrag.link3D?.clear()
                if (!TextUtils.isEmpty(binding.link3dEt.text.toString()))
                    parentFrag.link3D!!.add(binding.link3dEt.text.toString())

                when {
                    !isSelectAnyImage -> Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء ارفاق بعض صور العقار"
                    )
                    TextUtils.isEmpty(binding.realEstateAriaTvEt.text.toString()) -> Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء كتابة مساحة العقار "
                    )
                    ((!TextUtils.isEmpty(binding.link3dEt.text.toString())) and (!URLUtil.isValidUrl(
                        binding.link3dEt.text.toString()
                    ))) -> Constant.makeToastMessage(
                        requireContext(),
                        "رابط التجول الافتراضي غير صحيح"
                    )
                    parentFrag.properties.isEmpty() -> Constant.makeToastMessage(
                        requireContext(),
                        "الرجاء ادخال بعض المميزات الرئيسية بالارقام"
                    )
                    else -> {
                        parentFrag.size =
                            Integer.parseInt(binding.realEstateAriaTvEt.text.toString())
                        parentFrag.displayPage3()
                    }
                }
            }
        }

//----------------------------------------------------------------------------------
        viewModel.getMainFeatures(myLang = (activity as HomeActivity).myLang , category = parentFrag.subCategory!!)
        viewModel.mainFeaturesResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.GONE
                    allFeaturesList = it.data?.features as ArrayList<Feature>
                    subFeaturesList.clear()
                    mainFeaturesList.clear()
                    val tempList: ArrayList<Feature> = ArrayList()
                    allFeaturesList.forEach { item ->
                        if (item.type == "BOOLEAN")
                            subFeaturesList.add(item)
                        else {
                            if (item.type == "NUMBER")
                                tempList.add(0, item)
                            else tempList.add(item)
                        }
                    }

                    tempList.forEachIndexed { index1, allFeatureItem ->
                        allFeatureItem.isSelected = false
                        allFeatureItem.value = "0"
                        if (allFeatureItem.type == "LIST") {
                            allFeatureItem.options!!.forEachIndexed { index2, optionElements ->
                                optionElements.isSelected = false
                                optionElements.thisFeaturePosition = index1
                                optionElements.thisFeatureId = allFeatureItem.id
                            }
                        }
                    }
                    mainFeaturesList = tempList
                    mainFeaturesListInAddEstateAdapter.differ.submitList(mainFeaturesList)

                    if (parentFrag.features.isNotEmpty())
                        parentFrag.features.forEachIndexed { index1, element1 ->
                            subFeaturesList.forEachIndexed { index2, element2 ->
                                if (element1 == element2.id)
                                    element2.isSelected = true
                            }
                        }

                    if (parentFrag.properties.isNotEmpty())
                        parentFrag.properties.forEachIndexed { index1, element1 ->
                            mainFeaturesList.forEachIndexed { index2, element2 ->
                                if (element2.type == "NUMBER") {
                                    if (parentFrag.properties[index1].property == mainFeaturesList[index2].id.toString()) {
                                        mainFeaturesList[index2].isSelected = true
                                        mainFeaturesList[index2].value =
                                            parentFrag.properties[index1].value
                                    }
                                } else {
                                    if (parentFrag.properties[index1].property == mainFeaturesList[index2].id.toString()) {
                                        mainFeaturesList[index2].isSelected = true
                                        mainFeaturesList[index2].value =
                                            parentFrag.properties[index1].value
                                        mainFeaturesList[index2].options!!.forEach { optionItem ->
                                            optionItem.isSelected =
                                                mainFeaturesList[index2].value == optionItem.id.toString()
                                        }
                                    }
                                }
                            }
                        }
                    subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
                    mainFeaturesListInAddEstateAdapter.differ.submitList(mainFeaturesList)
                }
                is Resource.Loading -> {
                    (activity as HomeActivity).binding.contentHomeId.homeProgressBar.visibility =
                        View.VISIBLE
                }
                is Resource.Error -> {
                    // Toast(context).showCustomToast(err,this)
                }
            }
        })
    }

    private fun initialImageListToStartPick() {
        uploadImagesAdapter = UploadImagesAdapter(this, this)
        binding.moreImagesRv.apply {
            adapter = uploadImagesAdapter
        }
    }

    private fun pickNormalImage() {
        CropImage.activity(resultUri)
            .setAspectRatio(1, 1)
            .start(requireActivity(), this)
    }

    private fun pickThreeDImage() {
        CropImage.activity(resultUri)
            .start(requireActivity(), this)
    }

    private fun displaySubFeatures() {
        subFeaturesFromListAdapter = SubFeaturesFromListAdapter(this)
        //  subFeaturesFromListAdapter.setHasStableIds(false) // under review
        binding.subFeaturesRv.apply {
            adapter = subFeaturesFromListAdapter
        }
    }

    private fun displayMainFeatures() {
        mainFeaturesListInAddEstateAdapter = MainFeaturesListInAddEstateAdapter(
            this@AddNewAdsPage2Fragment,
            this@AddNewAdsPage2Fragment
        )
        binding.mainFeaturesRv.apply {
            adapter = mainFeaturesListInAddEstateAdapter
        }
    }

    override fun subFeatureOnItemClick(feature: Feature, position: Int, isSelected: Boolean) {
        if (isSelected) {
            subFeaturesList[position].isSelected = true
            subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
        } else {
            subFeaturesList[position].isSelected = false
            subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
        }
        subFeaturesFromListAdapter.differ.submitList(subFeaturesList)
    }

    private fun displayDataIfExist() {
        if (parentFrag.size != null)
            binding.realEstateAriaTvEt.setText(parentFrag.size.toString())
        if (parentFrag.threeDImagesFile != null)
            binding.addEstateThreeDIv.setImageURI(Uri.fromFile(parentFrag.threeDImagesFile))
        if (parentFrag.pickedImagesList.isNotEmpty())
            uploadImagesAdapter.differ.submitList(parentFrag.pickedImagesList)
        if (!parentFrag.link3D.isNullOrEmpty())
            binding.link3dEt.setText(parentFrag.link3D!![0])
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((resultCode == Activity.RESULT_OK) && ( data != null)) {
            when (requestCode) {
                Constant.IMAGE_FILE_CODE -> {
                    resultUri = data.data!!
                    launchImageCrop(resultUri!!)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result = CropImage.getActivityResult(data)
                    resultUri = result.uri
                    if (imagePickerType == "normalImage$currentImagePosition") {
                        parentFrag.pickedImagesList[currentImagePosition].imageFile =
                            File(resultUri!!.path)
                        parentFrag.pickedImagesList[currentImagePosition].imageName =
                            File(resultUri!!.path).name
                        uploadImagesAdapter.differ.submitList(parentFrag.pickedImagesList)
                        uploadImagesAdapter.notifyDataSetChanged()
                        resultUri = Uri.EMPTY
                    }
                }
            }
        }
    }

    override fun mainFeatureOnItemClick(
        feature: Feature,
        featurePosition: Int,
        isSelectedFeature: Boolean,
        value: String
    ) {
        // for  main category type number
        if (isSelectedFeature) {
            mainFeaturesList[featurePosition].isSelected = true
            mainFeaturesList[featurePosition].value = value
            mainFeaturesListInAddEstateAdapter.differ.submitList(mainFeaturesList)
        } else {
            mainFeaturesList[featurePosition].isSelected = false
            mainFeaturesList[featurePosition].value = "0"
            mainFeaturesListInAddEstateAdapter.differ.submitList(mainFeaturesList)
        }
        mainFeaturesListInAddEstateAdapter.differ.submitList(mainFeaturesList)
        Log.d("feature_name", feature.name)
        Log.d("feature_value", value)
        Log.d("feature_position", featurePosition.toString())
        Log.d("feature_selected", isSelectedFeature.toString())
    }

    override fun optionOnItemClick2(
        thisFeaturePosition: Int,
        thisFeatureId: Int,
        optionPosition: Int,
        optionId: Int,
        isSelectedOption: Boolean
    ) {
        //   for  main category type List
        mainFeaturesList[thisFeaturePosition].isSelected = isSelectedOption
        mainFeaturesList[thisFeaturePosition].value =
            mainFeaturesList[thisFeaturePosition].options!![optionPosition].id.toString()
        mainFeaturesList[thisFeaturePosition].options!![optionPosition].isSelected =
            isSelectedOption
        mainFeaturesList[thisFeaturePosition].options!!.forEachIndexed { index, element ->
            if (index == optionPosition)
                element.isSelected = isSelectedOption
            else element.isSelected = false
        }
        Log.d("feature_name", mainFeaturesList[thisFeaturePosition].name)
        Log.d("feature_value", mainFeaturesList[thisFeaturePosition].value!!)
        Log.d("feature_position", thisFeaturePosition.toString())
        Log.d("feature_selected_option", isSelectedOption.toString())
        Log.d("feature_option_position", optionPosition.toString())
        mainFeaturesListInAddEstateAdapter.differ.submitList(mainFeaturesList)
    }

    override fun onIvClick(uploadImagesModel: UploadImagesModel, currentImagePosition: Int) {
        imagePickerType = "normalImage$currentImagePosition"
        this.currentImagePosition = currentImagePosition
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
            pickNormalImage()
        else
        pickFromGallery()
    }

    override fun onDeleteClick(currentImagePosition: Int) {
        parentFrag.pickedImagesList[currentImagePosition].imageFile = null
        parentFrag.pickedImagesList[currentImagePosition].imageUrlIfUpdate = ""
        uploadImagesAdapter.differ.submitList(parentFrag.pickedImagesList)
        uploadImagesAdapter.notifyDataSetChanged()
    }

    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setAspectRatio(1, 1)
            .start(requireActivity(), this)
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constant.IMAGE_FILE_CODE)
    }
}
