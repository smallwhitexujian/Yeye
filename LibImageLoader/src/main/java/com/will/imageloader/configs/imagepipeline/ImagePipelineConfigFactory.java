/******
 *  ImagePipeline配置类,
 **/

package com.will.imageloader.configs.imagepipeline;


import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.producers.NetworkFetcher;


public class ImagePipelineConfigFactory {


    private static final String IMAGE_PIPELINE_CACHE_DIR = "image_cache";///data/data/包名/Cache/$IMAGE_PIPELINE_CACHE_DIR

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();//分配的可用内存
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;//使用的缓存数量

    public static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 5 * ByteConstants.MB;//小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 10 * ByteConstants.MB;//小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;//小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）

    public static final int MAX_DISK_CACHE_VERYLOW_SIZE = 10 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_SIZE = 50 * ByteConstants.MB;//默认图磁盘缓存的最大值

    private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "imagepipeline_cache";//小图所放路径的文件夹名


    /**
     * 可以设置NetworkFetcher
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context, NetworkFetcher networkFetcher) {
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
        configureCaches(configBuilder, context);
        configureLoggingListeners(configBuilder);
        if (networkFetcher != null) {
            configBuilder.setNetworkFetcher(networkFetcher);
        }
        ImagePipelineConfig imagePipelineConfig = configBuilder.build();

        return imagePipelineConfig;
    }

    /**
     *  使用默认的 NetworkFetcher
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
        configureCaches(configBuilder, context);
        configureLoggingListeners(configBuilder);
        ImagePipelineConfig imagePipelineConfig = configBuilder.build();
        return imagePipelineConfig;
    }


    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size

        configBuilder
                .setBitmapMemoryCacheParamsSupplier(
                        new Supplier<MemoryCacheParams>() {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(
                        DiskCacheConfig.newBuilder(context)
                                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())
                                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                                .build())
                .setWebpSupportEnabled(true)//支持webp

        ;

    }

    private static void configureLoggingListeners(ImagePipelineConfig.Builder configBuilder) {
        configBuilder.setRequestListeners(Sets.newHashSet((RequestListener) new RequestLoggingListener()));
    }


    //    ImagePipelineConfig config = ImagePipelineConfig.newBuilder()
//            .setBitmapMemoryCacheParamsSupplier(bitmapCacheParamsSupplier)
//            .setCacheKeyFactory(cacheKeyFactory)
//            .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
//            .setExecutorSupplier(executorSupplier)
//            .setImageCacheStatsTracker(imageCacheStatsTracker)
//            .setMainDiskCacheConfig(mainDiskCacheConfig)
//            .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
//            .setNetworkFetchProducer(networkFetchProducer)
//            .setPoolFactory(poolFactory)
//            .setProgressiveJpegConfig(progressiveJpegConfig)
//            .setRequestListeners(requestListeners)
//            .setSmallImageDiskCacheConfig(smallImageDiskCacheConfig)
//            .build();

}
