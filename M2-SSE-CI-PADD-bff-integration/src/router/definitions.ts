const rootRoute = {
  root: {
    path: '/',
  },
}

const appRoutes = {
  command: {
    path: '/command',
  },
  payment: {
    path: '/payment',
  },
  mealDetail: {
    path: '/mealDetail/$mealId',
  },
  panier: {
    path: '/panier',
  },
  stateBoard: {
    path: '/state-board',
  },
  final: {
    path: '/final',
  },
}

const errorRoutes = {
  notFound: {
    path: '/404',
  },
}

export const routes = {
  ...rootRoute,
  ...appRoutes,
  ...errorRoutes,
} as const
