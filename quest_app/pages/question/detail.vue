<template>
	<view class="page">
		<view v-if="loading" class="loading-box">
			<text class="loading-text">加载中...</text>
		</view>

		<block v-else>
			<scroll-view scroll-y class="scroll-content">
				<view class="question-section">
					<view class="q-title-row">
						<text class="q-status" :class="question.status === 1 ? 'solved' : 'unsolved'">
							{{question.status === 1 ? '已解决' : '待解答'}}
						</text>
						<text class="q-title">{{question.title}}</text>
					</view>

					<view class="q-tags" v-if="question.tags">
						<text class="q-tag" v-for="tag in parseTags(question.tags)" :key="tag">{{tag}}</text>
					</view>

					<view class="q-content">
						<text class="q-content-text">{{question.content}}</text>
					</view>

					<view class="q-meta">
						<view class="q-author">
							<view class="avatar-circle">
								<text class="avatar-letter">{{(question.username || 'U').charAt(0).toUpperCase()}}</text>
							</view>
							<view class="author-detail">
								<text class="author-name">{{question.username || '匿名用户'}}</text>
								<text class="author-time">{{formatTime(question.createTime)}}</text>
							</view>
						</view>
						<view class="q-stats-row">
							<text class="q-stat">{{question.viewCount || 0}} 浏览</text>
							<text class="q-stat">{{question.answerCount || 0}} 回答</text>
						</view>
					</view>

					<view class="q-actions">
						<view class="action-btn" @click="toggleFavorite">
							<text class="action-icon">{{isFavorited ? '&#x2B50;' : '&#x2606;'}}</text>
							<text class="action-text">{{isFavorited ? '已收藏' : '收藏'}}</text>
						</view>
						<view class="action-btn" @click="toggleLike">
							<text class="action-icon">&#x1F44D;</text>
							<text class="action-text">{{question.voteCount || 0}}</text>
						</view>
					</view>
				</view>

				<view class="section-divider"></view>

				<view class="ai-answer-section" v-if="question.aiAnswer">
					<view class="section-header">
						<view class="ai-badge">
							<text class="ai-badge-text">AI</text>
						</view>
						<text class="section-title">AI 回答</text>
					</view>
					<view class="ai-answer-card">
						<text class="ai-answer-text">{{question.aiAnswer}}</text>
					</view>
				</view>

				<view class="answers-section">
					<view class="section-header">
						<text class="section-title">全部回答 ({{answers.length}})</text>
					</view>

					<view v-if="answers.length === 0" class="empty-answers">
						<text class="empty-text">暂无回答，快来抢沙发吧</text>
					</view>

					<view
						class="answer-card"
						v-for="answer in answers" :key="answer.id"
					>
						<view class="answer-header">
							<view class="answer-author">
								<view class="avatar-circle small">
									<text class="avatar-letter">{{(answer.username || 'U').charAt(0).toUpperCase()}}</text>
								</view>
								<view class="author-detail">
									<text class="author-name">{{answer.username || '匿名用户'}}</text>
									<text class="author-time">{{formatTime(answer.createdAt)}}</text>
								</view>
							</view>
							<view class="answer-accepted" v-if="answer.isAccepted === 1">
								<text class="accepted-text">&#x2714; 采纳</text>
							</view>
						</view>
						<view class="answer-content">
							<text class="answer-text">{{answer.content}}</text>
						</view>
						<view class="answer-footer">
							<view class="answer-action" @click="likeAnswer(answer)">
								<text class="answer-action-icon">&#x1F44D;</text>
								<text class="answer-action-text">{{answer.voteCount || 0}}</text>
							</view>
						</view>
					</view>
				</view>

				<view style="height: 160rpx;"></view>
			</scroll-view>

			<view class="bottom-bar">
				<input
					class="answer-input"
					v-model="answerContent"
					placeholder="写下你的回答..."
					placeholder-class="input-placeholder"
					:confirm-type="'send'"
					@confirm="submitAnswer"
				/>
				<view class="send-btn" :class="answerContent.trim() ? 'active' : ''" @click="submitAnswer">
					<text class="send-text">发送</text>
				</view>
			</view>
		</block>
	</view>
</template>

<script>
	import { get, post, del } from '@/utils/request'
	import userStore from '@/store/user'
	
	export default {
		data() {
			return {
				questionId: '',
				question: {},
				answers: [],
				loading: true,
				answerContent: '',
				isFavorited: false,
				submitting: false
			}
		},
		onLoad(options) {
			this.questionId = options.id
			this.loadDetail()
		},
		methods: {
			async loadDetail() {
				this.loading = true
				try {
					const [qRes, aRes] = await Promise.all([
					get('/questions/' + this.questionId),
					get('/answers/question/' + this.questionId)
				])
				this.question = qRes.data
				this.answers = (aRes.data && aRes.data.records) ? aRes.data.records : (Array.isArray(aRes.data) ? aRes.data : [])
				
				try {
					const fRes = await get('/favorites/' + this.questionId + '/status')
					this.isFavorited = fRes.data && fRes.data.isFavorited
				} catch (e) {}
				} catch (e) {
					console.error('加载失败', e)
					uni.showToast({ title: '加载失败', icon: 'none' })
				} finally {
					this.loading = false
				}
			},
			parseTags(tags) {
				if (!tags) return []
				if (typeof tags === 'string') return tags.split(',').filter(Boolean)
				return tags
			},
			async submitAnswer() {
				if (!this.answerContent.trim()) return
				if (!userStore.state.isLogin) {
					uni.navigateTo({ url: '/pages/login/login' })
					return
				}
				if (this.submitting) return
				this.submitting = true
				try {
					await post('/answers', {
						questionId: parseInt(this.questionId),
						content: this.answerContent
					})
					uni.showToast({ title: '回答成功', icon: 'success' })
					this.answerContent = ''
					this.loadDetail()
				} catch (e) {
					console.error('回答失败', e)
				} finally {
					this.submitting = false
				}
			},
			async toggleFavorite() {
				if (!userStore.state.isLogin) {
					uni.navigateTo({ url: '/pages/login/login' })
					return
				}
				try {
					if (this.isFavorited) {
						await del('/favorites/' + this.questionId)
					} else {
						await post('/favorites/' + this.questionId)
					}
					this.isFavorited = !this.isFavorited
				} catch (e) {}
			},
			async toggleLike() {
				if (!userStore.state.isLogin) {
					uni.navigateTo({ url: '/pages/login/login' })
					return
				}
				try {
					await post('/votes', {
						targetType: 1,
						targetId: parseInt(this.questionId),
						voteType: 1
					})
					this.question.voteCount = (this.question.voteCount || 0) + 1
				} catch (e) {}
			},
			async likeAnswer(answer) {
				if (!userStore.state.isLogin) {
					uni.navigateTo({ url: '/pages/login/login' })
					return
				}
				try {
					await post('/votes', {
						targetType: 2,
						targetId: answer.id,
						voteType: 1
					})
					answer.voteCount = (answer.voteCount || 0) + 1
				} catch (e) {}
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
		position: relative;
	}

	.loading-box {
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 200rpx 0;
	}

	.loading-text {
		font-size: 28rpx;
		color: #999;
	}

	.scroll-content {
		height: 100vh;
	}

	.question-section {
		background: #FFFFFF;
		padding: 36rpx 32rpx;
	}

	.q-title-row {
		display: flex;
		align-items: flex-start;
		margin-bottom: 20rpx;
	}

	.q-status {
		font-size: 22rpx;
		padding: 6rpx 16rpx;
		border-radius: 8rpx;
		margin-right: 16rpx;
		flex-shrink: 0;
		margin-top: 6rpx;
	}

	.q-status.solved {
		color: #4CAF50;
		background: rgba(76,175,80,0.1);
	}

	.q-status.unsolved {
		color: #FF9800;
		background: rgba(255,152,0,0.1);
	}

	.q-title {
		font-size: 38rpx;
		font-weight: 700;
		color: #1A1A1A;
		line-height: 1.5;
		flex: 1;
	}

	.q-tags {
		display: flex;
		flex-wrap: wrap;
		margin-bottom: 24rpx;
	}

	.q-tag {
		font-size: 24rpx;
		color: #4A90D9;
		background: rgba(74,144,217,0.08);
		padding: 8rpx 20rpx;
		border-radius: 8rpx;
		margin-right: 14rpx;
		margin-bottom: 10rpx;
	}

	.q-content {
		margin-bottom: 28rpx;
	}

	.q-content-text {
		font-size: 30rpx;
		color: #444;
		line-height: 1.8;
	}

	.q-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 24rpx 0;
		border-top: 1rpx solid #F0F0F0;
	}

	.q-author {
		display: flex;
		align-items: center;
	}

	.avatar-circle {
		width: 64rpx;
		height: 64rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #4A90D9, #357ABD);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 16rpx;
	}

	.avatar-circle.small {
		width: 48rpx;
		height: 48rpx;
	}

	.avatar-letter {
		font-size: 26rpx;
		color: #FFFFFF;
		font-weight: 700;
	}

	.author-detail {
		display: flex;
		flex-direction: column;
	}

	.author-name {
		font-size: 28rpx;
		color: #333;
		font-weight: 600;
	}

	.author-time {
		font-size: 22rpx;
		color: #BBB;
		margin-top: 4rpx;
	}

	.q-stats-row {
		display: flex;
		align-items: center;
	}

	.q-stat {
		font-size: 24rpx;
		color: #999;
		margin-left: 24rpx;
	}

	.q-actions {
		display: flex;
		padding: 20rpx 0 0;
		border-top: 1rpx solid #F0F0F0;
	}

	.action-btn {
		display: flex;
		align-items: center;
		padding: 14rpx 32rpx;
		margin-right: 20rpx;
		border-radius: 24rpx;
		background: #F5F7FA;
	}

	.action-icon {
		font-size: 28rpx;
		margin-right: 8rpx;
	}

	.action-text {
		font-size: 26rpx;
		color: #666;
	}

	.section-divider {
		height: 16rpx;
		background: #F5F7FA;
	}

	.ai-answer-section {
		background: #FFFFFF;
		padding: 32rpx;
	}

	.section-header {
		display: flex;
		align-items: center;
		margin-bottom: 24rpx;
	}

	.ai-badge {
		width: 48rpx;
		height: 48rpx;
		background: linear-gradient(135deg, #9C27B0, #7B1FA2);
		border-radius: 12rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 14rpx;
	}

	.ai-badge-text {
		font-size: 22rpx;
		color: #FFFFFF;
		font-weight: 700;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: 700;
		color: #1A1A1A;
	}

	.ai-answer-card {
		background: linear-gradient(135deg, rgba(156,39,176,0.04), rgba(123,31,162,0.08));
		border-radius: 16rpx;
		padding: 28rpx;
		border-left: 6rpx solid #9C27B0;
	}

	.ai-answer-text {
		font-size: 28rpx;
		color: #444;
		line-height: 1.8;
	}

	.answers-section {
		background: #FFFFFF;
		padding: 32rpx;
	}

	.empty-answers {
		padding: 60rpx 0;
		text-align: center;
	}

	.empty-text {
		font-size: 28rpx;
		color: #BBB;
	}

	.answer-card {
		padding: 28rpx 0;
		border-bottom: 1rpx solid #F5F5F5;
	}

	.answer-card:last-child {
		border-bottom: none;
	}

	.answer-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 20rpx;
	}

	.answer-author {
		display: flex;
		align-items: center;
	}

	.answer-accepted {
		background: rgba(76,175,80,0.1);
		padding: 8rpx 16rpx;
		border-radius: 8rpx;
	}

	.accepted-text {
		font-size: 22rpx;
		color: #4CAF50;
		font-weight: 600;
	}

	.answer-content {
		margin-bottom: 16rpx;
	}

	.answer-text {
		font-size: 28rpx;
		color: #444;
		line-height: 1.8;
	}

	.answer-footer {
		display: flex;
		align-items: center;
	}

	.answer-action {
		display: flex;
		align-items: center;
		padding: 10rpx 20rpx;
		border-radius: 20rpx;
		background: #F5F7FA;
	}

	.answer-action-icon {
		font-size: 24rpx;
		margin-right: 6rpx;
	}

	.answer-action-text {
		font-size: 24rpx;
		color: #999;
	}

	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		align-items: center;
		padding: 16rpx 24rpx;
		padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
		background: #FFFFFF;
		box-shadow: 0 -2rpx 12rpx rgba(0,0,0,0.06);
		z-index: 200;
	}

	.answer-input {
		flex: 1;
		height: 76rpx;
		background: #F5F7FA;
		border-radius: 38rpx;
		padding: 0 28rpx;
		font-size: 28rpx;
		color: #333;
	}

	.input-placeholder {
		color: #CCC;
	}

	.send-btn {
		width: 120rpx;
		height: 76rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-left: 16rpx;
		background: #E0E0E0;
		border-radius: 38rpx;
	}

	.send-btn.active {
		background: linear-gradient(135deg, #4A90D9, #357ABD);
	}

	.send-text {
		font-size: 28rpx;
		color: #FFFFFF;
		font-weight: 600;
	}
</style>
