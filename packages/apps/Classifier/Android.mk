LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)

src_dirs := src
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dirs))

LOCAL_PACKAGE_NAME := com.example.wdy.classifier
LOCAL_SDK_VERSION := current
LOCAL_RESOURCE_DIR := $(addprefix $(LOCAL_PATH)/, res)

include $(BUILD_PACKAGE)
include $(CLEAR_VARS)
include $(call all-makefiles-under, $(LOCAL_PATH))
