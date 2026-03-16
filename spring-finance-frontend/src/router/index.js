import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "home",
    component: () => import("../views/HomeView.vue"),
  },
  {
    path: "/record",
    name: "record",
    component: () => import("../views/RecordView.vue"),
  },
  {
    path: "/stats",
    name: "stats",
    component: () => import("../views/StatsView.vue"),
  },
  {
    path: "/budget",
    name: "budget",
    component: () => import("../views/BudgetView.vue"),
  },
  {
    path: "/accounts",
    name: "accounts",
    component: () => import("../views/AccountsView.vue"),
  },
  {
    path: "/profile",
    name: "profile",
    component: () => import("../views/ProfileView.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
