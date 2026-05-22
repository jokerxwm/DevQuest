import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/Home.vue')
      },
      {
        path: 'questions',
        name: 'QuestionList',
        component: () => import('@/views/QuestionList.vue')
      },
      {
        path: 'questions/:id',
        name: 'QuestionDetail',
        component: () => import('@/views/QuestionDetail.vue')
      },
      {
        path: 'questions/:id/edit',
        name: 'EditQuestion',
        component: () => import('@/views/EditQuestion.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'questions/:id/answer',
        name: 'AnswerEditor',
        component: () => import('@/views/AnswerEditor.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'ask',
        name: 'AskQuestion',
        component: () => import('@/views/AskQuestion.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'hot',
        name: 'HotQuestions',
        component: () => import('@/views/HotQuestions.vue')
      },
      {
        path: 'tags',
        name: 'Tags',
        component: () => import('@/views/Tags.vue')
      },
      {
        path: 'tags/:id',
        name: 'TagQuestions',
        component: () => import('@/views/TagQuestions.vue')
      },
      {
        path: 'search',
        name: 'SearchResults',
        component: () => import('@/views/SearchResults.vue')
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: () => import('@/views/UserProfile.vue')
      },
      {
        path: 'settings',
        name: 'UserSettings',
        component: () => import('@/views/UserSettings.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/Favorites.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'drafts',
        name: 'Drafts',
        component: () => import('@/views/Drafts.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'messages',
        name: 'MessageList',
        component: () => import('@/views/MessageList.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'messages/new',
        name: 'NewMessage',
        component: () => import('@/views/NewMessage.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'messages/:userId',
        name: 'MessageConversation',
        component: () => import('@/views/MessageConversation.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'ai',
        name: 'AiChat',
        component: () => import('@/views/AiChat.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'ai/daily',
        name: 'AiDaily',
        component: () => import('@/views/AiDaily.vue')
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/Notifications.vue'),
        meta: { requiresAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
