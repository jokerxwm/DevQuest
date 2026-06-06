<template>
	<view class="page">
		<view class="header">
			<view class="header-top">
				<text class="logo-text">DevQuest</text>
				<view class="search-box" @click="goSearch">
					<text class="search-icon">&#x1F50D;</text>
					<text class="search-placeholder">搜索问题、技术...</text>
				</view>
			</view>
			<scroll-view scroll-x class="tab-scroll">
				<view class="tab-list">
					<view 
						v-for="tab in tabs" :key="tab.value"
						:class="['tab-item', activeTab === tab.value ? 'active' : '']"
						@click="switchTab(tab.value)"
					>
						<text>{{tab.label}}</text>
					</view>
				</view>
			</scroll-view>
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
				<text class="empty-icon">&#x1F4AD;</text>
				<text class="empty-text">暂无问题</text>
				<text class="empty-sub">快来提第一个问题吧</text>
			</view>

			<view v-else class="question-list">
				<view 
					class="question-card" 
					v-for="item in list" :key="item.id"
					@click="goDetail(item.id)"
				>
					<view class="card-header">
						<view class="title-row">
							<text class="question-title">{{item.title}}</text>
						</view>
						<text class="question-desc">{{item.content ? item.content.substring(0, 100) : ''}}</text>
					</view>
					<view class="card-tags" v-if="item.tags && item.tags.length > 0">
						<text 
							class="tag" 
							v-for="tag in (typeof item.tags === 'string' ? item.tags.split(',').slice(0,3) : item.tags.slice(0,3))" 
							:key="tag"
						>{{tag}}</text>
					</view>
					<view class="card-footer">
						<view class="author-info">
							<view class="avatar-small">
								<text class="avatar-text">{{(item.username || 'U').charAt(0).toUpperCase()}}</text>
							</view>
							<text class="author-name">{{item.username || '匿名用户'}}</text>
						</view>
						<view class="stats">
							<text class="stat-item">{{item.viewCount || 0}} 浏览</text>
							<text class="stat-dot">·</text>
							<text class="stat-item">{{item.answerCount || 0}} 回答</text>
							<text class="stat-dot">·</text>
							<text class="stat-item">{{formatTime(item.createTime)}}</text>
						</view>
					</view>
				</view>

				<view class="load-more" v-if="loading">
					<text class="load-more-text">加载更多...</text>
				</view>
				<view class="load-more" v-else-if="noMore && list.length > 0">
					<text class="load-more-text">—— 没有更多了 ——</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import { get } from '@/utils/request'
	
	export default {
		data() {
			return {
				tabs: [
					{ label: '最新', value: 'latest' },
					{ label: '热门', value: 'hot' },
					{ label: '未回答', value: 'unanswered' },
					{ label: 'AI', value: 'AI' },
					{ label: 'Java', value: 'Java' },
					{ label: 'Vue.js', value: 'Vue.js' },
					{ label: 'Python', value: 'Python' }
				],
				activeTab: 'latest',
				list: [],
				page: 1,
				pageSize: 10,
				loading: false,
				refreshing: false,
				noMore: false
			}
		},
		onLoad() {
			this.loadData()
		},
		methods: {
			async loadData() {
				if (this.loading) return
				this.loading = true
				try {
					let url = '/questions/list'
					let params = { page: this.page, pageSize: this.pageSize }
					
					if (this.activeTab === 'hot') {
						url = '/questions/hot'
					} else if (this.activeTab === 'unanswered') {
						params.status = 0
					} else if (this.activeTab !== 'latest') {
						params.keyword = this.activeTab
					}
					
					const res = await get(url, params)
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
			switchTab(tab) {
				if (this.activeTab === tab) return
				this.activeTab = tab
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
			goDetail(id) {
				uni.navigateTo({ url: '/pages/question/detail?id=' + id })
			},
			goSearch() {
				// placeholder
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

	.header {
		background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
		padding: 20rpx 30rpx 0;
		position: sticky;
		top: 0;
		z-index: 100;
	}

	.header-top {
		display: flex;
		align-items: center;
		padding: 10rpx 0 20rpx;
	}

	.logo-text {
		font-size: 36rpx;
		font-weight: 700;
		color: #FFFFFF;
		margin-right: 20rpx;
		letter-spacing: 1rpx;
	}

	.search-box {
		flex: 1;
		display: flex;
		align-items: center;
		background: rgba(255,255,255,0.2);
		border-radius: 32rpx;
		padding: 14rpx 24rpx;
	}

	.search-icon {
		font-size: 28rpx;
		margin-right: 10rpx;
	}

	.search-placeholder {
		font-size: 26rpx;
		color: rgba(255,255,255,0.8);
	}

	.tab-scroll {
		white-space: nowrap;
	}

	.tab-list {
		display: flex;
		padding: 16rpx 0;
	}

	.tab-item {
		display: inline-flex;
		padding: 10rpx 28rpx;
		margin-right: 16rpx;
		border-radius: 28rpx;
		font-size: 26rpx;
		color: rgba(255,255,255,0.8);
		background: rgba(255,255,255,0.1);
		flex-shrink: 0;
	}

	.tab-item.active {
		background: #FFFFFF;
		color: #4A90D9;
		font-weight: 600;
	}

	.content {
		height: calc(100vh - 200rpx);
	}

	.loading-box, .empty-box {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 120rpx 0;
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

	.question-list {
		padding: 20rpx 24rpx;
	}

	.question-card {
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 28rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.04);
	}

	.card-header {
		margin-bottom: 16rpx;
	}

	.title-row {
		margin-bottom: 12rpx;
	}

	.question-title {
		font-size: 32rpx;
		font-weight: 600;
		color: #1A1A1A;
		line-height: 1.5;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}

	.question-desc {
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}

	.card-tags {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 16rpx;
	}

	.tag {
		font-size: 22rpx;
		color: #4A90D9;
		background: rgba(74,144,217,0.08);
		padding: 6rpx 16rpx;
		border-radius: 6rpx;
		margin-right: 12rpx;
		margin-bottom: 8rpx;
	}

	.card-footer {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.author-info {
		display: flex;
		align-items: center;
	}

	.avatar-small {
		width: 40rpx;
		height: 40rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #4A90D9, #357ABD);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 10rpx;
	}

	.avatar-text {
		font-size: 20rpx;
		color: #FFFFFF;
		font-weight: 600;
	}

	.author-name {
		font-size: 24rpx;
		color: #666;
	}

	.stats {
		display: flex;
		align-items: center;
	}

	.stat-item {
		font-size: 22rpx;
		color: #999;
	}

	.stat-dot {
		font-size: 22rpx;
		color: #CCC;
		margin: 0 6rpx;
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
