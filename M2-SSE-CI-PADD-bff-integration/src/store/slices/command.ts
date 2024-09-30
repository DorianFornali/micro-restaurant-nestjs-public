import { StateCreator } from 'zustand'
import { MenuItemCategoryAll } from '../../types/Menu'

export interface CommandSlice {
  step: number
  tableNumber: number
  peopleCount: number
  mealStep: number
  selectedMeal: {
    [key: string]: string[]
  }
  setStep: (step: number) => void
  setTableNumber: (tableNumber: number) => void
  setPeopleCount: (peopleCount: number) => void
  setMealStep: (mealStep: number) => void
  setSelectedMeal: (selectedMeal: { [key: string]: string[] }) => void
  reset: () => void
}

export const createCommandSlice: StateCreator<CommandSlice> = (set) => ({
  step: 0,
  tableNumber: 0,
  peopleCount: 0,
  mealStep: 0,
  selectedMeal: Object.keys(MenuItemCategoryAll).reduce(
    (acc, key) => {
      acc[key] = []
      return acc
    },
    {} as { [key: string]: string[] }
  ),
  setStep: (step) => set({ step }),
  setTableNumber: (tableNumber) => set({ tableNumber }),
  setPeopleCount: (peopleCount) => set({ peopleCount }),
  setMealStep: (mealStep) => set({ mealStep }),
  setSelectedMeal: (selectedMeal) => set({ selectedMeal }),
  reset: () =>
    set({
      step: 0,
      tableNumber: 0,
      peopleCount: 0,
      mealStep: 0,
      selectedMeal: Object.keys(MenuItemCategoryAll).reduce(
        (acc, key) => {
          acc[key] = []
          return acc
        },
        {} as { [key: string]: string[] }
      ),
    }),
})
