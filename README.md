# Simple Podcast

adalah aplikasi mutimedia yang menyediakan layanan streaming lagu dan podcast


## MVP Architecture

Model-View-Presenter atau yang biasa disingkat menjadi MVP adalah sebuah konsep arsitektur pengembangan aplikasi yang memisahkan antara tampilan aplikasi dengan proses bisnis yang bekerja pada aplikasi. Arsitektur ini akan membuat pengembangan aplikasi kita menjadi lebih terstuktur, mudah di-test dan juga mudah di-maintain.

Berikut penjelasan masing-masing layer pada MVP.
- View, merupakan layer untuk menampilkan data dan interaksi ke user. View biasanya berupa Activity, Fragment atau Dialog di Android. View ini juga yang langsung berkomunikasi dengan user.
- Model, merupakan layer yang menunjuk kepada objek dan data yang ada pada aplikasi.
- Presenter, merupakan layer yang menghubungkan komunikasi antara Model dan View. Setiap interaksi yang dilakukan oleh user akan memanggil Presenter untuk memrosesnya dan mengakses Model lalu mengembalikan responnya kembali kepada View.


sumber : [medium MVP by Eminarti Sianturi
](https://medium.com/easyread/android-mvp-series-membangun-aplikasi-android-dengan-arsitektur-mvp-fbf1f77ecaec)

sumber : [github.com/MindorksOpenSource/android-mvp-architecture](https://github.com/MindorksOpenSource/android-mvp-architecture)


 

# Overview



## Splash Activity

adalah activity yang akan ditampilkan saat aplikasi di buka,
activity ini juga akan menjalankan service 

![GitHub Logo](/design/app_1_small.png) 




## Home Activity

adalah activity yang akan menampilkan total saldo dan menu pengeluaran serta
satu menu laporan, dimana pada saat saldo di klik oleh user, maka akan masuk
ke form input pemasukkan, terdapat 3 menu pengeluaran dan satu menu laporan
menu-menu tersebut menggunakan recycleview dengan 4 item, jika salah satu item di klik
dan di validasi id menunya, maka akan menjalankan aktivity yg dituju sesuai dengan
id menu, contoh saat user menklik menu pengeluaran, maka user akan diarakan ke form
input pengeluaran

![GitHub Logo](/design/app_2_small.png) 



