<template>
	<view style="padding: 20rpx">
		<view class="box" style="background-color: antiquewhite; padding: 10rpx 20rpx">
			<uni-icons type="sound" size="16" style="position: relative; top: 2rpx" color="orange"></uni-icons>
			<text style="font-size: 24rpx; margin-left: 5rpx; color: orange">温馨提示：请适量点餐，避免浪费</text>
		</view>

		<navigator url="/pages/address/address" class="box" style="margin: 20rpx 0">
			<!-- 如果收货地址存在，直接显示收货地址，不显示“添加收货地址” -->
			<view v-if="addressId">
				<view style="margin-bottom: 10rpx; display: flex">
					<view style="flex: 1">{{ address.address }}</view>
					<uni-icons type="right" color="#666"></uni-icons>
				</view>
				<view style="color: #888; margin-bottom: 20rpx">
					<text>{{ address.user }}</text>
					<text style="margin-left: 20rpx">{{ address.phone }}</text>
				</view>
				<view style="display: flex">
					<view style="flex: 1; font-weight: bold">平台配送</view>
					<view style="flex: 1; color: dodgerblue; text-align: right">预计30分钟送达</view>
				</view>
			</view>
			<view v-else>
				<uni-icons type="plus" size="18" style="position: relative; top: 4rpx" color="dodgerblue"></uni-icons>
				<text style="margin-left: 5rpx; color: dodgerblue">请添加收货地址</text>
			</view>
		</navigator>

		<!-- 购物车商品和金额信息 -->
		<view class="box" style="margin: 20rpx 0">
			<view style="margin-bottom: 20rpx; color: #666; font-size: 36rpx">{{ business.name }}</view>

			<view>
				<view style="display: flex; grid-gap: 20rpx; margin-bottom: 20rpx" v-for="item in cartList" :key="item.id">
					<view style="width: 140rpx; height: 140rpx">
						<image :src="item.goods.img" style="width: 100%; height: 100%; display: block; border-radius: 10rpx"></image>
					</view>
					<view style="flex: 1; display: flex; flex-direction: column; justify-content: space-between">
						<view>{{ item.goods.name }}</view>
						<view style="color: #888">x {{ item.num }}</view>
					</view>
					<view style="width: 150rpx; text-align: right; color: red; padding-top: 10rpx">￥{{ item.goods.actualPrice }}</view>
				</view>
			</view>
			<!-- 显示优惠和总金额信息 -->
			<view v-if="amount.discount">
				<view style="text-align: right">
					<text>原价</text>
					<text style="color: red">￥{{ amount.amount }}</text>
					<text style="margin-left: 20rpx">已优惠</text>
					<text style="color: red">￥{{ amount.discount }}</text>
					<text style="margin-left: 20rpx">小计</text>
					<text style="color: red; font-size: 36rpx; font-weight: bold">￥{{ amount.actual }}</text>
				</view>
			</view>
		</view>
		<!-- 购物车商品和金额信息 -->

		<!-- 商家承诺 -->
		<view class="box" style="margin: 20rpx 0">
			<view style="display: flex; margin-bottom: 10rpx">
				<view style="flex: 1; font-weight: bold">商家承诺</view>
				<view style="flex: 1; text-align: right; color: #666">售后无忧 · 食无忧</view>
			</view>
			<view style="display: flex; margin-bottom: 10rpx">
				<view style="flex: 1; font-weight: bold">隐私保护</view>
				<view style="flex: 1; text-align: right; color: #666">号码保护 · 隐私无忧</view>
			</view>
			<view style="display: flex; margin-bottom: 10rpx">
				<view style="flex: 1; font-weight: bold">备注</view>
				<!-- 点击备注跳转到/orderComment页面 -->
				<navigator url="/pages/orderComment/orderComment" style="flex: 1; text-align: right; color: #999">
					<text v-if="comment">{{ comment }}</text>
					<text v-else>请填写口味偏好</text>
					<uni-icons type="right" color="#999" style="position: relative; top: 2rpx"></uni-icons>
				</navigator>
			</view>
		</view>
		<!-- 商家承诺 -->

		<view class="box" style="margin-bottom: 110rpx">
			<uni-data-checkbox v-model="payType" :localdata="range"></uni-data-checkbox>
		</view>

		<!-- 提交按钮 -->
		<view class="box" style="position: fixed; bottom: 0; width: 100%; left: -40rpx">
			<view style="text-align: right">
				<text>已优惠</text>
				<text style="color: red">￥{{ amount.discount }}</text>
				<text style="margin-left: 20rpx">小计</text>
				<text style="color: red; font-size: 36rpx; font-weight: bold">￥{{ amount.actual }}</text>
				<button
					size="mini"
					type="primary"
					style="margin-left: 20rpx; background-color: dodgerblue; border-color: dodgerblue; position: relative; top: 6rpx"
					@click="addOrder"
				>
					提交订单
				</button>
			</view>
		</view>
		<!-- 提交按钮 -->
	</view>
</template>

<script>
export default {
	data() {
		return {
			businessId: 0,
			addressId: 0,
			user: uni.getStorageSync('xm-user'),
			cartList: [],
			amount: {},
			business: {},
			address: {},
			comment: '',
			payType: '支付宝', // 支付方式默认为支付宝
			// 支付方式组件变量
			range: [
				{ value: '支付宝', text: '支付宝' },
				{ value: '微信支付', text: '微信支付' }
			]
		};
	},
	// 由于onLoad()只能用于第一次加载界面调用,因此我们把onLoad()替换成onShow()方法,且onShow()方法不能通过options参数传参,我们将子页面的参数存放在缓存'xm-orders'内,通过xmOrders.属性的方式获取
	/* onShow和onLoad函数的区别
			onShow表示只要当页面加载时就会调用,一般配合navigateBack()返回上一级页面使用
			onLoad表示只有页面第一次加载时才会调用
		**/
	onShow() {
		// 获取缓存数据
		let xmOrders = uni.getStorageSync('xm-orders');
		this.businessId = xmOrders.businessId || 0; // 获取商家详情界面传入的参数businessId
		this.addressId = xmOrders.addressId || 0; // 获取地址界面传入的参数addressId
		this.comment = xmOrders.comment || ''; // 获取备注界面传入的参数comment

		this.loadCart();
		this.loadAddress();
	},
	methods: {
		// 提交订单
		addOrder() {
			if (!this.addressId) {
				uni.showToast({
					icon: 'error',
					title: '请选择收货地址'
				});
				return;
			}
			let form = {
				businessId: this.businessId,
				user: this.user.name,
				address: this.address.address,
				phone: this.address.phone,
				comment: this.comment,
				payType: this.payType
			};
			this.$request.post('/orders/addOrder', form).then((res) => {
				if (res.code === '200') {
					// 清除订单缓存
					uni.removeStorageSync('xm-orders');
					uni.showToast({
						icon: 'success',
						title: '下单成功'
					});
					setTimeout(() => {
						// 跳转到订单详情
						uni.switchTab({
							url: '/pages/orders/orders'
						});
					}, 500);
				} else {
					uni.showToast({
						icon: 'error',
						title: res.msg
					});
				}
			});
		},
		// 加载地址信息
		loadAddress() {
			this.$request.get('/address/selectById/' + this.addressId).then((res) => {
				this.address = res.data || {};
			});
		},
		// 加载购物车信息
		loadCart() {
			this.$request.get('/cart/selectAll', { userId: this.user.id, businessId: this.businessId }).then((res) => {
				this.cartList = res.data || [];
				if (this.cartList.length) {
					this.business = this.cartList[0].business || {};
				}
			});

			this.$request.get('/cart/calc', { userId: this.user.id, businessId: this.businessId }).then((res) => {
				this.amount = res.data || {};
			});
		}
	}
};
</script>

<style></style>
