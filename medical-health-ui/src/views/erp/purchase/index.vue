<template>
  <div class="app-container">
    <!-- 查询条件开始 -->
    <el-form ref="queryForm" :model="queryParam" :inline="true" label-width="98px">
      <el-form-item label="供应商名称" prop="providerId">
        <el-select
          v-model="queryParam.providerId"
          placeholder="请选择供应商名称"
          clearable
          size="small"
          style="width:180px"
        >
          <el-option
            v-for="dict in providerOptions"
            :key="dict.providerId"
            :label="dict.providerName"
            :value="dict.providerId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="申请人" prop="applyUserName">
        <el-input
          v-model="queryParam.applyUserName"
          placeholder="请输入申请人"
          clearable
          size="small"
          style="width:180px"
        />
      </el-form-item>
      <el-form-item label="单据状态" prop="status">
        <el-select
          v-model="queryParam.status"
          placeholder="单据状态"
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
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="handleToNewPurchase">新增采购</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="el-icon-edit" size="mini" :disabled="single" @click="handleDoAudit">提交审核</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" icon="el-icon-delete" size="mini" :disabled="single" @click="handleDoInvalid">作废</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" icon="el-icon-edit" size="mini" :disabled="single" @click="handleDoInventory">提交入库</el-button>
      </el-col>
    </el-row>
    <!-- 表格工具按钮结束 -->

    <!-- 数据表格开始 -->
    <el-table v-loading="loading" border :data="purchaseTableList" @selection-change="handleSelectionChnage">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单据ID" align="center" width="200" prop="purchaseId">
        <template slot-scope="scope">
          <router-link :to="'/erp/purchase/editPurchase/'+scope.row.purchaseId" class="link-type">
            <span>{{ scope.row.purchaseId }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="供应商" width="180" align="center" prop="providerId" :formatter="providerFormatter" />
      <el-table-column label="采购批发总额" align="center" prop="purchaseTradeTotalAmount" />
      <el-table-column label="状态" prop="status" align="center" :formatter="statusFormatter" />
      <el-table-column label="申请人" align="center" prop="applyUserName" />
      <el-table-column label="入库操作人" align="center" prop="storageOptUser" />
      <el-table-column label="入库时间" width="180" align="center" prop="storageOptTime" />
      <el-table-column label="审核信息" align="center" prop="auditMsg" />
      <el-table-column label="创建时间" width="180" align="center" prop="createTime" />
    </el-table>
    <!-- 数据表格结束 -->
    <!-- 分页控件开始 -->
    <el-pagination
      v-show="total>0"
      :current-page="queryParam.pageNum"
      :page-sizes="[5, 10, 20, 30]"
      :page-size="queryParam.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
    <!-- 分页控件结束 -->
  </div>
</template>

<script>
// 这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
// 例如：import 《组件名称》 from '《组件路径》';
import { listPurchaseForPage, doAudit, doInvalid, doInventory } from '@/api/erp/purchase'
import { selectAllProvider } from '@/api/erp/provider'
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
      purchaseTableList: [],
      // 状态数据字典
      statusOptions: [],
      // 供应商的数据
      providerOptions: [],
      // 查询参数
      queryParam: {
        pageNum: 1,
        pageSize: 10,
        providerId: undefined,
        applyUserName: undefined,
        status: undefined
      }
    }
  },
  // 生命周期 - 创建完成（可以访问当前this实例）
  created() {
    // 使用全局的根据字典类型查询字典数据的方法查询字典数据
    this.getDataByType('his_order_status').then(res => {
      this.statusOptions = res.data
    })
    // 查询所有可用的供应商
    selectAllProvider().then(res => {
      this.providerOptions = res.data
    })
    // 查询表格数据
    this.getPurchaseList()
  },
  // 方法集合
  methods: {
    // 查询表格数据
    getPurchaseList() {
      this.loading = true // 打开遮罩
      listPurchaseForPage(this.queryParam).then(res => {
        this.purchaseTableList = res.data
        this.total = res.total
        this.loading = false// 关闭遮罩
      })
    },
    /* 查询条件相关方法 */
    handleQuery() {
      this.getPurchaseList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.dateRange = []
      this.getPurchaseList()
    },

    /* 表格工具按钮相关方法 */
    handleToNewPurchase() {
      this.$router.replace('/erp/purchase/newPurchase')
    },
    handleSelectionChnage(selection) {
      this.ids = selection.map(item => item.purchaseId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleDoAudit() {
      const purchaseId = this.ids[0]
      const tx = this
      tx.$confirm('是否确认提交单据ID为:' + purchaseId + '的数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        doAudit(purchaseId).then(res => {
          tx.msgSuccess('提交审核成功')
          tx.getPurchaseList()// 全查询
        }).catch(() => {
          tx.msgError('提交审核失败')
        })
      }).catch(() => {
        tx.msgError('提交已取消')
      })
    },
    handleDoInvalid() {
      const purchaseId = this.ids[0]
      const tx = this
      tx.$confirm('是否确认作废单据ID为:' + purchaseId + '的数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        doInvalid(purchaseId).then(res => {
          tx.msgSuccess('作废成功')
          tx.getPurchaseList()// 全查询
        }).catch(() => {
          tx.msgError('作废失败')
        })
      }).catch(() => {
        tx.msgError('作废已取消')
      })
    },
    handleDoInventory() {
      const purchaseId = this.ids[0]
      const tx = this
      tx.$confirm('是否确认提交入库单据ID为:' + purchaseId + '的数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        doInventory(purchaseId).then(res => {
          tx.msgSuccess('入库成功')
          tx.getPurchaseList()// 全查询
        }).catch(() => {
          tx.msgError('入库失败')
        })
      }).catch(() => {
        tx.msgError('入库已取消')
      })
    },

    /* 数据表格相关方法 */
    statusFormatter(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },
    // 翻译供应商
    providerFormatter(row) {
      let name = ''
      this.providerOptions.filter(item => {
        if (parseInt(row.providerId) === parseInt(item.providerId)) {
          name = item.providerName
        }
      })
      return name
    },

    /* 分页控件相关方法 */
    handleSizeChange(val) {
      this.queryParam.pageSize = val
      this.getPurchaseList()
    },
    handleCurrentChange(val) {
      this.queryParam.pageNum = val
      this.getPurchaseList()
    }

  }
}
</script>
