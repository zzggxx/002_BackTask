package com.example.loader.loader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * 加载器是我们加载数据的工具，通过将对应的 URI 以及其他的查询条件传递给加载器，便可让加载器在后台高效地加载数据，
 * 等数据加载完成了便会返回一个 Cursor.
 */

public class AlbumLoader extends CursorLoader {

    public static final String COLUMN_COUNT = "count";

    /**
     * content://media/external/file
     */
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    /**
     * (media_type=? OR media_type =?) AND _size>0) GROUP BY (bucket_id
     */
    private static final String SELECTION =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0"
                    + ") GROUP BY (bucket_id";

    private static final String[] SELECTION_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
    };

    private static final String BUCKET_ORDER_BY = "datetaken DESC";

    private AlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    public static CursorLoader newInstance(Context context) {
        String selection = SELECTION;
        String[] selectionArgs = SELECTION_ARGS;
        return new AlbumLoader(context, selection, selectionArgs);
    }

    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }
}
