<template>
	<view style="padding: 20rpx">
		<view class="box">
			<uni-forms :modelValue="form" :rules="rules" ref="formRef" label-width="160rpx" label-align="right">
				<uni-forms-item label="地址信息" name="address" required>
					<uni-easyinput type="textarea" v-model="form.address" placeholder="请输入地址" />
				</uni-forms-item>
				<uni-forms-item label="联系人" name="user" required>
					<uni-easyinput type="text" v-model="form.user" placeholder="请输入联系人" />
				</uni-forms-item>
				<uni-forms-item label="联系方式" name="phone" required>
					<uni-easyinput type="text" v-model="form.phone" placeholder="请输入联系方式" />
				</uni-forms-item>

				<uni-forms-item>
					<button type="primary" size="mini" @click="saveAddress">保 存</button>
				</uni-forms-item>
			</uni-forms>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			form: {},
			user: uni.getStorageSync('xm-user'),
			rules: {
				address: {
					rules: [
						{
							required: true,
							errorMessage: '请填写收货地址'
						}
					]
				},
				user: {
					rules: [
						{
							required: true,
							errorMessage: '请填写收货人'
						}
					]
				},
				phone: {
					rules: [
						{
							required: true,
							errorMessage: '请填写联系方式'
						}
					]
				}
			}
		};
	},
	onLoad(option) {
		// 拿到地址页面传来的addressId
		let addressId = option.addressId || 0;

		// 当修改地址时会传入addressId，需要请求/address/selectById/返回需要修改的地址信息到页面上，如果为新增则不需要这一步
		if (!addressId == 0) {
			// 请求接口拿到表单数据
			this.$request.get('/address/selectById/' + addressId).then((res) => {
				this.form = res.data || [];
			});
		}
	},
	methods: {
		// 保存存在两种情况:1.新增保存调用post接口 2.修改保存调用put接口
		saveAddress() {
			// 验证表单验证
			this.$refs.formRef
				.validate()
				.then((res) => {
					this.$request
						.request({
							method: this.form.id ? 'PUT' : 'POST',
							url: this.form.id ? '/address/update' : '/address/add',
							data: this.form
						})
						.then((res) => {
							if (res.code === '200') {
								uni.showToast({
									title: '操作成功'
								});
								// 地址新增成功后返回到刚才的页面
								uni.navigateBack();
							} else {
								uni.showToast({
									icon: 'error',
									title: res.msg
								});
							}
						});
				})
				.catch((err) => {
					console.log('err', err);
				});
		}
	}
};
</script>

<style></style>
