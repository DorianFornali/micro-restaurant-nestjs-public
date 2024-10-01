export enum MenuItemCategory {
  BEVERAGE = 'BEVERAGE',
  STARTER = 'STARTER',
  MAIN = 'MAIN',
  DESSERT = 'DESSERT',
}

export enum MenuItemCategoryCustom {
  ADDON = 'ADDON',
}

export enum MenuItemCategoryAll {
  STARTER = 'STARTER',
  MAIN = 'MAIN',
  DESSERT = 'DESSERT',
  BEVERAGE = 'BEVERAGE',
  ADDON = 'ADDON',
}

export type MenuItem = {
  _id: string
  fullName: string
  shortName: string
  price: number
  category: MenuItemCategory | MenuItemCategoryCustom
  image: string
  supplement: boolean;
}

export type Menu = {
  [MenuItemCategory.STARTER]: MenuItem[]
  [MenuItemCategory.MAIN]: MenuItem[]
  [MenuItemCategory.DESSERT]: MenuItem[]
  [MenuItemCategory.BEVERAGE]: MenuItem[]
  [MenuItemCategoryCustom.ADDON]: MenuItem[]
}
