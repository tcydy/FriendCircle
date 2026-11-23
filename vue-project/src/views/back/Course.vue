<script setup>
import { reactive, ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Edit, Plus, Search } from "@element-plus/icons-vue";
import request from "@/utils/request.js";

// 表格数据
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)

// 表单数据
const form = ref({})
const dialogFormVisible = ref(false)
const multipleSelection = ref([])

// 搜索条件
const searchForm = reactive({
  name: '',
  creditsMin: null,
  creditsMax: null,
})

// 加载数据
const load = () => {
  request.get("/courses", {
    params: {
      page: pageNum.value,
      pageSize: pageSize.value,
      name: searchForm.name,
      creditsMin: searchForm.creditsMin,
      creditsMax: searchForm.creditsMax,
    }
  }).then(res => {
    if (res.data) {
      tableData.value = res.data.rows
      total.value = res.data.total
    }
  })
}

// 重置搜索
const reset = () => {
  searchForm.name = ""
  searchForm.creditsMin = null
  searchForm.creditsMax = null
  load()
}

// 保存（区分新增和编辑）
const save = () => {
  if (form.value.id) {
    request.put("/courses", form.value).then(res => {
      if (res.code === 1) {
        ElMessage.success("修改成功")
        dialogFormVisible.value = false
        load()
      } else {
        ElMessage.error(res.msg || "修改失败")
      }
    })
  } else {
    request.post("/courses", form.value).then(res => {
      if (res.code === 1) {
        ElMessage.success("新增成功")
        dialogFormVisible.value = false
        load()
      } else {
        ElMessage.error(res.msg || "新增失败")
      }
    })
  }
}

// 添加
const handleAdd = () => {
  form.value = {}
  dialogFormVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  form.value = JSON.parse(JSON.stringify(row))
  dialogFormVisible.value = true
}

// 删除
const del = (id) => {
  request.delete("/courses", {
    params: { ids: [id] }
  }).then(res => {
    if (res.code === 1) {
      ElMessage.success("删除成功")
      load()
    } else {
      ElMessage.error("删除失败")
    }
  })
}

// 批量删除
const delBatch = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning("请至少选择一条记录")
    return
  }

  const ids = multipleSelection.value.map(v => v.id)
  // 正确传参
  request.delete("/courses", {
    params: { ids: ids }
  }).then(res => {
    if (res.code === 1) {
      ElMessage.success("批量删除成功")
      load()
    } else {
      ElMessage.error("批量删除失败")
    }
  })
}



// 表格选择变化
const handleSelectionChange = (val) => {
  multipleSelection.value = val
}

// 分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size
  pageNum.value = 1 // 切换每页条数时回到第一页
  load()
}

// 页码变化
const handleCurrentChange = (current) => {
  pageNum.value = current
  load()
}

// 确认删除
const confirmDelete = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条数据吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    del(id)
  }).catch(() => { }) // 防止用户点击取消时报未捕获的 Promise 错误
}

// 确认批量删除
const confirmBatchDelete = () => {
  if (multipleSelection.value.length === 0) {
    ElMessage.warning("请至少选择一条记录")
    return
  }

  ElMessageBox.confirm(
    '确定要批量删除这些数据吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
    delBatch()
  }).catch(() => { }) // 防止点击取消报错
}

onMounted(() => {
  load()
})

</script>


<template>
  <div class="page">
    <!-- 页面标题 -->
    <header class="page-head">
      <div class="page-head-text">
        <h1 class="page-title">课程管理</h1>
        <p class="page-sub">管理课程账号信息</p>
      </div>
      <div class="page-badge">
        <span class="dot"></span>
        共 {{ total }} 门课程
      </div>
    </header>

    <!-- 搜索区域 -->
    <section class="panel search-panel">
      <div class="search-row">
        <el-input v-model="searchForm.name" placeholder="请输入课程名称" class="filter-input filter-item" :prefix-icon="Search"
          clearable />
        <el-input-number v-model="searchForm.creditsMin" :controls="false" :min="0"
          :max="searchForm.creditsMax ?? Infinity" :precision="1" :step="0.1" placeholder="学分最小值" class="range-input" />
        <span class="range-sep">至</span>
        <el-input-number v-model="searchForm.creditsMax" :controls="false" :min="searchForm.creditsMin ?? 0"
          :max="Infinity" :precision="1" :step="0.1" placeholder="学分最大值" class="range-input" />

        <div class="search-actions">
          <el-button class="btn btn-primary" @click="load">搜索</el-button>
          <el-button class="btn btn-ghost" @click="reset">重置</el-button>
        </div>
      </div>
    </section>

    <!-- 操作按钮区域 -->
    <div class="toolbar-section">
      <el-button class="btn btn-primary" :icon="Plus" @click="handleAdd">新增</el-button>
      <el-button class="btn btn-danger" :icon="Delete" @click="confirmBatchDelete">批量删除</el-button>
    </div>

    <!-- 表格区域 -->
    <section class="panel table-panel">
      <div class="panel-head">
        <span class="panel-title">课程列表</span>
        <span class="panel-hint">点击行首复选框可批量操作</span>
      </div>

      <el-table :data="tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="60" align="center" />
        <el-table-column prop="id" label="ID" width="50" align="center" />
        <el-table-column prop="name" label="课程名称" min-width="160" />
        <el-table-column prop="credits" label="学分" min-width="100" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column prop="updateTime" label="修改时间" min-width="160" />

        <el-table-column label="操作" width="140" align="center" fixed="right">
          <template #default="scope">
            <el-tooltip content="编辑" placement="top" effect="light">
              <el-button circle class="circle-btn" :icon="Edit" @click="handleEdit(scope.row)" />
            </el-tooltip>
            <el-tooltip content="删除" placement="top" effect="light">
              <el-button circle class="circle-btn circle-btn-danger" :icon="Delete"
                @click="confirmDelete(scope.row.id)" />
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-section">
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </section>

    <!-- 表单对话框 -->
    <el-dialog v-model="dialogFormVisible" :title="form.id ? '编辑用户' : '新增用户'" width="460px" class="soft-dialog"
      destroy-on-close>
      <el-form :model="form" label-width="88px">

        <el-form-item label="课程名称" required>
          <el-input v-model="form.name" placeholder="请输入课程名称" />
        </el-form-item>

        <el-form-item label="学分" required>
          <el-input-number v-model="form.credits" :min="0" :max="99.9" :precision="1" :step="0.1" :controls="false"
            placeholder="请输入学分" class="form-number" style="width: 100%" />
        </el-form-item>

      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button class="soft-cancel" @click="dialogFormVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
/* ==================== 设计变量 ==================== */
.page {
  --primary: #00d15f;
  --primary-hover: #12e06f;
  --primary-soft: #e8fbf0;
  --primary-deep: #0b7a3e;
  --bg: #f5f7f9;
  --surface: #ffffff;
  --dark: #1a1d20;
  --muted: #98a2ab;
  --line: #eef1f3;
  --radius-xl: 28px;
  --radius-pill: 999px;
  --shadow-soft: 0 12px 34px rgba(26, 29, 32, 0.05), 0 2px 8px rgba(26, 29, 32, 0.03);

  --el-color-primary: #00d15f;
  --el-border-radius-base: 12px;

  box-sizing: border-box;
  min-height: 100%;
  padding: 28px 32px 40px;
  background: var(--bg);
  color: var(--dark);
  font-family: "Inter", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", -apple-system, sans-serif;
  -webkit-font-smoothing: antialiased;
}

/* ==================== 页头 ==================== */
.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 6px;
  font-size: 26px;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: var(--dark);
}

.page-sub {
  margin: 0;
  font-size: 13px;
  color: var(--muted);
}

.page-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 9px 18px;
  font-size: 13px;
  font-weight: 500;
  color: var(--muted);
  background: var(--surface);
  border-radius: var(--radius-pill);
  box-shadow: var(--shadow-soft);
  white-space: nowrap;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--primary);
  box-shadow: 0 0 0 4px var(--primary-soft);
}

/* ==================== 通用卡片 ==================== */
.panel {
  background: var(--surface);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-soft);
}

/* ==================== 搜索区 ==================== */
.search-panel {
  padding: 18px 22px;
  margin-bottom: 16px;
}

.search-row {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-item {
  flex: 0 0 200px;
  width: 200px;
}

/* 年份范围稍宽一点 */
.filter-date.filter-item {
  flex: 0 0 260px;
  width: 260px;
}

.search-actions {
  display: flex;
  gap: 10px;
  margin-left: auto;
}

.filter-input {
  width: 300px;
}

.filter-input :deep(.el-input__wrapper) {
  height: 44px;
  padding: 0 18px;
  border-radius: var(--radius-pill);
  background: #f7f9fa;
  box-shadow: none;
  transition: background 0.2s ease, box-shadow 0.2s ease;
}

.filter-input :deep(.el-input__wrapper:hover) {
  background: #f2f5f6;
}

.filter-input :deep(.el-input__wrapper.is-focus) {
  background: #fff;
  box-shadow: inset 0 0 0 1.5px var(--primary);
}

.filter-input :deep(.el-input__inner) {
  font-size: 14px;
  color: var(--dark);
}

.filter-input :deep(.el-input__inner::placeholder) {
  color: var(--muted);
}

.filter-input :deep(.el-input__prefix) {
  color: var(--muted);
}

/* ==================== 按钮（药丸状） ==================== */
.btn {
  height: 44px;
  padding: 0 24px;
  font-size: 14px;
  font-weight: 600;
  letter-spacing: 0.01em;
  border: none;
  border-radius: var(--radius-pill);
  transition: background 0.2s ease, color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.btn-primary {
  background: var(--primary);
  color: #06280f;
  box-shadow: 0 10px 22px rgba(0, 209, 95, 0.22);
}

.btn-primary:hover {
  background: var(--primary-hover);
  color: #06280f;
  transform: translateY(-1px);
  box-shadow: 0 12px 26px rgba(0, 209, 95, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-ghost {
  background: #fff;
  color: #5c666e;
  box-shadow: inset 0 0 0 1px var(--line);
}

.btn-ghost:hover {
  background: #f7f9fa;
  color: var(--dark);
}

.btn-danger {
  background: #fff2f2;
  color: #e5484d;
}

.btn-danger:hover {
  background: #ffe6e6;
  color: #cf3a3f;
  transform: translateY(-1px);
}

/* ==================== 工具栏 ==================== */
.toolbar-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}

/* ==================== 表格卡片 ==================== */
.table-panel {
  padding: 6px 24px 20px;
}

/* 表格内容与表头全部居中 */
.table-panel :deep(.el-table th.el-table__cell),
.table-panel :deep(.el-table td.el-table__cell) {
  text-align: center;
}

/* 兜底：cell 内部也居中，避免个别单元格继承到左对齐 */
.table-panel :deep(.el-table th.el-table__cell .cell),
.table-panel :deep(.el-table td.el-table__cell .cell) {
  text-align: center;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 18px 4px 10px;
  gap: 12px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--dark);
}

.panel-hint {
  font-size: 12px;
  color: var(--muted);
}

/* --- 表格本体：轻量化 --- */
.table-panel :deep(.el-table) {
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
  --el-table-header-bg-color: transparent;
  --el-table-row-hover-bg-color: #f6fcf9;
  --el-table-border-color: var(--line);
  --el-table-text-color: var(--dark);
  --el-table-header-text-color: var(--muted);

  background: transparent;
  font-size: 14px;
  color: var(--dark);
}

.table-panel :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.table-panel :deep(.el-table th.el-table__cell) {
  padding: 12px 0;
  background: transparent;
  border-bottom: 1px solid var(--line);
}

.table-panel :deep(.el-table th.el-table__cell .cell) {
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: var(--muted);
}

.table-panel :deep(.el-table td.el-table__cell) {
  padding: 14px 0;
  border-bottom: 1px solid var(--line);
}

.table-panel :deep(.el-table__body tr:last-child td.el-table__cell) {
  border-bottom: none;
}

.table-panel :deep(.el-table__body tr:hover > td.el-table__cell) {
  background: #f6fcf9;
}

/* 选中行：淡绿底 + 左侧荧光绿高亮条 */
.table-panel :deep(.el-table__body tr:has(.el-checkbox__input.is-checked) > td.el-table__cell) {
  background: #f2fcf7;
}

.table-panel :deep(.el-table__body tr:has(.el-checkbox__input.is-checked) > td.el-table__cell:first-child) {
  box-shadow: inset 3px 0 0 var(--primary);
}

/* 复选框主色 */
.table-panel :deep(.el-checkbox__input.is-checked .el-checkbox__inner),
.table-panel :deep(.el-checkbox__input.is-indeterminate .el-checkbox__inner) {
  background: var(--primary);
  border-color: var(--primary);
}

.table-panel :deep(.el-checkbox__inner:hover) {
  border-color: var(--primary);
}

/* 头像 */
.table-panel :deep(.el-avatar) {
  border: 2px solid #fff;
  box-shadow: 0 6px 16px rgba(26, 29, 32, 0.08);
  background: #f5f7f9;
}

/* 行内圆形操作按钮 */
.table-panel :deep(.circle-btn) {
  width: 36px;
  height: 36px;
  margin: 0 3px;
  padding: 0;
  border: none;
  background: #f5f7f9;
  color: #6b757d;
  box-shadow: none;
  transition: background 0.2s ease, color 0.2s ease;
}

.table-panel :deep(.circle-btn:hover) {
  background: var(--primary-soft);
  color: var(--primary-deep);
}

.table-panel :deep(.circle-btn-danger:hover) {
  background: #ffecec;
  color: #e5484d;
}

/* 固定列：必须不透明，否则横向滚动时内容穿透 */
.table-panel :deep(.el-table .el-table-fixed-column--right),
.table-panel :deep(.el-table .el-table-fixed-column--left) {
  background: var(--surface) !important;
  z-index: 2;
}

/* 固定列表头也要不透明 */
.table-panel :deep(.el-table th.el-table-fixed-column--right),
.table-panel :deep(.el-table th.el-table-fixed-column--left) {
  background: var(--surface) !important;
}

/* 固定列 hover 时保持和整行一致的浅绿底 */
.table-panel :deep(.el-table__body tr:hover > .el-table-fixed-column--right),
.table-panel :deep(.el-table__body tr:hover > .el-table-fixed-column--left) {
  background: #f6fcf9 !important;
}

/* 固定列选中行时保持和整行一致的淡绿底 */
.table-panel :deep(.el-table__body tr:has(.el-checkbox__input.is-checked) > .el-table-fixed-column--right),
.table-panel :deep(.el-table__body tr:has(.el-checkbox__input.is-checked) > .el-table-fixed-column--left) {
  background: #f2fcf7 !important;
}

/* 恢复固定列左侧的内阴影分隔线，让固定区和滚动区有视觉分界 */
.table-panel :deep(.el-table .el-table-fixed-column--right.is-first-column) {
  box-shadow: -6px 0 12px -6px rgba(26, 29, 32, 0.08) !important;
}

/* ==================== 分页 ==================== */
.pagination-section {
  display: flex;
  justify-content: flex-end;
  margin-top: 18px;
}

.table-panel :deep(.el-pagination) {
  --el-pagination-font-size: 13px;
  font-weight: 500;
  color: var(--muted);
}

.table-panel :deep(.el-pagination button) {
  border-radius: var(--radius-pill);
  background: transparent;
  color: var(--muted);
}

.table-panel :deep(.el-pagination button:hover) {
  color: var(--dark);
}

.table-panel :deep(.el-pager li) {
  min-width: 32px;
  height: 32px;
  margin: 0 2px;
  line-height: 32px;
  font-weight: 500;
  color: var(--muted);
  background: transparent;
  border-radius: var(--radius-pill);
}

.table-panel :deep(.el-pager li:hover) {
  background: #f2f5f6;
  color: var(--dark);
}

.table-panel :deep(.el-pager li.is-active) {
  background: var(--primary);
  color: #06280f;
  font-weight: 700;
}

.table-panel :deep(.el-pagination .el-select__wrapper) {
  min-height: 32px;
  padding: 0 12px;
  background: #fff;
  border-radius: var(--radius-pill);
  box-shadow: inset 0 0 0 1px var(--line);
}

.table-panel :deep(.el-pagination .el-input__wrapper) {
  background: #fff;
  border-radius: var(--radius-pill);
  box-shadow: inset 0 0 0 1px var(--line);
}

.table-panel :deep(.el-pagination__jump .el-input__wrapper) {
  border-radius: 10px;
}

/* ==================== 弹窗内部（scoped 能生效的部分） ==================== */
.upload-container {
  display: flex;
  /* ← 之前少了这一行，导致 align-items 和 gap 不生效 */
  align-items: center;
  gap: 30px;
}

.form-avatar {
  border: 2px solid #fff;
  box-shadow: 0 8px 20px rgba(26, 29, 32, 0.1);
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
}
</style>

<!-- 弹窗被 teleport 到 body，样式需独立作用域，用 .el-dialog 直接覆盖，加 !important 保证优先级 -->
<style>
/* ========== 弹窗主体 ========== */
.el-dialog {
  padding: 0 !important;
  overflow: hidden !important;
  border-radius: 28px !important;
  box-shadow: 0 30px 80px rgba(26, 29, 32, 0.16) !important;
}

.el-dialog .el-dialog__header {
  padding: 26px 28px 6px !important;
  margin: 0 !important;
}

.el-dialog .el-dialog__title {
  font-size: 18px !important;
  font-weight: 700 !important;
  letter-spacing: -0.01em !important;
  color: #1a1d20 !important;
}

.el-dialog .el-dialog__headerbtn {
  top: 24px !important;
  right: 24px !important;
}

.el-dialog .el-dialog__headerbtn .el-dialog__close {
  color: #98a2ab !important;
}

.el-dialog .el-dialog__headerbtn:hover .el-dialog__close {
  color: #1a1d20 !important;
}

.el-dialog .el-dialog__body {
  padding: 18px 28px 8px !important;
  color: #1a1d20 !important;
}

.el-dialog .el-dialog__footer {
  padding: 10px 28px 26px !important;
}

/* ========== 表单 ========== */
.el-dialog .el-form-item {
  margin-bottom: 20px !important;
}

.el-dialog .el-form-item__label {
  font-size: 13px !important;
  font-weight: 500 !important;
  color: #5c666e !important;
}

/* ========== 输入框 ========== */
.el-dialog .el-input__wrapper {
  padding: 4px 14px !important;
  background: #f9fafb !important;
  border-radius: 14px !important;
  box-shadow: inset 0 0 0 1px #eef1f3 !important;
  transition: box-shadow 0.2s ease, background 0.2s ease;
}

.el-dialog .el-input__wrapper:hover {
  box-shadow: inset 0 0 0 1px #e2e7ea !important;
}

.el-dialog .el-input__wrapper.is-focus {
  background: #fff !important;
  box-shadow: inset 0 0 0 1.5px #00d15f !important;
}

/* ========== 按钮 ========== */
.el-dialog .el-button {
  height: 42px !important;
  padding: 0 24px !important;
  font-weight: 600 !important;
  border-radius: 999px !important;
  transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.el-dialog .el-button--primary {
  background: #00d15f !important;
  border-color: #00d15f !important;
  color: #06280f !important;
  box-shadow: 0 10px 22px rgba(0, 209, 95, 0.22) !important;
}

.el-dialog .el-button--primary:hover,
.el-dialog .el-button--primary:focus {
  background: #12e06f !important;
  border-color: #12e06f !important;
  color: #06280f !important;
}

.el-dialog .el-button.soft-cancel {
  background: #f5f7f9 !important;
  border-color: transparent !important;
  color: #5c666e !important;
}

.el-dialog .el-button.soft-cancel:hover {
  background: #eef1f3 !important;
  color: #1a1d20 !important;
}
</style>