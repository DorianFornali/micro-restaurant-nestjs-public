import { queryOptions } from '@tanstack/react-query'
import APP from '../config/api'

export const mealDetailQuery = (mealId: string) => {
  return queryOptions({
    queryKey: ['mealDetail'],
    queryFn: () => {
      return new Promise((resolve, reject) => {
        fetch(`${APP.API_GET_MENU}/${mealId}`)
          .then((res) => res.json())
          .then((data) => {
            resolve(data)
          })
          .catch((err) => reject(err))
      })
    },
  })
}
