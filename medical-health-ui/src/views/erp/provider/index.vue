<template>
  <div class="app-container">
    <!-- 查询条件开始 -->
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="98px">
      <el-form-item label="供应商名称" prop="providerName">
        <el-input
          v-model="queryParams.providerName"
          placeholder="请输入供应商名称"
          clearable
          size="small"
          style="width:180px"
        />
      </el-form-item>
      <el-form-item label="联系人" prop="contactName">
        <el-input
          v-model="queryParams.contactName"
          placeholder="请输入联系人"
          clearable
          size="small"
          style="width:180px"
        />
      </el-form-item>
      <el-form-item label="供应商电话" prop="contactTel">
        <el-input
          v-model="queryParams.contactTel"
          placeholder="请输入供应商电话"
          clearable
          size="small"
          style="width:180px"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="可用状态"
          clearable
          size="small"
          style="width:180px"
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
    <el-table v-loading="loading" border :data="providerTableList" @selection-change="handleSelectionChnage">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="供应商ID" align="center" width="120" prop="providerId" />
      <el-table-column label="供应商名称" width="220" prop="providerName" />
      <el-table-column label="联系人" align="center" prop="contactName" />
      <el-table-column label="联系人手机" align="center" prop="contactMobile" />
      <el-table-column label="联系人电话" align="center" prop="contactTel" />
      <el-table-column label="银行账号" width="200" align="center" prop="bankAccount" />
      <el-table-column label="供应商地址" align="center" prop="providerAddress" />
      <el-table-column label="状态" prop="status" align="center" :formatter="statusFormatter" />
      <el-table-column label="创建时间" width="180" align="center" prop="createTime" />
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
      width="600px"
      center
      append-to-body
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item el-form-item label="供应商名称" prop="providerName">
          <el-input v-model="form.providerName" placeholder="请输入供应商名称" clearable size="small" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="请输入联系人" clearable size="small" />
        </el-form-item>
        <el-form-item label="手机" prop="contactMobile">
          <el-input v-model="form.contactMobile" placeholder="请输入手机" clearable size="small" />
        </el-form-item>
        <el-form-item label="电话" prop="contactTel">
          <el-input v-model="form.contactTel" placeholder="请输入电话" clearable size="small" />
        </el-form-item>
        <el-form-item label="银行账号" prop="bankAccount">
          <el-input v-model="form.bankAccount" placeholder="请输入银行账号" clearable size="small" />
        </el-form-item>
        <el-form-item label="地址" prop="providerAddress">
          <el-input v-model="form.providerAddress" placeholder="请输入地址" clearable size="small" />
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
import { listProviderForPage, addProvider, updateProvider, getProviderById, deleteProviderByIds } from '@/api/erp/provider'
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
      providerTableList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 状态数据字典
      statusOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        providerName: undefined,
        contactName: undefined,
        contactTel: undefined,
        status: undefined
      },
      // 表单数据
      form: {},
      // 表单校验
      rules: {
        providerName: [
          { required: true, message: '供应商名称不能为空', trigger: 'blur' }
        ],
        contactName: [
          { required: true, message: '联系人不能为空', trigger: 'blur' }
        ],
        contactMobile: [
          { required: true, message: '手机不能为空', trigger: 'blur' },
          { pattern: /^1[3|4|5|7|8|9][0-9]\d{8}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ],
        bankAccount: [
          { required: true, message: '银行账号不能为空', trigger: 'blur' }
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
    this.getProviderList()
  },
  // 方法集合
  methods: {
    // 查询表格数据
    getProviderList() {
      this.loading = true // 打开遮罩
      listProviderForPage(this.queryParam).then(res => {
        this.providerTableList = res.data
        this.total = res.total
        this.loading = false// 关闭遮罩
      })
    },
    /* 查询条件相关方法 */
    handleQuery() {
      this.getProviderList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.dateRange = []
      this.getProviderList()
    },

    /* 表格工具按钮相关方法 */
    handleAdd() {
      this.open = true
      this.reset()
      this.title = '添加生产厂家信息'
    },
    handleSelectionChnage(selection) {
      this.ids = selection.map(item => item.providerId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },

    /* 数据表格相关方法 */
    statusFormatter(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 翻译类型
    typeFormatter(row) {
      return this.selectDictLabel(this.typeOptions, row.typeId)
    },
    handleUpdate(row) {
      this.title = '修改生产厂家信息'
      const providerId = row.providerId || this.ids
      this.open = true
      this.reset()
      // 根据dictId查询一个字典信息
      this.loading = true
      getProviderById(providerId).then(res => {
        this.form = res.data
        this.loading = false
      })
    },
    handleDelete(row) {
      const providerIds = row.providerId || this.ids
      this.$confirm('此操作将永久删除该生产厂家数据, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        deleteProviderByIds(providerIds).then(res => {
          this.loading = false
          this.msgSuccess('删除成功')
          this.getProviderList()// 全查询
        })
      }).catch(() => {
        this.msgError('删除已取消')
        this.loading = false
      })
    },

    /* 分页控件相关方法 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.getProviderList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getProviderList()
    },

    /* 弹出层相关方法 */
    handleSubmit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          // 做添加
          this.loading = true
          if (this.form.providerId === undefined) {
            addProvider(this.form).then(res => {
              this.msgSuccess('保存成功')
              this.loading = false
              this.getProviderList()// 列表重新查询
              this.open = false// 关闭弹出层
            }).catch(() => {
              this.loading = false
            })
          } else { // 做修改
            updateProvider(this.form).then(res => {
              this.msgSuccess('修改成功')
              this.loading = false
              this.getProviderList()// 列表重新查询
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
        providerId: undefined,
        providerName: undefined,
        contactName: undefined,
        contactMobile: undefined,
        contactTel: undefined,
        bankAccount: undefined,
        providerAddress: undefined,
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
