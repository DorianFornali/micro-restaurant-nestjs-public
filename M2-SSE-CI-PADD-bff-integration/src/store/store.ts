import { create } from 'zustand'

import { AppSlice, createAppSlice } from './slices/app'
import { CommandSlice, createCommandSlice } from './slices/command'

type StoreState = AppSlice & CommandSlice

export const useAppStore = create<StoreState>()((...a) => ({
  ...createAppSlice(...a),
  ...createCommandSlice(...a),
}))
