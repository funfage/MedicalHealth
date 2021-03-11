import request from '@/utils/request'
// 根据挂号ID查询未支付的处方信息及详情
export function getNoChargeCareHistoryByRegId(regId) {
  return request({
    url: '/doctor/charge/getNoChargeCareHistoryByRegId/' + regId,
    method: 'get'
  })
}
// 处方收费里面的现金支付
export function createOrderChargeWithCash(data) {
  return request({
    url: '/doctor/charge/createOrderChargeWithCash',
    method: 'post',
    data: data
  })
}
// 处方收费里面的支付宝支付
export function createOrderChargeWithZfb(data) {
  return request({
    url: '/doctor/charge/createOrderChargeWithZfb',
    method: 'post',
    data: data
  })
}
// 根据订单ID查询订单信息【验证是否支付成功】
export function queryOrderChargeOrderId(orderId) {
  return request({
    url: '/doctor/charge/queryOrderChargeOrderId/' + orderId,
    method: 'get'
  })
}
// 根据订单ID删除订单信息【验证是否支付成功】
export function deleteOrderChargeAndItemsByOrderId(orderId) {
  return request({
    url: '/doctor/charge/deleteOrderChargeAndItemsByOrderId/' + orderId,
    method: 'delete'
  })
}
// 分页查询所有的订单
export function queryAllOrderChargeForPage(params) {
  return request({
    url: '/doctor/charge/queryAllOrderChargeForPage',
    method: 'get',
    params: params
  })
}
// 根据订单ID查询订单详情
export function queryOrderChargeItemByOrderId(orderId) {
  return request({
    url: '/doctor/charge/queryOrderChargeItemByOrderId/' + orderId,
    method: 'get'
  })
}
// 根据订单ID返回订单支付地址和总金额
export function toPayOrderWithZfb(orderId) {
  return request({
    url: '/doctor/charge/toPayOrderWithZfb/' + orderId,
    method: 'get'
  })
}
// 现金支付
export function payWithCash(orderId) {
  return request({
    url: '/doctor/charge/payWithCash/' + orderId,
    method: 'get'
  })
}

