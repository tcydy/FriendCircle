<script setup>
import {ref, shallowRef } from "vue";

import { Delete, UploadFilled, EditPen } from "@element-plus/icons-vue";
import { serverHost } from "@/config/config.default.js";
import { ElMessage} from "element-plus";
import request from "@/utils/request.js";

import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from "@wangeditor/editor-for-vue";

//定义富文本数据
const htmlContent = ref('')
const editorRefContent = shallowRef();
//wangEditor配置
const editorConfig = {
    placeholder: '请输入内容...',
}
// 工具栏配置（排除图片视频）
const toolbarConfig = {
    excludeKeys: [
        'group-image',
        'group-video',
    ],
};

// 补上 title 初始值
const form = ref({
    title: '',
    status: 0,
    images: []
})
const location = ref({})

//保存
const save = () => {
    // ✅ 删掉 join，直接赋值数组
    form.value.images = images.value;
    form.value.content = htmlContent.value;

    request.post("/posts", form.value).then(res => {
        if (res.code === 1) {
            ElMessage.success("保存成功");
            location.value.postId = res.data;
            // 先执行定位，定位接口完成后再跳转
            getBrowserLocation(() => {
                //window.location.href = '/front/square'
            });
        } else {
            ElMessage.error("保存失败");
        }
    })
}

//多图列表
const images = ref([])

//多图片上传成功处理
const handleImgListUploadSuccess = (res) => {
    images.value.push(res.data);
};

//删除已上传的图片
const removeImgList = (index) => {
    images.value.splice(index, 1);
};

/** 获取定位，增加回调，定位请求结束后再跳转 */
function getBrowserLocation(callback) {
    if (!navigator.geolocation) {
        ElMessage.warning('你的浏览器不支持定位');
        callback?.()
        return
    }
    navigator.geolocation.getCurrentPosition(
        (pos) => {
            location.value.latitude = pos.coords.latitude
            location.value.longitude = pos.coords.longitude
            console.log('经纬度：', pos.coords.latitude, pos.coords.longitude)
            request.put('/posts/location',location.value).then(res => {
                if (res.code !== 1) {
                    ElMessage.error("保存位置失败")
                }
            }).finally(() => {
                callback?.()
            })
        },
        (err) => {
            console.error(err.code, err.message)
            switch (err.code) {
                case 1: ElMessage.error('用户拒绝了定位权限'); break
                case 2: ElMessage.error('暂时无法获取位置'); break
                case 3: ElMessage.error('定位超时'); break
            }
            // 定位失败也允许跳转
            callback?.()
        },
        {
            timeout: 80000,
            enableHighAccuracy: true,
            maximumAge: 0
        }
    )
}

</script>

<template>
    <div class="publish-container">
        <div class="publish-card">

            <!-- 头部设计 (匹配个人信息页) -->
            <header class="card-header">
                <div class="header-icon">
                    <el-icon :size="20">
                        <EditPen />
                    </el-icon>
                </div>
                <div class="header-text">
                    <h1 class="header-title">发布动态</h1>
                    <p class="header-subtitle">分享此刻的所见所闻</p>
                </div>
            </header>

            <el-divider class="custom-divider"></el-divider>

            <el-form :model="form" label-width="100px" class="custom-form">

                <!-- 多图组件上传 -->
                <el-form-item label="多张图片">
                    <div class="upload-container">
                        <div class="image-list" v-if="images.length > 0">
                            <div v-for="(img, index) in images" :key="index" class="image-item">
                                <el-avatar :src="img" :size="200" shape="square" class="uploaded-avatar" />
                                <el-button type="danger" circle :icon="Delete" class="delete-btn"
                                    @click="removeImgList(index)"></el-button>
                            </div>
                        </div>
                        <el-upload :action="`${serverHost}/web/upload`" :on-success="handleImgListUploadSuccess"
                            :show-file-list="false" multiple>
                            <el-button type="primary" plain round :icon="UploadFilled"
                                class="upload-btn">上传图片</el-button>
                        </el-upload>
                    </div>
                </el-form-item>

                <el-form-item label="标题" required>
                    <el-input v-model="form.title" placeholder="简介清晰的标题会吸引更多人" :maxlength="100" show-word-limit
                        class="theme-input" />
                </el-form-item>

                <!-- 富文本编辑 -->
                <el-form-item label="内容" required>
                    <div class="editor-wrapper">
                        <Toolbar class="editor-toolbar" :editor="editorRefContent" :default-config="toolbarConfig"
                            mode="default" />
                        <Editor class="editor-content" v-model="htmlContent" :default-config="editorConfig"
                            mode="default" @onCreated="editorRefContent = $event" />
                    </div>
                </el-form-item>

                <el-form-item label="可见范围" required>
                    <el-radio-group v-model="form.status" class="custom-radio-group">
                        <el-radio value="0" border>全部可见</el-radio>
                        <el-radio value="1" border>仅被关注可见</el-radio>
                        <el-radio value="2" border>仅自己可见</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="地点名称">
                    <el-input v-model="location.locationName" placeholder="请输入地点名称" :maxlength="100" show-word-limit
                        class="theme-input" />
                </el-form-item>
            </el-form>

            <!-- 操作按钮区 -->
            <div class="form-footer">
                <button class="btn-primary" @click="save">发布动态</button>
            </div>

        </div>
    </div>
</template>


<style scoped>
/* ==================== 设计令牌 & 全局容器 ==================== */
.publish-container {
    --c-primary: #00d03c;
    --c-primary-dark: #00bd36;
    --c-primary-soft: rgba(0, 208, 60, 0.12);
    --c-bg: #ffffff;
    --c-text: #1a1a24;
    --c-text-sub: #9ca3af;
    --c-border: #eceef4;
    --c-border-soft: #f1f2f6;

    display: flex;
    justify-content: center;
    align-items: flex-start;
    width: 100%;
    min-height: 100vh;
    background-color: var(--c-bg);
    padding: 0px;
    box-sizing: border-box;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
}

.publish-card {
    width: 100%;
    max-width: 800px;
    background-color: #fff;
    border-radius: 24px;
    padding: 40px;

    box-sizing: border-box;
}

/* ==================== 卡片头部 ==================== */
.card-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 24px;
}

.header-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
    width: 48px;
    height: 48px;
    border-radius: 16px;
    background-color: var(--c-primary-soft);
    color: var(--c-primary);
}

.header-title {
    margin: 0;
    font-size: 22px;
    font-weight: 700;
    letter-spacing: -0.02em;
    color: var(--c-text);
}

.header-subtitle {
    margin: 4px 0 0;
    font-size: 14px;
    color: var(--c-text-sub);
}

.custom-divider {
    margin: 0 0 32px 0;
    border-color: var(--c-border);
}

/* ==================== 表单通用样式 ==================== */
.custom-form :deep(.el-form-item__label) {
    font-weight: 500;
    color: var(--c-text);
    padding-bottom: 8px;
}

/* 统一的输入框风格 */
.theme-input {
    width: 100%;
}

.theme-input :deep(.el-input__wrapper) {
    min-height: 44px;
    padding: 0 16px;
    border: 1px solid transparent;
    border-radius: 16px;
    background-color: var(--c-bg);
    box-shadow: none;
    transition: all 0.3s;
}

.theme-input :deep(.el-input__wrapper.is-focus) {
    background-color: #fff;
    border-color: var(--c-primary);
    box-shadow: 0 0 0 1px var(--c-primary) inset;
}

/* ==================== 多图上传区 ==================== */
.upload-container {
    width: 100%;
}

.image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 16px;
}

.image-item {
    position: relative;
}

.uploaded-avatar {
    border-radius: 12px;
    border: 1px solid var(--c-border);
}

.delete-btn {
    position: absolute;
    top: -8px;
    right: -8px;
    transform: scale(0.75);
    z-index: 2;
}

.upload-btn {
    border-radius: 9999px !important;
    padding: 0 20px;
    height: 36px;
    border-color: var(--c-primary) !important;
    color: var(--c-primary) !important;
}

.upload-btn:hover {
    background-color: var(--c-primary-soft) !important;
}

/* ==================== 富文本编辑器 ==================== */
.editor-wrapper {
    width: 100%;
    border: 1px solid var(--c-border);
    border-radius: 16px;
    overflow: hidden;
    background-color: #fff;
    z-index: 100;
}

.editor-toolbar {
    border-bottom: 1px solid var(--c-border) !important;
    background-color: var(--c-bg);
}

.editor-content {
    min-height: 250px;
    overflow-y: hidden;
}

/* ==================== 单选按钮组优化 ==================== */
.custom-radio-group :deep(.el-radio.is-bordered) {
    border-radius: 12px;
    height: 40px;
    padding: 0 16px;
    border-color: var(--c-border);
    margin-right: 12px;
    margin-bottom: 10px;
}

.custom-radio-group :deep(.el-radio.is-bordered.is-checked) {
    border-color: var(--c-primary);
    background-color: var(--c-primary-soft);
}

.custom-radio-group :deep(.el-radio.is-bordered.is-checked .el-radio__label) {
    color: var(--c-primary-dark);
}

/* ==================== 操作按钮区 ==================== */
.form-footer {
    display: flex;
    justify-content: center;
    margin-top: 50px;
}

.btn-primary {
    width: 100%;
    max-width: 400px;
    height: 50px;
    background-color: var(--c-primary);
    color: #fff;
    border: none;
    border-radius: 9999px;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    box-shadow: 0 8px 20px rgba(0, 208, 60, 0.25);
    transition: all 0.2s;
}

.btn-primary:hover {
    background-color: var(--c-primary-dark);
    transform: translateY(-1px);
}

.btn-primary:active {
    transform: translateY(1px);
}
</style>