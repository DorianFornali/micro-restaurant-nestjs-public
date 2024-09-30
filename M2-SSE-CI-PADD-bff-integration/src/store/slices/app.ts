import { StateCreator } from 'zustand'
import { Menu } from '../../types/Menu'
import APP from '../../config/api'
import { transformMenu } from '../../adapters/Menu'

export interface AppSlice {
  menu: Menu
}

export const createAppSlice: StateCreator<AppSlice> = (set) => ({
  menu: {
    STARTER: [],
    MAIN: [],
    DESSERT: [],
    BEVERAGE: [],
    ADDON: [],
  },
  getMenu: () => {
    return new Promise((resolve, reject) => {
      console.log(`${APP.API_GET_MENU}`)
      fetch(`${APP.API_GET_MENU}`)
        .then((res) => res.json())
        .then((data) => {
          const menu = transformMenu(data)
          set({ menu })
          resolve(menu)
        })
        .catch((err) => reject(err))
    })
  },
})
