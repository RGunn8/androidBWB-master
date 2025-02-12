package com.learning.leap.bwb.library.libraryCategory

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.learning.leap.bwb.BackButton
import com.learning.leap.bwb.R
import com.learning.leap.bwb.destinations.LibraryCategoryScreenDestination
import com.learning.leap.bwb.destinations.LibraryTipScreenDestination
import com.learning.leap.bwb.library.LibraryScreenType
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun LibraryCategoryScreen(navigator: DestinationsNavigator,
                          viewModel: LibraryCategoryViewModel = hiltViewModel(),
                          isSubCategory: Boolean = false,
                          subCategoryString:String = ""
){
 val pairsList = viewModel.pairsStateFlow.collectAsStateWithLifecycle()
  if (isSubCategory){
     viewModel.getLibrarySubCategories(subCategoryString)
  }else{
      viewModel.getLibraryCategories()
  }
    Column(modifier = Modifier.fillMaxSize()) {
    BackButton {
        navigator.popBackStack()
    }
        LibraryCategoryContent(
            isSubCategory,
            subCategoryString,
            pairs = pairsList.value,
            onItemClick = { category ->
                if (category == "All" && !isSubCategory) {
                    navigator.navigate(LibraryTipScreenDestination(LibraryScreenType.ALLTIPS))
                } else if (category == "Favorites"){
                    navigator.navigate(LibraryTipScreenDestination(LibraryScreenType.FAVORITE))
                } else if (category == "All" && isSubCategory) {
                    navigator.navigate(LibraryTipScreenDestination(LibraryScreenType.ALLSUBCATEGORY,subCategoryString))
                }else if (isSubCategory) {
                    navigator.navigate(LibraryTipScreenDestination(LibraryScreenType.SUBCATEGORY,category))
                } else {
                    navigator.navigate(
                        LibraryCategoryScreenDestination(
                            isSubCategory = true,
                            category
                        )
                    )
                }
            })
    }


}

@Composable
fun LibraryCategoryContent( isSubCategory:Boolean, categoryName:String, pairs: List<Pair<String,Int>>, onItemClick:(String) -> Unit){
    Box(
        Modifier
            .fillMaxSize()
            ) {
        Image(
            painter = painterResource(id = R.drawable.library_bg),
            contentDescription = " Library Category Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isSubCategory) {
                Image(
                    painter = painterResource(id = R.drawable.library_icon),
                    contentDescription = "Library Icon",
                    contentScale = ContentScale.FillBounds, modifier = Modifier
                        .padding(top = 24.dp)
                        .size(80.dp)
                )
                Text(
                    text = stringResource(id = R.string.library_category_title),
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(id = R.string.library_category_subtitle),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }else{
                Text(
                    text = categoryName,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                    color = colorResource(id = R.color.lipstick),
                    fontSize = 16.sp
                )
            }


           LazyColumn(){
               items(
                   count = pairs.size,
                   key = {
                       pairs[it].first
                   },
                   itemContent = { index ->
                       val pair = pairs[index]
                       Column(modifier = Modifier
                           .fillMaxWidth()
                           .padding(horizontal = 8.dp)
                           .clickable { onItemClick.invoke(pair.first) }
                           .background(
                               Color.White
                           )
                       ) {
                           Text(text = pair.first + "(${pair.second})",
                               modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                               fontSize = 20.sp)
                           Divider()
                       }
                   }
               )
           }
        }
    }
}

@Composable
@Preview
fun LibraryCategoryPreview(){
    LibraryCategoryContent( isSubCategory = false,"", pairs = listOf(Pair("All",25), Pair("test",12),Pair("play",42),Pair("come one",32),Pair("Nas",15)
    ) , onItemClick = {} )
}

@Composable
@Preview
fun LibrarySubCategoryPreview(){
    LibraryCategoryContent( isSubCategory = true,"Test", pairs = listOf(Pair("All",15), Pair("team",12),Pair("wow",12),) , onItemClick = {} )
}


