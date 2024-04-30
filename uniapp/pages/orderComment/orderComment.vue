<template>
	<view style="padding: 20rpx">
		<view class="box">
			<uni-forms :modelValue="form" :rules="rules" ref="formRef" label-width="140rpx" label-align="right">
				<uni-forms-item label="备注" name="comment">
					<uni-easyinput type="textarea" v-model="comment" placeholder="请输入" />
				</uni-forms-item>

				<uni-forms-item>
					<button type="primary" size="mini" @click="save">确 定</button>
				</uni-forms-item>
			</uni-forms>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			comment: ''
		};
	},
	onShow() {
		let xmOrders = uni.getStorageSync('xm-orders') || {}; // 首先定义一个变量获取缓存里的xmOrders对象
		this.comment = xmOrders.comment; // 界面显示历史备注
	},
	methods: {
		save() {
			let xmOrders = uni.getStorageSync('xm-orders') || {}; // 首先定义一个变量获取缓存里的xmOrders对象
			xmOrders.comment = this.comment; // 将comment赋值到变量内
			uni.setStorageSync('xm-orders', xmOrders); // 再将变量赋值给缓存 这样做的目的是比直接对缓存的数据存取更加安全，不会照成缓存数据的丢失（相比于uni.setStorageSync('xm-orders', {addressId: this.addressId})）
			uni.navigateBack();
		}
	}
};
</script>

<style></style>
