# AcademiaApp

## 核心功能

Academia 是一款针对**科学类英语论文**的便捷的**搜索与管理应用**。其核心功能基于 Arxiv API，并提供多项功能，包括：**简洁的搜索引擎、论文预览、用户及论文管理系统、云端 AI Agent** 等。多数功能需要网络连接。

## 项目简介

- **BIT** 北理工 北京理工大学 2024-2025下/大二下 《Android技术开发基础》课程作业
- **运行环境**： 10.0以上版本Android的Android手机
- **部署方法：** 直接安装Academia.apk即可
- 开发语言及框架：Kotlin + Jetpack Compose + Gradle

## 依赖声明

| 功能         | 依赖                                                         |
| :----------- | ------------------------------------------------------------ |
| UI设计       | Compose 1.8.0；androidx.compose.material:material-icons-core:1.5.4；  androidx.compose.material:material-icons-extended:1.5.4  com.google.accompanist:accompanist-swiperefresh:0.17.0（下拉刷新） |
| 生命周期管理 | androidx.lifecycle:lifecycle-viewmodel-compose:2.8.5；  androidx.lifecycle:lifecycle-livedata-ktx:2.9.0；  androidx.lifecycle:lifecycle-runtime-ktx:2.9.0 |
| 数据存取     | androidx.datastore:datastore-preferences:1.1.7  io.coil-kt:coil-compose:2.3.0（图片加载框架）  androidx.room:room-runtime:2.7.1（数据库） |
| 网络         | com.squareup.retrofit2:retrofit:2.9.0；  com.squareup.retrofit2:converter-gson:2.9.0  com.squareup.okhttp3:okhttp:4.12.0；  com.squareup.okhttp3:logging-interceptor:4.12.0 |
| Arxiv接口    | olegthelilfix:ArxivApiAccess:0.2-RELEASE（第三方库，封装接口）  （Github：https://github.com/ResearchPreprintsTools/ArxivApiAccess） |
| PDF渲染      | io.github.afreakyelf:Pdf-Viewer:2.3.6  （Github：https://github.com/afreakyelf/Pdf-Viewer） |
| 字符解析     | javax.xml.stream:stax-api:1.0-2（XML解析，与Arxiv接口相关）  com.github.jeziellago:compose-markdown:0.5.7  （Github：https://github.com/jeziellago/compose-markdown) |
| 依赖注入     | com.google.dagger:hilt-android:2.56.2  androidx.hilt:hilt-work:1.0.0 |

## 详细介绍请参看开发文档
