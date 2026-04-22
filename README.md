# 🐸 MemeAlbum

App Android xem meme từ r/memes với giao diện album ảnh mượt mà.

## Tính năng

- 🎵 **Splash screen** phát YouTube video 10 giây khi mở app
- 📸 **Grid album** 3 cột giống gallery điện thoại
- 🔄 **Auto-update** meme từ Reddit (Hot / New / Top)
- ♾️ **Infinite scroll** tự load thêm khi cuộn xuống
- 🔍 **Zoom ảnh** với PhotoView (pinch to zoom)
- 📤 **Share** ảnh meme
- 📋 **Copy link** ảnh
- 🗑️ **Xóa** meme khỏi danh sách (long press để chọn nhiều)
- ℹ️ **Info panel** hiện tên tác giả, điểm upvote, ngày đăng
- ✨ **Animation mượt** khi mở ảnh (shared element transition)

## Cách build

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 17
- Android SDK 34

### Bước 1: Mở project
```
File → Open → chọn thư mục MemeAlbum
```

### Bước 2: Sync Gradle
Android Studio sẽ tự sync. Nếu không, nhấn **Sync Now** trên thanh thông báo.

### Bước 3: Build & Run
- Kết nối thiết bị Android (API 24+) hoặc mở emulator
- Nhấn **Run ▶** hoặc `Shift+F10`

### Build APK
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```
APK sẽ ở: `app/build/outputs/apk/debug/app-debug.apk`

## Cấu trúc project

```
MemeAlbum/
├── app/src/main/
│   ├── java/com/memealbum/app/
│   │   ├── data/
│   │   │   ├── api/          # Retrofit API + client
│   │   │   ├── model/        # Data models
│   │   │   └── repository/   # Repository pattern
│   │   └── ui/
│   │       ├── adapter/      # RecyclerView adapter
│   │       ├── viewmodel/    # ViewModel
│   │       ├── SplashActivity.kt
│   │       ├── MainActivity.kt
│   │       └── PhotoViewActivity.kt
│   └── res/
│       ├── layout/           # XML layouts
│       ├── drawable/         # Icons & shapes
│       ├── anim/             # Animations
│       └── transition/       # Shared element transitions
```

## Lưu ý

- App dùng Reddit public JSON API (không cần API key)
- YouTube player cần kết nối internet
- Ảnh được cache tự động bởi Glide
