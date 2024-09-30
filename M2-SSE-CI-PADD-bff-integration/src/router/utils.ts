import { useAppStore } from '../store/store'

export const init = async () => {
  console.log('running init')
  // @ts-expect-error - getMenu is defined in the store
  return Promise.all([useAppStore.getState().getMenu()])
}
