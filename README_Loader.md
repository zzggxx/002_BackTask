## 前言

[质疑声在此,又是一个新的领域啊](https://blog.csdn.net/c6E5UlI1N/article/details/79472672)

在 Android 中，任何耗时的操作都不能放在 UI 线程中，所以耗时的操作都需要使用异步加载来实
现。其实，加载耗时数据的常用方式其实也挺多的，就让我们来看一下

1. Thread + Handler
2. AsyncTask
3. Loader

前面两种异步加载方式，相信大家是比较熟悉的，但是第三种方式，可能有些人是没怎么接触过的，
其实在 ContentProvider 中也可能存在耗时的操作，这时候也应该使用异步操作，而 Android
3.0 之后**最推荐的异步操作就是 Loader**，使用 Loader 机制能让我们高效地加载数据

## 一.Loader 简介
Android 3.0 中引入了 Loader 机制，让开发者能轻松在 Activity 和 Fragment
中异步加载数据，Loader 机制具有以下特征：

1. 可用于每个 Activity 或 Fragment
2. 支持异步加载数据
3. 监控数据源并在内容变化时传递新结果
4. 在某一配置更改后重建加载器时，会自动重新连接上一个加载器的游标。因此，它们无需重新查询其数据。

我们用一张图来直观地认识下 Loader 机制和另外两种做法之间的区别(主要就是直接的联系view和
model,后边数据变化的时候不要再去监听更新了,因为这个工作已经有游标替你工作了):

![Loader工作模式](https://raw.githubusercontent.com/zzggxx/Picture/master/BackTask/Loader%E7%89%B9%E7%82%B9.png)

从图片中可以看出 Loader 机制的写法是相当简洁的，可以让我们进行快速的开发，而且效率方面也是非常高的。

## 二、相关类和 API 介绍

在介绍 Loader 的使用之前，我们先来看一下与 Loader 机制相关的一些类和接口

类 / 接口	说明

1. LoaderManager

    一种与 Activity 或 Fragment 相关联的抽象类，用于管理一个或多个 Loader 实例。这有助于应用管理与 Activity 或 Fragment 生命周期相关的、运行时间较长的操作。它常见的用法是 与 CursorLoader 一起使用，不过应用也可以自由写入自己的加载器，用于加载其他类型的数据

2. LoaderManager.LoaderCallbacks

    回调接口，用于客户端与 LoaderManager 进行交互，例如，可以使用 onCreateLoader() 回调方法创建新的加载器

3. Loader

    一种执行异步加载数据的抽象类。这是加载器的基类。我们通常会使用 CursorLoader，但也可以实现自己的子类。当加载器处于活动状态时，应监控其数据源并在内容变化时传递新结果

4. AsyncTaskLoader

    提供 AsyncTask 来执行工作的抽象加载器

5. CursorLoader

    AsyncTaskLoader 的子类，它将查询 ContentResolver 并返回一个 Cursor。使用此加载器是从 ContentProvider 异步加载数据的最佳方式，而不用通过 Activity 或 Fragment 的 API 来执行托管查询

以上便是 Loader 机制相关的类，但并不是我们创建的每个加载器都要用到上述所有的类和接口。但是，为了初始化加载器以及实现一个 Loader 类（如 CursorLoader），我们需要引用 LoaderManager。

### 2.1 加载器的使用

使用加载器的应用通常包括：

1. Activity 或 Fragment
2. LoaderManager 的实例
3. 一个 CursorLoader，用于加载由 ContentProvider 支持的数据。当然我们也可以实现自己的 Loader 或 AsyncTaskLoader 子类，从其他的数据源中加载数据
4. 一个 LoaderManager.LoaderCallbacks 实现，可以使用它来创建新的加载器，并管理对现有加载器的引用
5. 显示加载器数据的方法，如 SimpleCursorAdapter
6. 使用 CursorLoader 时的数据源，如 ContentProvider

#### 启动加载器

1. LoaderManager 可在 Activity 或 Fragment 内管理一个或多个 Loader 实例，每个 Activity 或 Fragment 中只有一个 LoaderManager。通过我们会在 Activity 的 onCreate() 方法或 Fragment 中的 onActivityCreate() 方法内初始化 Loader

```java
    getSupportLoaderManager().initLoader(0，null，this);
```

initLoader() 方法采用以下参数：

* 用于标识加载器的唯一 ID，在代码示例中，ID 为 0
* 在构建时提供给加载器的可选参数（在代码示例中，为 null）
* LoaderManager.LoaderCallbacks 实现，LoaderManager 将调用该实现来报告加载器事件。在此示例中，本地类实现了 LoaderManager.LoaderCallbacks 接口，因此直接传递它对自身的引用 this

initLoader() 调用确保加载器已经初始化且处于活动状态，这可能会出现两种结果：
* 如果指定 ID 的加载器已经存在，那么将重复使用上次创建的加载器
* 如果指定 ID 的加载器不存在，则 initLoader() 将触发 LoaderManager.LoaderCallbacks 中的 onCreateLoader() 方法，在这个方法中，我们可以实现代码以实例化并返回新的加载器

无论何种情况，给定的 LoaderManager.LoaderCallbacks 实现均与加载器相关联，且在加载器状态变化时调用。如果在调用时，调用程序处于启动状态，且请求的加载器已存在并生成了数据，则系统将立即调用 onLoadFinish()

有一点要注意的是，initLoader() 方法将返回已创建的 Loader，但我们不用捕获它的引用。LoaderManager 将自动管理加载器的生命周期。LoaderManager 将根据需要启动和停止加载，并维护加载器的状态及其相关内容。这意味着我们将很少与加载器直接进行交互。当特定事件发生时，我们通常会使用 LoaderManager.LoaderCallbacks 方法干预加载进程。

#### 重启加载器

当我们使用 initLoader()，它将使用含有指定 ID 的现有加载器（如有）。如果没有它会创建一个。但有时，我们想舍弃这些旧数据并重新开始。

要舍弃旧数据，我们需要使用 restartLoader()，例如，当用户的查询更改时，SearchView.OnQueryTextListener 实现将重启加载器。加载器需要重启，以便它能够使用修正后的搜索过滤器执行新查询：

```java
public boolean onQueryTextChanged(String newText){
      mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
      getSupportLoaderManager().restartLoader(0, null, this);
      return true;
}
```

#### 使用 LoaderManager 回调

LoaderManager.LoaderCallbacks 是一个支持客户端与 LoaderManager 交互的回调接口

加载器（特别是 CursorLoader）在停止运行后，仍需保留其数据，这样既可保留 Activity 或 Fragment 的 onStop() 和 onStart() 方法中的数据。当用户返回应用时，无需等待它重新加载这些数据。

LoaderManager.LoaderCallbacks 接口包括以下方法

1. onCreateLoader()：针对指定的 ID 进行实例化并返回新的 Loader
2. onLoadFinished()：将在先前创建的加载器完成加载时调用
3. onLoaderReset()：将在先前创建的加载器重置且其数据因此不可用时调用

##### onCreateLoader()

当我们尝试访问加载器时（例如，通过 initLoader()），该方法将检查是否已存在由该 ID 指定的加载器。如果没有，它将触发 LoaderManager.LoaderCallbacks 中的 onCreateLoader() 方法。在此方法中，我们可以创建加载器，通过这个方法将返回 CursorLoader，但我们也可以实现自己的 Loader 子类。

在下面的示例中，onCreateLoader() 方法创建了 CursorLoader。我们必须使用它的构造方法来构建 CursorLoader。构造方法 需要对 ContentProvider 执行查询时所需的一系列完整信息

```java
    public CursorLoader(Context context, Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        super(context);
        mObserver = new ForceLoadContentObserver();
        mUri = uri;
        mProjection = projection;
        mSelection = selection;
        mSelectionArgs = selectionArgs;
        mSortOrder = sortOrder;
    }
```
参数名	作用
* uri	用于检索内容的 URI
* projection	返回的列的列表。传递 null 时，将返回所有列，这样的话效率会很低
* selection	一种用于声明返回那些行的过滤器，采用 SQL WHERE 子句格式。传递 null 时，将为指定的 URI 返回所有行
* selectionArgs	我们可以在 selection 中包含 ?，它将按照在 selection 中显示的顺序替换为 selectionArgs 中的值
* sortOrder	行的排序依据，采用 SQL ORDER BY 子句格式。传递 null 时，将使用默认排序顺序（可能并未排序）
示例代码：
```java
public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    Uri baseUri;
    if (mCurFilter != null) {
        baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI,
                  Uri.encode(mCurFilter));
    } else {
        baseUri = Contacts.CONTENT_URI;
    }

    String select = "((" + Contacts.DISPLAY_NAME + " NOTNULL) AND ("
            + Contacts.HAS_PHONE_NUMBER + "=1) AND ("
            + Contacts.DISPLAY_NAME + " != '' ))";
    return new CursorLoader(getActivity(), baseUri,
            CONTACTS_SUMMARY_PROJECTION, select, null,
            Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
}
```

##### onloadFinished

当先前创建的加载器完成加载时，将会调用此方法。该方法必须在为此加载器提供的最后一个数据释放之前调用。此时，我们应该移除所有使用的旧数据（因为它们很快就会被释放），但不要自行释放这些数据，因为这些数据归加载器所有，加载器会处理它们。

当加载器发现应用不再使用这些数据时，将会释放它们。例如，如果数据是来自 CursorLoader 的一个游标，则我们不应手动对其调用 close()。如果游标放置在 CursorAdapter 中，则应使用 swapCursor() 方法，使旧 Cursor 不会关闭
```java
SimpleCursorAdapter mAdapter;

public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
}
````

##### onLoadReset

该方法将在 先前创建的加载器重置 且 数据因此不可用 时调用，通过此回调，我们可以了解何时将释放数据，因此能够及时移除其引用。

此实现调用值为 null 的 swapCursor()
```java
SimpleCursorAdapter mAdapter;

public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
}
```
## 三、Loader 机制的使用场景和使用方式

Loader 机制一般用于数据加载，特别是用于加载 ContentProvider 中的内容，比起 Handler + Thread 或者 AsyncTask 的实现方式，Loader 机制能让代码更加的简洁易懂，而且是 Android 3.0 之后最推荐的加载方式。

Loader 机制的 使用场景 有：

* 展现某个 Android 手机有多少应用程序
* 加载手机中的图片和视频资源
* 访问用户联系人

具体逻辑参见Demo.


