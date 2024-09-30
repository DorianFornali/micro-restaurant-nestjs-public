import { FC, useMemo } from 'react'
import { Box, Typography } from '@mui/material'
import MealCard from '../components/MealCard'
import CommandCard from '../components/CommandCard'
import { useNavigate } from '@tanstack/react-router'
import { routes } from '../router/definitions'
import { useAppStore } from '../store/store'
import { findStepBasedOnMealId, mealSteps } from '../mocks/Meal'
import { getViewOfSelectedMeals } from '../utils/Menu'

const Panier: FC = () => {
  const selectedMeals = useAppStore((state) => state.selectedMeal)
  const navigate = useNavigate()

  const spliceSelectedMeal = useMemo(
    () => getViewOfSelectedMeals(selectedMeals, 4),
    [selectedMeals]
  )

  const deleteItem = (id: string) => {
    const step = findStepBasedOnMealId(id)
    useAppStore.getState().setSelectedMeal({
      ...selectedMeals,
      [step]: selectedMeals[step].filter((mealId) => mealId !== id),
    })
  }

  const flatSelectedMeals = Object.values(selectedMeals).flat()

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        height: '100vh',
      }}
    >
      <Typography variant="h4" component="h1" gutterBottom align="center">
        Votre Panier
      </Typography>

      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: 'repeat(2, 1fr)',
          gap: 2,
          maxHeight: 'calc(100% - 100px)',
          overflow: 'auto',
          paddingBottom: '20px',
        }}
      >
        {flatSelectedMeals.map((mealId: string) => {
          const meal = Object.values(mealSteps)
            .flat()
            .find((meal) => meal.id === mealId)
          if (!meal) return null

          return (
            <MealCard
              key={meal.id}
              id={meal.id}
              img={meal.img}
              name={meal.name}
              price={meal.price}
              onDelete={() => deleteItem(meal.id)}
            />
          )
        })}
      </Box>

      <Box sx={{ marginTop: 4 }}>
        <CommandCard
          items={spliceSelectedMeal
            .map((mealId: string) => {
              const meal = Object.values(mealSteps)
                .flat()
                .find((meal) => meal.id === mealId)

              if (!meal) return null

              return {
                name: meal.name,
                img: meal.img,
              }
            })
            .filter((meal) => meal !== null)}
          price={flatSelectedMeals
            .map((mealId: string) => {
              const meal = Object.values(mealSteps)
                .flat()
                .find((meal) => meal.id === mealId)
              return meal?.price || 0
            })
            .reduce((acc, price) => acc + price, 0)}
          primaryBtnProps={{
            label: 'Valider la commande',
            onClick: () => {
              navigate({
                to: routes.payment.path,
              })
            },
          }}
          secondaryBtnProps={{
            label: 'Retour',
            onClick: () => {
              navigate({
                to: routes.command.path,
              })
            },
          }}
        />
      </Box>
    </Box>
  )
}

export default Panier
