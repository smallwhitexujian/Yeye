package com.will.imageloader;

import android.content.Context;
import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.will.imageloader.configs.imagepipeline.ImagePipelineConfigFactory;

/**
 * Created by jjfly on 16-3-16.
 */
public class FrescoHelper {


    public static void frescoInitDefault(Context context){
        ImagePipelineConfig config = ImagePipelineConfigFactory.getImagePipelineConfig(context);
        Fresco.initialize(context,config);
    }

    public static void frescoInit(Context context,ImagePipelineConfig config){
        Fresco.initialize(context,config);
    }

    public static void frescoInit(Context context){
        Fresco.initialize(context);
    }



    public static void clean(Uri uri){
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.isInBitmapMemoryCache(uri);
    }


    //清除sdcard缓存
    public static void cleanDiskCache(){
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearDiskCaches();
    }

    //清除内存缓存
    public static void cleanMemoryCache(){
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    //清除内存sdcard缓存
    public static void cleanAllCache(){
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearCaches();
    }

}
