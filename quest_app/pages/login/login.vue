<template>
	<view class="page">
		<view class="header-area">
			<view class="logo-wrap">
				<text class="logo-icon">DQ</text>
			</view>
			<text class="app-name">DevQuest</text>
			<text class="app-desc">AI 驱动的开发者技术问答社区</text>
		</view>

		<view class="form-card">
			<view class="form-group">
				<text class="form-label">用户名</text>
				<input 
					class="form-input" 
					v-model="form.username" 
					placeholder="请输入用户名"
					placeholder-class="placeholder"
				/>
			</view>
			<view class="form-group">
				<text class="form-label">密码</text>
				<input 
					class="form-input" 
					v-model="form.password" 
					placeholder="请输入密码"
					placeholder-class="placeholder"
					:password="true"
				/>
			</view>

			<button class="btn-login" :loading="submitting" @click="handleLogin">
				登 录
			</button>

			<view class="form-footer">
				<text class="link-text" @click="goRegister">还没有账号？立即注册</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { login } from '@/store/user'
	
	export default {
		data() {
			return {
				form: {
					username: '',
					password: ''
				},
				submitting: false
			}
		},
		methods: {
			async handleLogin() {
				if (!this.form.username.trim()) {
					uni.showToast({ title: '请输入用户名', icon: 'none' })
					return
				}
				if (!this.form.password.trim()) {
					uni.showToast({ title: '请输入密码', icon: 'none' })
					return
				}
				this.submitting = true
				try {
					await login(this.form.username, this.form.password)
					uni.showToast({ title: '登录成功', icon: 'success' })
					setTimeout(() => {
						uni.switchTab({ url: '/pages/index/index' })
					}, 800)
				} catch (e) {
					console.error('登录失败', e)
				} finally {
					this.submitting = false
				}
			},
			goRegister() {
				uni.navigateTo({ url: '/pages/register/register' })
			}
		}
	}
</script>

<style>
	.page {
		min-height: 100vh;
		background: #F5F7FA;
	}

	.header-area {
		background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
		padding: 100rpx 0 80rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		border-radius: 0 0 60rpx 60rpx;
	}

	.logo-wrap {
		width: 120rpx;
		height: 120rpx;
		border-radius: 30rpx;
		background: rgba(255,255,255,0.2);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 24rpx;
		backdrop-filter: blur(10px);
	}

	.logo-icon {
		font-size: 48rpx;
		font-weight: 800;
		color: #FFFFFF;
		letter-spacing: 2rpx;
	}

	.app-name {
		font-size: 44rpx;
		font-weight: 700;
		color: #FFFFFF;
		margin-bottom: 12rpx;
	}

	.app-desc {
		font-size: 24rpx;
		color: rgba(255,255,255,0.8);
	}

	.form-card {
		margin: -40rpx 40rpx 0;
		background: #FFFFFF;
		border-radius: 24rpx;
		padding: 50rpx 40rpx;
		box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.08);
	}

	.form-group {
		margin-bottom: 36rpx;
	}

	.form-label {
		font-size: 28rpx;
		color: #333;
		font-weight: 600;
		margin-bottom: 16rpx;
		display: block;
	}

	.form-input {
		height: 88rpx;
		background: #F5F7FA;
		border-radius: 12rpx;
		padding: 0 28rpx;
		font-size: 28rpx;
		color: #333;
		border: 2rpx solid transparent;
		transition: border-color 0.2s;
	}

	.placeholder {
		color: #BBB;
	}

	.btn-login {
		width: 100%;
		height: 88rpx;
		background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
		color: #FFFFFF;
		font-size: 32rpx;
		font-weight: 600;
		border-radius: 12rpx;
		border: none;
		margin-top: 20rpx;
		letter-spacing: 4rpx;
	}

	.btn-login::after {
		border: none;
	}

	.form-footer {
		text-align: center;
		margin-top: 36rpx;
	}

	.link-text {
		font-size: 26rpx;
		color: #4A90D9;
	}
</style>
