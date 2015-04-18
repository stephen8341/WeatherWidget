
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_AIDL_INCLUDES := $(LOCAL_PATH)/src
LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += \
        src/com/aidl/IService.aidl \

LOCAL_PACKAGE_NAME := DakeleWeather
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_JNI_SHARED_LIBRARIES := liblocSDK4

LOCAL_STATIC_JAVA_LIBRARIES := \
	afinal_0.5_bin \
	locSDK_4.0 \
	android-support-v4

include $(BUILD_PACKAGE)

include $(CLEAR_VARS)	
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := \
	afinal_0.5_bin:libs/afinal_0.5_bin.jar \
	locSDK_4.0:libs/locSDK_4.0.jar 

#LOCAL_PREBUILT_LIBS :=liblocSDK4:libs/armeabi/liblocSDK4.so
LOCAL_MODULE_TAGS := optional
include $(BUILD_MULTI_PREBUILT) 

include $(CLEAR_VARS)

LOCAL_MODULE := liblocSDK4.so	
LOCAL_MODULE_PATH := $(TARGET_OUT_SHARED_LIBRARIES)
LOCAL_SRC_FILES := libs/armeabi/liblocSDK4.so	
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := SHARED_LIBRARIES

include $(BUILD_PREBUILT)