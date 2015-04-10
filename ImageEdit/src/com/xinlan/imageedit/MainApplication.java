package com.xinlan.imageedit;

import java.io.File;

import lib.imageloader.cache.disc.impl.UnlimitedDiskCache;
import lib.imageloader.cache.disc.naming.HashCodeFileNameGenerator;
import lib.imageloader.cache.memory.impl.LruMemoryCache;
import lib.imageloader.core.DisplayImageOptions;
import lib.imageloader.core.ImageLoader;
import lib.imageloader.core.ImageLoaderConfiguration;
import lib.imageloader.core.assist.QueueProcessingType;
import lib.imageloader.core.decode.BaseImageDecoder;
import lib.imageloader.core.download.BaseImageDownloader;
import lib.imageloader.utils.StorageUtils;
import android.app.Application;

public class MainApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader();
	}

	/**
	 * 
	 * 初始化图片载入框架
	 */
	private void initImageLoader() {
		File cacheDir = StorageUtils.getCacheDirectory(this);
		int MAXMEMONRY = (int) (Runtime.getRuntime().maxMemory());
		// System.out.println("dsa-->"+MAXMEMONRY+"   "+(MAXMEMONRY/5));//.memoryCache(new
		// LruMemoryCache(50 * 1024 * 1024))
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).memoryCacheExtraOptions(480, 800)
				.diskCacheExtraOptions(480, 800, null).threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(MAXMEMONRY / 5))
				.diskCache(new UnlimitedDiskCache(cacheDir))
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(this)) // default
				.imageDecoder(new BaseImageDecoder(false)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);
	}
}
