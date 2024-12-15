package com.cyanO94.infinitescrollhorizontalpager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cyanO94.infinitescrollhorizontalpager.ui.theme.InfiniteScrollHorizontalPagerTheme
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InfiniteScrollHorizontalPagerTheme {
                InfiniteHorizontalImagePager(
                    images = persistentListOf(
                        Color.Cyan,
                        Color.Red,
                        Color.Blue,
                        Color.Green
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InfiniteHorizontalImagePager(
    modifier: Modifier = Modifier,
    images: PersistentList<Color>
) {
    if (images.isEmpty()) return

    // 맨 앞과 맨 뒤에 이미지를 하나씩 추가하여 무한 스크롤 구현
    val extendedImages = remember(images) {
        persistentListOf(images.last()) + images + images.first()
    }

    // 초기 페이지는 1로 설정(실제 컨텐츠의 첫 번째 이미지)
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { extendedImages.size }
    )

    // 현재 페이지 애니메이션 종료 조건
    val animationEnd by remember {
        derivedStateOf { pagerState.currentPageOffsetFraction in -0.05f..0.05f }
    }

    var scrollEnabled by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // 페이지 이동 발생 시 일시적으로 스크롤 비활성화
    LaunchedEffect(pagerState.currentPage) {
        scrollEnabled = false
    }

    // 애니메이션이 끝나면 페이지를 재조정하여 무한 반복되도록 처리
    LaunchedEffect(scrollEnabled, animationEnd) {
        if (!scrollEnabled && animationEnd) {
            when (pagerState.currentPage) {
                extendedImages.lastIndex -> {
                    pagerState.scrollToPage(1)
                }
                0 -> {
                    pagerState.scrollToPage(extendedImages.lastIndex - 1)
                }
            }
            scrollEnabled = true
        }
    }

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = scrollEnabled,
            modifier = Modifier.fillMaxWidth()
        ) { pageIndex ->
            val image = extendedImages[pageIndex]
            ImageItem(
                image = image
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clip(CircleShape)
                .background(Color.White)
                .size(40.dp)
                .clickable {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .clip(CircleShape)
                .background(Color.White)
                .size(40.dp)
                .clickable {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
        )
    }
}

@Composable
private fun ImageItem(
    image: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp)
                .background(image)
        ) {}
    }
}
