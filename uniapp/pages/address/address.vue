<template>
	<view style="padding: 20rpx">
		<!-- margin-bottom: 110rpx表示设置元素的下外边距（底部边距），以给别的底部元素空出边距防止元素重叠 -->
		<view style="margin-bottom: 110rpx">
			<view class="box" v-for="item in addressList" :key="item.id" style="margin-bottom: 10rpx">
				<view style="display: flex; align-items: center; margin-bottom: 10rpx" @click="selectAddress(item.id)">
					<view style="flex: 1">
						<view style="font-size: 32rpx; margin-bottom: 10rpx">{{ item.address }}</view>
						<view style="color: #888; margin-bottom: 10rpx">
							<text style="margin-right: 10rpx">{{ item.user }}</text>
							<text>{{ item.phone }}</text>
						</view>
					</view>
					<view>
						<uni-icons type="forward" color="#999"></uni-icons>
					</view>
				</view>
				<view style="text-align: right">
					<uni-icons type="compose" color="#999" style="margin-right: 10rpx" @click="handleEdit(item.id)"></uni-icons>
					<uni-icons type="trash-filled" color="#999" @click="delAddress(item.id)"></uni-icons>
				</view>
			</view>
		</view>
		<!-- 跳转到新增收货地址页面 -->
		<!-- 跳转有两种方式：
			方式一：使用<navigator url="">方式实现跳转
			方式二：使用@click方法，在方法里定义navigateTo()跳转方法 -->
		<navigator url="/pages/addAddress/addAddress">
			<view style="position: fixed; bottom: 20rpx; width: calc(100% - 40rpx)">
				<button plain type="primary" style="font-size: 32rpx; padding: 0">新增收货地址</button>
			</view>
		</navigator>
	</view>
</template>

<script>
export default {
	data() {
		return {
			user: uni.getStorageSync('xm-user'),
			addressList: [],
			orderComment: '',
			businessId: 0,
			addressId: 0
		};
	},

	onShow() {
		this.loadAddress();
	},

	methods: {
		// 选择地址是调用函数，将addressId存储到xm-orders缓存对象内，并返回页面
		selectAddress(addressId) {
			let xmOrders = uni.getStorageSync('xm-orders') || {}; // 首先定义一个变量获取缓存里的xmOrders对象
			xmOrders.addressId = addressId; // 将addressId赋值到变量内
			// 再将变量赋值给缓存 这样做的目的是比直接对缓存的数据存取更加安全，不会照成缓存数据的丢失（相比于uni.setStorageSync('xm-orders', {addressId: this.addressId})）
			uni.setStorageSync('xm-orders', xmOrders);
			uni.navigateBack();
		},
		// 编辑地址
		handleEdit(addressId) {
			uni.navigateTo({
				url: '/pages/addAddress/addAddress?addressId=' + addressId
			});
		},
		// 删除地址
		delAddress(addressId) {
			this.$request.del('/address/delete/' + addressId).then((res) => {
				if (res.code === '200') {
					uni.showToast({
						icon: 'success',
						title: '操作成功'
					});
					this.loadAddress();
				} else {
					uni.showToast({
						icon: 'error',
						title: res.msg
					});
				}
			});
		},
		loadAddress() {
			this.$request.get('/address/selectAll', { userId: this.user.id }).then((res) => {
				this.addressList = res.data || [];
			});
		}
	}
};
</script>

<style></style>
