<template>
	<view class="page">
		<view class="tabs-bar">
			<view 
				class="tab-item" 
				:class="activeType === '' ? 'active' : ''" 
				@click="switchType('')"
			>
				<text>全部</text>
			</view>
			<view 
				class="tab-item" 
				:class="activeType === 'ANSWER' ? 'active' : ''" 
				@click="switchType('ANSWER')"
			>
				<text>回答</text>
			</view>
			<view 
				class="tab-item" 
				:class="activeType === 'FOLLOW' ? 'active' : ''" 
				@click="switchType('FOLLOW')"
			>
				<text>关注</text>
			</view>
			<view 
				class="tab-item" 
				:class="activeType === 'LIKE' ? 'active' : ''" 
				@click="switchType('LIKE')"
			>
				<text>点赞</text>
			</view>
		</view>

		<scroll-view 
			scroll-y 
			class="content"
			@scrolltolower="loadMore"
			refresher-enabled
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
		>
			<view v-if="loading && list.length === 0" class="loading-box">
				<text class="loading-text">加载中...</text>
			</view>

			<view v-else-if="list.length === 0" class="empty-box">
				<text class="empty-icon">&#x1F514;</text>
				<text class="empty-text">暂无消息</text>
				<text class="empty-sub">有新消息时会在这里通知你</text>
			</view>

			<view v-else class="noti-list">
				<view 
					class="noti-card" 
					:class="item.isRead === 0 ? 'unread' : ''"
					v-for="item in list" :key="item.id"
					@click="readNoti(item)"
				>
					<view class="noti-icon-wrap" :class="getTypeClass(item.type)">
						<text class="noti-icon">{{getTypeIcon(item.type)}}</text>
					</view>
					<view class="noti-body">
						<text class="noti-content">{{item.content}}</text>
						<text class="noti-time">{{formatTime(item.createTime)}}</text>
					</view>
					<view class="unread-dot" v-if="item.isRead === 0"></view>
				</view>

				<view class="load-more" v-if="loading">
					<text class="load-more-text">加载更多...</text>
				</view>
				<view class="load-more" v-else-if="noMore">
					<text class="load-more-text">—— 没有更多了 ——</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { get, put } from '@/utils/request'
	import userStore from '@/store/user'
	
	export default {
		data() {
			return {
				activeType: '',
				list: [],
				page: 1,
				pageSize: 20,
				loading: false,
				refreshing: false,
				noMore: false
			}
		},
		onShow() {
			if (!userStore.state.isLogin) {
				uni.navigateTo({ url: '/pages/login/login' })
				return
			}
			this.page = 1
			this.noMore = false
			this.loadData()
		},
		methods: {
			async loadData() {
				if (this.loading) return
				this.loading = true
				try {
					let params = { page: this.page, pageSize: this.pageSize }
					if (this.activeType) params.type = this.activeType
					const res = await get('/notifications', params)
					const records = res.data.list || res.data.records || []
					if (this.page === 1) {
						this.list = records
					} else {
						this.list = [...this.list, ...records]
					}
					if (records.length < this.pageSize) {
						this.noMore = true
					}
				} catch (e) {
					console.error('加载失败', e)
				} finally {
					this.loading = false
					this.refreshing = false
				}
			},
			switchType(type) {
				if (this.activeType === type) return
				this.activeType = type
				this.page = 1
				this.noMore = false
				this.list = []
				this.loadData()
			},
			async onRefresh() {
				this.refreshing = true
				this.page = 1
				this.noMore = false
				await this.loadData()
			},
			loadMore() {
				if (this.noMore || this.loading) return
				this.page++
				this.loadData()
			},
			async readNoti(item) {
				if (item.isRead === 0) {
					try {
						await put('/notifications/' + item.id + '/read')
						item.isRead = 1
					} catch (e) {}
				}
				if (item.targetId) {
					uni.navigateTo({ url: '/pages/question/detail?id=' + item.targetId })
				}
			},
			getTypeClass(type) {
				if (type === 'ANSWER') return 'type-answer'
				if (type === 'FOLLOW') return 'type-follow'
				if (type === 'LIKE') return 'type-like'
				return 'type-system'
			},
			getTypeIcon(type) {
				if (type === 'ANSWER') return '\u{1F4AC}'
				if (type === 'FOLLOW') return '\u{1F465}'
				if (type === 'LIKE') return '\u{1F44D}'
				return '\u{1F514}'
			},
			formatTime(time) {
				if (!time) return ''
				const d = new Date(time)
				const now = new Date()
				const diff = (now - d) / 1000
				if (diff < 60) return '刚刚'
				if (diff < 3600) return Math.floor(diff / 60) + '分钟前'
				if (diff < 86400) return Math.floor(diff / 3600) + '小时前'
				if (diff < 2592000) return Math.floor(diff / 86400) + '天前'
				return d.getFullYear() + '-' + String(d.getMonth()+1).padStart(2,'0') + '-' + String(d.getDate()).padStart(2,'0')
			}
		}
	}
</script>

<style>
	.page {
		min-height: 100vh;
		background: #F5F7FA;
	}

	.tabs-bar {
		display: flex;
		background: #FFFFFF;
		padding: 0 20rpx;
		box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.04);
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.tab-item {
		flex: 1;
		text-align: center;
		padding: 24rpx 0;
		font-size: 28rpx;
		color: #666;
		position: relative;
	}

	.tab-item.active {
		color: #4A90D9;
		font-weight: 600;
	}

	.tab-item.active::after {
		content: '';
		position: absolute;
		bottom: 0;
		left: 50%;
		transform: translateX(-50%);
		width: 48rpx;
		height: 6rpx;
		background: #4A90D9;
		border-radius: 3rpx;
	}

	.content {
		height: calc(100vh - 90rpx);
	}

	.loading-box, .empty-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 160rpx 0;
	}

	.loading-text {
		font-size: 28rpx;
		color: #999;
	}

	.empty-icon {
		font-size: 80rpx;
		margin-bottom: 20rpx;
	}

	.empty-text {
		font-size: 32rpx;
		color: #333;
		font-weight: 600;
		margin-bottom: 10rpx;
	}

	.empty-sub {
		font-size: 26rpx;
		color: #999;
	}

	.noti-list {
		padding: 16rpx 24rpx;
	}

	.noti-card {
		display: flex;
		align-items: center;
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 24rpx;
		margin-bottom: 16rpx;
		box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.03);
		position: relative;
	}

	.noti-card.unread {
		background: #FAFCFF;
		border-left: 6rpx solid #4A90D9;
	}

	.noti-icon-wrap {
		width: 72rpx;
		height: 72rpx;
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
		flex-shrink: 0;
	}

	.type-answer {
		background: rgba(74,144,217,0.1);
	}

	.type-follow {
		background: rgba(76,175,80,0.1);
	}

	.type-like {
		background: rgba(255,152,0,0.1);
	}

	.type-system {
		background: rgba(156,39,176,0.1);
	}

	.noti-icon {
		font-size: 32rpx;
	}

	.noti-body {
		flex: 1;
		margin-right: 16rpx;
	}

	.noti-content {
		font-size: 28rpx;
		color: #333;
		line-height: 1.5;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}

	.noti-time {
		font-size: 22rpx;
		color: #BBB;
		margin-top: 8rpx;
	}

	.unread-dot {
		width: 16rpx;
		height: 16rpx;
		border-radius: 50%;
		background: #F44336;
		flex-shrink: 0;
	}

	.load-more {
		padding: 30rpx 0;
		text-align: center;
	}

	.load-more-text {
		font-size: 24rpx;
		color: #CCC;
	}
</style>
