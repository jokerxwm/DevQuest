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
        path: 'ask',
        name: 'AskQuestion',
        component: () => import('@/views/AskQuestion.vue'),
        meta: { requiresAuth: true }
      },
      {
        path: 'user/:id',
        name: 'UserProfile',
        component: () => import('@/views/UserProfile.vue')
      },
      {
        path: 'ai',
        name: 'AiChat',
        component: () => import('@/views/AiChat.vue'),
        meta: { requiresAuth: true }
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
