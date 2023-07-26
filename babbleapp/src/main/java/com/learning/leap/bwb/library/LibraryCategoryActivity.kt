package com.learning.leap.bwb.library

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.learning.leap.bwb.R
import com.learning.leap.bwb.model.BabbleTip
import com.learning.leap.bwb.room.BabbleDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class LibraryCategoryActivity : AppCompatActivity() {
    companion object {
        val SUB_CATEGORY = "SUB_CATEGORY"
        val HAS_SUB_CATEGORY = "HAS_SUB_CATEGORY"
    }

    val disposables = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_category)
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.lipstick
                )
            )
        )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (intent.getBooleanExtra(HAS_SUB_CATEGORY, false)) {
            supportActionBar?.title = "Library Subcategories"
//            librarySubCategoryTextView.text = intent.getStringExtra(SUB_CATEGORY)
//            librarySubCategoryTextView.visibility = View.VISIBLE
//            libraryCategoryLinearLayout.visibility = View.GONE
            intent.getStringExtra(SUB_CATEGORY)?.let {
                getSubCategory(it)
            }
        } else {
            supportActionBar?.title = "Library Categories"
            getAllCategories()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId: Int = item.itemId
        if (itemId == android.R.id.home) {
            finish()
        }
        return true

    }

    private fun getAllCategories() {
        val hashMap = mutableMapOf<String, List<BabbleTip>>()
//        val disposable =
//            BabbleDatabase.getInstance().babbleTipDAO().getAll().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    createCategoryList(it, hashMap)
//                }, {
//                    it.printStackTrace()
//                })

        //disposables.add(disposable)
    }

    private fun getSubCategory(category: String) {
//        val hashMap = mutableMapOf<String, List<BabbleTip>>()
//        val disposable =
//            BabbleDatabase.getInstance().babbleTipDAO().getTipsForCategory(category)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    createSubCategoryList(it, hashMap)
//                }, {
//                    it.printStackTrace()
//                })
//
//        disposables.add(disposable)
    }

    private fun getCategoryString(isSubCategory: Boolean, babbleTip: BabbleTip): String {
        return if (isSubCategory) "babbleTip.subcategory" else babbleTip.category
    }

    private fun createCategoryList(
        collection: List<BabbleTip>,
        hashMap: MutableMap<String, List<BabbleTip>>
    ) {
        val disposable = Observable.fromIterable(collection).groupBy { it.category }.flatMapSingle {
            it.toList()
        }.subscribe({
            if (it.isNotEmpty()) {
                hashMap[it.get(0).category] = it
            }
        }, {
            it.printStackTrace()
        }, {
            createList(hashMap)
        })

        disposables.add(disposable)
    }

    private fun createSubCategoryList(
        collection: List<BabbleTip>,
        hashMap: MutableMap<String, List<BabbleTip>>
    ) {
        val disposable =
            Observable.fromIterable(collection).groupBy { it.subCategory }.flatMapSingle {
                it.toList()
            }.subscribe({
                if (it.isNotEmpty()) {
                    hashMap[it.get(0).subCategory] = it
                }
            }, {
                it.printStackTrace()
            }, {
                createList(hashMap)
            })

        disposables.add(disposable)
    }

    private fun createList(hashMap: MutableMap<String, List<BabbleTip>>) {
        var size = 0
        val recyclerViewList = mutableListOf<String>()
        val categoriesStrings = mutableListOf<String>()
        hashMap.forEach {
            if (it.key.isNotEmpty()) {
                size += it.value.size
                categoriesStrings.add(it.key)
                recyclerViewList.add("${it.key}(${it.value.size})")
            }
        }
        recyclerViewList.add(0, "All($size)")
        categoriesStrings.add(0, "All")
        addFavoriteToList(recyclerViewList, categoriesStrings)

    }


    private fun addFavoriteToList(
        recyclerViewList: MutableList<String>,
        categoriesStrings: MutableList<String>
    ) {
//        val disposable = BabbleDatabase.getInstance().babbleTipDAO().getTipsForFavorites()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ results ->
//                var favoriteString: String? = null
//                favoriteString =
//                    if (results.isEmpty() || intent.getBooleanExtra(HAS_SUB_CATEGORY, false)) {
//                        null
//                    } else {
//                        "Favorites(${results.size})"
//                    }
//                favoriteString?.let {
//                    categoriesStrings.add(1, "Favorites")
//                    recyclerViewList.add(1, it)
//                }
//                displayRecyclerView(recyclerViewList, categoriesStrings)
//            }, {
//                it.printStackTrace()
//            })
//
//        disposables.add(disposable)
    }

    fun displayRecyclerView(
        recyclerViewList: MutableList<String>,
        categoriesStrings: MutableList<String>
    ) {
        val adapter = LibraryCategoryAdapter(recyclerViewList)
//        libraryCategoryRecyclerView.adapter = adapter
//        libraryCategoryRecyclerView.addItemDecoration(
//            DividerItemDecoration(
//                this,
//                LinearLayoutManager.VERTICAL
//            )
//        )
//        libraryCategoryRecyclerView.layoutManager = LinearLayoutManager(this)
//        adapter.itemOnClick = { category, position ->
//            if (category.contains("All")) {
//                if (intent.getBooleanExtra(HAS_SUB_CATEGORY, false)) {
//                    val categoryIntent = Intent(this, LibraryActivity::class.java)
//                    categoryIntent.putExtra(LibraryActivity.IS_CATEGORY, true)
//                    categoryIntent.putExtra(
//                        LibraryActivity.LIBRARY_CATEGORY,
//                        intent.getStringExtra(LibraryCategoryActivity.SUB_CATEGORY)
//                    )
//                    startActivity(categoryIntent)
//                } else {
//                    val allIntent = Intent(this, LibraryActivity::class.java)
//                    allIntent.putExtra(LibraryActivity.IS_ALL, true)
//                    startActivity(allIntent)
//                }
//
//            } else if (category.contains("Favorites")) {
//                val favoriteIntent = Intent(this, LibraryActivity::class.java)
//                favoriteIntent.putExtra(LibraryActivity.IS_FAVORITE, true)
//                startActivity(favoriteIntent)
//            } else {
//                if (!intent.getBooleanExtra(HAS_SUB_CATEGORY, false)) {
//                    val subCategoryIntent = Intent(this, LibraryCategoryActivity::class.java)
//                    subCategoryIntent.putExtra(HAS_SUB_CATEGORY, true)
//                    subCategoryIntent.putExtra(SUB_CATEGORY, categoriesStrings[position])
//                    startActivity(subCategoryIntent)
//                } else {
//                    val subCategoryIntent = Intent(this, LibraryActivity::class.java)
//                    subCategoryIntent.putExtra(LibraryActivity.IS_SUB_CATEGORY, true)
//                    subCategoryIntent.putExtra(
//                        LibraryActivity.LIBRARY_SUB_CATEGORY,
//                        categoriesStrings[position]
//                    )
//                    startActivity(subCategoryIntent)
//                }
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}