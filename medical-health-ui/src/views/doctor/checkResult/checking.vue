<template>
  <div class="app-container">
    <!-- 查询条件开始 -->
    <el-card style="margin-bottom:3px">
      <el-form ref="queryForm" :model="queryParams" :inline="true" label-width="68px">
        <el-form-item label="检查项目" prop="checkItemIds">
          <el-checkbox v-model="checkAll" :indeterminate="isIndeterminate" @change="handleCheckAllChange">全选</el-checkbox>
          <div style="margin: 15px 0;" />
          <el-checkbox-group v-model="queryParams.checkItemIds" @change="handleCheckedItemChange">
            <el-checkbox
              v-for="d in checkItemOptions"
              :key="d.checkItemId"
              :label="d.checkItemId"
              :value="d.checkItemId"
            >{{ d.checkItemName }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="挂号单号" prop="regId">
          <el-input
            v-model="queryParams.regId"
            placeholder="请输入挂号单号"
            clearable
            size="small"
            style="width:380px"
          />
        </el-form-item>
        <el-form-item label="患者姓名" prop="patientName">
          <el-input
            v-model="queryParams.patientName"
            placeholder="请输入患者姓名"
            clearable
            size="small"
            style="width:380px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button type="primary" icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <!-- 查询条件结束 -->
    <!-- 数据表格开始 -->
    <el-card style="margin-bottom:3px">
      <el-table
        ref="refTable"
        v-loading="loading"
        border
        :data="checkResultList"
      >
        <el-table-column type="expand">
          <template slot-scope="props">
            <el-form label-position="left" inline class="demo-table-expand">
              <el-form-item label="价格">
                <span>{{ props.row.price }}</span>
              </el-form-item>
              <el-form-item label="创建时间">
                <span>{{ props.row.createTime }}</span>
              </el-form-item>
            </el-form>
          </template>
        </el-table-column>
        <el-table-column label="检查单位" align="center" prop="itemId" width="250" />
        <el-table-column label="挂号单号" align="center" width="300" prop="regId" />
        <el-table-column label="项目名称" align="center" prop="checkItemName" />
        <el-table-column label="患者姓名" align="center" prop="patientName" />
        <el-table-column label="检查状态" align="center" prop="resultStatus" :formatter="resultStatusFormatter" />
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" size="mini" @click="handleImportResult(scope.row)">录入结果</el-button>
          </template>
        </el-table-column>
      </el-table>
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
    </el-card>
    <!-- 数据表格结束 -->
    <!-- 检查结果录入弹出层开始 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="1000px"
      center
      append-to-body
    >
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="检查结果" prop="resultMsg">
          <el-input
            v-model="form.resultMsg"
            type="textarea"
            :rows="4"
            placeholder="请输入检查结果"
          />
        </el-form-item>
        <el-form-item label="文件上传">
          <el-upload
            :action="uploadPath"
            accept="image/*"
            name="mf"
            :headers="headers"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :on-error="handleError"
            :file-list="fileList"
            list-type="picture"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </span>
    </el-dialog>
    <!-- 检查结果录入弹出层结束 -->
  </div>
</template>

<script>
// 这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
// 例如：import 《组件名称》 from '《组件路径》';
import { selectAllCheckItem } from '@/api/system/checkItem'
import { queryAllCheckingResultForPage, completeCheckResult } from '@/api/doctor/checkResult'
import { getToken } from '@/utils/auth'
export default {
  data() {
    // 这里存放数据
    return {
      // 遮罩
      loading: false,
      // 总条数
      total: 0,
      // 检查项目数据
      checkItemOptions: [],
      // 查询参数
      queryParams: {
        regId: undefined,
        patientName: undefined,
        checkItemIds: [],
        pageNum: 1,
        pageSize: 10
      },
      // 是否全选
      checkAll: true,
      // 是否为半选状态
      isIndeterminate: false,
      // 检查结果的状态字典
      resultStatusOptions: [],
      // 检查结果的数据
      checkResultList: [],
      // 是否打开录入结果的弹出层
      open: false,
      title: '',
      form: {
        itemId: undefined,
        resultMsg: undefined,
        resultImg: undefined
      },
      // 空间里的文件列表
      fileList: [],
      // 声明文件上传路径
      uploadPath: undefined,
      // 文件上传时的请求头部
      headers: undefined,
      // 文件列表json对象
      fileListJsonObj: []
    }
  },
  created() {
    // 加载状态的字典数据
    this.getDataByType('his_check_result_status').then(res => {
      this.resultStatusOptions = res.data
    })
    selectAllCheckItem().then(res => {
      this.checkItemOptions = res.data
      this.queryParams.checkItemIds = this.checkItemOptions.map(item => item.checkItemId)
      this.queryAllCheckingResultForPage()
    })
    // 设置文件上传路径，加上代理的前缀
    this.uploadPath = process.env.VUE_APP_BASE_API + '/system/upload/doUploadImage'
    // 设置请求头token
    this.headers = { 'token': getToken() }
  },
  // 方法集合
  methods: {
    queryAllCheckingResultForPage() {
      this.loading = true
      queryAllCheckingResultForPage(this.queryParams).then(res => {
        this.checkResultList = res.data
        this.loading = false
        this.total = res.total
      }).catch(() => {
        this.msgError('查询失败')
        this.loading = false
      })
    },
    /* 查询条件相关方法 */
    // 全选
    handleCheckAllChange(val) {
      this.queryParams.checkItemIds = val ? this.checkItemOptions.map(item => item.checkItemId) : []
      this.isIndeterminate = false
    },
    // 选择某一个项目
    handleCheckedItemChange(value) {
      const checkedCount = value.length
      this.checkAll = checkedCount === this.checkItemOptions.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.checkItemOptions.length
    },
    handleQuery() {
      this.queryAllCheckingResultForPage()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.checkItemIds = this.checkItemOptions.map(item => item.checkItemId)
      this.queryAllCheckingResultForPage()
    },
    /* 数据表格相关方法 */
    // 录入结果
    resultStatusFormatter(row) {
      return this.selectDictLabel(this.resultStatusOptions, row.resultStatus)
    },
    // 每页显示多少条的数据变化
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryAllCheckingResultForPage()
    },
    // 分页跳转
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.queryAllCheckingResultForPage()
    },
    // 打开检查录入的弹出层
    handleImportResult(row) {
      this.open = true
      this.title = '录入【' + row.patientName + '】的检查结果'
      // 记录当前选中的详情id
      this.form.itemId = row.itemId
    },

    /* 检查结果录入弹出层相关方法 */
    // 移除的回调
    handleRemove(file, fileList) {
      this.fileListJsonObj.filter((value, index) => {
        if (file.response.data.url === value) {
          this.fileListJsonObj.splice(index, 1)
        }
      })
    },
    // 上传成功的回调
    handleSuccess(response, file, fileList) {
      this.fileListJsonObj.push(response.data.url)
    },
    // 上传失败的回调
    handleError() {
      this.msgError('图片上传失败')
    },
    // 录入结果提交
    handleSubmit() {
      const tx = this
      tx.$confirm('是否确定录入检查结果?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        tx.loading = true
        // 设置文件列表url的json对象
        tx.form.resultImg = JSON.stringify(tx.fileListJsonObj)
        completeCheckResult(tx.form).then(res => {
          tx.msgSuccess('录入检查结果成功')
          tx.queryAllCheckingResultForPage()
          tx.cancel()
        }).catch(() => {
          tx.msgError('录入检查结果失败')
          tx.cancel()
          tx.loading = false
        })
      }).catch(() => {
        tx.msgError('录入检查结果已取消')
        tx.cancel()
        tx.loading = false
      })
    },
    cancel() {
      this.open = false
      this.fileList = []
      this.fileListJsonObj = []
      this.form = {
        itemId: undefined,
        resultMsg: undefined,
        resultImg: undefined
      }
    }
  }
}
</script>
<style scoped>
/* @import url(); 引入公共css类 */
  .demo-table-expand {
    font-size: 0;
  }
  .demo-table-expand label {
    width: 90px;
    color: #99a9bf;
  }
  .demo-table-expand .el-form-item {
    margin-right: 0;
    margin-bottom: 0;
    width: 50%;
  }
</style>
