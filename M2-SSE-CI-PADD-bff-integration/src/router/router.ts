import {
  createRootRoute,
  createRoute,
  createRouter,
} from '@tanstack/react-router'

import RootLayout from '../layouts/RootLayout'
import NotFoundPage from '../pages/404'
import HomePage from '../pages/Home'
import CommandPage from '../pages/Command/Command'
import PaymentPage from '../pages/Payment/Payment'
import MealDetail from '../pages/MealDetail'
import Panier from '../pages/Panier'
import FinalPage from '../pages/Final'
import StateBoardPage from '../pages/StateBoard/StateBoard'

import { routes } from './definitions'
import { init } from './utils'
import { mealDetailQuery } from './postQueries'

export const rootRoute = createRootRoute({
  component: RootLayout,
  loader: init,
  staleTime: 1000 * 60 * 60, // When user navigates into the app, we will refetch the data every hour
})

export const notFoundRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.notFound.path,
  component: NotFoundPage,
})

const indexRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.root.path,
  component: HomePage,
})

const commandRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.command.path,
  component: CommandPage,
})

const stateBoardRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.stateBoard.path,
  component: StateBoardPage,
})

const paymentRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.payment.path,
  component: PaymentPage,
})

const mealDetailRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.mealDetail.path,
  component: MealDetail,
  loader: ({ params: { mealId } }: { params: { mealId: string } }) =>
    mealDetailQuery(mealId),
})

const panierRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.panier.path,
  component: Panier,
})

const finalRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: routes.final.path,
  component: FinalPage,
})

const routeTree = rootRoute.addChildren([
  indexRoute,
  commandRoute,
  mealDetailRoute,
  panierRoute,
  paymentRoute,
  stateBoardRoute,
  finalRoute,
  notFoundRoute,
])

export const router = createRouter({ routeTree })

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}
