<template>
	<view class="page">
		<view v-if="!isLogin" class="not-login">
			<view class="login-card">
				<view class="login-logo">
					<text class="logo-icon">DQ</text>
				</view>
				<text class="login-title">登录 DevQuest</text>
				<text class="login-sub">解锁更多功能，参与技术讨论</text>
				<button class="btn-login" @click="goLogin">立即登录</button>
				<view class="register-link" @click="goRegister">
					<text class="register-text">没有账号？注册</text>
				</view>
			</view>
		</view>

		<block v-else>
			<view class="profile-header">
				<view class="user-info">
					<view class="avatar-lg">
						<text class="avatar-letter">{{(user.username || 'U').charAt(0).toUpperCase()}}</text>
					</view>
					<view class="user-detail">
						<text class="username">{{user.username}}</text>
						<text class="user-bio">{{user.bio || '这个人很懒，什么都没写~'}}</text>
					</view>
				</view>
				<view class="stats-row">
					<view class="stat-box">
						<text class="stat-num">{{stats.questionCount || 0}}</text>
						<text class="stat-label">提问</text>
					</view>
					<view class="stat-divider"></view>
					<view class="stat-box">
						<text class="stat-num">{{stats.answerCount || 0}}</text>
						<text class="stat-label">回答</text>
					</view>
					<view class="stat-divider"></view>
					<view class="stat-box">
						<text class="stat-num">{{stats.likeCount || 0}}</text>
						<text class="stat-label">获赞</text>
					</view>
					<view class="stat-divider"></view>
					<view class="stat-box">
						<text class="stat-num">{{stats.followersCount || 0}}</text>
						<text class="stat-label">粉丝</text>
					</view>
				</view>
			</view>

			<view class="menu-section">
				<view class="menu-card">
					<view class="menu-item" @click="goMyQuestions">
						<text class="menu-icon">&#x1F4CB;</text>
						<text class="menu-text">我的问题</text>
						<text class="menu-arrow">&#x203A;</text>
					</view>
					<view class="menu-item" @click="goMyAnswers">
						<text class="menu-icon">&#x1F4AC;</text>
						<text class="menu-text">我的回答</text>
						<text class="menu-arrow">&#x203A;</text>
					</view>
					<view class="menu-item" @click="goFavorites">
						<text class="menu-icon">&#x2B50;</text>
						<text class="menu-text">我的收藏</text>
						<text class="menu-arrow">&#x203A;</text>
					</view>
					<view class="menu-item" @click="goDrafts">
						<text class="menu-icon">&#x1F4DD;</text>
						<text class="menu-text">草稿箱</text>
						<text class="menu-arrow">&#x203A;</text>
					</view>
				</view>

				<view class="menu-card">
					<view class="menu-item" @click="goNotifications">
						<text class="menu-icon">&#x1F514;</text>
						<text class="menu-text">消息通知</text>
						<view class="badge" v-if="unreadCount > 0">
							<text class="badge-text">{{unreadCount > 99 ? '99+' : unreadCount}}</text>
						</view>
						<text class="menu-arrow">&#x203A;</text>
					</view>
				</view>

				<view class="menu-card">
					<view class="menu-item logout" @click="handleLogout">
						<text class="menu-icon">&#x1F6AA;</text>
						<text class="menu-text logout-text">退出登录</text>
					</view>
				</view>
			</view>
		</block>
	</view>
</template>

<script>
	import { get } from '@/utils/request'
	import userStore from '@/store/user'
	
	export default {
		data() {
			return {
				isLogin: false,
				user: {},
				stats: {},
				unreadCount: 0
			}
		},
		onShow() {
			this.isLogin = userStore.state.isLogin
			if (this.isLogin) {
				this.user = userStore.state.user || {}
				this.loadStats()
				this.loadUnread()
			}
		},
		methods: {
			async loadStats() {
				try {
					const res = await get('/users/' + this.user.id + '/stats')
					this.stats = res.data
				} catch (e) {}
			},
			async loadUnread() {
				try {
					const res = await get('/notifications/unread-count')
					this.unreadCount = res.data || 0
				} catch (e) {}
			},
			goLogin() {
				uni.navigateTo({ url: '/pages/login/login' })
			},
			goRegister() {
				uni.navigateTo({ url: '/pages/register/register' })
			},
			goMyQuestions() {
				uni.showToast({ title: '我的问题', icon: 'none' })
			},
			goMyAnswers() {
				uni.showToast({ title: '我的回答', icon: 'none' })
			},
			goFavorites() {
				uni.showToast({ title: '我的收藏', icon: 'none' })
			},
			goDrafts() {
				uni.showToast({ title: '草稿箱', icon: 'none' })
			},
			goNotifications() {
				uni.switchTab({ url: '/pages/notifications/notifications' })
			},
			handleLogout() {
				uni.showModal({
					title: '提示',
					content: '确定退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							userStore.logout()
							this.isLogin = false
							this.user = {}
							this.stats = {}
							uni.showToast({ title: '已退出', icon: 'success' })
						}
					}
				})
			}
		}
	}
</script>

<style>
	.page {
		min-height: 100vh;
		background: #F5F7FA;
	}

	.not-login {
		display: flex;
		align-items: center;
		justify-content: center;
		min-height: 100vh;
		padding: 40rpx;
	}

	.login-card {
		background: #FFFFFF;
		border-radius: 24rpx;
		padding: 60rpx 40rpx;
		width: 100%;
		text-align: center;
		box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.06);
	}

	.login-logo {
		width: 120rpx;
		height: 120rpx;
		border-radius: 30rpx;
		background: linear-gradient(135deg, #4A90D9, #357ABD);
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0 auto 30rpx;
	}

	.logo-icon {
		font-size: 48rpx;
		font-weight: 800;
		color: #FFFFFF;
	}

	.login-title {
		font-size: 38rpx;
		font-weight: 700;
		color: #1A1A1A;
		display: block;
		margin-bottom: 12rpx;
	}

	.login-sub {
		font-size: 26rpx;
		color: #999;
		display: block;
		margin-bottom: 40rpx;
	}

	.btn-login {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #4A90D9, #357ABD);
		color: #FFFFFF;
		font-size: 32rpx;
		font-weight: 600;
		border-radius: 12rpx;
		border: none;
		margin-bottom: 24rpx;
	}

	.btn-login::after {
		border: none;
	}

	.register-link {
		padding: 10rpx 0;
	}

	.register-text {
		font-size: 26rpx;
		color: #4A90D9;
	}

	.profile-header {
		background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
		padding: 60rpx 32rpx 40rpx;
		border-radius: 0 0 40rpx 40rpx;
	}

	.user-info {
		display: flex;
		align-items: center;
		margin-bottom: 36rpx;
	}

	.avatar-lg {
		width: 120rpx;
		height: 120rpx;
		border-radius: 50%;
		background: rgba(255,255,255,0.2);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 28rpx;
		border: 4rpx solid rgba(255,255,255,0.3);
	}

	.avatar-letter {
		font-size: 48rpx;
		color: #FFFFFF;
		font-weight: 700;
	}

	.user-detail {
		flex: 1;
	}

	.username {
		font-size: 36rpx;
		font-weight: 700;
		color: #FFFFFF;
		display: block;
		margin-bottom: 8rpx;
	}

	.user-bio {
		font-size: 24rpx;
		color: rgba(255,255,255,0.8);
	}

	.stats-row {
		display: flex;
		background: rgba(255,255,255,0.15);
		border-radius: 16rpx;
		padding: 24rpx 0;
		backdrop-filter: blur(10px);
	}

	.stat-box {
		flex: 1;
		text-align: center;
	}

	.stat-num {
		font-size: 36rpx;
		font-weight: 700;
		color: #FFFFFF;
		display: block;
	}

	.stat-label {
		font-size: 22rpx;
		color: rgba(255,255,255,0.8);
		margin-top: 4rpx;
	}

	.stat-divider {
		width: 1rpx;
		background: rgba(255,255,255,0.2);
		margin: 8rpx 0;
	}

	.menu-section {
		padding: 24rpx;
	}

	.menu-card {
		background: #FFFFFF;
		border-radius: 16rpx;
		margin-bottom: 20rpx;
		overflow: hidden;
		box-shadow: 0 2rpx 12rpx rgba(0,0,0,0.03);
	}

	.menu-item {
		display: flex;
		align-items: center;
		padding: 28rpx 32rpx;
		border-bottom: 1rpx solid #F5F5F5;
	}

	.menu-item:last-child {
		border-bottom: none;
	}

	.menu-icon {
		font-size: 36rpx;
		margin-right: 20rpx;
	}

	.menu-text {
		flex: 1;
		font-size: 30rpx;
		color: #333;
	}

	.menu-arrow {
		font-size: 36rpx;
		color: #CCC;
	}

	.badge {
		background: #F44336;
		border-radius: 20rpx;
		padding: 2rpx 14rpx;
		margin-right: 12rpx;
	}

	.badge-text {
		font-size: 20rpx;
		color: #FFFFFF;
	}

	.logout .menu-text {
		color: #F44336;
	}
</style>
