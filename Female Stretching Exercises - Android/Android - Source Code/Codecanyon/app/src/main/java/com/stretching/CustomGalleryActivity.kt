package com.stretching

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Window
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.stretching.adapter.ImageListRecyclerAdapter
import com.stretching.objects.CustomGallery
import kotlinx.android.synthetic.main.activity_custom_gallery.*
import java.util.*

class CustomGalleryActivity : BaseActivity() {



    internal lateinit var handler: Handler
    internal lateinit var imageListRecyclerAdapter: ImageListRecyclerAdapter
    private val imagesUri: HashMap<String, CustomGallery>? = null
    private var limit = 5


    private// show newest photo at beginning of the list
    val galleryPhotos: ArrayList<CustomGallery>
        get() {
            val galleryList = ArrayList<CustomGallery>()

            try {
                val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
                val orderBy = MediaStore.Images.Media._ID

                val imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy
                )

                if (imagecursor != null && imagecursor.count > 0) {

                    while (imagecursor.moveToNext()) {
                        val item = CustomGallery()

                        val dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA)

                        item.sdcardPath = imagecursor.getString(dataColumnIndex)

                        galleryList.add(item)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Collections.reverse(galleryList)
            return galleryList
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_custom_gallery)
        initIntentParams()
        initBack()
        init()
    }

    private fun initIntentParams() {
        try {
            if (intent.extras != null) {
                if (intent.extras!!.containsKey("Limit")) {
                    limit = intent.getIntExtra("Limit", 5)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun init() {
        tvTitleText!!.setText("Select Images")
        handler = Handler()
        recyclerView!!.setLayoutManager(GridLayoutManager(applicationContext, 3))
        imageListRecyclerAdapter = ImageListRecyclerAdapter(applicationContext)
        imageListRecyclerAdapter.setMultiplePick(true)
        recyclerView!!.setAdapter(imageListRecyclerAdapter)
        imageListRecyclerAdapter.setMultiplePick(true)

        imageListRecyclerAdapter.setEventListner(object : ImageListRecyclerAdapter.EventListener {
            override fun onItemClickListener(position: Int) {

                if (!imageListRecyclerAdapter.isSelected(position)) {
                    if (imageListRecyclerAdapter.totalSelected === limit) {
                        showToast("Can not select more than $limit Images", Toast.LENGTH_SHORT)
                        return
                    }
                }
                imageListRecyclerAdapter.changeSelection(position)
                onSelectItemChanged()

            }
        })

        tvDone!!.setOnClickListener {
            val selected = imageListRecyclerAdapter.selected
            if (!selected.isEmpty()) {
                val allPath = arrayOfNulls<String>(selected.size)
                for (i in allPath.indices) {
                    allPath[i] = selected.get(i).sdcardPath
                }
                val data = Intent().putExtra("all_path", allPath)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
        }
        try {
            //showDialog("");
            object : Thread() {
                override fun run() {
                    Looper.prepare()
                    handler.post {
                        //dismissDialog();
                        imageListRecyclerAdapter.addAll(galleryPhotos)
                        //checkImageStatus();
                    }
                    Looper.loop()
                }


            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun onSelectItemChanged() {
        if (imageListRecyclerAdapter.totalSelected > 0) {
            tvTitleText!!.setText(imageListRecyclerAdapter.totalSelected.toString() + " Selected")
        } else {
            tvTitleText!!.setText("Select Images")
        }
    }

}
