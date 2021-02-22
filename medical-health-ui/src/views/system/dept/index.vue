<template>
  <div class="app-container">
    <!-- 查询条件开始 -->
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
      <el-form-item label="科室名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入科室名称"
          clearable
          size="small"
          style="width:240px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="可用状态"
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
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="dateRange"
          size="small"
          style="width:240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholde="开始日期"
          end-placeholde="结束日期"
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
    </el-row>
    <!-- 表格工具按钮结束 -->

    <!-- 数据表格开始 -->
    <el-table v-loading="loading" border :data="deptTableList" @selection-change="handleSelectionChnage">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="科室ID" align="center" prop="deptId" />
      <el-table-column label="科室名称" align="center" prop="deptName" />
      <el-table-column label="科室编码" align="center" prop="deptNumber" />
      <el-table-column label="当前挂号量" align="center" prop="regNumber" />
      <el-table-column label="排序码" align="center" prop="orderNum" />
      <el-table-column label="负责人" align="center" prop="deptLeader" />
      <el-table-column label="电话" align="center" prop="leaderPhone" />
      <el-table-column label="状态" prop="status" align="center" :formatter="statusFormatter" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
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
      :title="title"
      :visible.sync="open"
      width="500px"
      center
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="科室名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入科室名称" clearable size="small" />
        </el-form-item>
        <el-form-item label="科室编码" prop="deptNumber">
          <el-input v-model="form.deptNumber" placeholder="请输入科室编码" clearable size="small" />
        </el-form-item>
        <el-form-item label="挂号开始编号" prop="regNumber">
          <el-input-number v-model="form.regNumber" placeholder="请输入挂号开始编号" clearable size="small" />
        </el-form-item>
        <el-form-item label="负责人" prop="deptLeader">
          <el-input v-model="form.deptLeader" placeholder="请输入负责人" clearable size="small" />
        </el-form-item>
        <el-form-item label="电话" prop="leaderPhone">
          <el-input v-model="form.leaderPhone" placeholder="请输入负责人电话" clearable size="small" />
        </el-form-item>
        <el-form-item label="排序码" prop="orderNum">
          <el-input-number v-model="form.orderNum" clearable size="small" />
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
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </span>
    </el-dialog>
    <!-- 添加修改弹出层结束 -->
  </div>
</template>

<script>
// 这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
// 例如：import 《组件名称》 from '《组件路径》';
// 引入api
import { listDeptForPage, addDept, updateDept, getDeptById, deleteDeptByIds } from '@/api/system/dept'
export default {
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
      deptTableList: [],
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
        deptName: undefined,
        status: undefined
      },
      // 表单数据
      form: {},
      // 表单校验
      rules: {
        deptName: [
          { required: true, message: '科室名称不能为空', trigger: 'blur' }
        ],
        deptNumber: [
          { required: true, message: '科室编码不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    // 使用全局的根据字典类型查询字典数据的方法查询字典数据
    this.getDataByType('sys_normal_disable').then(res => {
      this.statusOptions = res.data
    })
    // 查询表格数据
    this.getDeptList()
  },
  // 方法集合
  methods: {
    // 查询表格数据
    getDeptList() {
      this.loading = true // 打开遮罩
      listDeptForPage(this.addDateRange(this.queryParams, this.dateRange)).then(res => {
        this.deptTableList = res.data
        this.total = res.total
        this.loading = false// 关闭遮罩
      })
    },
    /* 查询条件相关方法 */
    handleQuery() {
      this.getDeptList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.dateRange = []
      this.getDeptList()
    },

    /* 表格工具按钮相关方法 */
    handleAdd() {
      this.open = true
      this.reset()
      this.title = '添加科室信息'
    },
    handleSelectionChnage(selection) {
      this.ids = selection.map(item => item.deptId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },

    /* 数据表格相关方法 */
    statusFormatter(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    handleUpdate(row) {
      this.title = '修改科室信息'
      const deptId = row.deptId || this.ids
      this.open = true
      this.reset()
      // 根据dictId查询一个字典信息
      this.loading = true
      getDeptById(deptId).then(res => {
        this.form = res.data
        this.loading = false
      })
    },
    handleDelete(row) {
      const deptIds = row.deptId || this.ids
      this.$confirm('此操作将永久删除该科室数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteDeptByIds(deptIds).then(res => {
          this.loading = false
          this.msgSuccess('删除成功')
          this.getDeptList()// 全查询
        })
      }).catch(() => {
        this.msgError('删除已取消')
        this.loading = false
      })
    },

    /* 分页控件相关方法 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getDeptList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getDeptList()
    },

    /* 弹出层相关方法 */
    handleSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // 做添加
          this.loading = true
          if (this.form.deptId === undefined) {
            addDept(this.form).then(res => {
              this.msgSuccess('保存成功')
              this.loading = false
              this.getDeptList()// 列表重新查询
              this.open = false// 关闭弹出层
            }).catch(() => {
              this.loading = false
            })
          } else { // 做修改
            updateDept(this.form).then(res => {
              this.msgSuccess('修改成功')
              this.loading = false
              this.getDeptList()// 列表重新查询
              this.open = false// 关闭弹出层
            }).catch(() => {
              this.loading = false
            })
          }
        }
      })
    },
    reset() {
      this.resetForm('form')
      this.form = {
        deptId: undefined,
        deptName: undefined,
        deptLeader: undefined,
        deptNumber: undefined,
        orderNum: 0,
        regNumber: 0,
        status: '0'
      }
    },
    cancel() {
      this.open = false
      this.title = ''
    }
  }
}
</script>
<style scoped>
/* @import url(); 引入公共css类 */

</style>
