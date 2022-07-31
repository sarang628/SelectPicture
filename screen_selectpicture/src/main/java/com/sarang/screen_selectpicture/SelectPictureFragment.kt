package com.sarang.screen_selectpicture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.example.torang_core.data.model.MyImage
import com.sarang.screen_selectpicture.SelectPictureFragment
import com.sarang.screen_selectpicture.databinding.SelectPictureFragmentBinding
import com.sarang.torangimageloader.ImageLoadBindingAdapter.loadImage

class SelectPictureFragment : Fragment(){
    private val mViewModel: SelectPictureViewModel by viewModels()
    var mBinding: SelectPictureFragmentBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_picture_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = SelectPictureFragmentBinding.bind(view)
        val pictureManager: PictureManager = PictureManager.instance

        // Creating adapter for spinner
        val folderList: ArrayList<String> = pictureManager.requestPicFolderList(requireActivity())
        val dataAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, folderList
        )

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        mBinding!!.spinner.adapter = dataAdapter
        mBinding!!.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                (mBinding!!.selectPicRv.adapter as SelectPicRvAdt?)?.setMyImageList(
                    pictureManager.getPicList(
                        requireContext(),
                        folderList[i]
                    )
                )
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        mBinding!!.selectPicRv.setAdapter(object :
            SelectPicRvAdt() {
            override fun clickPicture(myImage: MyImage?) {
                loadImage(mBinding!!.imageView2, myImage?.data)
            }
        })
        mBinding!!.textView.setOnClickListener { }
    }

    val selectedImage: ArrayList<MyImage>
        get() = (mBinding!!.selectPicRv.adapter as SelectPicRvAdt?)?.selectedImgList!!

    companion object {
        fun newInstance(): SelectPictureFragment {
            return SelectPictureFragment()
        }

        fun go(supportFragmentManager: FragmentManager, layoutId: Int) {
            supportFragmentManager.beginTransaction()
                .add(layoutId, newInstance(), SelectPictureFragment::class.java.name)
                .addToBackStack(SelectPictureFragment::class.java.name)
                .commit()
        }
    }
}