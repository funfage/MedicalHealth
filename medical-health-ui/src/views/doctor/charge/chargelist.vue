<template>
  <div class="app-container">
    <!-- 查询条件开始 -->
    <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="98px">
      <el-form-item label="患者姓名" prop="patientName">
        <el-input
          v-model="queryParams.patientName"
          placeholder="请输入患者姓名"
          clearable
          size="small"
          style="width:240px"
        />
      </el-form-item>
      <el-form-item label="挂号单号" prop="regId">
        <el-input
          v-model="queryParams.regId"
          placeholder="请输入挂号单号"
          clearable
          size="small"
          style="width:240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button type="primary" icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <!-- 查询条件结束 -->

    <!-- 数据表格开始 -->
    <el-table v-loading="loading" border :data="orderChargeTableList">
      <el-table-column label="订单号" align="center" width="210" prop="orderId" />
      <el-table-column label="挂号单号" align="center" width="200" prop="regId" />
      <el-table-column label="患者姓名" align="center" prop="patientName" />
      <el-table-column label="总金额" align="center" prop="orderAmount" />
      <el-table-column label="支付类型" align="center" prop="payType" :formatter="payTypeFormatter" />
      <el-table-column label="订单状态" prop="orderStatus" align="center" :formatter="orderStatusFormatter" />
      <el-table-column label="创建时间" align="center" prop="createTime" />
      <el-table-column label="操作" width="300px" align="center">
        <template slot-scope="scope">
          <el-button type="text" icon="el-icon-view" size="mini" @click="handleViewItem(scope.row)">查看详情</el-button>
          <el-button type="text" :disabled="scope.row.orderStatus==='1'" icon="el-icon-bank-card" size="mini" @click="handlePayWithCash(scope.row)">现金收费</el-button>
          <el-button type="text" :disabled="scope.row.orderStatus==='1'" icon="el-icon-bank-card" size="mini" @click="handlePayWithZfb(scope.row)">支付宝收费</el-button>
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

    <!-- 查看详情弹出层开始 -->
    <el-dialog
      v-loading="loading"
      :title="title"
      :visible.sync="open"
      width="1000px"
      center
      append-to-body
    >
      <el-table v-loading="itemTableloading" border :data="orderChargeItemList">
        <el-table-column label="详情ID" align="center" width="210" prop="itemId" />
        <el-table-column label="处方ID" align="center" width="200" prop="coId" />
        <el-table-column label="名称" align="center" prop="itemName" />
        <el-table-column label="价格" align="center" prop="itemPrice" />
        <el-table-column label="数量" align="center" prop="itemNum" />
        <el-table-column label="小计" prop="itemAmount" align="center" />
        <el-table-column label="类型" align="center" prop="itemType">
          <template slot-scope="scope">
            {{ scope.row.itemType==='0'?'药品':'检查' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" :formatter="statusFormatter" />
      </el-table>
    </el-dialog>
    <!-- 查看详情弹出层结束 -->

    <!-- 支付宝支付的弹出层开始 -->
    <el-dialog
      title="请使用支付宝支付"
      :visible.sync="openPay"
      center
      :close-on-click-modal="false"
      :before-close="handleClose"
      append-to-body
    >
      <el-form label-position="left" label-width="120px" inline class="demo-table-expand">
        <el-card>
          <el-form-item label="订单号:">
            <span>{{ payObj.orderId }}</span>
          </el-form-item>
          <el-form-item label="总金额:">
            <span>{{ payObj.allAmount }}</span>
          </el-form-item>
        </el-card>
      </el-form>
      <div style="text-align:center">
        <vue-qr :text="payObj.payUrl" :size="200" />
        <div>请使用支付宝扫码</div>
      </div>
    </el-dialog>
    <!-- 支付宝支付的弹出层结束 -->
  </div>
</template>

<script>
// 这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
// 例如：import 《组件名称》 from '《组件路径》';
import { queryAllOrderChargeForPage, queryOrderChargeOrderId, queryOrderChargeItemByOrderId, toPayOrderWithZfb, payWithCash } from '@/api/doctor/charge'
// 引入生成二维码的组件
import vueQr from 'vue-qr'
export default {
  components: {
    vueQr
  },
  data() {
    return {
      // 是否启用遮罩层
      loading: false,
      // 详情表格的遮罩
      itemTableloading: false,
      // 分页数据总条数
      total: 0,
      // 弹出层标题
      title: '',
      // 是否显示支付宝支付的弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        patientName: undefined,
        regId: undefined
      },
      // 数据表格的数据
      orderChargeTableList: [],
      // 支付类型字典数据
      payTypeOptions: [],
      // 订单状态字典数据
      orderStatusOptions: [],
      // 详情表格的数据
      orderChargeItemList: [],
      // 详情状态的数据字典
      statusOptions: [],
      // 支付对象
      payObj: {},
      // 是否打开支付宝二维码支付层
      openPay: false,
      // 定时轮询对象
      intervalObj: undefined
    }
  },
  created() {
    // 查询支付类型字典数据
    this.getDataByType('his_pay_type_status').then(res => {
      this.payTypeOptions = res.data
    })
    // 订单状态字典数据
    this.getDataByType('his_order_charge_status').then(res => {
      this.orderStatusOptions = res.data
    })
    // 加载详情状态的字典数据
    this.getDataByType('his_order_details_status').then(res => {
      this.statusOptions = res.data
    })
    // 查询订单数据
    this.listOrderCharge()
  },
  // 方法集合
  methods: {
    listOrderCharge() {
      this.loading = true
      queryAllOrderChargeForPage(this.queryParams).then(res => {
        this.orderChargeTableList = res.data
        this.total = res.total
        this.loading = false
      }).catch(() => {
        this.msgError('查询失败')
        this.loading = false
      })
    },

    /* 查询条件相关方法 */
    handleQuery() {
      this.listOrderCharge()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.listOrderCharge()
    },

    /* 数据表格相关方法 */
    payTypeFormatter(row) {
      return this.selectDictLabel(this.payTypeOptions, row.payType)
    },
    orderStatusFormatter(row) {
      return this.selectDictLabel(this.orderStatusOptions, row.orderStatus)
    },
    // 查看详情
    handleViewItem(row) {
      this.open = true
      this.title = '查询【' + row.patientName + '】支付订单详情'
      this.itemTableloading = true
      queryOrderChargeItemByOrderId(row.orderId).then(res => {
        this.orderChargeItemList = res.data
        this.itemTableloading = false
      }).catch(() => {
        this.msgError('查询失败')
        this.itemTableloading = false
      })
    },
    // 现金收费
    handlePayWithCash(row) {
      this.$confirm('是否确定现金支付?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        payWithCash(row.orderId).then(res => {
          this.msgSuccess('现金支付成功')
          this.loading = false
          this.listOrderCharge()// 刷新列表
        }).catch(() => {
          this.msgError('现金支付失败')
          this.loading = false
        })
      }).catch(() => {
        this.msgError('现金支付已取消')
        this.loading = false
      })
    },
    // 支付宝收费
    handlePayWithZfb(row) {
      toPayOrderWithZfb(row.orderId).then(res => {
        this.msgSuccess('请扫码支付')
        this.payObj = res.data
        const tx = this
        tx.openPay = true
        // 每两秒钟轮询一次
        tx.intervalObj = setInterval(() => {
          // 查询订单
          queryOrderChargeOrderId(tx.payObj.orderId).then(r => {
            if (r.data.orderStatus === '1') {
              // 清空定时器
              clearInterval(tx.intervalObj)
              tx.$notify({
                title: '支付成功',
                message: '订单号为【' + tx.payObj.orderId + '】的订单支付成功',
                type: 'success'
              })
              tx.openPay = false
              tx.listOrderCharge()
            }
          }).catch(() => {
            clearInterval(tx.intervalObj)
            tx.openPay = false
          })
        }, 2000)
        this.loading = false
      }).catch(() => {
        this.msgError('支付失败')
        this.loading = false
      })
    },

    /* 分页控件相关方法 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      // 重新查询
      this.listOrderCharge()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      // 重新查询
      this.listOrderCharge()
    },

    /* 查看详情弹出层相关方法 */
    statusFormatter(row) {
      return this.selectDictLabel(this.statusOptions, row.status)
    },

    handleClose() {
      this.$confirm('您确定放弃支付吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.msgError('您已放弃支付，可以回到收费查询列表里面再次支付')
        this.openPay = false
        // 关闭轮询
        clearInterval(this.intervalObj)
      }).catch(() => {
        this.msgSuccess('欢迎继续支付')
      })
    }
  }
}
</script>
<style scoped>
/* @import url(); 引入公共css类 */

</style>
