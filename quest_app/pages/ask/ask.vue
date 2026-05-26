<template>
	<view class="page">
		<view class="form-section">
			<view class="form-card">
				<view class="form-group">
					<text class="form-label">问题标题</text>
					<input 
						class="form-input" 
						v-model="form.title" 
						placeholder="一句话描述你的问题"
						placeholder-class="placeholder"
						maxlength="100"
					/>
				</view>

				<view class="form-group">
					<text class="form-label">问题描述</text>
					<textarea 
						class="form-textarea" 
						v-model="form.content" 
						placeholder="详细描述你的问题，越详细越容易获得精准回答..."
						placeholder-class="placeholder"
						maxlength="5000"
						:auto-height="true"
					/>
				</view>

				<view class="form-group">
					<text class="form-label">选择标签</text>
					<view class="tags-wrap">
						<view 
							class="tag-item" 
							v-for="tag in allTags" :key="tag.id"
							:class="selectedTags.includes(tag.name) ? 'selected' : ''"
							@click="toggleTag(tag.name)"
						>
							<text class="tag-name">{{tag.name}}</text>
						</view>
					</view>
					<view v-if="allTags.length === 0" class="tags-loading">
						<text class="tags-hint">加载标签中...</text>
					</view>
				</view>

				<view class="selected-tags" v-if="selectedTags.length > 0">
					<text class="selected-label">已选标签：</text>
					<view class="selected-tag-list">
						<view class="selected-tag" v-for="tag in selectedTags" :key="tag">
							<text class="selected-tag-text">{{tag}}</text>
							<text class="remove-tag" @click="removeTag(tag)">&#x2716;</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<view class="ai-tip">
			<view class="ai-tip-icon">
				<text class="ai-icon-text">AI</text>
			</view>
			<text class="ai-tip-text">提交后 AI 将自动分析并生成回答建议</text>
		</view>

		<view class="btn-area">
			<button class="btn-submit" :loading="submitting" @click="handleSubmit">
				发布问题
			</button>
			<view class="btn-draft" @click="saveDraft">
				<text class="draft-text">存草稿</text>
			</view>
		</view>
	</view>
</template>

<script>
	import { get, post } from '@/utils/request'
	import userStore from '@/store/user'
	
	export default {
		data() {
			return {
				form: {
					title: '',
					content: ''
				},
				allTags: [],
				selectedTags: [],
				submitting: false
			}
		},
		onLoad() {
			if (!userStore.state.isLogin) {
				uni.navigateTo({ url: '/pages/login/login' })
				return
			}
			this.loadTags()
		},
		methods: {
			async loadTags() {
				try {
					const res = await get('/tags')
					this.allTags = res.data || []
				} catch (e) {
					console.error('加载标签失败', e)
				}
			},
			toggleTag(name) {
				const idx = this.selectedTags.indexOf(name)
				if (idx >= 0) {
					this.selectedTags.splice(idx, 1)
				} else {
					if (this.selectedTags.length >= 5) {
						uni.showToast({ title: '最多选择5个标签', icon: 'none' })
						return
					}
					this.selectedTags.push(name)
				}
			},
			removeTag(name) {
				const idx = this.selectedTags.indexOf(name)
				if (idx >= 0) this.selectedTags.splice(idx, 1)
			},
			async handleSubmit() {
				if (!this.form.title.trim()) {
					uni.showToast({ title: '请输入问题标题', icon: 'none' }); return
				}
				if (!this.form.content.trim()) {
					uni.showToast({ title: '请输入问题描述', icon: 'none' }); return
				}
				if (this.submitting) return
				this.submitting = true
				try {
					await post('/questions', {
						title: this.form.title,
						content: this.form.content,
						tags: this.selectedTags.join(',')
					})
					uni.showToast({ title: '发布成功', icon: 'success' })
					setTimeout(() => {
						uni.switchTab({ url: '/pages/index/index' })
					}, 800)
				} catch (e) {
					console.error('发布失败', e)
				} finally {
					this.submitting = false
				}
			},
			async saveDraft() {
				if (!this.form.title.trim() && !this.form.content.trim()) {
					uni.showToast({ title: '内容为空', icon: 'none' }); return
				}
				try {
					await post('/drafts', {
						title: this.form.title,
						content: this.form.content,
						tags: this.selectedTags.join(',')
					})
					uni.showToast({ title: '已保存草稿', icon: 'success' })
				} catch (e) {
					console.error('保存草稿失败', e)
				}
			}
		}
	}
</script>

<style>
	.page {
		min-height: 100vh;
		background: #F5F7FA;
	}

	.form-section {
		padding: 24rpx;
	}

	.form-card {
		background: #FFFFFF;
		border-radius: 20rpx;
		padding: 36rpx 32rpx;
		box-shadow: 0 4rpx 20rpx rgba(0,0,0,0.04);
	}

	.form-group {
		margin-bottom: 36rpx;
	}

	.form-label {
		font-size: 30rpx;
		color: #333;
		font-weight: 600;
		margin-bottom: 18rpx;
		display: block;
	}

	.form-input {
		height: 88rpx;
		background: #F8F9FA;
		border-radius: 12rpx;
		padding: 0 28rpx;
		font-size: 28rpx;
		color: #333;
		border: 2rpx solid #EEF0F2;
	}

	.form-textarea {
		min-height: 240rpx;
		background: #F8F9FA;
		border-radius: 12rpx;
		padding: 24rpx 28rpx;
		font-size: 28rpx;
		color: #333;
		border: 2rpx solid #EEF0F2;
		width: 100%;
		box-sizing: border-box;
		line-height: 1.7;
	}

	.placeholder {
		color: #CCC;
	}

	.tags-wrap {
		display: flex;
		flex-wrap: wrap;
	}

	.tag-item {
		padding: 14rpx 28rpx;
		margin-right: 16rpx;
		margin-bottom: 16rpx;
		border-radius: 24rpx;
		background: #F5F7FA;
		border: 2rpx solid #E8EAED;
		transition: all 0.2s;
	}

	.tag-item.selected {
		background: rgba(74,144,217,0.1);
		border-color: #4A90D9;
	}

	.tag-name {
		font-size: 26rpx;
		color: #666;
	}

	.tag-item.selected .tag-name {
		color: #4A90D9;
		font-weight: 600;
	}

	.tags-loading {
		padding: 20rpx 0;
	}

	.tags-hint {
		font-size: 26rpx;
		color: #BBB;
	}

	.selected-tags {
		display: flex;
		align-items: flex-start;
		padding: 20rpx 0;
		border-top: 1rpx solid #F0F0F0;
	}

	.selected-label {
		font-size: 26rpx;
		color: #666;
		margin-right: 12rpx;
		flex-shrink: 0;
		padding-top: 8rpx;
	}

	.selected-tag-list {
		display: flex;
		flex-wrap: wrap;
	}

	.selected-tag {
		display: flex;
		align-items: center;
		padding: 8rpx 16rpx;
		background: rgba(74,144,217,0.1);
		border-radius: 20rpx;
		margin-right: 10rpx;
		margin-bottom: 10rpx;
	}

	.selected-tag-text {
		font-size: 24rpx;
		color: #4A90D9;
		margin-right: 8rpx;
	}

	.remove-tag {
		font-size: 20rpx;
		color: #4A90D9;
	}

	.ai-tip {
		display: flex;
		align-items: center;
		margin: 0 24rpx 24rpx;
		padding: 20rpx 24rpx;
		background: linear-gradient(135deg, rgba(156,39,176,0.06), rgba(123,31,162,0.04));
		border-radius: 12rpx;
	}

	.ai-tip-icon {
		width: 40rpx;
		height: 40rpx;
		background: linear-gradient(135deg, #9C27B0, #7B1FA2);
		border-radius: 10rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 14rpx;
	}

	.ai-icon-text {
		font-size: 18rpx;
		color: #FFFFFF;
		font-weight: 700;
	}

	.ai-tip-text {
		font-size: 24rpx;
		color: #9C27B0;
	}

	.btn-area {
		padding: 0 24rpx 40rpx;
	}

	.btn-submit {
		width: 100%;
		height: 96rpx;
		background: linear-gradient(135deg, #4A90D9 0%, #357ABD 100%);
		color: #FFFFFF;
		font-size: 34rpx;
		font-weight: 600;
		border-radius: 16rpx;
		border: none;
		letter-spacing: 4rpx;
		box-shadow: 0 8rpx 24rpx rgba(74,144,217,0.3);
	}

	.btn-submit::after {
		border: none;
	}

	.btn-draft {
		text-align: center;
		padding: 24rpx 0;
	}

	.draft-text {
		font-size: 28rpx;
		color: #999;
	}
</style>
