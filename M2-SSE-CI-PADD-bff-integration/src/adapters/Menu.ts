import { Menu, MenuItem } from '../types/Menu'

export const transformMenu = (menu: MenuItem[]): Menu => {
  const transformedMenu: Menu = {
    STARTER: [],
    MAIN: [],
    DESSERT: [],
    BEVERAGE: [],
    ADDON: [],
  }

  menu.forEach((item) => {
    transformedMenu[item.category].push(item)
    transformedMenu.ADDON.push(item)
  })

  return transformedMenu
}
