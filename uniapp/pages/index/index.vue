<template>
  <view>
	  <uni-search-bar @confirm="search" style="width: 200px;"></uni-search-bar>
	  <!-- 广告图片轮播图 -->
    <swiper circular autoplay :interval="3000" :duration="500" indicator-dots style="height: 350rpx;"
            indicator-color="rgba(255, 255, 255, 0.6)" indicator-active-color="#3CB371">
      <swiper-item v-for="item in banners" :key="item.id">
        <navigator :url="'/pages/detail/detail?businessId='+item.businessId">
          <image :src="item.img" alt="" mode="widthFix" style="width: 100%;"/>
        </navigator>
      </swiper-item>
    </swiper>

    <view style="padding: 20rpx;">
      <!-- 公告 -->
      <view class="box">
        <uni-icons type="sound" size="20" style="position: relative; top: 5rpx;"></uni-icons>
        <text style="margin-left: 5rpx;">{{ notice }}</text>
      </view>

      <!-- 商家分类 -->
      <view class="box" style="margin: 20rpx 0;">
        <view style="display: flex;">
          <view class="category-item">
            <image src="@/static/imgs/咖啡.png" mode="widthFix" style="width: 50%;" @click="goBusiness('奶茶饮品')"></image>
            <text>奶茶饮品</text>
          </view>
          <view class="category-item">
            <image src="@/static/imgs/轻食.png" mode="widthFix" style="width: 50%;" @click="goBusiness('轻食简餐')"></image>
            <text>轻食简餐</text>
          </view>
          <view class="category-item">
            <image src="@/static/imgs/汉堡.png" mode="widthFix" style="width: 50%;" @click="goBusiness('炸鸡汉堡')"></image>
            <text>炸鸡汉堡</text>
          </view>
          <view class="category-item">
            <image src="@/static/imgs/美食.png" mode="widthFix" style="width: 50%;" @click="goBusiness('特色美味')"></image>
            <text>特色美味</text>
          </view>
        </view>
      </view>

      <!-- 商家列表 -->
      <view class="box" style="color: orange; font-size: 32rpx; font-weight: bold; margin-bottom: 10rpx;">
        热门商家
      </view>

      <view>
		  <!-- key="item.id"表示根据businessList数组中每个元素的id属性来唯一标识循环生成的每个元素，可以使vue更有效地跟踪每个元素的变化 -->
        <view class="box" v-for="item in businessList" :key="item.id"
              style="display: flex; grid-gap: 30rpx; margin-bottom: 10rpx;" @click="goToDetail(item.id)">
          <view style="width: 30%;">
            <image :src="item.avatar"
                   style="width: 100%; height: 200rpx; border-radius: 10rpx; display: block;"></image>
          </view>
          <view
              style="flex: 1; display: flex; flex-direction: column; justify-content: space-between; grid-gap: 10rpx;">
            <view style="font-size: 36rpx; font-weight: bold;">{{ item.name }}</view>

            <view style="display: flex;  color: #666; ">
              <view style="flex: 1;">
                <text style="color: #ff9800; font-weight: bold;">{{ item.score }}分</text>
                <text style="margin-left: 10rpx;">已售{{ item.nums }}</text>
              </view>
              <view style="flex: 1; text-align: right;">30分钟内送达</text>
              </view>
            </view>

            <view style="color: #ff9800;">免配送费</view>

            <view style="background-color: #ffd281; color: brown; border-radius: 4rpx; width: fit-content;
							padding: 0 8rpx;">{{ item.info || '商家介绍' }}
            </view>

          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      banners: [],
      noticeList: [],
      notice: '',
      businessList: []
    }
  },
  onLoad() {
    this.load()
  },
  methods: {
	  goBusiness(type){
		  uni.navigateTo({
		  	url:'/pages/categoryBusiness/categoryBusiness?type=' + type
		  })
	  },
	search(res) {
	  let value = res.value
	  uni.navigateTo({
	    url: '/pages/search/search?name=' + value
	  })
	},
	  
	goToDetail(businessId){
		  uni.navigateTo({
			  url: '/pages/detail/detail?businessId=' + businessId
		  })
	  },
	  
    load() {
      // 获取轮播图数据
      this.$request.get('/banner/selectAll').then(res => {
        this.banners = res.data || []
      })

      // 获取公告数据并循环展示
      this.$request.get('/notice/selectAll').then(res => {
        this.noticeList = res.data || []
        if (this.noticeList.length) { // 如果noticeList返回的数据不是空，开启循环
          let i = 0
          this.notice = this.noticeList[i++].content // 将noticeList第一个公告内容赋值给notice
          if (this.noticeList.length > 1) { // 如果noticeList中的公告数量大于1，则开启轮播展示,否则不循环
            setInterval(() => { // 使用setInterval函数每隔3秒执行一次轮播逻辑
              if (i === this.noticeList.length) { // 如果当轮播图循环到了最后一个，将循环又从头开始
                i = 0
              }
              this.notice = this.noticeList[i++].content
            }, 3000)
          }
        }
      })

      // 获取商家的列表
      this.$request.get('/business/selectAll', {status: '通过'}).then(res => { // 传入一个对象为{status: '通过'}
        this.businessList = res.data || []
      })
    }
  }
}
</script>

<style>
.category-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  grid-gap: 10rpx;
}

</style>