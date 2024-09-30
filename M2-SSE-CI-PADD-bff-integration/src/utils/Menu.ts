import { CommandSlice } from '../store/slices/command'
import { MenuItemCategoryAll } from '../types/Menu'

export const getCurrentStep = (step: number): MenuItemCategoryAll => {
  return Object.keys(MenuItemCategoryAll)[step] as MenuItemCategoryAll
}

export const getCurrentStepTranslation = (
  step: number | string,
  t: (s: string) => string
) => {
  if (typeof step === 'string') {
    return t(`menu.${step}`)
  }
  return t(`menu.${getCurrentStep(step)}`)
}

export const getViewOfSelectedMeals = (
  selectedMeals: CommandSlice['selectedMeal'],
  spliceNumber: number
) => {
  const meals = []
  for (const category of Object.values(selectedMeals)) {
    for (const mealId of category) {
      if (meals.length < spliceNumber) {
        meals.push(mealId)
      } else {
        break
      }
    }
    if (meals.length >= spliceNumber) {
      break
    }
  }
  return meals
}
