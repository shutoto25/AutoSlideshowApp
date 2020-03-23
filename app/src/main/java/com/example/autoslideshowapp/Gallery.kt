package com.example.autoslideshowapp

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log

open class Gallery(cursor: Cursor) {
    /**
     * クラス名.
     */
    private val CLASS_NAME = "Gallery"
    /**
     * Cursor.
     */
    private val mCursor = cursor


    /**
     * 一番最初の画像を取得.
     * @return uri?
     */
    fun getFirst(): Uri? {
        Log.d(TAG, "$CLASS_NAME.getFirst")

        return when (mCursor.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            true -> {
                val fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = mCursor.getLong(fieldIndex)
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
            false -> null
        }
    }


    /**
     * 次の画像を取得.
     * @return uri?
     */
    fun getNext(): Uri? {
        Log.d(TAG, "$CLASS_NAME.getNext")

        return when (mCursor.moveToNext()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            true -> {
                val fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = mCursor.getLong(fieldIndex)
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
            false -> null
        }
    }


    /**
     * 前の画像を取得.
     * @return uri?
     */
    fun getPrevious(): Uri? {
        Log.d(TAG, "$CLASS_NAME.getPrevious")

        return when (mCursor.moveToPrevious()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            true -> {
                val fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = mCursor.getLong(fieldIndex)
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
            false -> null
        }
    }


    /**
     * 一番最後の画像を取得.
     * @return uri?
     */
    fun getLast(): Uri? {
        Log.d(TAG, "$CLASS_NAME.getLast")

        return when (mCursor.moveToLast()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            true -> {
                val fieldIndex = mCursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = mCursor.getLong(fieldIndex)
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            }
            false -> null
        }
    }

    /**
     * カーソルを閉じる.
     */
    fun close() {
        Log.d(TAG, "$CLASS_NAME.close")

        mCursor.close()
    }
}