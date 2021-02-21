<template>
  <div class="app-container">
    <!-- 查询条件开始 -->
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="字典名称" prop="dictName">
        <el-input
          v-model="queryParams.dictName"
          type="text"
          placeholder="请输入字典名称"
          clearable
          size="small"
          style="width:240px"
        />
      </el-form-item>
      <el-form-item label="字典类型" prop="dictType">
        <el-input
          v-model="queryParams.dictType"
          type="text"
          placeholder="请输入字典名称"
          clearable
          size="small"
          style="width:240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="字典状态"
          clearable
          size="small"
          style="width:240px"
        >
          <el-option
            v-for="dict in statusOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-date-picker
          v-model="dateRange"
          clearable
          size="small"
          style="width:240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button type="primary" icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 查询条件结束 -->

    <!-- 表格工具按钮开始 -->
    <el-row :gutter="10" style="margin-bottom: 8px;">
      <el-col :span="1.5">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" icon="el-icon-refresh" size="mini" @click="handleCacheAsync">缓存同步</el-button>
      </el-col>
    </el-row>
    <!-- 表格工具按钮结束 -->

    <!-- 数据表格开始 -->
    <el-table v-loading="loading" border :data="dictTypeTableList" @selection-change="handleSelectionChnage">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" prop="dictId" align="center" />
      <el-table-column label="字典名称" prop="dictName" align="center" :show-overflow-tooltip="true" />
      <el-table-column label="字典类型" prop="dictType" align="center" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <router-link :to="'/dict/data/' + scope.row.dictId" class="link-type">
            <span>{{ scope.row.dictType }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="status" align="center" :formatter="statusFormatter" />
      <el-table-column label="备注" prop="remark" align="center" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-edit" size="mini" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button type="text" icon="el-icon-delete" size="mini" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- 数据表格结束 -->
    <!-- 分页控件开始 -->
    <el-pagination
      v-show="total>0"
      :current-page="queryParams.pageNum"
      :page-sizes="[5, 10, 20, 30]"
      :page-size="queryParams.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <!-- 分页控件结束 -->

    <!-- 添加修改弹出层开始 -->
    <el-dialog
      title="title"
      :visible.sync="open"
      width="500px"
      center
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="form.dictName" placeholder="请输入字典名称" clearable size="small" />
        </el-form-item>
        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="请输入字典类型" clearable size="small" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusOptions"
              :key="dict.dictValue"
              :label="dict.dictValue"
              :value="dict.dictValue"
            >{{ dict.dictLabel }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入字典备注" clearable size="small" />
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSubmit('form')">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </span>
      <!-- 添加修改弹出层结束 -->
    </el-dialog></div>
</template>

<script>
// 这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
// 例如：import 《组件名称》 from '《组件路径》';
// 引入type的api
import { listForPage, addDictType, updateDictType, getDictTypeById, deleteDictTypeByIds, dictCacheAsync } from '@/api/system/dict/type'

export default {
// import引入的组件需要注入到对象中才能使用
  components: {},
  data() {
    // 这里存放数据
    return {
      // 是否启用遮罩层
      loading: false,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 分页数据总条数
      total: 0,
      // 字典表格数据
      dictTypeTableList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 状态数据字典
      statusOptions: [],
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        dictName: undefined,
        dictType: undefined,
        status: undefined
      },
      // 表单数据
      form: {},
      // 表单校验
      rules: {
        dictName: [
          { required: true, message: '字典名称不能为空', trigger: 'blur' }
        ],
        dictType: [
          { required: true, message: '字典类型不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  // 监听属性 类似于data概念
  computed: {},
  // 监控data中的数据变化
  watch: {},
  // 生命周期 - 创建完成（可以访问当前this实例）
  created() {
    // 查询表格数据
    this.getDictTypeList()
    // 使用全局的数据字典类型查询数据字典数据的方法
    this.getDataByType('sys_normal_disable').then(res => {
      this.statusOptions = res.data
    })
  },
  // 方法集合
  methods: {
    // 查询表格数据
    getDictTypeList() {
      // 打开遮罩
      this.loading = true
      // 调用Type的api的listForPage方法进行分页查询
      listForPage(this.addDateRange(this.queryParams, this.queryParams)).then(res => {
        this.dictTypeTableList = res.data
        this.total = res.total
        this.loading = false
      })
    },
    /* 与搜索相关的方法 */
    // 条件查询
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getDictTypeList()
    },
    // 重置查询条件
    resetQuery() {
      // 使用全局表单重置方法
      this.resetForm('queryForm')
      // 将日期数组也设置为空数组
      this.dateRange = []
      // 重新获取查询的字典
      this.getDictTypeList()
    },

    /* 与表格工具按钮相关的方法 */
    // 打开添加的弹出层
    handleAdd() {
      // 打开弹出层
      this.open = true
      // 对弹出层中的表单信息进行重置
      this.reset()
    },

    // 缓存同步
    handleCacheAsync() {
      this.loading = true
      dictCacheAsync().then(res => {
        this.loading = false
        this.msgSuccess('缓存同步成功')
      })
    },

    /* 与表格数据相关的方法 */
    // 翻译状态
    statusFormatter(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 打开修改的弹出层
    handleUpdate(row) {
      // 根据不同的修改方式获取id
      const dictId = row.dictId || this.ids
      this.open = true
      this.reset()
      // 根据dictId查询一个字典信息
      getDictTypeById(dictId).then(res => {
        // 将查询的数据复制给form即可
        this.form = res.data
      })
    },
    // 删除字典
    handleDelete(row) {
      // 根据不同的修改方式获取id
      const dictIds = row.dictId || this.ids
      this.$confirm('此操作将永久删除该字典数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteDictTypeByIds(dictIds).then(res => {
          this.loading = false
          this.msgSuccess('删除成功')
          // 重新查询数据
          this.getDictTypeList()
        })
      })
    },
    // 数据表格的多选框选择时触发
    handleSelectionChnage(selection) {
      // 获取所有的id值
      this.ids = selection.map(item => item.dictId)
      // 当所选择的个数不是1时无法修改
      this.single = selection.length !== 1
      // 当未勾选时无法删除
      this.multiple = !selection.length
    },

    /* 分页控件相关的方法 */
    // 当pageSize发生变化时触发
    handleSizeChange(val) {
      // 给pageSize重新赋值
      this.queryParams.pageSize = val
      // 重新查询
      this.getDictTypeList()
    },
    // 当页码改变时触发
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getDictTypeList()
    },

    /* 与添加修改弹出层相关的方法 */
    // 取消
    cancel() {
      this.open = false
    },
    // 重置弹出层中的表单
    reset() {
      this.form = {
        dictId: undefined,
        dictName: undefined,
        dictType: undefined,
        status: '0',
        remark: undefined
      }
      this.resetForm('form')
    },
    // 提交
    handleSubmit(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.loading = true
          // 做添加
          if (this.form.dictId === undefined) {
            addDictType(this.form).then(res => {
              this.msgSuccess('保存成功')
              this.loading = false
              // 重新查询列表数据
              this.getDictTypeList()
              // 退出弹出层
              this.open = false
            }).catch(() => {
              this.loading = false
            })
          } else { // 做修改
            updateDictType(this.form).then(res => {
              this.msgSuccess('修改成功')
              this.loading = false
              // 重新查询列表数据
              this.getDictTypeList()
              // 退出弹出层
              this.open = false
            }).catch(() => {
              this.loading = false
            })
          }
        }
      })
    }
  }
}
</script>
<style scoped>
/* @import url(); 引入公共css类 */

</style>
