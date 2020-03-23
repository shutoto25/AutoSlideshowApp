package com.example.autoslideshowapp

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

/**
 * デバッグ用タグ.
 */
const val TAG = "AutoSlideShowApp"

class MainActivity : AppCompatActivity()
    , View.OnClickListener {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "MainActivity"
    /**
     *パーミッションリクエストコード.
     */
    private val PERMISSION_REQUEST_CODE = 100
    /**
     *リクエストパーミッション.
     */
    private val PERMISSIONS = Manifest.permission.READ_EXTERNAL_STORAGE
    /**
     * パーミッション許諾フラグ.
     */
    private var FLAG_PERMISSION_GRANTED = false
    /**
     * スライドショー中フラグ.
     */
    private var FLAG_SLIDE_SHOW = false
    /**
     * タイマー.
     */
    private var mTimer: Timer? = null
    /**
     * ハンドラ.
     */
    private var mHandler = Handler()
    /**
     * スタートボタンテキスト.
     */
    private val start = "SlideSHow：▷"
    /**
     * ストップボタンテキスト.
     */
    private val stop = "SlideShow：Ⅱ"
    /**
     * Gallery
     */
    private var mGallery: Gallery? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "$CLASS_NAME.onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // リスナーセット.
        setListener()
        // ボタンテキストを初期化.
        btSlideShow.text = start
    }


    override fun onStart() {
        Log.d(TAG, "$CLASS_NAME.onStart")
        super.onStart()

        if (!FLAG_PERMISSION_GRANTED) {
            // パーミッションチェック.
            checkPermissions()
        }
    }


    override fun onPause() {
        Log.d(TAG, "$CLASS_NAME.onPause")
        super.onPause()

        // ボタンテキストとフラグを初期化.
        btSlideShow.text = start
        FLAG_SLIDE_SHOW = false

        if (mTimer != null) {
            // 現在スケジュールされているタスクを破棄してタイマーを終了する
            mTimer!!.cancel()
            mTimer = null
        }
    }


    override fun onDestroy() {
        Log.d(TAG, "$CLASS_NAME.onDestroy")
        super.onDestroy()

        if (mGallery != null) {
            // カーソルを閉じる.
            mGallery!!.close()
        }
    }


    /**
     * パーミッション許諾確認.
     */
    private fun checkPermissions() {
        Log.d(TAG, "$CLASS_NAME.checkPermissions")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(PERMISSIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "パーミッション許諾済み")

                FLAG_PERMISSION_GRANTED = true
                // パーミッション要求を満たしているので画像取得.
                getGallery()
            } else {
                requestPermissions(arrayOf(PERMISSIONS), PERMISSION_REQUEST_CODE)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "$CLASS_NAME.onRequestPermissionsResult")

        // ユーザの選択結果を判定.
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "パーミッション許諾")

            FLAG_PERMISSION_GRANTED = true
            // パーミッション要求を満たしているので画像取得.
            getGallery()
        } else {
            Log.d(TAG, "パーミッション拒否")

            FLAG_PERMISSION_GRANTED = false
        }
    }


    override fun onClick(v: View) {
        Log.d(TAG, "$CLASS_NAME.onClick")

        if (!FLAG_PERMISSION_GRANTED) {
            // パーミッションチェック.
            checkPermissions()
            return
        }

        when (v.id) {
            R.id.btBack -> {
                if (FLAG_SLIDE_SHOW) {
                    // スライドショー中はボタン操作抑止.
                    Toast.makeText(this,
                        R.string.toast_message_slide_show, Toast.LENGTH_SHORT).show()
                    return
                }
                // 前の画像を表示
                // 一番最初の画像で「←」をした場合、一番最後の画像を取得して表示
                var imageUri: Uri? = mGallery!!.getPrevious()
                if (imageUri == null) {
                    imageUri = mGallery!!.getLast()
                }
                ivImage.setImageURI(imageUri)
            }

            R.id.btNext -> {
                if (FLAG_SLIDE_SHOW) {
                    // スライドショー中はボタン操作抑止.
                    Toast.makeText(this,
                        R.string.toast_message_slide_show, Toast.LENGTH_SHORT).show()
                    return
                }
                // 次の画像を表示.
                // 一番最後の画像で「→」をタップした場合、一番最初の画像を取得して表示
                var imageUri: Uri? = mGallery!!.getNext()
                if (imageUri == null) {
                    imageUri = mGallery!!.getFirst()
                }
                ivImage.setImageURI(imageUri)
            }

            R.id.btSlideShow -> {
                if (btSlideShow.text == start) {
                    // ストップボタンとフラグをセット.
                    btSlideShow.text = stop
                    FLAG_SLIDE_SHOW = true
                } else {
                    // スタートボタンとフラグをセット.
                    btSlideShow.text = start
                    FLAG_SLIDE_SHOW = false

                    mTimer!!.cancel()
                    mTimer = null
                    return
                }

                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            // 次の画像を表示.
                            // 一番最後の画像で「→」をタップした場合、一番最初の画像を取得して表示
                            var imageUri: Uri? = mGallery!!.getNext()
                            if (imageUri == null) {
                                imageUri = mGallery!!.getFirst()
                            }
                            ivImage.setImageURI(imageUri)
                        }
                    }
                }, 2000, 2000) // 2000ms経過する毎に2000msのループを設定する
            }
        }
    }


    /**
     * ストレージの画像を取得.
     */
    private fun getGallery() {
        Log.d(TAG, "$CLASS_NAME.getGallery")

        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null) {
            mGallery = Gallery(cursor)
            val imageUri = mGallery!!.getFirst()
            ivImage.setImageURI(imageUri)
        } else {
            Toast.makeText(this,
                R.string.toast_message_no_gallery, Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * セットリスナー.
     */
    private fun setListener() {
        Log.d(TAG, "$CLASS_NAME.setListener")

        btBack.setOnClickListener(this)
        btNext.setOnClickListener(this)
        btSlideShow.setOnClickListener(this)
    }
}

/**
/* 3つのボタン（進む、戻る、再生/停止）を使って、Android端末に保存されているGallery画像を
/* 順に自動送りで表示するスライドショーアプリを作成してください。
/*
/* 下記の要件を満たしてください。
/*
/* プロジェクトを新規作成し、 AutoSlideshowApp というプロジェクト名をつけてください
/* スライドさせる画像は、Android端末に保存されているGallery画像を表示させてください（つまり、ContentProviderの利用）
/* 画面にはImageViewと3つのボタン（進む、戻る、再生/停止）を配置してください
/* 進むボタンで1つ先の画像を表示し、戻るボタンで1つ前の画像を表示します
/* 最後の画像の表示時に、進むボタンをタップすると、最初の画像が表示されるようにしてください
/* 最初の画像の表示時に、戻るボタンをタップすると、最後の画像が表示されるようにしてください
/* 再生ボタンをタップすると2秒後に自動送りが始まり、2秒毎にスライドさせてください
/* 自動送りの間は、進むボタンと戻るボタンはタップ不可にしてください
/* 再生ボタンをタップすると、ボタンの表示が「停止」になり、停止ボタンをタップするとボタンの表示が「再生」になるようににしてください
/* 停止ボタンをタップすると自動送りが止まり、進むボタンと戻るボタンをタップ可能にしてください
/* ユーザがパーミッションの利用を「拒否」した場合にも、アプリの強制終了やエラーが発生しないようにして下さい。
/* 要件を満たすものであれば、どのようなものでも構いません。
/* 見栄え良く、楽しめるスライドショーアプリを目指しましょう！
 */