<template>
	<view class="page">
		<view class="header-area">
			<text class="page-title">创建账号</text>
			<text class="page-desc">加入 DevQuest 开发者社区</text>
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
				<text class="form-label">邮箱</text>
				<input 
					class="form-input" 
					v-model="form.email" 
					placeholder="请输入邮箱"
					placeholder-class="placeholder"
				/>
			</view>
			<view class="form-group">
				<text class="form-label">密码</text>
				<input 
					class="form-input" 
					v-model="form.password" 
					placeholder="请输入密码（至少6位）"
					placeholder-class="placeholder"
					:password="true"
				/>
			</view>
			<view class="form-group">
				<text class="form-label">确认密码</text>
				<input 
					class="form-input" 
					v-model="form.confirmPassword" 
					placeholder="请再次输入密码"
					placeholder-class="placeholder"
					:password="true"
				/>
			</view>

			<button class="btn-register" :loading="submitting" @click="handleRegister">
				注 册
			</button>

			<view class="form-footer">
				<text class="link-text" @click="goLogin">已有账号？返回登录</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { register } from '@/store/user'
	
	export default {
		data() {
			return {
				form: {
					username: '',
					email: '',
					password: '',
					confirmPassword: ''
				},
				submitting: false
			}
		},
		methods: {
			async handleRegister() {
				if (!this.form.username.trim()) {
					uni.showToast({ title: '请输入用户名', icon: 'none' }); return
				}
				if (!this.form.email.trim()) {
					uni.showToast({ title: '请输入邮箱', icon: 'none' }); return
				}
				if (!this.form.password.trim() || this.form.password.length < 6) {
					uni.showToast({ title: '密码至少6位', icon: 'none' }); return
				}
				if (this.form.password !== this.form.confirmPassword) {
					uni.showToast({ title: '两次密码不一致', icon: 'none' }); return
				}
				this.submitting = true
				try {
					await register({
						username: this.form.username,
						email: this.form.email,
						password: this.form.password
					})
					uni.showToast({ title: '注册成功', icon: 'success' })
					setTimeout(() => {
						uni.switchTab({ url: '/pages/index/index' })
					}, 800)
				} catch (e) {
					console.error('注册失败', e)
				} finally {
					this.submitting = false
				}
			},
			goLogin() {
				uni.navigateBack()
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
		padding: 80rpx 40rpx 60rpx;
		border-radius: 0 0 60rpx 60rpx;
	}

	.page-title {
		font-size: 44rpx;
		font-weight: 700;
		color: #FFFFFF;
		display: block;
		margin-bottom: 12rpx;
	}

	.page-desc {
		font-size: 26rpx;
		color: rgba(255,255,255,0.8);
	}

	.form-card {
		margin: -30rpx 40rpx 0;
		background: #FFFFFF;
		border-radius: 24rpx;
		padding: 50rpx 40rpx;
		box-shadow: 0 8rpx 40rpx rgba(0,0,0,0.08);
	}

	.form-group {
		margin-bottom: 32rpx;
	}

	.form-label {
		font-size: 28rpx;
		color: #333;
		font-weight: 600;
		margin-bottom: 14rpx;
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
	}

	.placeholder {
		color: #BBB;
	}

	.btn-register {
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

	.btn-register::after {
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
